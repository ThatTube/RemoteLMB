package net.mcreator.morebosses;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

@Mod.EventBusSubscriber(modid = "morebosses", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BossDamageHandler {
	// Criamos as referências para as tags de EntityType
	private static final TagKey<EntityType<?>> BOSS_TAG = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("lmb", "boss"));
	private static final TagKey<EntityType<?>> MINIBOSS_TAG = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("morebosses", "miniboss"));

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onLivingHurt(LivingHurtEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity == null)
			return;
		// Verifica se o tipo da entidade está no arquivo JSON da tag
		if (entity.getType().is(BOSS_TAG)) {
			if (event.getAmount() > 22.0f) {
				event.setAmount(22.0f);
			}
		} else if (entity.getType().is(MINIBOSS_TAG)) {
			if (event.getAmount() > 12.0f) {
				event.setAmount(12.0f);
			}
		}
	}
}
