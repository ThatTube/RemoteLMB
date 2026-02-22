package net.mcreator.morebosses.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SimpleColoredParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected SimpleColoredParticle(ClientLevel level, double x, double y, double z, 
                                     double vx, double vy, double vz, SpriteSet sprites,
                                     int r, int g, int b) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = sprites;
        this.setSpriteFromAge(sprites);
        this.rCol = r / 255f;
        this.gCol = g / 255f;
        this.bCol = b / 255f;
        this.quadSize = 0.5f;
        this.lifetime = 20;
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        private final int r, g, b;

        public Provider(SpriteSet sprites, int r, int g, int b) {
            this.sprites = sprites;
            this.r = r;
            this.g = g;
            this.b = b;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new SimpleColoredParticle(level, x, y, z, vx, vy, vz, sprites, r, g, b);
        }
    }
}