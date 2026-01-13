package net.mcreator.morebosses.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Comparator;

public class PlantAttackDetecitonProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		double Xpar = 0;
		double Ypar = 0;
		double ZPar = 0;
		double Range = 0;
		Range = 0.75;
		if (!((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) == null)) {
			for (int index0 = 0; index0 < 4; index0++) {
				Xpar = x + entity.getLookAngle().x * Range;
				Xpar = y + 1.75;
				Xpar = z + entity.getLookAngle().z * Range;
				{
					final Vec3 _center = new Vec3(Xpar, Ypar, ZPar);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(0.5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator == (entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null)) {
							entity.getPersistentData().putString("State", "Attack");
						}
					}
				}
				Range = ZPar + 0.75;
			}
		}
	}
}
