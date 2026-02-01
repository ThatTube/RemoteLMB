package net.mcreator.morebosses.entity;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.util.RandomSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;

import net.mcreator.morebosses.procedures.MissileProjectileHitsBlockProcedure;
import net.mcreator.morebosses.init.MorebossesModEntities;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class MissileEntity extends AbstractArrow implements ItemSupplier {
    public static final ItemStack PROJECTILE_ITEM = new ItemStack(Blocks.REDSTONE_BLOCK);

    public MissileEntity(PlayMessages.SpawnEntity packet, Level world) {
        super(MorebossesModEntities.MISSILE.get(), world);
    }

    public MissileEntity(EntityType<? extends MissileEntity> type, Level world) {
        super(type, world);
    }

    public MissileEntity(EntityType<? extends MissileEntity> type, double x, double y, double z, Level world) {
        super(type, x, y, z, world);
    }

    public MissileEntity(EntityType<? extends MissileEntity> type, LivingEntity entity, Level world) {
        super(type, entity, world);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack getItem() {
        return PROJECTILE_ITEM;
    }

    @Override
    protected ItemStack getPickupItem() {
        return PROJECTILE_ITEM;
    }

    @Override
    protected void doPostHurtEffects(LivingEntity entity) {
        super.doPostHurtEffects(entity);
        entity.setArrowCount(entity.getArrowCount() - 1);
    }

    @Override
    public void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        MissileProjectileHitsBlockProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ());
    }

    @Override
    public void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        MissileProjectileHitsBlockProcedure.execute(this.level(), blockHitResult.getBlockPos().getX(), blockHitResult.getBlockPos().getY(), blockHitResult.getBlockPos().getZ());
    }

    @Override
    public void tick() {
        // Salvar velocidade atual antes do tick
        double prevMotionX = this.getDeltaMovement().x;
        double prevMotionY = this.getDeltaMovement().y;
        double prevMotionZ = this.getDeltaMovement().z;
        
        // Executar tick normal
        super.tick();
        
        // Se estava na água, restaurar a velocidade anterior
        if (this.isInWater()) {
            // Restaurar velocidade (ignorar arrasto da água)
            this.setDeltaMovement(prevMotionX, prevMotionY, prevMotionZ);
            
            // Também evitar que flutue/afunde muito
            if (Math.abs(prevMotionY) < 0.01) {
                this.setDeltaMovement(prevMotionX, -0.02, prevMotionZ); // Manter trajetória descendente suave
            }
        }
        
        if (this.inGround)
            this.discard();
    }

    // Método mais simples - sobrescrever a lógica de movimento na água
    @Override
    protected void doWaterSplashEffect() {
        // Não fazer efeito de splash na água
    }
    
    @Override
    public boolean isInWater() {
        // Verificar se está na água, mas não aplicar efeitos
        return super.isInWater();
    }

    public static MissileEntity shoot(Level world, LivingEntity entity, RandomSource source) {
        return shoot(world, entity, source, 1f, 7, 1);
    }

    public static MissileEntity shoot(Level world, LivingEntity entity, RandomSource source, float pullingPower) {
        return shoot(world, entity, source, pullingPower * 1f, 7, 1);
    }

    public static MissileEntity shoot(Level world, LivingEntity entity, RandomSource random, float power, double damage, int knockback) {
        MissileEntity entityarrow = new MissileEntity(MorebossesModEntities.MISSILE.get(), entity, world);
        entityarrow.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z, power * 2, 0);
        entityarrow.setSilent(true);
        entityarrow.setCritArrow(false);
        entityarrow.setBaseDamage(damage);
        entityarrow.setKnockback(knockback);
        world.addFreshEntity(entityarrow);
        world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.tnt.primed")), SoundSource.PLAYERS, 1, 1f / (random.nextFloat() * 0.5f + 1) + (power / 2));
        return entityarrow;
    }

    public static MissileEntity shoot(LivingEntity entity, LivingEntity target) {
        MissileEntity entityarrow = new MissileEntity(MorebossesModEntities.MISSILE.get(), entity, entity.level());
        double dx = target.getX() - entity.getX();
        double dy = target.getY() + target.getEyeHeight() - 1.1;
        double dz = target.getZ() - entity.getZ();
        entityarrow.shoot(dx, dy - entityarrow.getY() + Math.hypot(dx, dz) * 0.2F, dz, 1f * 2, 12.0F);
        entityarrow.setSilent(true);
        entityarrow.setBaseDamage(7);
        entityarrow.setKnockback(1);
        entityarrow.setCritArrow(false);
        entity.level().addFreshEntity(entityarrow);
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.tnt.primed")), SoundSource.PLAYERS, 1, 1f / (RandomSource.create().nextFloat() * 0.5f + 1));
        return entityarrow;
    }
}