// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class Modellaser_beam<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "laser_beam"), "main");
	private final ModelPart center;
	private final ModelPart whole;
	private final ModelPart body;
	private final ModelPart bb_main;

	public Modellaser_beam(ModelPart root) {
		this.center = root.getChild("center");
		this.whole = this.center.getChild("whole");
		this.body = this.whole.getChild("body");
		this.bb_main = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition center = partdefinition.addOrReplaceChild("center", CubeListBuilder.create(),
				PartPose.offset(0.0F, 16.0F, 16.0F));

		PartDefinition whole = center.addOrReplaceChild("whole", CubeListBuilder.create(),
				PartPose.offset(0.0F, 0.0F, -8.0F));

		PartDefinition body = whole.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F,
				-8.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -8.0F));

		PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(38, 120)
				.addBox(10.0F, 2.0F, 10.0F, -20.0F, -20.0F, -20.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 128);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		center.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}