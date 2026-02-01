package net.mcreator.morebosses.entity;

import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.GeoEntity;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;

import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.nbt.CompoundTag;

import net.mcreator.morebosses.entity.LaserEntity;
import net.mcreator.morebosses.entity.MissileEntity;
import net.mcreator.morebosses.init.MorebossesModEntities;

import java.util.EnumSet;

public class RobotWhaleEntity extends Monster implements GeoEntity {
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(RobotWhaleEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(RobotWhaleEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(RobotWhaleEntity.class, EntityDataSerializers.STRING);
    
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public String animationprocedure = "empty";
    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.BLUE, ServerBossEvent.BossBarOverlay.PROGRESS);

    public RobotWhaleEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(MorebossesModEntities.ROBOT_WHALE.get(), world);
    }

    public RobotWhaleEntity(EntityType<RobotWhaleEntity> type, Level world) {
        super(type, world);
        xpReward = 23;
        setNoAi(false);
        setMaxUpStep(1f);
        setPersistenceRequired();
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHOOT, false);
        this.entityData.define(ANIMATION, "undefined");
        this.entityData.define(TEXTURE, "robot_whale");
    }

    public void setTexture(String texture) { this.entityData.set(TEXTURE, texture); }
    public String getTexture() { return this.entityData.get(TEXTURE); }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new WaterBoundPathNavigation(this, world);
    }

    @Override
    public void travel(Vec3 p_218382_1_) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(0.1F, p_218382_1_);
            this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(p_218382_1_);
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new WhaleAttackGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, LivingEntity.class, true, false));
        this.goalSelector.addGoal(3, new RandomSwimmingGoal(this, 1, 40));
        this.targetSelector.addGoal(4, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    public MobType getMobType() { return MobType.WATER; }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) { return false; }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.hurt"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.death"));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // IMUNIDADES: Afogamento, Queda e Explosões
        if (source.is(DamageTypes.DROWN) || source.is(DamageTypes.FALL) || source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.PLAYER_EXPLOSION))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Texture", this.getTexture());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Texture"))
            this.setTexture(compound.getString("Texture"));
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public void customServerAiStep() {
        super.customServerAiStep();
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }

    public static void init() {}

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.25)
            .add(Attributes.MAX_HEALTH, 500)
            .add(Attributes.ARMOR, 20)
            .add(Attributes.ATTACK_DAMAGE, 10)
            .add(Attributes.FOLLOW_RANGE, 32);
    }

    public String getSyncedAnimation() { return this.entityData.get(ANIMATION); }
    public void setAnimation(String animation) { this.entityData.set(ANIMATION, animation); }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if (this.isDeadOrDying()) return event.setAndContinue(RawAnimation.begin().thenPlay("death"));
            if (this.isInWaterOrBubble() || this.getDeltaMovement().lengthSqr() > 0.003) 
                return event.setAndContinue(RawAnimation.begin().thenLoop("swim"));
            return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.STOP;
    }

    String prevAnim = "empty";
    private PlayState procedurePredicate(AnimationState event) {
        if (!animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED || (!this.animationprocedure.equals(prevAnim) && !this.animationprocedure.equals("empty"))) {
            if (!this.animationprocedure.equals(prevAnim)) event.getController().forceAnimationReset();
            event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
            if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
                this.animationprocedure = "empty";
                event.getController().forceAnimationReset();
            }
        } else if (animationprocedure.equals("empty")) {
            prevAnim = "empty";
            return PlayState.STOP;
        }
        prevAnim = this.animationprocedure;
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
        data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return this.cache; }

    // --- LÓGICA DE ATAQUE CORRIGIDA ---
    class WhaleAttackGoal extends Goal {
        private final RobotWhaleEntity mob;
        private int attackTime;

        public WhaleAttackGoal(RobotWhaleEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.mob.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (target == null) return;

            double distance = this.mob.distanceToSqr(target);
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (this.attackTime > 0) this.attackTime--;

            if (distance <= 6.5D) { 
                if (this.attackTime == 0) {
                    this.mob.setAnimation("bite");
                    this.mob.doHurtTarget(target);
                    this.attackTime = 20; 
                }
                this.mob.getNavigation().moveTo(target, 1.2D);
            } 
            else {
                if (this.attackTime == 0) {
                    if (this.mob.getRandom().nextFloat() < 0.15F) {
                        this.mob.setAnimation("missile");
                        shootProjectile(target, "missile", 1);
                        this.attackTime = 60;
                    } 
                    else {
                        this.mob.setAnimation("shot");
                        shootProjectile(target, "laser", 2);
                        this.attackTime = 30;
                    }
                }
                if (distance > 256.0D) this.mob.getNavigation().moveTo(target, 1.0D);
                else this.mob.getNavigation().stop();
            }
        }

        private void shootProjectile(LivingEntity target, String type, int count) {
            for (int i = 0; i < count; i++) {
                AbstractArrow projectile;
                if (type.equals("laser")) projectile = new LaserEntity(MorebossesModEntities.LASER.get(), this.mob.level());
                else projectile = new MissileEntity(MorebossesModEntities.MISSILE.get(), this.mob.level());

                projectile.setOwner(this.mob);
                
                // Posição de disparo (Olhos da baleia)
                double spawnX = this.mob.getX();
                double spawnY = this.mob.getY() + this.mob.getEyeHeight();
                double spawnZ = this.mob.getZ();
                projectile.setPos(spawnX, spawnY, spawnZ);

                // CÁLCULO DE MIRA DIRETO (Estilo Esqueleto/Blaze)
                double dX = target.getX() - spawnX;
                double dY = target.getY(0.5D) - spawnY; // Mira no meio do corpo do alvo
                double dZ = target.getZ() - spawnZ;
                
                // O segredo está aqui: tiramos o arco de gravidade (0.2D) para o tiro ser reto
                projectile.shoot(dX, dY, dZ, 1.6F, 1.0F); 
                this.mob.level().addFreshEntity(projectile);
            }
        }
    }
}