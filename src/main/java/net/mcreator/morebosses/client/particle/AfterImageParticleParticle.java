package net.mcreator.morebosses.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class AfterImageParticleParticle {

    // Método provider que o MCreator espera (recebe SpriteSet e retorna ParticleProvider)
    @OnlyIn(Dist.CLIENT)
    public static ParticleProvider<SimpleParticleType> provider(SpriteSet spriteSet) {
        return new Factory();
    }

    // ------------------------------------------------------------
    // CLASSE DA PARTÍCULA (o afterimage em si)
    // ------------------------------------------------------------
    @OnlyIn(Dist.CLIENT)
    public static class Instance extends Particle {
        private final int entityId;
        private final float colorR, colorG, colorB;
        private final boolean ghost;

        protected Instance(ClientLevel level, double x, double y, double z,
                           int r, int g, int b, int entityId, boolean ghost, int lifetime) {
            super(level, x, y, z);
            this.entityId = entityId;
            this.ghost = ghost;
            this.lifetime = lifetime;
            this.rCol = r;
            this.gCol = g;
            this.bCol = b;
            this.colorR = r / 255.0f;
            this.colorG = g / 255.0f;
            this.colorB = b / 255.0f;
        }

        @Override
        public void tick() {
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            if (this.age++ >= this.lifetime) {
                this.remove();
                return;
            }
            Entity entity = getEntity();
            if (entity == null || !entity.isAlive()) {
                this.remove();
            }
        }

        private Entity getEntity() {
            return entityId == -1 ? null : level.getEntity(entityId);
        }

        @Override
        public void render(@NotNull VertexConsumer dummy, @NotNull Camera camera, float partialTicks) {
            Entity entity = getEntity();
            if (!(entity instanceof LivingEntity living)) return;

            Minecraft mc = Minecraft.getInstance();
            MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
            Vec3 camPos = camera.getPosition();
            EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
            PoseStack poseStack = new PoseStack();

            double lerpX = Mth.lerp(partialTicks, this.xo, this.x);
            double lerpY = Mth.lerp(partialTicks, this.yo, this.y);
            double lerpZ = Mth.lerp(partialTicks, this.zo, this.z);

            float alpha = 0.5f / (Math.abs(age) + 1);

            var rendererRaw = dispatcher.getRenderer(living);
            if (!(rendererRaw instanceof LivingEntityRenderer<?, ?>)) return;
            @SuppressWarnings("unchecked")
            LivingEntityRenderer<LivingEntity, ?> renderer = (LivingEntityRenderer<LivingEntity, ?>) rendererRaw;

            boolean flag = !living.isInvisible();
            boolean flag1 = !flag && !living.isInvisibleTo(mc.player);
            boolean flag2 = mc.shouldEntityAppearGlowing(living);
            ResourceLocation texture = renderer.getTextureLocation(living);
            RenderType renderType;
            if (flag1) {
                renderType = RenderType.itemEntityTranslucentCull(texture);
            } else if (flag) {
                renderType = ghost ? RenderType.entityTranslucent(texture) : RenderType.entityTranslucent(texture);
            } else {
                renderType = flag2 ? RenderType.outline(texture) : null;
            }
            if (renderType == null) return;

            MultiBufferSource tintedSource = (rt) -> {
                VertexConsumer original = bufferSource.getBuffer(rt);
                return new VertexConsumer() {
                    @Override
                    public VertexConsumer vertex(double x, double y, double z) {
                        original.vertex(x, y, z);
                        return this;
                    }

                    @Override
                    public VertexConsumer color(int r, int g, int b, int a) {
                        original.color((int)(r * colorR), (int)(g * colorG), (int)(b * colorB), (int)(a * alpha));
                        return this;
                    }

                    @Override
                    public VertexConsumer uv(float u, float v) {
                        original.uv(u, v);
                        return this;
                    }

                    @Override
                    public VertexConsumer overlayCoords(int u, int v) {
                        original.overlayCoords(u, v);
                        return this;
                    }

                    @Override
                    public VertexConsumer uv2(int u, int v) {
                        original.uv2(u, v);
                        return this;
                    }

                    @Override
                    public VertexConsumer normal(float x, float y, float z) {
                        original.normal(x, y, z);
                        return this;
                    }

                    @Override
                    public void endVertex() {
                        original.endVertex();
                    }

                    @Override
                    public void defaultColor(int r, int g, int b, int a) {
                        original.defaultColor(r, g, b, a);
                    }

                    @Override
                    public void unsetDefaultColor() {
                        original.unsetDefaultColor();
                    }
                };
            };

            dispatcher.render(living,
                    lerpX - camPos.x,
                    lerpY - camPos.y,
                    lerpZ - camPos.z,
                    living.getYRot(),
                    partialTicks,
                    poseStack,
                    tintedSource,
                    dispatcher.getPackedLightCoords(living, partialTicks));
        }

        @Override
        public @NotNull ParticleRenderType getRenderType() {
            return ParticleRenderType.CUSTOM;
        }
    }

    // ------------------------------------------------------------
    // FACTORY (obrigatório para o registro do MCreator)
    // ------------------------------------------------------------
    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            // Decodifica parâmetros (exemplo, adapte conforme necessário)
            int r = (int) xSpeed;
            int g = (int) ySpeed;
            int b = (int) zSpeed;
            int entityId = (int) (xSpeed * 100) % 1000;
            boolean ghost = ySpeed > 100;
            int lifetime = (int) zSpeed % 100;
            return new Instance(level, x, y, z, r, g, b, entityId, ghost, lifetime);
        }
    }

    // ------------------------------------------------------------
    // MÉTODO PÚBLICO PARA CRIAR UM AFTERIMAGE (chame no lado cliente)
    // ------------------------------------------------------------
    @OnlyIn(Dist.CLIENT)
    public static void spawn(ClientLevel level, double x, double y, double z,
                             int r, int g, int b, int entityId, boolean ghost, int lifetime) {
        Instance particle = new Instance(level, x, y, z, r, g, b, entityId, ghost, lifetime);
        Minecraft.getInstance().particleEngine.add(particle);
    }
}