package net.greenjab.nekomasfixed.render.entity.state;

import net.greenjab.nekomasfixed.registry.entity.TuffGolem.TuffGolem;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

public class TuffGolemRenderState extends LivingEntityRenderState {
    public TuffGolem.State entityState = TuffGolem.State.IDLE;
    public  AnimationState  idleAnimationState = new AnimationState();
    public  AnimationState walkingAnimationState = new AnimationState();
}
