package ai.gamu.bluestaffy;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

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

    public static boolean checkBlueStaffySpawnRules(EntityType<BlueStaffyEntity> type, LevelAccessor level, MobSpawnType reason,
            BlockPos pos, RandomSource random) {
        return Wolf.checkWolfSpawnRules(type, level, reason, pos, random);
    }
}
