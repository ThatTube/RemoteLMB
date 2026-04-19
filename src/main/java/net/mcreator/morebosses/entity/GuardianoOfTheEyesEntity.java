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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.monster.Monster;
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
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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

import net.mcreator.morebosses.init.MorebossesModEntities;

import java.util.List;

public class GuardianoOfTheEyesEntity extends Monster implements GeoEntity {
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(GuardianoOfTheEyesEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(GuardianoOfTheEyesEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(GuardianoOfTheEyesEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Float> HEAD_YAW = SynchedEntityData.defineId(GuardianoOfTheEyesEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> HEAD_PITCH = SynchedEntityData.defineId(GuardianoOfTheEyesEntity.class, EntityDataSerializers.FLOAT);
    
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.GREEN, ServerBossEvent.BossBarOverlay.PROGRESS);
    
    // Variáveis de Controle de Ataque
    private int attackTimer = 0;
    private int cooldownTimer = 0;
    private int attackState = 0; 
    // Estados: 0=Idle, 1=PunchA, 2=PunchB, 3=Slam, 4=LaserCharge, 5=LaserActive, 6=Teleporting, 7=Blocking
    
    // Referência ao laser ativo
    private EnderLaserBeamEntity activeLaser = null;
    
    // Ângulos da cabeça para animação
    private float headYaw = 0;
    private float headPitch = 0;
    private float prevHeadYaw = 0;
    private float prevHeadPitch = 0;

    // Constantes do Death Laser (baseadas no Harbinger)
    private static final int DEATH_LASER_FIRE_TICK = 18;       // Tick da animação de carga em que o laser é disparado
    private static final int DEATH_LASER_BEAM_DURATION = 60;   // Duração do feixe em ticks
    private static final int DEATH_LASER_COOLDOWN = 120;       // Cooldown após o ataque
    
    // Campo para controlar o fim do laser (tempo em ticks do mundo)
    private int deathLaserEndTick = 0;
    
    public String animationprocedure = "empty";
    String prevAnim = "empty";

    public GuardianoOfTheEyesEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(MorebossesModEntities.GUARDIANO_OF_THE_EYES.get(), world);
    }

    public GuardianoOfTheEyesEntity(EntityType<GuardianoOfTheEyesEntity> type, Level world) {
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
        this.entityData.define(ANIMATION, "undefined");
        this.entityData.define(TEXTURE, "guardiane");
        this.entityData.define(HEAD_YAW, 0f);
        this.entityData.define(HEAD_PITCH, 0f);
    }

    public void setTexture(String texture) {
        this.entityData.set(TEXTURE, texture);
    }

    public String getTexture() {
        return this.entityData.get(TEXTURE);
    }
    
    public float getHeadYaw() {
        return this.entityData.get(HEAD_YAW);
    }
    
    public void setHeadYaw(float yaw) {
        this.entityData.set(HEAD_YAW, yaw);
    }
    
    public float getHeadPitch() {
        return this.entityData.get(HEAD_PITCH);
    }
    
    public void setHeadPitch(float pitch) {
        this.entityData.set(HEAD_PITCH, pitch);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new BossAttackGoal(this)); 
        this.targetSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<>(this, net.minecraft.world.entity.player.Player.class, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new FloatGoal(this));
    }

    // --- LÓGICA DE AI CUSTOMIZADA E ATAQUES ---

    @Override
    public void customServerAiStep() {
        super.customServerAiStep();
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());

        LivingEntity target = this.getTarget();
        
        // Atualizar rotação da cabeça para seguir o alvo
        updateHeadRotation(target);
        
        if (target != null && !this.isDeadOrDying()) {
            // Rotação do corpo (mais lenta que a cabeça)
            double d0 = target.getX() - this.getX();
            double d1 = target.getZ() - this.getZ();
            
            // Rotação do corpo só muda quando não está em ataques especiais
            if (attackState != 5) { // Não vira o corpo durante o laser
                float bodyYaw = (float)(Mth.atan2(d1, d0) * (180D / Math.PI)) - 90.0F;
                this.setYRot(bodyYaw);
                this.yBodyRot = bodyYaw;
                this.yBodyRotO = bodyYaw;
            }
            
            // Se estiver longe e fora de um estado de ataque, força o caminho até o player
            if (this.attackState == 0 && this.tickCount % 10 == 0) {
                this.getNavigation().moveTo(target, 1.25);
            }
        }
        
        // Verificar se o laser ainda existe e está ativo
        if (activeLaser != null && (!activeLaser.isAlive() || !activeLaser.on)) {
            activeLaser = null;
        }
        
        // Decrementa cooldown global
        if (cooldownTimer > 0) cooldownTimer--;

        // Se estiver executando um ataque, processa a lógica dele
        if (attackState != 0) {
            tickAttackLogic();
        } else if (this.getTarget() != null && cooldownTimer == 0 && !this.isDeadOrDying()) {
            chooseNextAttack();
        }
    }
    
