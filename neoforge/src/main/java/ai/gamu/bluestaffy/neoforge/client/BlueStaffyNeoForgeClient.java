package ai.gamu.bluestaffy.neoforge.client;

import ai.gamu.bluestaffy.BlueStaffy;
import ai.gamu.bluestaffy.client.model.BlueStaffyModel;
import ai.gamu.bluestaffy.client.renderer.BlueStaffyRenderer;
import ai.gamu.bluestaffy.registry.ModEntities;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Mod(value = BlueStaffy.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = BlueStaffy.MOD_ID, value = Dist.CLIENT)
public final class BlueStaffyNeoForgeClient {

    @SubscribeEvent
    static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BlueStaffyModel.LAYER_LOCATION,
                BlueStaffyModel::createBodyLayer);
        event.registerLayerDefinition(BlueStaffyModel.BABY_LAYER_LOCATION,
                () -> LayerDefinition.create(
                        BlueStaffyModel.BABY_TRANSFORMER.apply(BlueStaffyModel.createMeshDefinition()),
                        64, 64));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @SubscribeEvent
    static void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // BlueStaffyRenderer is typed to Wolf (the parent class).
        // BlueStaffyEntity IS a Wolf, so this cast is safe at runtime.
        event.registerEntityRenderer(
                (net.minecraft.world.entity.EntityType<Wolf>) (net.minecraft.world.entity.EntityType<?>)
                        ModEntities.BLUE_STAFFY.get(),
                BlueStaffyRenderer::new);
    }
}
