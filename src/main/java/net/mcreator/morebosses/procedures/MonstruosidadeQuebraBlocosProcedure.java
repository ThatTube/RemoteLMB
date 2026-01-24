package net.mcreator.morebosses.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingEvent;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;

import net.mcreator.morebosses.entity.CopperMonstrosityEntity;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class MonstruosidadeQuebraBlocosProcedure {
	@SubscribeEvent
	public static void onEntityTick(LivingEvent.LivingTickEvent event) {
		execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity());
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof CopperMonstrosityEntity) {
			int horizontalRadiusSquare = (int) 5 - 1;
			int verticalRadiusSquare = (int) 5 - 1;
			int yIterationsSquare = verticalRadiusSquare;
			for (int i = -yIterationsSquare; i <= yIterationsSquare; i++) {
				for (int xi = -horizontalRadiusSquare; xi <= horizontalRadiusSquare; xi++) {
					for (int zi = -horizontalRadiusSquare; zi <= horizontalRadiusSquare; zi++) {
						// Execute the desired statements within the square/cube
						if (!((world.getBlockState(BlockPos.containing(x + xi, y + i, z + zi))).getBlock() instanceof LiquidBlock)
								&& world.getBlockState(BlockPos.containing(x + xi, y + 5, z + zi)).getDestroySpeed(world, BlockPos.containing(x + xi, y + 5, z + zi)) < 22
								&& world.getBlockState(BlockPos.containing(x + xi, y + 5, z + zi)).getDestroySpeed(world, BlockPos.containing(x + xi, y + 5, z + zi)) > 0
								&& world.getBlockState(BlockPos.containing(x + xi, y + 4, z + zi)).getDestroySpeed(world, BlockPos.containing(x + xi, y + 4, z + zi)) < 22
								&& world.getBlockState(BlockPos.containing(x + xi, y + 4, z + zi)).getDestroySpeed(world, BlockPos.containing(x + xi, y + 4, z + zi)) > 0
								&& world.getBlockState(BlockPos.containing(x + xi, y + 3, z + zi)).getDestroySpeed(world, BlockPos.containing(x + xi, y + 3, z + zi)) < 22
								&& world.getBlockState(BlockPos.containing(x + xi, y + 3, z + zi)).getDestroySpeed(world, BlockPos.containing(x + xi, y + 3, z + zi)) > 0
								&& world.getBlockState(BlockPos.containing(x + xi, y + 2, z + zi)).getDestroySpeed(world, BlockPos.containing(x + xi, y + 2, z + zi)) < 22
								&& world.getBlockState(BlockPos.containing(x + xi, y + 2, z + zi)).getDestroySpeed(world, BlockPos.containing(x + xi, y + 2, z + zi)) > 0
								&& world.getBlockState(BlockPos.containing(x + xi, y + 1, z + zi)).getDestroySpeed(world, BlockPos.containing(x + xi, y + 1, z + zi)) < 22
								&& world.getBlockState(BlockPos.containing(x + xi, y + 1, z + zi)).getDestroySpeed(world, BlockPos.containing(x + xi, y + 1, z + zi)) > 0) {
							{
								BlockPos _pos = BlockPos.containing(x + xi, y + 2, z + zi);
								Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
								world.destroyBlock(_pos, false);
							}
							{
								BlockPos _pos = BlockPos.containing(x + xi, y + 3, z + zi);
								Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
								world.destroyBlock(_pos, false);
							}
							{
								BlockPos _pos = BlockPos.containing(x + xi, y + 1, z + zi);
								Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
								world.destroyBlock(_pos, false);
							}
							{
								BlockPos _pos = BlockPos.containing(x + xi, y + 4, z + zi);
								Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
								world.destroyBlock(_pos, false);
							}
							{
								BlockPos _pos = BlockPos.containing(x + xi, y + 5, z + zi);
								Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
								world.destroyBlock(_pos, false);
							}
						}
					}
				}
			}
		}
	}
}
