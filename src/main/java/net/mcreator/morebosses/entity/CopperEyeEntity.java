package net.mcreator.morebosses.entity;

import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.util.RandomSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import net.mcreator.morebosses.init.MorebossesModItems;
import net.mcreator.morebosses.init.MorebossesModEntities;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class CopperEyeEntity extends AbstractArrow implements ItemSupplier {
	public static final ItemStack PROJECTILE_ITEM = new ItemStack(MorebossesModItems.MONSTROSITY_EYE.get());

	public CopperEyeEntity(PlayMessages.SpawnEntity packet, Level world) {
		super(MorebossesModEntities.COPPER_EYE.get(), world);
		setupEntity();
	}

	public CopperEyeEntity(EntityType<? extends CopperEyeEntity> type, Level world) {
		super(type, world);
		setupEntity();
	}

	public CopperEyeEntity(EntityType<? extends CopperEyeEntity> type, double x, double y, double z, Level world) {
		super(type, x, y, z, world);
		setupEntity();
	}

	public CopperEyeEntity(EntityType<? extends CopperEyeEntity> type, LivingEntity entity, Level world) {
		super(type, entity, world);
		setupEntity();
	}

	private void setupEntity() {
		this.setNoGravity(true);
		this.setSilent(true);
		this.setInvulnerable(true);
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
	public void tick() {
		// ⚡ ATUALIZAÇÃO PERSONALIZADA
		this.baseTick(); // Atualizações básicas do entity
		// Atualizar posição MANUALMENTE
		Vec3 motion = this.getDeltaMovement();
		this.setPos(this.getX() + motion.x, this.getY() + motion.y, this.getZ() + motion.z);
		// ✨ PARTÍCULAS
		if (this.level().isClientSide) {
			BlockPos currentPos = this.blockPosition();
			boolean isInsideBlock = !this.level().isEmptyBlock(currentPos);
			if (isInsideBlock) {
				// 🔥 BRILHO dentro do bloco
				for (int i = 0; i < 4; ++i) {
					this.level().addParticle(ParticleTypes.GLOW, this.getX() + (this.random.nextDouble() - 0.5) * 0.4, this.getY() + (this.random.nextDouble() - 0.5) * 0.4, this.getZ() + (this.random.nextDouble() - 0.5) * 0.4, 0, 0, 0);
				}
				// Efeito especial ocasional
			} 
		}
		// ⏱️ ⏱️ ⏱️ TEMPO LIMITE: 5 SEGUNDOS (100 ticks) ⏱️ ⏱️ ⏱️
		if (this.tickCount >= 60) {
			discardWithEffects();
			return;
		}
		// 🔄 Diminuir velocidade gradualmente (opcional)
		
		// Nunca ficar preso no chão
		this.inGround = false;
	}

	// 🎇 MÉTODO PARA DESAPARECER COM EFEITOS
	private void discardWithEffects() {
		if (this.level().isClientSide) {
			// Efeito de desaparecimento
			for (int i = 0; i < 15; ++i) {
				this.level().addParticle(ParticleTypes.WAX_ON, this.getX() + (this.random.nextDouble() - 0.5) * 0.5, this.getY() + (this.random.nextDouble() - 0.5) * 0.5, this.getZ() + (this.random.nextDouble() - 0.5) * 0.5,
						(this.random.nextDouble() - 0.5) * 0.2, (this.random.nextDouble() - 0.5) * 0.2, (this.random.nextDouble() - 0.5) * 0.2);
			}
			// Partículas de brilho
			for (int i = 0; i < 8; ++i) {
				this.level().addParticle(ParticleTypes.GLOW, this.getX(), this.getY(), this.getZ(), (this.random.nextDouble() - 0.5) * 0.3, (this.random.nextDouble() - 0.5) * 0.3, (this.random.nextDouble() - 0.5) * 0.3);
			}
		}
		// Som de desaparecimento
		if (!this.level().isClientSide) {
			this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENDER_EYE_DEATH, SoundSource.AMBIENT, 0.3F, 1.0F);
		}
		this.discard();
	}

	// ✨ BRILHO VISUAL quando dentro de bloco
	@Override
	public float getLightLevelDependentMagicValue() {
		if (!this.level().isEmptyBlock(this.blockPosition())) {
			return 1.0F; // Brilho máximo dentro de bloco
		}
		return 0.7F; // Brilho normal
	}

	// ⚡ IGNORAR PAREDES (para não ficar preso)
	@Override
	public boolean isInWall() {
		return false;
	}

	// 🚫 IGNORAR FLUIDOS
	@Override
	public boolean isInWater() {
		return false;
	}

	@Override
	public boolean isInLava() {
		return false;
	}

	protected float getWaterSlowDown() {
		return 1.0f; // Sem redução na água
	}

	// 🔧 MÉTODOS DE DISPARO (mantidos do original)
	public static CopperEyeEntity shoot(Level world, LivingEntity entity, RandomSource source) {
		return shoot(world, entity, source, 1.0f, 0, 0);
	}

	public static CopperEyeEntity shoot(Level world, LivingEntity entity, RandomSource source, float pullingPower) {
		return shoot(world, entity, source, pullingPower * 1.0f, 0, 0);
	}

	public static CopperEyeEntity shoot(Level world, LivingEntity entity, RandomSource random, float power, double damage, int knockback) {
		CopperEyeEntity entityarrow = new CopperEyeEntity(MorebossesModEntities.COPPER_EYE.get(), entity, world);
		entityarrow.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z, power * 2, 0);
		entityarrow.setSilent(true);
		entityarrow.setCritArrow(false);
		entityarrow.setBaseDamage(damage);
		entityarrow.setKnockback(knockback);
		world.addFreshEntity(entityarrow);
		return entityarrow;
	}

	public static CopperEyeEntity shoot(LivingEntity entity, LivingEntity target) {
		CopperEyeEntity entityarrow = new CopperEyeEntity(MorebossesModEntities.COPPER_EYE.get(), entity, entity.level());
		double dx = target.getX() - entity.getX();
		double dy = target.getY() + target.getEyeHeight() - 1.1;
		double dz = target.getZ() - entity.getZ();
		entityarrow.shoot(dx, dy - entityarrow.getY() + Math.hypot(dx, dz) * 0.2F, dz, 1.5f * 2, 12.0F);
		entityarrow.setSilent(true);
		entityarrow.setBaseDamage(0);
		entityarrow.setKnockback(0);
		entityarrow.setCritArrow(false);
		entity.level().addFreshEntity(entityarrow);
		return entityarrow;
	}
}