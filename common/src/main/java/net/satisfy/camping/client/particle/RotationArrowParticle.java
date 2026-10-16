package net.satisfy.camping.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class RotationArrowParticle extends TextureSheetParticle {
    private final double centerX;
    private final double centerZ;
    private final double startAngle;
    private final double sweep;
    private final double radius;

    protected RotationArrowParticle(ClientLevel level, double x, double y, double z, double startAngle, double sweep, double radius, SpriteSet sprites) {
        super(level, x, y, z);
        this.centerX = x;
        this.centerZ = z;
        this.startAngle = startAngle;
        this.sweep = sweep;
        this.radius = radius;
        this.lifetime = 14;
        this.quadSize = 0.22F;
        this.hasPhysics = false;
        this.gravity = 0.0F;
        this.pickSprite(sprites);
        moveTo(0.0F);
        this.xo = this.x;
        this.zo = this.z;
    }

    private double angleAt(float progress) {
        float eased = 1.0F - (1.0F - progress) * (1.0F - progress);
        return startAngle + sweep * eased;
    }

    private void moveTo(float progress) {
        double angle = angleAt(progress);
        this.setPos(centerX + Math.cos(angle) * radius, this.y, centerZ + Math.sin(angle) * radius);
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
        moveTo(Math.min(1.0F, (float) this.age / (this.lifetime - 4)));
        this.alpha = this.age > this.lifetime - 4 ? (this.lifetime - this.age) / 4.0F : 1.0F;
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, @NotNull Camera camera, float partialTicks) {
        float progress = Mth.clamp((this.age + partialTicks) / (this.lifetime - 4), 0.0F, 1.0F);
        double angle = angleAt(progress);
        double sign = Math.signum(sweep);
        double tangentX = -Math.sin(angle) * sign;
        double tangentZ = Math.cos(angle) * sign;
        float yaw = (float) Math.atan2(-tangentZ, tangentX);
        renderRotatedQuad(buffer, camera, new Quaternionf().rotationY(yaw), partialTicks);
        renderRotatedQuad(buffer, camera, new Quaternionf().rotationY(yaw).rotateX(Mth.PI), partialTicks);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z, double startAngle, double sweep, double radius) {
            return new RotationArrowParticle(level, x, y, z, startAngle, sweep, radius, sprites);
        }
    }
}
