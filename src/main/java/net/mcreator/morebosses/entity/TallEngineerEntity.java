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

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;

import net.mcreator.morebosses.init.MorebossesModEntities;

import java.util.EnumSet;

public class TallEngineerEntity extends Monster implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(TallEngineerEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> SUMMONING = SynchedEntityData.defineId(TallEngineerEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(TallEngineerEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(TallEngineerEntity.class, EntityDataSerializers.STRING);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	private int summonCooldown = 0;
	private int summonAnimationTime = 0;
	private static final int SUMMON_COOLDOWN_TICKS = 200; // 10 segundos
	public String animationprocedure = "empty";

	public TallEngineerEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(MorebossesModEntities.TALL_ENGINEER.get(), world);
	}

	public TallEngineerEntity(EntityType<TallEngineerEntity> type, Level world) {
		super(type, world);
		xpReward = 7;
		setNoAi(false);
		setMaxUpStep(1f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(SUMMONING, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "tall_engineer");
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	public void setTexture(String texture) {
		this.entityData.set(TEXTURE, texture);
	}

	public String getTexture() {
		return this.entityData.get(TEXTURE);
	}

	public boolean isSummoning() {
		return this.entityData.get(SUMMONING);
	}

	public void setSummoning(boolean summoning) {
		this.entityData.set(SUMMONING, summoning);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true, false));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
			}
		});
		this.goalSelector.addGoal(3, new SummonAttackGoal(this));
		this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
		this.targetSelector.addGoal(5, new HurtByTargetGoal(this).setAlertOthers());
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(7, new FloatGoal(this));
	}

	@Override
	public void aiStep() {
		super.aiStep();
		// Atualizar cooldown do summon
		if (summonCooldown > 0) {
			summonCooldown--;
		}
		// Controlar animação de summon
		if (summonAnimationTime > 0) {
			summonAnimationTime--;
			if (!this.level().isClientSide()) {
				this.setSummoning(true);
				this.setAnimation("summon");
				// Spawnar partículas no servidor (meio da animação)
				if (summonAnimationTime == 20) { // No meio da animação
					spawnTurret();
					spawnSummonParticles();
				}
				// Finalizar animação
				if (summonAnimationTime == 0) {
					this.setSummoning(false);
				}
			} else {
				// Spawnar partículas no cliente
				spawnWaxParticles();
			}
		}
	}

	private void spawnWaxParticles() {
		if (this.level().isClientSide()) {
			for (int i = 0; i < 3; i++) {
				double offsetX = (this.random.nextDouble() - 0.5) * 1.0;
				double offsetY = this.random.nextDouble() * 1.5;
				double offsetZ = (this.random.nextDouble() - 0.5) * 1.0;
				this.level().addParticle(ParticleTypes.WAX_ON, this.getX() + offsetX, this.getY() + 1.0 + offsetY, this.getZ() + offsetZ, 0, 0.1, 0);
			}
		}
	}

	private void spawnSummonParticles() {
		// Spawnar partículas no local do summon
		Vec3 spawnPos = this.position().add(this.getLookAngle().scale(2.0)).add(0, 0.5, 0);
		for (int i = 0; i < 15; i++) {
			double offsetX = (this.random.nextDouble() - 0.5) * 2.0;
			double offsetY = this.random.nextDouble() * 1.5;
			double offsetZ = (this.random.nextDouble() - 0.5) * 2.0;
			if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
				serverLevel.sendParticles(ParticleTypes.WAX_ON, spawnPos.x + offsetX, spawnPos.y + offsetY, spawnPos.z + offsetZ, 1, 0, 0, 0, 0.05);
			}
		}
	}

	private void spawnTurret() {
		if (!this.level().isClientSide()) {
			// Criar a torreta
			TurretEntity turret = MorebossesModEntities.TURRET.get().create(this.level());
			if (turret != null) {
				// Posicionar a torreta próximo ao engenheiro
				Vec3 spawnPos = this.position().add(this.getLookAngle().scale(2.0)).add(0, 0.5, 0);
				turret.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, this.getYRot(), this.getXRot());
				// Adicionar ao mundo
				this.level().addFreshEntity(turret);
				// Fazer a torreta atacar o mesmo alvo (simples, sem domar)
				if (this.getTarget() != null) {
					turret.setTarget(this.getTarget());
				}
				// Tocar som de bigorna (opcional)
				this.level().playSound(null, this.getX(), this.getY(), this.getZ(), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.anvil.place")), this.getSoundSource(), 0.5F, 1.0F);
			}
		}
	}

	private void startSummonAnimation() {
		if (summonCooldown <= 0) {
			summonAnimationTime = 40; // 2 segundos de animação
			summonCooldown = SUMMON_COOLDOWN_TICKS;
			// Tocar som de início de summon
			if (!this.level().isClientSide()) {
				this.level().playSound(null, this.getX(), this.getY(), this.getZ(), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.anvil.use")), this.getSoundSource(), 0.5F, 1.0F);
			}
		}
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEFINED;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.death"));
	}

	@Override
	public SoundEvent getAmbientSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.villager.work_toolsmith"));
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.FALL))
			return false;
		if (source.getDirectEntity() instanceof AbstractArrow)
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putInt("SummonCooldown", summonCooldown);
		compound.putInt("SummonAnimationTime", summonAnimationTime);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("SummonCooldown"))
			summonCooldown = compound.getInt("SummonCooldown");
		if (compound.contains("SummonAnimationTime"))
			summonAnimationTime = compound.getInt("SummonAnimationTime");
	}

	@Override
	public void baseTick() {
		super.baseTick();
		this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 1);
	}

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
		builder = builder.add(Attributes.MAX_HEALTH, 35);
		builder = builder.add(Attributes.ARMOR, 4);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 6);
		builder = builder.add(Attributes.FOLLOW_RANGE, 24);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.2);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("death"));
			}
			if (this.isSummoning()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("summon"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationState event) {
		double d1 = this.getX() - this.xOld;
		double d0 = this.getZ() - this.zOld;
		float velocity = (float) Math.sqrt(d1 * d1 + d0 * d0);
		if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 7L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("attack"));
		}
		return PlayState.CONTINUE;
	}

	String prevAnim = "empty";

	private PlayState procedurePredicate(AnimationState event) {
		if (!animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED || (!this.animationprocedure.equals(prevAnim) && !this.animationprocedure.equals("empty"))) {
			if (!this.animationprocedure.equals(prevAnim))
				event.getController().forceAnimationReset();
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
	protected void tickDeath() {
		++this.deathTime;
		if (this.deathTime == 20) {
			this.remove(TallEngineerEntity.RemovalReason.KILLED);
			this.dropExperience();
		}
	}

	public String getSyncedAnimation() {
		return this.entityData.get(ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(ANIMATION, animation);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 4, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	// Classe interna para o goal de summon - SIMPLES como no MachoLote
	// Classe interna para o goal de summon - Apenas alterna entre ataque e summon
	static class SummonAttackGoal extends Goal {
		private final TallEngineerEntity entity;
		private int attackCooldown = 0;

		public SummonAttackGoal(TallEngineerEntity entity) {
			this.entity = entity;
			this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		}

		@Override
		public boolean canUse() {
			return this.entity.getTarget() != null && this.entity.getTarget().isAlive();
		}

		@Override
		public boolean canContinueToUse() {
			return this.canUse();
		}

		@Override
		public void start() {
			this.attackCooldown = 0;
		}

		@Override
		public void tick() {
			LivingEntity target = this.entity.getTarget();
			if (target != null) {
				// Olhar para o alvo
				this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
				// Sistema de cooldown
				if (this.attackCooldown > 0) {
					this.attackCooldown--;
					return;
				}
				// Decidir qual ataque usar
				if (this.entity.summonCooldown <= 0) {
					// USAR SUMMON!
					this.entity.startSummonAnimation();
					this.attackCooldown = 100; // 5 segundos de cooldown geral
				} else {
					// Ataque corpo a corpo
					if (this.entity.distanceToSqr(target) < this.entity.getBbWidth() * this.entity.getBbWidth() + target.getBbWidth() + 9.0D) {
						this.entity.doHurtTarget(target);
						this.attackCooldown = 40; // 2 segundos entre ataques
					}
				}
			}
		}
	}
}
