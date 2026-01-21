package net.mcreator.morebosses.procedures;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingEvent;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class AquaImmunetoWetEffectProcedure {
	@SubscribeEvent
	public static void onEntityTick(LivingEvent.LivingTickEvent event) {
		execute(event, event.getEntity());
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;
		// pega o efeito Wetness do Cataclysm
		MobEffect wetness = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("cataclysm", "wetness"));
		if (wetness == null)
			return; // segurança anti-crash, pq modder esperto é modder vivo
		if (entity instanceof LivingEntity living && living.hasEffect(wetness) && entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("morebosses:aqua")))) {
			living.removeEffect(wetness);
		}
	}
}
