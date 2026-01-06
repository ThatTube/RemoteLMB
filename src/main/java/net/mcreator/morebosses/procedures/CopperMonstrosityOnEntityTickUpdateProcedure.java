package net.mcreator.morebosses.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.TagKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;

import net.mcreator.morebosses.init.MorebossesModEntities;
import net.mcreator.morebosses.entity.WindBurstEntity;
import net.mcreator.morebosses.entity.CopperMonstrosityEntity;
import net.mcreator.morebosses.MorebossesMod;

import java.util.Random;
import java.util.List;
import java.util.Comparator;

public class CopperMonstrosityOnEntityTickUpdateProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		double Random = 0;
		if (entity.getPersistentData().getDouble("spawTime") > 0) {
			if (entity instanceof CopperMonstrosityEntity && !((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) == null)) {
				Random = Math.random();
			} else {
				Random = 0;
			}
			entity.getPersistentData().putDouble("spawTime", (entity.getPersistentData().getDouble("spawTime") - 1));
		} else {
			if (Random > 0.4 && Random < 0.6) {
				if (entity instanceof CopperMonstrosityEntity) {
					((CopperMonstrosityEntity) entity).setAnimation("slam");
				}
				if (world instanceof Level _level && !_level.isClientSide())
					_level.explode(null, x, y, z, 2, Level.ExplosionInteraction.NONE);
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("lmb:immunetoshockwave")))) {
							entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MOB_ATTACK)), 25);
						}
					}
				}
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = MorebossesModEntities.SHOCK_WAVE.get().spawn(_level, BlockPos.containing(x, y, z + 2), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setDeltaMovement(0, 0, 0);
					}
				}
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = MorebossesModEntities.SHOCK_WAVE.get().spawn(_level, BlockPos.containing(x, y, z - 2), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setDeltaMovement(0, 0, 0);
					}
				}
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = MorebossesModEntities.SHOCK_WAVE.get().spawn(_level, BlockPos.containing(x - 2, y, z), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setDeltaMovement(0, 0, 0);
					}
				}
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = MorebossesModEntities.SHOCK_WAVE.get().spawn(_level, BlockPos.containing(x + 2, y, z), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setDeltaMovement(0, 0, 0);
					}
				}
			} else if (Random > 0.7 && 8 < ((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) != null ? entity.distanceTo((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null)) : -1)
					&& 32 > ((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) != null ? entity.distanceTo((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null)) : -1)) {
				if (entity instanceof CopperMonstrosityEntity) {
					((CopperMonstrosityEntity) entity).setAnimation("shot");
				}
				{
					Entity _shootFrom = entity;
					Level projectileLevel = _shootFrom.level();
					if (!projectileLevel.isClientSide()) {
						Projectile _entityToSpawn = new Object() {
							public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
								AbstractArrow entityToSpawn = new WindBurstEntity(MorebossesModEntities.WIND_BURST.get(), level);
								entityToSpawn.setOwner(shooter);
								entityToSpawn.setBaseDamage(damage);
								entityToSpawn.setKnockback(knockback);
								entityToSpawn.setSilent(true);
								return entityToSpawn;
							}
						}.getArrow(projectileLevel, entity, 18, 0);
						_entityToSpawn.setPos(_shootFrom.getX(), _shootFrom.getEyeY() - 0.1, _shootFrom.getZ());
						_entityToSpawn.shoot(_shootFrom.getLookAngle().x, _shootFrom.getLookAngle().y, _shootFrom.getLookAngle().z, 1, 0);
						projectileLevel.addFreshEntity(_entityToSpawn);
					}
				}
				MorebossesMod.queueServerWork(20, () -> {
					{
						Entity _shootFrom = entity;
						Level projectileLevel = _shootFrom.level();
						if (!projectileLevel.isClientSide()) {
							Projectile _entityToSpawn = new Object() {
								public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
									AbstractArrow entityToSpawn = new WindBurstEntity(MorebossesModEntities.WIND_BURST.get(), level);
									entityToSpawn.setOwner(shooter);
									entityToSpawn.setBaseDamage(damage);
									entityToSpawn.setKnockback(knockback);
									entityToSpawn.setSilent(true);
									return entityToSpawn;
								}
							}.getArrow(projectileLevel, entity, 18, 0);
							_entityToSpawn.setPos(_shootFrom.getX(), _shootFrom.getEyeY() - 0.1, _shootFrom.getZ());
							_entityToSpawn.shoot(_shootFrom.getLookAngle().x, _shootFrom.getLookAngle().y, _shootFrom.getLookAngle().z, 1, 0);
							projectileLevel.addFreshEntity(_entityToSpawn);
						}
					}
				});
			}
			entity.getPersistentData().putDouble("spawTime", 100);
		}
	}
}
