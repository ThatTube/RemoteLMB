
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

public abstract class BobFluid extends ForgeFlowingFluid {
	public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(() -> MorebossesModFluidTypes.BOB_TYPE.get(), () -> MorebossesModFluids.BOB.get(), () -> MorebossesModFluids.FLOWING_BOB.get())
			.explosionResistance(100f).bucket(() -> MorebossesModItems.BOB_BUCKET.get()).block(() -> (LiquidBlock) MorebossesModBlocks.BOB.get());

	private BobFluid() {
		super(PROPERTIES);
	}

	public static class Source extends BobFluid {
		public int getAmount(FluidState state) {
			return 8;
		}

		public boolean isSource(FluidState state) {
			return true;
		}
	}

	public static class Flowing extends BobFluid {
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
