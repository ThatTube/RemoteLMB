package net.mcreator.morebosses.procedures;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

public class BaleiaQuebreblocosProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		int horizontalRadiusSquare = (int) 2 - 1;
		int verticalRadiusSquare = (int) 2 - 1;
		int yIterationsSquare = verticalRadiusSquare;
		for (int i = -yIterationsSquare; i <= yIterationsSquare; i++) {
			for (int xi = -horizontalRadiusSquare; xi <= horizontalRadiusSquare; xi++) {
				for (int zi = -horizontalRadiusSquare; zi <= horizontalRadiusSquare; zi++) {
					// Execute the desired statements within the square/cube
					if (world.getBlockState(BlockPos.containing(x + xi, y + 1, z + zi)).getDestroySpeed(world, BlockPos.containing(x + xi, y + 1, z + zi)) < 22
							&& 0 > world.getBlockState(BlockPos.containing(x + xi, y + 1, z + zi)).getDestroySpeed(world, BlockPos.containing(x + xi, y + 1, z + zi))
							&& world.getBlockState(BlockPos.containing(x + xi, y + i, z + zi)).getDestroySpeed(world, BlockPos.containing(x + xi, y + i, z + zi)) < 22
							&& 0 > world.getBlockState(BlockPos.containing(x + xi, y + i, z + zi)).getDestroySpeed(world, BlockPos.containing(x + xi, y + i, z + zi))) {
						{
							BlockPos _pos = BlockPos.containing(x + xi, y, z + zi);
							Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
							world.destroyBlock(_pos, false);
						}
						{
							BlockPos _pos = BlockPos.containing(x + xi, y + 1, z + zi);
							Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
							world.destroyBlock(_pos, false);
						}
					}
				}
			}
		}
	}
}
