
package net.mcreator.morebosses.entity;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.common.ForgeMod;

import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.Difficulty;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;

import net.mcreator.morebosses.init.MorebossesModEntities;

public class ShrimpEntity extends PathfinderMob {
	public ShrimpEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(MorebossesModEntities.SHRIMP.get(), world);
	}

	public ShrimpEntity(EntityType<ShrimpEntity> type, Level world) {
		super(type, world);
		setMaxUpStep(1f);
		xpReward = 2;
		setNoAi(false);
		this.setPathfindingMalus(BlockPathTypes.WATER, 0);
		this.moveControl = new MoveControl(this) {
			@Override
			public void tick() {
				if (ShrimpEntity.this.isInWater())
					ShrimpEntity.this.setDeltaMovement(ShrimpEntity.this.getDeltaMovement().add(0, 0.005, 0));
				if (this.operation == MoveControl.Operation.MOVE_TO && !ShrimpEntity.this.getNavigation().isDone()) {
					double dx = this.wantedX - ShrimpEntity.this.getX();
					double dy = this.wantedY - ShrimpEntity.this.getY();
					double dz = this.wantedZ - ShrimpEntity.this.getZ();
					float f = (float) (Mth.atan2(dz, dx) * (double) (180 / Math.PI)) - 90;
					float f1 = (float) (this.speedModifier * ShrimpEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
					ShrimpEntity.this.setYRot(this.rotlerp(ShrimpEntity.this.getYRot(), f, 10));
					ShrimpEntity.this.yBodyRot = ShrimpEntity.this.getYRot();
					ShrimpEntity.this.yHeadRot = ShrimpEntity.this.getYRot();
					if (ShrimpEntity.this.isInWater()) {
						ShrimpEntity.this.setSpeed((float) ShrimpEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
						float f2 = -(float) (Mth.atan2(dy, (float) Math.sqrt(dx * dx + dz * dz)) * (180 / Math.PI));
						f2 = Mth.clamp(Mth.wrapDegrees(f2), -85, 85);
						ShrimpEntity.this.setXRot(this.rotlerp(ShrimpEntity.this.getXRot(), f2, 5));
						float f3 = Mth.cos(ShrimpEntity.this.getXRot() * (float) (Math.PI / 180.0));
						ShrimpEntity.this.setZza(f3 * f1);
						ShrimpEntity.this.setYya((float) (f1 * dy));
					} else {
						ShrimpEntity.this.setSpeed(f1 * 0.05F);
					}
				} else {
					ShrimpEntity.this.setSpeed(0);
					ShrimpEntity.this.setYya(0);
					ShrimpEntity.this.setZza(0);
				}
			}
		};
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected PathNavigation createNavigation(Level world) {
		return new WaterBoundPathNavigation(this, world);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(2, new RandomSwimmingGoal(this, 4, 40));
		this.goalSelector.addGoal(3, new PanicGoal(this, 5));
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
	public boolean checkSpawnObstruction(LevelReader world) {
		return world.isUnobstructed(this);
	}

	@Override
	public boolean canBreatheUnderwater() {
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		Level world = this.level();
		Entity entity = this;
		return true;
	}

	public static void init() {
		SpawnPlacements.register(MorebossesModEntities.SHRIMP.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				(entityType, world, reason, pos, random) -> (world.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(world, pos, random) && Mob.checkMobSpawnRules(entityType, world, reason, pos, random)));
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
		builder = builder.add(Attributes.MAX_HEALTH, 3);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 0);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(ForgeMod.SWIM_SPEED.get(), 1.3);
		return builder;
	}
}
