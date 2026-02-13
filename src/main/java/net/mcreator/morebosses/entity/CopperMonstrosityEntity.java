package net.mcreator.morebosses.entity;

import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.GeoEntity;
import net.minecraft.sounds.SoundSource;

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
import java.util.List;

public class CopperMonstrosityEntity extends Monster implements GeoEntity {
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(CopperMonstrosityEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(CopperMonstrosityEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(CopperMonstrosityEntity.class, EntityDataSerializers.STRING);
    
    // Dados de Estado
    public static final EntityDataAccessor<Boolean> DORMANT = SynchedEntityData.defineId(CopperMonstrosityEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> WAKING_UP = SynchedEntityData.defineId(CopperMonstrosityEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean swinging;
    private boolean isPerformingRanged = false;
    private int musicLoopTimer = 0;
    private static final int MUSIC_DURATION_TICKS = 5180;
    
    // Variáveis de Combate
    private int slamInvulnerableTicks = 0;
    private int slamCooldown = 0;
    private int rangedCooldown = 0;
    private boolean useSlamNext = false;
    private boolean useRangedNext = false;
    private long lastSwing;
    public String animationprocedure = "empty";
    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.BLUE, ServerBossEvent.BossBarOverlay.PROGRESS);
    
    private int slamEffectTimer = 0;
    private int rangedEffectTimer = 0;
    
    // Timer para acordar
    private int wakeUpTimer = 0;
    private static final int WAKE_UP_DURATION = 40; 

    // Variáveis de Inteligência e Pânico
    private int damageHitCounter = 0;
    private int panicTimer = 0;

    private static final int SLAM_COOLDOWN_TIME = 100;
    private static final int RANGED_COOLDOWN_TIME = 80;
    private static final double MOVEMENT_THRESHOLD = 1.0E-6D;
    private boolean isPerformingSlam = false;

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
        // IA Melhorada: Prioridade para revidar e atacar
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, DryBonesEntity.class, true));
        
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                // Alcance levemente maior para facilitar o ataque
                return (double)(this.mob.getBbWidth() * 1.5F * this.mob.getBbWidth() * 1.5F + entity.getBbWidth());
            }

