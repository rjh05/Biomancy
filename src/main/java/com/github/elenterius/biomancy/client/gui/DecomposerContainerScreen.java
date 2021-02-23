package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.inventory.DecomposerContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;

public class DecomposerContainerScreen extends ContainerScreen<DecomposerContainer> {

	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(BiomancyMod.MOD_ID, "textures/gui/decomposer_inventory.png");
	public final int FUEL_BAR_POS_X = 16 + 1;
	public final int FUEL_BAR_POS_Y = 33 + 4;
	public final int FUEL_BAR_WIDTH = 5;
	public final int FUEL_BAR_HEIGHT = 60 - FUEL_BAR_POS_Y;

	public DecomposerContainerScreen(DecomposerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		//texture size
		xSize = 176;
		ySize = 166;
	}

	public static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
		return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		final float FONT_Y_SPACING = 12;
		font.func_243248_b(matrixStack, title, 10, 18 - FONT_Y_SPACING, 0xFFFFFF);

		int craftingProgress = (int) (container.getCraftingProgressNormalized() * 100);
		font.drawString(matrixStack, craftingProgress + "%", 52, 52 + 6, 0xFFFFFF);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		//noinspection ConstantConditions
		minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		int edgeSpacingX = (width - xSize) / 2;
		int edgeSpacingY = (height - ySize) / 2;
		blit(matrixStack, edgeSpacingX, edgeSpacingY, 0, 0, xSize, ySize);

		int posX = guiLeft + FUEL_BAR_POS_X;
		int posY = guiTop + FUEL_BAR_POS_Y;
		int maxPosY = posY + FUEL_BAR_HEIGHT;
		AbstractGui.fill(matrixStack, posX, posY + (int) (FUEL_BAR_HEIGHT * (1f - container.getFuelNormalized())), posX + FUEL_BAR_WIDTH, maxPosY, 0xFF9B2425);
		posX += FUEL_BAR_WIDTH + 6;
		AbstractGui.fill(matrixStack, posX, posY + (int) (FUEL_BAR_HEIGHT * (1f - container.getSpeedFuelNormalized())), posX + FUEL_BAR_WIDTH, maxPosY, 0xFFF4EFC8);
	}

	@Override
	protected void renderHoveredTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
		//noinspection ConstantConditions
		if (!minecraft.player.inventory.getItemStack().isEmpty()) return;

		List<ITextComponent> hoveringText = new ArrayList<>();

		if (isInRect(guiLeft + FUEL_BAR_POS_X, guiTop + FUEL_BAR_POS_Y, FUEL_BAR_WIDTH, FUEL_BAR_HEIGHT, mouseX, mouseY)) {
			int mainFuel = (int) (container.getFuelNormalized() * 100);
			hoveringText.add(BiomancyMod.getTranslationText("tooltip", "main_fuel").appendString(": " + mainFuel + "%"));
		}

		if (isInRect(guiLeft + FUEL_BAR_POS_X + FUEL_BAR_WIDTH + 6, guiTop + FUEL_BAR_POS_Y, FUEL_BAR_WIDTH, FUEL_BAR_HEIGHT, mouseX, mouseY)) {
			int speedFuel = (int) (container.getSpeedFuelNormalized() * 100);
			hoveringText.add(BiomancyMod.getTranslationText("tooltip", "speed_fuel").appendString(": " + speedFuel + "%"));
		}

		if (!hoveringText.isEmpty()) {
			func_243308_b(matrixStack, hoveringText, mouseX, mouseY);
		}
		else {
			super.renderHoveredTooltip(matrixStack, mouseX, mouseY);
		}
	}
}