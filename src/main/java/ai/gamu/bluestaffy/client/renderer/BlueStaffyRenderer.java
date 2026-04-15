package ai.gamu.bluestaffy.client.renderer;

import ai.gamu.bluestaffy.BlueStaffy;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.resources.Identifier;

public class BlueStaffyRenderer extends WolfRenderer {

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(BlueStaffy.MODID, "textures/entity/bluestaffy.png");

    public BlueStaffyRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Identifier getTextureLocation(WolfRenderState state) {
        return TEXTURE;
    }
}
