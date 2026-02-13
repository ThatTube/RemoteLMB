package net.mcreator.morebosses.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import net.mcreator.morebosses.init.MorebossesModItems;

import java.util.List;
import java.util.ArrayList;

public class DashLifeEnderProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;

		// 1. Cooldown
		if (entity instanceof Player _player)
			_player.getCooldowns().addCooldown(MorebossesModItems.THE_LIFE_ENDER.get(), 120);

		// 2. CONFIGURAÇÃO DO DASH (Impulso Forte)
		double forcaHorizontal = 3.5; 
		double forcaVertical = 1.0;   
		
		Vec3 look = entity.getLookAngle();
		entity.setDeltaMovement(new Vec3(look.x * forcaHorizontal, look.y * forcaVertical, look.z * forcaHorizontal));

		// 3. DETECÇÃO E DANO (Corrigido para evitar o erro de compilação)
		final Vec3 _center = new Vec3(x, y, z);
		// Removido o .stream() e .toList() pois getEntitiesOfClass já retorna a lista pronta
		List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(6.0d), e -> true);

		for (Entity entityiterator : _entfound) {
			// Não dar dano em si mesmo e apenas em seres vivos
			if (entityiterator != entity && entityiterator instanceof LivingEntity _livEnt) {
				
				// Aplica Dano de Ataque (12.0f = 6 corações)
				_livEnt.hurt(entity.damageSources().mobAttack((LivingEntity) entity), 12.0f);

				// Aplica o efeito Armor Breach
				_livEnt.addEffect(new MobEffectInstance(
					BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation("morebosses:armor_breach")), 
					300, // 15 segundos
					0, 
					false, false, true));
			}
		}
	}
}