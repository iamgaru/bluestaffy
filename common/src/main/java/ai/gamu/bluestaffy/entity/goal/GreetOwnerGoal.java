package ai.gamu.bluestaffy.entity.goal;

import ai.gamu.bluestaffy.entity.BlueStaffyEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Reserved for future use — greeting mini-zoomie when owner comes close.
 * Not currently registered in BlueStaffyEntity.registerGoals().
 */
public class GreetOwnerGoal extends Goal {

    private final BlueStaffyEntity staffy;

    public GreetOwnerGoal(BlueStaffyEntity staffy) {
        this.staffy = staffy;
        this.setFlags(EnumSet.noneOf(Flag.class));
    }

    @Override public boolean canUse()           { return false; }
    @Override public boolean canContinueToUse() { return false; }
    @Override public void start()               { }
}
