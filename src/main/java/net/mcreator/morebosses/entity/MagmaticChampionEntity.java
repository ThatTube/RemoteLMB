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
import net.minecraft.world.level.ClipContext;
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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import net.mcreator.morebosses.init.MorebossesModEntities;
import net.mcreator.morebosses.init.MorebossesModMobEffects;

import java.util.List;
import java.util.Comparator;

public class MagmaticChampionEntity extends Monster implements GeoEntity {
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(MagmaticChampionEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(MagmaticChampionEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(MagmaticChampionEntity.class, EntityDataSerializers.STRING);
    
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.RED, ServerBossEvent.BossBarOverlay.PROGRESS);
    
    // Variáveis de Controle de Estado e Combate
    private int actionState = 0; // 0=Idle, 1=Slash, 2=SlashLeft, 3=Slam, 4=Parry, 5=Revenge, 6=JumpStart, 7=Jumping, 8=JumpEnd
    private int actionTimer = 0;
    private int attackCooldown = 0;
    private boolean canCombo = false;
    private boolean hasComboed = false;
    private boolean isDying = false; // Nova flag para controle de morte
    
    // Constantes de Estado
    private static final int ST_IDLE = 0;
    private static final int ST_SLASH = 1;
    private static final int ST_SLASH_LEFT = 2;
    private static final int ST_SLAM = 3;
    private static final int ST_PARRY = 4;
    private static final int ST_REVENGE = 5;
    private static final int ST_JUMP_START = 6;
    private static final int ST_JUMP_MID = 7;
    private static final int ST_JUMP_END = 8;

    public String animationprocedure = "empty";

    public MagmaticChampionEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(MorebossesModEntities.MAGMATIC_CHAMPION.get(), world);
    }

