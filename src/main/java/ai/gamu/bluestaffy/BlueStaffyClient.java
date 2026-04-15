package ai.gamu.bluestaffy;

import ai.gamu.bluestaffy.client.model.BlueStaffyModel;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import ai.gamu.bluestaffy.client.renderer.BlueStaffyRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = BlueStaffy.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = BlueStaffy.MODID, value = Dist.CLIENT)
public class BlueStaffyClient {

    public BlueStaffyClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        BlueStaffy.LOGGER.info("Blue Staffy client setup");
    }

    /** Register the adult and baby model layer definitions. */
    @SubscribeEvent
    static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BlueStaffyModel.LAYER_LOCATION,
                BlueStaffyModel::createBodyLayer);
        event.registerLayerDefinition(BlueStaffyModel.BABY_LAYER_LOCATION,
                () -> LayerDefinition.create(
                        BlueStaffyModel.BABY_TRANSFORMER.apply(BlueStaffyModel.createMeshDefinition()),
                        64, 64));
    }

    /** Register the entity renderer. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @SubscribeEvent
    static void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // BlueStaffyRenderer is typed to Wolf (the parent class).
        // BlueStaffyEntity IS a Wolf, so this cast is safe at runtime.
        event.registerEntityRenderer(
                (net.minecraft.world.entity.EntityType<Wolf>) (net.minecraft.world.entity.EntityType<?>)
                        BlueStaffy.BLUE_STAFFY.get(),
                BlueStaffyRenderer::new);
    }
}
