
package net.mcreator.morebosses.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;

public class BlueSoulLightBlock extends Block {
	public BlueSoulLightBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.FROGLIGHT).strength(0.5f, 5f));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}
}
