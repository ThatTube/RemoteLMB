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
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.ArrayList;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class WaveEffect {
    // IDs ATUALIZADOS - O segredo estava no "glove" no singular!
    private static final ResourceLocation SHOCK_DAMAGE_TYPE = new ResourceLocation("morebosses", "shock");
    private static final ResourceLocation MONSTROUSFOOTWEAR_BOOTS = new ResourceLocation("morebosses", "monstrousfootwear_boots");
    private static final ResourceLocation GERADOR_DE_WAVES = new ResourceLocation("morebosses", "gerador_de_waves");
    private static final ResourceLocation COPPER_GLOVE = new ResourceLocation("morebosses", "copper_glove");
    
    public static void createShockwave(Level world, BlockPos center, int maxRadius, int baseDamage) {
        for (int radius = 2; radius <= maxRadius; radius++) {
            for (BlockPos pos : getRing(center, radius)) {
                MorebossesMod.queueServerWork(radius * 4, () -> {
                    // Partículas
                    if (world instanceof ServerLevel _level)
                        _level.getServer().getCommands().performPrefixedCommand(
                                new CommandSourceStack(CommandSource.NULL, new Vec3(pos.getX() + 0.5, (pos.getY() + 1.1), pos.getZ() + 0.5), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
                                ("/particle sweep_attack ~ ~ ~ 0 0 0 1 0 normal"));

                    // Dano e Imunidade
                    AABB aabb = new AABB(pos).inflate(3.0, 1.5, 3.0);
                    for (Entity entity : world.getEntitiesOfClass(LivingEntity.class, aabb)) {
                        
                        if (entity instanceof Player player) {
                            // --- CHECAGEM DE IMUNIDADE ---
                            
                            // 1. Botas (Funcionando como antes)
                            boolean hasBoots = ForgeRegistries.ITEMS.getKey(player.getInventory().getArmor(0).getItem()).equals(MONSTROUSFOOTWEAR_BOOTS);
                            
                            // 2. Copper Glove (Agora no singular e checando a Mão Principal)
                            boolean hasGlove = ForgeRegistries.ITEMS.getKey(player.getMainHandItem().getItem()).equals(COPPER_GLOVE);
                            
                            // 3. Gerador de Waves (Inventário completo, ignora cooldown)
                            boolean hasGerador = false;
                            for (ItemStack item : player.getInventory().items) {
                                if (!item.isEmpty() && ForgeRegistries.ITEMS.getKey(item.getItem()).equals(GERADOR_DE_WAVES)) {
                                    hasGerador = true;
                                    break;
                                }
                            }
                            if (!hasGerador) {
                                hasGerador = ForgeRegistries.ITEMS.getKey(player.getOffhandItem().getItem()).equals(GERADOR_DE_WAVES);
                            }

                            // Se QUALQUER um for verdadeiro, o player é imune
                            if (hasBoots || hasGlove || hasGerador) {
                                continue; 
                            }
                        }

                        if (entity.getTags().contains("lmb:immunetoshockwave")) continue;
                        
                        // Cálculo de distância e dano
                        double dx = entity.getX() - center.getX();
                        double dz = entity.getZ() - center.getZ();
                        double distance = Math.sqrt(dx * dx + dz * dz);
                        double distanceFactor = Math.max(0.25, 1.0 - (distance / maxRadius));
                        
                        entity.hurt(createShockDamageSource(world), (int)(baseDamage * distanceFactor));
                        entity.setDeltaMovement(entity.getDeltaMovement().x(), 1.1 * distanceFactor, entity.getDeltaMovement().z());
                    }

                    // Efeito de blocos saltando
                    if (!world.isEmptyBlock(pos) && world.getBlockState(pos).getBlock() != Blocks.WATER) {
                        if (world instanceof ServerLevel _level) {
                            String blockName = ForgeRegistries.BLOCKS.getKey(world.getBlockState(pos).getBlock()).toString();
                            _level.getServer().getCommands().performPrefixedCommand(
                                    new CommandSourceStack(CommandSource.NULL, new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
                                    ("/summon falling_block ~ ~1 ~ {BlockState:{Name:\"" + blockName + "\"},Time:1,Motion:[0.0,0.2,0.0]}"));
                            _level.getServer().getCommands().performPrefixedCommand(
                                    new CommandSourceStack(CommandSource.NULL, new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
                                    ("/fill ~ ~ ~ ~ ~ ~ air replace"));
                        }
                    }
                });
            }
        }
    }

    private static DamageSource createShockDamageSource(Level world) {
        var damageTypeRegistry = world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        var shockHolder = damageTypeRegistry.getHolder(net.minecraft.resources.ResourceKey.create(Registries.DAMAGE_TYPE, SHOCK_DAMAGE_TYPE));
        if (shockHolder.isPresent()) return new DamageSource(shockHolder.get());
        return new DamageSource(damageTypeRegistry.getHolderOrThrow(DamageTypes.EXPLOSION));
    }

    private static Iterable<BlockPos> getRing(BlockPos center, int radius) {
        List<BlockPos> positions = new ArrayList<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist >= radius - 0.5 && dist <= radius + 0.5) {
                    positions.add(center.offset(dx, 0, dz));
                }
            }
        }
        return positions;
    }
}