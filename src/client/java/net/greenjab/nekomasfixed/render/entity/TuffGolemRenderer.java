package net.greenjab.nekomasfixed.render.entity;

import net.greenjab.nekomasfixed.NekomasFixed;
import net.greenjab.nekomasfixed.registries.ModModelLayerRegistry;
import net.greenjab.nekomasfixed.registry.entity.TuffGolem.TuffGolem;
import net.greenjab.nekomasfixed.render.entity.model.TuffGolemModel;
import net.greenjab.nekomasfixed.render.entity.state.TuffGolemRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class TuffGolemRenderer extends MobRenderer<TuffGolem, TuffGolemRenderState, TuffGolemModel> {
    private static final Identifier TEXTURE = NekomasFixed.id("textures/entity/tuff_golem/tuff_golem.png");

    public TuffGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new TuffGolemModel(context.bakeLayer(ModModelLayerRegistry.TUFF_GOLEM)), 0.25f);
    }

    @Override
    public void extractRenderState(TuffGolem entity, TuffGolemRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.entityState = entity.getState();
        state.idleAnimationState.copyFrom(entity.idleAnimationState);
        state.walkingAnimationState.copyFrom(entity.walkingAnimationState);
    }

    @Override
    public @NonNull Identifier getTextureLocation(@NonNull TuffGolemRenderState state) {
        return TEXTURE;
    }

    @Override
    public  TuffGolemRenderState createRenderState() {
        return new TuffGolemRenderState();
    }



}
