package ai.gamu.bluestaffy;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Simple spawn egg implementation that wraps the vanilla methods now driven by data components.
 */
public class BlueStaffySpawnEggItem extends Item {
    private final Supplier<? extends EntityType<? extends Mob>> entityType;

    public BlueStaffySpawnEggItem(Supplier<? extends EntityType<? extends Mob>> entityType, int primaryColor, int secondaryColor,
            Item.Properties properties) {
        super(properties);
        this.entityType = entityType;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos spawnPos = getSpawnPosition(context);
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        EntityType<? extends Mob> type = entityType.get();
        var entity = type.spawn(serverLevel, stack, player, spawnPos, EntitySpawnReason.SPAWN_EGG, true, false);
        if (entity != null) {
            stack.shrink(1);
        }
        return entity != null ? InteractionResult.CONSUME : InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);
        }

        InteractionResult result = useOn(new UseOnContext(player, hand, hitResult));
        return new InteractionResultHolder<>(result, stack);
    }

    private static BlockPos getSpawnPosition(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();
        Level level = context.getLevel();
        return level.getBlockState(pos).getCollisionShape(level, pos).isEmpty() ? pos : pos.relative(face);
    }
}
