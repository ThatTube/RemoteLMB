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
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;

import net.mcreator.morebosses.init.MorebossesModEntities;
import net.mcreator.morebosses.init.MorebossesModMobEffects;

import java.util.List;
import java.util.Optional;

public class SmagmarEntity extends Monster implements GeoEntity {
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(SmagmarEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(SmagmarEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Boolean> IS_ENRAGED = SynchedEntityData.defineId(SmagmarEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.RED, ServerBossEvent.BossBarOverlay.PROGRESS);

    private int attackTimer = 0;

    // =========================================================
    // PULO EM 3 FASES (mecânica do MagmaticChampionEntity)
    // 0 = sem pulo
    // 1 = JUMP_START  → animação de preparação ("jumpcharge"), para no lugar
    // 2 = JUMP_MID    → no ar ("jumploop"), aguarda tocar o chão
    // 3 = JUMP_END    → aterrissou ("jumpend"), aplica dano e espera
    // =========================================================
    private int jumpState = 0;
    private int jumpTimer = 0;

    public String animationprocedure = "empty";

    private AnimationController<SmagmarEntity> mainController;

    public SmagmarEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(MorebossesModEntities.SMAGMAR.get(), world);
    }

    public SmagmarEntity(EntityType<SmagmarEntity> type, Level world) {
        super(type, world);
        xpReward = 100;
        setPersistenceRequired();
        // Escurece o céu e cria névoa como o Wither faz
        this.bossInfo.setVisible(true);
        this.bossInfo.setDarkenScreen(true);
        this.bossInfo.setCreateWorldFog(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIMATION, "empty");
        this.entityData.define(TEXTURE, "smagmar");
        this.entityData.define(IS_ENRAGED, false);
    }

    public String getTexture() {
        return this.entityData.get(TEXTURE);
    }

    public String getSyncedAnimation() {
        return this.entityData.get(ANIMATION);
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25);
        builder = builder.add(Attributes.MAX_HEALTH, 450);
        builder = builder.add(Attributes.ARMOR, 25);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 9);
        builder = builder.add(Attributes.FOLLOW_RANGE, 32);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
        builder = builder.add(Attributes.ATTACK_KNOCKBACK, 0.1);
        return builder;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    @Override
    public void customServerAiStep() {
        super.customServerAiStep();
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());

