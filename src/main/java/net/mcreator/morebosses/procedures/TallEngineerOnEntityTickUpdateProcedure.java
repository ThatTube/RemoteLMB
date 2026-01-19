package net.mcreator.morebosses.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import net.mcreator.morebosses.init.MorebossesModEntities;
import net.mcreator.morebosses.entity.TurretEntity;

public class TallEngineerOnEntityTickUpdateProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D && !((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) == null)) {
			if (Math.random() > 0.9) {
				if (entity instanceof TurretEntity) {
					((TurretEntity) entity).setAnimation("summon");
				}
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.WAX_ON, x, y, z, 5, 3, 3, 3, 1);
				if (world instanceof ServerLevel _serverLevel) {
					Entity entityinstance = MorebossesModEntities.TURRET.get().create(_serverLevel, null, null, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED, false, false);
					if (entityinstance != null) {
						entityinstance.setYRot(world.getRandom().nextFloat() * 360.0F);
						if (entityinstance instanceof TamableAnimal _toTame && entity instanceof Player _owner)
							_toTame.tame(_owner);
						_serverLevel.addFreshEntity(entityinstance);
					}
				}
			}
		}
	}
}
