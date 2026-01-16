package net.mcreator.morebosses;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;

import java.util.UUID;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "morebosses")
public class CopperBossBar {
	private static final ResourceLocation CUSTOM_BAR_BG = new ResourceLocation("morebosses", "textures/screens/copper_bossbar.png");
	private static final ResourceLocation CUSTOM_BAR_FILL = new ResourceLocation("morebosses", "textures/screens/copper_bossbar_color.png");

	@SubscribeEvent
	public static void onRenderBossBar(CustomizeGuiOverlayEvent.BossEventProgress event) {
		// Filtro inicial pelo nome padrão
		if (event.getBossEvent().getName().getString().contains("Copper Monstrosity")) {
			event.setCanceled(true);

			Minecraft mc = Minecraft.getInstance();
			GuiGraphics guiGraphics = event.getGuiGraphics();

			int x = event.getX();
			int y = event.getY();
			int barWidth = 189;
			int barHeight = 35;

			// Renderiza o fundo
			guiGraphics.blit(CUSTOM_BAR_BG, x, y, 0, 0, barWidth, barHeight, barWidth, barHeight);

			// Renderiza o preenchimento
			float progress = event.getBossEvent().getProgress();
			int progressWidth = (int) (barWidth * progress);

			if (progressWidth > 0) {
				guiGraphics.blit(CUSTOM_BAR_FILL, x, y, 0, 0, progressWidth, barHeight, barWidth, barHeight);
			}

			// --- LÓGICA PARA FORÇAR O NOME REAL ---
			Component nameToShow = event.getBossEvent().getName();
			UUID bossId = event.getBossEvent().getId();
			// Tenta encontrar a entidade no mundo cliente para pegar o nome atualizado (etiqueta/renomeado)
			if (mc.level != null) {
				for (Entity entity : mc.level.entitiesForRendering()) {
					// Verifica se o ID da barra bate com o ID da entidade
					if (entity.getUUID().equals(bossId)) {
						nameToShow = entity.getDisplayName();
						break;
					}
				}
			}

			// Centraliza o texto
			int textWidth = mc.font.width(nameToShow);
			int textX = mc.getWindow().getGuiScaledWidth() / 2 - textWidth / 2;
			int textY = y - 10;

			// Desenha o nome em CIANO (0x00FFFF) com sombra (true)
			guiGraphics.drawString(mc.font, nameToShow, textX, textY, 0x00FFFF, true);
		}
	}
}
