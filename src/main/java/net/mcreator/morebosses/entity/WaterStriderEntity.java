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
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.Difficulty;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.nbt.CompoundTag;

import net.mcreator.morebosses.init.MorebossesModEntities;

import java.util.List;

public class WaterStriderEntity extends TamableAnimal implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(WaterStriderEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(WaterStriderEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(WaterStriderEntity.class, EntityDataSerializers.STRING);
	
	// NOVO: Rastreador para animação de domar
	public static final EntityDataAccessor<Boolean> TAME_ANIMATION_PLAYED = SynchedEntityData.defineId(WaterStriderEntity.class, EntityDataSerializers.BOOLEAN);
	
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";
	
	// Rastreia se está na água para trocar animações
	private boolean lastInWater = false;

	public WaterStriderEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(MorebossesModEntities.WATER_STRIDER.get(), world);
	}

	public WaterStriderEntity(EntityType<WaterStriderEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		setMaxUpStep(0.6f);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "wstrider");
		this.entityData.define(TAME_ANIMATION_PLAYED, false);
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
		this.goalSelector.addGoal(1, new FollowParentGoal(this, 1));
		this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1, (float) 10, (float) 2, false));
		this.goalSelector.addGoal(3, new BreedGoal(this, 1));
		this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
		this.goalSelector.addGoal(5, new RandomSwimmingGoal(this, 5, 40));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
	}

	@Override
	public MobType getMobType() {
		return MobType.WATER;
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
		if (source.is(DamageTypes.DROWN))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putBoolean("TameAnimationPlayed", this.entityData.get(TAME_ANIMATION_PLAYED));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("TameAnimationPlayed"))
			this.entityData.set(TAME_ANIMATION_PLAYED, compound.getBoolean("TameAnimationPlayed"));
	}

	@Override
	public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
		ItemStack itemstack = sourceentity.getItemInHand(hand);
		InteractionResult retval = InteractionResult.sidedSuccess(this.level().isClientSide());
		Item item = itemstack.getItem();
		
		if (itemstack.getItem() instanceof SpawnEggItem) {
			retval = super.mobInteract(sourceentity, hand);
		} else if (this.level().isClientSide()) {
			retval = (this.isTame() && this.isOwnedBy(sourceentity) || this.isFood(itemstack)) ? InteractionResult.sidedSuccess(this.level().isClientSide()) : InteractionResult.PASS;
		} else {
			if (this.isTame()) {
				if (this.isOwnedBy(sourceentity)) {
					if (item.isEdible() && this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
						this.usePlayerItem(sourceentity, hand, itemstack);
						this.heal((float) item.getFoodProperties().getNutrition());
						retval = InteractionResult.sidedSuccess(this.level().isClientSide());
					} else if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
						this.usePlayerItem(sourceentity, hand, itemstack);
						this.heal(4);
						retval = InteractionResult.sidedSuccess(this.level().isClientSide());
					} else {
						retval = super.mobInteract(sourceentity, hand);
					}
				}
			} else if (this.isFood(itemstack)) {
				this.usePlayerItem(sourceentity, hand, itemstack);
				if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, sourceentity)) {
					this.tame(sourceentity);
					// Toca a animação de domar
					this.animationprocedure = "tame";
					this.entityData.set(TAME_ANIMATION_PLAYED, true);
					this.level().broadcastEntityEvent(this, (byte) 7);
				} else {
					this.level().broadcastEntityEvent(this, (byte) 6);
				}
				this.setPersistenceRequired();
				retval = InteractionResult.sidedSuccess(this.level().isClientSide());
			} else {
				retval = super.mobInteract(sourceentity, hand);
				if (retval == InteractionResult.SUCCESS || retval == InteractionResult.CONSUME)
					this.setPersistenceRequired();
			}
		}
		
		// SÓ permite montar se estiver domado
		if (this.isTame() && this.isOwnedBy(sourceentity)) {
			sourceentity.startRiding(this);
		}
		
		return retval;
	}

	@Override
	public void baseTick() {
		super.baseTick();
		this.refreshDimensions();
		
		// Reseta a animação de domar após completar
		if (this.entityData.get(TAME_ANIMATION_PLAYED) && this.animationprocedure.equals("empty")) {
			this.entityData.set(TAME_ANIMATION_PLAYED, false);
		}
		
		// Fornece respiração aquática ao jogador quando montado e submerso
		if (this.isVehicle() && this.isInWater()) {
			Entity rider = this.getPassengers().get(0);
			if (rider instanceof Player player) {
				player.setAirSupply(300); // Restaura o ar completamente enquanto montado e submerso
			}
		}
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 1);
	}

	@Override
	public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
		WaterStriderEntity retval = MorebossesModEntities.WATER_STRIDER.get().create(serverWorld);
		retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null, null);
		return retval;
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return List.of(Items.COD, Items.SALMON, Items.TROPICAL_FISH).contains(stack.getItem());
	}

	@Override
	public void travel(Vec3 dir) {
		Entity entity = this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);
		
		if (this.isVehicle() && entity != null) {
			// Alinha a rotação da entidade com o passageiro
			this.setYRot(entity.getYRot());
			this.yRotO = this.getYRot();
			this.setXRot(entity.getXRot() * 0.5F);
			this.setRot(this.getYRot(), this.getXRot());
			this.yBodyRot = entity.getYRot();
			this.yHeadRot = entity.getYRot();
			
			if (entity instanceof LivingEntity passenger) {
				// Pega os inputs de movimento do passageiro
				float forward = passenger.zza;
				float strafe = passenger.xxa * 0.5F;
				
				// Verifica se está na água para nado tipo golfinho
				if (this.isInWater()) {
					// Mecânica de nado tipo golfinho
					this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.5F);
					
					// Cria vetor de movimento
					Vec3 movement = new Vec3(strafe, 0, forward);
					movement = movement.yRot((float) Math.toRadians(this.getYRot()));
					
					// Adiciona impulso para cima quando move para frente na água
					if (forward > 0 && this.isInWater()) {
						Vec3 currentVel = this.getDeltaMovement();
						this.setDeltaMovement(currentVel.x, Math.min(currentVel.y + 0.05, 0.4), currentVel.z);
					}
					
					super.travel(movement);
				} else {
					// Movimento em terra
					this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
					super.travel(new Vec3(strafe, 0, forward));
				}
			}
			
			// Atualiza animação de caminhada para montaria
			double d1 = this.getX() - this.xo;
			double d0 = this.getZ() - this.zo;
			float f1 = (float) Math.sqrt(d1 * d1 + d0 * d0) * 4;
			if (f1 > 1.0F)
				f1 = 1.0F;
			this.walkAnimation.setSpeed(this.walkAnimation.speed() + (f1 - this.walkAnimation.speed()) * 0.4F);
			this.walkAnimation.position(this.walkAnimation.position() + this.walkAnimation.speed());
			this.calculateEntityAnimation(true);
			return;
		}
		
		// Comportamento automático de natação quando não está sendo montado
		if (this.isInWater() && !this.isVehicle()) {
			this.setDeltaMovement(this.getDeltaMovement().add(0, 0.005, 0));
		}
		
		super.travel(dir);
	}
	
	@Override
	public void aiStep() {
		super.aiStep();
		this.updateSwingTime();
		
		// Rastreia mudanças de estado da água para animação
		boolean currentlyInWater = this.isInWater();
		if (currentlyInWater != lastInWater) {
			lastInWater = currentlyInWater;
		}
	}

	public static void init() {
		SpawnPlacements.register(MorebossesModEntities.WATER_STRIDER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				(entityType, world, reason, pos, random) -> (world.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(world, pos, random) && Mob.checkMobSpawnRules(entityType, world, reason, pos, random)));
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.35); // Aumentado para melhor natação
		builder = builder.add(Attributes.MAX_HEALTH, 16);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 0);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		return builder;
	}

	// PREDICADO DE MOVIMENTO - Controla as animações idle, idlew, walk e swim
	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			// Verifica se a entidade está se movendo
			boolean isMoving = (event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F));
			
			// Animação de morte
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("death"));
			}
			
			// Animações baseadas em água vs terra
			if (this.isInWater()) {
				if (isMoving) {
					// Animação de nado na água
					return event.setAndContinue(RawAnimation.begin().thenLoop("swim"));
				} else {
					// Animação idle na água (idlew)
					return event.setAndContinue(RawAnimation.begin().thenLoop("idlew"));
				}
			} else {
				if (isMoving) {
					// Animação de caminhada em terra
					return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
				} else {
					// Animação idle em terra (idle)
					return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
				}
			}
		}
		return PlayState.STOP;
	}

	String prevAnim = "empty";

	// PREDICADO DE PROCEDIMENTO - Controla animações especiais como "tame"
	private PlayState procedurePredicate(AnimationState event) {
		// Gerencia animação de domar e outras animações procedurais
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
			this.remove(WaterStriderEntity.RemovalReason.KILLED);
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
}