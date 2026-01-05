
package net.mcreator.morebosses.fluid;

import net.minecraftforge.fluids.ForgeFlowingFluid;

import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.LiquidBlock;

import net.mcreator.morebosses.init.MorebossesModItems;
import net.mcreator.morebosses.init.MorebossesModFluids;
import net.mcreator.morebosses.init.MorebossesModFluidTypes;
import net.mcreator.morebosses.init.MorebossesModBlocks;

public abstract class RawMagmaFluid extends ForgeFlowingFluid {
	public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(() -> MorebossesModFluidTypes.RAW_MAGMA_TYPE.get(), () -> MorebossesModFluids.RAW_MAGMA.get(), () -> MorebossesModFluids.FLOWING_RAW_MAGMA.get())
			.explosionResistance(100f).tickRate(25).levelDecreasePerBlock(2).bucket(() -> MorebossesModItems.RAW_MAGMA_BUCKET.get()).block(() -> (LiquidBlock) MorebossesModBlocks.RAW_MAGMA.get());

	private RawMagmaFluid() {
		super(PROPERTIES);
	}

	public static class Source extends RawMagmaFluid {
		public int getAmount(FluidState state) {
			return 8;
		}

		public boolean isSource(FluidState state) {
			return true;
		}
	}

	public static class Flowing extends RawMagmaFluid {
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}

		public boolean isSource(FluidState state) {
			return false;
		}
	}
}
