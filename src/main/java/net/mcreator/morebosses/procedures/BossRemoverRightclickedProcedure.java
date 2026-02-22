package net.mcreator.morebosses.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

public class BossRemoverRightclickedProcedure {
	public static void execute(Entity sourceentity) {
		if (sourceentity == null)
			return;
		if (sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("lmb:bosses"))) || sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("morebosses:minibosses")))) {
			if (!sourceentity.level().isClientSide())
				sourceentity.discard();
		}
	}
}
