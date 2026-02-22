package net.mcreator.morebosses.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.mcreator.morebosses.init.MorebossesModEntities;

public class EnderLaserBeamEntity extends Entity {
    public static final double RADIUS = 30;
    public LivingEntity caster;
    public double endPosX, endPosY, endPosZ;
    public double collidePosX, collidePosY, collidePosZ;
    public double prevCollidePosX, prevCollidePosY, prevCollidePosZ;
    public float renderYaw, renderPitch;
    public boolean on = true;
    public Direction blockSide = null;

    private static final EntityDataAccessor<Float> YAW = SynchedEntityData.defineId(EnderLaserBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> PITCH = SynchedEntityData.defineId(EnderLaserBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DURATION = SynchedEntityData.defineId(EnderLaserBeamEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CASTER = SynchedEntityData.defineId(EnderLaserBeamEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(EnderLaserBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HPDAMAGE = SynchedEntityData.defineId(EnderLaserBeamEntity.class, EntityDataSerializers.FLOAT);

    public float prevYaw;
    public float prevPitch;
    
    // Lista de entidades que já foram danificadas neste tick para evitar múltiplos danos
    private List<Integer> damagedEntitiesThisTick = new ArrayList<>();

    public EnderLaserBeamEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(world);
    }

    public EnderLaserBeamEntity(EntityType<? extends EnderLaserBeamEntity> type, Level world) {
        super(type, world);
        noCulling = true;
    }

    public EnderLaserBeamEntity(Level world) {
        super(MorebossesModEntities.ENDER_LASER_BEAM.get(), world);
        noCulling = true;
    }

    public EnderLaserBeamEntity(Level world, LivingEntity caster, double x, double y, double z, float yaw, float pitch, int duration, float damage, float hpDamage) {
        this(world);
        this.caster = caster;
        this.setYaw(yaw);
        this.setPitch(pitch);
        this.setDuration(duration);
        this.setPos(x, y, z);
        this.setDamage(damage);
        this.setHpDamage(hpDamage);
        this.calculateEndPos();
        if (!world.isClientSide) {
            this.setCasterID(caster.getId());
        }
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
public void tick() {
    super.tick();
    prevCollidePosX = collidePosX;
    prevCollidePosY = collidePosY;
    prevCollidePosZ = collidePosZ;
    prevYaw = renderYaw;
    prevPitch = renderPitch;
    xo = getX();
    yo = getY();
    zo = getZ();
    
    if (tickCount == 1 && level().isClientSide) {
        caster = (LivingEntity) level().getEntity(getCasterID());
    }

    if (caster != null) {
        // Usar a rotação da cabeça do caster
        float headYaw = caster.yHeadRot;
        float headPitch = caster.getXRot();
        
        renderYaw = (float) ((headYaw + 90.0d) * Math.PI / 180.0d);
        renderPitch = (float) (-headPitch * Math.PI / 180.0d);
        
        // Também atualizar os dados sincronizados
        if (!level().isClientSide) {
            this.setYaw(renderYaw);
            this.setPitch(renderPitch);
        }
        
        // Atualizar posição para seguir o caster na altura correta
        this.setPos(caster.getX(), caster.getY() + 7.2, caster.getZ());
    }

    // ... resto do código ...

       // Na parte do dano, dentro do tick():
if (!level().isClientSide) {
    for (LivingEntity target : hitResult.entities) {
        System.out.println("Laser checking target: " + target.getName().getString());
        
        if (caster != null && !caster.isAlliedTo(target) && target != caster && 
            !damagedEntitiesThisTick.contains(target.getId())) {
            
            float baseDamage = this.getDamage();
            float hpPercentDamage = (float) (target.getMaxHealth() * this.getHpDamage() * 0.01);
            float totalDamage = baseDamage + Math.min(baseDamage, hpPercentDamage);
            
            System.out.println("Attempting to deal " + totalDamage + " damage to " + target.getName().getString());
            
            boolean damaged = target.hurt(target.damageSources().indirectMagic(this, caster), totalDamage);
            
            if (damaged) {
                damagedEntitiesThisTick.add(target.getId());
                System.out.println("Damage dealt successfully!");
                
                // Efeitos visuais
                if (level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                        net.minecraft.core.particles.ParticleTypes.SONIC_BOOM,
                        target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                        5, 0.2, 0.2, 0.2, 0.1
                    );
                }
            }
        }
    }
}}

    @Override
    protected void defineSynchedData() {
        this.entityData.define(YAW, 0F);
        this.entityData.define(PITCH, 0F);
        this.entityData.define(DURATION, 0);
        this.entityData.define(CASTER, -1);
        this.entityData.define(DAMAGE, 0F);
        this.entityData.define(HPDAMAGE, 0F);
    }

    public float getDamage() {
        return entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        entityData.set(DAMAGE, damage);
    }

    public float getHpDamage() {
        return entityData.get(HPDAMAGE);
    }

    public void setHpDamage(float damage) {
        entityData.set(HPDAMAGE, damage);
    }

    public float getYaw() {
        return entityData.get(YAW);
    }

    public void setYaw(float yaw) {
        entityData.set(YAW, yaw);
    }

    public float getPitch() {
        return entityData.get(PITCH);
    }

    public void setPitch(float pitch) {
        entityData.set(PITCH, pitch);
    }

    public int getDuration() {
        return entityData.get(DURATION);
    }

    public void setDuration(int duration) {
        entityData.set(DURATION, duration);
    }

    public int getCasterID() {
        return entityData.get(CASTER);
    }

    public void setCasterID(int id) {
        entityData.set(CASTER, id);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {}

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    private void calculateEndPos() {
        if (level().isClientSide()) {
            endPosX = getX() + RADIUS * Math.cos(renderYaw) * Math.cos(renderPitch);
            endPosZ = getZ() + RADIUS * Math.sin(renderYaw) * Math.cos(renderPitch);
            endPosY = getY() + RADIUS * Math.sin(renderPitch);
        } else {
            endPosX = getX() + RADIUS * Math.cos(getYaw()) * Math.cos(getPitch());
            endPosZ = getZ() + RADIUS * Math.sin(getYaw()) * Math.cos(getPitch());
            endPosY = getY() + RADIUS * Math.sin(getPitch());
        }
    }

    public LaserbeamHitResult raytraceEntities(Level world, Vec3 from, Vec3 to) {
    LaserbeamHitResult result = new LaserbeamHitResult();
    result.setBlockHit(world.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)));
    
    if (result.blockHit != null) {
        Vec3 hitVec = result.blockHit.getLocation();
        collidePosX = hitVec.x;
        collidePosY = hitVec.y;
        collidePosZ = hitVec.z;
        blockSide = result.blockHit.getDirection();
    } else {
        collidePosX = endPosX;
        collidePosY = endPosY;
        collidePosZ = endPosZ;
        blockSide = null;
    }
    
    // AUMENTAR A ÁREA DE BUSCA
    AABB searchBox = new AABB(
        Math.min(from.x, to.x) - 2.0, 
        Math.min(from.y, to.y) - 2.0, 
        Math.min(from.z, to.z) - 2.0,
        Math.max(from.x, to.x) + 2.0, 
        Math.max(from.y, to.y) + 2.0, 
        Math.max(from.z, to.z) + 2.0
    );
    
    List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, searchBox);
    System.out.println("Found " + entities.size() + " entities in search box");
    
    for (LivingEntity entity : entities) {
        if (entity == caster) continue;
        
        // Verificar se a entidade está perto da linha do laser
        AABB entityBox = entity.getBoundingBox().inflate(1.0);
        Optional<Vec3> hit = entityBox.clip(from, to);
        
        if (hit.isPresent()) {
            result.addEntityHit(entity);
            System.out.println("Entity hit by laser: " + entity.getName().getString());
        } else {
            // Verificar também se a entidade está muito perto da linha
            double distanceToLine = distanceToLine(from, to, entity.position());
            if (distanceToLine < 2.0) {
                result.addEntityHit(entity);
                System.out.println("Entity close to laser line: " + entity.getName().getString());
            }
        }
    }
    return result;
}

// Método auxiliar para calcular distância de um ponto a uma linha
private double distanceToLine(Vec3 lineStart, Vec3 lineEnd, Vec3 point) {
    Vec3 lineDir = lineEnd.subtract(lineStart);
    Vec3 pointDir = point.subtract(lineStart);
    
    double lineLength = lineDir.length();
    if (lineLength < 0.0001) return pointDir.length();
    
    double t = pointDir.dot(lineDir) / lineLength;
    t = Mth.clamp(t, 0, lineLength);
    
    Vec3 projection = lineStart.add(lineDir.scale(t / lineLength));
    return point.distanceTo(projection);
}

    @Override
    public void push(Entity entityIn) {}

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 1024;
    }

    public static class LaserbeamHitResult {
        private BlockHitResult blockHit;
        private final List<LivingEntity> entities = new ArrayList<>();

        public BlockHitResult getBlockHit() {
            return blockHit;
        }

        public void setBlockHit(HitResult rayTraceResult) {
            if (rayTraceResult.getType() == HitResult.Type.BLOCK)
                this.blockHit = (BlockHitResult) rayTraceResult;
        }

        public void addEntityHit(LivingEntity entity) {
            entities.add(entity);
        }
    }
}