            @Override
            public boolean canUse() {
                // Bloqueia IA normal se estiver fazendo animação especial
                return super.canUse() && !isPerformingSlam && !isPerformingRanged && !isDormant() && !isWakingUp();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !isPerformingSlam && !isPerformingRanged && !isDormant() && !isWakingUp();
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
    public boolean hurt(DamageSource source, float amount) {
        // IMUNIDADE A RAIO E ELETRICIDADE
        if (source.is(DamageTypes.LIGHTNING_BOLT)) return false;

        if (slamInvulnerableTicks > 0) return false;
        if (source.is(DamageTypes.FALL)) return false;
        if (source.is(DamageTypes.EXPLOSION)) return false;
        
        if (this.isDormant()) {
            triggerWakeUp();
        }
        
        // CONTADOR DE DANO PARA MODO PÂNICO
        if (!this.level().isClientSide() && !isDormant() && !isWakingUp()) {
            this.damageHitCounter++;
            this.panicTimer = 60; // 3 segundos para considerar "ataque em grupo"
        }

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
        compound.putInt("MusicLoopTimer", this.musicLoopTimer);
        compound.putString("Texture", this.getTexture());
        compound.putInt("SlamCooldown", this.slamCooldown);
        compound.putInt("RangedCooldown", this.rangedCooldown);
        compound.putBoolean("UseSlamNext", this.useSlamNext);
        compound.putBoolean("UseRangedNext", this.useRangedNext);
        compound.putBoolean("IsPerformingSlam", this.isPerformingSlam);
        compound.putBoolean("IsPerformingRanged", this.isPerformingRanged);
        
        compound.putBoolean("Dormant", this.isDormant());
        compound.putBoolean("WakingUp", this.isWakingUp());
        compound.putInt("WakeUpTimer", this.wakeUpTimer);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Texture")) this.setTexture(compound.getString("Texture"));
        if (compound.contains("SlamCooldown")) this.slamCooldown = compound.getInt("SlamCooldown");
        if (compound.contains("RangedCooldown")) this.rangedCooldown = compound.getInt("RangedCooldown");
        if (compound.contains("UseSlamNext")) this.useSlamNext = compound.getBoolean("UseSlamNext");
        if (compound.contains("UseRangedNext")) this.useRangedNext = compound.getBoolean("UseRangedNext");
        if (compound.contains("IsPerformingSlam")) this.isPerformingSlam = compound.getBoolean("IsPerformingSlam");
        if (compound.contains("IsPerformingRanged")) this.isPerformingRanged = compound.getBoolean("IsPerformingRanged");
        if (compound.contains("MusicLoopTimer")) this.musicLoopTimer = compound.getInt("MusicLoopTimer");
        if (compound.contains("Dormant")) this.setDormant(compound.getBoolean("Dormant"));
        if (compound.contains("WakingUp")) this.setWakingUp(compound.getBoolean("WakingUp"));
        if (compound.contains("WakeUpTimer")) this.wakeUpTimer = compound.getInt("WakeUpTimer");
    }

    private void triggerWakeUp() {
        if (this.isDormant()) {
            this.setDormant(false);
            this.setWakingUp(true);
            this.wakeUpTimer = WAKE_UP_DURATION;
            
            SoundEvent wakeSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.warden.emerge"));
            if (wakeSound != null) {
                this.playSound(wakeSound, 1.0f, 0.5f);
            }
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        MonstruosidadeQuebraBlocosProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
        this.refreshDimensions();
        
        if (!this.level().isClientSide()) {
            if (slamCooldown > 0) slamCooldown--;
            if (rangedCooldown > 0) rangedCooldown--;
            if (slamInvulnerableTicks > 0) slamInvulnerableTicks--;
            
            // LÓGICA DE PÂNICO E DETECÇÃO DE CERCO
            if (panicTimer > 0) {
                panicTimer--;
                if (panicTimer == 0) damageHitCounter = 0;
            }
            
            // Se tomou 3 hits ou mais e não está atacando
            if (damageHitCounter >= 3 && !isPerformingSlam && !isPerformingRanged) {
                // Verifica inimigos próximos
                long nearbyEnemies = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(6.0D))
                    .stream()
                    .filter(e -> e != this && (e instanceof Player || e instanceof Monster))
                    .count();

                // Se tiver 2 ou mais inimigos colados, ativa o SLAM defensivo
                if (nearbyEnemies >= 2) {
                    this.activatePanicSlam();
                }
            }

            // Controle de efeitos de animação
            if (slamEffectTimer > 0) {
                slamEffectTimer--;
                if (slamEffectTimer == 10 && this.animationprocedure.equals("slam")) executeSlamEffects();
                if (slamEffectTimer == 0) {
                    this.animationprocedure = "empty";
                    this.isPerformingSlam = false;
                }
            }
            if (rangedEffectTimer > 0) {
                rangedEffectTimer--;
                if (rangedEffectTimer == 15 && this.animationprocedure.equals("shot")) executeRangedAttack();
                if (rangedEffectTimer == 0) {
                    this.animationprocedure = "empty";
                    this.isPerformingRanged = false;
                }
            }
        }
    }

    private void activatePanicSlam() {
        this.damageHitCounter = 0; 
        this.slamCooldown = 0; // Zera cooldown
        this.useSlamNext = true;
        
        // Se já tiver um alvo, tenta atacar agora
        LivingEntity target = this.getTarget();
        if (target != null) {
            this.doHurtTarget(target);
        } else {
             // Se não tiver alvo, apenas seta o estado para o próximo AI tick pegar
             this.useSlamNext = true;
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

            if (sleepTarget != null) {
                if (this.distanceToSqr(sleepTarget) <= 225.0D) {
                    trigger = true;
                }
            } else {
                Player nearestPlayer = this.level().getNearestPlayer(this, 15.0D);
                if (nearestPlayer != null && !nearestPlayer.isCreative() && !nearestPlayer.isSpectator()) {
                    trigger = true;
                }
            }

            if (trigger) triggerWakeUp();
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

        super.customServerAiStep();
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
        
        if (!this.level().isClientSide() && this.getTarget() != null && rangedCooldown <= 0 && !isPerformingSlam && !isPerformingRanged) {
            LivingEntity target = this.getTarget();
            double distance = this.distanceToSqr(target);
            if (distance > 64.0 && distance < 256.0 && this.hasLineOfSight(target)) {
                if (ThreadLocalRandom.current().nextDouble() < 0.4) {
                    performRangedAttack();
                }
            }
        }
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.isWakingUp()) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("spawn"));
        }
        if (this.isDormant()) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("sleep"));
        }

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

    public static void init() { }

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
        if (isDormant() || isWakingUp()) return false;
        
        // TRAVA DE SEGURANÇA: Se já estiver dando Slam, não faz mais nada para não cancelar a animação
        if (isPerformingSlam || isPerformingRanged) return false;

        if (!(target instanceof LivingEntity living))
            return super.doHurtTarget(target);
            
        // Se a IA decidiu usar Ranged antes e está longe
        if (useRangedNext && rangedCooldown <= 0 && this.distanceToSqr(target) > 16.0) {
            performRangedAttack();
            return true;
        }
        
        // Chance de Combo: 15% de chance de usar Slam imediatamente após tentar atacar
        boolean comboChance = ThreadLocalRandom.current().nextDouble() < 0.15;

        // Se estiver marcado para usar Slam (seja pelo pânico ou pela sorte)
        if ((useSlamNext || comboChance) && slamCooldown <= 0) {
            this.animationprocedure = "slam";
            this.setAnimation("slam");
            this.isPerformingSlam = true;
            slamEffectTimer = 20;
            this.slamInvulnerableTicks = 30; // Fica invulnerável durante a subida do braço
            
            // Efeito de resistência para não morrer durante a animação
            this.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE, 40, 2, false, false));
            
            SoundEvent preparationSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.iron_golem.attack"));
            if (preparationSound != null) {
                this.playSound(preparationSound, 1.5f, 0.8f);
            }
            slamCooldown = SLAM_COOLDOWN_TIME;
            useSlamNext = false;
            damageHitCounter = 0; // Reseta o pânico
            return true;
        }
        
        // ATAQUE PADRÃO (SOCO)
        this.animationprocedure = "attack";
        this.setAnimation("attack");
        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        boolean hit = living.hurt(this.damageSources().mobAttack(this), damage);
        if (hit) {
            living.knockback(0.6, living.getX() - this.getX(), living.getZ() - this.getZ());
            SoundEvent attackSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.iron_golem.attack"));
            if (attackSound != null) {
                this.playSound(attackSound, 1.0f, 0.7f);
            }
        }
        
        // IA Decide o próximo passo
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

    private void performRangedAttack() {
        this.animationprocedure = "shot";
        this.setAnimation("shot");
        this.isPerformingRanged = true;
        rangedEffectTimer = 25;
        net.minecraft.world.effect.MobEffect heavyEffect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("morebosses", "heavy"));
        if (heavyEffect != null) {
            this.addEffect(new net.minecraft.world.effect.MobEffectInstance(heavyEffect, 25, 0, false, true));
        } else {
            this.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 25, 3, false, true));
        }
        SoundEvent windSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.trident.throw"));
        if (windSound != null) {
            this.playSound(windSound, 1.0f, 0.5f);
        }
        rangedCooldown = RANGED_COOLDOWN_TIME;
        useRangedNext = false;
    }

    private void executeRangedAttack() {
        LivingEntity target = this.getTarget();
        if (target == null) return;

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
            } catch (InterruptedException e) {}
        }
    }

    private void executeSlamEffects() {
        SoundEvent explosionSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.explode"));
        if (explosionSound != null) {
            this.playSound(explosionSound, 1.0f, 1.6f);
        }
        try {
            BlockPos center = new BlockPos((int) this.getX(), (int) this.getY() - 1, (int) this.getZ());
            WaveEffect.createShockwave(this.level(), center, 8, 12);
        } catch (Exception e) {
            System.err.println("Erro ao criar onda de choque: " + e.getMessage());
        }
        double radius = 4.0;
        for (Player player : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(radius, 3.0, radius))) {
            if (player.isBlocking()) {
                player.disableShield(true);
                player.getCooldowns().addCooldown(player.getUseItem().getItem(), 40);
                var panicEffect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("morebosses", "panic"));
                if (panicEffect != null) {
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(panicEffect, 60, 0, false, true));
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
        if (!this.isDormant()) {
            this.bossInfo.addPlayer(player);
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
    
    private void stopBossMusic() {
        if (!this.level().isClientSide() && this.getServer() != null) {
            String command = "stopsound @a record morebosses:copper_placeholder";
            this.getServer().getCommands().performPrefixedCommand(this.createCommandSourceStack().withSuppressedOutput(), command);
        }
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        stopBossMusic(); 
    }
}