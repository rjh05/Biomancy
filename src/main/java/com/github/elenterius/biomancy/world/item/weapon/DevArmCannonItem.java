package com.github.elenterius.biomancy.world.item.weapon;

import com.github.elenterius.biomancy.client.gui.DevCannonScreen;
import com.github.elenterius.biomancy.client.renderer.item.DevArmCannonRenderer;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.ModProjectiles;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.styles.TooltipHacks;
import com.github.elenterius.biomancy.world.item.IArmPoseProvider;
import com.github.elenterius.biomancy.world.item.IBiomancyItem;
import com.github.elenterius.biomancy.world.item.IKeyListener;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class DevArmCannonItem extends Item implements IAnimatable, IArmPoseProvider, IBiomancyItem, IKeyListener {

	public static final Set<Enchantment> VALID_ENCHANTMENTS = Set.of(Enchantments.PUNCH_ARROWS, Enchantments.POWER_ARROWS);
	private final AnimationFactory animationFactory = new AnimationFactory(this);

	public DevArmCannonItem(Properties properties) {
		super(properties);
	}

	private static float getBonusDamage(ItemStack stack) {
		return 0.6f * EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
	}

	private static int getBonusKnockBack(ItemStack stack) {
		return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
	}

	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IItemRenderProperties() {
			private final DevArmCannonRenderer renderer = new DevArmCannonRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
				return renderer;
			}
		});
	}

	@Override
	public InteractionResultHolder<Byte> onClientKeyPress(ItemStack stack, Level level, Player player, EquipmentSlot slot, byte flags) {
		if (slot.getType() == EquipmentSlot.Type.HAND) {
			InteractionHand hand = slot == EquipmentSlot.MAINHAND ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
			tryToOpenClientScreen(hand);
		}
		return InteractionResultHolder.fail(flags); //don't send button press to server
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerLevel level, Player player, byte flags) {
		if (flags < 0 || flags >= ModProjectiles.PRECONFIGURED_PROJECTILES.size()) {
			flags = 0;
		}
		stack.getOrCreateTag().putByte("ProjectileIndex", flags);
	}

	@OnlyIn(Dist.CLIENT)
	private void tryToOpenClientScreen(InteractionHand hand) {
		Screen currScreen = Minecraft.getInstance().screen;
		if (currScreen == null && Minecraft.getInstance().player != null) {
			Minecraft.getInstance().setScreen(new DevCannonScreen(hand));
			Minecraft.getInstance().player.playNotifySound(ModSoundEvents.UI_RADIAL_MENU_OPEN.get(), SoundSource.PLAYERS, 1f, 1f);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		if (!level.isClientSide) {
			ItemStack stack = player.getItemInHand(usedHand);

			byte index = stack.getOrCreateTag().getByte("ProjectileIndex");
			if (index < 0 || index >= ModProjectiles.PRECONFIGURED_PROJECTILES.size()) {
				index = 0;
				stack.getOrCreateTag().putByte("ProjectileIndex", (byte) 0);
			}
			ModProjectiles.PRECONFIGURED_PROJECTILES.get(index).shoot(level, player, FloatUnaryOperator.identity(), d -> d + getBonusDamage(stack), k -> k + getBonusKnockBack(stack), FloatUnaryOperator.identity());
		}
		return InteractionResultHolder.consume(player.getItemInHand(usedHand));
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return VALID_ENCHANTMENTS.contains(enchantment) || super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.NONE;
	}

	@Override
	public HumanoidModel.ArmPose getArmPose(Player player, InteractionHand usedHand, ItemStack stack) {
		return !player.swinging ? HumanoidModel.ArmPose.CROSSBOW_HOLD : HumanoidModel.ArmPose.ITEM;
	}

	@Override
	public void registerControllers(AnimationData data) {
		//do nothing
	}

	@Override
	public AnimationFactory getFactory() {
		return animationFactory;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(TooltipHacks.HR_COMPONENT);
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()));

		tooltip.add(TooltipHacks.EMPTY_LINE_COMPONENT);
		tooltip.add(new TextComponent("The quick brown fox jumps over the lazy dog.").withStyle(TextStyles.MAYKR_RUNES_GRAY));

		tooltip.add(TooltipHacks.EMPTY_LINE_COMPONENT);
		byte index = stack.getOrCreateTag().getByte("ProjectileIndex");
		if (index < 0 || index >= ModProjectiles.PRECONFIGURED_PROJECTILES.size()) {
			index = 0;
		}
		tooltip.add(new TextComponent(ModProjectiles.PRECONFIGURED_PROJECTILES.get(index).name()));

		tooltip.add(TooltipHacks.EMPTY_LINE_COMPONENT);

		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getTooltipText("action_open_inventory")).withStyle(TextStyles.MAYKR_RUNES_GRAY));
		// /tellraw @a {"text":"The quick brown fox jumps over the lazy dog. 1234567890!?","color":"#9e1316","font":"biomancy:maykr_runes"}
	}

	@Override
	public Component getHighlightTip(ItemStack stack, Component displayName) {
		byte index = stack.getOrCreateTag().getByte("ProjectileIndex");
		if (index < 0 || index >= ModProjectiles.PRECONFIGURED_PROJECTILES.size()) {
			index = 0;
		}
		return new TextComponent("").append(displayName).append(" (" + ModProjectiles.PRECONFIGURED_PROJECTILES.get(index).name() + ")");
	}

}
