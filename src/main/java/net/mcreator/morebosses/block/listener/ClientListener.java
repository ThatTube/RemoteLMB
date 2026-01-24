package net.mcreator.morebosses.block.listener;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.mcreator.morebosses.init.MorebossesModBlockEntities;
import net.mcreator.morebosses.block.renderer.MonstrosityAltarTileRenderer;
import net.mcreator.morebosses.block.renderer.GongTileRenderer;
import net.mcreator.morebosses.block.renderer.CopperDoorTileRenderer;
import net.mcreator.morebosses.MorebossesMod;

@Mod.EventBusSubscriber(modid = MorebossesMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientListener {
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(MorebossesModBlockEntities.GONG.get(), context -> new GongTileRenderer());
		event.registerBlockEntityRenderer(MorebossesModBlockEntities.MONSTROSITY_ALTAR.get(), context -> new MonstrosityAltarTileRenderer());
		event.registerBlockEntityRenderer(MorebossesModBlockEntities.COPPER_DOOR.get(), context -> new CopperDoorTileRenderer());
	}
}
