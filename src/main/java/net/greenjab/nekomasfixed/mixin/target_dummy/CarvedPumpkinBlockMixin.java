package net.greenjab.nekomasfixed.mixin.target_dummy;

import net.greenjab.nekomasfixed.registry.entity.TargetDummy;
import net.greenjab.nekomasfixed.registry.entity.TuffGolem.TuffGolem;
import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(CarvedPumpkinBlock.class)
public abstract class CarvedPumpkinBlockMixin {

    @Shadow
    private static void spawnGolemInWorld(Level level, BlockPattern.BlockPatternMatch match, Entity golem, BlockPos spawnPos) {
    }

    @Shadow @Final private static Predicate<BlockState> PUMPKINS_PREDICATE;
    @Unique
    @Nullable
    private BlockPattern targetDummyPattern;
    private BlockPattern tuffGolemPattern;

    @Unique
    private BlockPattern getTargetDummyPattern() {
        if (this.targetDummyPattern == null) {
            this.targetDummyPattern = BlockPatternBuilder.start()
                    .aisle("^", "#")
                    .where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE))
                    .where('#', BlockInWorld.hasState(/* method_72574 */ state -> state.is(Blocks.HAY_BLOCK)))
                    .build();
        }

        return this.targetDummyPattern;
    }

    @Unique
    private BlockPattern getTuffGolemPattern() {
        if (this.tuffGolemPattern == null) {
            this.tuffGolemPattern = BlockPatternBuilder.start()
                    .aisle("P", "^", "#")
                    .where('P', BlockInWorld.hasState(PUMPKINS_PREDICATE))
                    .where('^', BlockInWorld.hasState(state -> state.is(BlockTags.WOOL)))
                    .where('#', BlockInWorld.hasState(/* method_72574 */ state -> state.is(Blocks.TUFF)))
                    .build();
        }

        return this.tuffGolemPattern;
    }

    @Inject(method="trySpawnGolem", at = @At( value = "HEAD"), cancellable = true)
    private void spawnTargetDummy(Level level, BlockPos topPos, CallbackInfo ci) {
        BlockState block = level.getBlockState(topPos);
        BlockPattern.BlockPatternMatch result3 = this.getTargetDummyPattern().find(level, topPos);
        BlockPattern.BlockPatternMatch resultMatchesTuffGolem = this.getTuffGolemPattern().find(level, topPos);

        if(resultMatchesTuffGolem != null){
            TuffGolem tuffGolem = EntityTypeRegistry.TUFF_GOLEM.create(level, EntitySpawnReason.TRIGGERED);
            if(tuffGolem!=null){
                BlockState pumpkinState = resultMatchesTuffGolem.getBlock(0, 0, 0).getState();
                Direction dir = pumpkinState.getValue(HorizontalDirectionalBlock.FACING);

                tuffGolem.snapTo(topPos.getX(), topPos.getY(), topPos.getZ(), dir.toYRot(), 0.0F);
                tuffGolem.faceDirection(dir);
                tuffGolem.getEntityData().set(TuffGolem.DIRECTION_FACING, pumpkinState.getValue(CarvedPumpkinBlock.FACING));
                tuffGolem.getEntityData().set(TuffGolem.ANCHOR_BLOCK_STATE, resultMatchesTuffGolem.getBlock(0, 3, 0).getState());
                tuffGolem.getEntityData().set(TuffGolem.ANCHOR_BLOCK_POS, resultMatchesTuffGolem.getBlock(0, 3, 0).getPos());

                spawnGolemInWorld(level, resultMatchesTuffGolem, tuffGolem, resultMatchesTuffGolem.getBlock(0, 2, 0).getPos());

                if (block.is(Blocks.CARVED_PUMPKIN)) {
                    tuffGolem.snapTo(resultMatchesTuffGolem.getBlock(0, 2, 0).getPos(), block.getValue(HorizontalDirectionalBlock.FACING).toYRot(),0);
                }
                ci.cancel();
            }
        }

        if (result3 != null) {
            TargetDummy targetDummy = EntityTypeRegistry.TARGET_DUMMY.create(level, EntitySpawnReason.TRIGGERED);
            if (targetDummy != null) {
                spawnGolemInWorld(level, result3, targetDummy, result3.getBlock(0, 1, 0).getPos());
                if (block.is(Blocks.CARVED_PUMPKIN)) {
                    targetDummy.snapTo(result3.getBlock(0, 1, 0).getPos(), block.getValue(HorizontalDirectionalBlock.FACING).toYRot(),0);
                }
                ci.cancel();
            }
        }
    }
}
