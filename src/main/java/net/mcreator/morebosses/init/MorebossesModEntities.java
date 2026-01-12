
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

import net.mcreator.morebosses.entity.WindBurstEntity;
import net.mcreator.morebosses.entity.ShockWaveEntity;
import net.mcreator.morebosses.entity.MinilotlEntity;
import net.mcreator.morebosses.entity.MaxolotEntity;
import net.mcreator.morebosses.entity.DryBonesEntity;
import net.mcreator.morebosses.entity.CopperMonstrosityEntity;
import net.mcreator.morebosses.MorebossesMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MorebossesModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MorebossesMod.MODID);
	public static final RegistryObject<EntityType<CopperMonstrosityEntity>> COPPER_MONSTROSITY = register("copper_monstrosity",
			EntityType.Builder.<CopperMonstrosityEntity>of(CopperMonstrosityEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(128).setUpdateInterval(3).setCustomClientFactory(CopperMonstrosityEntity::new)

					.sized(4.5f, 5.2f));
	public static final RegistryObject<EntityType<ShockWaveEntity>> SHOCK_WAVE = register("shock_wave",
			EntityType.Builder.<ShockWaveEntity>of(ShockWaveEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(ShockWaveEntity::new).fireImmune().sized(1f, 2f));
	public static final RegistryObject<EntityType<WindBurstEntity>> WIND_BURST = register("wind_burst",
			EntityType.Builder.<WindBurstEntity>of(WindBurstEntity::new, MobCategory.MISC).setCustomClientFactory(WindBurstEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(1f, 1f));
	public static final RegistryObject<EntityType<MaxolotEntity>> MAXOLOT = register("maxolot",
			EntityType.Builder.<MaxolotEntity>of(MaxolotEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(MaxolotEntity::new)

<<<<<<< HEAD
<<<<<<< HEAD
					.sized(1.5f, 3f));
	public static final RegistryObject<EntityType<DryBonesEntity>> DRY_BONES = register("dry_bones",
			EntityType.Builder.<DryBonesEntity>of(DryBonesEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(DryBonesEntity::new).fireImmune().sized(1.2f, 4f));
	public static final RegistryObject<EntityType<MinilotlEntity>> MINILOTL = register("minilotl",
			EntityType.Builder.<MinilotlEntity>of(MinilotlEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(MinilotlEntity::new)

					.sized(0.6f, 1.6f));
=======
					.sized(0.6f, 1.8f));
>>>>>>> parent of 7748096 (Algumas melhorias no Maxolote, agora a Monstruosidade e o Maxolote quebram blocos, add o cataclysm só para testes, será removido qualquer coisa relacionada ao cataclysm na versão de exportar)
=======
					.sized(0.6f, 1.8f));
>>>>>>> parent of 7748096 (Algumas melhorias no Maxolote, agora a Monstruosidade e o Maxolote quebram blocos, add o cataclysm só para testes, será removido qualquer coisa relacionada ao cataclysm na versão de exportar)

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
		});
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(COPPER_MONSTROSITY.get(), CopperMonstrosityEntity.createAttributes().build());
		event.put(SHOCK_WAVE.get(), ShockWaveEntity.createAttributes().build());
		event.put(MAXOLOT.get(), MaxolotEntity.createAttributes().build());
		event.put(DRY_BONES.get(), DryBonesEntity.createAttributes().build());
		event.put(MINILOTL.get(), MinilotlEntity.createAttributes().build());
	}
}
