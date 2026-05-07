package net.mcreator.morebosses.procedures;

import net.minecraftforge.network.NetworkDirection;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.Connection;
import net.minecraft.world.InteractionHand;

import net.mcreator.morebosses.init.MorebossesModItems;
import net.mcreator.morebosses.init.MorebossesModEntities;
import net.mcreator.morebosses.init.MorebossesModEnchantments;
import net.mcreator.morebosses.entity.EnergyDaggeEntity;
import net.mcreator.morebosses.MorebossesMod;

import java.util.List;

public class DaggershotProcedure {
    public static void execute(LevelAccessor world, Entity entity) {
        if (entity == null || !(entity instanceof LivingEntity _livingEntity))
            return;

        ItemStack itemstack = _livingEntity.getMainHandItem();
        
        if (!(itemstack.getItem() == MorebossesModItems.ENERGY_DAGGER.get()))
            return;

        int tripleShotLevel = EnchantmentHelper.getItemEnchantmentLevel(MorebossesModEnchantments.TRIPLE_SHOT.get(), itemstack);
        
        triggerAnimation(world, entity);

        if (tripleShotLevel > 0) {
            // Aumentei o ângulo para 15 graus para ficar mais visível a separação
            float[] angles = {-15.0F, 0.0F, 15.0F};
            for (float angle : angles) {
                shootProjectile(world, entity, angle);
            }
            
            if (entity instanceof Player _player)
                _player.getCooldowns().addCooldown(MorebossesModItems.ENERGY_DAGGER.get(), 100);
        } else {
            shootProjectile(world, entity, 0);
            
            if (entity instanceof Player _player)
                _player.getCooldowns().addCooldown(MorebossesModItems.ENERGY_DAGGER.get(), 50);
        }

        // Lógica de consumo de item (apenas 1)
        if (!(entity instanceof Player _player && _player.getAbilities().instabuild)) {
            itemstack.shrink(1);
            if (itemstack.isEmpty() && entity instanceof Player) {
                _livingEntity.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }
        }
    }

    private static void shootProjectile(LevelAccessor world, Entity entity, float angleOffset) {
        if (!world.isClientSide() && world instanceof Level projectileLevel) {
            EnergyDaggeEntity _entityToSpawn = new EnergyDaggeEntity(MorebossesModEntities.ENERGY_DAGGE.get(), projectileLevel);
            _entityToSpawn.setOwner(entity);
            _entityToSpawn.setBaseDamage(7);
            _entityToSpawn.setKnockback(1);
            _entityToSpawn.setSilent(true);
            _entityToSpawn.pickup = AbstractArrow.Pickup.ALLOWED;
            
            _entityToSpawn.setPos(entity.getX(), entity.getEyeY() - 0.1, entity.getZ());
            
            // --- NOVA LÓGICA DE ESPALHAMENTO ---
            
            // 1. Pegar o vetor de visão base do jogador
            Vec3 lookVec = entity.getLookAngle();
            
            // 2. Criar um vetor de rotação baseado no ângulo offset (convertido para radianos)
            // Rotacionamos apenas no eixo Y (horizontal)
            double radians = Math.toRadians(angleOffset);
            
            // 3. Aplicar a rotação matemática ao vetor de visão
            double cos = Math.cos(radians);
            double sin = Math.sin(radians);
            
            // Nova direção X e Z (fórmula de rotação 2D)
            double newX = lookVec.x * cos - lookVec.z * sin;
            double newZ = lookVec.x * sin + lookVec.z * cos;
            
            // Mantemos o Y original (para não alterar a altura do tiro)
            Vec3 spreadVec = new Vec3(newX, lookVec.y, newZ);
            
            // 4. Atirar usando o novo vetor rotacionado
            // Velocidade 1.5F, Incoerência (spread) 0 (queremos precisão no ângulo calculado)
            _entityToSpawn.shoot(spreadVec.x, spreadVec.y, spreadVec.z, 1.5F, 0);
            
            projectileLevel.addFreshEntity(_entityToSpawn);
        }
    }

    private static void triggerAnimation(LevelAccessor world, Entity entity) {
        if (world.isClientSide() && entity instanceof Player _player) {
            SetupAnimationsProcedure.setAnimationClientside(_player, "throw", true);
        } else if (!world.isClientSide() && world instanceof ServerLevel srvLvl) {
            List<Connection> connections = srvLvl.getServer().getConnection().getConnections();
            synchronized (connections) {
                for (Connection connection : connections) {
                    if (connection.isConnected())
                        MorebossesMod.PACKET_HANDLER.sendTo(new SetupAnimationsProcedure.MorebossesModAnimationMessage(Component.literal("throw"), entity.getId(), false), connection, NetworkDirection.PLAY_TO_CLIENT);
                }
            }
        }
    }
}