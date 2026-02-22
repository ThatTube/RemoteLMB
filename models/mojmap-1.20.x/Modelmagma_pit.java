// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class Modelmagma_pit<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "magma_pit"), "main");
	private final ModelPart bone;

	public Modelmagma_pit(ModelPart root) {
		this.bone = root.getChild("bone");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone = partdefinition.addOrReplaceChild("bone",
				CubeListBuilder.create().texOffs(40, 27)
						.addBox(-11.0F, -2.0F, -1.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 63)
						.addBox(2.0F, -2.0F, -12.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(56, 35)
						.addBox(-13.0F, -3.0F, 3.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(52, 44)
						.addBox(10.0F, -3.0F, -9.0F, 4.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(28, 44)
						.addBox(8.0F, -4.0F, -3.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(52, 55)
						.addBox(10.0F, -3.0F, 0.0F, 5.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(56, 27)
						.addBox(-13.0F, -4.0F, -5.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(24, 56)
						.addBox(-11.0F, -5.0F, -9.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 55)
						.addBox(-8.0F, -3.0F, -11.0F, 7.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(40, 17)
						.addBox(-4.0F, -4.0F, -13.0F, 7.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(28, 33)
						.addBox(6.0F, -4.0F, -14.0F, 7.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)).texOffs(0, 33)
						.addBox(-10.0F, -5.0F, 6.0F, 7.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)).texOffs(0, 45)
						.addBox(-3.0F, -4.0F, 8.0F, 7.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(0, 17)
						.addBox(4.0F, -6.0F, 6.0F, 10.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-9.0F, -1.0F, -8.0F, 19.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}