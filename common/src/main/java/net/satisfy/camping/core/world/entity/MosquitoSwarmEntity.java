package net.satisfy.camping.core.world.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.satisfy.camping.core.registry.CampingEffects;
import net.satisfy.camping.core.registry.CampingSounds;
import net.satisfy.camping.core.util.MosquitoUtil;
import net.satisfy.camping.platform.PlatformHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.Optional;

public class MosquitoSwarmEntity extends Mob {
    public static final int MIN_MOSQUITOES = 3;
    public static final int MAX_MOSQUITOES = 8;
    private static final int ITCHY_DURATION = 600;
    private static final int TIME_TO_BITE = 60;
    private static final int FED_DURATION = 1200;
    private static final double CHASE_RANGE = 16.0;
    private static final double GIVE_UP_RANGE = 24.0;
    private static final DustParticleOptions SPLAT = new DustParticleOptions(new Vector3f(0.3F, 0.3F, 0.3F), 0.5F);
    private static final EntityDataAccessor<Integer> MOSQUITOES = SynchedEntityData.defineId(MosquitoSwarmEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> CHASING = SynchedEntityData.defineId(MosquitoSwarmEntity.class, EntityDataSerializers.BOOLEAN);

    @Nullable
    private BlockPos home;
    @Nullable
    private Player target;
    private int chaseTicks;
    private int fedTicks;
    private float orbitAngle;

    public MosquitoSwarmEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.orbitAngle = this.random.nextFloat() * 6.2831855F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 1.0).add(Attributes.FLYING_SPEED, 0.3);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MOSQUITOES, MAX_MOSQUITOES);
        builder.define(CHASING, false);
    }

    public int getMosquitoes() {
        return this.entityData.get(MOSQUITOES);
    }

    public void setMosquitoes(int mosquitoes) {
        this.entityData.set(MOSQUITOES, Math.max(0, Math.min(MAX_MOSQUITOES, mosquitoes)));
    }

    public boolean isChasing() {
        return this.entityData.get(CHASING);
    }

    public void setHome(BlockPos home) {
        this.home = home;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel serverLevel) {
            serverTick(serverLevel);
        }
    }

    private void serverTick(ServerLevel level) {
        if (this.home == null) this.home = this.blockPosition();
        if (!PlatformHelper.isMosquitoesEnabled() || (!level.isNight() && this.random.nextInt(100) == 0)) {
            this.discard();
            return;
        }
        if (this.fedTicks > 0) this.fedTicks--;

        if (this.target != null && (!this.target.isAlive() || this.fedTicks > 0 || this.distanceToSqr(this.target) > GIVE_UP_RANGE * GIVE_UP_RANGE || (this.tickCount % 10 == 0 && !MosquitoUtil.canBother(level, this.target)))) {
            this.target = null;
        }
        if (this.target == null && this.fedTicks == 0 && this.tickCount % 20 == 0) {
            this.target = level.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(CHASE_RANGE), player -> MosquitoUtil.canBother(level, player)).stream()
                    .min(Comparator.comparingDouble(this::distanceToSqr)).orElse(null);
            this.chaseTicks = 0;
        }
        this.entityData.set(CHASING, this.target != null);

        Vec3 goal;
        if (this.target != null) {
            this.orbitAngle += 0.06F;
            goal = this.target.position().add(Math.cos(this.orbitAngle) * 1.5, 0.3, Math.sin(this.orbitAngle) * 1.5);
            if (this.distanceToSqr(this.target) < 9.0 && ++this.chaseTicks >= TIME_TO_BITE) {
                bite(level, this.target);
            }
        } else {
            goal = Vec3.atBottomCenterOf(this.home).add(Math.sin(this.tickCount * 0.03) * 0.4, 0.1, Math.cos(this.tickCount * 0.025) * 0.4);
        }
        Vec3 pull = goal.subtract(this.position()).scale(0.03);
        double speed = this.getAttributeValue(Attributes.FLYING_SPEED);
        if (pull.lengthSqr() > speed * speed) pull = pull.normalize().scale(speed);
        this.setDeltaMovement(this.getDeltaMovement().scale(0.85).add(pull.scale(0.4)));
    }

    private void bite(ServerLevel level, Player player) {
        if (PlatformHelper.isItchyEffectEnabled()) {
            player.addEffect(new MobEffectInstance(CampingEffects.itchy(), ITCHY_DURATION, 0, false, false, true));
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.PLAYERS, 0.3F, 1.8F);
        }
        this.fedTicks = FED_DURATION;
        this.target = null;
    }

    public void sprayed() {
        if (this.level() instanceof ServerLevel level) {
            level.sendParticles(SPLAT, this.getX(), this.getY() + 1.0, this.getZ(), 4 + getMosquitoes(), 0.3, 0.6, 0.3, 0.0);
        }
        this.discard();
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (this.isInvulnerableTo(source) || this.level().isClientSide || this.invulnerableTime > 0) return false;
        int left = getMosquitoes() - 1;
        if (this.level() instanceof ServerLevel level) {
            level.sendParticles(SPLAT, this.getX(), this.getY() + 1.0, this.getZ(), 4, 0.25, 0.3, 0.25, 0.0);
            level.playSound(null, this.getX(), this.getY() + 1.0, this.getZ(), SoundEvents.SLIME_SQUISH_SMALL, SoundSource.NEUTRAL, 0.5F, 1.6F + this.random.nextFloat() * 0.3F);
        }
        if (left <= 0) {
            return super.hurt(source, Float.MAX_VALUE);
        }
        setMosquitoes(left);
        this.invulnerableTime = 8;
        if (source.getEntity() instanceof ServerPlayer player && this.fedTicks == 0) {
            this.target = player;
        }
        return true;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, @NotNull BlockState state, @NotNull BlockPos pos) {
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, @NotNull DamageSource source) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(@NotNull Entity entity) {
    }

    @Override
    public boolean shouldDropExperience() {
        return false;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return CampingSounds.MOSQUITO_BUZZ;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 60;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(@NotNull DamageSource source) {
        return null;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return null;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Mosquitoes", getMosquitoes());
        tag.putInt("FedTicks", this.fedTicks);
        if (this.home != null) tag.put("Home", NbtUtils.writeBlockPos(this.home));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Mosquitoes")) setMosquitoes(tag.getInt("Mosquitoes"));
        this.fedTicks = tag.getInt("FedTicks");
        Optional<BlockPos> home = NbtUtils.readBlockPos(tag, "Home");
        this.home = home.orElse(null);
    }
}
