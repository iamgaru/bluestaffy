package ai.gamu.bluestaffy.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;

/**
 * Client renderer that currently reuses the vanilla wolf texture so the entity renders immediately.
 */
public class BlueStaffyRenderer extends WolfRenderer {
    private static final ResourceLocation BLUE_STAFFY_TEXTURE = ResourceLocation.fromNamespaceAndPath("bluestaffy", "textures/entity/blue_staffy.png");

    public BlueStaffyRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Wolf entity) {
        return BLUE_STAFFY_TEXTURE;
    }
}
