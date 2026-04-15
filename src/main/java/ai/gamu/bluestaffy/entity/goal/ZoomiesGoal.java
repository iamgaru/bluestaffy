package ai.gamu.bluestaffy.entity.goal;

import ai.gamu.bluestaffy.entity.BlueStaffyEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Handles zoomies: agile, erratic sprinting triggered by eating or unsitting.
 *
 * Speed is 2.5× base (a noticeable excited sprint without being cartoonish).
 * Direction changes every 5–7 ticks with tight 8-block arcs to feel agile.
 * Priority 0 overrides sit, follow, wander, and attack.
 */
public class ZoomiesGoal extends Goal {

    private static final Identifier SPEED_ID =
            Identifier.fromNamespaceAndPath("bluestaffy", "zoomies_speed");
    // ADD_MULTIPLIED_BASE: final = base × (1 + 1.5) = 2.5× base speed
    private static final AttributeModifier SPEED_BOOST =
            new AttributeModifier(SPEED_ID, 1.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

    private final BlueStaffyEntity staffy;
    private int nextTurnIn = 0;

    public ZoomiesGoal(BlueStaffyEntity staffy) {
        this.staffy = staffy;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override public boolean canUse()           { return staffy.isZooming(); }
    @Override public boolean canContinueToUse() { return staffy.isZooming(); }

    @Override
    public void start() {
        var attr = staffy.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null) attr.addTransientModifier(SPEED_BOOST);
        staffy.playZoomiesStartSound();
        nextTurnIn = 0;
    }

    @Override
    public void tick() {
        staffy.tickZoomies();
        if (--nextTurnIn <= 0) {
            // Tight arcs: 8-block radius, change direction every 5–7 ticks
            double dx = (staffy.getRandom().nextDouble() - 0.5) * 8.0;
            double dz = (staffy.getRandom().nextDouble() - 0.5) * 8.0;
            staffy.getNavigation().moveTo(
                    staffy.getX() + dx,
                    staffy.getY(),
                    staffy.getZ() + dz,
                    2.5);
            nextTurnIn = 5 + staffy.getRandom().nextInt(3);
        }
    }

    @Override
    public void stop() {
        var attr = staffy.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null) attr.removeModifier(SPEED_ID);
        staffy.getNavigation().stop();
    }
}
