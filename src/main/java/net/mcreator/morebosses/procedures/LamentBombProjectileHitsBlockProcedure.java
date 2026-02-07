package net.mcreator.morebosses.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.EntityType;
import net.minecraft.server.level.ServerLevel;

public class LamentBombProjectileHitsBlockProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel projectileLevel) {
			Projectile _entityToSpawn = new Object() {
				public Projectile getPotion(Level level) {
					ThrownPotion entityToSpawn = new ThrownPotion(EntityType.POTION, level);
					entityToSpawn.setItem(PotionUtils.setPotion(Items.LINGERING_POTION.getDefaultInstance(), Potions.SLOWNESS));
					return entityToSpawn;
				}
			}.getPotion(projectileLevel);
			_entityToSpawn.setPos(x, y, z);
			_entityToSpawn.shoot(1, 1, 1, 1, 0);
			projectileLevel.addFreshEntity(_entityToSpawn);
		}
	}
}
