package ai.gamu.bluestaffy.entity;

import ai.gamu.bluestaffy.BlueStaffy;
import ai.gamu.bluestaffy.entity.goal.NapGoal;
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

    /** Remaining ticks of zoomies; 0 = not zooming. */
    private int zoomTicks = 0;

    /** Tracks water entry so zoomies fire on exit. */
    private boolean wasInWater = false;

    /** Cooldown before the dog can deal ram damage again (prevents per-tick spam). */
    private int ramCooldown = 0;

    /** True while doing a steak-triggered zoomie — enables periodic excited barking. */
    private boolean steakExcited = false;

    /** Countdown between excited barks during a steak zoomie. */
    private int barkCooldown = 0;

    public BlueStaffyEntity(EntityType<? extends Wolf> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_NAPPING, false);
        builder.define(DATA_SIDE_FLOPPING, false);
    }

    public boolean isNapping()      { return this.entityData.get(DATA_NAPPING); }
    public void setNapping(boolean v) { this.entityData.set(DATA_NAPPING, v); }

    public boolean isSideFlopping()      { return this.entityData.get(DATA_SIDE_FLOPPING); }
    public void setSideFlopping(boolean v) { this.entityData.set(DATA_SIDE_FLOPPING, v); }

    // ── Appearance ─────────────────────────────────────────────────────────────

    @Override
    public Identifier getTexture() {
        return TEXTURE;
    }

    // ── Sounds ─────────────────────────────────────────────────────────────────

    /** Idle oinks — Staffies are notorious snorters. */
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PIG_AMBIENT;
    }

    /**
     * Bark played when zoomies begin.
     * Calls Wolf's ambient sound directly (bypasses our pig override) so you get
     * a proper wolf pant/growl rather than an oink on the sprint start.
     */
    public void playZoomiesStartSound() {
        SoundEvent bark = super.getAmbientSound();
        this.playSound(bark, 1.2F, 0.85F + this.random.nextFloat() * 0.3F);
    }

    // ── Goals ──────────────────────────────────────────────────────────────────

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // Priority 0: zoomies override everything (sit, follow, attack, wander)
        this.goalSelector.addGoal(0, new ZoomiesGoal(this));
        // Priority 9: nap when tamed, sitting, and idle — very low priority
        this.goalSelector.addGoal(9, new NapGoal(this));
    }

    // ── Zoomies API ────────────────────────────────────────────────────────────

    public boolean isZooming() { return zoomTicks > 0; }

    /** Start a zoomie session. No-ops if already zooming. */
    public void startZoomies(int durationTicks) {
        if (!isZooming()) zoomTicks = durationTicks;
    }

    /** Decrements the timer each tick; called from ZoomiesGoal. */
    public void tickZoomies() {
        if (zoomTicks > 0) {
            zoomTicks--;
            if (zoomTicks == 0) steakExcited = false;
        }
    }

    // ── Trigger 1: food, and Trigger 2: unsitting ──────────────────────────────

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack item = player.getItemInHand(hand);

        // Snapshot state before super() can mutate it
        boolean isSteakTreat = item.is(Items.COOKED_BEEF) && this.isTame() && this.getHealth() < this.getMaxHealth();
        boolean willFeed     = item.is(ItemTags.WOLF_FOOD)  && this.isTame() && this.getHealth() < this.getMaxHealth();
        boolean wasSitting   = this.isOrderedToSit();

        InteractionResult result = super.mobInteract(player, hand);

        if (!this.level().isClientSide() && result.consumesAction()) {
            if (isSteakTreat) {
                // Cooked steak — extra long zoomies (14–18 s) with excited bark burst
                startZoomies(280 + this.random.nextInt(80));
                steakExcited = true;
                barkCooldown = 0;
                // Three quick barks at the moment of feeding
                playZoomiesStartSound();
                playZoomiesStartSound();
                playZoomiesStartSound();
            } else if (willFeed) {
                // Other wolf food — standard zoomies burst (6–9 s)
                startZoomies(120 + this.random.nextInt(60));
            } else if (wasSitting && !this.isOrderedToSit()) {
                // Player unsit the dog — excited sprint (3–5 s)
                startZoomies(60 + this.random.nextInt(40));
            }
        }
        return result;
    }

    // ── Per-tick: whimper + collision damage ───────────────────────────────────

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide()) return;

        // Trigger 3: post-water zoomies — classic Staffy post-bath sprint
        boolean inWater = this.isInWater();
        if (wasInWater && !inWater && this.onGround()) {
            startZoomies(60 + this.random.nextInt(40)); // 3–5 s
        }
        wasInWater = inWater;

        // Resting whimper — quiet fox-sleep sound while sitting (~25 s average gap)
        if (this.isInSittingPose() && !this.isSilent() && this.random.nextInt(500) == 0) {
            this.playSound(SoundEvents.FOX_SLEEP, 0.4F, 1.0F + this.random.nextFloat() * 0.2F);
        }

        // Periodic barking during steak-triggered zoomies
        if (isZooming() && steakExcited) {
            if (barkCooldown > 0) {
                barkCooldown--;
            } else {
                playZoomiesStartSound();
                // bark every 1.5–3 s (30–60 ticks)
                barkCooldown = 30 + this.random.nextInt(30);
            }
        }

        // Collision (ram) damage while zooming
        if (isZooming()) {
            if (ramCooldown > 0) {
                ramCooldown--;
            } else {
                AABB hitBox = this.getBoundingBox().inflate(0.25);
                List<LivingEntity> targets = this.level().getEntitiesOfClass(
                        LivingEntity.class, hitBox,
                        e -> e != this && e != this.getOwner());
                if (!targets.isEmpty()) {
                    for (LivingEntity target : targets) {
                        target.hurt(this.damageSources().mobAttack(this), 1.5F);
                    }
                    ramCooldown = 20; // 1-second cooldown before next ram
                }
            }
        } else {
            ramCooldown = 0;
        }
    }
}
