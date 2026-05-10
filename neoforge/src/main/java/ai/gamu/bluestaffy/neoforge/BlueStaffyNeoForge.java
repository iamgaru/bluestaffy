package ai.gamu.bluestaffy.neoforge;

import ai.gamu.bluestaffy.BlueStaffy;
import ai.gamu.bluestaffy.entity.BlueStaffyEntity;
import ai.gamu.bluestaffy.registry.ModEntities;
import ai.gamu.bluestaffy.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.animal.wolf.Wolf;

@Mod(BlueStaffy.MOD_ID)
public final class BlueStaffyNeoForge {

    // NeoForge-specific creative tab registration (custom tab, NeoForge DeferredRegister)
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BlueStaffy.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BLUE_STAFFY_TAB =
            CREATIVE_MODE_TABS.register("blue_staffy_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.bluestaffy"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> ModItems.BLUE_STAFFY_SPAWN_EGG.get().getDefaultInstance())
                    .displayItems((parameters, output) -> output.accept(ModItems.BLUE_STAFFY_SPAWN_EGG.get()))
                    .build());

    public BlueStaffyNeoForge(IEventBus modBus, ModContainer modContainer) {
        BlueStaffy.init();
        CREATIVE_MODE_TABS.register(modBus);
        modBus.addListener(this::onAttributes);
    }

    private void onAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.BLUE_STAFFY.get(), Wolf.createAttributes().build());
    }
}
