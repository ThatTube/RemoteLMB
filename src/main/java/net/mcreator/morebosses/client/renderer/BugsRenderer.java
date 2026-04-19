
package net.mcreator.morebosses.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.HierarchicalModel;

import net.mcreator.morebosses.entity.BugsEntity;
import net.mcreator.morebosses.client.model.animations.bugsAnimation;
import net.mcreator.morebosses.client.model.Modelbugs;

public class BugsRenderer extends MobRenderer<BugsEntity, Modelbugs<BugsEntity>> {
	public BugsRenderer(EntityRendererProvider.Context context) {
		super(context, new AnimatedModel(context.bakeLayer(Modelbugs.LAYER_LOCATION)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(BugsEntity entity) {
		return new ResourceLocation("morebosses:textures/entities/bugsss.png");
	}

	private static final class AnimatedModel extends Modelbugs<BugsEntity> {
		private final ModelPart root;
		private final HierarchicalModel animator = new HierarchicalModel<BugsEntity>() {
			@Override
			public ModelPart root() {
				return root;
			}

			@Override
			public void setupAnim(BugsEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
				this.root().getAllParts().forEach(ModelPart::resetPose);
				this.animate(entity.animationState0, bugsAnimation.cafumaba, ageInTicks, 1f);
			}
		};

		public AnimatedModel(ModelPart root) {
			super(root);
			this.root = root;
		}

		@Override
		public void setupAnim(BugsEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
			animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		}
	}
}
