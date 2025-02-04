package com.github.elenterius.biomancy.client;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.client.ClientSetupHandler;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
import com.github.elenterius.biomancy.world.item.IKeyListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientInputHandler {

	private static final EquipmentSlot[] armorSlotTypes = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
	private static final EquipmentSlot[] handSlotTypes = new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};

	private ClientInputHandler() {}

	@SubscribeEvent
	public static void onKeyInput(final InputEvent.KeyInputEvent event) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (!(mc.screen instanceof InventoryScreen) && mc.screen != null) return;

		if (player != null && event.getKey() == ClientSetupHandler.ITEM_DEFAULT_KEY_BINDING.getKey().getValue() && event.getAction() == GLFW.GLFW_RELEASE) {
			if (event.getModifiers() == GLFW.GLFW_MOD_CONTROL) {
				handleEquipmentSlots(armorSlotTypes, player);
			}
			else {
				handleEquipmentSlots(handSlotTypes, player);
			}
		}

	}

	private static void handleEquipmentSlots(EquipmentSlot[] slots, LocalPlayer player) {
		for (EquipmentSlot slot : slots) { //worst case this will send 2 or 4 packets to the server
			ItemStack stack = player.getItemBySlot(slot);
			if (!stack.isEmpty() && stack.getItem() instanceof IKeyListener keyListener) {
				InteractionResultHolder<Byte> result = keyListener.onClientKeyPress(stack, player.clientLevel, player, slot, (byte) 0);
				if (result.getResult().shouldSwing()) {
					ModNetworkHandler.sendKeyBindPressToServer(slot, result.getObject());
				}
			}
		}
	}

	//	@SubscribeEvent
	//	public static void onMouseClick(final InputEvent.ClickInputEvent event) {
	//		if (event.isAttack()) {
	//			ClientPlayerEntity player = Minecraft.getInstance().player;
	//			if (player != null) {
	//				ItemStack heldStack = player.getHeldItem(event.getHand());
	//				if (!heldStack.isEmpty() && heldStack.getItem() == ModItems.INFESTED_RIFLE.get()) {
	//					event.setSwingHand(false);
	//					event.setCanceled(true);
	//				}
	//			}
	//		}
	//	}
}
