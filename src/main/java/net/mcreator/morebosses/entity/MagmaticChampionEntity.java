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
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import net.mcreator.morebosses.init.MorebossesModMobEffects;
import net.mcreator.morebosses.init.MorebossesModEntities;

import java.util.List;

public class MagmaticChampionEntity extends Monster implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(MagmaticChampionEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(MagmaticChampionEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(MagmaticChampionEntity.class, EntityDataSerializers.STRING);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.RED, ServerBossEvent.BossBarOverlay.PROGRESS);
	// Variáveis de Controle de Estado e Combate
	private int actionState = 0;
	private int actionTimer = 0;
	private int attackCooldown = 0;
	private boolean canCombo = false;
	private boolean hasComboed = false;
	private boolean isDying = false;
	// Novas variáveis para controle de facing
	private int facingLockTimer = 0;
	private boolean forceFacing = false;
	// Constantes de Estado
	private static final int ST_IDLE = 0;
	private static final int ST_SLASH = 1;
	private static final int ST_SLASH_LEFT = 2;
	private static final int ST_SLAM = 3;
	private static final int ST_PARRY = 4;
	private static final int ST_REVENGE = 5;
	private static final int ST_JUMP_START = 6;
	private static final int ST_JUMP_MID = 7;
	private static final int ST_JUMP_END = 8;
	public String animationprocedure = "empty";

	public MagmaticChampionEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(MorebossesModEntities.MAGMATIC_CHAMPION.get(), world);
	}

	public MagmaticChampionEntity(EntityType<MagmaticChampionEntity> type, Level world) {
		super(type, world);
		xpReward = 25;
		setNoAi(false);
		setMaxUpStep(1f);
		// Configurar barra de boss
		this.bossInfo.setVisible(true);
		this.bossInfo.setCreateWorldFog(true);
		this.bossInfo.setDarkenScreen(true);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "champion");
	}

	public void setTexture(String texture) {
		this.entityData.set(TEXTURE, texture);
	}

	public String getTexture() {
		return this.entityData.get(TEXTURE);
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
			public boolean canUse() {
				return super.canUse() && actionState == ST_IDLE && !isDying;
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && actionState == ST_IDLE && !isDying;
			}

			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth() + 3.5;
			}
		});
		this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1) {
			@Override
			public boolean canUse() {
				return super.canUse() && !isDying && actionState == ST_IDLE;
			}
		});
		this.targetSelector.addGoal(4, new HurtByTargetGoal(this).setAlertOthers());
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				return super.canUse() && !isDying;
			}
		});
		this.goalSelector.addGoal(6, new FloatGoal(this));
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEAD;
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
	public boolean hurt(DamageSource source, float amount) {
		if (this.isDying) {
			return false;
		}
		// Lógica do PARRY
		if (this.actionState == ST_PARRY) {
			Entity attacker = source.getEntity();
			if (attacker instanceof LivingEntity) {
				this.actionState = ST_REVENGE;
				this.actionTimer = 25;
				// Força o olhar para o atacante
				this.forceFacing = true;
				this.lookAt(attacker, 360, 360);
				this.getLookControl().setLookAt(attacker, 360, 360);
				if (!this.level().isClientSide()) {
					this.animationprocedure = "revenge";
					this.setAnimation("revenge");
					this.level().broadcastEntityEvent(this, (byte) 0);
				}
				return false;
			}
		}
		if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.LAVA) || source.is(DamageTypes.FALL) || source.is(DamageTypes.WITHER) || source.is(DamageTypes.WITHER_SKULL) || source.is(DamageTypes.HOT_FLOOR)
				|| source.is(DamageTypes.FIREBALL) || source.is(DamageTypes.UNATTRIBUTED_FIREBALL) || source.is(DamageTypes.DRAGON_BREATH)) {
			return false;
		}
		boolean hurt = super.hurt(source, amount);
		if (this.getHealth() <= 0 && !this.isDying) {
			startDeathAnimation();
		}
		return hurt;
	}

	@Override
	public boolean fireImmune() {
		return true;
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		if (source.is(DamageTypes.WITHER) || source.is(DamageTypes.WITHER_SKULL)) {
			return true;
		}
		return super.isInvulnerableTo(source);
	}

	@Override
	public boolean canFreeze() {
		return false;
	}

	@Override
	public void lavaHurt() {
	}

	@Override
	public void setSecondsOnFire(int seconds) {
	}

	@Override
	public void setRemainingFireTicks(int ticks) {
	}

	private void startDeathAnimation() {
		if (this.isDying)
			return;
		this.isDying = true;
		this.actionState = ST_IDLE;
		this.actionTimer = 0;
		this.attackCooldown = 0;
		this.setDeltaMovement(0, 0, 0);
		this.getNavigation().stop();
		if (!this.level().isClientSide()) {
			this.animationprocedure = "death";
			this.setAnimation("death");
			this.level().broadcastEntityEvent(this, (byte) 2);
		}
		this.setNoAi(true);
	}

	@Override
	public void customServerAiStep() {
		super.customServerAiStep();
		if (this.isDying) {
			this.setDeltaMovement(0, 0, 0);
			return;
		}
		this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
		if (!this.level().isClientSide()) {
			List<ServerPlayer> players = ((ServerLevel) this.level()).getPlayers(p -> p.distanceToSqr(this) < 2500);
			for (ServerPlayer player : players) {
				if (player.hasLineOfSight(this) && !this.bossInfo.getPlayers().contains(player)) {
					this.bossInfo.addPlayer(player);
				}
			}
			List<ServerPlayer> playersToRemove = this.bossInfo.getPlayers().stream().filter(player -> player.distanceToSqr(this) > 2500 || !player.hasLineOfSight(this)).toList();
			for (ServerPlayer player : playersToRemove) {
				this.bossInfo.removePlayer(player);
			}
		}
		if (this.attackCooldown > 0)
			this.attackCooldown--;
		if (this.facingLockTimer > 0)
			this.facingLockTimer--;
		LivingEntity target = this.getTarget();
		if (target != null && !isDying) {
			// Força o boss a ficar virado para o alvo
			this.lookAt(target, 360, 360);
			this.getLookControl().setLookAt(target, 360, 360);
			double dx = target.getX() - this.getX();
			double dz = target.getZ() - this.getZ();
			float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
			this.setYRot(yaw);
			this.yRotO = this.getYRot();
			this.yBodyRot = yaw;
			this.yHeadRot = yaw;
		}
		if (this.actionState != ST_IDLE) {
			handleActionTick(target);
		} else {
			if (target != null && this.attackCooldown <= 0 && this.distanceTo(target) < 16 && !isDying) {
				decideAttack(target);
			}
		}
	}

	@Override
	public void remove(Entity.RemovalReason reason) {
		if (!this.level().isClientSide()) {
			this.bossInfo.removeAllPlayers();
			this.bossInfo.setCreateWorldFog(false);
			this.bossInfo.setDarkenScreen(false);
			this.bossInfo.setVisible(false);
		}
		super.remove(reason);
	}

	@Override
	public boolean canChangeDimensions() {
		return false;
	}

	// ==================================================================================================
	// LÓGICA PRINCIPAL
	// ==================================================================================================
	private void decideAttack(LivingEntity target) {
		double dist = this.distanceTo(target);
		double random = Math.random();
		// SÓ PULA SE ESTIVER LONGE DO ALVO (distância > 8)
		if (dist > 8 && random < 0.7) {
			startJump(target);
		} else if (random < 0.1) {
			startParry();
		} else if (random < 0.45) {
			startSlam();
		} else {
			startSlash(false);
		}
	}

	private void handleActionTick(LivingEntity target) {
		this.actionTimer--;
		this.getNavigation().stop();
		switch (actionState) {
			case ST_SLASH :
			case ST_SLASH_LEFT :
				if (actionTimer == 18) {
					Vec3 vec = this.getLookAngle().scale(1.5);
					this.setDeltaMovement(vec.x, 0.2, vec.z);
					if (!this.level().isClientSide()) {
						spawnDashParticles();
					}
				} else if (actionTimer != 18) {
					this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
				}
				if (actionTimer == 10) {
					performConeSlash(actionState == ST_SLASH_LEFT);
				}
				if (actionTimer < 8 && actionTimer > 0 && !hasComboed) {
					if (target != null && this.distanceTo(target) < 5 && Math.random() < 0.6) {
						hasComboed = true;
						if (actionState == ST_SLASH)
							startSlash(true);
						else
							startSlash(false);
						return;
					}
				}
				if (actionTimer <= 0)
					resetToIdle(20);
				break;
			case ST_SLAM :
				this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
				if (actionTimer == 10) {
					performSlamLineDamage();
					spawnMagmaPitsInLine();
				}
				if (actionTimer <= 0)
					resetToIdle(30);
				break;
			case ST_PARRY :
				this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
				if (actionTimer <= 0)
					resetToIdle(10);
				break;
			case ST_REVENGE :
				this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
				if (actionTimer == 12) {
					performRevengeAttack();
				}
				if (actionTimer <= 0)
					resetToIdle(15);
				break;
			case ST_JUMP_START :
				this.setDeltaMovement(0, 0, 0);
				if (actionTimer <= 0) {
					this.actionState = ST_JUMP_MID;
					this.actionTimer = 40;
					jumpTowardsTarget(target);
					updateAnimation("jmiddle");
					if (!this.level().isClientSide()) {
						spawnJumpParticles();
					}
				}
				break;
			case ST_JUMP_MID :
				if (this.onGround() && actionTimer < 35 && this.actionState == ST_JUMP_MID) {
					this.actionState = ST_JUMP_END;
					this.actionTimer = 15;
					updateAnimation("jend");
					performJumpLandingEffects(target);
				}
				if (actionTimer <= 0)
					resetToIdle(10);
				break;
			case ST_JUMP_END :
				this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
				if (actionTimer <= 0)
					resetToIdle(10);
				break;
		}
	}

	// ==================================================================================================
	// MÉTODOS DE AÇÃO
	// ==================================================================================================
	private void startSlash(boolean isLeft) {
		this.actionState = isLeft ? ST_SLASH_LEFT : ST_SLASH;
		this.actionTimer = 20;
		this.canCombo = true;
		updateAnimation(isLeft ? "slashleft" : "slash");
		if (!this.level().isClientSide()) {
			this.setDeltaMovement(this.getLookAngle().scale(0.3));
		}
	}

	private void startSlam() {
		this.actionState = ST_SLAM;
		this.actionTimer = 30;
		this.hasComboed = false;
		updateAnimation("slam");
	}

	private void startParry() {
		this.actionState = ST_PARRY;
		this.actionTimer = 60;
		updateAnimation("parry");
	}

	private void startJump(LivingEntity target) {
		if (target == null) {
			resetToIdle(0);
			return;
		}
		this.actionState = ST_JUMP_START;
		this.actionTimer = 10;
		updateAnimation("jstart");
	}

	private void resetToIdle(int cooldown) {
		this.actionState = ST_IDLE;
		this.attackCooldown = cooldown;
		this.animationprocedure = "empty";
		this.setAnimation("empty");
		this.hasComboed = false;
		this.forceFacing = false;
	}

	private void jumpTowardsTarget(LivingEntity target) {
		if (target == null)
			return;
		double dx = target.getX() - this.getX();
		double dz = target.getZ() - this.getZ();
		double distance = Math.sqrt(dx * dx + dz * dz);
		if (distance > 0) {
			dx /= distance;
			dz /= distance;
		}
		// PULO MAIS CURTO - Valores reduzidos
		double jumpDistance = Math.min(distance * 0.9, 10.0); // Reduzido de 14.0 para 10.0
		double verticalForce = 0.6 + (jumpDistance / 20.0); // Reduzido de 0.7 para 0.6
		double horizontalForce = jumpDistance * 0.2; // Reduzido de 0.28 para 0.25
		this.setDeltaMovement(dx * horizontalForce, verticalForce, dz * horizontalForce);
	}

	private int findGroundY(Level world, int x, int z, int startY) {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, startY, z);
		while (pos.getY() > world.getMinBuildHeight() + 1) {
			BlockState state = world.getBlockState(pos);
			if (!state.isAir() && state.isSolid()) {
				return pos.getY() + 1;
			}
			pos.move(0, -1, 0);
		}
		return startY;
	}

	private void performJumpLandingEffects(LivingEntity target) {
		this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.explode")), 1.5f, 0.8f);
		performLandingDamage();
		if (!this.level().isClientSide()) {
			spawnLandingParticles();
			causeScreenShakeToNearbyPlayers();
		}
	}

	private void spawnDashParticles() {
		if (!(this.level() instanceof ServerLevel serverLevel))
			return;
		serverLevel.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY() + 1.0, this.getZ(), 15, 0.5, 0.2, 0.5, 0.1);
		serverLevel.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 10, 0.3, 0.1, 0.3, 0.05);
	}

	private void spawnJumpParticles() {
		if (!(this.level() instanceof ServerLevel serverLevel))
			return;
		serverLevel.sendParticles(ParticleTypes.LAVA, this.getX(), this.getY(), this.getZ(), 40, 2.0, 0.3, 2.0, 0.1);
		serverLevel.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 30, 1.5, 0.5, 1.5, 0.05);
	}

	private void spawnLandingParticles() {
		if (!(this.level() instanceof ServerLevel serverLevel))
			return;
		serverLevel.sendParticles(ParticleTypes.LAVA, this.getX(), this.getY() + 0.5, this.getZ(), 150, 4.0, 0.5, 4.0, 0.15);
		serverLevel.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY() + 1, this.getZ(), 80, 5.0, 1.0, 5.0, 0.1);
		serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX(), this.getY() + 2, this.getZ(), 50, 3.0, 2.0, 3.0, 0.05);
	}

	private void causeScreenShakeToNearbyPlayers() {
		if (this.level().isClientSide())
			return;
		ServerLevel serverLevel = (ServerLevel) this.level();
		List<ServerPlayer> nearbyPlayers = serverLevel.getPlayers(player -> player.distanceToSqr(this) < 500);
		for (ServerPlayer player : nearbyPlayers) {
			double distance = Math.sqrt(player.distanceToSqr(this));
			float intensity = (float) Math.max(0.15, 1.2 - (distance / 18.0));
			player.knockback(0.15 * intensity, player.getX() - this.getX(), player.getZ() - this.getZ());
			if (distance < 12) {
				player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 15, 3, false, false));
			}
		}
	}

	private void performLandingDamage() {
		List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5.0D));
		for (LivingEntity entity : list) {
			if (entity == this)
				continue;
			double dist = this.distanceTo(entity);
			if (dist < 5.0) {
				float damage = 15.0f * (1.0f - (float) dist / 5.0f);
				entity.hurt(this.damageSources().mobAttack(this), damage);
				entity.addEffect(new MobEffectInstance(MorebossesModMobEffects.HELLISH_BURN.get(), 150, 2));
				if (dist > 0) {
					Vec3 awayDir = entity.position().subtract(this.position()).normalize();
					double force = 2.0 * (1.0 - dist / 5.0);
					entity.setDeltaMovement(awayDir.x * force, Math.min(1.0, force * 0.6), awayDir.z * force);
					entity.hurtMarked = true;
				}
			}
		}
	}

	private void updateAnimation(String animName) {
		if (!this.level().isClientSide()) {
			this.animationprocedure = animName;
			this.setAnimation(animName);
			this.level().broadcastEntityEvent(this, (byte) 1);
		}
	}

	// ==================================================================================================
	// NOVOS MÉTODOS DE DANO
	// ==================================================================================================
	private void performConeSlash(boolean left) {
		double coneAngle = 90.0;
		double coneLength = 4.0;
		float damage = 10.0f;
		List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(coneLength));
		Vec3 lookDir = this.getLookAngle();
		Vec3 bossPos = this.position();
		for (LivingEntity entity : entities) {
			if (entity == this)
				continue;
			Vec3 targetPos = entity.position();
			double distance = bossPos.distanceTo(targetPos);
			if (distance > coneLength)
				continue;
			// Verifica linha de visão primeiro (não atravessa blocos)
			if (!hasLineOfSightTo(entity)) {
				continue;
			}
			Vec3 toEntity = targetPos.subtract(bossPos).normalize();
			// Calcula o ângulo entre a direção do olhar e a direção da entidade
			double dot = lookDir.dot(toEntity);
			// Garantir que o valor está entre -1 e 1 para o acos
			dot = Math.min(1.0, Math.max(-1.0, dot));
			double angle = Math.toDegrees(Math.acos(dot));
			// Verifica se está dentro do cone (incluindo bordas)
			if (angle <= coneAngle / 2) {
				// Dano cheio para entidades dentro do cone
				entity.hurt(this.damageSources().mobAttack(this), damage);
				entity.addEffect(new MobEffectInstance(MorebossesModMobEffects.HELLISH_BURN.get(), 150, 1));
				// Knockback radial
				Vec3 knockbackDir = targetPos.subtract(bossPos).normalize();
				entity.setDeltaMovement(knockbackDir.scale(0.8));
				entity.hurtMarked = true;
			}
			// Verifica se está nas bordas do cone
			else if (angle <= coneAngle / 2 + 15.0) {
				// Dano reduzido para bordas
				entity.hurt(this.damageSources().mobAttack(this), damage * 0.6f);
				entity.addEffect(new MobEffectInstance(MorebossesModMobEffects.HELLISH_BURN.get(), 100, 0));
			}
		}
		// Spawn de partículas do cone (apenas visuais, sem dano)
		if (!this.level().isClientSide()) {
			spawnConeParticles(lookDir, coneLength, coneAngle);
		}
	}

	private void spawnConeParticles(Vec3 direction, double length, double angle) {
		if (!(this.level() instanceof ServerLevel serverLevel))
			return;
		for (int i = 0; i < 15; i++) {
			double randomAngle = (Math.random() - 0.5) * Math.toRadians(angle);
			double randomLength = Math.random() * length;
			double rotatedX = direction.x * Math.cos(randomAngle) - direction.z * Math.sin(randomAngle);
			double rotatedZ = direction.x * Math.sin(randomAngle) + direction.z * Math.cos(randomAngle);
			Vec3 particlePos = this.position().add(rotatedX * randomLength, 1.0, rotatedZ * randomLength);
			// Verifica se a posição da partícula está em um bloco válido
			BlockPos blockPos = BlockPos.containing(particlePos);
			if (this.level().getBlockState(blockPos).isAir()) {
				serverLevel.sendParticles(ParticleTypes.FLAME, particlePos.x, particlePos.y, particlePos.z, 1, 0.1, 0.1, 0.1, 0.01);
			}
		}
	}

	private void performSlamLineDamage() {
		double lineLength = 8.0;
		double lineWidth = 2.0;
		Vec3 lookDir = this.getLookAngle();
		Vec3 bossPos = this.position();
		List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(lineLength));
		for (LivingEntity entity : entities) {
			if (entity == this)
				continue;
			Vec3 targetPos = entity.position();
			double distance = bossPos.distanceTo(targetPos);
			if (distance > lineLength)
				continue;
			// Verifica linha de visão
			if (!hasLineOfSightTo(entity)) {
				continue;
			}
			Vec3 toEntity = targetPos.subtract(bossPos);
			// Projeta a posição da entidade na direção do olhar
			double projection = toEntity.dot(lookDir);
			if (projection > 0 && projection < lineLength) {
				// Calcula a distância perpendicular à linha
				Vec3 perpendicular = toEntity.subtract(lookDir.scale(projection));
				double perpDistance = perpendicular.length();
				// Se estiver dentro da largura da linha ou nas bordas
				if (perpDistance <= lineWidth) {
					float damage = 15.0f * (1.0f - (float) projection / (float) lineLength);
					// Dano principal na linha
					if (perpDistance <= lineWidth / 2) {
						entity.hurt(this.damageSources().mobAttack(this), damage);
						entity.addEffect(new MobEffectInstance(MorebossesModMobEffects.HELLISH_BURN.get(), 200, 2));
						// Knockback para longe na direção da linha
						entity.setDeltaMovement(lookDir.scale(1.5).add(0, 0.5, 0));
						entity.hurtMarked = true;
					}
					// Dano reduzido nas bordas
					else {
						entity.hurt(this.damageSources().mobAttack(this), damage * 0.5f);
						entity.addEffect(new MobEffectInstance(MorebossesModMobEffects.HELLISH_BURN.get(), 100, 1));
					}
				}
			}
		}
		// Spawn de partículas da linha
		if (!this.level().isClientSide()) {
			spawnLineParticles(lookDir, lineLength, lineWidth);
		}
	}

	private void spawnLineParticles(Vec3 direction, double length, double width) {
		if (!(this.level() instanceof ServerLevel serverLevel))
			return;
		for (int i = 0; i < 20; i++) {
			double progress = Math.random() * length;
			double offset = (Math.random() - 0.5) * width;
			Vec3 right = new Vec3(-direction.z, 0, direction.x).normalize();
			Vec3 particlePos = this.position().add(direction.scale(progress)).add(right.scale(offset));
			// Verifica se a posição da partícula está em um bloco válido
			BlockPos blockPos = BlockPos.containing(particlePos);
			if (this.level().getBlockState(blockPos).isAir()) {
				serverLevel.sendParticles(ParticleTypes.LAVA, particlePos.x, particlePos.y + 1.0, particlePos.z, 2, 0.2, 0.1, 0.2, 0.05);
			}
		}
	}

	// Método melhorado para verificar linha de visão
	private boolean hasLineOfSightTo(LivingEntity target) {
		Vec3 start = this.getEyePosition();
		Vec3 end = target.getEyePosition();
		ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this);
		BlockHitResult result = this.level().clip(context);
		// Se não acertou nada, tem visão livre
		if (result.getType() == HitResult.Type.MISS) {
			return true;
		}
		// Verifica se o bloco atingido é o mesmo onde o alvo está
		BlockPos hitPos = result.getBlockPos();
		BlockPos targetPos = target.blockPosition();
		// Se o bloco atingido está próximo do alvo, considera que tem visão
		if (hitPos.distSqr(targetPos) < 4.0) {
			return true;
		}
		return false;
	}

	// ==================================================================================================
	// MÉTODOS DE MAGMA PITS
	// ==================================================================================================
	private void spawnMagmaPitsInLine() {
		if (this.level().isClientSide())
			return;
		Vec3 start = this.position();
		Vec3 lookDir = this.getLookAngle();
		int lineLength = 5;
		double spacing = 2.5;
		for (int i = 2; i <= lineLength; i++) {
			Vec3 pitPos = start.add(lookDir.x * i * spacing, 0, lookDir.z * i * spacing);
			int groundY = findGroundY(this.level(), (int) Math.floor(pitPos.x), (int) Math.floor(pitPos.z), (int) Math.floor(this.getY()));
			if (groundY <= this.level().getMinBuildHeight() + 1) {
				groundY = (int) Math.floor(this.getY());
			}
			// Verifica se a posição é válida antes de spawnar
			BlockPos blockPos = new BlockPos((int) pitPos.x, groundY, (int) pitPos.z);
			if (this.level().getBlockState(blockPos).isAir() || this.level().getBlockState(blockPos).canBeReplaced()) {
				Entity pit = MorebossesModEntities.MAGMA_PIT.get().create(this.level());
				if (pit != null) {
					pit.moveTo(pitPos.x, groundY, pitPos.z, 0, 0);
					this.level().addFreshEntity(pit);
				}
			}
			// Magma pits laterais apenas em alguns pontos
			if (i == 3 || i == 5) {
				spawnLateralMagmaPit(start, lookDir, i, spacing, true); // Esquerda
				spawnLateralMagmaPit(start, lookDir, i, spacing, false); // Direita
			}
		}
	}

	private void spawnLateralMagmaPit(Vec3 start, Vec3 lookDir, int index, double spacing, boolean isLeft) {
		double lateralOffset = isLeft ? -1.5 : 1.5;
		Vec3 pitPos = start.add(lookDir.x * index * spacing - lookDir.z * lateralOffset, 0, lookDir.z * index * spacing + lookDir.x * lateralOffset);
		int groundY = findGroundY(this.level(), (int) Math.floor(pitPos.x), (int) Math.floor(pitPos.z), (int) Math.floor(this.getY()));
		if (groundY <= this.level().getMinBuildHeight() + 1) {
			groundY = (int) Math.floor(this.getY());
		}
		// Verifica se a posição é válida
		BlockPos blockPos = new BlockPos((int) pitPos.x, groundY, (int) pitPos.z);
		if (this.level().getBlockState(blockPos).isAir() || this.level().getBlockState(blockPos).canBeReplaced()) {
			Entity pit = MorebossesModEntities.MAGMA_PIT.get().create(this.level());
			if (pit != null) {
				pit.moveTo(pitPos.x, groundY, pitPos.z, 0, 0);
				this.level().addFreshEntity(pit);
			}
		}
	}

	private void performRevengeAttack() {
		LivingEntity target = this.getTarget();
		if (target != null && this.distanceTo(target) < 6) {
			target.hurt(this.damageSources().mobAttack(this), 18f);
			double dx = target.getX() - this.getX();
			double dz = target.getZ() - this.getZ();
			target.knockback(3.0F, -dx, -dz);
			if (target instanceof Player player) {
				player.getCooldowns().addCooldown(player.getUseItem().getItem(), 150);
				player.stopUsingItem();
			}
			target.addEffect(new MobEffectInstance(MorebossesModMobEffects.STUN.get(), 80, 1));
			if (!this.level().isClientSide()) {
				((ServerLevel) this.level()).sendParticles(ParticleTypes.SOUL_FIRE_FLAME, target.getX(), target.getY() + 1, target.getZ(), 30, 0.5, 0.5, 0.5, 0.2);
			}
		}
	}

	// ==================================================================================================
	// DATAS E SERIALIZAÇÃO
	// ==================================================================================================
	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putBoolean("IsDying", this.isDying);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("IsDying"))
			this.isDying = compound.getBoolean("IsDying");
	}

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
		builder = builder.add(Attributes.MAX_HEALTH, 500);
		builder = builder.add(Attributes.ARMOR, 35);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 15);
		builder = builder.add(Attributes.FOLLOW_RANGE, 64);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
		builder = builder.add(Attributes.ATTACK_KNOCKBACK, 0.3);
		return builder;
	}

	// ==================================================================================================
	// GECKOLIB CONTROLLERS - ANIMAÇÃO DE ANDAR FUNCIONANDO
	// ==================================================================================================
	private PlayState movementPredicate(AnimationState event) {
		// Se estiver executando uma animação de ataque, para a animação de movimento
		if (!this.animationprocedure.equals("empty") && !this.animationprocedure.equals("undefined")) {
			return PlayState.STOP;
		}
		// Se estiver morrendo, toca animação de morte
		if (this.isDeadOrDying() || this.isDying) {
			return event.setAndContinue(RawAnimation.begin().thenPlay("death"));
		}
		// Verifica se está se movendo (andando) - isso ativa a animação "walk"
		if (event.isMoving()) {
			return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
		}
		// Se não estiver se movendo, toca idle
		return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
	}

	private PlayState procedurePredicate(AnimationState event) {
		// Toca animações de ataque quando não está vazio
		if (!this.animationprocedure.equals("empty") && !this.animationprocedure.equals("undefined")) {
			RawAnimation animation = RawAnimation.begin().thenPlay(this.animationprocedure);
			return event.setAndContinue(animation);
		}
		return PlayState.STOP;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
		data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	public String getSyncedAnimation() {
		return this.entityData.get(ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(ANIMATION, animation);
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == (byte) 1) {
			if (this.level().isClientSide()) {
				String anim = this.entityData.get(ANIMATION);
				if (!anim.equals("undefined")) {
					this.animationprocedure = anim;
				}
			}
		} else if (id == (byte) 2) {
			if (this.level().isClientSide()) {
				this.animationprocedure = "death";
			}
		}
		super.handleEntityEvent(id);
	}
}
