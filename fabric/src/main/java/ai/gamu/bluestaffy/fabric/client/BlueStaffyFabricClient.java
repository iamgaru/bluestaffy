package ai.gamu.bluestaffy.fabric.client;

import ai.gamu.bluestaffy.client.model.BlueStaffyModel;
import ai.gamu.bluestaffy.client.renderer.BlueStaffyRenderer;
import ai.gamu.bluestaffy.registry.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.animal.wolf.Wolf;

public final class BlueStaffyFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(
                BlueStaffyModel.LAYER_LOCATION,
                BlueStaffyModel::createBodyLayer
        );
        EntityModelLayerRegistry.registerModelLayer(
                BlueStaffyModel.BABY_LAYER_LOCATION,
                () -> LayerDefinition.create(
                        BlueStaffyModel.BABY_TRANSFORMER.apply(BlueStaffyModel.createMeshDefinition()),
                        64, 64)
        );
        @SuppressWarnings({"unchecked", "rawtypes"})
        // BlueStaffyRenderer is typed to Wolf (the parent class).
        // BlueStaffyEntity IS a Wolf, so this cast is safe at runtime.
        net.minecraft.world.entity.EntityType<Wolf> entityType =
                (net.minecraft.world.entity.EntityType<Wolf>) (net.minecraft.world.entity.EntityType<?>)
                        ModEntities.BLUE_STAFFY.get();
        EntityRendererRegistry.register(entityType, BlueStaffyRenderer::new);
    }
}
