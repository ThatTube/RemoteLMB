
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;

import net.mcreator.morebosses.entity.WitherMissileEntity;
import net.mcreator.morebosses.entity.WindBurstEntity;
import net.mcreator.morebosses.entity.TurretEntityProjectile;
import net.mcreator.morebosses.entity.TurretEntity;
import net.mcreator.morebosses.entity.TallEngineerEntity;
import net.mcreator.morebosses.entity.SoulDionaeaEntity;
import net.mcreator.morebosses.entity.ShrimpEntity;
import net.mcreator.morebosses.entity.ShockWaveEntity;
import net.mcreator.morebosses.entity.RobotWhaleEntity;
import net.mcreator.morebosses.entity.PiglinBoxerEntity;
import net.mcreator.morebosses.entity.PiglinBoulusEntity;
import net.mcreator.morebosses.entity.OilEngineerEntity;
import net.mcreator.morebosses.entity.OilDropEntity;
import net.mcreator.morebosses.entity.MissileEntity;
import net.mcreator.morebosses.entity.MinilotlEntity;
import net.mcreator.morebosses.entity.MaxolotEntity;
import net.mcreator.morebosses.entity.LaserEntity;
import net.mcreator.morebosses.entity.EngineerEntity;
import net.mcreator.morebosses.entity.DryBonesEntity;
import net.mcreator.morebosses.entity.CopperMonstrosityEntity;
import net.mcreator.morebosses.entity.CopperGrablerEntity;
import net.mcreator.morebosses.entity.CopperEyeEntity;
import net.mcreator.morebosses.entity.BruteEngineerEntity;
import net.mcreator.morebosses.entity.BeggarWolfEntity;
import net.mcreator.morebosses.MorebossesMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MorebossesModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MorebossesMod.MODID);
	public static final RegistryObject<EntityType<CopperMonstrosityEntity>> COPPER_MONSTROSITY = register("copper_monstrosity",
			EntityType.Builder.<CopperMonstrosityEntity>of(CopperMonstrosityEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(128).setUpdateInterval(3).setCustomClientFactory(CopperMonstrosityEntity::new)

					.sized(4.5f, 5.2f));
	public static final RegistryObject<EntityType<ShockWaveEntity>> SHOCK_WAVE = register("shock_wave",
			EntityType.Builder.<ShockWaveEntity>of(ShockWaveEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(ShockWaveEntity::new).fireImmune().sized(1f, 2f));
	public static final RegistryObject<EntityType<MaxolotEntity>> MAXOLOT = register("maxolot",
			EntityType.Builder.<MaxolotEntity>of(MaxolotEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(MaxolotEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<DryBonesEntity>> DRY_BONES = register("dry_bones",
			EntityType.Builder.<DryBonesEntity>of(DryBonesEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(DryBonesEntity::new).fireImmune().sized(1.5f, 3f));
	public static final RegistryObject<EntityType<MinilotlEntity>> MINILOTL = register("minilotl",
			EntityType.Builder.<MinilotlEntity>of(MinilotlEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(MinilotlEntity::new)

					.sized(0.6f, 1.6f));
	public static final RegistryObject<EntityType<WindBurstEntity>> WIND_BURST = register("wind_burst",
			EntityType.Builder.<WindBurstEntity>of(WindBurstEntity::new, MobCategory.MISC).setCustomClientFactory(WindBurstEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.6f, 0.6f));
	public static final RegistryObject<EntityType<SoulDionaeaEntity>> SOUL_DIONAEA = register("soul_dionaea",
			EntityType.Builder.<SoulDionaeaEntity>of(SoulDionaeaEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(SoulDionaeaEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<BeggarWolfEntity>> BEGGAR_WOLF = register("beggar_wolf",
			EntityType.Builder.<BeggarWolfEntity>of(BeggarWolfEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(BeggarWolfEntity::new)

					.sized(1.3f, 2f));
	public static final RegistryObject<EntityType<EngineerEntity>> ENGINEER = register("engineer",
			EntityType.Builder.<EngineerEntity>of(EngineerEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(EngineerEntity::new)

					.sized(0.6f, 0.6f));
	public static final RegistryObject<EntityType<BruteEngineerEntity>> BRUTE_ENGINEER = register("brute_engineer",
			EntityType.Builder.<BruteEngineerEntity>of(BruteEngineerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(BruteEngineerEntity::new)

					.sized(1f, 1f));
	public static final RegistryObject<EntityType<TurretEntity>> TURRET = register("turret",
			EntityType.Builder.<TurretEntity>of(TurretEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(TurretEntity::new)

					.sized(0.5f, 1f));
	public static final RegistryObject<EntityType<TurretEntityProjectile>> TURRET_PROJECTILE = register("projectile_turret", EntityType.Builder.<TurretEntityProjectile>of(TurretEntityProjectile::new, MobCategory.MISC)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(TurretEntityProjectile::new).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<TallEngineerEntity>> TALL_ENGINEER = register("tall_engineer",
			EntityType.Builder.<TallEngineerEntity>of(TallEngineerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(TallEngineerEntity::new)

					.sized(0.6f, 1.5f));
	public static final RegistryObject<EntityType<OilDropEntity>> OIL_DROP = register("oil_drop",
			EntityType.Builder.<OilDropEntity>of(OilDropEntity::new, MobCategory.MISC).setCustomClientFactory(OilDropEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<OilEngineerEntity>> OIL_ENGINEER = register("oil_engineer",
			EntityType.Builder.<OilEngineerEntity>of(OilEngineerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(OilEngineerEntity::new)

					.sized(0.3f, 0.3f));
	public static final RegistryObject<EntityType<CopperEyeEntity>> COPPER_EYE = register("copper_eye",
			EntityType.Builder.<CopperEyeEntity>of(CopperEyeEntity::new, MobCategory.MISC).setCustomClientFactory(CopperEyeEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<CopperGrablerEntity>> COPPER_GRABLER = register("copper_grabler",
			EntityType.Builder.<CopperGrablerEntity>of(CopperGrablerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(CopperGrablerEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<WitherMissileEntity>> WITHER_MISSILE = register("wither_missile",
			EntityType.Builder.<WitherMissileEntity>of(WitherMissileEntity::new, MobCategory.MISC).setCustomClientFactory(WitherMissileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<MissileEntity>> MISSILE = register("missile",
			EntityType.Builder.<MissileEntity>of(MissileEntity::new, MobCategory.MISC).setCustomClientFactory(MissileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<RobotWhaleEntity>> ROBOT_WHALE = register("robot_whale",
			EntityType.Builder.<RobotWhaleEntity>of(RobotWhaleEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(RobotWhaleEntity::new)

					.sized(2f, 1.2f));
	public static final RegistryObject<EntityType<LaserEntity>> LASER = register("laser",
			EntityType.Builder.<LaserEntity>of(LaserEntity::new, MobCategory.MISC).setCustomClientFactory(LaserEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<ShrimpEntity>> SHRIMP = register("shrimp",
			EntityType.Builder.<ShrimpEntity>of(ShrimpEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(ShrimpEntity::new)

					.sized(0.2f, 0.2f));
	public static final RegistryObject<EntityType<PiglinBoulusEntity>> PIGLIN_BOULUS = register("piglin_boulus", EntityType.Builder.<PiglinBoulusEntity>of(PiglinBoulusEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(128).setUpdateInterval(3).setCustomClientFactory(PiglinBoulusEntity::new).fireImmune().sized(1.2f, 3f));
	public static final RegistryObject<EntityType<PiglinBoxerEntity>> PIGLIN_BOXER = register("piglin_boxer",
			EntityType.Builder.<PiglinBoxerEntity>of(PiglinBoxerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(PiglinBoxerEntity::new)

					.sized(0.6f, 1.8f));

	// Start of user code block custom entities
	// End of user code block custom entities
	private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			CopperMonstrosityEntity.init();
			ShockWaveEntity.init();
			MaxolotEntity.init();
			DryBonesEntity.init();
			MinilotlEntity.init();
			SoulDionaeaEntity.init();
			BeggarWolfEntity.init();
			EngineerEntity.init();
			BruteEngineerEntity.init();
			TurretEntity.init();
			TallEngineerEntity.init();
			OilEngineerEntity.init();
			CopperGrablerEntity.init();
			RobotWhaleEntity.init();
			ShrimpEntity.init();
			PiglinBoulusEntity.init();
			PiglinBoxerEntity.init();
		});
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(COPPER_MONSTROSITY.get(), CopperMonstrosityEntity.createAttributes().build());
		event.put(SHOCK_WAVE.get(), ShockWaveEntity.createAttributes().build());
		event.put(MAXOLOT.get(), MaxolotEntity.createAttributes().build());
		event.put(DRY_BONES.get(), DryBonesEntity.createAttributes().build());
		event.put(MINILOTL.get(), MinilotlEntity.createAttributes().build());
		event.put(SOUL_DIONAEA.get(), SoulDionaeaEntity.createAttributes().build());
		event.put(BEGGAR_WOLF.get(), BeggarWolfEntity.createAttributes().build());
		event.put(ENGINEER.get(), EngineerEntity.createAttributes().build());
		event.put(BRUTE_ENGINEER.get(), BruteEngineerEntity.createAttributes().build());
		event.put(TURRET.get(), TurretEntity.createAttributes().build());
		event.put(TALL_ENGINEER.get(), TallEngineerEntity.createAttributes().build());
		event.put(OIL_ENGINEER.get(), OilEngineerEntity.createAttributes().build());
		event.put(COPPER_GRABLER.get(), CopperGrablerEntity.createAttributes().build());
		event.put(ROBOT_WHALE.get(), RobotWhaleEntity.createAttributes().build());
		event.put(SHRIMP.get(), ShrimpEntity.createAttributes().build());
		event.put(PIGLIN_BOULUS.get(), PiglinBoulusEntity.createAttributes().build());
		event.put(PIGLIN_BOXER.get(), PiglinBoxerEntity.createAttributes().build());
	}
}
