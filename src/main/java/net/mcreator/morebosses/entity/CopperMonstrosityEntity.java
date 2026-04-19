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
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.DifficultyInstance;
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
import net.minecraft.core.BlockPos;

import net.mcreator.morebosses.procedures.MonstruosidadeQuebraBlocosProcedure;
import net.mcreator.morebosses.procedures.AnimaMonstruosidadeGerarProcedure;
import net.mcreator.morebosses.init.MorebossesModEntities;
import net.mcreator.morebosses.WaveEffect;

import javax.annotation.Nullable;

import java.util.concurrent.ThreadLocalRandom;
import java.util.List;

public class CopperMonstrosityEntity extends Monster implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(CopperMonstrosityEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(CopperMonstrosityEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(CopperMonstrosityEntity.class, EntityDataSerializers.STRING);
	// Dados de Estado
	public static final EntityDataAccessor<Boolean> DORMANT = SynchedEntityData.defineId(CopperMonstrosityEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> WAKING_UP = SynchedEntityData.defineId(CopperMonstrosityEntity.class, EntityDataSerializers.BOOLEAN);
	// NOVA VARIÁVEL: Indica se a entidade está atacando corpo a corpo (excluindo ranged)
	public static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(CopperMonstrosityEntity.class, EntityDataSerializers.BOOLEAN);
	
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	// --- Variáveis de Combate e Cooldowns ---
	private int strikeInvulnerableTicks = 0;
	// Cooldowns
	private int strikeCooldown = 0;
	private int rangedCooldown = 0;
	private int areaAttackCooldown = 0;
	private int slapCooldown = 0;
	private int summonCooldown = 0;
	// Flags de Ação
	private boolean isPerformingStrike = false;
	private boolean isPerformingRanged = false;
	private boolean isPerformingAreaAttack = false;
	private boolean isPerformingSlap = false;
	private boolean isPerformingSummon = false;
	private long lastSwing;
	public String animationprocedure = "empty";
	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.BLUE, ServerBossEvent.BossBarOverlay.PROGRESS);
	// Timers de Efeito
	private int strikeEffectTimer = 0;
	private int rangedEffectTimer = 0;
	private int areaAttackEffectTimer = 0;
	private int slapEffectTimer = 0;
	private int summonEffectTimer = 0;
	// Timer para acordar
	private int wakeUpTimer = 0;
	private static final int WAKE_UP_DURATION = 40;
	// Constantes de Tempo
	private static final int STRIKE_COOLDOWN_TIME = 100;
	private static final int RANGED_COOLDOWN_TIME = 80;
	private static final int AREA_COOLDOWN_TIME = 140;
	private static final int SLAP_COOLDOWN_TIME = 60;
	private static final int SUMMON_COOLDOWN_TIME = 400;

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
		this.entityData.define(DORMANT, true);
		this.entityData.define(WAKING_UP, false);
		this.entityData.define(ATTACKING, false); // Inicializa como false
	}

	public void setTexture(String texture) {
		this.entityData.set(TEXTURE, texture);
	}

	public String getTexture() {
		return this.entityData.get(TEXTURE);
	}

	public boolean isDormant() {
		return this.entityData.get(DORMANT);
	}

	public void setDormant(boolean dormant) {
		this.entityData.set(DORMANT, dormant);
	}

	public boolean isWakingUp() {
		return this.entityData.get(WAKING_UP);
	}

	public void setWakingUp(boolean waking) {
		this.entityData.set(WAKING_UP, waking);
	}
	
	// NOVO MÉTODO: Retorna true quando a entidade está atacando corpo a corpo
	public boolean isAttacking() {
		return this.entityData.get(ATTACKING);
	}
	
	// NOVO MÉTODO: Define o estado de ataque corpo a corpo
	public void setAttacking(boolean attacking) {
		this.entityData.set(ATTACKING, attacking);
	}

	public boolean isBusy() {
		return isPerformingStrike || isPerformingRanged || isPerformingAreaAttack || isPerformingSlap || isPerformingSummon || isDormant() || isWakingUp();
	}

	@Override
	public boolean canCollideWith(Entity entity) {
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(0, new HurtByTargetGoal(this).setAlertOthers());
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
		// Exemplo genérico, substitua DryBonesEntity.class se necessário
		// this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, DryBonesEntity.class, true));
		this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return (double) (this.mob.getBbWidth() * 1.5F * this.mob.getBbWidth() * 1.5F + entity.getBbWidth());
			}

			@Override
			public boolean canUse() {
				return super.canUse() && !isBusy();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && !isBusy();
			}
		});
		this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.8));
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
	public boolean isPushable() {
		// Impede que seja empurrado por qualquer coisa quando estiver em certos estados
		if (this.isDormant() || this.isWakingUp() || this.isBusy()) {
			return false;
		}
		return super.isPushable();
	}

	@Override
	public void push(Entity entity) {
		// IMPUNIDADE AO EMPURRÃO DA SHOCKWAVE
		// Se a entidade que está empurrando é uma shockwave ou tem a tag de shockwave
		if (entity != null && entity.getTags().contains("shockwave")) {
			return; // Não é empurrado
		}
		super.push(entity);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		// MODIFICAÇÃO: Se estiver atacando corpo a corpo, o dano é reduzido, não anulado
		if (this.isAttacking()) {
			amount *= 0.5f; // reduz o dano em 50% durante o ataque
			// Continua o processamento normal do dano
		}
		
		var damageTypeKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DAMAGE_TYPE, new ResourceLocation("morebosses", "shock"));
		if (source.is(damageTypeKey)) {
			return false; // Imune ao dano da shockwave
		}
		if (source.is(DamageTypes.LIGHTNING_BOLT))
			return false;
		if (strikeInvulnerableTicks > 0)
			return false;
		if (source.is(DamageTypes.FALL))
			return false;
		if (source.is(DamageTypes.EXPLOSION))
			return false;
		if (this.isDormant()) {
			triggerWakeUp();
		}
		// Logica de panico removida conforme solicitado
		return super.hurt(source, amount);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		AnimaMonstruosidadeGerarProcedure.execute(this);
		this.setDormant(true);
		this.setWakingUp(false);
		return retval;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("StrikeCooldown", this.strikeCooldown);
		compound.putInt("RangedCooldown", this.rangedCooldown);
		compound.putInt("AreaCooldown", this.areaAttackCooldown);
		compound.putInt("SlapCooldown", this.slapCooldown);
		compound.putInt("SummonCooldown", this.summonCooldown);
		compound.putBoolean("Dormant", this.isDormant());
		compound.putBoolean("WakingUp", this.isWakingUp());
		compound.putInt("WakeUpTimer", this.wakeUpTimer);
		compound.putBoolean("Attacking", this.isAttacking()); // Salva o estado
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("StrikeCooldown"))
			this.strikeCooldown = compound.getInt("StrikeCooldown");
		if (compound.contains("RangedCooldown"))
			this.rangedCooldown = compound.getInt("RangedCooldown");
		if (compound.contains("AreaCooldown"))
			this.areaAttackCooldown = compound.getInt("AreaCooldown");
		if (compound.contains("SlapCooldown"))
			this.slapCooldown = compound.getInt("SlapCooldown");
		if (compound.contains("SummonCooldown"))
			this.summonCooldown = compound.getInt("SummonCooldown");
		if (compound.contains("Dormant"))
			this.setDormant(compound.getBoolean("Dormant"));
		if (compound.contains("WakingUp"))
			this.setWakingUp(compound.getBoolean("WakingUp"));
		if (compound.contains("WakeUpTimer"))
			this.wakeUpTimer = compound.getInt("WakeUpTimer");
		if (compound.contains("Attacking"))
			this.setAttacking(compound.getBoolean("Attacking")); // Carrega o estado
	}

	private void triggerWakeUp() {
		if (this.isDormant()) {
			this.setDormant(false);
			this.setWakingUp(true);
			this.wakeUpTimer = WAKE_UP_DURATION;
			SoundEvent wakeSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.emerge"));
			if (wakeSound != null)
				this.playSound(wakeSound, 1.0f, 0.5f);
		}
	}

	@Override
	public void baseTick() {
		super.baseTick();
		MonstruosidadeQuebraBlocosProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
		this.refreshDimensions();
		if (!this.level().isClientSide()) {
			// Decrementar Cooldowns
			if (strikeCooldown > 0)
				strikeCooldown--;
			if (rangedCooldown > 0)
				rangedCooldown--;
			if (areaAttackCooldown > 0)
				areaAttackCooldown--;
			if (slapCooldown > 0)
				slapCooldown--;
			if (summonCooldown > 0)
				summonCooldown--;
			if (strikeInvulnerableTicks > 0)
				strikeInvulnerableTicks--;
			// Gerenciamento de Efeitos
			if (strikeEffectTimer > 0) {
				strikeEffectTimer--;
				if (strikeEffectTimer == 10 && this.animationprocedure.equals("strike"))
					executeStrikeEffects();
				if (strikeEffectTimer == 0) {
					this.animationprocedure = "empty";
					this.isPerformingStrike = false;
					this.setAttacking(false); // Desativa ataque quando termina
				}
			}
			if (rangedEffectTimer > 0) {
				rangedEffectTimer--;
				if (rangedEffectTimer == 15 && this.animationprocedure.equals("shoot"))
					executeRangedAttack();
				if (rangedEffectTimer == 0) {
					this.animationprocedure = "empty";
					this.isPerformingRanged = false;
				}
			}
			if (areaAttackEffectTimer > 0) {
				areaAttackEffectTimer--;
				if (areaAttackEffectTimer == 15 && this.animationprocedure.equals("attackarea"))
					executeAreaAttack();
				if (areaAttackEffectTimer == 0) {
					this.animationprocedure = "empty";
					this.isPerformingAreaAttack = false;
					this.setAttacking(false); // Desativa ataque quando termina
				}
			}
			if (slapEffectTimer > 0) {
				slapEffectTimer--;
				if (slapEffectTimer == 10 && this.animationprocedure.equals("slap"))
					executeSlapAttack();
				if (slapEffectTimer == 0) {
					this.animationprocedure = "empty";
					this.isPerformingSlap = false;
					this.setAttacking(false); // Desativa ataque quando termina
				}
			}
			if (summonEffectTimer > 0) {
				summonEffectTimer--;
				if (summonEffectTimer == 15 && this.animationprocedure.equals("summon"))
					executeSummon();
				if (summonEffectTimer == 0) {
					this.animationprocedure = "empty";
					this.isPerformingSummon = false;
				}
			}
		}
	}

	@Override
	public void customServerAiStep() {
		if (this.isDormant()) {
			this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
			this.getNavigation().stop();
			this.getLookControl().setLookAt(this.getX(), this.getY(), this.getZ());
			boolean trigger = false;
			LivingEntity sleepTarget = this.getTarget();
			if (sleepTarget != null && this.distanceToSqr(sleepTarget) <= 225.0D)
				trigger = true;
			else {
				Player nearestPlayer = this.level().getNearestPlayer(this, 15.0D);
				if (nearestPlayer != null && !nearestPlayer.isCreative() && !nearestPlayer.isSpectator())
					trigger = true;
			}
			if (trigger)
				triggerWakeUp();
			this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
			return;
		}
		if (this.isWakingUp()) {
			this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
			this.getNavigation().stop();
			this.wakeUpTimer--;
			if (this.wakeUpTimer <= 0) {
				this.setWakingUp(false);
				if (!this.level().isClientSide()) {
					for (Player player : this.level().players()) {
						if (player instanceof ServerPlayer serverPlayer && serverPlayer.hasLineOfSight(this)) {
							this.bossInfo.addPlayer(serverPlayer);
						}
					}
				}
			}
			this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
			return;
		}
		if (this.isBusy() && !this.isDormant() && !this.isWakingUp()) {
			this.getNavigation().stop();
			if (this.getTarget() != null) {
				this.getLookControl().setLookAt(this.getTarget(), 100.0F, 100.0F);
				this.setYBodyRot(this.yHeadRot);
				this.yBodyRot = this.yHeadRot;
			}
		}
		super.customServerAiStep();
		this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
		if (!this.level().isClientSide() && this.getTarget() != null && rangedCooldown <= 0 && !isBusy()) {
			LivingEntity target = this.getTarget();
			double distance = this.distanceToSqr(target);
			if (distance > 64.0 && distance < 256.0 && this.hasLineOfSight(target)) {
				if (ThreadLocalRandom.current().nextDouble() < 0.4) {
					performRangedAttack();
				}
			}
		}
		LivingEntity target = this.getTarget();
		if (target != null) {
			// Força a entidade a olhar diretamente para o alvo
			this.getLookControl().setLookAt(target, 100.0F, 100.0F);
			// Sincroniza a rotação do corpo com a cabeça
			this.setYBodyRot(this.yHeadRot);
			this.yBodyRot = this.yHeadRot;
			// Garante que a rotação do corpo seja atualizada
			this.yBodyRotO = this.yBodyRot;
		}
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.isWakingUp())
			return event.setAndContinue(RawAnimation.begin().thenPlay("spawn"));
		if (this.isDormant())
			return event.setAndContinue(RawAnimation.begin().thenLoop("sleep"));
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))) {
				if (this.getTarget() != null)
					return event.setAndContinue(RawAnimation.begin().thenLoop("sprint"));
				return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
			}
			if (this.isDeadOrDying())
				return event.setAndContinue(RawAnimation.begin().thenPlay("death"));
			return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
		}
		return PlayState.STOP;
	}

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25);
		builder = builder.add(Attributes.MAX_HEALTH, 800);
		builder = builder.add(Attributes.ARMOR, 20);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 20);
		builder = builder.add(Attributes.FOLLOW_RANGE, 64);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 100);
		builder = builder.add(Attributes.ATTACK_KNOCKBACK, 1);
		return builder;
	}

	private PlayState attackingPredicate(AnimationState event) {
		double d1 = this.getX() - this.xOld;
		double d0 = this.getZ() - this.zOld;
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
        // Remove a barra de boss antes de remover a entidade
        this.bossInfo.removeAllPlayers();
        this.remove(CopperMonstrosityEntity.RemovalReason.KILLED);
        this.dropExperience();
    }
}

