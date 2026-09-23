package net.greenjab.nekomasfixed.mixin;

import net.greenjab.nekomasfixed.registry.block.ClamBlock;
import net.greenjab.nekomasfixed.registry.entity.Carpet;
import net.greenjab.nekomasfixed.registry.item.CarpetItem;
import net.greenjab.nekomasfixed.registry.other.AnimalComponent;
import net.greenjab.nekomasfixed.registry.registries.ComponentRegistry;
import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.greenjab.nekomasfixed.util.ModTags;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PostSpawnProcessor;
import net.minecraft.world.entity.decoration.Cushion;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @Shadow
    public abstract boolean placeBlock(BlockPlaceContext context, BlockState placementState);

    @Inject(method="onDestroyed", at = @At( value = "HEAD"), cancellable = true)
    private void releaseAnimalOnNautilusDestroyed(ItemEntity entity, CallbackInfo ci) {
        AnimalComponent animalComponent = entity.getItem().get(ComponentRegistry.ANIMAL);
        if (animalComponent != null && !animalComponent.animal().isEmpty()) {
            AnimalComponent.StoredEntityData animal = animalComponent.animal().getFirst();
            Level level = entity.level();
            BlockPos pos = entity.blockPosition();
            Entity releasedEntity = animal.loadEntity(level);
            if (releasedEntity != null) {
                double e = pos.getX() + 0.5;
                double g = pos.getY() + 0.5 - releasedEntity.getBbHeight() / 2.0F;
                double h = pos.getZ() + 0.5;
                releasedEntity.snapTo(e, g, h, releasedEntity.getYRot(), releasedEntity.getXRot());
                level.addFreshEntity(releasedEntity);
            }
            ci.cancel();
        }
    }

    @Inject(method = "placeBlock", at = @At("HEAD"), cancellable = true)
    public void placeBlock(BlockPlaceContext context, BlockState placementState, CallbackInfoReturnable<Boolean> cir) {
        BlockItem item = (BlockItem) (Object)this;
        if(placementState!=null && placementState.is(BlockTags.WOOL_CARPETS)){
            if (item.getDefaultInstance().getItem() instanceof CarpetItem carpetItem){
                carpetItem.useOn(context);
                cir.cancel();
            }
            UseOnContext recalculatedContext = CarpetItem.recalculateContextForSpecialCollisionShapes(context);
            Level level = context.getLevel();
            Direction clickedFace = recalculatedContext.getClickedFace();
            if (clickedFace != Direction.UP) {
                 cir.setReturnValue(false);
            }

            BlockPlaceContext placeContext = new BlockPlaceContext(recalculatedContext);
            BlockPos blockPos = placeContext.getClickedPos();
            Vec3 entityPos = Vec3.atCenterOfWithY(blockPos, recalculatedContext.getClickLocation().y);
            AABB spawnAABB = EntityTypeRegistry.CARPET.getSpawnAABB(entityPos);
            if (!Cushion.canBePlacedAt(level, spawnAABB)) {
                 cir.setReturnValue(false);
            }

            ItemStack itemStack = context.getItemInHand();
            if (level instanceof ServerLevel serverLevel) {
                if (!serverLevel.getEntitiesOfClass(Carpet.class, spawnAABB).isEmpty()) {
                     cir.setReturnValue(false);
                }

                PostSpawnProcessor<Carpet> entityConfig = EntityType.createDefaultStackConfig(serverLevel, itemStack, context.getPlayer());
                Carpet carpet = EntityTypeRegistry.CARPET.create(serverLevel, entityConfig, blockPos, EntitySpawnReason.SPAWN_ITEM_USE, true, true);
                if (carpet == null) {
                     cir.setReturnValue(false);
                }

                carpet.setColor(DyeColor.YELLOW);
                carpet.snapTo(entityPos, Direction.fromYRot(placeContext.getRotation()).toYRot(), 0.0F);
                serverLevel.addFreshEntity(carpet);
                carpet.destroyIfInFire(serverLevel);
                level.playSound(null, carpet.getX(), carpet.getY(), carpet.getZ(), SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
                carpet.gameEvent(GameEvent.ENTITY_PLACE, context.getPlayer());
                itemStack.consume(1, placeContext.getPlayer());
            }
            cir.cancel();

        }
    }

    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    public void place(BlockPlaceContext placeContext, CallbackInfoReturnable<InteractionResult> cir) {
        BlockItem item = (BlockItem) (Object)this;
        if (!item.getBlock().isEnabled(placeContext.getLevel().enabledFeatures())) {
            cir.setReturnValue(InteractionResult.FAIL);
            return;
        }

        if (!placeContext.canPlace()) {
            cir.setReturnValue(InteractionResult.FAIL);
            return;
        }

        BlockPlaceContext updatedPlaceContext = item.updatePlacementContext(placeContext);
        if (updatedPlaceContext == null) {
            cir.setReturnValue(InteractionResult.FAIL);
            return;
        }

        BlockState placementState = item.getPlacementState(updatedPlaceContext);
        if (placementState == null) {
            cir.setReturnValue(InteractionResult.FAIL);
            return;
        }

        if (!item.placeBlock(updatedPlaceContext, placementState)) {
            cir.setReturnValue(InteractionResult.FAIL);
            return;
        }

        BlockPos pos = updatedPlaceContext.getClickedPos();
        Level level = updatedPlaceContext.getLevel();
        Player player = updatedPlaceContext.getPlayer();
        ItemStack itemStack = updatedPlaceContext.getItemInHand();
        BlockState placedState = level.getBlockState(pos);
        if (placedState.is(placementState.getBlock())) {
            placedState = BlockItem.updateBlockStateFromTag(pos, level, itemStack, placedState);
            BlockItem.updateCustomBlockEntityTag(level, player, pos, itemStack);
            BlockItem.updateBlockEntityComponents(level, pos, itemStack);
            placedState.getBlock().setPlacedBy(level, pos, placedState, player, itemStack);
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, pos, itemStack);
            }
        }

        SoundType soundType = placedState.getSoundType();
        level.playSound(player, pos, item.getPlaceSound(placedState), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
        level.gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(player, placedState));
        itemStack.consume(1, player);
        cir.setReturnValue(InteractionResult.SUCCESS);
        cir.cancel();
    }

    @Inject(method="updateBlockStateFromTag", at = @At( value = "HEAD"))
    private static void placeOpenClam(BlockPos pos, Level level, ItemStack itemStack, BlockState placedState, CallbackInfoReturnable<BlockState> cir) {
        if (itemStack.is(ModTags.CLAMTAG)) {
            Integer i = itemStack.getOrDefault(ComponentRegistry.CLAM_STATE, 0);
            if (i > 0) {
                placedState = placedState.setValue(ClamBlock.OPEN, true);
                level.setBlock(pos, placedState, Block.UPDATE_CLIENTS);
            }
        }
    }
}
