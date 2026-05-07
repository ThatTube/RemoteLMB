// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class ModelDagger<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "dagger"), "main");
	private final ModelPart bone3;
	private final ModelPart bone2;
	private final ModelPart bone;

	public ModelDagger(ModelPart root) {
		this.bone3 = root.getChild("bone3");
		this.bone2 = this.bone3.getChild("bone2");
		this.bone = this.bone3.getChild("bone");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone3 = partdefinition.addOrReplaceChild("bone3", CubeListBuilder.create(),
				PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition bone2 = bone3.addOrReplaceChild("bone2", CubeListBuilder.create(),
				PartPose.offset(-1.0F, 2.0F, 0.0F));

		PartDefinition cube_r1 = bone2.addOrReplaceChild("cube_r1",
				CubeListBuilder.create().texOffs(-16, 0).addBox(-8.0F, -0.75F, -9.0F, 16.0F, 0.0F, 16.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 1.5708F, 0.0F, -2.3562F));

		PartDefinition bone = bone3
				.addOrReplaceChild("bone",
						CubeListBuilder.create().texOffs(-16, 0).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 0.0F, 16.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.3562F, 0.0F));

		return LayerDefinition.create(meshdefinition, 16, 16);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		bone3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}