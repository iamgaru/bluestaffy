package ai.gamu.bluestaffy.client.renderer;

import ai.gamu.bluestaffy.BlueStaffy;
import ai.gamu.bluestaffy.client.model.BlueStaffyModel;
import ai.gamu.bluestaffy.entity.BlueStaffyEntity;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.wolf.Wolf;

public class BlueStaffyRenderer extends AgeableMobRenderer<Wolf, BlueStaffyRenderState, BlueStaffyModel> {

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(BlueStaffy.MODID, "textures/entity/bluestaffy.png");

    public BlueStaffyRenderer(EntityRendererProvider.Context context) {
        super(context,
                new BlueStaffyModel(context.bakeLayer(BlueStaffyModel.LAYER_LOCATION)),
                new BlueStaffyModel(context.bakeLayer(BlueStaffyModel.BABY_LAYER_LOCATION)),
                0.5F);
    }

    @Override
    public Identifier getTextureLocation(BlueStaffyRenderState state) {
        return TEXTURE;
    }

    @Override
    public BlueStaffyRenderState createRenderState() {
        return new BlueStaffyRenderState();
    }

    @Override
    public void extractRenderState(Wolf entity, BlueStaffyRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isAngry       = entity.isAngry();
        state.isSitting     = entity.isInSittingPose();
        state.tailAngle     = entity.getTailAngle();
        state.headRollAngle = entity.getHeadRollAngle(partialTick);
        state.shakeAnim     = entity.getShakeAnim(partialTick);
        state.texture       = entity.getTexture();
        state.wetShade      = entity.getWetShade(partialTick);
        state.collarColor   = entity.isTame() ? entity.getCollarColor() : null;
        state.bodyArmorItem = entity.getBodyArmorItem().copy();
        if (entity instanceof BlueStaffyEntity staffy) {
            state.isNapping      = staffy.isNapping();
            state.isSideFlopping = staffy.isSideFlopping();
        }
    }
}
