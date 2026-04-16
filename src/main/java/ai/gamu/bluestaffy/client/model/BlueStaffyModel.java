package ai.gamu.bluestaffy.client.model;

import java.util.Set;

import ai.gamu.bluestaffy.BlueStaffy;
import net.minecraft.client.model.BabyModelTransform;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
/**
 * Custom entity model for the Blue Staffy dog.
 *
 * Proportions compared to the vanilla wolf:
 *   - Wider, rounder skull with a shorter, broader muzzle
 *   - Rose ears (small, folded flat to the sides — not upright)
 *   - Much wider barrel chest
 *   - Shorter, thicker legs with a wider stance
 *   - Short, thick tail carried low
 *
 * UV layout (64×64 texture):
 *   ( 0,  0) Head skull    8×7×6   → 28×13
 *   ( 0, 13) Muzzle        6×4×3   → 18×7
 *   ( 0, 20) Ears (×2)    3×3×1   → 8×4 each
 *   (28,  0) Chest        10×7×8   → 36×15
 *   (28, 15) Body          8×9×8   → 32×17
 *   ( 0, 23) Front legs    3×6×3   → 12×9
 *   (12, 23) Hind legs     3×6×3   → 12×9
 *   ( 0, 32) Tail          3×5×3   → 12×8
 */
