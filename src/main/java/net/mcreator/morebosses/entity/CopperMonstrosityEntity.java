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

import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.DifficultyInstance;
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
import net.minecraft.core.BlockPos;

import net.mcreator.morebosses.procedures.MonstruosidadeQuebraBlocosProcedure;
import net.mcreator.morebosses.procedures.AnimaMonstruosidadeGerarProcedure;
import net.mcreator.morebosses.init.MorebossesModEntities;
import net.mcreator.morebosses.WaveEffect;

import javax.annotation.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class CopperMonstrosityEntity extends Monster implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(CopperMonstrosityEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(CopperMonstrosityEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(CopperMonstrosityEntity.class, EntityDataSerializers.STRING);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private int slamInvulnerableTicks = 0;
	private int slamCooldown = 0;
	private int rangedCooldown = 0;
	private boolean useSlamNext = false;
	private boolean useRangedNext = false;
	private long lastSwing;
	public String animationprocedure = "empty";
	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.BLUE, ServerBossEvent.BossBarOverlay.PROGRESS);
	// Timer para sincronizar efeitos do slam
	private int slamEffectTimer = 0;
	private int rangedEffectTimer = 0;
	private static final int SLAM_COOLDOWN_TIME = 100; // 5 segundos
	private static final int RANGED_COOLDOWN_TIME = 80; // 4 segundos
	private static final double MOVEMENT_THRESHOLD = 1.0E-6D;
	private boolean isPerformingSlam = false;
	private boolean isPerformingRanged = false;

	public CopperMonstrosityEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(MorebossesModEntities.COPPER_MONSTROSITY.get(), world);
	}

	public CopperMonstrosityEntity(EntityType<CopperMonstrosityEntity> type, Level world) {
		super(type, world);
		xpReward = 35;
		setNoAi(false);
		setMaxUpStep(1f);
		setPersistenceRequired();
	}

	@Override
	protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
		return 5F;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "copper_monstrosity");
	}

	public void setTexture(String texture) {
		this.entityData.set(TEXTURE, texture);
	}

	public String getTexture() {
		return this.entityData.get(TEXTURE);
	}

	@Override
	public boolean canCollideWith(Entity entity) {
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
		Entity entity = this;
		Level world = entity.level();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		return false;
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true, false));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, DryBonesEntity.class, true, false));
		this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
			}

			@Override
			public boolean canUse() {
				return super.canUse() && !isPerformingSlam && !isPerformingRanged;
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && !isPerformingSlam && !isPerformingRanged;
			}
		});
		this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
		this.targetSelector.addGoal(5, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(7, new FloatGoal(this));
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEFINED;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
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
		// Invulnerável durante o slam
		if (slamInvulnerableTicks > 0) {
			return false;
		}
		if (source.is(DamageTypes.FALL))
			return false;
		if (source.is(DamageTypes.EXPLOSION))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		AnimaMonstruosidadeGerarProcedure.execute(this);
		return retval;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putInt("SlamCooldown", this.slamCooldown);
		compound.putInt("RangedCooldown", this.rangedCooldown);
		compound.putBoolean("UseSlamNext", this.useSlamNext);
		compound.putBoolean("UseRangedNext", this.useRangedNext);
		compound.putBoolean("IsPerformingSlam", this.isPerformingSlam);
		compound.putBoolean("IsPerformingRanged", this.isPerformingRanged);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("SlamCooldown"))
			this.slamCooldown = compound.getInt("SlamCooldown");
		if (compound.contains("RangedCooldown"))
			this.rangedCooldown = compound.getInt("RangedCooldown");
		if (compound.contains("UseSlamNext"))
			this.useSlamNext = compound.getBoolean("UseSlamNext");
		if (compound.contains("UseRangedNext"))
			this.useRangedNext = compound.getBoolean("UseRangedNext");
		if (compound.contains("IsPerformingSlam"))
			this.isPerformingSlam = compound.getBoolean("IsPerformingSlam");
		if (compound.contains("IsPerformingRanged"))
			this.isPerformingRanged = compound.getBoolean("IsPerformingRanged");
	}

	@Override
	public void baseTick() {
		super.baseTick();
		MonstruosidadeQuebraBlocosProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
		this.refreshDimensions();
		if (!this.level().isClientSide()) {
			// Atualiza cooldowns
			if (slamCooldown > 0) {
				slamCooldown--;
			}
			if (rangedCooldown > 0) {
				rangedCooldown--;
			}
			// Timer de invulnerabilidade do slam
			if (slamInvulnerableTicks > 0) {
				slamInvulnerableTicks--;
			}
			// Controla timer dos efeitos do slam
			if (slamEffectTimer > 0) {
				slamEffectTimer--;
				if (slamEffectTimer == 10 && this.animationprocedure.equals("slam")) {
					executeSlamEffects();
				}
				if (slamEffectTimer == 0) {
					this.animationprocedure = "empty";
					this.isPerformingSlam = false;
				}
			}
			// Controla timer do ataque ranged
			if (rangedEffectTimer > 0) {
				rangedEffectTimer--;
				if (rangedEffectTimer == 15 && this.animationprocedure.equals("shot")) {
					executeRangedAttack();
				}
				if (rangedEffectTimer == 0) {
					this.animationprocedure = "empty";
					this.isPerformingRanged = false;
				}
			}
		}
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 1);
	}

	@Override
	public boolean canChangeDimensions() {
		return false;
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
		// Verifica se deve usar ataque ranged
		if (!this.level().isClientSide() && this.getTarget() != null && rangedCooldown <= 0 && !isPerformingSlam && !isPerformingRanged) {
			LivingEntity target = this.getTarget();
			double distance = this.distanceToSqr(target);
			// Se o alvo está longe (mais de 8 blocos) e tem linha de visão
			if (distance > 64.0 && distance < 256.0 && this.hasLineOfSight(target)) {
				// 40% de chance de usar ranged se estiver no cooldown
				if (ThreadLocalRandom.current().nextDouble() < 0.4) {
					performRangedAttack();
				}
			}
		}
	}

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25);
		builder = builder.add(Attributes.MAX_HEALTH, 800);
		builder = builder.add(Attributes.ARMOR, 50);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 20);
		builder = builder.add(Attributes.FOLLOW_RANGE, 64);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 100);
		builder = builder.add(Attributes.ATTACK_KNOCKBACK, 1);
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
		if (this.deathTime == 40) {
			this.remove(CopperMonstrosityEntity.RemovalReason.KILLED);
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
	public boolean doHurtTarget(Entity target) {
		if (!(target instanceof LivingEntity living))
			return super.doHurtTarget(target);
		// Verifica se pode usar ataque ranged
		if (useRangedNext && rangedCooldown <= 0 && this.distanceToSqr(target) > 16.0) {
			performRangedAttack();
			return true;
		}
		// ================= ATAQUE SLAM =================
		if (useSlamNext && slamCooldown <= 0 && this.getDeltaMovement().horizontalDistanceSqr() > MOVEMENT_THRESHOLD) {
			this.animationprocedure = "slam";
			this.setAnimation("slam");
			this.isPerformingSlam = true;
			// Inicia timer para efeitos (20 ticks = 1 segundo)
			slamEffectTimer = 20;
			// Aplica RESISTÊNCIA MÁXIMA (255) por 40 ticks para não tomar dano do próprio slam
			this.slamInvulnerableTicks = 40;
			// Aplica efeito HEAVY DURANTE o slam (40 ticks também)
			net.minecraft.world.effect.MobEffect heavyEffect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("morebosses", "heavy"));
			if (heavyEffect != null) {
				this.addEffect(new net.minecraft.world.effect.MobEffectInstance(heavyEffect, 40, // 2 segundos - mesma duração da resistência
						0, // Nível 0 (I)
						false, // Não é ambiental
						true // Mostra partículas
				));
			} else {
				// Fallback: Slowness nível III
				this.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 40, // 2 segundos
						2, // Nível III
						false, // Não é ambiental
						true // Mostra partículas
				));
			}
			// Toca som de preparação (com verificação de null)
			SoundEvent preparationSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.iron_golem.attack"));
			if (preparationSound != null) {
				this.playSound(preparationSound, 1.5f, 0.8f);
			}
			// Cooldown de 5 segundos
			slamCooldown = SLAM_COOLDOWN_TIME;
			// Alterna pro ataque normal
			useSlamNext = false;
			return true;
		}
		// ================= ATAQUE NORMAL =================
		this.animationprocedure = "attack";
		this.setAnimation("attack");
		// Aplica dano normal
		float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
		boolean hit = living.hurt(this.damageSources().mobAttack(this), damage);
		if (hit) {
			// Aplica knockback
			living.knockback(0.5, living.getX() - this.getX(), living.getZ() - this.getZ());
			// Toca som (usando um som válido do Minecraft)
			SoundEvent attackSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.iron_golem.attack")); // Som válido
			if (attackSound != null) {
				this.playSound(attackSound, 1.0f, 0.7f);
			}
		}
		// Próxima vez tenta slam (30% de chance) ou ranged (20% de chance)
		double rand = ThreadLocalRandom.current().nextDouble();
		if (rand < 0.3) {
			useSlamNext = true;
			useRangedNext = false;
		} else if (rand < 0.5) {
			useRangedNext = true;
			useSlamNext = false;
		} else {
			useSlamNext = false;
			useRangedNext = false;
		}
		return hit;
	}

	// Método para executar ataque ranged
	private void performRangedAttack() {
		this.animationprocedure = "shot";
		this.setAnimation("shot");
		this.isPerformingRanged = true;
		// Inicia timer para disparar os projéteis (25 ticks)
		rangedEffectTimer = 25;
		// Aplica efeito HEAVY para ficar parado durante o ataque
		net.minecraft.world.effect.MobEffect heavyEffect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("morebosses", "heavy"));
		if (heavyEffect != null) {
			this.addEffect(new net.minecraft.world.effect.MobEffectInstance(heavyEffect, 25, // mesma duração da animação
					0, // Nível 0 (I)
					false, // Não é ambiental
					true // Mostra partículas
			));
		} else {
			// Fallback: Slowness nível IV (mais forte para imobilizar)
			this.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 25, 3, // Nível IV
					false, true));
		}
		// Toca som de preparação
		SoundEvent windSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.trident.throw"));
		if (windSound != null) {
			this.playSound(windSound, 1.0f, 0.5f);
		}
		// Cooldown de 4 segundos
		rangedCooldown = RANGED_COOLDOWN_TIME;
		useRangedNext = false;
	}

	// Método para executar o disparo dos projéteis
	private void executeRangedAttack() {
		LivingEntity target = this.getTarget();
		if (target == null)
			return;
		// Toca som de disparo
		SoundEvent shootSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.witch.throw"));
		if (shootSound != null) {
			this.playSound(shootSound, 1.0f, 0.8f);
		}
		// Calcula direção para o alvo
		double dx = target.getX() - this.getX();
		double dy = target.getY() - this.getY();
		double dz = target.getZ() - this.getZ();
		double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
		if (distance > 0) {
			dx = dx / distance;
			dy = dy / distance;
			dz = dz / distance;
		}
		// Posição de spawn dos projéteis (MUITO mais na frente da monstruosidade)
		double spawnX = this.getX() + this.getLookAngle().x * 4.0; // Aumentado de 2.0 para 4.0
		double spawnY = this.getY() + 4.0; // Aumentado de 3.0 para 4.0 (mais alto)
		double spawnZ = this.getZ() + this.getLookAngle().z * 4.0; // Aumentado de 2.0 para 4.0
		// Dispara 2 projéteis com pequenas variações
		for (int i = 0; i < 2; i++) {
			// Cria o projétil WindBurst
			WindBurstEntity projectile = new WindBurstEntity(MorebossesModEntities.WIND_BURST.get(), this.level());
			projectile.setOwner(this);
			projectile.setPos(spawnX, spawnY, spawnZ);
			// Adiciona uma leve tendência para o alvo com pequena variação
			double variationX = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.2;
			double variationY = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.1;
			double variationZ = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.2;
			// 80% de precisão na direção do alvo, 20% de variação
			double finalDx = dx * 0.8 + variationX;
			double finalDy = dy * 0.8 + variationY;
			double finalDz = dz * 0.8 + variationZ;
			// Normaliza o vetor
			double finalDistance = Math.sqrt(finalDx * finalDx + finalDy * finalDy + finalDz * finalDz);
			if (finalDistance > 0) {
				finalDx = finalDx / finalDistance;
				finalDy = finalDy / finalDistance;
				finalDz = finalDz / finalDistance;
			}
			// Define velocidade do projétil (aumentada um pouco)
			projectile.shoot(finalDx, finalDy, finalDz, 1.5F, 2.0F); // Aumentado de 1.2F para 1.5F
			// Adiciona ao mundo
			this.level().addFreshEntity(projectile);
			// Pequeno delay entre os projéteis
			try {
				Thread.sleep(10); // 10ms de delay
			} catch (InterruptedException e) {
				// Ignora
			}
		}
	}

	// Método para executar efeitos do slam (chamado durante a animação)
	private void executeSlamEffects() {
		// Toca som de explosão (com verificação de null)
		SoundEvent explosionSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.explode"));
		if (explosionSound != null) {
			this.playSound(explosionSound, 1.0f, 1.6f);
		}
		// Cria onda de choque (com proteção contra null) - DANO REDUZIDO PARA 12
		try {
			BlockPos center = new BlockPos((int) this.getX(), (int) this.getY() - 1, (int) this.getZ());
			WaveEffect.createShockwave(this.level(), center, 8, 12); // DANO: 20 → 12
		} catch (Exception e) {
			System.err.println("Erro ao criar onda de choque: " + e.getMessage());
		}
		double radius = 4.0;
		for (Player player : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(radius, 3.0, radius))) {
			if (player.isBlocking()) {
				// Desarma escudo
				player.disableShield(true);
				// Cooldown do escudo (2s)
				player.getCooldowns().addCooldown(player.getUseItem().getItem(), 40);
				// Aplica PANIC
				var panicEffect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("morebosses", "panic"));
				if (panicEffect != null) {
					player.addEffect(new net.minecraft.world.effect.MobEffectInstance(panicEffect, 60, // duração: 3 segundos
							0, // nível I
							false, true));
				}
			}
		}
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
