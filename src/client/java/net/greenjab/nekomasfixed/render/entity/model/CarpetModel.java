package net.greenjab.nekomasfixed.render.entity.model;

import net.greenjab.nekomasfixed.render.entity.state.CarpetRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class CarpetModel extends EntityModel<CarpetRenderState> {
    public CarpetModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();
        root.addOrReplaceChild(
                "carpet",
                CubeListBuilder.create().texOffs(0, 0).addBox(-31.0F, -1.0F, -1.0F, 16.0F, 1.0F, 16.0F, new CubeDeformation(-0.005F)),
                PartPose.offset(23.0F, 4.0F, -7.0F)
        );
        return LayerDefinition.create(meshDefinition, 64, 64);
    }


}
