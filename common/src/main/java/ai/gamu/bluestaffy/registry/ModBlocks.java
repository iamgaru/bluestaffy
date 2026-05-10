package ai.gamu.bluestaffy.registry;

import ai.gamu.bluestaffy.BlueStaffy;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(BlueStaffy.MOD_ID, Registries.BLOCK);

    public static final RegistrySupplier<Block> EXAMPLE_BLOCK =
            BLOCKS.register("example_block", () ->
                    new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
}
