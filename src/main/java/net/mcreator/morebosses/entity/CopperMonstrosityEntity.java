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
    private boolean lastloop;
    private int musicLoopTimer = 0;
    // 4 minutos e 19 segundos = 259 segundos. 259 * 20 ticks = 5180 ticks.
    private static final int MUSIC_DURATION_TICKS = 5180;
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
    private static final int WAKE_UP_DURATION = 40; // 2 segundos de animação spawn

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
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true, false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, DryBonesEntity.class, true, false));
        
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
            }

            @Override
            public boolean canUse() {
                // Impede uso de AI se estiver dormindo/acordando
                return super.canUse() && !isPerformingSlam && !isPerformingRanged && !isDormant() && !isWakingUp();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !isPerformingSlam && !isPerformingRanged && !isDormant() && !isWakingUp();
            }
        });
        
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1) {
            @Override
            public boolean canUse() {
                return super.canUse() && !isDormant() && !isWakingUp();
            }
        });
        
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
        if (slamInvulnerableTicks > 0) return false;
        if (source.is(DamageTypes.FALL)) return false;
        if (source.is(DamageTypes.EXPLOSION)) return false;
        
        // --- RESTAURADO ---
        // Se estiver dormindo e receber dano (ex: flecha de longe), ele acorda.
        if (this.isDormant()) {
            triggerWakeUp();
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
         
        // ... outros puts ...
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
        // Só executa se estiver dormindo para evitar resets acidentais
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

   @Override
    public void customServerAiStep() {
        // ==================================================
        // 1. LÓGICA DE DORMIR
        // ==================================================
        if (this.isDormant()) {
            // TRAVA MOVIMENTO E ROTAÇÃO
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
            this.getNavigation().stop();
            this.getLookControl().setLookAt(this.getX(), this.getY(), this.getZ());

            boolean trigger = false;
            // AQUI ESTAVA A PRIMEIRA DEFINIÇÃO DE CURRENT TARGET
            LivingEntity sleepTarget = this.getTarget(); 

            // CASO 1: Tem um alvo definido e está perto
            if (sleepTarget != null) {
                if (this.distanceToSqr(sleepTarget) <= 225.0D) {
                    trigger = true;
                }
            } 
            // CASO 2: Detecção passiva de jogador perto
            else {
                Player nearestPlayer = this.level().getNearestPlayer(this, 15.0D);
                if (nearestPlayer != null && !nearestPlayer.isCreative() && !nearestPlayer.isSpectator()) {
                    trigger = true;
                }
            }

            if (trigger) {
                triggerWakeUp();
            }
            
            // Interrompe o resto da IA enquanto dorme
            this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
            return; // <--- O CÓDIGO PARAVA AQUI, POR ISSO A MÚSICA NÃO TOCAVA
        } // <--- FECHEI O IF DO DORMANT AQUI

        // ==================================================
        // 2. LÓGICA DE ACORDAR
        // ==================================================
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

        // ==================================================
        // 3. COMPORTAMENTO PADRÃO (Música e Ataques)
        // ==================================================
        super.customServerAiStep();
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());

        // --- LÓGICA DA MÚSICA (Agora no lugar certo) ---
        if (!this.level().isClientSide()) {
            boolean shouldPlayMusic = false;

            // Verifica se está viva, acordada e não acordando
            if (this.isAlive() && !this.isDormant() && !this.isWakingUp()) {
                // AQUI ESTAVA A SEGUNDA DEFINIÇÃO (AGORA VÁLIDA POIS MUDAMOS O ESCOPO)
                LivingEntity musicTarget = this.getTarget(); 
                
                // Verifica se tem alvo, se é Player e está vivo
                if (musicTarget instanceof Player && musicTarget.isAlive()) {
                    // Raio de 40 blocos (40^2 = 1600)
                    if (this.distanceToSqr(musicTarget) <= 1600.0D) {
                        shouldPlayMusic = true;
                    }
                }
            }

            if (shouldPlayMusic) {
                if (this.musicLoopTimer <= 0) {
                    SoundEvent musicSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("morebosses", "copper_placeholder"));
                    
                    if (musicSound != null) {
                        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), 
                            musicSound, net.minecraft.sounds.SoundSource.RECORDS, 4.0f, 1.0f);
                    }
                    this.musicLoopTimer = MUSIC_DURATION_TICKS;
                }
                this.musicLoopTimer--;
            } else {
                this.musicLoopTimer = 0;
            }
        }
        
        // --- ATAQUE À DISTÂNCIA ALEATÓRIO ---
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

    // Métodos estáticos e auxiliares mantidos
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

        if (!(target instanceof LivingEntity living))
            return super.doHurtTarget(target);
            
        if (useRangedNext && rangedCooldown <= 0 && this.distanceToSqr(target) > 16.0) {
            performRangedAttack();
            return true;
        }
        
        if (useSlamNext && slamCooldown <= 0 && this.getDeltaMovement().horizontalDistanceSqr() > MOVEMENT_THRESHOLD) {
            this.animationprocedure = "slam";
            this.setAnimation("slam");
            this.isPerformingSlam = true;
            slamEffectTimer = 20;
            this.slamInvulnerableTicks = 40;
            
            net.minecraft.world.effect.MobEffect heavyEffect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("morebosses", "heavy"));
            if (heavyEffect != null) {
                this.addEffect(new net.minecraft.world.effect.MobEffectInstance(heavyEffect, 40, 0, false, true));
            } else {
                this.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 40, 2, false, true));
            }
            
            SoundEvent preparationSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.iron_golem.attack"));
            if (preparationSound != null) {
                this.playSound(preparationSound, 1.5f, 0.8f);
            }
            slamCooldown = SLAM_COOLDOWN_TIME;
            useSlamNext = false;
            return true;
        }
        
        this.animationprocedure = "attack";
        this.setAnimation("attack");
        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        boolean hit = living.hurt(this.damageSources().mobAttack(this), damage);
        if (hit) {
            living.knockback(0.5, living.getX() - this.getX(), living.getZ() - this.getZ());
            SoundEvent attackSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.iron_golem.attack"));
            if (attackSound != null) {
                this.playSound(attackSound, 1.0f, 0.7f);
            }
        }
        
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
    
    // REMOVENDO o som de disparo
    // SoundEvent shootSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.witch.throw"));
    // if (shootSound != null) {
    //     this.playSound(shootSound, 1.0f, 0.8f);
    // }
    
    // Calculando direção mais precisa para o jogador
    double targetX = target.getX();
    double targetY = target.getY() + target.getEyeHeight() * 0.5; // Mirar na altura do torso
    double targetZ = target.getZ();
    
    double spawnX = this.getX() + this.getLookAngle().x * 4.0;
    double spawnY = this.getY() + 4.0;
    double spawnZ = this.getZ() + this.getLookAngle().z * 4.0;
    
    for (int i = 0; i < 2; i++) {
        WindBurstEntity projectile = new WindBurstEntity(MorebossesModEntities.WIND_BURST.get(), this.level());
        projectile.setOwner(this);
        projectile.setPos(spawnX, spawnY, spawnZ);
        
        // Calculando direção direta para o alvo com pequena variação
        double dx = targetX - spawnX;
        double dy = targetY - spawnY;
        double dz = targetZ - spawnZ;
        
        // Normalizar o vetor
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > 0) {
            dx = dx / distance;
            dy = dy / distance;
            dz = dz / distance;
        }
        
        // Adicionar pequena variação aleatória (reduzida para ser mais preciso)
        double variationX = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.1; // Reduzido de 0.2 para 0.1
        double variationY = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.05; // Reduzido de 0.1 para 0.05
        double variationZ = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.1; // Reduzido de 0.2 para 0.1
        
        // Direção final (90% para o alvo, 10% variação)
        double finalDx = dx * 0.9 + variationX;
        double finalDy = dy * 0.9 + variationY;
        double finalDz = dz * 0.9 + variationZ;
        
        // Normalizar novamente
        double finalDistance = Math.sqrt(finalDx * finalDx + finalDy * finalDy + finalDz * finalDz);
        if (finalDistance > 0) {
            finalDx = finalDx / finalDistance;
            finalDy = finalDy / finalDistance;
            finalDz = finalDz / finalDistance;
        }
        
        // Velocidade mais rápida para serem mais diretos
        projectile.shoot(finalDx, finalDy, finalDz, 2.0F, 0.0F); // Aumentado de 1.5F para 2.0F
        
        // Configurar para não colidir com outros projéteis do mesmo tipo
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
        // Só adiciona o jogador à barra de boss se não estiver dormindo
        if (!this.isDormant()) {
            this.bossInfo.addPlayer(player);
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        // Remove o jogador da barra de boss
        this.bossInfo.removePlayer(player);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}