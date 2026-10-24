package net.greenjab.nekomasfixed.render.entity.model;

import net.greenjab.nekomasfixed.registry.entity.TuffGolem.TuffGolem;
import net.greenjab.nekomasfixed.render.entity.animation.TuffGolemAnimations;
import net.greenjab.nekomasfixed.render.entity.state.TuffGolemRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;


public class TuffGolemModel extends EntityModel<TuffGolemRenderState> {
// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
//    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "model_- converted"), "main");
    private final ModelPart root;
    private final ModelPart right_leg;
    private final ModelPart right_leg2;
    private final ModelPart left_leg;
    private final ModelPart left_leg2;
    private final ModelPart body;
    private final ModelPart tuff_golem;
    private final ModelPart torso;
    private final ModelPart nose;
    private final ModelPart arms;
    private final ModelPart left_arm;
    private final ModelPart right_arm;
    private final ModelPart skirt;
    private final ModelPart left_cape;
    private final ModelPart right_cape;
    private final ModelPart cape;
    private final ModelPart cape1;
    private final ModelPart bend;

    public final KeyframeAnimation idleAnimationState;
    public final KeyframeAnimation movingAnimationState;

    public TuffGolemModel(ModelPart root) {
        super(root);
        this.root = root.getChild("root");
        this.right_leg = this.root.getChild("right_leg");
        this.right_leg2 = this.right_leg.getChild("right_leg2");
        this.left_leg = this.root.getChild("left_leg");
        this.left_leg2 = this.left_leg.getChild("left_leg2");
        this.body = this.root.getChild("body");
        this.tuff_golem = this.body.getChild("tuff_golem");
        this.torso = this.tuff_golem.getChild("torso");
        this.nose = this.torso.getChild("nose");
        this.arms = this.torso.getChild("arms");
        this.left_arm = this.arms.getChild("left_arm");
        this.right_arm = this.arms.getChild("right_arm");
        this.skirt = this.torso.getChild("skirt");
        this.left_cape = this.skirt.getChild("left_cape");
        this.right_cape = this.skirt.getChild("right_cape");
        this.cape = this.skirt.getChild("cape");
        this.cape1 = this.skirt.getChild("cape1");
        this.bend = this.cape1.getChild("bend");

        idleAnimationState = TuffGolemAnimations.ANIM_IDLE.bake(root);
        movingAnimationState = TuffGolemAnimations.ANIM_RUNNING.bake(root);
    }

    @Override
    public void setupAnim(TuffGolemRenderState state) {
        super.setupAnim(state);
        this.movingAnimationState.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 2.0F, 2.5F);
        this.idleAnimationState.apply(state.idleAnimationState, 2f);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-4.0F, -5.0F, 0.0F));

        PartDefinition right_leg2 = right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(22, 51).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 7.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, -2.0F, 0.0F));

        PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(3.0F, -5.0F, 0.0F));

        PartDefinition left_leg2 = left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(22, 51).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 7.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, -2.0F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition tuff_golem = body.addOrReplaceChild("tuff_golem", CubeListBuilder.create(), PartPose.offsetAndRotation(6.0F, -6.0F, 5.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition torso = tuff_golem.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -19.5F, -7.0F, 14.0F, 19.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 6.5F, 6.0F));

        PartDefinition nose = torso.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(0, 3).addBox(-2.0F, -1.0F, -3.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -11.5F, -7.0F));

        PartDefinition arms = torso.addOrReplaceChild("arms", CubeListBuilder.create(), PartPose.offset(3.5F, -3.0F, -4.5F));

        PartDefinition left_arm = arms.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 33).addBox(0.0F, -2.0F, -3.0F, 6.0F, 15.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, -6.0F, 4.5F));

        PartDefinition right_arm = arms.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 33).addBox(-6.0F, -2.0F, -3.0F, 6.0F, 15.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-10.5F, -6.0F, 4.5F));

        PartDefinition skirt = torso.addOrReplaceChild("skirt", CubeListBuilder.create().texOffs(0, 43).addBox(-7.0F, -5.0F, 17.025F, 14.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, -10.0F));

        PartDefinition left_cape = skirt.addOrReplaceChild("left_cape", CubeListBuilder.create().texOffs(0, 29).addBox(0.0F, -0.5F, -7.025F, 0.0F, 9.0F, 14.125F, new CubeDeformation(0.0F)), PartPose.offset(-7.025F, -4.5F, 10.0F));

        PartDefinition right_cape = skirt.addOrReplaceChild("right_cape", CubeListBuilder.create().texOffs(0, 29).addBox(0.0F, 0.0F, -7.025F, 0.0F, 10.0F, 14.1F, new CubeDeformation(0.0F)), PartPose.offset(7.025F, -5.0F, 10.0F));

        PartDefinition cape = skirt.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(0, 43).addBox(-7.025F, -1.0F, 0.025F, 14.075F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 17.0F));

        PartDefinition cape1 = skirt.addOrReplaceChild("cape1", CubeListBuilder.create().texOffs(0, 33).addBox(-7.05F, 0.0F, -0.025F, 14.1F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 3.0F));

        PartDefinition bend = cape1.addOrReplaceChild("bend", CubeListBuilder.create().texOffs(0, 39).addBox(-7.0F, 0.0F, -0.025F, 14.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

//    @Override
//    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
//
//    }
//
//    @Override
//    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
//        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
//    }
}
