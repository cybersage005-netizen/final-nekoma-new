package net.greenjab.nekomasfixed.registry.entity;

import com.mojang.serialization.Codec;
import net.greenjab.nekomasfixed.registry.registries.ItemRegistry;
import net.greenjab.nekomasfixed.util.BlockDyeMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Continuation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.decoration.BlockAttachedEntity;
import net.minecraft.world.entity.decoration.Cushion;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Carpet extends BlockAttachedEntity {
    private static final EntityDataAccessor<DyeColor> DATA_COLOR = SynchedEntityData.defineId(Carpet.class, EntityDataSerializers.DYE_COLOR);
    private static final EntityDataAccessor<Boolean> DATA_SPOTTED = SynchedEntityData.defineId(Carpet.class, EntityDataSerializers.BOOLEAN);
    private static final DyeColor DEFAULT_COLOR = DyeColor.YELLOW;
    private static final boolean DEFAULT_SPOTTED_VALUE = false;

    // i did not extend it from cushion class since it would lead to bugs where the code uses
    // instanceof Cushion, or maybe, im wrong T - T

    private static Map<DyeColor, Item> SPOTTED_ITEM_MAP = new HashMap<>();

    public DyeColor getColor() {
        return this.entityData.get(DATA_COLOR);
    }
    public Boolean isSpotted() {return this.entityData.get(DATA_SPOTTED);}

    public void setColor(final DyeColor color) {
        this.entityData.set(DATA_COLOR, color);
    }

    public void setSpotted(final boolean b) {
        this.entityData.set(DATA_SPOTTED, b);
    }

    @Override
    public boolean collidedWithFluid(FluidState fluidState, BlockPos blockPos, Vec3 from, Vec3 to) {
        if(fluidState.is(FluidTags.WATER)){
            if(this.level() instanceof ServerLevel serverLevel){
                this.kill(serverLevel);
                this.dropItem(serverLevel, null);
            }
            return true;
        }
        return super.collidedWithFluid(fluidState, blockPos, from, to);
    }

    public static boolean canBePlacedAt(final Level level, final AABB boundingBox) {
        return wouldSurviveAt(level, boundingBox) && !isAnchorBuried(level, boundingBox);
    }

    public Carpet(EntityType<? extends Carpet> type, final  Level level) {
        super(type, level);
        SPOTTED_ITEM_MAP.put(DyeColor.WHITE, ItemRegistry.WHITE_SPOTTED_CARPET);
        SPOTTED_ITEM_MAP.put(DyeColor.ORANGE, ItemRegistry.ORANGE_SPOTTED_CARPET);
        SPOTTED_ITEM_MAP.put(DyeColor.MAGENTA, ItemRegistry.MAGENTA_SPOTTED_CARPET);
        SPOTTED_ITEM_MAP.put(DyeColor.LIGHT_BLUE, ItemRegistry.LIGHT_BLUE_SPOTTED_CARPET);
        SPOTTED_ITEM_MAP.put(DyeColor.YELLOW, ItemRegistry.YELLOW_SPOTTED_CARPET);
        SPOTTED_ITEM_MAP.put(DyeColor.LIME, ItemRegistry.LIME_SPOTTED_CARPET);
        SPOTTED_ITEM_MAP.put(DyeColor.PINK, ItemRegistry.PINK_SPOTTED_CARPET);
        SPOTTED_ITEM_MAP.put(DyeColor.GRAY, ItemRegistry.GRAY_SPOTTED_CARPET);
        SPOTTED_ITEM_MAP.put(DyeColor.LIGHT_GRAY, ItemRegistry.LIGHT_GRAY_SPOTTED_CARPET);
        SPOTTED_ITEM_MAP.put(DyeColor.CYAN, ItemRegistry.CYAN_SPOTTED_CARPET);
        SPOTTED_ITEM_MAP.put(DyeColor.PURPLE, ItemRegistry.PURPLE_SPOTTED_CARPET);
        SPOTTED_ITEM_MAP.put(DyeColor.BLUE, ItemRegistry.BLUE_SPOTTED_CARPET);
        SPOTTED_ITEM_MAP.put(DyeColor.BROWN, ItemRegistry.BROWN_SPOTTED_CARPET);
        SPOTTED_ITEM_MAP.put(DyeColor.GREEN, ItemRegistry.GREEN_SPOTTED_CARPET);
        SPOTTED_ITEM_MAP.put(DyeColor.RED, ItemRegistry.RED_SPOTTED_CARPET);
        SPOTTED_ITEM_MAP.put(DyeColor.BLACK, ItemRegistry.BLACK_SPOTTED_CARPET);
    }

    public void dropItem(final ServerLevel level, final @Nullable Entity causedBy) {
        this.playSound(SoundEvents.WOOL_BREAK, 1.0F, 1.0F);
        this.showBreakingParticles();
        if ((Boolean)level.getGameRules().get(GameRules.ENTITY_DROPS)) {
            if (causedBy instanceof Player) {
                Player player = (Player)causedBy;
                if (player.hasInfiniteMaterials()) {
                    return;
                }
            }
            ItemEntity itemEntity = this.spawnAtLocation(level, this.getCushionItemStackWithData());
            if (itemEntity != null && causedBy instanceof LightningBolt) {
                itemEntity.setInvulnerableTime(20);
            }
        }
    }

    private static boolean isBreakingDeniedFor(final DamageSource source) {
        Entity var2 = source.getEntity();
        boolean var10000;
        if (var2 instanceof Player player) {
            if (!player.mayBuild()) {
                var10000 = true;
                return var10000;
            }
        }
        var10000 = false;
        return var10000;
    }

    private boolean isBreakingDeniedAtPosFor(final ServerLevel level, final DamageSource source) {
        Entity var4 = source.getEntity();
        boolean var10000;
        if (var4 instanceof Player player) {
            if (!level.mayInteract(player, this.pos)) {
                var10000 = true;
                return var10000;
            }
        }
        var10000 = false;
        return var10000;
    }

    public boolean hurtServer(final ServerLevel level, final DamageSource source, final float damage) {
        return !isBreakingDeniedFor(source) && !this.isBreakingDeniedAtPosFor(level, source) ? super.hurtServer(level, source, damage) : false;
    }

    public boolean hurtClient(final DamageSource source) {
        return isBreakingDeniedFor(source) ? false : super.hurtClient(source);
    }

    public ItemStack getPickResult() {
        return isSpotted() ? new ItemStack(SPOTTED_ITEM_MAP.get(this.getColor())) :
                new ItemStack(Items.CARPET.pick(this.getColor()));
    }

    protected void tickAtCheckInterval() {
        Level var2 = this.level();
        if (var2 instanceof ServerLevel level) {
            BlockPos blockPos = this.blockPosition();
            FluidState fluidState = level.getBlockState(blockPos).getFluidState();
            if (this.collidedWithFluid(fluidState, blockPos, this.position(), this.position())) {
                fluidState.entityInside(level, blockPos, this, this.insideEffectCollector);
                this.insideEffectCollector.applyAndClear(this);
            }
            this.destroyIfInFire(level);
        }

    }

    public void destroyIfInFire(final ServerLevel level) {
        if (!this.isRemoved()) {
            level.findBlocksIn(this.getBoundingBox().nextDeflated()).filterState((state) -> state.is(BlockTags.FIRE)).forEachUntil((var2, var3) -> {
                this.hurtServer(level, this.damageSources().inFire(), 1.0F);
                return Continuation.ABORT;
            });
        }
    }

    public void thunderHit(final ServerLevel level, final LightningBolt lightningBolt) {
        if (!this.isRemoved()) {
            this.kill(level, lightningBolt);
            this.dropItem(level, lightningBolt);
        }
    }

    public void setPos(final double x, final double y, final double z) {
        this.setPosRaw(x, y, z);
        super.setPos(x, y, z);
    }

    private void showBreakingParticles() {
        Level var2 = this.level();
        if (var2 instanceof ServerLevel level) {
            level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, ((Block) Blocks.WOOL.pick(this.getColor())).defaultBlockState()), this.getX(), this.getY(0.6666666666666666), this.getZ(), 10, (double)(this.getBbWidth() / 4.0F), (double)(this.getBbHeight() / 4.0F), (double)(this.getBbWidth() / 4.0F), 0.05);
        }
    }

    public static boolean wouldSurviveAt(final Level level, final AABB boundingBox) {
        return hasAnchorBelow(level, boundingBox) && !isCoveredBySuffocatingBlocks(level, boundingBox);
    }

    private static boolean hasAnchorBelow(final Level level, final AABB boundingBox) {
        AABB anchorBox = new AABB(boundingBox.minX, boundingBox.minY - (double)0.015625F, boundingBox.minZ, Math.nextDown(boundingBox.maxX), boundingBox.minY, Math.nextDown(boundingBox.maxZ));
        return level.findBlocksIn(anchorBox.expandTowards((double)0.0F, (double)-0.125F, (double)0.0F)).forEachUntil((blockPos, blockState) -> {
            VoxelShape shape = blockState.getShape(level, blockPos);
            return !shape.isEmpty() && shape.bounds().move(blockPos).intersects(anchorBox) ? Continuation.ABORT : Continuation.CONTINUE;
        });
    }

    private static boolean isAnchorBuried(final Level level, final AABB boundingBox) {
        AABB restingSlice = (new AABB(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.minY + (double)0.015625F, boundingBox.maxZ)).nextDeflated();
        VoxelShape exposedSurface = Shapes.create(restingSlice);

        for(VoxelShape collider : level.getBlockCollisions((Entity)null, restingSlice)) {
            exposedSurface = Shapes.join(exposedSurface, collider, BooleanOp.ONLY_FIRST);
            if (exposedSurface.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    private static boolean isCoveredBySuffocatingBlocks(final Level level, final AABB boundingBox) {
        for(BlockPos blockPos : BlockPos.betweenClosed(boundingBox.nextDeflated())) {
            if (!level.getBlockState(blockPos).isSuffocating(level, blockPos)) {
                return false;
            }
        }

        return true;
    }

    public boolean survives() {
        return wouldSurviveAt(this.level(), this.getBoundingBox());
    }

    protected void recalculateBoundingBox() {
        this.setBoundingBox(this.makeBoundingBox());
    }

    protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
        entityData.define(DATA_COLOR, DEFAULT_COLOR);
        entityData.define(DATA_SPOTTED, DEFAULT_SPOTTED_VALUE);
    }

    protected void addAdditionalSaveData(final ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store("color", DyeColor.CODEC, this.getColor());
        output.store("spotted", Codec.BOOL, this.isSpotted());
    }

    protected void readAdditionalSaveData(final ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setColor((DyeColor)input.read("color", DyeColor.CODEC).orElse(DEFAULT_COLOR));
        this.setSpotted(input.read("spotted", Codec.BOOL).orElse(DEFAULT_SPOTTED_VALUE));
    }

    public <T> @Nullable T get(final DataComponentType<? extends T> type) {
        return (T)(type == DataComponents.CUSHION_COLOR ? castComponentValue(type, this.getColor()) : super.get(type));
    }

    protected void applyImplicitComponents(final DataComponentGetter components) {
        this.applyImplicitComponentIfPresent(components, DataComponents.CUSHION_COLOR);
        super.applyImplicitComponents(components);
    }

    protected <T> boolean applyImplicitComponent(final DataComponentType<T> type, final T value) {
        if (type == DataComponents.CUSHION_COLOR) {
            this.setColor((DyeColor)castComponentValue(DataComponents.CUSHION_COLOR, value));
            return true;
        } else {
            return super.applyImplicitComponent(type, value);
        }
    }

    private Item getStackToDrop(){
        return this.isSpotted() ? SPOTTED_ITEM_MAP.get(this.getColor()) :
                Items.CARPET.pick(this.getColor());
    }

    private ItemStack getCushionItemStackWithData() {
        ItemStack itemStack = new ItemStack(getStackToDrop());
        itemStack.set(DataComponents.CUSTOM_NAME, this.getCustomName());
        return itemStack;
    }

}
