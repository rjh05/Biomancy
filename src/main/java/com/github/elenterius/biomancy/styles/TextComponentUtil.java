package com.github.elenterius.biomancy.styles;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public final class TextComponentUtil {

	private TextComponentUtil() {}

	public static String getTranslationKey(String prefix, String suffix) {
		return prefix + "." + BiomancyMod.MOD_ID + "." + suffix;
	}

	public static TranslatableComponent getTranslationText(String prefix, String suffix) {
		return new TranslatableComponent(getTranslationKey(prefix, suffix));
	}

	public static TranslatableComponent getTooltipText(String tooltipKey) {
		return new TranslatableComponent(getTranslationKey("tooltip", tooltipKey));
	}

	public static TranslatableComponent getTooltipText(String tooltipKey, Object... formatArgs) {
		return new TranslatableComponent(getTranslationKey("tooltip", tooltipKey), formatArgs);
	}

	public static TranslatableComponent getMsgText(String msgKey) {
		return new TranslatableComponent(getTranslationKey("msg", msgKey));
	}

	public static TranslatableComponent getMsgText(String msgKey, Object... formatArgs) {
		return new TranslatableComponent(getTranslationKey("msg", msgKey), formatArgs);
	}

	public static Component getFailureMsgText(String msgKey) {
		return getMsgText(msgKey).withStyle(TextStyles.ERROR);
	}

	public static Component getFailureMsgText(String msgKey, Object... formatArgs) {
		return getMsgText(msgKey, formatArgs).withStyle(TextStyles.ERROR);
	}

}
