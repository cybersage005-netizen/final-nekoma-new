package net.greenjab.nekomasfixed.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.greenjab.nekomasfixed.registries.ModModelLayerRegistry;
import net.greenjab.nekomasfixed.registry.entity.Carpet;
import net.greenjab.nekomasfixed.render.entity.model.CarpetModel;
import net.greenjab.nekomasfixed.render.entity.state.CarpetRenderState;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.CushionRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.decoration.Cushion;
import net.minecraft.world.item.DyeColor;

import java.util.EnumMap;

public class CarpetRenderer extends EntityRenderer<Carpet, CarpetRenderState> {
    private final CarpetModel model;

    public CarpetRenderer(final EntityRendererProvider.Context context) {
        super(context);
        this.model = new CarpetModel(context.bakeLayer(ModModelLayerRegistry.CARPET));
    }

    private Identifier getId(DyeColor color, boolean isSpotted){
        return Identifier.fromNamespaceAndPath("nekomasfixed", "textures/entity/carpet/"+color.getName() + (isSpotted ? "_spotted" : "") + "_carpet.png");
    }

    public void extractRenderState(final Carpet carpet, final CarpetRenderState state, final float partialTicks) {
        super.extractRenderState(carpet, state, partialTicks);
        state.direction = Direction.fromYRot(carpet.getYRot());
        state.texture = getId(carpet.getColor(), carpet.isSpotted());
    }

    public void submit(final CarpetRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.rotateDegrees(Axis.YP, 180.0F - state.direction.toYRot());
        poseStack.rotateDegrees(Axis.XP, 180.0F);
        poseStack.translate(0.0, -0.25, 0.0);
        submitNodeCollector.submitModel(
                this.model, state, poseStack, this.model.renderType(state.texture), state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor
        );
        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    public CarpetRenderState createRenderState() {
        return new CarpetRenderState();
    }
}
