
package net.mcreator.morebosses.client.screens;

import org.checkerframework.checker.units.qual.h;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.Minecraft;

import net.mcreator.morebosses.procedures.WatchedLayerDisplayOverlayIngameProcedure;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class WatchedLayerOverlay {
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = event.getWindow().getGuiScaledWidth();
		int h = event.getWindow().getGuiScaledHeight();
		Level world = null;
		double x = 0;
		double y = 0;
		double z = 0;
		Player entity = Minecraft.getInstance().player;
		if (entity != null) {
			world = entity.level();
			x = entity.getX();
			y = entity.getY();
			z = entity.getZ();
		}
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		if (WatchedLayerDisplayOverlayIngameProcedure.execute(entity)) {
			event.getGuiGraphics().blit(new ResourceLocation("morebosses:textures/screens/big_eyes.png"), w / 2 + 121, h / 2 + -82, 0, 0, 64, 64, 64, 64);

			event.getGuiGraphics().blit(new ResourceLocation("morebosses:textures/screens/big_eyes.png"), w / 2 + -154, h / 2 + 74, 0, 0, 64, 64, 64, 64);

			event.getGuiGraphics().blit(new ResourceLocation("morebosses:textures/screens/big_eyes.png"), w / 2 + -213, h / 2 + -120, 0, 0, 64, 64, 64, 64);

			event.getGuiGraphics().blit(new ResourceLocation("morebosses:textures/screens/big_eyes.png"), w / 2 + 68, h / 2 + 48, 0, 0, 64, 64, 64, 64);

			event.getGuiGraphics().blit(new ResourceLocation("morebosses:textures/screens/big_eyes.png"), w / 2 + -229, h / 2 + 10, 0, 0, 64, 64, 64, 64);

			event.getGuiGraphics().blit(new ResourceLocation("morebosses:textures/screens/big_eyes.png"), w / 2 + -106, h / 2 + -125, 0, 0, 64, 64, 64, 64);

			event.getGuiGraphics().blit(new ResourceLocation("morebosses:textures/screens/big_eyes.png"), w / 2 + -208, h / 2 + -52, 0, 0, 64, 64, 64, 64);

			event.getGuiGraphics().blit(new ResourceLocation("morebosses:textures/screens/big_eyes.png"), w / 2 + 31, h / 2 + -111, 0, 0, 64, 64, 64, 64);

			event.getGuiGraphics().blit(new ResourceLocation("morebosses:textures/screens/big_eyes.png"), w / 2 + 135, h / 2 + 21, 0, 0, 64, 64, 64, 64);

			event.getGuiGraphics().blit(new ResourceLocation("morebosses:textures/screens/big_eyes.png"), w / 2 + 162, h / 2 + -135, 0, 0, 64, 64, 64, 64);

			event.getGuiGraphics().blit(new ResourceLocation("morebosses:textures/screens/big_eyes.png"), w / 2 + 91, h / 2 + -144, 0, 0, 64, 64, 64, 64);

			event.getGuiGraphics().blit(new ResourceLocation("morebosses:textures/screens/big_eyes.png"), w / 2 + 125, h / 2 + 78, 0, 0, 64, 64, 64, 64);

			event.getGuiGraphics().blit(new ResourceLocation("morebosses:textures/screens/big_eyes.png"), w / 2 + 175, h / 2 + -35, 0, 0, 64, 64, 64, 64);

			event.getGuiGraphics().blit(new ResourceLocation("morebosses:textures/screens/big_eyes.png"), w / 2 + -155, h / 2 + -86, 0, 0, 64, 64, 64, 64);

			event.getGuiGraphics().blit(new ResourceLocation("morebosses:textures/screens/big_eyes.png"), w / 2 + -143, h / 2 + -115, 0, 0, 32, 32, 32, 32);

			event.getGuiGraphics().blit(new ResourceLocation("morebosses:textures/screens/big_eyes.png"), w / 2 + -212, h / 2 + 60, 0, 0, 64, 64, 64, 64);

			event.getGuiGraphics().blit(new ResourceLocation("morebosses:textures/screens/big_eyes.png"), w / 2 + -158, h / 2 + 42, 0, 0, 32, 32, 32, 32);

			event.getGuiGraphics().blit(new ResourceLocation("morebosses:textures/screens/big_eyes.png"), w / 2 + 106, h / 2 + 26, 0, 0, 32, 32, 32, 32);

			event.getGuiGraphics().blit(new ResourceLocation("morebosses:textures/screens/big_eyes.png"), w / 2 + 184, h / 2 + 71, 0, 0, 32, 32, 32, 32);

			event.getGuiGraphics().blit(new ResourceLocation("morebosses:textures/screens/big_eyes.png"), w / 2 + 86, h / 2 + -74, 0, 0, 32, 32, 32, 32);

			event.getGuiGraphics().blit(new ResourceLocation("morebosses:textures/screens/big_eyes.png"), w / 2 + 182, h / 2 + -67, 0, 0, 32, 32, 32, 32);

			event.getGuiGraphics().blit(new ResourceLocation("morebosses:textures/screens/big_eyes.png"), w / 2 + -33, h / 2 + -119, 0, 0, 32, 32, 32, 32);

			event.getGuiGraphics().blit(new ResourceLocation("morebosses:textures/screens/watchedower.png"), w / 2 + -252, h / 2 + -128, 0, 0, 512, 256, 512, 256);

		}
		RenderSystem.depthMask(true);
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
}
