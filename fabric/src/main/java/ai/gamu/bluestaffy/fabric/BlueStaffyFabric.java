package ai.gamu.bluestaffy.fabric;

import ai.gamu.bluestaffy.BlueStaffy;
import ai.gamu.bluestaffy.entity.BlueStaffyEntity;
import ai.gamu.bluestaffy.registry.ModEntities;
import ai.gamu.bluestaffy.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.CreativeModeTabs;

public final class BlueStaffyFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        BlueStaffy.init();

        FabricDefaultAttributeRegistry.register(
                ModEntities.BLUE_STAFFY.get(),
                Wolf.createAttributes().build()
        );

        // NOTE: Fabric build adds spawn egg to the vanilla SPAWN_EGGS tab rather than a
        // mod-specific tab. NeoForge creates a custom "Blue Staffy" tab. Both are functionally
        // equivalent for finding the spawn egg; a Fabric custom-tab implementation can be added
        // as a follow-up using Architectury's CreativeTabRegistry.
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries ->
                entries.accept(ModItems.BLUE_STAFFY_SPAWN_EGG.get())
        );
    }
}
