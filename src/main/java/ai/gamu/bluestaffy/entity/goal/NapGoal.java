package ai.gamu.bluestaffy.entity.goal;

import ai.gamu.bluestaffy.entity.BlueStaffyEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Triggers a nap when the Staffy is tamed, sitting, and idle.
 *
 * Lifecycle per nap session:
 *   - Random sighing sounds every 5–12 s
 *   - Occasionally (every 35–55 s) the dog flops to its side for 12–18 s,
 *     accompanied by a settling sigh as it goes down
 *
 * The visual animation (head droop, breathing, side-roll) is handled client-side
 * in BlueStaffyModel; this goal just sets the server-synced flags and plays sounds.
 */
public class NapGoal extends Goal {

    private final BlueStaffyEntity staffy;

    /** Total remaining ticks of this nap session. */
    private int napTicks = 0;

    /** Ticks until the next ambient sigh. */
    private int sighCooldown = 0;

    /** Ticks until the dog considers flopping to its side (0 = ready to flop). */
    private int flopCooldown = 0;

    /** Remaining ticks of the current side-flop (0 = not flopping). */
    private int flopDuration = 0;

    public NapGoal(BlueStaffyEntity staffy) {
        this.staffy = staffy;
        this.setFlags(EnumSet.of(Flag.LOOK)); // hold the head in place while dozing
    }

    @Override
    public boolean canUse() {
        return staffy.isTame()
                && staffy.isInSittingPose()
                && !staffy.isZooming()
                && !staffy.isInWater()
                && staffy.onGround()
                && staffy.getRandom().nextInt(200) == 0; // ~10 s average before triggering
    }

    @Override
    public boolean canContinueToUse() {
        return staffy.isInSittingPose()
                && !staffy.isZooming()
                && napTicks > 0;
    }

    @Override
    public void start() {
        napTicks     = 300 + staffy.getRandom().nextInt(600); // 15–45 s
        sighCooldown = 60  + staffy.getRandom().nextInt(60);
        // First flop won't happen for at least 35 s into the nap
        flopCooldown = 700 + staffy.getRandom().nextInt(400);
        flopDuration = 0;
        staffy.setNapping(true);
        staffy.setSideFlopping(false);
    }

    @Override
    public void tick() {
        napTicks--;

        // ── Ambient sighs ───────────────────────────────────────────────────────
        if (sighCooldown-- <= 0) {
            // Pitch is fully random across a wide range so you get high then low then
            // medium sighs in no predictable order — like a real sleeping dog
            float pitch = 0.4F + staffy.getRandom().nextFloat() * 0.85F; // 0.4–1.25
            staffy.playSound(SoundEvents.FOX_SLEEP, 0.25F, pitch);
            sighCooldown = 100 + staffy.getRandom().nextInt(150); // 5–12 s
        }

        // ── Side-flop lifecycle ─────────────────────────────────────────────────
        if (flopDuration > 0) {
            // Currently flopped — count down
            flopDuration--;
            if (flopDuration == 0) {
                staffy.setSideFlopping(false);
                // Long gap before the next flop (35–55 s)
                flopCooldown = 700 + staffy.getRandom().nextInt(400);
            }
        } else if (flopCooldown > 0) {
            flopCooldown--;
        } else {
            // Cooldown expired — flop and play a settling sigh
            staffy.setSideFlopping(true);
            flopDuration = 240 + staffy.getRandom().nextInt(120); // 12–18 s
            float flopPitch = 0.4F + staffy.getRandom().nextFloat() * 0.85F; // same wide range
            staffy.playSound(SoundEvents.FOX_SLEEP, 0.4F, flopPitch); // slightly louder than ambient
        }
    }

    @Override
    public void stop() {
        napTicks     = 0;
        flopDuration = 0;
        staffy.setNapping(false);
        staffy.setSideFlopping(false);
    }
}
