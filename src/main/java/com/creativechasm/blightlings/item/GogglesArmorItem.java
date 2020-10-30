package com.creativechasm.blightlings.item;

import com.creativechasm.blightlings.BlightlingsMod;
import com.creativechasm.blightlings.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class GogglesArmorItem extends ArmorItem implements IRevealInvisible<GogglesArmorItem>
{
    public static final String ARMOR_TEXTURE = BlightlingsMod.MOD_ID + ":textures/models/armor/true_sight_goggles.png";

    public GogglesArmorItem(IArmorMaterial materialIn, Properties properties) {
        super(materialIn, EquipmentSlotType.HEAD, properties);
    }

    @Override
    public boolean canRevealInvisibleEntity(ItemStack stack, PlayerEntity player, Entity invisibleEntity) {
        return true;
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        if (world.isRemote()) return;

        if (player.isPotionActive(Effects.BLINDNESS)) {
            player.removePotionEffect(Effects.BLINDNESS);
        }

        EffectInstance activeEffect = player.getActivePotionEffect(Effects.NIGHT_VISION);
        if (activeEffect == null) {
            EffectInstance effectInstance = new EffectInstance(Effects.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false);
            if (player.isPotionApplicable(effectInstance)) {
                player.addPotionEffect(effectInstance);
            }
        }
        else if (player.ticksExisted % 1200 == 0 && activeEffect.getDuration() < Integer.MAX_VALUE - 2000) {
            EffectInstance effectInstance = new EffectInstance(Effects.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false);
            if (player.isPotionApplicable(effectInstance)) {
                player.addPotionEffect(effectInstance);
            }
        }
    }

    public void cancelEffect(LivingEntity entity) {
        if (entity.isPotionActive(Effects.NIGHT_VISION)) entity.removePotionEffect(Effects.NIGHT_VISION);
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return ARMOR_TEXTURE;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return ModItems.BLIGHT_SHARD == repair.getItem();
    }
}
