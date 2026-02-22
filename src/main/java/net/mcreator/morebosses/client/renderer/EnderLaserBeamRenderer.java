package net.mcreator.morebosses.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.mcreator.morebosses.entity.EnderLaserBeamEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class EnderLaserBeamRenderer extends EntityRenderer<EnderLaserBeamEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("morebosses:textures/entities/laser_beam.png");
    private static final float TEXTURE_WIDTH = 256;
    private static final float TEXTURE_HEIGHT = 32;
    private static final float START_RADIUS = 0.75f;
    private static final float BEAM_RADIUS = 0.75F;
    private boolean clearerView = false;

    public EnderLaserBeamRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(EnderLaserBeamEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(EnderLaserBeamEntity laserBeam, float entityYaw, float delta, PoseStack poseStack, 
                      MultiBufferSource buffer, int packedLight) {
        
        clearerView = laserBeam.caster instanceof Player && 
                     Minecraft.getInstance().player == laserBeam.caster && 
                     Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON;

        double collidePosX = laserBeam.prevCollidePosX + (laserBeam.collidePosX - laserBeam.prevCollidePosX) * delta;
        double collidePosY = laserBeam.prevCollidePosY + (laserBeam.collidePosY - laserBeam.prevCollidePosY) * delta;
        double collidePosZ = laserBeam.prevCollidePosZ + (laserBeam.collidePosZ - laserBeam.prevCollidePosZ) * delta;
        
        double posX = laserBeam.xo + (laserBeam.getX() - laserBeam.xo) * delta;
        double posY = laserBeam.yo + (laserBeam.getY() - laserBeam.yo) * delta;
        double posZ = laserBeam.zo + (laserBeam.getZ() - laserBeam.zo) * delta;
        
        float yaw = laserBeam.prevYaw + (laserBeam.renderYaw - laserBeam.prevYaw) * delta;
        float pitch = laserBeam.prevPitch + (laserBeam.renderPitch - laserBeam.prevPitch) * delta;

        float length = (float) Math.sqrt(Math.pow(collidePosX - posX, 2) + 
                                        Math.pow(collidePosY - posY, 2) + 
                                        Math.pow(collidePosZ - posZ, 2));

        int frame = Math.min(5, (laserBeam.tickCount / 2) % 6);
        
        VertexConsumer vertexBuilder = buffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(laserBeam)));

        // Renderizar o feixe principal
        renderBeam(length, 180f / (float) Math.PI * yaw, 180f / (float) Math.PI * pitch, 
                  frame, poseStack, vertexBuilder, packedLight);

        // Renderizar o ponto de impacto
        poseStack.pushPose();
        poseStack.translate(collidePosX - posX, collidePosY - posY, collidePosZ - posZ);
        renderEnd(frame, laserBeam.blockSide, poseStack, vertexBuilder, packedLight);
        poseStack.popPose();
        
        // Renderizar o ponto de origem
        renderStart(frame, poseStack, vertexBuilder, packedLight);
    }

    private void renderFlatQuad(int frame, PoseStack poseStack, VertexConsumer builder, int packedLight) {
        float minU = 0 + 16F / TEXTURE_WIDTH * frame;
        float minV = 0;
        float maxU = minU + 16F / TEXTURE_WIDTH;
        float maxV = minV + 16F / TEXTURE_HEIGHT;
        
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        
        drawVertex(matrix4f, matrix3f, builder, -START_RADIUS, -START_RADIUS, 0, minU, minV, 1, packedLight);
        drawVertex(matrix4f, matrix3f, builder, -START_RADIUS, START_RADIUS, 0, minU, maxV, 1, packedLight);
        drawVertex(matrix4f, matrix3f, builder, START_RADIUS, START_RADIUS, 0, maxU, maxV, 1, packedLight);
        drawVertex(matrix4f, matrix3f, builder, START_RADIUS, -START_RADIUS, 0, maxU, minV, 1, packedLight);
    }

    private void renderStart(int frame, PoseStack poseStack, VertexConsumer builder, int packedLight) {
        if (clearerView) return;
        
        poseStack.pushPose();
        Quaternionf quat = this.entityRenderDispatcher.cameraOrientation();
        poseStack.mulPose(quat);
        renderFlatQuad(frame, poseStack, builder, packedLight);
        poseStack.popPose();
    }

    private void renderEnd(int frame, Direction side, PoseStack poseStack, VertexConsumer builder, int packedLight) {
        poseStack.pushPose();
        Quaternionf quat = this.entityRenderDispatcher.cameraOrientation();
        poseStack.mulPose(quat);
        renderFlatQuad(frame, poseStack, builder, packedLight);
        poseStack.popPose();
        
        if (side == null) return;
        
        poseStack.pushPose();
        Quaternionf sideQuat = side.getRotation();
        sideQuat.mul(new Quaternionf().rotationX((float) Math.toRadians(90)));
        poseStack.mulPose(sideQuat);
        poseStack.translate(0, 0, -0.01f);
        renderFlatQuad(frame, poseStack, builder, packedLight);
        poseStack.popPose();
    }

    private void drawBeam(float length, int frame, PoseStack poseStack, VertexConsumer builder, int packedLight) {
        float minU = 0;
        float minV = 16 / TEXTURE_HEIGHT + (1 / TEXTURE_HEIGHT) * frame;
        float maxU = minU + 20 / TEXTURE_WIDTH;
        float maxV = minV + 1 / TEXTURE_HEIGHT;
        
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        
        float offset = clearerView ? -1 : 0;
        
        drawVertex(matrix4f, matrix3f, builder, -BEAM_RADIUS, offset, 0, minU, minV, 1, packedLight);
        drawVertex(matrix4f, matrix3f, builder, -BEAM_RADIUS, length, 0, minU, maxV, 1, packedLight);
        drawVertex(matrix4f, matrix3f, builder, BEAM_RADIUS, length, 0, maxU, maxV, 1, packedLight);
        drawVertex(matrix4f, matrix3f, builder, BEAM_RADIUS, offset, 0, maxU, minV, 1, packedLight);
    }

    private void renderBeam(float length, float yaw, float pitch, int frame, PoseStack poseStack, 
                           VertexConsumer builder, int packedLight) {
        poseStack.pushPose();
        
        // Rotacionar para alinhar o feixe
        poseStack.mulPose(new Quaternionf().rotationX((float) Math.toRadians(90)));
        poseStack.mulPose(new Quaternionf().rotationZ((float) Math.toRadians(yaw - 90)));
        poseStack.mulPose(new Quaternionf().rotationX((float) Math.toRadians(-pitch)));
        
        poseStack.pushPose();
        if (!clearerView) {
            poseStack.mulPose(new Quaternionf().rotationY(
                (float) Math.toRadians(Minecraft.getInstance().gameRenderer.getMainCamera().getXRot() + 90)));
        }
        drawBeam(length, frame, poseStack, builder, packedLight);
        poseStack.popPose();

        if (!clearerView) {
            poseStack.pushPose();
            poseStack.mulPose(new Quaternionf().rotationY(
                (float) Math.toRadians(-Minecraft.getInstance().gameRenderer.getMainCamera().getXRot() - 90)));
            drawBeam(length, frame, poseStack, builder, packedLight);
            poseStack.popPose();
        }
        
        poseStack.popPose();
    }

    private void drawVertex(Matrix4f matrix, Matrix3f normals, VertexConsumer vertexBuilder, 
                           float offsetX, float offsetY, float offsetZ, 
                           float textureX, float textureY, float alpha, int packedLight) {
        vertexBuilder
                .vertex(matrix, offsetX, offsetY, offsetZ)
                .color(1, 1, 1, 1 * alpha)
                .uv(textureX, textureY)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normals, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }
}