@Override
public void remove(RemovalReason reason) {
    // Sempre remove a barra quando a entidade for removida por qualquer motivo
    this.bossInfo.removeAllPlayers();
    super.remove(reason);
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
		if (isBusy())
			return false;
		if (!(target instanceof LivingEntity living))
			return super.doHurtTarget(target);
		this.getNavigation().stop();
		double rand = ThreadLocalRandom.current().nextDouble();
		double distSqr = this.distanceToSqr(target);
		if (summonCooldown <= 0 && rand < 0.1) {
			performSummon();
			return true;
		}
		if (areaAttackCooldown <= 0 && distSqr < 36.0 && rand < 0.4) {
			performAreaAttack();
			return true;
		}
		if (strikeCooldown <= 0 && rand < 0.3) {
			startStrike();
			return true;
		}
		if (slapCooldown <= 0 && rand < 0.5) {
			performSlap();
			return true;
		}
		// Ataque básico
		this.animationprocedure = "attack";
		this.setAnimation("attack");
		this.setAttacking(true); // Ativa ataque corpo a corpo
		
		// DANO FIXO DE 15 COMO SOLICITADO
		float damage = 15.0f;
		boolean hit = living.hurt(this.damageSources().mobAttack(this), damage);
		if (hit) {
			living.knockback(0.6, living.getX() - this.getX(), living.getZ() - this.getZ());
			SoundEvent attackSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.iron_golem.attack"));
			if (attackSound != null)
				this.playSound(attackSound, 1.0f, 0.7f);
		}
		
		// Desativa ataque após um curto período (simula duração do ataque)
		// Nota: Em um cenário real, você pode querer desativar quando a animação terminar
		this.setAttacking(false);
		
		return hit;
	}

	// --- MÉTODOS DE PREPARAÇÃO DOS ATAQUES ---
	private void startStrike() {
		this.getNavigation().stop();
		this.animationprocedure = "strike";
		this.setAnimation("strike");
		this.isPerformingStrike = true;
		this.setAttacking(true); // Ativa ataque corpo a corpo
		strikeEffectTimer = 20;
		this.strikeInvulnerableTicks = 30;
		// EFEITOS NO SLAM (Heavy 255)
		this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 2, false, false));
		// Busca o efeito morebosses:heavy do registro.
		if (ForgeRegistries.MOB_EFFECTS.containsKey(new ResourceLocation("morebosses", "heavy"))) {
			this.addEffect(new MobEffectInstance(ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("morebosses", "heavy")), 60, 255, false, false));
		}
		SoundEvent preparationSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.iron_golem.attack"));
		if (preparationSound != null)
			this.playSound(preparationSound, 1.5f, 0.8f);
		strikeCooldown = STRIKE_COOLDOWN_TIME;
	}

	private void performRangedAttack() {
		this.getNavigation().stop();
		this.animationprocedure = "shoot";
		this.setAnimation("shoot");
		this.isPerformingRanged = true;
		// Não ativa isAttacking() pois é ranged
		rangedEffectTimer = 25;
		this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 25, 3, false, true));
		SoundEvent windSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.trident.throw"));
		if (windSound != null)
			this.playSound(windSound, 1.0f, 0.5f);
		rangedCooldown = RANGED_COOLDOWN_TIME;
	}

	private void performAreaAttack() {
		this.getNavigation().stop();
		this.animationprocedure = "attackarea";
		this.setAnimation("attackarea");
		this.isPerformingAreaAttack = true;
		this.setAttacking(true); // Ativa ataque corpo a corpo
		areaAttackEffectTimer = 20;
		areaAttackCooldown = AREA_COOLDOWN_TIME;
	}

	private void performSlap() {
		this.getNavigation().stop();
		this.animationprocedure = "slap";
		this.setAnimation("slap");
		this.isPerformingSlap = true;
		this.setAttacking(true); // Ativa ataque corpo a corpo
		slapEffectTimer = 15;
		slapCooldown = SLAP_COOLDOWN_TIME;
	}

	private void performSummon() {
		this.getNavigation().stop();
		this.animationprocedure = "summon";
		this.setAnimation("summon");
		this.isPerformingSummon = true;
		// Não ativa isAttacking() pois é summon (não é ataque direto corpo a corpo)
		summonEffectTimer = 25;
		summonCooldown = SUMMON_COOLDOWN_TIME;
		SoundEvent summonSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.evoker.prepare_summon"));
		if (summonSound != null)
			this.playSound(summonSound, 1.0f, 1.0f);
	}

	// --- EXECUÇÃO DOS EFEITOS ---
	private void executeStrikeEffects() { // SLAM
		try {
			BlockPos center = new BlockPos((int) this.getX(), (int) this.getY() - 1, (int) this.getZ());
			WaveEffect.createShockwave(this.level(), center, 8, 12);
			this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.explode")), 1.0f, 1.5f);
		} catch (Exception e) {
		}
	}

	private void executeSummon() {
		if (this.level().isClientSide())
			return;
		Vec3 look = this.getLookAngle();
		Vec3 right = new Vec3(-look.z, 0, look.x).normalize();
		Vec3 left = right.scale(-1);
		BlockPos rightPos = this.blockPosition().offset((int) right.x, 0, (int) right.z);
		BlockPos leftPos = this.blockPosition().offset((int) left.x, 0, (int) left.z);
		spawnEngineer(rightPos);
		spawnEngineer(leftPos);
	}

	private void spawnEngineer(BlockPos pos) {
		Entity entity = MorebossesModEntities.OIL_ENGINEER.get().create(this.level());
		if (entity instanceof Mob mob) {
			mob.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, this.getYRot(), 0);
			mob.finalizeSpawn((ServerLevelAccessor) this.level(), this.level().getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null, null);
			// FAZ O INIMIGO INVOCADO PEGAR O MESMO ALVO DO BOSS
			if (this.getTarget() != null) {
				mob.setTarget(this.getTarget());
			}
			this.level().addFreshEntity(mob);
			((ServerLevel) this.level()).sendParticles(net.minecraft.core.particles.ParticleTypes.POOF, mob.getX(), mob.getY() + 1, mob.getZ(), 10, 0.3, 0.3, 0.3, 0.1);
		}
	}

	private void executeSlapAttack() {
		if (this.getTarget() != null) {
			Entity target = this.getTarget();
			if (this.distanceToSqr(target) < 30.0) {
				// DANO FIXO DE 15 NO SLAP
				float damage = 15.0f;
				if (target.hurt(this.damageSources().mobAttack(this), damage)) {
					if (target instanceof LivingEntity living) {
						double d0 = living.getX() - this.getX();
						double d1 = living.getZ() - this.getZ();
						living.knockback(3.0F, -d0, -d1);
					}
					SoundEvent slapSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.player.attack.knockback"));
					if (slapSound != null)
						this.playSound(slapSound, 1.0f, 0.5f);
				}
			}
		}
	}

	private void executeAreaAttack() {
		double range = 6.0;
		double coneAngle = 120.0;
		// DANO FIXO DE 22 NO ATTACK AREA
		float damage = 22.0f;
		Vec3 lookDir = this.getLookAngle().multiply(1, 0, 1).normalize();
		List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(range, 2.0, range));
		for (LivingEntity entity : entities) {
			if (entity == this)
				continue;
			double yDiff = entity.getY() - this.getY();
			if (yDiff < -1.0 || yDiff > 2.5)
				continue;
			Vec3 targetDir = entity.position().subtract(this.position()).multiply(1, 0, 1).normalize();
			double dotProduct = lookDir.dot(targetDir);
			double angleThreshold = Math.cos(Math.toRadians(coneAngle / 2.0));
			if (dotProduct > angleThreshold && this.distanceTo(entity) <= range) {
				entity.hurt(this.damageSources().mobAttack(this), damage);
				((ServerLevel) this.level()).sendParticles(net.minecraft.core.particles.ParticleTypes.EXPLOSION, entity.getX(), entity.getY(), entity.getZ(), 1, 0, 0, 0, 0);
			}
		}
		SoundEvent smashSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.explode"));
		if (smashSound != null)
			this.playSound(smashSound, 1.0f, 1.2f);
	}

	private void executeRangedAttack() {
		LivingEntity target = this.getTarget();
		if (target == null)
			return;
		double targetX = target.getX();
		double targetY = target.getY() + target.getEyeHeight() * 0.5;
		double targetZ = target.getZ();
		double spawnX = this.getX() + this.getLookAngle().x * 4.0;
		double spawnY = this.getY() + 4.0;
		double spawnZ = this.getZ() + this.getLookAngle().z * 4.0;
		for (int i = 0; i < 2; i++) {
			WindBurstEntity projectile = new WindBurstEntity(MorebossesModEntities.WIND_BURST.get(), this.level());
			projectile.setOwner(this);
			projectile.setPos(spawnX, spawnY, spawnZ);
			double dx = targetX - spawnX;
			double dy = targetY - spawnY;
			double dz = targetZ - spawnZ;
			double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
			if (distance > 0) {
				dx = dx / distance;
				dy = dy / distance;
				dz = dz / distance;
			}
			double variationX = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.1;
			double variationY = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.05;
			double variationZ = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.1;
			double finalDx = dx * 0.9 + variationX;
			double finalDy = dy * 0.9 + variationY;
			double finalDz = dz * 0.9 + variationZ;
			double finalDistance = Math.sqrt(finalDx * finalDx + finalDy * finalDy + finalDz * finalDz);
			if (finalDistance > 0) {
				finalDx = finalDx / finalDistance;
				finalDy = finalDy / finalDistance;
				finalDz = finalDz / finalDistance;
			}
			projectile.shoot(finalDx, finalDy, finalDz, 2.0F, 0.0F);
			projectile.setNoGravity(true);
			this.level().addFreshEntity(projectile);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}
}