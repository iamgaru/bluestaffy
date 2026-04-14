package ai.gamu.bluestaffy;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * Basic wolf-derived entity so we have a concrete type for the Blue Staffy spawn egg.
 */
public class BlueStaffyEntity extends Wolf {
    public BlueStaffyEntity(EntityType<? extends Wolf> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Wolf.createAttributes()
                .add(Attributes.MAX_HEALTH, 32.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D);
    }

    public static boolean checkBlueStaffySpawnRules(EntityType<? extends Wolf> type, ServerLevelAccessor level, EntitySpawnReason reason,
            BlockPos pos, RandomSource random) {
        @SuppressWarnings("unchecked")
        EntityType<Wolf> wolfType = (EntityType<Wolf>) (EntityType<?>) type;
        return Wolf.checkWolfSpawnRules(wolfType, level, reason, pos, random);
    }
}
