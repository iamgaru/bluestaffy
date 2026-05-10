package ai.gamu.bluestaffy.registry;

import ai.gamu.bluestaffy.BlueStaffy;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(BlueStaffy.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<SpawnEggItem> BLUE_STAFFY_SPAWN_EGG =
            ITEMS.register("blue_staffy_spawn_egg", () ->
                    new SpawnEggItem(new Item.Properties().spawnEgg(ModEntities.BLUE_STAFFY.get())));

    public static final RegistrySupplier<BlockItem> EXAMPLE_BLOCK_ITEM =
            ITEMS.register("example_block", () ->
                    new BlockItem(ModBlocks.EXAMPLE_BLOCK.get(), new Item.Properties()));

    public static final RegistrySupplier<Item> EXAMPLE_ITEM =
            ITEMS.register("example_item", () ->
                    new Item(new Item.Properties().food(new FoodProperties.Builder()
                            .alwaysEdible().nutrition(1).saturationModifier(2f).build())));
}
