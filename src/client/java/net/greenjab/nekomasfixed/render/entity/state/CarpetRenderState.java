package net.greenjab.nekomasfixed.render.entity.state;

import net.minecraft.client.model.object.cushion.CushionModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.decoration.Cushion;

public class CarpetRenderState extends EntityRenderState {
    private static final Identifier DEFAULT_TEXTURE = Identifier.fromNamespaceAndPath("nekomasfixed","textures/entity/carpet/white_carpet.png");
    public Direction direction = Direction.NORTH;
    public Identifier texture = DEFAULT_TEXTURE;

}
