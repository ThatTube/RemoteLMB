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
import net.minecraft.world.item.Items;
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
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
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

import net.mcreator.morebosses.init.MorebossesModEntities;

import java.util.UUID;

public class PiglinBoulusEntity extends Monster implements GeoEntity {
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(PiglinBoulusEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(PiglinBoulusEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(PiglinBoulusEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> ATTACK_TYPE = SynchedEntityData.defineId(PiglinBoulusEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> IS_SPAWNING = SynchedEntityData.defineId(PiglinBoulusEntity.class, EntityDataSerializers.BOOLEAN);

    private static final UUID RAGE_DAMAGE_MODIFIER_UUID = UUID.fromString("12345678-1234-1234-1234-1234567890ab");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean swinging;
    private boolean lastloop;
    private long lastSwing;
    public String animationprocedure = "empty";
    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.RED, ServerBossEvent.BossBarOverlay.PROGRESS);

    private int spawnTimer = 0;
    private boolean hasSpawned = false;
    private boolean isEnraged = false;

    public PiglinBoulusEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(MorebossesModEntities.PIGLIN_BOULUS.get(), world);
    }

    public PiglinBoulusEntity(EntityType<PiglinBoulusEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        setNoAi(false);
        setMaxUpStep(1f);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHOOT, false);
        this.entityData.define(ANIMATION, "idle");
        this.entityData.define(TEXTURE, "piglinboulus");
        this.entityData.define(ATTACK_TYPE, 0);
        this.entityData.define(IS_SPAWNING, false);
    }

    public void setTexture(String texture) {
        this.entityData.set(TEXTURE, texture);
    }

    public String getTexture() {
        return this.entityData.get(TEXTURE);
    }

    public void setIsSpawning(boolean spawning) {
        this.entityData.set(IS_SPAWNING, spawning);
    }

    public boolean getIsSpawning() {
        return this.entityData.get(IS_SPAWNING);
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
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, true) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
            }
            