    public MagmaticChampionEntity(EntityType<MagmaticChampionEntity> type, Level world) {
        super(type, world);
        xpReward = 25;
        setNoAi(false);
        setMaxUpStep(1f);
        
        // Configurar barra de boss
        this.bossInfo.setVisible(true);
        this.bossInfo.setCreateWorldFog(true);
        this.bossInfo.setDarkenScreen(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHOOT, false);
        this.entityData.define(ANIMATION, "undefined");
        this.entityData.define(TEXTURE, "champion");
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
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true, false));
        // Aumentei o range do MeleeAttackGoal para ele não ficar "grudado" tentando bater normal enquanto faz as skills
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true) {
            @Override
            public boolean canUse() {
                // Só persegue se não estiver executando um ataque especial
                return super.canUse() && actionState == ST_IDLE && !isDying;
            }
            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && actionState == ST_IDLE && !isDying;
            }
             @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth() + 2.0; 
            }
        });
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1) {
            @Override
            public boolean canUse() {
                return super.canUse() && !isDying;
            }
        });
        this.targetSelector.addGoal(4, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && !isDying;
            }
        });
        this.goalSelector.addGoal(6, new FloatGoal(this));
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
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
        // Se estiver morrendo, não toma mais dano
        if (this.isDying) {
            return false;
        }
        
        // Lógica do PARRY
        if (this.actionState == ST_PARRY) {
             Entity attacker = source.getEntity();
             if (attacker instanceof LivingEntity) {
                 // Cancela o ataque, vira para o alvo e executa Revenge
                 this.actionState = ST_REVENGE;
                 this.actionTimer = 25;
                 this.lookAt(attacker, 360, 360);
                 
                 // Resetar animação para garantir transição suave
                 if (!this.level().isClientSide()) {
                    this.animationprocedure = "revenge";
                    this.setAnimation("revenge");
                    this.level().broadcastEntityEvent(this, (byte) 0);
                 }
                 
                 return false;
             }
        }

        // **IMUNIDADE A FOGO, WITHER E QUEDA**
        if (source.is(DamageTypes.IN_FIRE) || 
            source.is(DamageTypes.ON_FIRE) || 
            source.is(DamageTypes.LAVA) ||
            source.is(DamageTypes.FALL) || 
            source.is(DamageTypes.WITHER) || 
            source.is(DamageTypes.WITHER_SKULL) ||
            source.is(DamageTypes.HOT_FLOOR) ||
            source.is(DamageTypes.FIREBALL) ||
            source.is(DamageTypes.UNATTRIBUTED_FIREBALL) ||
            source.is(DamageTypes.DRAGON_BREATH)) {
            return false;
        }
            
        boolean hurt = super.hurt(source, amount);
        
        // Verifica se morreu após o dano
        if (this.getHealth() <= 0 && !this.isDying) {
            startDeathAnimation();
        }
        
        return hurt;
    }
    
    @Override
    public boolean fireImmune() {
        return true; // Imune a fogo
    }
    
    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        // Imunidade adicional a wither
        if (source.is(DamageTypes.WITHER) || source.is(DamageTypes.WITHER_SKULL)) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }
    
    @Override
    public boolean canFreeze() {
        return false; // Não congela
    }
    
    @Override
    public void lavaHurt() {
        // Não toma dano de lava
    }
    
    @Override
    public void setSecondsOnFire(int seconds) {
        // Não pega fogo
    }
    
    @Override
    public void setRemainingFireTicks(int ticks) {
        // Não mantém ticks de fogo
    }
    
    // Novo método para iniciar animação de morte
    private void startDeathAnimation() {
        if (this.isDying) return;
        
        this.isDying = true;
        this.actionState = ST_IDLE; // Reseta estado
        this.actionTimer = 0;
        this.attackCooldown = 0;
        
        // Para todos os movimentos
        this.setDeltaMovement(0, 0, 0);
        this.getNavigation().stop();
        
        // Toca animação de morte
        if (!this.level().isClientSide()) {
            this.animationprocedure = "death";
            this.setAnimation("death");
            this.level().broadcastEntityEvent(this, (byte) 2); // Evento especial de morte
        }
        
        // Desativa AI
        this.setNoAi(true);
    }

    // Método para atualizar jogadores que podem ver o boss
    @Override
    public void customServerAiStep() {
        super.customServerAiStep();
        
        // Se estiver morrendo, não faz nada
        if (this.isDying) {
            this.setDeltaMovement(0, 0, 0);
            return;
        }
        
        // Atualiza a barra de boss
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
        
        // Adiciona/remove jogadores da barra de boss baseado na visibilidade
        if (!this.level().isClientSide()) {
            List<ServerPlayer> players = ((ServerLevel) this.level()).getPlayers(p -> p.distanceToSqr(this) < 2500);
            
            for (ServerPlayer player : players) {
                if (player.hasLineOfSight(this) && !this.bossInfo.getPlayers().contains(player)) {
                    this.bossInfo.addPlayer(player);
                }
            }
            
            List<ServerPlayer> playersToRemove = this.bossInfo.getPlayers().stream()
                .filter(player -> player.distanceToSqr(this) > 2500 || !player.hasLineOfSight(this))
                .toList();
                
            for (ServerPlayer player : playersToRemove) {
                this.bossInfo.removePlayer(player);
            }
        }

        // Reduz cooldowns globais
        if (this.attackCooldown > 0) this.attackCooldown--;

        // Se tiver alvo, gira pra ele (exceto se estiver no meio de um pulo ou parry travado)
        LivingEntity target = this.getTarget();
        if (target != null && actionState != ST_PARRY && actionState != ST_JUMP_MID && actionState != ST_JUMP_START && !isDying) {
            this.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }

        // --- MÁQUINA DE ESTADOS ---
        if (this.actionState != ST_IDLE) {
            handleActionTick(target);
        } else {
            // Se estiver ocioso e tiver alvo
            if (target != null && this.attackCooldown <= 0 && this.distanceTo(target) < 12 && !isDying) {
                decideAttack(target);
            }
        }
    }

    // Remove jogadores da barra e desativa efeitos visuais quando o boss morrer
    @Override
    public void remove(Entity.RemovalReason reason) {
        if (!this.level().isClientSide()) {
            // Remove todos os jogadores da barra de boss
            this.bossInfo.removeAllPlayers();
            
            // Desativa os efeitos visuais (névoa e céu escuro)
            this.bossInfo.setCreateWorldFog(false);
            this.bossInfo.setDarkenScreen(false);
            
            // Esconde a barra de boss
            this.bossInfo.setVisible(false);
        }
        super.remove(reason);
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    // ==================================================================================================
    // LÓGICA PRINCIPAL (CÉREBRO DO BOSS)
    // ==================================================================================================
    private void decideAttack(LivingEntity target) {
        double dist = this.distanceTo(target);
        double random = Math.random();

        if (dist > 6 && random < 0.6) {
            startJump(target);
        } else if (random < 0.15) {
            startParry();
        } else if (random < 0.50) {
            startSlam();
        } else {
            startSlash(false);
        }
    }

    // Gerencia o que acontece a cada tick dependendo do estado atual
    private void handleActionTick(LivingEntity target) {
        this.actionTimer--;
        
        // Bloqueia movimento padrão durante ataques
        this.getNavigation().stop();

        switch (actionState) {
            case ST_SLASH: // Slash Normal
            case ST_SLASH_LEFT: // Slash Esquerda
                // **APENAS SLASH TEM DASH** - E apenas no momento exato
                if (actionTimer == 18) { // Apenas no tick 18
                    Vec3 vec = this.getLookAngle().scale(0.8); // Pequeno dash
                    this.setDeltaMovement(vec.x, 0, vec.z); // Y sempre 0 no dash
                } else if (actionTimer != 18) {
                    // Em todos os outros ticks do slash, congela o movimento
                    this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
                }
                
                // Dano no momento certo
                if (actionTimer == 10) {
                    performAreaSlash(actionState == ST_SLASH_LEFT);
                }
                
                // Janela de Combo
                if (actionTimer < 8 && actionTimer > 0 && !hasComboed) {
                    if (target != null && this.distanceTo(target) < 4 && Math.random() < 0.5) {
                         hasComboed = true;
                         if (actionState == ST_SLASH) startSlash(true);
                         else startSlash(false);
                         return;
                    }
                }
                if (actionTimer <= 0) resetToIdle(20);
                break;

            case ST_SLAM:
                // **SEM DASH NO SLAM** - Congela completamente
                this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
                if (actionTimer == 10) {
                    spawnMagmaPitsInLine();
                }
                if (actionTimer <= 0) resetToIdle(30);
                break;

            case ST_PARRY:
                // **SEM DASH NO PARRY** - Congela completamente
                this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
                if (actionTimer <= 0) resetToIdle(10);
                break;

            case ST_REVENGE:
                // **SEM DASH NO REVENGE** - Congela completamente
                this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
                if (actionTimer == 12) {
                     performRevengeAttack();
                }
                if (actionTimer <= 0) resetToIdle(15);
                break;
                
            case ST_JUMP_START:
                // **SEM DASH NO JUMP_START** - Só prepara o pulo
                this.setDeltaMovement(0, 0, 0);
                if (actionTimer <= 0) {
                    this.actionState = ST_JUMP_MID;
                    this.actionTimer = 40;
                    jumpTowardsTarget(target);
                    updateAnimation("jmiddle");
                    
                    if (!this.level().isClientSide()) {
                        spawnJumpParticles();
                    }
                }
                break;
                
            case ST_JUMP_MID:
                // **NO JUMP_MID DEIXA A FÍSICA AGIR** - Não modifica movimento além do pulo inicial
                // Verifica se tocou no chão
                if (this.onGround() && actionTimer < 35 && this.actionState == ST_JUMP_MID) {
                    this.actionState = ST_JUMP_END;
                    this.actionTimer = 15;
                    updateAnimation("jend");
                    
                    performJumpLandingEffects(target);
                }
                if (actionTimer <= 0) resetToIdle(10);
                break;
                
            case ST_JUMP_END:
                // **NO JUMP_END CONGELA** - Recuperação do pouso
                this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
                if (actionTimer <= 0) resetToIdle(10);
                break;
        }
    }

    // ==================================================================================================
    // MÉTODOS DE AÇÃO (START E EXECUTE)
    // ==================================================================================================

    private void startSlash(boolean isLeft) {
        this.actionState = isLeft ? ST_SLASH_LEFT : ST_SLASH;
        this.actionTimer = 20;
        this.canCombo = true;
        updateAnimation(isLeft ? "slashleft" : "slash");
    }

    private void startSlam() {
        this.actionState = ST_SLAM;
        this.actionTimer = 30;
        this.hasComboed = false;
        updateAnimation("slam");
    }

    private void startParry() {
        this.actionState = ST_PARRY;
        this.actionTimer = 60;
        updateAnimation("parry");
    }
    
    private void startJump(LivingEntity target) {
        if (target == null) {
            resetToIdle(0);
            return;
        }
        this.actionState = ST_JUMP_START;
        this.actionTimer = 10;
        updateAnimation("jstart");
    }

    private void resetToIdle(int cooldown) {
        this.actionState = ST_IDLE;
        this.attackCooldown = cooldown;
        this.animationprocedure = "empty";
        this.setAnimation("empty");
        this.hasComboed = false;
    }
    
    private void jumpTowardsTarget(LivingEntity target) {
        if (target == null) return;
        
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);
        
        if (distance > 0) {
            dx /= distance;
            dz /= distance;
        }
        
        double jumpDistance = Math.min(distance * 0.8, 12.0);
        double verticalForce = 0.8 + (jumpDistance / 15.0);
        double horizontalForce = jumpDistance * 0.2;
        
        this.setDeltaMovement(dx * horizontalForce, verticalForce, dz * horizontalForce);
    }
    
    // CORREÇÃO: Método para encontrar a posição Y correta do chão
    private int findGroundY(Level world, int x, int z, int startY) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, startY, z);
        
        while (pos.getY() > world.getMinBuildHeight() + 1) {
            BlockState state = world.getBlockState(pos);
            if (!state.isAir() && state.isSolid()) {
                return pos.getY() + 1;
            }
            pos.move(0, -1, 0);
        }
        
        return startY;
    }
    
    private void performJumpLandingEffects(LivingEntity target) {
        this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.explode")), 1.5f, 0.8f);
        performLandingDamage();
        
        if (!this.level().isClientSide()) {
            spawnLandingParticles();
            causeScreenShakeToNearbyPlayers();
        }
    }
    
    private void spawnJumpParticles() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;
        
        serverLevel.sendParticles(
            ParticleTypes.LAVA,
            this.getX(), this.getY(), this.getZ(),
            30,
            1.5, 0.2, 1.5,
            0.05
        );
        
        serverLevel.sendParticles(
            ParticleTypes.SMOKE,
            this.getX(), this.getY() + 0.5, this.getZ(),
            20,
            1.0, 0.5, 1.0,
            0.02
        );
    }
    
    private void spawnLandingParticles() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;
        
        serverLevel.sendParticles(
            ParticleTypes.LAVA,
            this.getX(), this.getY() + 0.5, this.getZ(),
            100,
            3.0, 0.5, 3.0,
            0.1
        );
        
        serverLevel.sendParticles(
            ParticleTypes.FLAME,
            this.getX(), this.getY() + 1, this.getZ(),
            60,
            4.0, 1.0, 4.0,
            0.08
        );
        
        serverLevel.sendParticles(
            ParticleTypes.CAMPFIRE_COSY_SMOKE,
            this.getX(), this.getY() + 2, this.getZ(),
            40,
            2.0, 2.0, 2.0,
            0.03
        );
    }
    
    private void causeScreenShakeToNearbyPlayers() {
        if (this.level().isClientSide()) return;
        
        ServerLevel serverLevel = (ServerLevel) this.level();
        List<ServerPlayer> nearbyPlayers = serverLevel.getPlayers(
            player -> player.distanceToSqr(this) < 400
        );
        
        for (ServerPlayer player : nearbyPlayers) {
            double distance = Math.sqrt(player.distanceToSqr(this));
            float intensity = (float) Math.max(0.1, 1.0 - (distance / 20.0));
            
            player.knockback(0.1 * intensity, 
                player.getX() - this.getX(),
                player.getZ() - this.getZ());
            
            if (distance < 10) {
                player.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN, 
                    10,
                    2,
                    false,
                    false
                ));
            }
        }
    }
    
    private void performLandingDamage() {
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0D));
        
        for (LivingEntity entity : list) {
            if (entity == this) continue;
            
            double dist = this.distanceTo(entity);
            if (dist < 4.0) {
                float damage = 12.0f * (1.0f - (float)dist / 4.0f);
                entity.hurt(this.damageSources().mobAttack(this), damage);
                
                entity.addEffect(new MobEffectInstance(MorebossesModMobEffects.HELLISH_BURN.get(), 100, 1));
                
                if (dist > 0) {
                    Vec3 awayDir = entity.position().subtract(this.position()).normalize();
                    double force = 1.5 * (1.0 - dist / 4.0);
                    entity.setDeltaMovement(
                        awayDir.x * force,
                        Math.min(0.8, force * 0.5),
                        awayDir.z * force
                    );
                    entity.hurtMarked = true;
                }
            }
        }
    }
    
    private void updateAnimation(String animName) {
        if (!this.level().isClientSide()) {
            this.animationprocedure = animName;
            this.setAnimation(animName);
            this.level().broadcastEntityEvent(this, (byte) 1);
        }
    }

    private void performAreaSlash(boolean left) {
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0D));
        Vec3 lookDir = this.getLookAngle();
        
        for (LivingEntity entity : list) {
            if (entity == this) continue;
            
            Vec3 entityDir = entity.position().vectorTo(this.position()).normalize();
            
            double dot = lookDir.dot(entityDir.scale(-1)); 
            if (dot > 0.5) {
                if (canSeeTargetThroughBlocks(entity.position())) {
                    entity.hurt(this.damageSources().mobAttack(this), 8f);
                    entity.addEffect(new MobEffectInstance(MorebossesModMobEffects.HELLISH_BURN.get(), 100, 0));
                }
            }
        }
    }

    private boolean canSeeTargetThroughBlocks(Vec3 targetPos) {
        Vec3 start = this.getEyePosition();
        ClipContext context = new ClipContext(start, targetPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this);
        var result = this.level().clip(context);
        
        if (result.getType() != net.minecraft.world.phys.HitResult.Type.MISS) {
            BlockPos pos = result.getBlockPos();
            BlockState state = this.level().getBlockState(pos);
            if (state.getExplosionResistance(this.level(), pos, null) > 12) {
                return false;
            }
        }
        return true;
    }

    private void spawnMagmaPitsInLine() {
        if (this.level().isClientSide()) return;
        
        Vec3 start = this.position();
        Vec3 lookDir = this.getLookAngle();
        
        int lineLength = 6;
        double spacing = 1.5;
        
        for (int i = 1; i <= lineLength; i++) {
            Vec3 pitPos = start.add(lookDir.x * i * spacing, 0, lookDir.z * i * spacing);
            
            int groundY = findGroundY(this.level(), 
                (int)Math.floor(pitPos.x), 
                (int)Math.floor(pitPos.z),
                (int)Math.floor(this.getY()));
            
            if (groundY <= this.level().getMinBuildHeight() + 1) {
                groundY = (int)Math.floor(this.getY());
            }
            
            Entity pit = MorebossesModEntities.MAGMA_PIT.get().create(this.level());
            if (pit != null) {
                pit.moveTo(pitPos.x, groundY, pitPos.z, 0, 0);
                this.level().addFreshEntity(pit);
            }
            
            if (i > 1 && i < lineLength) {
                Vec3 leftPos = start.add(
                    lookDir.x * i * spacing - lookDir.z * 0.8,
                    0,
                    lookDir.z * i * spacing + lookDir.x * 0.8
                );
                spawnSingleMagmaPitAtGround(leftPos);
                
                Vec3 rightPos = start.add(
                    lookDir.x * i * spacing + lookDir.z * 0.8,
                    0,
                    lookDir.z * i * spacing - lookDir.x * 0.8
                );
                spawnSingleMagmaPitAtGround(rightPos);
            }
        }
    }
    
    private void spawnSingleMagmaPitAtGround(Vec3 position) {
        int groundY = findGroundY(this.level(), 
            (int)Math.floor(position.x), 
            (int)Math.floor(position.z),
            (int)Math.floor(this.getY()));
        
        if (groundY <= this.level().getMinBuildHeight() + 1) {
            groundY = (int)Math.floor(this.getY());
        }
        
        Entity pit = MorebossesModEntities.MAGMA_PIT.get().create(this.level());
        if (pit != null) {
            pit.moveTo(position.x, groundY, position.z, 0, 0);
            this.level().addFreshEntity(pit);
        }
    }
    
    private void performRevengeAttack() {
        LivingEntity target = this.getTarget();
        if (target != null && this.distanceTo(target) < 5) {
             target.hurt(this.damageSources().mobAttack(this), 15f);
             
             double dx = target.getX() - this.getX();
             double dz = target.getZ() - this.getZ();
             target.knockback(2.5F, -dx, -dz);
             
             if (target instanceof Player player) {
                 player.getCooldowns().addCooldown(player.getUseItem().getItem(), 100);
                 player.stopUsingItem();
             }
             
             target.addEffect(new MobEffectInstance(MorebossesModMobEffects.STUN.get(), 60, 0));
        }
    }

    // ==================================================================================================
    // DATAS E SERIALIZAÇÃO
    // ==================================================================================================
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Texture", this.getTexture());
        compound.putBoolean("IsDying", this.isDying);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Texture"))
            this.setTexture(compound.getString("Texture"));
        if (compound.contains("IsDying"))
            this.isDying = compound.getBoolean("IsDying");
    }
    
    public static void init() {}

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25);
        builder = builder.add(Attributes.MAX_HEALTH, 500);
        builder = builder.add(Attributes.ARMOR, 35);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 12);
        builder = builder.add(Attributes.FOLLOW_RANGE, 64);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
        builder = builder.add(Attributes.ATTACK_KNOCKBACK, 0.1);
        return builder;
    }

    // ==================================================================================================
    // GECKOLIB CONTROLLERS
    // ==================================================================================================
    private PlayState movementPredicate(AnimationState event) {
        if (!this.animationprocedure.equals("empty") && !this.animationprocedure.equals("undefined")) {
            return PlayState.STOP;
        }
        
        if (this.isDeadOrDying() || this.isDying) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("death"));
        }
        
        if (event.isMoving()) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
        }
        
        return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
    }

    private PlayState procedurePredicate(AnimationState event) {
        if (!this.animationprocedure.equals("empty") && !this.animationprocedure.equals("undefined")) {
            RawAnimation animation = RawAnimation.begin().thenPlay(this.animationprocedure);
            return event.setAndContinue(animation);
        }
        return PlayState.STOP;
    }
    
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
        data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
    
    public String getSyncedAnimation() {
        return this.entityData.get(ANIMATION);
    }

    public void setAnimation(String animation) {
        this.entityData.set(ANIMATION, animation);
    }
    
    @Override
    public void handleEntityEvent(byte id) {
        if (id == (byte) 1) {
            if (this.level().isClientSide()) {
                String anim = this.entityData.get(ANIMATION);
                if (!anim.equals("undefined")) {
                    this.animationprocedure = anim;
                }
            }
        } else if (id == (byte) 2) {
            // Evento de morte
            if (this.level().isClientSide()) {
                this.animationprocedure = "death";
            }
        }
        super.handleEntityEvent(id);
    }
}