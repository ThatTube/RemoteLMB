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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import net.mcreator.morebosses.init.MorebossesModEntities;

import java.util.EnumSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SlagtioTheMightyEntity extends Monster implements GeoEntity {
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(SlagtioTheMightyEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(SlagtioTheMightyEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(SlagtioTheMightyEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Boolean> AWAKE = SynchedEntityData.defineId(SlagtioTheMightyEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public String animationprocedure = "empty";
    public int dashTimer = 0;
    public boolean isImpaling = false;

    public SlagtioTheMightyEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(MorebossesModEntities.SLAGTIO_THE_MIGHTY.get(), world);
    }

    public SlagtioTheMightyEntity(EntityType<SlagtioTheMightyEntity> type, Level world) {
        super(type, world);
        xpReward = 100;
        setNoAi(false);
        setMaxUpStep(2f);
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHOOT, false);
        this.entityData.define(ANIMATION, "undefined");
        this.entityData.define(TEXTURE, "broken_knight");
        this.entityData.define(AWAKE, false);
    }

    public String getSyncedAnimation() {
        return this.entityData.get(ANIMATION);
    }

    public void setAnimation(String animation) {
        this.entityData.set(ANIMATION, animation);
        this.animationprocedure = animation;
    }

    public boolean isAwake() {
        return this.entityData.get(AWAKE);
    }

    public void setAwake(boolean awake) {
        this.entityData.set(AWAKE, awake);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SlagtioBossAttackGoal(this));
        
        // Target através de paredes e de longe
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true, false) {
            @Override
            public boolean canUse() { return super.canUse() && isAwake(); }
        });
        
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1) {
            @Override
            public boolean canUse() { return super.canUse() && isAwake(); }
        });
        this.targetSelector.addGoal(4, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() { return super.canUse() && isAwake(); }
        });
        this.goalSelector.addGoal(6, new FloatGoal(this));
    }

    @Override
    public MobType getMobType() { return MobType.UNDEFINED; }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) { return false; }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.anvil.place"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.iron_golem.death"));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!isAwake() && !source.is(DamageTypes.GENERIC_KILL)) return false;
        if (source.is(DamageTypes.FALL)) return false;
        if (source.is(DamageTypes.CACTUS)) return false;
        return super.hurt(source, amount);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        ResourceLocation itemID = ForgeRegistries.ITEMS.getKey(itemstack.getItem());
        if (!this.isAwake() && itemID != null && itemID.toString().equals("morebosses:broken_idol")) {
            if (!this.level().isClientSide) {
                this.setAwake(true);
                this.setAnimation("wake");
                if (!player.isCreative()) itemstack.shrink(1);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Texture", this.getTexture());
        compound.putBoolean("IsAwake", this.isAwake());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Texture")) this.setTexture(compound.getString("Texture"));
        if (compound.contains("IsAwake")) this.setAwake(compound.getBoolean("IsAwake"));
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
        
        // --- Lógica da Empalada ---
        if (this.isImpaling && this.isAwake()) {
            if (this.dashTimer > 0) this.dashTimer--;
            
            if (this.dashTimer < 5 && this.dashTimer > 0) {
                Vec3 look = this.getLookAngle();
                AABB attackBox = this.getBoundingBox().expandTowards(look.scale(1.5)).inflate(0.5, 0.0, 0.5);
                
                // Quebrar Blocos
                BlockPos min = new BlockPos((int)attackBox.minX, (int)attackBox.minY, (int)attackBox.minZ);
                BlockPos max = new BlockPos((int)attackBox.maxX, (int)attackBox.maxY, (int)attackBox.maxZ);

                for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
                    if (pos.getY() < this.blockPosition().getY()) continue; // Não quebra chão
                    BlockState state = this.level().getBlockState(pos);
                    float resistance = state.getExplosionResistance(this.level(), pos, null);
                    if (!state.isAir() && resistance >= 0 && resistance <= 22) {
                        this.level().destroyBlock(pos, true);
                    }
                }
                
                // Dano em Entidades (Empalada)
                List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, attackBox);
                MobEffect sampleEffect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("morebosses", "armor_breach"));

                for (LivingEntity e : entities) {
                    if (e != this) {
                        // 1. Quebra Escudo
                        if (e instanceof Player p && p.isBlocking()) {
                            p.disableShield(true);
                        }

                        // 2. Aplica Dano
                        if (e.hurt(this.damageSources().mobAttack(this), 12.0f)) {
                            // 3. Aplica Knockback
                            e.knockback(1.5, this.getX() - e.getX(), this.getZ() - e.getZ());
                            
                            // 4. Aplica Efeito "morebosses:sample"
                            if (sampleEffect != null) {
                                e.addEffect(new MobEffectInstance(sampleEffect, 100, 0)); // 5 segundos
                            }
                        }
                    }
                }
            } else if (this.dashTimer <= 0) {
                this.isImpaling = false;
            }
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale((float) 1);
    }

    public static void init() { }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25);
        builder = builder.add(Attributes.MAX_HEALTH, 200);
        builder = builder.add(Attributes.ARMOR, 12);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 12);
        builder = builder.add(Attributes.FOLLOW_RANGE, 64);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
        return builder;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 4, event -> {
            if (!isAwake()) return event.setAndContinue(RawAnimation.begin().thenLoop("sleep"));
            if (this.animationprocedure.equals("empty")) {
                if (event.isMoving()) return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
                return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            }
            return PlayState.STOP;
        }));
        data.add(new AnimationController<>(this, "procedure", 4, event -> {
            if (!this.animationprocedure.equals("empty") && !this.animationprocedure.equals("undefined")) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
                if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
                    this.animationprocedure = "empty";
                    event.getController().forceAnimationReset();
                }
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return this.cache; }

    public void setTexture(String texture) { this.entityData.set(TEXTURE, texture); }
    public String getTexture() { return this.entityData.get(TEXTURE); }

    // --- IA DE COMBATE ---
    static class SlagtioBossAttackGoal extends Goal {
        private final SlagtioTheMightyEntity boss;
        private int attackCooldown = 0;
        private int comboWindow = 0;
        private AttackType currentAttack = AttackType.NONE;

        enum AttackType { NONE, SLASH, SLASH360, EMPALADA, CORTE, CORTEBAIXO }

        public SlagtioBossAttackGoal(SlagtioTheMightyEntity boss) {
            this.boss = boss;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override public boolean canUse() { return boss.isAwake() && boss.getTarget() != null && !boss.isImpaling; }

        @Override
        public void tick() {
            LivingEntity targetEntity = boss.getTarget();
            if (targetEntity == null) return;
            
            boss.getLookControl().setLookAt(targetEntity, 30.0F, 30.0F);
            double distSqr = boss.distanceToSqr(targetEntity);

            if (attackCooldown > 0) {
                attackCooldown--;
                if (distSqr > 9.0 && !boss.isImpaling) { 
                     boss.getNavigation().moveTo(targetEntity, 1.2);
                }

                if (comboWindow > 0) {
                    comboWindow--;
                    float hpPct = boss.getHealth() / boss.getMaxHealth();
                    if (currentAttack == AttackType.SLASH && hpPct < 0.40f && comboWindow > 5) {
                        startAttack(AttackType.SLASH360);
                        comboWindow = 0;
                        return;
                    }
                    if (currentAttack == AttackType.CORTE && hpPct < 0.67f && comboWindow > 5) {
                        startAttack(AttackType.CORTEBAIXO);
                        comboWindow = 0;
                        return;
                    }
                }
                return;
            }

            if (distSqr > 16.0) { 
                if (boss.getRandom().nextInt(40) == 0) {
                    startAttack(AttackType.EMPALADA);
                } else {
                    boss.getNavigation().moveTo(targetEntity, 1.2);
                }
            } else { 
                boss.getNavigation().stop();
                float hpPct = boss.getHealth() / boss.getMaxHealth();
                int rng = boss.getRandom().nextInt(100);
                
                if (hpPct < 0.70f && rng < 30) startAttack(AttackType.SLASH360);
                else if (rng < 60) startAttack(AttackType.CORTE);
                else startAttack(AttackType.SLASH);
            }
        }

        private void startAttack(AttackType type) {
            this.currentAttack = type;
            Vec3 look = boss.getLookAngle();
            LivingEntity targetEntity = boss.getTarget();

            switch (type) {
                case SLASH:
                    boss.setAnimation("slash");
                    boss.setDeltaMovement(look.scale(0.8));
                    this.attackCooldown = 25; this.comboWindow = 15;
                    // FIX DO SLASH: Novo método de detecção
                    performSlashCone(3.0, 110, 6.0f);
                    break;
                case SLASH360:
                    boss.setAnimation("slash360");
                    boss.setDeltaMovement(look.scale(0.8));
                    this.attackCooldown = 30;
                    performAreaDamage(3.5, 10.0f);
                    break;
                case EMPALADA:
                    boss.setAnimation("empalada");
                    boss.isImpaling = true;
                    boss.dashTimer = 20; 
                    boss.setDeltaMovement(Vec3.ZERO);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                             if(!boss.isDeadOrDying()) 
                                 boss.setDeltaMovement(boss.getLookAngle().scale(2.5));
                        }
                    }, 250);
                    this.attackCooldown = 50;
                    break;
                case CORTE:
                    boss.setAnimation("corte");
                    this.attackCooldown = 20; this.comboWindow = 15;
                    if (targetEntity != null && boss.doHurtTarget(targetEntity)) 
                        targetEntity.knockback(2.0, boss.getX() - targetEntity.getX(), boss.getZ() - targetEntity.getZ());
                    break;
                case CORTEBAIXO:
                    boss.setAnimation("cortebaixo");
                    boss.setDeltaMovement(look.scale(0.6));
                    this.attackCooldown = 25;
                    if (targetEntity != null && boss.doHurtTarget(targetEntity)) 
                        targetEntity.knockback(2.0, boss.getX() - targetEntity.getX(), boss.getZ() - targetEntity.getZ());
                    break;
            }
        }

        // --- MÉTODO CORRIGIDO PARA O SLASH ---
        // Usa matemática 2D para ser mais indulgente com a mira
        private void performSlashCone(double range, double angleDeg, float damage) {
            List<LivingEntity> list = boss.level().getEntitiesOfClass(LivingEntity.class, boss.getBoundingBox().inflate(range));
            Vec3 bossLook = boss.getLookAngle();
            Vec3 bossPos = boss.position();

            for (LivingEntity e : list) {
                if (e != boss) {
                    // Vetor do boss até o alvo (ignorando altura Y para o ângulo)
                    Vec3 targetDir = e.position().subtract(bossPos);
                    Vec3 targetDir2D = new Vec3(targetDir.x, 0, targetDir.z).normalize();
                    Vec3 bossLook2D = new Vec3(bossLook.x, 0, bossLook.z).normalize();

                    double dot = bossLook2D.dot(targetDir2D);
                    
                    // Verifica ângulo (2D) e Distância (3D)
                    // Aumentei o range um pouco na verificação final para garantir
                    if (dot > Math.cos(Math.toRadians(angleDeg / 2)) && boss.distanceTo(e) <= range + 1.0) {
                        if (e.hurt(boss.damageSources().mobAttack(boss), damage))
                            e.knockback(1.0, boss.getX() - e.getX(), boss.getZ() - e.getZ());
                    }
                }
            }
        }

        private void performAreaDamage(double range, float damage) {
            for (LivingEntity e : boss.level().getEntitiesOfClass(LivingEntity.class, boss.getBoundingBox().inflate(range))) {
                if (e != boss && e.hurt(boss.damageSources().mobAttack(boss), damage))
                    e.knockback(1.0, boss.getX() - e.getX(), boss.getZ() - e.getZ());
            }
        }
    }
}