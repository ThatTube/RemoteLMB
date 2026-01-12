/**
 * The code of this mod element is always locked.
 *
 * You can register new events in this class too.
 *
 * If you want to make a plain independent class, create it using
 * Project Browser -> New... and make sure to make the class
 * outside net.mcreator.morebosses as this package is managed by MCreator.
 *
 * If you change workspace package, modid or prefix, you will need
 * to manually adapt this file to these changes or remake it.
 *
 * This class will be added in the mod root package.
*/
package net.mcreator.morebosses;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

import java.util.List;
import java.util.ArrayList;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class WaveEffect {
    private static final net.minecraft.resources.ResourceLocation SHOCK_DAMAGE_TYPE = 
        new net.minecraft.resources.ResourceLocation("morebosses", "shock");
    private static final net.minecraft.resources.ResourceLocation MONSTROUSFOOTWEAR_BOOTS = 
        new net.minecraft.resources.ResourceLocation("morebosses", "monstrousfootwear_boots");
    
    public static void createShockwave(Level world, BlockPos center, int maxRadius, int baseDamage) {
        for (int radius = 2; radius <= maxRadius; radius++) {
            for (BlockPos pos : getRing(center, radius)) {
                //Replace "YourMod" with the actual Mod class, in my case it's "BoltzyBreezeMod"
                MorebossesMod.queueServerWork(radius * 4, () -> {
                    //Some Particles
                    if (world instanceof ServerLevel _level)
                        _level.getServer().getCommands().performPrefixedCommand(
                                new CommandSourceStack(CommandSource.NULL, new Vec3(pos.getX() + 0.5, (pos.getY() + 1.1), pos.getZ() + 0.5), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
                                ("/particle sweep_attack ~ ~ ~ 0 0 0 1 0 normal"));
                    // Displace the block at the current position
                    if (world.getBlockState(pos).getBlock() != Blocks.AIR) {
                        if (!((world.getBlockState(pos)).getBlock() == Blocks.WATER) && !world.isEmptyBlock(pos)) {
                            if (world instanceof ServerLevel _level)
                                _level.getServer().getCommands().performPrefixedCommand(
                                        new CommandSourceStack(CommandSource.NULL, new Vec3(pos.getX() + 0.5, (pos.getY()), pos.getZ() + 0.5), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
                                        ("/summon falling_block ~ ~1 ~ {BlockState:{Name:\"" + "" + ForgeRegistries.BLOCKS.getKey((world.getBlockState(pos)).getBlock()).toString() + "\"},Time:1,Motion:[" + "0.0,"
                                                + new java.text.DecimalFormat("##.##").format(0.2) + ",0.0" + "]}"));
                            if (world instanceof ServerLevel _level)
                                _level.getServer().getCommands().performPrefixedCommand(
                                        new CommandSourceStack(CommandSource.NULL, new Vec3(pos.getX() + 0.5, (pos.getY()), pos.getZ() + 0.5), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
                                        ("/fill ~ ~ ~ ~ ~ ~ air replace"));
                        }
                    }
                    // Damage entities within the current position
                    AABB aabb = new AABB(pos).inflate(3.0, 1.5, 3.0);
                    for (Entity entity : world.getEntitiesOfClass(LivingEntity.class, aabb)) {
                        // Verifica se a entidade tem a tag de imunidade
                        if (entity.getTags().contains("lmb:immunetoshockwave")) {
                            continue; // Pula esta entidade se tiver a tag
                        }
                        
                        // Verifica se é um jogador
                        if (entity instanceof Player player) {
                            // Verifica se o jogador tem o item GeradorDeWaves
                            boolean hasGeradorDeWaves = hasGeradorDeWaves(player);
                            
                            // Verifica se o jogador tem a bota MONSTROUSFOOTWEAR_BOOTS equipada
                            boolean hasMonstrousFootwearBoots = hasMonstrousFootwearBoots(player);
                            
                            // Se o jogador tiver GeradorDeWaves OU as botas, pula o dano
                            if (hasGeradorDeWaves || hasMonstrousFootwearBoots) {
                                continue;
                            }
                        }
                        
                        // Calcula a distância horizontal do centro da explosão
                        double dx = entity.getX() - center.getX();
                        double dz = entity.getZ() - center.getZ();
                        double distance = Math.sqrt(dx * dx + dz * dz);
                        
                        // Escala de dano ATUALIZADA para novo alcance (3 blocos)
                        double distanceFactor;
                        if (distance <= 2.0) {
                            distanceFactor = 1.0; // 100% de dano no centro (raio 2)
                        } else if (distance >= 3.0) {
                            distanceFactor = 0.25; // 25% de dano na borda (raio 3)
                        } else {
                            // Interpola linear entre 1.0 e 0.25 (de 2 a 3 blocos)
                            distanceFactor = 1.0 - (0.75 * ((distance - 2.0) / 1.0));
                        }
                        
                        // Calcula dano escalonado
                        int scaledDamage = (int)(baseDamage * distanceFactor);
                        if (scaledDamage < 1) scaledDamage = 1; // Mínimo 1 de dano
                        
                        // Cria fonte de dano de SHOCK
                        DamageSource shockDamage = createShockDamageSource(world);
                        
                        // Aplica dano
                        entity.hurt(shockDamage, scaledDamage);
                        
                        // Aplica empurrão para TODAS as entidades (exceto as com proteções)
                        // Empurrão também diminui com a distância
                        double pushFactor = distanceFactor; // Mesmo fator do dano
                        double pushY = 1.1 * pushFactor;
                        entity.setDeltaMovement(entity.getDeltaMovement().x(), pushY, entity.getDeltaMovement().z());
                    }
                });
            }
        }
    }
    
    // Verifica se jogador tem GeradorDeWaves
    private static boolean hasGeradorDeWaves(Player player) {
        for (ItemStack item : player.getInventory().items) {
            if (!item.isEmpty() && item.getItem().getDescriptionId().contains("gerador_de_waves")) {
                return true;
            }
        }
        return false;
    }
    
    // Verifica se jogador tem MONSTROUSFOOTWEAR_BOOTS equipada
    private static boolean hasMonstrousFootwearBoots(Player player) {
        // Verifica o slot de botas (slot 36 é o slot de botas no inventário do jogador)
        ItemStack boots = player.getInventory().getArmor(0); // Slot 0 = botas
        
        if (!boots.isEmpty()) {
            // Verifica se a bota é a MONSTROUSFOOTWEAR_BOOTS
            return ForgeRegistries.ITEMS.getKey(boots.getItem()).equals(MONSTROUSFOOTWEAR_BOOTS);
        }
        return false;
    }
    
    // Cria fonte de dano de SHOCK
    private static DamageSource createShockDamageSource(Level world) {
        // Tenta obter o holder para o dano customizado SHOCK
        var damageTypeRegistry = world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        var shockHolder = damageTypeRegistry.getHolder(net.minecraft.resources.ResourceKey.create(
            Registries.DAMAGE_TYPE, SHOCK_DAMAGE_TYPE));
        
        if (shockHolder.isPresent()) {
            // Usa dano customizado SHOCK
            return new DamageSource(shockHolder.get());
        } else {
            // Fallback para EXPLOSION se SHOCK não existir
            System.err.println("DamageType morebosses:shock não encontrado! Usando EXPLOSION como fallback.");
            var explosionHolder = damageTypeRegistry.getHolderOrThrow(DamageTypes.EXPLOSION);
            return new DamageSource(explosionHolder);
        }
    }

    // Helper method to get a ring of blocks at a specific radius
    private static Iterable<BlockPos> getRing(BlockPos center, int radius) {
        List<BlockPos> positions = new ArrayList<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (Math.sqrt(dx * dx + dz * dz) >= radius - 0.5 && Math.sqrt(dx * dx + dz * dz) <= radius + 0.5) {
                    positions.add(center.offset(dx, 0, dz));
                }
            }
        }
        return positions;
    }
}