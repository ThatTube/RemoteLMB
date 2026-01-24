package net.mcreator.morebosses.item;

import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.tags.TagKey;
import net.minecraft.stats.Stats;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

import net.mcreator.morebosses.init.MorebossesModEntities;
import net.mcreator.morebosses.entity.CopperEyeEntity;

public class MonstrosityEyeItem extends Item {
	private final TagKey<Structure> structureTag;

	// Construtor com tag (para uso manual)
	public MonstrosityEyeItem(TagKey<Structure> structureTag) {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
		this.structureTag = structureTag;
	}

	// Construtor sem parâmetros (para MCreator)
	public MonstrosityEyeItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
		// Cria a tag "morebosses:test" inline
		this.structureTag = TagKey.create(net.minecraft.core.registries.Registries.STRUCTURE, new net.minecraft.resources.ResourceLocation("morebosses", "workshop"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (player.getCooldowns().isOnCooldown(this)) {
			return InteractionResultHolder.fail(itemstack);
		}
		if (!level.isClientSide()) {
			ServerLevel serverlevel = (ServerLevel) level;
			BlockPos blockpos = serverlevel.findNearestMapStructure(structureTag, player.blockPosition(), 10000, false);
			if (blockpos != null) {
				// Método MAIS SIMPLES: Usar shoot() existente
				CopperEyeEntity eyeEntity = new CopperEyeEntity(MorebossesModEntities.COPPER_EYE.get(), player.getX(), player.getY() + player.getEyeHeight() * 0.8, player.getZ(), level);
				// Ajustar direção para a estrutura
				double dx = blockpos.getX() - player.getX();
				double dy = blockpos.getY() - player.getY();
				double dz = blockpos.getZ() - player.getZ();
				double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
				if (distance > 0) {
					eyeEntity.setDeltaMovement((dx / distance) * 0.5, (dy / distance + 0.3) * 0.5, // Leve curva para cima
							(dz / distance) * 0.5);
				}
				// ⚡ CONFIGURAÇÕES PARA ATRAVESSAR
				eyeEntity.setNoGravity(true);
				eyeEntity.setSilent(true);
				eyeEntity.setNoPhysics(true); // ✨ ESSENCIAL PARA ATRAVESSAR
				eyeEntity.setInvulnerable(true);
				level.addFreshEntity(eyeEntity);
				// ✨ ESSENCIAL: Tirar a gravidade!
				eyeEntity.setNoGravity(true);
				// Efeitos
				level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
				level.levelEvent(null, 1003, player.blockPosition(), 0);
				player.getCooldowns().addCooldown(this, 40);
				player.awardStat(Stats.ITEM_USED.get(this));
				player.swing(hand, true);
				int dist = (int) distance;
				return InteractionResultHolder.success(itemstack);
			} else {
				player.getCooldowns().addCooldown(this, 20);
				return InteractionResultHolder.fail(itemstack);
			}
		}
		player.swing(hand, true);
		return InteractionResultHolder.consume(itemstack);
	}
}
