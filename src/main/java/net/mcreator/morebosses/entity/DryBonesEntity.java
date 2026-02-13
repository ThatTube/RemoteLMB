package net.mcreator.morebosses.entity;

import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.GeoEntity;

import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.core.particles.ParticleTypes;

import net.mcreator.morebosses.init.MorebossesModEntities;

public class DryBonesEntity extends Monster implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(DryBonesEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(DryBonesEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(DryBonesEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> ATTACK_TYPE = SynchedEntityData.defineId(DryBonesEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Boolean> IS_SPINNING = SynchedEntityData.defineId(DryBonesEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> IS_STUNNED = SynchedEntityData.defineId(DryBonesEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(DryBonesEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> IS_LAUGHING = SynchedEntityData.defineId(DryBonesEntity.class, EntityDataSerializers.BOOLEAN);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private long lastSwing;
	private int spinTimer = 0;
	private int stunTimer = 0;
	private int attackCooldown = 0;
	private int attackChoice = 0;
	private int attackAnimationTimer = 0;
	private int laughTimer = 0;
	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.RED, ServerBossEvent.BossBarOverlay.PROGRESS);
	private static final double MINI_HITBOX_HEIGHT = 1.5;
	private static final double MINI_HITBOX_SIZE = 0.4;
	private static final double MINI_HITBOX_DAMAGE_MULTIPLIER = 1.5;

	public DryBonesEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(MorebossesModEntities.DRY_BONES.get(), world);
	}

	public DryBonesEntity(EntityType<DryBonesEntity> type, Level world) {
		super(type, world);
		xpReward = 54;
		setNoAi(false);
		setMaxUpStep(1f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "eisque");
		this.entityData.define(ATTACK_TYPE, 0);
		this.entityData.define(IS_SPINNING, false);
		this.entityData.define(IS_STUNNED, false);
		this.entityData.define(IS_ATTACKING, false);
		this.entityData.define(IS_LAUGHING, false);
	}

	public void setTexture(String texture) {
		this.entityData.set(TEXTURE, texture);
	}

	public String getTexture() {
		return this.entityData.get(TEXTURE);
	}

	public boolean isSpinning() {
		return this.entityData.get(IS_SPINNING);
	}

	public void setSpinning(boolean spinning) {
		this.entityData.set(IS_SPINNING, spinning);
	}

	public boolean isStunned() {
		return this.entityData.get(IS_STUNNED);
	}

	public void setStunned(boolean stunned) {
		this.entityData.set(IS_STUNNED, stunned);
	}

	public boolean isAttacking() {
		return this.entityData.get(IS_ATTACKING);
	}

	public void setAttacking(boolean attacking) {
		this.entityData.set(IS_ATTACKING, attacking);
	}

	public boolean isLaughing() {
		return this.entityData.get(IS_LAUGHING);
	}

	public void setLaughing(boolean laughing) {
		this.entityData.set(IS_LAUGHING, laughing);
	}

	public int getAttackType() {
		return this.entityData.get(ATTACK_TYPE);
	}

	public void setAttackType(int type) {
		this.entityData.set(ATTACK_TYPE, type);
	}

	// Métodos restaurados para compatibilidade com EntityAnimationFactory
	public String getSyncedAnimation() {
		return this.entityData.get(ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(ANIMATION, animation);
	}

	// Variável temporária para compatibilidade com EntityAnimationFactory
	public String animationprocedure = "empty";

	public static void init() {
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, CopperMonstrosityEntity.class, (float) 2, 3, 1.2));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true, false));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, PiglinBoulusEntity.class, true, false));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, PiglinBoxerEntity.class, true, false));
		this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.5, true) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
			}

			@Override
			protected void checkAndPerformAttack(LivingEntity target, double distance) {
				if (distance <= this.getAttackReachSqr(target) && this.getTicksUntilNextAttack() <= 0) {
					DryBonesEntity dryBones = (DryBonesEntity) this.mob;
					if (dryBones.isSpinning() || dryBones.isStunned() || dryBones.isAttacking() || dryBones.isLaughing())
						return;
					if (dryBones.attackCooldown <= 0) {
						dryBones.performAttack(target);
						this.resetAttackCooldown();
					}
				}
			}
		});
		this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1) {
			@Override
			public boolean canUse() {
				return !DryBonesEntity.this.isSpinning() && !DryBonesEntity.this.isStunned() && !DryBonesEntity.this.isAttacking() && !DryBonesEntity.this.isLaughing() && super.canUse();
			}
		});
		this.targetSelector.addGoal(7, new HurtByTargetGoal(this).setAlertOthers());
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(9, new FloatGoal(this));
	}

	private void performAttack(LivingEntity target) {
		this.setAttacking(true);
		this.attackAnimationTimer = 20;
		if (attackChoice == 0) {
			this.setAttackType(1);
			this.swing(InteractionHand.MAIN_HAND);
			target.hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
		} else {
			this.setAttackType(3);
			this.setSpinning(true);
			this.spinTimer = 45;
		}
		this.attackCooldown = 40;
		this.attackChoice = (this.attackChoice + 1) % 2;
	}

	private void doSpinDamage() {
		if (!this.level().isClientSide && this.spinTimer > 0) {
			AABB spinArea = this.getBoundingBox().inflate(3.0);
			for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, spinArea)) {
				if (entity != this && this.canAttack(entity)) {
					if (this.spinTimer % 5 == 0) {
						entity.hurt(this.damageSources().mobAttack(this), 3.0f);
					}
				}
			}
			this.spinTimer--;
			if (this.spinTimer <= 0) {
				this.setSpinning(false);
				this.setStunned(true);
				this.stunTimer = 60;
				this.getNavigation().stop();
			}
		}
	}

	private void startLaughing() {
		this.setLaughing(true);
		this.laughTimer = 40;
		this.getNavigation().stop();
	}

	@Override
	public void customServerAiStep() {
		super.customServerAiStep();
		this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
		if (this.attackCooldown > 0)
			this.attackCooldown--;
		if (this.isAttacking()) {
			if (this.attackAnimationTimer > 0)
				this.attackAnimationTimer--;
			else
				this.setAttacking(false);
		}
		if (this.isSpinning())
			doSpinDamage();
		if (this.isStunned()) {
			if (this.stunTimer > 0) {
				this.stunTimer--;
				this.getNavigation().stop();
			} else
				this.setStunned(false);
		}
		if (this.isLaughing()) {
			if (this.laughTimer > 0) {
				this.laughTimer--;
				this.getNavigation().stop();
			} else
				this.setLaughing(false);
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		// Previne dano de fogo e queda como antes
		if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.FALL)) {
			return false;
		}

		// Se NÃO estiver atordoado, todo dano é reduzido para 1
		if (!this.isStunned()) {
			// Toca som de colocar bloco de netherite
			if (!this.level().isClientSide) {
				this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.NETHERITE_BLOCK_PLACE, SoundSource.HOSTILE, 1.5f, 1.5f);

				// Spawna partículas cinza escuro (tipo netherite)
				ServerLevel serverLevel = (ServerLevel) this.level();
				for (int i = 0; i < 15; i++) {
					double offsetX = (this.random.nextDouble() - 0.5) * 1.5;
					double offsetY = this.random.nextDouble() * 2.0;
					double offsetZ = (this.random.nextDouble() - 0.5) * 1.5;
					serverLevel.sendParticles(ParticleTypes.DOLPHIN, this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ, 1, 0, 0, 0, 0.1);
				}
			}

			// Chama o hurt original com dano 1
			return super.hurt(source, 1.0f);
		}

		// Se estiver atordoado, toma dano normal
		return super.hurt(source, amount);
	}

	@Override
	public void awardKillScore(Entity entity, int score, DamageSource damageSource) {
		super.awardKillScore(entity, score, damageSource);
		// Evento de risada removido quando mata algo
		// startLaughing(); // Comentado/removido conforme solicitado
	}

	@Override
	public void baseTick() {
		super.baseTick();
		// Manter a variável animationprocedure atualizada para compatibilidade
		if (!this.level().isClientSide) {
			if (this.isSpinning())
				this.animationprocedure = "spin";
			else if (this.isStunned())
				this.animationprocedure = "stun";
			else if (this.isLaughing())
				this.animationprocedure = "lol";
			else if (this.isAttacking())
				this.animationprocedure = "attack";
			else
				this.animationprocedure = "empty";
		}
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEAD;
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

	// ALTERAÇÕES: 300 de vida, mantido ARMOR e KNOCKBACK_RESISTANCE
	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.MAX_HEALTH, 300) // Mudado de 500 para 300
				.add(Attributes.ATTACK_DAMAGE, 14).add(Attributes.ARMOR, 10).add(Attributes.KNOCKBACK_RESISTANCE, 1.0).add(Attributes.FOLLOW_RANGE, 64.0);
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.isSpinning())
			return event.setAndContinue(RawAnimation.begin().thenLoop("spin"));
		if (this.isStunned())
			return event.setAndContinue(RawAnimation.begin().thenLoop("stun"));
		if (this.isLaughing())
			return event.setAndContinue(RawAnimation.begin().thenLoop("lol"));
		if (this.isAttacking())
			return event.setAndContinue(RawAnimation.begin().thenPlay("attack"));
		if (event.isMoving())
			return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
		return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		// Controlador único para todas as animações
		data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