public class BlueStaffyModel extends EntityModel<WolfRenderState> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(BlueStaffy.MODID, "blue_staffy"), "main");
    public static final ModelLayerLocation BABY_LAYER_LOCATION = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(BlueStaffy.MODID, "blue_staffy"), "baby");

    // Enlarges the head for the puppy/baby variant
    public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(Set.of("head"));

    private final ModelPart head;
    private final ModelPart realHead;
    private final ModelPart body;
    private final ModelPart upperBody;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart tail;
    private final ModelPart realTail;

    public BlueStaffyModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.realHead = this.head.getChild("real_head");
        this.body = root.getChild("body");
        this.upperBody = root.getChild("upper_body");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.tail = root.getChild("tail");
        this.realTail = this.tail.getChild("real_tail");
    }

    /**
     * Defines the raw mesh geometry (without texture/UV size wrapper).
     * Used directly by createBodyLayer() and to produce the baby layer via BABY_TRANSFORMER.
     */
    public static MeshDefinition createMeshDefinition() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        buildParts(root);
        return mesh;
    }

    /** Convenience wrapper that packages the mesh into a ready-to-bake LayerDefinition. */
    public static LayerDefinition createBodyLayer() {
        return LayerDefinition.create(createMeshDefinition(), 64, 64);
    }

    private static void buildParts(PartDefinition root) {

        // ── HEAD ─────────────────────────────────────────────────────────
        // Pivot sits slightly higher and further back than the wolf so the
        // wider skull doesn't clip into the chest.
        PartDefinition head = root.addOrReplaceChild(
                "head",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 12.5F, -7.0F));

        head.addOrReplaceChild(
                "real_head",
                CubeListBuilder.create()
                        // Wide Staffy skull — 8 units across vs wolf's 6
                        .texOffs(0, 0)
                        .addBox(-4.0F, -4.0F, -3.0F, 8.0F, 7.0F, 6.0F)
                        // Rose ears: small, folded flat, sitting on the sides of the skull
                        .texOffs(0, 20)
                        .addBox(-5.5F, -5.0F, 0.5F, 3.0F, 3.0F, 1.0F)   // left rose ear
                        .texOffs(8, 20)
                        .addBox(2.5F, -5.0F, 0.5F, 3.0F, 3.0F, 1.0F)    // right rose ear
                        // Short wide muzzle — 6 wide, only 3 deep vs wolf's 4
                        .texOffs(0, 13)
                        .addBox(-3.0F, 0.0F, -5.0F, 6.0F, 4.0F, 3.0F),
                PartPose.ZERO);

        // ── BODY ─────────────────────────────────────────────────────────
        // Rotated PI/2 around X so Z-size becomes the vertical height in world space.

        // Lower body: compact but wide (7 wide, 7 deep, 8 tall in model = 8 wide in world after rotation)
        root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(28, 15)
                        .addBox(-3.5F, -2.0F, -4.0F, 7.0F, 7.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, 14.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F));

        // Chest/upper body: Staffy barrel chest — wide but tighter than before
        root.addOrReplaceChild(
                "upper_body",
                CubeListBuilder.create()
                        .texOffs(28, 0)
                        .addBox(-4.0F, -2.5F, -3.5F, 8.0F, 6.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 14.0F, -3.0F, (float) (Math.PI / 2), 0.0F, 0.0F));

        // ── LEGS ─────────────────────────────────────────────────────────
        // Shorter (6 vs 8) and thicker (3×3 vs 2×2) with a wider stance
        CubeListBuilder frontLeg = CubeListBuilder.create()
                .texOffs(0, 23).addBox(0.0F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F);
        CubeListBuilder frontLegMirror = CubeListBuilder.create().mirror()
                .texOffs(0, 23).addBox(0.0F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F);
        CubeListBuilder hindLeg = CubeListBuilder.create()
                .texOffs(12, 23).addBox(0.0F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F);
        CubeListBuilder hindLegMirror = CubeListBuilder.create().mirror()
                .texOffs(12, 23).addBox(0.0F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F);

        // Right legs are mirrored; wider stance (-3/0 on X)
        root.addOrReplaceChild("right_front_leg", frontLegMirror, PartPose.offset(-3.0F, 17.0F, -4.5F));
        root.addOrReplaceChild("left_front_leg",  frontLeg,       PartPose.offset( 0.0F, 17.0F, -4.5F));
        root.addOrReplaceChild("right_hind_leg",  hindLegMirror,  PartPose.offset(-3.0F, 17.0F,  6.5F));
        root.addOrReplaceChild("left_hind_leg",   hindLeg,        PartPose.offset( 0.0F, 17.0F,  6.5F));

        // ── TAIL ─────────────────────────────────────────────────────────
        // Short, thick, carried low — PI/6 vs wolf's PI/5
        PartDefinition tail = root.addOrReplaceChild(
                "tail",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 13.0F, 8.0F, (float) (Math.PI / 6), 0.0F, 0.0F));
        tail.addOrReplaceChild(
                "real_tail",
                CubeListBuilder.create()
                        .texOffs(0, 32)
                        .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F),
                PartPose.ZERO);

    }

    // ── ANIMATION ──────────────────────────────────────────────────────────
    // Mirrors WolfModel.setupAnim() — same behaviour, different geometry.

    @Override
    public void setupAnim(WolfRenderState state) {
        super.setupAnim(state);

        float walkPos   = state.walkAnimationPos;
        float walkSpeed = state.walkAnimationSpeed;

        // Tail wag (or stiff when angry)
        if (state.isAngry) {
            this.tail.yRot = 0.0F;
        } else {
            this.tail.yRot = Mth.cos(walkPos * 0.6662F) * 1.4F * walkSpeed;
        }

        if (state.isSitting) {
            float s = state.ageScale;
            this.upperBody.y    += 2.0F * s;
            this.upperBody.xRot  = (float) (Math.PI * 2.0 / 5.0);
            this.upperBody.yRot  = 0.0F;
            this.body.y         += 4.0F * s;
            this.body.z         -= 2.0F * s;
            this.body.xRot       = (float) (Math.PI / 4);
            this.tail.y         += 9.0F * s;
            this.tail.z         -= 2.0F * s;
            this.rightHindLeg.y += 6.7F * s;
            this.rightHindLeg.z -= 5.0F * s;
            this.rightHindLeg.xRot = (float) (Math.PI * 3.0 / 2.0);
            this.leftHindLeg.y  += 6.7F * s;
            this.leftHindLeg.z  -= 5.0F * s;
            this.leftHindLeg.xRot = (float) (Math.PI * 3.0 / 2.0);
            this.rightFrontLeg.xRot = 5.811947F;
            this.rightFrontLeg.x   += 0.01F * s;
            this.rightFrontLeg.y   += 1.0F * s;
            this.leftFrontLeg.xRot  = 5.811947F;
            this.leftFrontLeg.x    -= 0.01F * s;
            this.leftFrontLeg.y    += 1.0F * s;
        } else {
            this.rightHindLeg.xRot  = Mth.cos(walkPos * 0.6662F) * 1.4F * walkSpeed;
            this.leftHindLeg.xRot   = Mth.cos(walkPos * 0.6662F + (float) Math.PI) * 1.4F * walkSpeed;
            this.rightFrontLeg.xRot = Mth.cos(walkPos * 0.6662F + (float) Math.PI) * 1.4F * walkSpeed;
            this.leftFrontLeg.xRot  = Mth.cos(walkPos * 0.6662F) * 1.4F * walkSpeed;
        }

        // Wet-shake body roll
        this.realHead.zRot  = state.headRollAngle + state.getBodyRollAngle(0.0F);
        this.upperBody.zRot = state.getBodyRollAngle(-0.08F);
        this.body.zRot      = state.getBodyRollAngle(-0.16F);
        this.realTail.zRot  = state.getBodyRollAngle(-0.2F);

        // Head look
        this.head.xRot = state.xRot * (float) (Math.PI / 180.0);
        this.head.yRot = state.yRot * (float) (Math.PI / 180.0);

        // Behaviour 4: idle head tilt — fluid with random direction each cycle.
        // 160-tick period (~8 s). Two modes chosen per cycle (~50/50):
        //   Single: ease in → hold → ease out → pause
        //   Sweep:  ease in → brief hold → cosine sweep to other side → hold → ease out → pause
        if (state.walkAnimationSpeed < 0.05F) {
            float phase  = state.ageInTicks % 160.0F;
            int   cycle  = (int)(state.ageInTicks / 160.0F);
            int   hash   = cycle * 0x9e3779b9;
            float dir    = (hash & 0x80000000) != 0 ? 1.0F : -1.0F;
            boolean sweep = (hash & 0x40000000) != 0;

            float tilt = 0.0F;
            if (!sweep) {
                // Single tilt: ease-in 12t, hold 48t, ease-out 12t, pause 88t
                if (phase < 12.0F) {
                    tilt = dir * 0.52F * Mth.sin((phase / 12.0F) * (float)(Math.PI / 2));
                } else if (phase < 60.0F) {
                    tilt = dir * 0.52F;
                } else if (phase < 72.0F) {
                    tilt = dir * 0.52F * Mth.sin(((72.0F - phase) / 12.0F) * (float)(Math.PI / 2));
                }
            } else {
                // Sweep: ease-in 12t, hold 18t, cosine sweep to other side 40t, hold 18t, ease-out 12t, pause 60t
                if (phase < 12.0F) {
                    tilt = dir * 0.52F * Mth.sin((phase / 12.0F) * (float)(Math.PI / 2));
                } else if (phase < 30.0F) {
                    tilt = dir * 0.52F;
                } else if (phase < 70.0F) {
                    // cosine interpolation: dir → 0 → -dir
                    float t = (phase - 30.0F) / 40.0F;
                    tilt = dir * 0.52F * Mth.cos(t * (float)Math.PI);
                } else if (phase < 88.0F) {
                    tilt = -dir * 0.52F;
                } else if (phase < 100.0F) {
                    tilt = -dir * 0.52F * Mth.sin(((100.0F - phase) / 12.0F) * (float)(Math.PI / 2));
                }
            }
            this.head.zRot += tilt;
        }

        this.tail.xRot = state.tailAngle;
    }
}
