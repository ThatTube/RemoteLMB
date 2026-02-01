package net.mcreator.morebosses.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.morebosses.world.inventory.WorkshopGUIMenu;
import net.mcreator.morebosses.network.WorkshopGUIButtonMessage;
import net.mcreator.morebosses.MorebossesMod;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class WorkshopGUIScreen extends AbstractContainerScreen<WorkshopGUIMenu> {
	private final static HashMap<String, Object> guistate = WorkshopGUIMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_merge;

	public WorkshopGUIScreen(WorkshopGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	private static final ResourceLocation texture = new ResourceLocation("morebosses:textures/screens/workshop_gui.png");

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

		guiGraphics.blit(new ResourceLocation("morebosses:textures/screens/workshopp.png"), this.leftPos + 0, this.topPos + 0, 0, 0, 176, 70, 176, 70);

		RenderSystem.disableBlend();
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		return super.keyPressed(key, b, c);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.morebosses.workshop_gui.label_workshop"), 65, 11, -16777216, false);
	}

	@Override
	public void init() {
		super.init();
		button_merge = new PlainTextButton(this.leftPos + 85, this.topPos + 51, 51, 20, Component.translatable("gui.morebosses.workshop_gui.button_merge"), e -> {
			if (true) {
				MorebossesMod.PACKET_HANDLER.sendToServer(new WorkshopGUIButtonMessage(0, x, y, z));
				WorkshopGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		}, this.font);
		guistate.put("button:button_merge", button_merge);
		this.addRenderableWidget(button_merge);
	}
}
