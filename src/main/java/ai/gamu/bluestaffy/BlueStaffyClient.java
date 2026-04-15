package ai.gamu.bluestaffy;

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

// This class will not load on dedicated servers. Accessing client side code from here is safe.
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    @SubscribeEvent
    static void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Cast needed because BlueStaffyRenderer is typed to Wolf (its parent),
        // but our entity IS a Wolf so this is safe at runtime.
        event.registerEntityRenderer(
                (net.minecraft.world.entity.EntityType<Wolf>) (net.minecraft.world.entity.EntityType<?>) BlueStaffy.BLUE_STAFFY.get(),
                BlueStaffyRenderer::new
        );
    }
}
