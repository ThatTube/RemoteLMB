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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraftforge.network.NetworkHooks;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.Difficulty;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.nbt.CompoundTag;

import net.mcreator.morebosses.init.MorebossesModEntities;

public class SoulsperEntity extends Monster implements GeoEntity {
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(SoulsperEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(SoulsperEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(SoulsperEntity.class, EntityDataSerializers.STRING);
    
    // Novo DataAccessor INT para gerenciar estados perfeitamente sincronizados
    public static final EntityDataAccessor<Integer> MOB_STATE = SynchedEntityData.defineId(SoulsperEntity.class, EntityDataSerializers.INT);
    // Estados: 0 = Normal, 1 = Lazy (Deitando), 2 = Dormindo, 3 = Acordando, 4 = Carregando Explosão

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public String animationprocedure = "empty";

    private int idleTicks = 0;
    private int actionTicks = 0; // Timer usado para esperar a animação acabar

    // === TEMPOS DE ANIMAÇÃO (AJUSTE AQUI SE NECESSÁRIO) ===
    private final int TIME_LAZY = 30;   // Ticks que a animação "lazy" dura (30 = 1.5s)
    private final int TIME_WAKE = 20;   // Ticks que a animação "wake" dura (20 = 1.0s)
    private final int TIME_CHARGE = 30; // Ticks para explodir (30 = 1.5s)

    public SoulsperEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(MorebossesModEntities.SOULSPER.get(), world);
    }

    public SoulsperEntity(EntityType<SoulsperEntity> type, Level world) {
        super(type, world);
        xpReward = 5;
        setNoAi(false);
        setMaxUpStep(0.6f);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHOOT, false);
        this.entityData.define(ANIMATION, "undefined");
        this.entityData.define(TEXTURE, "soulsper");
        this.entityData.define(MOB_STATE, 0); // Começa Normal
    }

    public void setTexture(String texture) { this.entityData.set(TEXTURE, texture); }
    public String getTexture() { return this.entityData.get(TEXTURE); }

    public int getState() { return this.entityData.get(MOB_STATE); }
    public void setState(int state) { this.entityData.set(MOB_STATE, state); }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        
        // Prioridade 1: Flutuar na água (para não afogar)
        this.goalSelector.addGoal(1, new FloatGoal(this));
        
        // Prioridade 2: Atacar o alvo de perto
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false));
        
        // Prioridade 3: Se não tiver o que fazer, caminhar aleatoriamente
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1));
        
        // Prioridade 4: Olhar em volta
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        // --- TARGET SELECTION (IA de Alvo) ---
        
        // Alvo 1: Jogadores (Isso faz ele se comportar como um Creeper/Zumbi)
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        
        // Alvo 2: Revidar se for atingido
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    @Override
    public MobType getMobType() { return MobType.UNDEFINED; }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) { return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.axe.strip")); }

    @Override
    public SoundEvent getDeathSound() { return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.creeper.death")); }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_FIRE)) return false;
        return super.hurt(source, amount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Texture", this.getTexture());
        compound.putInt("MobState", this.getState());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Texture")) this.setTexture(compound.getString("Texture"));
        if (compound.contains("MobState")) this.setState(compound.getInt("MobState"));
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();

        if (!this.level().isClientSide()) {
            LivingEntity target = this.getTarget();
            int currentState = this.getState();

            if (target != null && target.isAlive()) {
                this.idleTicks = 0;

                // Se tinha alvo mas estava deitado/dormindo, começa a acordar
                if (currentState == 1 || currentState == 2) {
                    this.setState(3); // Estado "Waking Up"
                    this.actionTicks = TIME_WAKE; 
                }

                // Espera a animação de acordar terminar
                if (currentState == 3) {
                    if (this.actionTicks > 0) this.actionTicks--;
                    else this.setState(0); // Fica normal e pronto pra caçar
                }

                // Se já estiver normal, verifica proximidade para explodir
                if (currentState == 0) {
                    if (this.distanceToSqr(target) < 9.0D) {
                        this.setState(4); // Estado "Charging"
                        this.actionTicks = TIME_CHARGE;
                    }
                }

                // Lógica da Explosão (Charging)
                if (currentState == 4) {
                    if (this.distanceToSqr(target) > 16.0D) {
                        this.setState(0); // Alvo fugiu, cancela a explosão
                        this.actionTicks = 0;
                    } else {
                        if (this.actionTicks > 0) {
                            this.actionTicks--;
                        } else {
                            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 3.0F, Level.ExplosionInteraction.MOB);
                            this.discard();
                        }
                    }
                }

            } else {
                // SEM ALVO
                if (currentState == 4) {
                    this.setState(0); // Cancela charge
                    this.actionTicks = 0;
                }

                if (currentState == 3) {
                    if (this.actionTicks > 0) this.actionTicks--;
                    else this.setState(0);
                }

                if (currentState == 0) {
                    this.idleTicks++;
                    if (this.idleTicks >= 1200) { // 1 Minuto
                        this.setState(1); // Inicia animação "Lazy"
                        this.actionTicks = TIME_LAZY;
                    }
                }

                // Se está na animação Lazy, espera acabar antes de dormir
                if (currentState == 1) {
                    if (this.actionTicks > 0) {
                        this.actionTicks--;
                    } else {
                        this.setState(2); // Finalmente entra em "Sleep" definitivo
                    }
                }
            }
        }
    }

    @Override
    public void travel(Vec3 dir) {
        // Congela os movimentos se NÃO estiver no estado 0 (Normal)
        if (this.getState() != 0) {
            if (this.getNavigation() != null) {
                this.getNavigation().stop();
            }
            super.travel(Vec3.ZERO);
            return;
        }
        super.travel(dir);
    }

    @Override
    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale((float) 1);
    }

    public static void init() {
        SpawnPlacements.register(MorebossesModEntities.SOULSPER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, world, reason, pos, random) -> (world.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(world, pos, random) && Mob.checkMobSpawnRules(entityType, world, reason, pos, random)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 20);
        builder = builder.add(Attributes.ARMOR, 0.2);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 0);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        return builder;
    }

    // === GESTÃO PERFEITA DE ANIMAÇÕES PELO GECKOLIB ===
    private PlayState movementPredicate(AnimationState event) {
        int state = this.getState();

        if (state == 4) return event.setAndContinue(RawAnimation.begin().thenPlay("charge"));
        if (state == 3) return event.setAndContinue(RawAnimation.begin().thenPlay("wake"));
        if (state == 2) return event.setAndContinue(RawAnimation.begin().thenLoop("sleep"));
        if (state == 1) return event.setAndContinue(RawAnimation.begin().thenPlay("lazy"));

        // Se state == 0 (Normal)
        if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
        }
        if (this.isDeadOrDying()) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("lazy"));
        }
        return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
    }

    // Manti esse predicate apenas para não quebrar compatibilidade com outras procedures do seu MCreator
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
        if (this.deathTime == 20) {
            this.remove(SoulsperEntity.RemovalReason.KILLED);
            this.dropExperience();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        // Colocando as animações no controller principal de movimentação para que transicionem com perfeição
        data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
        data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
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
}