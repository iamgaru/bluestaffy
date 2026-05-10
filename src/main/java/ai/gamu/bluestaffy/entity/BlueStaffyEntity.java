package ai.gamu.bluestaffy.entity;

import ai.gamu.bluestaffy.BlueStaffy;
import ai.gamu.bluestaffy.entity.goal.ZoomiesGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class BlueStaffyEntity extends Wolf {

    private static final EntityDataAccessor<Boolean> DATA_NAPPING =
            SynchedEntityData.defineId(BlueStaffyEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SIDE_FLOPPING =
            SynchedEntityData.defineId(BlueStaffyEntity.class, EntityDataSerializers.BOOLEAN);

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(BlueStaffy.MODID, "textures/entity/bluestaffy.png");

    // ── Zoomies ────────────────────────────────────────────────────────────────
    private int zoomTicks    = 0;
    private boolean wasInWater   = false;
    private int ramCooldown  = 0;
    private boolean steakExcited = false;
    private int barkCooldown = 0;

    // ── Napping (managed in aiStep, no Goal system) ────────────────────────────
    private int napTicks     = 0;   // remaining ticks of current nap; 0 = awake
    private int napCooldown  = 0;   // ticks before the dog may nap again
    private int sighCooldown = 0;   // ticks until next ambient sigh
    private int flopCooldown = 0;   // ticks until the dog may flop to its side
    private int flopDuration = 0;   // remaining ticks of current side-flop; 0 = upright

    public BlueStaffyEntity(EntityType<? extends Wolf> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_NAPPING, false);
        builder.define(DATA_SIDE_FLOPPING, false);
    }

    public boolean isNapping()         { return this.entityData.get(DATA_NAPPING); }
    public void    setNapping(boolean v) { this.entityData.set(DATA_NAPPING, v); }

    public boolean isSideFlopping()         { return this.entityData.get(DATA_SIDE_FLOPPING); }
    public void    setSideFlopping(boolean v) { this.entityData.set(DATA_SIDE_FLOPPING, v); }

    // ── Appearance ─────────────────────────────────────────────────────────────

    @Override
    public Identifier getTexture() { return TEXTURE; }

    // ── Sounds ─────────────────────────────────────────────────────────────────

    @Override
    protected SoundEvent getAmbientSound() { return SoundEvents.PIG_AMBIENT; }

    public void playZoomiesStartSound() {
        SoundEvent bark = super.getAmbientSound();
        this.playSound(bark, 1.2F, 0.85F + this.random.nextFloat() * 0.3F);
    }

    // ── Goals ──────────────────────────────────────────────────────────────────

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new ZoomiesGoal(this));
    }

    // ── Zoomies API ────────────────────────────────────────────────────────────

    public boolean isZooming() { return zoomTicks > 0; }

    public void startZoomies(int durationTicks) {
        if (!isZooming()) zoomTicks = durationTicks;
    }

    public void tickZoomies() {
        if (zoomTicks > 0) {
            zoomTicks--;
            if (zoomTicks == 0) steakExcited = false;
        }
    }

    // ── Interaction: food and unsitting ────────────────────────────────────────

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack item = player.getItemInHand(hand);

        boolean isSteakTreat = item.is(Items.COOKED_BEEF)   && this.isTame() && this.getHealth() < this.getMaxHealth();
        boolean willFeed     = item.is(ItemTags.WOLF_FOOD)  && this.isTame() && this.getHealth() < this.getMaxHealth();
        boolean wasSitting   = this.isOrderedToSit();

        InteractionResult result = super.mobInteract(player, hand);

        if (!this.level().isClientSide() && result.consumesAction()) {
            if (isSteakTreat) {
                startZoomies(280 + this.random.nextInt(80));
                steakExcited = true;
                barkCooldown = 0;
                playZoomiesStartSound();
                playZoomiesStartSound();
                playZoomiesStartSound();
            } else if (willFeed) {
                startZoomies(120 + this.random.nextInt(60));
            } else if (wasSitting && !this.isOrderedToSit()) {
                startZoomies(60 + this.random.nextInt(40));
            }
        }
        return result;
    }

    // ── Per-tick ───────────────────────────────────────────────────────────────

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide()) return;

        // ── Zoomies trigger 3: post-water sprint ───────────────────────────────
        boolean inWater = this.isInWater();
        if (wasInWater && !inWater && this.onGround()) {
            startZoomies(60 + this.random.nextInt(40));
        }
        wasInWater = inWater;

        // ── Steak zoomies: periodic barking ───────────────────────────────────
        if (isZooming() && steakExcited) {
            if (barkCooldown > 0) { barkCooldown--; }
            else {
                playZoomiesStartSound();
                barkCooldown = 30 + this.random.nextInt(30);
            }
        }

        // ── Collision damage while zooming ────────────────────────────────────
        if (isZooming()) {
            if (ramCooldown > 0) { ramCooldown--; }
            else {
                AABB hitBox = this.getBoundingBox().inflate(0.25);
                List<LivingEntity> targets = this.level().getEntitiesOfClass(
                        LivingEntity.class, hitBox,
                        e -> e != this && e != this.getOwner());
                if (!targets.isEmpty()) {
                    for (LivingEntity t : targets) t.hurt(this.damageSources().mobAttack(this), 1.5F);
                    ramCooldown = 20;
                }
            }
        } else {
            ramCooldown = 0;
        }

        // ── Napping ───────────────────────────────────────────────────────────
        boolean canNap = this.isInSittingPose()
                && !this.isZooming()
                && !this.isInWater()
                && this.onGround();

        // DEBUG — remove once napping is confirmed working
        if (this.tickCount % 100 == 0) {
            BlueStaffy.LOGGER.info("[Staffy nap] tame={} sitting={} zooming={} inWater={} onGround={} canNap={} napCooldown={} isNapping={}",
                    this.isTame(), this.isInSittingPose(), this.isZooming(),
                    this.isInWater(), this.onGround(), canNap, napCooldown, isNapping());
        }

        if (isNapping()) {
            if (!canNap) {
                // Conditions broken (stood up, fell in water, etc.) — wake immediately
                stopNap();
            } else {
                napTicks--;
                if (napTicks <= 0) {
                    stopNap();
                    napCooldown = 200 + this.random.nextInt(200); // ~15–25 s before next nap
                } else {
                    tickNapSounds();
                }
            }
        } else if (canNap) {
            if (napCooldown > 0) {
                napCooldown--;
            } else if (this.random.nextInt(100) == 0) { // ~5 s average at 20 tps
                startNap();
            }
        }
    }

    // ── Nap helpers ────────────────────────────────────────────────────────────

    private void startNap() {
        napTicks     = 300 + this.random.nextInt(600); // 15–45 s
        sighCooldown = 60  + this.random.nextInt(60);
        flopCooldown = 700 + this.random.nextInt(400); // first flop no sooner than 35 s in
        flopDuration = 0;
        setNapping(true);
        setSideFlopping(false);
    }

    private void stopNap() {
        napTicks     = 0;
        flopDuration = 0;
        setNapping(false);
        setSideFlopping(false);
    }

    private void tickNapSounds() {
        // Ambient sighs — random pitch so you get high, low, and mid unpredictably
        if (sighCooldown-- <= 0) {
            float pitch = 0.4F + this.random.nextFloat() * 0.85F; // 0.4–1.25
            this.playSound(SoundEvents.FOX_SLEEP, 0.25F, pitch);
            sighCooldown = 100 + this.random.nextInt(150); // 5–12 s
        }

        // Side-flop lifecycle
        if (flopDuration > 0) {
            flopDuration--;
            if (flopDuration == 0) {
                setSideFlopping(false);
                flopCooldown = 700 + this.random.nextInt(400); // 35–55 s gap
            }
        } else if (flopCooldown > 0) {
            flopCooldown--;
        } else {
            // Time to flop — play a deeper settling sigh
            setSideFlopping(true);
            flopDuration = 240 + this.random.nextInt(120); // 12–18 s
            float flopPitch = 0.4F + this.random.nextFloat() * 0.85F;
            this.playSound(SoundEvents.FOX_SLEEP, 0.4F, flopPitch);
        }
    }
}