        if (!this.entityData.get(IS_ENRAGED) && this.getHealth() < this.getMaxHealth() * 0.3) {
            this.entityData.set(IS_ENRAGED, true);
            this.entityData.set(TEXTURE, "smbroke");
            this.setAnimation("kill");
            this.getAttribute(Attributes.ARMOR).setBaseValue(10);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(12);
            this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("morebosses:armorbroke")), 4.0f, 0.75f);
        }

        // Gerencia o pulo em 3 fases — tem prioridade sobre combate comum
        if (jumpState != 0) {
            handleJump();
            return;
        }

        if (this.getTarget() != null && this.isAlive()) {
            handleCombat();
        }
    }

    // ==============================================================
    // PULO EM 3 FASES — portado do MagmaticChampionEntity
    // ==============================================================

    /**
     * Inicia a sequência de pulo.
     * Fase 1: o boss para e carrega o salto ("jumpcharge") por 10 ticks.
     */
    private void startJump(LivingEntity target) {
        if (target == null) return;
        jumpState = 1;
        jumpTimer = 10;
        this.setAnimation("jumpcharge");
        this.getNavigation().stop();
    }

    /**
     * Chamado a cada tick enquanto jumpState != 0.
     * Gerencia as transições entre as 3 fases.
     */
    private void handleJump() {
        jumpTimer--;
        this.getNavigation().stop();

        LivingEntity target = this.getTarget();

        switch (jumpState) {

            // -------------------------------------------------------
            // FASE 1: JUMP_START — parado, animação de preparação
            // Quando o timer zera, lança o boss em direção ao alvo
            // -------------------------------------------------------
            case 1:
                this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
                if (jumpTimer <= 0) {
                    jumpState = 2;
                    jumpTimer = 40;
                    jumpTowardsTarget(target);
                    this.setAnimation("jumploop");
                    if (!this.level().isClientSide()) {
                        spawnJumpParticles();
                    }
                }
                break;

            // -------------------------------------------------------
            // FASE 2: JUMP_MID — no ar, aguarda tocar o chão
            // Quando aterrissa (onGround), vai para a fase 3
            // -------------------------------------------------------
            case 2:
                // Detecta o pouso: deve estar no chão E o timer < 35
                // (os primeiros 5 ticks do lançamento o boss ainda está "no chão")
                if (this.onGround() && jumpTimer < 35) {
                    jumpState = 3;
                    jumpTimer = 15;
                    this.setAnimation("jumpend");
                    performJumpLandingEffects();
                }
                // Segurança: se o timer expirou e ainda não pousou, cancela
                if (jumpTimer <= 0) {
                    jumpState = 0;
                    attackTimer = 10;
                }
                break;

            // -------------------------------------------------------
            // FASE 3: JUMP_END — animação de aterrissagem, sem movimento
            // Quando termina, volta ao combate normal
            // -------------------------------------------------------
            case 3:
                this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
                if (jumpTimer <= 0) {
                    jumpState = 0;
                    attackTimer = 20;
                }
                break;
        }
    }

    /**
     * Calcula e aplica a força de salto em direção ao alvo.
     * Lógica idêntica ao MagmaticChampionEntity#jumpTowardsTarget.
     */
    private void jumpTowardsTarget(LivingEntity target) {
        if (target == null) return;
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);
        if (distance > 0) {
            dx /= distance;
            dz /= distance;
        }
        double jumpDistance = Math.min(distance * 0.9, 10.0);
        double verticalForce = 0.6 + (jumpDistance / 20.0);
        double horizontalForce = jumpDistance * 0.2;
        this.setDeltaMovement(dx * horizontalForce, verticalForce, dz * horizontalForce);
    }

    /** Partículas no momento do salto. */
    private void spawnJumpParticles() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;
        serverLevel.sendParticles(ParticleTypes.LAVA, this.getX(), this.getY(), this.getZ(), 40, 2.0, 0.3, 2.0, 0.1);
        serverLevel.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 30, 1.5, 0.5, 1.5, 0.05);
    }

    /** Partículas na aterrissagem. */
    private void spawnLandingParticles() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;
        serverLevel.sendParticles(ParticleTypes.LAVA, this.getX(), this.getY() + 0.5, this.getZ(), 150, 4.0, 0.5, 4.0, 0.15);
        serverLevel.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY() + 1, this.getZ(), 80, 5.0, 1.0, 5.0, 0.1);
        serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX(), this.getY() + 2, this.getZ(), 50, 3.0, 2.0, 3.0, 0.05);
    }

    /**
     * Efeitos ao pousar: som, dano em área e tremor de tela.
     * Usa os valores de dano originais do Smagmar (3.0f, raio 4).
     */
    private void performJumpLandingEffects() {
        this.playSound(SoundEvents.GENERIC_EXPLODE, 2.0F, 0.5F);
        applyConeDamage(360, 4, 3.0f, true);
        if (!this.level().isClientSide()) {
            spawnLandingParticles();
            causeScreenShakeToNearbyPlayers();
        }
    }

    /**
     * Aplica um leve knockback e lentidão nos jogadores próximos ao pousar.
     * Portado do MagmaticChampionEntity.
     */
    private void causeScreenShakeToNearbyPlayers() {
        if (this.level().isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) this.level();
        List<ServerPlayer> nearbyPlayers = serverLevel.getPlayers(player -> player.distanceToSqr(this) < 36);
        for (ServerPlayer player : nearbyPlayers) {
            double distance = Math.sqrt(player.distanceToSqr(this));
            float intensity = (float) Math.max(0.15, 1.2 - (distance / 18.0));
            player.knockback(0.15 * intensity, player.getX() - this.getX(), player.getZ() - this.getZ());
            if (distance < 12) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 15, 3, false, false));
            }
        }
    }

    // ==============================================================
    // COMBATE NORMAL (inalterado)
    // ==============================================================

    private void handleCombat() {
        LivingEntity target = this.getTarget();
        double distance = this.distanceTo(target);

        if (attackTimer > 0) attackTimer--;

        // Só inicia o pulo se não há pulo em andamento (jumpState == 0 já garantido acima)
        if (distance > 10 && attackTimer == 0) {
            startJump(target);
        }

        if (distance < 6 && attackTimer == 0) {
            int rand = this.random.nextInt(100);
            if (rand < 15)      executeAttack("attack1", 1);
            else if (rand < 30) executeAttack("attack2", 2);
            else if (rand < 45) executeAttack("attack3", 3);
            else if (rand < 60) executeAttack("attack4", 4);
            else if (rand < 75) executeAttack("dash",    5);
            else if (rand < 85) executeAttack("roar",    6);
            else                executeAttack("stomp",   7);
        }
    }

    private void executeAttack(String anim, int type) {
        this.setAnimation(anim);
        this.attackTimer = 30;

        switch (type) {
            case 1 -> {
                this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("morebosses:swing1")), 4.0f, 0.75f);
                applyConeDamage(120, 5, 9.0f, false);
            }
            case 2 -> {
                this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("morebosses:swing2")), 4.0f, 0.75f);
                applyConeDamage(360, 5, 10.0f, false);
            }
            case 3 -> {
                this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("morebosses:swing1")), 4.0f, 0.75f);
                applySingleDamage(9.0f);
            }
            case 4 -> {
                this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("morebosses:swing2")), 4.0f, 0.75f);
                applySingleDamage(9.5f);
            }
            case 5 -> {
                this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("morebosses:swing1")), 4.0f, 0.75f);
                Vec3 look = this.getLookAngle();
                this.setDeltaMovement(look.x * 1.2, 0.1, look.z * 1.2);
                applyConeDamage(120, 4, 12.0f, false);
            }
            case 6 -> {
                this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("morebosses:swing2")), 4.0f, 0.75f);
                applyConeDamage(120, 6, 8.0f, true);
            }
            case 7 -> {
                this.playSound(SoundEvents.GENERIC_EXPLODE, 1.5F, 0.5F);
                executeStomp();
            }
        }
    }

    private void applyConeDamage(float angle, double range, float damage, boolean knockback) {
        List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(range));
        for (LivingEntity target : entities) {
            if (target != this) {
                Vec3 vecToTarget = target.position().subtract(this.position()).normalize();
                double dot = this.getLookAngle().dot(vecToTarget);
                if (dot > Math.cos(Math.toRadians(angle / 2)) || angle >= 360) {
                    dealSmagmarDamage(target, damage);
                    if (knockback) target.knockback(1.5, -vecToTarget.x, -vecToTarget.z);
                }
            }
        }
    }

    private void applySingleDamage(float damage) {
        if (this.getTarget() != null) dealSmagmarDamage(this.getTarget(), damage);
    }

    private void dealSmagmarDamage(LivingEntity target, float amount) {
        target.hurt(this.damageSources().mobAttack(this), amount);
        target.addEffect(new MobEffectInstance(MorebossesModMobEffects.HELLISH_BURN.get(), 100, 0));
    }

    private void executeStomp() {
        applyConeDamage(360, 5, 8.0f, true);
        if (!this.level().isClientSide()) {
            ServerLevel world = (ServerLevel) this.level();
            for (int i = 0; i < 8; i++) {
                double ang = Math.toRadians(i * 45);
                Entity pit = MorebossesModEntities.MAGMA_PIT.get().create(world);
                if (pit != null) {
                    pit.moveTo(this.getX() + Math.cos(ang) * 3, this.getY(), this.getZ() + Math.sin(ang) * 3);
                    world.addFreshEntity(pit);
                }
            }
            world.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 40, 2, 0.5, 2, 0.1);
        }
    }

    // ==============================================================
    // tick() — lógica de pulo REMOVIDA (agora está em handleJump())
    // Mantido apenas para comportamento base do Minecraft
    // ==============================================================
    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        if (!this.level().isClientSide()) {
            this.bossInfo.removeAllPlayers();
            this.bossInfo.setDarkenScreen(false);
            this.bossInfo.setCreateWorldFog(false);
            this.bossInfo.setVisible(false);
        }
        super.remove(reason);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide && this.random.nextFloat() < 0.20 && attackTimer == 0 && jumpState == 0) {
            boolean right = this.random.nextBoolean();
            this.setAnimation(right ? "dashr" : "dashl");
            Vec3 side = this.getLookAngle().yRot(right ? (float) Math.PI / 2 : (float) -Math.PI / 2);
            this.setDeltaMovement(side.scale(0.8));
            return false;
        }
        return super.hurt(source, amount);
    }
    // ==============================================================
    // GECKOLIB (inalterado)
    // ==============================================================

    public void setAnimation(String anim) {
        this.entityData.set(ANIMATION, anim);
        this.animationprocedure = anim;
        if (this.mainController != null) {
            this.mainController.forceAnimationReset();
            this.mainController.triggerableAnim(anim, RawAnimation.begin().thenPlay(anim));
        }
    }

    private PlayState controller(AnimationState<SmagmarEntity> event) {
        if (this.mainController == null) {
            this.mainController = event.getController();
        }

        // Lê entityData (sincronizado servidor->cliente) em vez do campo local
        String anim = this.getSyncedAnimation();
        this.animationprocedure = anim;

        if (!anim.equals("empty")) {
            if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
                this.entityData.set(ANIMATION, "empty");
                this.animationprocedure = "empty";
                event.getController().forceAnimationReset();
            } else {
                return event.setAndContinue(RawAnimation.begin().thenPlay(anim));
            }
        }

        if (anim.equals("empty")) {
            if (event.isMoving()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            } else {
                return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            }
        }

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        AnimationController<SmagmarEntity> controller = new AnimationController<>(this, "main", 2, this::controller);
        data.add(controller);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    // ==============================================================
    // BOSS BAR (inalterado)
    // ==============================================================

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

    public static void init() {
    }
}