            @Override
            public boolean canUse() {
                return super.canUse() && hasSpawned;
            }
            
            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && hasSpawned;
            }
        });
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1) {
            @Override
            public boolean canUse() {
                return super.canUse() && hasSpawned;
            }
        });
        this.targetSelector.addGoal(5, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && hasSpawned;
            }
        });
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
        if (source.is(DamageTypes.IN_FIRE))
            return false;
        if (source.is(DamageTypes.FALL))
            return false;
        
        if (!hasSpawned && spawnTimer < 100)
            return false;
            
        return super.hurt(source, amount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Texture", this.getTexture());
        compound.putBoolean("HasSpawned", this.hasSpawned);
        compound.putBoolean("IsEnraged", this.isEnraged);
        compound.putInt("SpawnTimer", this.spawnTimer);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Texture"))
            this.setTexture(compound.getString("Texture"));
        if (compound.contains("HasSpawned"))
            this.hasSpawned = compound.getBoolean("HasSpawned");
        if (compound.contains("IsEnraged"))
            this.isEnraged = compound.getBoolean("IsEnraged");
        if (compound.contains("SpawnTimer"))
            this.spawnTimer = compound.getInt("SpawnTimer");
        
        // Restaura o estado de spawning
        this.setIsSpawning(!this.hasSpawned && this.spawnTimer > 0 && this.spawnTimer < 100);
    }

    @Override
    public void tick() {
        super.tick();
        
        if (this.level().isClientSide) {
            // NO CLIENTE: Sincroniza a animação do servidor
            this.animationprocedure = this.getSyncedAnimation();
        }
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

        // --- LÓGICA DE SPAWN (Servidor) ---
        if (!hasSpawned) {
            spawnTimer++;
            
            // Trava movimento
            this.setDeltaMovement(0, 0, 0);
            this.getNavigation().stop();
            if (this.getTarget() != null) this.setTarget(null);

            // COMEÇA A ANIMAÇÃO DE SPAWN
            if (spawnTimer == 1) {
                this.setAnimation("spawn");
                this.setIsSpawning(true);
                this.setInvulnerable(true);
                System.out.println("PORRA! Iniciando animação de spawn!");
            }

            // Som
            if (spawnTimer == 70) {
                this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("morebosses:piglinboulusscream")), 1.0f, 1.0f);
            }

            // Termina spawn
            if (spawnTimer >= 100) {
                this.hasSpawned = true;
                this.setInvulnerable(false);
                this.setAnimation("idle");
                this.setIsSpawning(false);
                System.out.println("PORRA! Spawn completo!");
            }
        }

        // --- LÓGICA DE RAIVA (Servidor) ---
        if (!this.level().isClientSide) {
            if (!this.isEnraged && this.getHealth() <= this.getMaxHealth() / 2) {
                this.isEnraged = true;
                this.setTexture("piglinangry");
                this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("morebosses:piglinboulusscream")), 1.0f, 1.0f);

                if (this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                    this.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(RAGE_DAMAGE_MODIFIER_UUID);
                    this.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(
                        new AttributeModifier(RAGE_DAMAGE_MODIFIER_UUID, "Rage damage boost", 2.5, AttributeModifier.Operation.ADDITION)
                    );
                }
            }
        }
    }

    @Override
    public void swing(InteractionHand hand) {
        if (!this.level().isClientSide) {
            int type = Math.random() < 0.7 ? 1 : 2;
            this.entityData.set(ATTACK_TYPE, type);
        }
        this.swinging = true;
        this.lastSwing = this.level().getGameTime();
        super.swing(hand);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!hasSpawned) {
            return false;
        }
        
        boolean result = super.doHurtTarget(target);
        
        if (result && !this.level().isClientSide) {
            int type = this.entityData.get(ATTACK_TYPE);
            
            if (type == 2) {
                if (target instanceof LivingEntity livingTarget) {
                    // DANO EXTRA 5%
                    float maxHealth = livingTarget.getMaxHealth();
                    float extraDamage = maxHealth * 0.05f;
                    livingTarget.hurt(this.damageSources().mobAttack(this), extraDamage);
                    
                    if (livingTarget.isBlocking()) {
                        this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.shield.break")), 1.0f, 1.0f);
                        
                        // EFEITO PANIC
                        ResourceLocation panicEffectId = new ResourceLocation("morebosses:panic");
                        var panicEffect = ForgeRegistries.MOB_EFFECTS.getValue(panicEffectId);
                        if (panicEffect != null) {
                            livingTarget.addEffect(new MobEffectInstance(panicEffect, 60, 0));
                        }
                        
                        if (livingTarget instanceof Player player) {
                            player.getCooldowns().addCooldown(Items.SHIELD, 100); 
                            player.stopUsingItem();
                        }
                    } else {
                        livingTarget.setDeltaMovement(livingTarget.getDeltaMovement().add(0, 2.0, 0));
                        livingTarget.hasImpulse = true;
                    }
                }
            }
        }
        return result;
    }

    public static void init() { }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
        builder = builder.add(Attributes.MAX_HEALTH, 500);
        builder = builder.add(Attributes.ARMOR, 20);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 14);
        builder = builder.add(Attributes.FOLLOW_RANGE, 32);
        return builder;
    }

    // --- ANIMAÇÕES ---
    
    private PlayState movementPredicate(AnimationState event) {
        // SE ESTÁ SPAWNANDO, NÃO ANDA
        if (this.getIsSpawning()) {
            return PlayState.STOP;
        }
        
        // Morto
        if (this.isDeadOrDying()) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("death"));
        }
        
        // Andando ou parado
        double dx = this.getX() - this.xOld;
        double dz = this.getZ() - this.zOld;
        double distance = Math.sqrt(dx * dx + dz * dz);
        
        if (distance > 0.01) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
        } else {
            return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
    }

    private PlayState attackingPredicate(AnimationState event) {
        // SE ESTÁ SPAWNANDO, NÃO ATACA
        if (this.getIsSpawning()) {
            return PlayState.STOP;
        }
        
        // Lógica de ataque
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 7L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            int attackType = this.entityData.get(ATTACK_TYPE);
            if (attackType == 2) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("jab"));
            } else {
                return event.setAndContinue(RawAnimation.begin().thenPlay("punch"));
            }
        }
        return PlayState.CONTINUE;
    }

    private PlayState procedurePredicate(AnimationState event) {
        // ESSA É A PORRA DA ANIMAÇÃO DE SPAWN!
        if (!this.animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(PiglinBoulusEntity.RemovalReason.KILLED);
            this.dropExperience();
        }
    }

    public String getSyncedAnimation() {
        return this.entityData.get(ANIMATION);
    }

    public void setAnimation(String animation) {
        // ESSA PORRA AQUI MANDA A ANIMAÇÃO PRO CLIENTE!
        this.entityData.set(ANIMATION, animation);
        System.out.println("Setando animação para: " + animation);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        // ORDEM IMPORTA! procedure primeiro!
        data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
        data.add(new AnimationController<>(this, "movement", 3, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 1, this::attackingPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}