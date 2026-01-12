
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

public abstract class GreenMagmaFluid extends ForgeFlowingFluid {
	public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(() -> MorebossesModFluidTypes.GREEN_MAGMA_TYPE.get(), () -> MorebossesModFluids.GREEN_MAGMA.get(), () -> MorebossesModFluids.FLOWING_GREEN_MAGMA.get())
			.explosionResistance(100f).tickRate(15).levelDecreasePerBlock(2).bucket(() -> MorebossesModItems.GREEN_MAGMA_BUCKET.get()).block(() -> (LiquidBlock) MorebossesModBlocks.GREEN_MAGMA.get());

	private GreenMagmaFluid() {
		super(PROPERTIES);
	}

	public static class Source extends GreenMagmaFluid {
		public int getAmount(FluidState state) {
			return 8;
		}

		public boolean isSource(FluidState state) {
			return true;
		}
	}

	public static class Flowing extends GreenMagmaFluid {
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
