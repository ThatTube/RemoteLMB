package net.mcreator.morebosses.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.ParticleTypes;

import net.mcreator.morebosses.entity.SoulDionaeaEntity;

import java.util.List;
import java.util.Comparator;

public class PlantaAtaqueProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		double Range = 0;
		double Xpar = 0;
		double YPar = 0;
		double ZPar = 0;
		Range = 0.25;
		if (entity.getPersistentData().getDouble("IA") == 0) {
			if (entity instanceof SoulDionaeaEntity) {
				((SoulDionaeaEntity) entity).setAnimation("attack");
			}
			entity.getPersistentData().putDouble("Look", (entity.getYRot()));
		}
		{
			Entity _ent = entity;
			_ent.setYRot((float) entity.getPersistentData().getDouble("Look"));
			_ent.setXRot(0);
			_ent.setYBodyRot(_ent.getYRot());
			_ent.setYHeadRot(_ent.getYRot());
			_ent.yRotO = _ent.getYRot();
			_ent.xRotO = _ent.getXRot();
			if (_ent instanceof LivingEntity _entity) {
				_entity.yBodyRotO = _entity.getYRot();
				_entity.yHeadRotO = _entity.getYRot();
			}
		}
		entity.getPersistentData().putDouble("IA", (entity.getPersistentData().getDouble("IA") + 1));
		if (entity.getPersistentData().getDouble("IA") < 7) {
			for (int index0 = 0; index0 < 4; index0++) {
				Xpar = x + entity.getLookAngle().x * Range;
				YPar = y + 1.75;
				ZPar = z + entity.getLookAngle().z * Range;
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.SOUL, Xpar, YPar, ZPar, 5, 0.15, 0.15, 0.15, 0);
				Range = Range + 0.95;
			}
		}
		if (entity.getPersistentData().getDouble("IA") == 7 && entity.getPersistentData().getDouble("IA") < 8) {
			for (int index1 = 0; index1 < 4; index1++) {
				Xpar = x + entity.getLookAngle().x * Range;
				YPar = y + 1.75;
				ZPar = z + entity.getLookAngle().z * Range;
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, Xpar, YPar, ZPar, 5, 0.15, 0.15, 0.15, 0);
				{
					final Vec3 _center = new Vec3(Xpar, YPar, ZPar);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(0.75 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (!(entityiterator == entity)) {
							entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MOB_ATTACK)), 7);
							entity.setDeltaMovement(new Vec3((Xpar * 0.35), (YPar * 0.35), (ZPar * 0.35)));
						}
					}
				}
				Range = Range + 0.95;
			}
		}
		if (entity.getPersistentData().getDouble("IA") == 17) {
			entity.getPersistentData().putDouble("IA", 0);
			entity.getPersistentData().putString("State", "Idle");
		}
	}
}
