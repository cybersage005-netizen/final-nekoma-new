package net.greenjab.nekomasfixed.registry.item;

import net.greenjab.nekomasfixed.registry.entity.Carpet;
import net.greenjab.nekomasfixed.registry.registries.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.PostSpawnProcessor;
import net.minecraft.world.entity.decoration.Cushion;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class CarpetItem extends Item {
    private static final double COLLISION_SHAPE_RAYCAST_EPSILON = 0.001;

    public CarpetItem(final Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(final UseOnContext context) {
        UseOnContext recalculatedContext = recalculateContextForSpecialCollisionShapes(context);
        Level level = context.getLevel();
        Direction clickedFace = recalculatedContext.getClickedFace();
        if (clickedFace != Direction.UP) {
            return InteractionResult.FAIL;
        }

        BlockPlaceContext placeContext = new BlockPlaceContext(recalculatedContext);
        BlockPos blockPos = placeContext.getClickedPos();
        Vec3 entityPos = Vec3.atCenterOfWithY(blockPos, recalculatedContext.getClickLocation().y);
        AABB spawnAABB = EntityTypeRegistry.CARPET.getSpawnAABB(entityPos);
        if (!Cushion.canBePlacedAt(level, spawnAABB)) {
            return InteractionResult.FAIL;
        }

        ItemStack itemStack = context.getItemInHand();
        if (level instanceof ServerLevel serverLevel) {
            if (!serverLevel.getEntitiesOfClass(Carpet.class, spawnAABB).isEmpty()) {
                return InteractionResult.FAIL;
            }

            PostSpawnProcessor<Carpet> entityConfig = EntityType.createDefaultStackConfig(serverLevel, itemStack, context.getPlayer());
            Carpet carpet = EntityTypeRegistry.CARPET.create(serverLevel, entityConfig, blockPos, EntitySpawnReason.SPAWN_ITEM_USE, true, true);
            if (carpet == null) {
                return InteractionResult.FAIL;
            }

            carpet.setColor(DyeColor.YELLOW);
            carpet.snapTo(entityPos, Direction.fromYRot(placeContext.getRotation()).toYRot(), 0.0F);
            serverLevel.addFreshEntity(carpet);
            carpet.destroyIfInFire(serverLevel);
            level.playSound(null, carpet.getX(), carpet.getY(), carpet.getZ(), SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
            carpet.gameEvent(GameEvent.ENTITY_PLACE, context.getPlayer());
            itemStack.consume(1, placeContext.getPlayer());
        }

        return InteractionResult.SUCCESS;
    }

    public static UseOnContext recalculateContextForSpecialCollisionShapes(final UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return context;
        }

        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);
        if (!clickedState.is(BlockTags.CUSHION_USES_COLLISION_SHAPE)) {
            return context;
        }

        Vec3 rayFrom = player.getEyePosition();
        Vec3 ray = context.getClickLocation().subtract(rayFrom);
        Vec3 rayTo = context.getClickLocation().add(ray.normalize().scale(0.001));
        BlockHitResult collisionHitResult = clickedState.getCollisionShape(level, clickedPos).clip(rayFrom, rayTo, clickedPos);
        return collisionHitResult == null ? context : new UseOnContext(player, context.getHand(), collisionHitResult);
    }
}
