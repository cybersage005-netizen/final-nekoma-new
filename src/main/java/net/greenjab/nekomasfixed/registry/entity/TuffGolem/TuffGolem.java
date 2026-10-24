package net.greenjab.nekomasfixed.registry.entity.TuffGolem;

import com.mojang.serialization.Codec;
import com.sun.jna.platform.win32.WinDef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class TuffGolem extends PathfinderMob implements PlayerRideable {
    public static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(TuffGolem.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Direction> DIRECTION_FACING = SynchedEntityData.defineId(TuffGolem.class, EntityDataSerializers.DIRECTION);
    public static final EntityDataAccessor<BlockState> ANCHOR_BLOCK_STATE = SynchedEntityData.defineId(TuffGolem.class, EntityDataSerializers.BLOCK_STATE);
    public static final EntityDataAccessor<BlockPos> ANCHOR_BLOCK_POS = SynchedEntityData.defineId(TuffGolem.class, EntityDataSerializers.BLOCK_POS);
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkingAnimationState = new AnimationState();

    public TuffGolem(EntityType<TuffGolem> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(STATE, State.IDLE.getState());
        builder.define(ANCHOR_BLOCK_POS, this.getOnPos());
        builder.define(ANCHOR_BLOCK_STATE, Blocks.GRASS_BLOCK.defaultBlockState());
        builder.define(DIRECTION_FACING, this.getDirection());
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            updateAnimations();
        } else {
            updateState();
        }
    }

    public void faceDirection(Direction dir) {
        float yaw = dir.toYRot();
        this.setYRot(yaw);
        this.yRotO = yaw;
        this.setYHeadRot(yaw);
        this.yHeadRotO = yaw;
        this.setYBodyRot(yaw);
        this.yBodyRotO = yaw;
        this.entityData.set(DIRECTION_FACING, dir);
    }

    private void updateState() {
        double dx = getX() - xOld, dz = getZ() - zOld;
        boolean moving = dx * dx + dz * dz > 1.0E-6;
        boolean holding = !getMainHandItem().isEmpty();
        setState(moving ? (holding ? State.HOLDING_MOVING : State.MOVING)
                : (holding ? State.HOLDING_IDLE : State.IDLE));
    }

    private void updateAnimations() {
        State s = getState();
        boolean moving = s == State.MOVING || s == State.HOLDING_MOVING;
        if (moving) {
            walkingAnimationState.startIfStopped(tickCount);
            idleAnimationState.stop();
        } else {
            walkingAnimationState.stop();
            idleAnimationState.startIfStopped(tickCount);
        }
    }

    public State getState() {
        return State.getState(entityData.get(STATE));
    }

    public void setState(State state) {
        if (getState() != state) entityData.set(STATE, state.getState());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4);

    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 0.4d));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return true;
    }

    @Override
    public @NonNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(STATE, input.read("state", Codec.INT).orElse(0));
        this.entityData.set(DIRECTION_FACING, input.read("direction_facing", Direction.CODEC).orElse(Direction.NORTH));
        this.entityData.set(ANCHOR_BLOCK_STATE, input.read("anchor_block_state", BlockState.CODEC).orElse(Blocks.GRASS_BLOCK.defaultBlockState()));
        this.entityData.set(ANCHOR_BLOCK_POS, input.read("anchor_block_pos", BlockPos.CODEC).orElse(this.getOnPos()));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store("state", Codec.INT, this.entityData.get(STATE));
        output.store("direction_facing", Direction.CODEC, this.entityData.get(DIRECTION_FACING));
        output.store("anchor_block_state", BlockState.CODEC, this.entityData.get(ANCHOR_BLOCK_STATE));
        output.store("anchor_block_pos", BlockPos.CODEC, this.entityData.get(ANCHOR_BLOCK_POS));
    }

    public  enum State{
        IDLE(0), HOLDING_IDLE(3), HOLDING_MOVING(4), MOVING(1), LOOKING(2);

        private final int state;
        State(int state){
            this.state = state;
        }

        public static State getState(int ord){
            return switch (ord){
                case 0 -> IDLE;
                case 1 -> MOVING;
                case 2 -> LOOKING;
                case 3 -> HOLDING_IDLE;
                case 4 -> HOLDING_MOVING;
                default -> IDLE;
            };
        }

        public int getState(){
            return this.state;
        }
    }
}