    private void updateHeadRotation(LivingEntity target) {
        prevHeadYaw = headYaw;
        prevHeadPitch = headPitch;
        
        if (target != null) {
            // Calcular ângulos para a cabeça olhar para o alvo
            double d0 = target.getX() - this.getX();
            double d1 = target.getY() + target.getEyeHeight() - (this.getY() + 6.2); // Altura do laser
            double d2 = target.getZ() - this.getZ();
            
            double horizontalDistance = Math.sqrt(d0 * d0 + d2 * d2);
            
            float targetHeadYaw = (float)(Mth.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
            float targetHeadPitch = (float)(-Mth.atan2(d1, horizontalDistance) * (180D / Math.PI));
            
            // Suavizar a rotação da cabeça
            headYaw += Mth.wrapDegrees(targetHeadYaw - headYaw) * 0.3f;
            headPitch += (targetHeadPitch - headPitch) * 0.3f;
            
            // Limitar o pitch da cabeça
            headPitch = Mth.clamp(headPitch, -60, 60);
        } else {
            // Voltar gradualmente para a posição neutra
            headYaw += (0 - headYaw) * 0.1f;
            headPitch += (0 - headPitch) * 0.1f;
        }
        
        // Sincronizar com o cliente
        setHeadYaw(headYaw);
        setHeadPitch(headPitch);
    }

    private void chooseNextAttack() {
        LivingEntity target = this.getTarget();
        if (target == null) return;

        double distanceSq = this.distanceToSqr(target);
        double random = this.random.nextDouble();

        if (distanceSq <= 25.0) {
            // Aumentada chance de soco (antes 0.6, agora 0.85)
            if (random < 0.85) startPunchA();
            else startSlam();
        } 
        else if (distanceSq > 25.0 && distanceSq < 256.0) {
            if (random < 0.5) startLaserAttack();
            else if (random < 0.8) startTeleport();
        }
        else {
            startTeleport();
        }
    }

    private void tickAttackLogic() {
        attackTimer++;
        LivingEntity target = this.getTarget();
        
        switch (attackState) {
            case 1: // Punch A
                if (attackTimer == 8) { 
                    // Aumentado alcance e ângulo para facilitar acerto
                    performConeDamage(7.0f, 90f, (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                }
                if (attackTimer >= 25) { 
                    if (target != null && this.distanceToSqr(target) <= 36.0 && Math.random() < 0.9) { // Aumentada chance de combo
                        startPunchB();
                    } else {
                        resetAttack(5); // Reduzido cooldown
                    }
                }
                break;

            case 2: // Punch B
                if (attackTimer == 8) {
                    // Aumentado alcance e ângulo para facilitar acerto
                    performConeDamage(7.5f, 90f, (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) + 4f);
                }
                if (attackTimer >= 25) {
                    resetAttack(10); // Reduzido cooldown
                }
                break;

            case 3: // Ground Slam
                if (attackTimer == 15) {
                    performLineDamage(7, 3, 25f);
                    this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("morebosses:enderslam")), 1.0f, 1.0f);
                }
                if (attackTimer >= 30) resetAttack(40);
                break;

            case 4: // Laser Charge
                if (attackTimer == DEATH_LASER_FIRE_TICK) {
                    spawnDeathLaserBeam();
                    this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("morebosses:enderbombshoot")), 4.0f, 0.75f);
                }
                if (attackTimer >= 25) {
                    attackState = 5;
                    attackTimer = 0;
                    animationprocedure = "laserloop";
                    setAnimation("laserloop");
                }
                break;

            case 5: // Laser Active
                if (target != null) {
                    this.getLookControl().setLookAt(target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(), 6, 90);
                    this.lookAt(target, 30, 30);
                }
                if (this.tickCount >= deathLaserEndTick || activeLaser == null) {
                    resetAttack(DEATH_LASER_COOLDOWN);
                }
                break;
                
            case 6: // Teleport
                if (attackTimer == 10) performTeleport();
                if (attackTimer >= 20) resetAttack(10);
                break;

            case 7: // Block
                if (attackTimer >= 15) resetAttack(5);
                break;
        }
    }

    // --- MÉTODOS DE INÍCIO DE ATAQUE ---

    private void startPunchA() {
        this.attackState = 1;
        this.attackTimer = 0;
        this.animationprocedure = "attack_a";
        setAnimation("attack_a");
    }

    private void startPunchB() {
        this.attackState = 2;
        this.attackTimer = 0;
        this.animationprocedure = "attack_b";
        setAnimation("attack_b");
    }

    private void startSlam() {
        this.attackState = 3;
        this.attackTimer = 0;
        this.animationprocedure = "groundslam";
        setAnimation("groundslam");
    }

    private void startLaserAttack() {
        this.attackState = 4;
        this.attackTimer = 0;
        this.animationprocedure = "lasercharge";
        setAnimation("lasercharge");
    }

    private void startLaserActive() {
        this.attackState = 5;
        this.attackTimer = 0;
        this.animationprocedure = "laserloop";
        setAnimation("laserloop");
        
        // Spawn do laser
        spawnDeathLaserBeam();
    }

    private void startTeleport() {
        this.attackState = 6;
        this.attackTimer = 0;
        this.animationprocedure = "summon";
        setAnimation("summon");
    }
    
    private void triggerBlock() {
        this.attackState = 7;
        this.attackTimer = 0;
        this.animationprocedure = "block";
        setAnimation("block");
    }

    private void resetAttack(int cooldown) {
        this.attackState = 0;
        this.attackTimer = 0;
        this.cooldownTimer = cooldown;
        this.animationprocedure = "empty";
        setAnimation("empty");
        
        // Remover o laser se existir
        if (activeLaser != null) {
            activeLaser.on = false;
            activeLaser = null;
        }
    }

    // --- MÉTODO DO LASER BEAM ---
    
    private void spawnDeathLaserBeam() {
        if (this.level().isClientSide) return;
        
        // Remove o laser anterior se existir
        if (activeLaser != null) {
            activeLaser.on = false;
            activeLaser = null;
        }
        
        // Parâmetros do Death Laser
        float baseDamage = 8.0f;
        float hpDamagePercentage = 10.0f;
        int duration = DEATH_LASER_BEAM_DURATION;
        double laserHeight = 7.2;
        
        // Usa a rotação da cabeça para apontar
        float headYaw = this.yHeadRot;
        float headPitch = this.getXRot();
        float laserYaw = (float) Math.toRadians(headYaw + 90);
        float laserPitch = (float) Math.toRadians(-headPitch);
        
        EnderLaserBeamEntity laser = new EnderLaserBeamEntity(
            this.level(),
            this,
            this.getX(),
            this.getY() + laserHeight,
            this.getZ(),
            laserYaw,
            laserPitch,
            duration,
            baseDamage,
            hpDamagePercentage
        );
        
        deathLaserEndTick = this.tickCount + duration;
        
        this.level().addFreshEntity(laser);
        activeLaser = laser;
    }

    // --- LÓGICA DE DANO ---

    private void performConeDamage(float range, float angle, float damage) {
        if (this.level().isClientSide) return;
        
        AABB searchBox = this.getBoundingBox().inflate(range, range/2, range);
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, searchBox);
        Vec3 lookDir = this.getViewVector(1.0F).normalize();
        
        for (LivingEntity e : list) {
            if (e != this && e.isAlive()) {
                Vec3 targetDir = e.position().subtract(this.position()).normalize();
                double dot = lookDir.dot(targetDir);
                double actualDistance = this.distanceTo(e);
                
                if (dot > Math.cos(Math.toRadians(angle / 2)) && actualDistance <= range) {
                    e.hurt(this.damageSources().mobAttack(this), damage);
                    e.knockback(0.8, this.getX() - e.getX(), this.getZ() - e.getZ());
                    
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SWEEP_ATTACK, 
                            e.getX(), e.getY() + 1, e.getZ(), 10, 0.2, 0.2, 0.2, 0.1);
                    }
                }
            }
        }
    }

    private void performLineDamage(double distance, double width, float damage) {
        if (this.level().isClientSide) return;
        Vec3 viewVector = this.getViewVector(1.0F);
        Vec3 startPos = this.position().add(0, 1, 0);
        Vec3 endPos = startPos.add(viewVector.scale(distance));
        
        AABB damageBox = new AABB(startPos, endPos).inflate(width, 2, width);
        
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, damageBox);
        for (LivingEntity e : list) {
            if (e != this && e.isAlive()) {
                e.hurt(this.damageSources().mobAttack(this), damage);
                e.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 60, 3));
                
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.EXPLOSION, 
                        e.getX(), e.getY() + 1, e.getZ(), 5, 0.5, 0.5, 0.5, 0.1);
                }
            }
        }
        this.playSound(SoundEvents.GENERIC_EXPLODE, 1.5f, 1.0f);
    }

    private void performTeleport() {
        if (this.level().isClientSide) return;

        LivingEntity target = this.getTarget();
        Vec3 teleportPos = null;
        final double MIN_TELEPORT_DISTANCE = 5.0;

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.PORTAL,
                this.getX(), this.getY() + 1, this.getZ(), 30, 0.5, 1, 0.5, 0.5);
        }

        for (int i = 0; i < 15; i++) {
            double x, y, z;
            if (target != null && this.random.nextDouble() < 0.6) {
                Vec3 back = target.getLookAngle().scale(-4);
                x = target.getX() + back.x + (this.random.nextDouble() - 0.5) * 3;
                z = target.getZ() + back.z + (this.random.nextDouble() - 0.5) * 3;
                y = target.getY();
            } else {
                double range = 20;
                x = this.getX() + (this.random.nextDouble() - 0.5) * range * 2;
                z = this.getZ() + (this.random.nextDouble() - 0.5) * range * 2;
                y = this.getY() + (this.random.nextInt(4) - 2);
            }

            double distSq = this.distanceToSqr(x, y, z);
            if (distSq < MIN_TELEPORT_DISTANCE * MIN_TELEPORT_DISTANCE) {
                continue;
            }

            BlockPos pos = BlockPos.containing(x, y, z);
            if (this.level().getBlockState(pos.below()).isSolid() &&
                this.level().noCollision(this, this.getBoundingBox().move(x - this.getX(), y - this.getY(), z - this.getZ()))) {
                teleportPos = new Vec3(x, y, z);
                break;
            }
        }

        if (teleportPos != null) {
            this.teleportTo(teleportPos.x, teleportPos.y, teleportPos.z);
            this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("morebosses:endertp")), 1.0f, 1.0f);

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.PORTAL,
                    teleportPos.x, teleportPos.y + 1, teleportPos.z, 30, 0.5, 1, 0.5, 0.5);
            }
        }
    }

    // --- SISTEMA DE DANO RECEBIDO ---

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Imunidades absolutas (sempre negam dano)
        if (source.is(DamageTypes.FALL) || source.is(DamageTypes.DRAGON_BREATH) || 
            source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.PLAYER_EXPLOSION))
            return false;

        // Verifica se o dano é causado por uma entidade (mob, player ou projétil)
        boolean isEntityDamage = source.getEntity() != null;

        // Danos não causados por entidades (ambientais: fogo, lava, afogamento, etc.) passam direto
        if (!isEntityDamage) {
            return super.hurt(source, amount);
        }

        // A partir daqui, só lidamos com danos vindos de entidades

        // Durante o ataque de laser (estados 4 e 5) – NENHUMA redução ou bloqueio
        if (attackState == 4 || attackState == 5) {
            return super.hurt(source, amount);
        }

        // Estados de ataque corpo a corpo (1,2,3) e teleporte (6) – redução de 50%
        if (attackState >= 1 && attackState <= 6) {
            amount *= 0.5f; // reduz dano pela metade
            // Som de impacto para indicar resistência
            if (this.tickCount % 5 == 0) {
                this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("morebosses:enderblock")), 1.5f, 1.5f);
            }
            return super.hurt(source, amount);
        }

        // Estado de bloqueio dedicado (7) – nega completamente o dano de entidades
        if (attackState == 7) {
            if (this.tickCount % 5 == 0) {
                this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("morebosses:enderblock")), 1.5f, 1.5f);
            }
            return false;
        }

        // Estado idle (0) – chance de ativar bloqueio (8%) apenas contra dano de entidades
        if (attackState == 0 && amount > 0) {
            if (this.random.nextFloat() < 0.08f) {
                triggerBlock(); // ativa animação de block
                this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("morebosses:enderblock")), 1.0f, 1.0f);
                return false; // bloqueou, não toma dano
            }
        }

        // Qualquer outro caso (ex.: estado idle sem block) – dano normal
        return super.hurt(source, amount);
    }
    
    // --- GECKOLIB / ANIMAÇÕES ---

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
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("morebosses:endehurts"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.ender_dragon.death"));
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
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();

        if (!this.level().isClientSide && (this.isInWaterRainOrBubble() || this.isInPowderSnow)) {
            this.hurt(this.damageSources().magic(), 1.0f);
            this.performTeleport();
            
            if (this.attackState == 4 || this.attackState == 5) {
                resetAttack(40);
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

    public static void init() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        // Velocidade de movimento reduzida (antes 0.25, agora 0.18)
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.18);
        builder = builder.add(Attributes.MAX_HEALTH, 450);
        builder = builder.add(Attributes.ARMOR, 20);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 12);
        builder = builder.add(Attributes.FOLLOW_RANGE, 64);
        builder = builder.add(Attributes.ATTACK_KNOCKBACK, 0.3);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))
                    && !this.isAggressive()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("death"));
            }
            if (this.isAggressive() && event.isMoving()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("run"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.STOP;
    }

    private PlayState procedurePredicate(AnimationState event) {
        if (!this.animationprocedure.equals("empty") && !this.animationprocedure.equals(prevAnim)) {
            event.getController().forceAnimationReset();
            event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
            prevAnim = this.animationprocedure;
            return PlayState.CONTINUE;
        } else if (this.animationprocedure.equals("empty")) {
            prevAnim = "empty";
            return PlayState.STOP;
        }
        if (this.animationprocedure.equals("laserloop")) {
             return event.setAndContinue(RawAnimation.begin().thenLoop("laserloop"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            if (activeLaser != null) {
                activeLaser.on = false;
                activeLaser = null;
            }
            this.remove(GuardianoOfTheEyesEntity.RemovalReason.KILLED);
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
        data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
    
    class BossAttackGoal extends MeleeAttackGoal {
        public BossAttackGoal(GuardianoOfTheEyesEntity mob) {
            super(mob, 1.2, false);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && GuardianoOfTheEyesEntity.this.attackState == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && GuardianoOfTheEyesEntity.this.attackState == 0;
        }
        
        @Override
        protected double getAttackReachSqr(LivingEntity entity) {
            return 9.0;
        }
    }
}