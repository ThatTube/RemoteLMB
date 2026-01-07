package net.mcreator.morebosses.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.TagKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;

import net.mcreator.morebosses.init.MorebossesModMobEffects;
import net.mcreator.morebosses.init.MorebossesModEntities;
import net.mcreator.morebosses.entity.WindBurstEntity;
import net.mcreator.morebosses.entity.CopperMonstrosityEntity;
import net.mcreator.morebosses.MorebossesMod;

import javax.annotation.Nullable;

import java.util.Random;
import java.util.List;
import java.util.Comparator;

@Mod.EventBusSubscriber
public class CopperMonstrosityOnEntityTickUpdateProcedure {

	@SubscribeEvent
	public static void onEntityTick(LivingEvent.LivingTickEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof CopperMonstrosityEntity)) return;
		if (entity.level().isClientSide()) return;

		execute(entity.level(), entity);
	}

	private static void execute(LevelAccessor world, Entity entity) {

		var data = entity.getPersistentData();
		double cooldown = data.getDouble("spawTime");

		if (cooldown > 0) {
			data.putDouble("spawTime", cooldown - 1);
			return;
		}

		Entity target = entity instanceof Mob mob ? mob.getTarget() : null;
		if (target == null) return;

		double distance = entity.distanceTo(target);
		double rng = Math.random();

		// ===================== SLAM =====================
		// RARO + ALVO PERTO
		if (rng < 0.25 && distance <= 6) {

			((CopperMonstrosityEntity) entity).setAnimation("slam");

			if (entity instanceof LivingEntity le) {
				le.addEffect(new MobEffectInstance(
						MorebossesModMobEffects.HEAVY.get(),
						60, 1, false, false
				));
			}

			if (world instanceof Level lvl) {
				lvl.explode(null,
						entity.getX(),
						entity.getY() - 1,
						entity.getZ(),
						2f,
						Level.ExplosionInteraction.NONE
				);
			}

			AABB area = entity.getBoundingBox().inflate(2.5);
			for (Entity e : world.getEntitiesOfClass(Entity.class, area)) {
				if (!e.getType().is(TagKey.create(
						Registries.ENTITY_TYPE,
						new ResourceLocation("lmb:immunetoshockwave")))) {

					e.hurt(entity.damageSources().mobAttack((Mob) entity), 25);
				}
			}

			if (world instanceof ServerLevel level) {
				spawnShockwave(level, entity.getX() + 2, entity.getY(), entity.getZ());
				spawnShockwave(level, entity.getX() - 2, entity.getY(), entity.getZ());
				spawnShockwave(level, entity.getX(), entity.getY(), entity.getZ() + 2);
				spawnShockwave(level, entity.getX(), entity.getY(), entity.getZ() - 2);
			}

			data.putDouble("spawTime", 140); // slam = cooldown maior
			return;
		}

		// ===================== SHOOT =====================
		if (distance > 6 && distance < 32) {

			((CopperMonstrosityEntity) entity).setAnimation("shot");

			shootFast(entity);

			// segundo tiro mais rápido
			MorebossesMod.queueServerWork(10, () -> shootFast(entity));

			data.putDouble("spawTime", 60); // atira mais vezes
		}
	}

	// ================= HELPERS =================

	private static void spawnShockwave(ServerLevel level, double x, double y, double z) {
		Entity e = MorebossesModEntities.SHOCK_WAVE.get()
				.spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
		if (e != null) e.setDeltaMovement(Vec3.ZERO);
	}

	private static void shootFast(Entity shooter) {
		Level level = shooter.level();
		if (level.isClientSide()) return;

		AbstractArrow proj = new WindBurstEntity(
				MorebossesModEntities.WIND_BURST.get(),
				level
		);

		proj.setOwner(shooter);
		proj.setBaseDamage(18);
		proj.setSilent(true);

		proj.setPos(
				shooter.getX(),
				shooter.getEyeY() - 0.1,
				shooter.getZ()
		);

		// MAIS RÁPIDO 🔥
		proj.shoot(
				shooter.getLookAngle().x,
				shooter.getLookAngle().y,
				shooter.getLookAngle().z,
				2.2f, // velocidade do projétil
				0f
		);

		level.addFreshEntity(proj);
	}
}