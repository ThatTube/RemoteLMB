package net.mcreator.morebosses.init;

import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

import net.mcreator.morebosses.MorebossesMod;

public class MorebossesModTags {

	// Tag para a estrutura "test"
	public static final TagKey<Structure> TEST_STRUCTURE = TagKey.create(Registries.STRUCTURE, new ResourceLocation(MorebossesMod.MODID, "test"));

	// Você pode adicionar mais tags aqui depois
	// public static final TagKey<Structure> BOSS_DUNGEON = ...
	// public static final TagKey<Structure> ANCIENT_TEMPLE = ...

}
