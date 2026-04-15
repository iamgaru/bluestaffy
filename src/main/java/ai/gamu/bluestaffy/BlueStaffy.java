package ai.gamu.bluestaffy;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import ai.gamu.bluestaffy.entity.BlueStaffyEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(BlueStaffy.MODID)
public class BlueStaffy {
    public static final String MODID = "bluestaffy";
    public static final Logger LOGGER = LogUtils.getLogger();

    // Deferred Registers
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);

    // --- Entity Types ---
    public static final DeferredHolder<EntityType<?>, EntityType<BlueStaffyEntity>> BLUE_STAFFY =
            ENTITY_TYPES.register("blue_staffy", id ->
                    EntityType.Builder.<BlueStaffyEntity>of(
                                    (type, level) -> new BlueStaffyEntity(type, level),
                                    MobCategory.CREATURE)
                            .sized(0.6F, 0.85F)
                            .clientTrackingRange(10)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, id)));

    // --- Spawn Egg ---
    // Use registerItem so Item.Properties gets its ResourceKey set (required in 1.21.11)
    // .spawnEgg() stores the entity type via DataComponents.ENTITY_DATA
    public static final DeferredItem<SpawnEggItem> BLUE_STAFFY_SPAWN_EGG =
            ITEMS.registerItem("blue_staffy_spawn_egg",
                    props -> new SpawnEggItem(props.spawnEgg(BLUE_STAFFY.get())));

    // --- Example content (kept from template) ---
    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", p -> p.mapColor(MapColor.STONE));
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);
    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", p -> p.food(new FoodProperties.Builder()
            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    // --- Creative Tab ---
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BLUE_STAFFY_TAB = CREATIVE_MODE_TABS.register("blue_staffy_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.bluestaffy"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> BLUE_STAFFY_SPAWN_EGG.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(BLUE_STAFFY_SPAWN_EGG.get());
            }).build());

    public BlueStaffy(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);

        modEventBus.addListener(this::onEntityAttributeCreation);

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Blue Staffy mod loading...");
    }

    private void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(BLUE_STAFFY.get(), Wolf.createAttributes().build());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Blue Staffy server starting");
    }
}
