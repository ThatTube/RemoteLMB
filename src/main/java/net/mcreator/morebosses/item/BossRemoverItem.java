package net.mcreator.morebosses.item;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

import java.util.Optional;
import java.util.List;

public class BossRemoverItem extends Item {
	public BossRemoverItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.morebosses.boss_remover.description_0"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);

		// Executa o procedimento original

		// Só executa no servidor
		if (!world.isClientSide()) {
			// Encontra a entidade que o jogador está mirando
			Entity target = getEntityLookingAt(player, 10.0D);

			if (target != null && target instanceof LivingEntity) {
				LivingEntity livingTarget = (LivingEntity) target;

				// Verifica se tem as tags de boss/miniboss
				if (hasBossTag(livingTarget)) {
					// Remove a entidade
					target.discard();

					// Feedback visual

					// Opcional: dá um efeito visual
					// (você pode adicionar partículas aqui se quiser)
				} else {

				}
			} else {

			}
		}

		return InteractionResultHolder.success(itemstack);
	}

	private Entity getEntityLookingAt(Player player, double range) {
		Level world = player.level();
		Vec3 eyePosition = player.getEyePosition(1.0F);
		Vec3 lookVector = player.getLookAngle();
		Vec3 endPosition = eyePosition.add(lookVector.x * range, lookVector.y * range, lookVector.z * range);

		// Primeiro, verifica se há alguma entidade diretamente no caminho
		EntityHitResult entityHitResult = findEntityHit(world, player, eyePosition, endPosition);
		if (entityHitResult != null) {
			return entityHitResult.getEntity();
		}

		return null;
	}

	private EntityHitResult findEntityHit(Level world, Player player, Vec3 start, Vec3 end) {
		// Cria uma caixa de detecção ao longo da linha de visão
		double range = start.distanceTo(end);
		AABB searchBox = new AABB(start, end).inflate(1.0D);

		// Lista todas as entidades próximas
		List<Entity> entities = world.getEntities(player, searchBox, e -> e instanceof LivingEntity && e.isPickable() && e != player);

		EntityHitResult closestHit = null;
		double closestDistance = range + 1.0;

		for (Entity entity : entities) {
			// Pega a caixa de colisão da entidade
			AABB entityBox = entity.getBoundingBox().inflate(0.3D);

			// Verifica se a linha de visão intersecta com a entidade
			Optional<Vec3> hitVec = entityBox.clip(start, end);

			if (hitVec.isPresent()) {
				double distance = start.distanceTo(hitVec.get());
				if (distance < closestDistance) {
					closestDistance = distance;
					closestHit = new EntityHitResult(entity, hitVec.get());
				}
			}
		}

		return closestHit;
	}

	private boolean hasBossTag(LivingEntity entity) {
		EntityType<?> entityType = entity.getType();

		// Cria as tags que queremos verificar
		TagKey<EntityType<?>> bossTag = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("lmb", "boss"));
		TagKey<EntityType<?>> miniBossTag = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("morebosses", "miniboss"));

		// Verifica se a entidade tem alguma das tags
		return entityType.is(bossTag) || entityType.is(miniBossTag);
	}
}
