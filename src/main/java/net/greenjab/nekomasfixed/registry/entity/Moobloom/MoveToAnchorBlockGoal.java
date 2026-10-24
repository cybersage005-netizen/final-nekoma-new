package net.greenjab.nekomasfixed.registry.entity.Moobloom;

import net.greenjab.nekomasfixed.registry.entity.TuffGolem.TuffGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;

public class MoveToAnchorBlockGoal extends MoveToBlockGoal {
    private final TuffGolem golem;
    public MoveToAnchorBlockGoal(TuffGolem mob, double speedModifier, int searchRange) {
        super(mob, speedModifier, searchRange);
        this.golem = mob;
    }

    @Override
    public void start() {
        super.start();
        golem.faceDirection(golem.getEntityData().get(TuffGolem.DIRECTION_FACING));
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        return golem.getEntityData().get(TuffGolem.ANCHOR_BLOCK_STATE).getBlock()
                .equals(level.getBlockState(pos).getBlock());
    }
}
