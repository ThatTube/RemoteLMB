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
import net.minecraftforge.common.ForgeMod;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
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

import net.mcreator.morebosses.procedures.MaxoloteQuebraBlocosProcedure;
import net.mcreator.morebosses.procedures.MaxolotDeathTimeIsReachedProcedure;
import net.mcreator.morebosses.init.MorebossesModMobEffects;
import net.mcreator.morebosses.init.MorebossesModEntities;

import javax.annotation.Nullable;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public class MaxolotEntity extends Monster implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(MaxolotEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(MaxolotEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(MaxolotEntity.class, EntityDataSerializers.STRING);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";
	@Nullable
	private ServerBossEvent bossInfo;
	// Variáveis para controle dos ataques
	private int attackCounter = 0;
	private int attackCooldown = 0;
	private static final int ATTACK_DELAY = 40; // 2 segundos entre ataques especiais
	private final List<UUID> tamedMinilotls = new ArrayList<>();

	public MaxolotEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(MorebossesModEntities.MAXOLOT.get(), world);
	}

	public MaxolotEntity(EntityType<? extends MaxolotEntity> type, Level world) {
		super(type, world);
		xpReward = 34;
		setNoAi(false);
		setMaxUpStep(0.6f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "maxolot");
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
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.5, true) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth() + 2.0;
			}

			@Override
			protected void checkAndPerformAttack(LivingEntity target, double range) {
				double d0 = this.getAttackReachSqr(target);
				if (range <= d0 && this.getTicksUntilNextAttack() <= 0) {
					this.resetAttackCooldown();
					// Causa dano base
					this.mob.doHurtTarget(target);
					// Sistema de ataques alternados
					if (attackCooldown <= 0) {
						performSpecialAttack(target);
						attackCooldown = ATTACK_DELAY;
					}
				}
			}
		});
		this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1));
		this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1, 40));
		this.targetSelector.addGoal(5, new HurtByTargetGoal(this).setAlertOthers());
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
	}

	// Método para ataques especiais
	private void performSpecialAttack(LivingEntity target) {
		// Primeiro ataque: normal (animação attack)
		if (attackCounter == 0) {
			triggerAnimation("attack");
			attackCounter = 1;
		}
		// Segundo ataque: soco desarmador (animação punch)
		else if (attackCounter == 1) {
			triggerAnimation("punch");
			performDisarmAttack(target);
			attackCounter = 2;
		}
		// Terceiro ataque: invocação (animação summon)
		else {
			triggerAnimation("summon");
			performSummonAttack();
			attackCounter = 0;
		}
		// Marca como atacando para animação
		this.swinging = true;
		this.lastSwing = this.level().getGameTime();
	}

	// Método para ataque desarmador
	private void performDisarmAttack(LivingEntity target) {
		if (target instanceof Player player) {
			if (player.isBlocking()) {
				// Desarma o escudo por 4 segundos (80 ticks)
				player.disableShield(true);
				// Aplica efeito panic (5 segundos = 100 ticks)
				target.addEffect(new MobEffectInstance(MorebossesModMobEffects.PANIC.get(), 100, 0));
			}
		}
	}

	// Método para invocação
	private void performSummonAttack() {
		Level world = this.level();
		if (!world.isClientSide()) {
			try {
				double radius = 3.0;
				for (int i = 0; i < 4; i++) {
					double angle = (Math.PI * 2 / 4) * i;
					double spawnX = this.getX() + Math.cos(angle) * radius;
					double spawnY = this.getY() + 0.5;
					double spawnZ = this.getZ() + Math.sin(angle) * radius;
					// Verifica se tem espaço
					BlockPos spawnPos = new BlockPos((int) spawnX, (int) spawnY, (int) spawnZ);
					if (world.getBlockState(spawnPos).isAir() || world.getBlockState(spawnPos).canBeReplaced()) {
						// Cria o Minilotl
						var minilotl = MorebossesModEntities.MINILOTL.get().create(world);
						if (minilotl != null) {
							minilotl.moveTo(spawnX, spawnY, spawnZ, this.getYRot(), 0);
							// Define o mesmo alvo do Maxolotl
							LivingEntity target = this.getTarget();
							if (target != null) {
								minilotl.setTarget(target);
							}
							world.addFreshEntity(minilotl);
							// Registra como domado
							tamedMinilotls.add(minilotl.getUUID());
						}
					}
				}
			} catch (Exception e) {
				// Log ou tratamento de erro
				System.err.println("Erro ao invocar Minilotls: " + e.getMessage());
			}
		}
	}

	// Limpa Minilotls mortos da lista
	private void cleanDeadMinilotls() {
		if (!this.level().isClientSide()) {
			List<UUID> toRemove = new ArrayList<>();
			for (UUID minilotlId : tamedMinilotls) {
				boolean found = false;
				// Busca todas as entidades Minilotl na área
				var minilotlType = MorebossesModEntities.MINILOTL.get();
				if (minilotlType != null) {
					// Busca por entidades do tipo Minilotl em uma área grande
					for (var entity : this.level().getEntitiesOfClass(minilotlType.getBaseClass(), this.getBoundingBox().inflate(100))) {
						if (entity.getUUID().equals(minilotlId) && entity.isAlive()) {
							found = true;
							break;
						}
					}
				}
				if (!found) {
					toRemove.add(minilotlId);
				}
			}
			tamedMinilotls.removeAll(toRemove);
		}
	}

	// Método para ativar animação
	private void triggerAnimation(String animation) {
		this.animationprocedure = animation;
		this.entityData.set(ANIMATION, animation);
	}

	@Override
	public MobType getMobType() {
		return MobType.WATER;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public SoundEvent getAmbientSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.axolotl.idle_air"));
	}

	@Override
	public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.axolotl.swim")), 0.15f, 1);
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.axolotl.hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.axolotl.death"));
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.getDirectEntity() instanceof AbstractArrow)
			return false;
		if (source.is(DamageTypes.FALL))
			return false;
		if (source.is(DamageTypes.DROWN))
			return false;
		// Quando é atacado, os Minilotls atacam o agressor
		if (!this.level().isClientSide() && source.getEntity() instanceof LivingEntity attacker) {
			for (UUID minilotlId : tamedMinilotls) {
				// Busca todas as entidades Minilotl na área
				var minilotlType = MorebossesModEntities.MINILOTL.get();
				if (minilotlType != null) {
					for (var entity : this.level().getEntitiesOfClass(minilotlType.getBaseClass(), this.getBoundingBox().inflate(50))) {
						if (entity.getUUID().equals(minilotlId) && entity.isAlive() && entity instanceof LivingEntity livingEntity) {
							livingEntity.setLastHurtByMob(attacker);
							if (entity instanceof Monster monster) {
								monster.setTarget(attacker);
							}
						}
					}
				}
			}
		}
		return super.hurt(source, amount);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putInt("AttackCounter", this.attackCounter);
		compound.putInt("AttackCooldown", this.attackCooldown);
		// Salva Minilotls domados
		CompoundTag minilotlsTag = new CompoundTag();
		int i = 0;
		for (UUID uuid : tamedMinilotls) {
			minilotlsTag.putUUID("Minilotl_" + i, uuid);
			i++;
		}
		minilotlsTag.putInt("Count", i);
		compound.put("TamedMinilotls", minilotlsTag);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("AttackCounter"))
			this.attackCounter = compound.getInt("AttackCounter");
		if (compound.contains("AttackCooldown"))
			this.attackCooldown = compound.getInt("AttackCooldown");
		// Carrega Minilotls domados
		tamedMinilotls.clear();
		if (compound.contains("TamedMinilotls")) {
			CompoundTag minilotlsTag = compound.getCompound("TamedMinilotls");
			int count = minilotlsTag.getInt("Count");
			for (int i = 0; i < count; i++) {
				if (minilotlsTag.hasUUID("Minilotl_" + i)) {
					tamedMinilotls.add(minilotlsTag.getUUID("Minilotl_" + i));
				}
			}
		}
	}

	@Override
	public void baseTick() {
		super.baseTick();
		if (!this.level().isClientSide()) {
			MaxoloteQuebraBlocosProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ());
		}
		this.refreshDimensions();
		// Reduz cooldown do ataque
		if (attackCooldown > 0) {
			attackCooldown--;
		}
		// Controla animação de ataque
		if (this.swinging && this.lastSwing + 15L <= level().getGameTime()) {
			this.swinging = false;
			this.animationprocedure = "empty";
		}
		// Limpa Minilotls mortos periodicamente
		if (this.tickCount % 100 == 0) { // A cada 5 segundos
			cleanDeadMinilotls();
		}
		// Atualiza alvos dos Minilotls
		if (!this.level().isClientSide() && this.tickCount % 20 == 0) { // A cada segundo
			LivingEntity target = this.getTarget();
			if (target != null) {
				var minilotlType = MorebossesModEntities.MINILOTL.get();
				if (minilotlType != null) {
					for (var entity : this.level().getEntitiesOfClass(minilotlType.getBaseClass(), this.getBoundingBox().inflate(50))) {
						if (tamedMinilotls.contains(entity.getUUID()) && entity.isAlive() && entity instanceof LivingEntity livingEntity) {
							// Atualiza o alvo do Minilotl
							livingEntity.setLastHurtByMob(target);
							if (entity instanceof Monster monster) {
								monster.setTarget(target);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public EntityDimensions getDimensions(Pose pose) {
		return super.getDimensions(pose).scale((float) 1);
	}

	@Override
	public boolean canChangeDimensions() {
		return false;
	}

	@Override
	public void startSeenByPlayer(ServerPlayer player) {
		super.startSeenByPlayer(player);
		if (this.bossInfo == null) {
			this.bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.PINK, ServerBossEvent.BossBarOverlay.PROGRESS);
		}
		this.bossInfo.addPlayer(player);
	}

	@Override
	public void stopSeenByPlayer(ServerPlayer player) {
		super.stopSeenByPlayer(player);
		if (this.bossInfo != null) {
			this.bossInfo.removePlayer(player);
		}
	}

	@Override
	public void customServerAiStep() {
		super.customServerAiStep();
		if (this.bossInfo != null) {
			this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
		}
	}

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
		builder = builder.add(Attributes.MAX_HEALTH, 600);
		builder = builder.add(Attributes.ARMOR, 35);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 15); // Aumentado para 15
		builder = builder.add(Attributes.FOLLOW_RANGE, 32); // Aumentado para 32 blocos
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1); // Reduzido para poder ser empurrado
		builder = builder.add(Attributes.ATTACK_KNOCKBACK, 1.0); // Aumentado knockback
		builder = builder.add(Attributes.ATTACK_SPEED, 0.8); // Velocidade de ataque
		builder = builder.add(ForgeMod.SWIM_SPEED.get(), 4); // Velocidade na água
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
			if (this.isInWaterOrBubble()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("swim"));
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
		if (this.swinging && this.lastSwing + 15L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			// Usa a animação atual ou padrão
			if (!this.animationprocedure.equals("empty")) {
				return event.setAndContinue(RawAnimation.begin().thenPlay(this.animationprocedure));
			}
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
			// Libera os Minilotls quando morre
			var minilotlType = MorebossesModEntities.MINILOTL.get();
			if (minilotlType != null) {
				for (var entity : this.level().getEntitiesOfClass(minilotlType.getBaseClass(), this.getBoundingBox().inflate(50))) {
					if (tamedMinilotls.contains(entity.getUUID()) && entity.isAlive()) {
						// Limpa o último agressor do Minilotl
						if (entity instanceof LivingEntity livingEntity) {
							livingEntity.setLastHurtByMob(null);
						}
						if (entity instanceof Monster monster) {
							monster.setTarget(null);
						}
					}
				}
			}
			this.remove(MaxolotEntity.RemovalReason.KILLED);
			this.dropExperience();
			if (!this.level().isClientSide()) {
				MaxolotDeathTimeIsReachedProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ());
			}
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
}
