package net.mcreator.morebosses.entity;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.util.RandomSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.nbt.CompoundTag;

import net.mcreator.morebosses.init.MorebossesModItems;
import net.mcreator.morebosses.init.MorebossesModEntities;

import java.util.stream.Stream;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class CurveSickleOldEntity extends AbstractArrow implements ItemSupplier {
    public static final ItemStack PROJECTILE_ITEM = new ItemStack(MorebossesModItems.OLD_STEEL_CURVESICKLE.get());
    
    private ItemStack weaponStack = PROJECTILE_ITEM.copy();
    private int ticksInAir = 0;
    private boolean isReturning = false;
    private boolean hasHitTarget = false;

    public CurveSickleOldEntity(PlayMessages.SpawnEntity packet, Level world) {
        super(MorebossesModEntities.CURVE_SICKLE_OLD.get(), world);
    }

    public CurveSickleOldEntity(EntityType<? extends CurveSickleOldEntity> type, Level world) {
        super(type, world);
    }

    public CurveSickleOldEntity(EntityType<? extends CurveSickleOldEntity> type, double x, double y, double z, Level world) {
        super(type, x, y, z, world);
    }

    public CurveSickleOldEntity(EntityType<? extends CurveSickleOldEntity> type, LivingEntity entity, Level world) {
        super(type, entity, world);
        if (entity != null) {
            this.weaponStack = entity.getMainHandItem().copy();
            // Se tiver Muscular Memory, define o nível de piercing para atravessar entidades
            if (this.shouldForceReturn()) {
                this.setPierceLevel((byte) 5); 
            }
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("WeaponStack", this.weaponStack.save(new CompoundTag()));
        compound.putBoolean("IsReturning", this.isReturning);
        compound.putBoolean("HasHitTarget", this.hasHitTarget);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("WeaponStack")) {
            this.weaponStack = ItemStack.of(compound.getCompound("WeaponStack"));
        }
        this.isReturning = compound.getBoolean("IsReturning");
        this.hasHitTarget = compound.getBoolean("HasHitTarget");
        
        // Re-aplica o pierce ao carregar a entidade se o NBT indicar o encantamento
        if (this.shouldForceReturn()) {
            this.setPierceLevel((byte) 5);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack getItem() {
        return this.weaponStack.isEmpty() ? PROJECTILE_ITEM : this.weaponStack;
    }

    @Override
    protected ItemStack getPickupItem() {
        return this.weaponStack.copy();
    }

    private boolean shouldForceReturn() {
        return EnchantmentHelper.getEnchantments(this.weaponStack).keySet().stream()
                .anyMatch(ench -> {
                    ResourceLocation res = ForgeRegistries.ENCHANTMENTS.getKey(ench);
                    return res != null && res.toString().equals("morebosses:muscular_memory");
                });
    }

    @Override
    public void tick() {
        super.tick();
        this.ticksInAir++;

        boolean forceReturn = shouldForceReturn();

        if (this.inGround) {
            if (forceReturn) {
                this.inGround = false;
                this.isReturning = true;
            } else {
                this.dropAsItem();
                return;
            }
        }

        // Inicia o retorno após 20 ticks (permitindo que atravesse mobs no caminho)
        if (this.ticksInAir > 20) {
            this.isReturning = true;
        }

        if (this.isReturning) {
            this.setNoGravity(true);
            if (this.getOwner() instanceof LivingEntity owner && owner.isAlive()) {
                Vec3 targetPos = owner.position().add(0, owner.getEyeHeight() * 0.75, 0);
                Vec3 direction = targetPos.subtract(this.position()).normalize();
                this.setDeltaMovement(direction.scale(1.4));

                if (this.distanceTo(owner) < 1.6) {
                    this.dropAsItem();
                }
            } else {
                this.isReturning = false;
                this.setNoGravity(false);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (result.getEntity() == this.getOwner()) {
            if (this.isReturning) this.dropAsItem();
            return;
        }

        if (shouldForceReturn()) {
            // Se tiver Muscular Memory, marcamos o acerto para lógica interna, 
            // mas não ativamos o retorno imediato nem cancelamos o movimento
            this.hasHitTarget = true;
            super.onHitEntity(result); 
        } else {
            // Comportamento normal: acerta um e volta
            super.onHitEntity(result);
            this.hasHitTarget = true;
            this.isReturning = true;
        }
    }

    private void dropAsItem() {
        if (!this.level().isClientSide) {
            ItemEntity item = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), this.weaponStack.copy());
            item.setPickUpDelay(3);
            this.level().addFreshEntity(item);
        }
        this.discard();
    }

    @Override
    protected void doPostHurtEffects(LivingEntity entity) {
        super.doPostHurtEffects(entity);
        // Remove a flecha visual do corpo do mob atingido
        entity.setArrowCount(entity.getArrowCount() - 1);
    }

    public static CurveSickleOldEntity shoot(Level world, LivingEntity entity, RandomSource source) {
        return shoot(world, entity, source, 1f, 6, 1);
    }

    public static CurveSickleOldEntity shoot(Level world, LivingEntity entity, RandomSource random, float power, double damage, int knockback) {
        CurveSickleOldEntity entityarrow = new CurveSickleOldEntity(MorebossesModEntities.CURVE_SICKLE_OLD.get(), entity, world);
        entityarrow.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z, power * 2, 0);
        entityarrow.setSilent(true);
        entityarrow.setBaseDamage(damage);
        entityarrow.setKnockback(knockback);
        world.addFreshEntity(entityarrow);
        world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.arrow.shoot")), SoundSource.PLAYERS, 1, 1f / (random.nextFloat() * 0.5f + 1) + (power / 2));
        return entityarrow;
    }
}