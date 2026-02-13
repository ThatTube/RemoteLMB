package net.mcreator.morebosses.procedures;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.NetworkDirection;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.tags.ItemTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.Connection;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

import net.mcreator.morebosses.MorebossesMod;

import java.util.List;
import java.util.Iterator;
import java.util.Comparator;

public class TauntOnKeyPressedProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		
		// Verificar se o jogador está segurando um escudo na mão off-hand
		boolean hasShield = false;
		if (entity instanceof Player) {
			Player player = (Player) entity;
			ItemStack offHandItem = player.getOffhandItem();
			
			// Verificar tags de escudo (suporta múltiplas tags)
			if (offHandItem.is(ItemTags.create(new ResourceLocation("c:shields"))) ||
				offHandItem.is(ItemTags.create(new ResourceLocation("minecraft:shields"))) ||
				offHandItem.is(ItemTags.create(new ResourceLocation("forge:shields")))) {
				hasShield = true;
			}
			
			// Também verificar por nome de item como fallback
			if (!hasShield) {
				String itemName = ForgeRegistries.ITEMS.getKey(offHandItem.getItem()).toString();
				if (itemName.contains("shield") || itemName.contains("Shield")) {
					hasShield = true;
				}
			}
			
			// Verificar cooldown (12 segundos = 240 ticks)
			if (player.getCooldowns().isOnCooldown(offHandItem.getItem())) {
				// Se estiver em cooldown, não executa
				return;
			}
		}
		
		// Se não tiver escudo, não executa o taunt
		if (!hasShield) {
			return;
		}
		
		// Aplicar efeito de Força 1 por 6 segundos (120 ticks) ao jogador
		if (entity instanceof LivingEntity _livingEntity) {
			// Força 1 = nível 0 (MobEffects.STRENGTH, 0, 120 ticks)
			_livingEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 120, 0, false, true));
		}
		
		// Aplicar cooldown de 12 segundos (240 ticks) no escudo
		if (entity instanceof Player) {
			Player player = (Player) entity;
			ItemStack offHandItem = player.getOffhandItem();
			player.getCooldowns().addCooldown(offHandItem.getItem(), 240);
			
			// Opcional: Enviar mensagem de cooldown
			if (!world.isClientSide()) {
				player.displayClientMessage(Component.literal("§aThis hability is on cooldown for 12 seconds!"), true);
			}
		}
		
		if (world.isClientSide()) {
			SetupAnimationsProcedure.setAnimationClientside((Player) entity, "taunt", true);
		}
		if (!world.isClientSide()) {
			if (entity instanceof Player && world instanceof ServerLevel srvLvl_) {
				List<Connection> connections = srvLvl_.getServer().getConnection().getConnections();
				synchronized (connections) {
					Iterator<Connection> iterator = connections.iterator();
					while (iterator.hasNext()) {
						Connection connection = iterator.next();
						if (!connection.isConnecting() && connection.isConnected())
							MorebossesMod.PACKET_HANDLER.sendTo(new SetupAnimationsProcedure.MorebossesModAnimationMessage(Component.literal("taunt"), entity.getId(), true), connection, NetworkDirection.PLAY_TO_CLIENT);
					}
				}
			}
		}
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("morebosses:taunt")), SoundSource.PLAYERS, 1, 1);
			} else {
				_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("morebosses:taunt")), SoundSource.PLAYERS, 1, 1, false);
			}
		}
		{
			final Vec3 _center = new Vec3(x, y, z);
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(8 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
			for (Entity entityiterator : _entfound) {
				if (!(entityiterator instanceof Player)) {
					{
						Entity _ent = entityiterator;
						if (!_ent.level().isClientSide() && _ent.getServer() != null) {
							_ent.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, _ent.position(), _ent.getRotationVector(), _ent.level() instanceof ServerLevel ? (ServerLevel) _ent.level() : null, 4,
									_ent.getName().getString(), _ent.getDisplayName(), _ent.level().getServer(), _ent), ("execute as " + entityiterator.getStringUUID() + " at @s run tp @s ~ ~ ~ facing entity " + entity.getStringUUID()));
						}
					}
					if (entityiterator instanceof Mob _entity && entity instanceof LivingEntity _ent)
						_entity.setTarget(_ent);
				}
			}
		}
	}
}