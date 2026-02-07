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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.TagKey;
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
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;

import net.mcreator.morebosses.init.MorebossesModEntities;
import net.mcreator.morebosses.entity.LamentBombEntity;

import java.util.List;
import java.util.EnumSet;

public class ArchdukeLytherionEntity extends Monster implements GeoEntity {
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(ArchdukeLytherionEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(ArchdukeLytherionEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ArchdukeLytherionEntity.class, EntityDataSerializers.STRING);
    
    public static final TagKey<Block> IGNORES_TAG = TagKey.create(Registries.BLOCK, new ResourceLocation("morebosses", "lytherion_ignores"));

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public String animationprocedure = "empty";
    
    // Boss Bar configurada para Azul e Progressiva
    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.BLUE, ServerBossEvent.BossBarOverlay.PROGRESS);

    public ArchdukeLytherionEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(MorebossesModEntities.ARCHDUKE_LYTHERION.get(), world);
    }

    public ArchdukeLytherionEntity(EntityType<ArchdukeLytherionEntity> type, Level world) {
        super(type, world);
        xpReward = 32;
        setNoAi(false);
        setMaxUpStep(1f);
        setPersistenceRequired();
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHOOT, false);
        this.entityData.define(ANIMATION, "undefined");
        this.entityData.define(TEXTURE, "arch_lytherion");
    }

    public void setTexture(String texture) { this.entityData.set(TEXTURE, texture); }
    public String getTexture() { return this.entityData.get(TEXTURE); }
    public String getSyncedAnimation() { return this.entityData.get(ANIMATION); }
    
    public void setAnimation(String animation) { 
        this.entityData.set(ANIMATION, animation); 
    }

    // --- CORREÇÃO DE DESYNC DE ANIMAÇÃO ---
    // Este método é CRUCIAL. Ele garante que quando o servidor muda a variável ANIMATION,
    // o cliente atualiza a variável local animationprocedure instantaneamente.
    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (ANIMATION.equals(key)) {
            this.animationprocedure = this.entityData.get(ANIMATION);
        }
    }

    public static void init() {
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new FlyingPathNavigation(this, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new LytherionAttackGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true, false));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.8)); 
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new FloatGoal(this));
    }

    // --- Lógica Principal de Tick e Boss Bar ---
    @Override
    public void aiStep() {
        super.aiStep();
        this.setNoGravity(true);

        // Atualiza a Boss Bar constantemente no servidor
        if (!this.level().isClientSide) {
            this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
        }

        // Lógica de Atravessar blocos e Brilho
        BlockPos pos = this.blockPosition();
        BlockState state = this.level().getBlockState(pos);
        if (state.is(IGNORES_TAG)) {
            this.noPhysics = true; 
            this.addEffect(new MobEffectInstance(MobEffects.GLOWING, 10, 0, false, false));
        } else {
            this.noPhysics = false;
        }
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
    }

    // --- Ataques ---

    public void performConeAttack() {
        double range = 2.5D; 
        Vec3 src = this.position().add(0, this.getEyeHeight() * 0.5, 0);
        Vec3 look = this.getViewVector(1.0F);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(range));
        for (LivingEntity target : targets) {
            if (target != this) {
                Vec3 targetCenter = target.position().add(0, target.getEyeHeight() * 0.5, 0);
                Vec3 targetVec = targetCenter.subtract(src);
                if (targetVec.length() <= range && look.dot(targetVec.normalize()) > 0.5) {
                    if (canSeeTargetThroughWalls(src, targetCenter)) {
                        target.hurt(this.damageSources().mobAttack(this), (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5f);
                    }
                }
            }
        }
    }

    private boolean canSeeTargetThroughWalls(Vec3 start, Vec3 end) {
        Vec3 currentStart = start;
        for (int i = 0; i < 5; i++) {
            BlockHitResult hit = this.level().clip(new ClipContext(currentStart, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (hit.getType() == HitResult.Type.MISS) return true;
            if (hit.getType() == HitResult.Type.BLOCK) {
                if (this.level().getBlockState(hit.getBlockPos()).is(IGNORES_TAG)) {
                    currentStart = hit.getLocation().add(end.subtract(start).normalize().scale(0.1));
                    continue;
                }
                return false;
            }
        }
        return false;
    }

    public void performRangedAttack(LivingEntity target) {
        if (target == null) return;
        LamentBombEntity projectile = new LamentBombEntity(MorebossesModEntities.LAMENT_BOMB.get(), this.level());
        double spawnX = this.getX() + this.getViewVector(1.0F).x;
        double spawnY = this.getY() + this.getEyeHeight();
        double spawnZ = this.getZ() + this.getViewVector(1.0F).z;
        projectile.moveTo(spawnX, spawnY, spawnZ, this.getYRot(), this.getXRot());
        double d0 = target.getX() - spawnX;
        double d1 = target.getY(0.33D) - spawnY;
        double d2 = target.getZ() - spawnZ;
        projectile.shoot(d0, d1 + Math.sqrt(d0 * d0 + d2 * d2) * 0.2D, d2, 1.5F, 1.0F);
        this.level().addFreshEntity(projectile);
        this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.ghast.warn")), 1.0F, 1.0F);
    }

    @Override public MobType getMobType() { return MobType.UNDEAD; }
    @Override public boolean removeWhenFarAway(double d) { return false; }
    @Override public SoundEvent getHurtSound(DamageSource ds) { return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.hurt")); }
    @Override public SoundEvent getDeathSound() { return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.death")); }
    @Override public boolean causeFallDamage(float l, float d, DamageSource s) { return false; }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.25)
            .add(Attributes.MAX_HEALTH, 520)
            .add(Attributes.ARMOR, 30)
            .add(Attributes.ATTACK_DAMAGE, 9)
            .add(Attributes.FOLLOW_RANGE, 64)
            .add(Attributes.FLYING_SPEED, 0.3);
    }

    // --- ANIMAÇÕES ---
    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if (!this.onGround() || event.isMoving()) return event.setAndContinue(RawAnimation.begin().thenLoop("fly"));
            return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.STOP;
    }

    private PlayState procedurePredicate(AnimationState event) {
        if (!this.animationprocedure.equals("empty") && !this.animationprocedure.equals("undefined")) {
            return event.setAndContinue(RawAnimation.begin().thenPlay(this.animationprocedure));
        }
        return PlayState.STOP;
    }

    @Override public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
        data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return this.cache; }

    // --- GOAL DE ATAQUE CORRIGIDO ---
    class LytherionAttackGoal extends Goal {
        private final ArchdukeLytherionEntity entity;
        private int attackTimer;
        private int attackDuration;

        public LytherionAttackGoal(ArchdukeLytherionEntity entity) {
            this.entity = entity;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override public boolean canUse() { return this.entity.getTarget() != null; }
        @Override public boolean canContinueToUse() { return canUse(); }
        
        @Override
        public void tick() {
            LivingEntity target = this.entity.getTarget();
            if (target == null) return;
            this.entity.getLookControl().setLookAt(target, 30F, 30F);
            double distSqr = this.entity.distanceToSqr(target);

            // Se está no meio de um ataque
            if (this.attackDuration > 0) {
                this.attackDuration--;
                
                // --- TIMING AGRESSIVO (Aplica dano mais cedo) ---
                
                // Animação Lateral (Duração 20): Dano no tick 15 (após 5 ticks/0.25s)
                if (this.entity.animationprocedure.equals("lateral") && this.attackDuration == 15) {
                    this.entity.performConeAttack();
                }
                
                // Animação Poc (Duração 20): Dano no tick 15 (após 5 ticks/0.25s)
                if (this.entity.animationprocedure.equals("poc") && this.attackDuration == 15) {
                    this.entity.doHurtTarget(target);
                }
                
                // Animação Shot (Duração 30): Disparo no tick 22 (após 8 ticks/0.4s)
                if (this.entity.animationprocedure.equals("shot") && this.attackDuration == 22) {
                    this.entity.performRangedAttack(target);
                }
                
                this.entity.getNavigation().stop();
                
                // Fim da animação
                if (this.attackDuration == 0) {
                    this.entity.animationprocedure = "empty";
                    if (!this.entity.level().isClientSide()) {
                        this.entity.setAnimation("empty");
                    }
                }
                return;
            }

            // Movimentação Padrão
            if (distSqr > 6.0D) {
                this.entity.getNavigation().moveTo(target, 1.2D);
            } else {
                this.entity.getNavigation().stop();
            }

            // Iniciar novos ataques
            if (--this.attackTimer <= 0) {
                this.attackTimer = 40; // Cooldown entre ataques
                if (distSqr <= 9.0D) {
                    if (this.entity.getRandom().nextBoolean()) startAttack("poc", 20);
                    else startAttack("lateral", 20);
                } else if (distSqr <= 25.0D) {
                    startAttack("lateral", 20);
                } else {
                    startAttack("shot", 30);
                }
            }
        }

        private void startAttack(String anim, int dur) {
            this.attackDuration = dur;
            this.entity.animationprocedure = anim;
            if (!this.entity.level().isClientSide) {
                this.entity.setAnimation(anim);
            }
        }
    }
}