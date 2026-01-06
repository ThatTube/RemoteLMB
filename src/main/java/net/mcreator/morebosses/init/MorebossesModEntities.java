
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
			EntityType.Builder.<WindBurstEntity>of(WindBurstEntity::new, MobCategory.MISC).setCustomClientFactory(WindBurstEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(5f, 1f));

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
		});
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(COPPER_MONSTROSITY.get(), CopperMonstrosityEntity.createAttributes().build());
		event.put(SHOCK_WAVE.get(), ShockWaveEntity.createAttributes().build());
	}
}
