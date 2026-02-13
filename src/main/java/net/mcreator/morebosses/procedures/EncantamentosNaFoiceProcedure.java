package net.mcreator.morebosses.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;

import net.mcreator.morebosses.init.MorebossesModMobEffects;
import net.mcreator.morebosses.init.MorebossesModEnchantments;

public class EncantamentosNaFoiceProcedure {
	public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return;
		if (EnchantmentHelper.getItemEnchantmentLevel(MorebossesModEnchantments.MOOCHER.get(), (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY)) != 0) {
			if (sourceentity instanceof LivingEntity _livEnt2 && _livEnt2.isBlocking()) {
				if (entity instanceof Player _player_) {
					if (!_player_.getOffhandItem().isEmpty() && _player_.getOffhandItem().getCount() > 0) {
						_player_.drop(new ItemStack(_player_.getOffhandItem().getItem(), 1), true);
						_player_.getOffhandItem().shrink(1);
						_player_.getInventory().setChanged();
					}
				}
			} else {
				if (entity instanceof Player _player_) {
					if (!_player_.getMainHandItem().isEmpty() && _player_.getMainHandItem().getCount() > 0) {
						_player_.drop(new ItemStack(_player_.getMainHandItem().getItem(), 1), true);
						_player_.getMainHandItem().shrink(1);
						_player_.getInventory().setChanged();
					}
				}
			}
		} else if (EnchantmentHelper.getItemEnchantmentLevel(MorebossesModEnchantments.SHOW_OFF.get(), (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY)) != 0) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 1));
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(MorebossesModMobEffects.ARMOR_BREACH.get(), 120, 1));
		}
	}
}
