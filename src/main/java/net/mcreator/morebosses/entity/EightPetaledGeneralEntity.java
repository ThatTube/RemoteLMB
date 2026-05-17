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

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

import net.mcreator.morebosses.init.MorebossesModEntities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EightPetaledGeneralEntity extends Monster implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(EightPetaledGeneralEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(EightPetaledGeneralEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(EightPetaledGeneralEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> IS_DASHING = SynchedEntityData.defineId(EightPetaledGeneralEntity.class, EntityDataSerializers.BOOLEAN);

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private long lastSwing;
	public String animationprocedure = "empty";
	String prevAnim = "empty";

	// ADAPTAÇÃO
	private final LinkedList<String> adaptationOrder = new LinkedList<>();
	private final Map<String, Float> adaptationLevels = new HashMap<>();
	private final Map<String, Integer> adaptationTimers = new HashMap<>();

	// ESTADOS
	private int attackTimer = 0;
	private int attackState = 0;
	private int attackComboCount = 0;
	private int tiredTicks = 0;
	private boolean isFlying = false;

	public EightPetaledGeneralEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(MorebossesModEntities.EIGHT_PETALED_GENERAL.get(), world);
	}

	public EightPetaledGeneralEntity(EntityType<EightPetaledGeneralEntity> type, Level world) {
		super(type, world);
		xpReward = 500;
		setNoAi(false);
		setMaxUpStep(1f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "cactus_knight");
		this.entityData.define(IS_DASHING, false);
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
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Husk.class, true, false));
		this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
		this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(6, new FloatGoal(this));
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
	public EntityDimensions getDimensions(Pose pose) {
		return super.getDimensions(pose).scale(1f);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.FALL) || source.is(DamageTypes.CACTUS))
			return false;

		if (this.tiredTicks > 0) {
			amount *= 1.5f;
			return super.hurt(source, amount);
		}

		if (this.random.nextFloat() < 0.23f) {
			this.animationprocedure = "block";
			this.playSound(SoundEvents.SHIELD_BLOCK, 1.0f, 1.0f);
			return false;
		}

		if (source.getEntity() instanceof LivingEntity attacker) {
			attacker.hurt(this.damageSources().thorns(this), 3.0f);
			this.playSound(SoundEvents.THORNS_HIT, 1.0f, 1.0f);
		}

		String sourceName = source.getMsgId();
		if (adaptationLevels.containsKey(sourceName)) {
			float reduction = adaptationLevels.get(sourceName);
			amount *= (1.0f - reduction);
			adaptationLevels.put(sourceName, Math.min(0.87f, reduction + 0.15f));
			adaptationTimers.put(sourceName, 0);
		} else {
			if (adaptationOrder.size() >= 10) {
				String oldest = adaptationOrder.removeFirst();
				adaptationLevels.remove(oldest);
				adaptationTimers.remove(oldest);
			}
			adaptationOrder.add(sourceName);
			adaptationLevels.put(sourceName, 0.1f);
			adaptationTimers.put(sourceName, 0);
			this.playSound(SoundEvents.BEACON_ACTIVATE, 1.0f, 2.0f);
			this.animationprocedure = (this.getDeltaMovement().horizontalDistance() > 0.05) ? "adwalk" : "adapt";
		}
		return super.hurt(source, amount);
	}

	@Override
	public void baseTick() {
		super.baseTick();
		this.refreshDimensions();

		if (this.level().isClientSide) {
			if (this.entityData.get(IS_DASHING)) spawnDashParticles();
			return;
		}

		// Fadiga
		if (this.tiredTicks > 0) {
			this.tiredTicks--;
			this.getNavigation().stop();
			this.animationprocedure = "tired";
			return;
		}

		// Limpeza de adaptação (30s)
		adaptationTimers.entrySet().removeIf(entry -> {
			entry.setValue(entry.getValue() + 1);
			if (entry.getValue() > 600) {
				adaptationLevels.remove(entry.getKey());
				adaptationOrder.remove(entry.getKey());
				return true;
			}
			return false;
		});

		LivingEntity target = this.getTarget();
		if (target != null && target.isAlive()) {
			double distance = this.distanceToSqr(target);

			// Voo
			if (distance > 100) {
				if (!isFlying) {
					this.animationprocedure = "flystart";
					this.playSound(SoundEvents.ENDER_DRAGON_FLAP, 1.0F, 1.0F);
					isFlying = true;
				}
				Vec3 dir = target.position().subtract(this.position()).normalize();
				this.setDeltaMovement(this.getDeltaMovement().add(dir.scale(0.05)));
				this.setNoGravity(true);
			} else {
				this.setNoGravity(false);
				isFlying = false;
			}

			if (attackTimer > 0) attackTimer--;
			if (distance <= 16 && attackTimer <= 0 && !isFlying) {
				executeRandomAttack(target, distance);
			}
			handleAttackStates(target);
		} else {
			this.setNoGravity(false);
			isFlying = false;
			attackComboCount = 0;
			this.entityData.set(IS_DASHING, false);
		}
	}

	private void spawnDashParticles() {
        if (this.level().isClientSide() && this.tickCount % 2 == 0) {
            try {
                // Usamos o método level() e o próprio objeto 'this' em vez de 'entity'
                
   if (this.level().isClientSide()) {
    net.mcreator.morebosses.client.particle.AfterImageParticleParticle.spawn(
        (net.minecraft.client.multiplayer.ClientLevel) this.level(),
        this.getX(), this.getY(), this.getZ(),
       	0, 255, 0,        // vermelho
        this.getId(),
        false,            // ghost
        10                // duração
    );
}

            } catch (Exception e) {
                // Silencioso para evitar crash no log
            }
        }
    }

	private void executeRandomAttack(LivingEntity target, double distance) {
		if (attackComboCount >= 6) {
			this.tiredTicks = 100;
			this.attackComboCount = 0;
			this.playSound(SoundEvents.PLAYER_BREATH, 1.0f, 0.5f);
			return;
		}
		this.getNavigation().stop();
		this.lookAt(target, 30.0F, 30.0F);
		int rand = this.random.nextInt(3);
		if (rand == 0 && distance <= 9) {
			this.animationprocedure = "slash";
			this.attackState = 1; this.attackTimer = 20;
			this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
		} else if (rand == 1) {
			this.animationprocedure = "estocada";
			this.attackState = 2; this.attackTimer = 10;
			this.playSound(SoundEvents.PLAYER_ATTACK_CRIT, 1.0F, 0.8F);
		} else if (distance <= 6) {
			this.animationprocedure = "corte";
			this.attackState = 4; this.attackTimer = 15;
			this.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.0F, 1.0F);
		}
		attackComboCount++;
	}

	private void handleAttackStates(LivingEntity target) {
		if (attackState == 1 && attackTimer == 5) {
			List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(3.0D));
			for (LivingEntity e : entities) {
				if (e != this && (e == target || e.distanceToSqr(this) < 9)) {
					Vec3 dir = e.position().subtract(this.position()).normalize();
					if (dir.dot(this.getLookAngle()) > 0.5D) e.hurt(this.damageSources().mobAttack(this), 12.0F);
				}
			}
		} else if (attackState == 2 && attackTimer <= 0) {
			this.attackState = 3; this.attackTimer = 10;
			this.entityData.set(IS_DASHING, true);
			Vec3 dashVec = this.getLookAngle().scale(1.5);
			this.setDeltaMovement(dashVec.x, this.getDeltaMovement().y, dashVec.z);
		} else if (attackState == 3) {
			this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0D)).forEach(e -> {
				if (e != this) e.hurt(this.damageSources().mobAttack(this), 15.0F);
			});
			if (attackTimer <= 0) {
				attackState = 0; this.entityData.set(IS_DASHING, false);
			}
		} else if (attackState == 4 && attackTimer == 5) {
			if (this.distanceToSqr(target) <= 9) {
				target.hurt(this.damageSources().mobAttack(this), 8.0F);
				target.knockback(0.5D, this.getX() - target.getX(), this.getZ() - target.getZ());
			}
		}
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if (this.isFlying) return event.setAndContinue(RawAnimation.begin().thenLoop("fly"));
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) && this.onGround()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
			}
			if (this.isDeadOrDying()) return event.setAndContinue(RawAnimation.begin().thenPlay("death"));
			if (!this.onGround()) return event.setAndContinue(RawAnimation.begin().thenLoop("fly"));
			return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationState event) {
		if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 7L <= level().getGameTime()) this.swinging = false;
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("slash"));
		}
		return PlayState.CONTINUE;
	}

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
	protected void tickDeath() {
		++this.deathTime;
		if (this.deathTime == 20) {
			this.remove(EightPetaledGeneralEntity.RemovalReason.KILLED);
			this.dropExperience();
		}
	}

	public String getSyncedAnimation() { return this.entityData.get(ANIMATION); }
	public void setAnimation(String animation) { this.entityData.set(ANIMATION, animation); }

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

	public static void init() {}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25);
		builder = builder.add(Attributes.MAX_HEALTH, 100);
		builder = builder.add(Attributes.ARMOR, 45);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 8);
		builder = builder.add(Attributes.FOLLOW_RANGE, 64);
		return builder;
	}
}