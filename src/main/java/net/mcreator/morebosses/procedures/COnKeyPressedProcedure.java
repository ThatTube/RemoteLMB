package net.mcreator.morebosses.procedures;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.NetworkDirection;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.Connection;
import net.minecraft.core.BlockPos;

import net.mcreator.morebosses.init.MorebossesModItems;
import net.mcreator.morebosses.MorebossesMod;

import java.util.List;
import java.util.Iterator;

public class COnKeyPressedProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem() == MorebossesModItems.METALIX_CHESTPLATE.get()
				&& !(entity instanceof Player _plrCldCheck2 && _plrCldCheck2.getCooldowns().isOnCooldown(MorebossesModItems.METALIX_CHESTPLATE.get()))) {
			if (world.isClientSide()) {
				SetupAnimationsProcedure.setAnimationClientside((Player) entity, "block", true);
			}
			if (!world.isClientSide()) {
				if (entity instanceof Player && world instanceof ServerLevel srvLvl_) {
					List<Connection> connections = srvLvl_.getServer().getConnection().getConnections();
					synchronized (connections) {
						Iterator<Connection> iterator = connections.iterator();
						while (iterator.hasNext()) {
							Connection connection = iterator.next();
							if (!connection.isConnecting() && connection.isConnected())
								MorebossesMod.PACKET_HANDLER.sendTo(new SetupAnimationsProcedure.MorebossesModAnimationMessage(Component.literal("block"), entity.getId(), true), connection, NetworkDirection.PLAY_TO_CLIENT);
						}
					}
				}
			}
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("morebosses:enderblock")), SoundSource.PLAYERS, 1, 1);
				} else {
					_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("morebosses:enderblock")), SoundSource.PLAYERS, 1, 1, false);
				}
			}
			entity.setInvulnerable(true);
			MorebossesMod.queueServerWork(20, () -> {
				entity.setInvulnerable(false);
			});
			if (entity instanceof Player _player)
				_player.getCooldowns().addCooldown(MorebossesModItems.METALIX_CHESTPLATE.get(), 220);
		}
	}
}
