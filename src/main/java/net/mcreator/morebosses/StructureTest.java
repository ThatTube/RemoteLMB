package net.mcreator.morebosses;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = "morebosses")
public class StructureTest {
    
    private static final Set<String> testedPlayers = new HashSet<>();
    
    @SubscribeEvent
    public static void testDetection(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;
        
        String playerId = player.getStringUUID();
        if (testedPlayers.contains(playerId)) return;
        
        // Verifica a cada 5 segundos
        if (player.tickCount % 100 != 0) return;
        
        BlockPos pos = player.blockPosition();
        
        // TESTE 1: Verifica bloco específico
        boolean foundBlock = false;
        String foundBlockId = "";
        
        for (int x = -10; x <= 10; x++) {
            for (int y = -5; y <= 5; y++) {
                for (int z = -10; z <= 10; z++) {
                    BlockPos check = pos.offset(x, y, z);
                    var block = player.level().getBlockState(check).getBlock();
                    var blockId = net.minecraftforge.registries.ForgeRegistries.BLOCKS.getKey(block);
                    
                    if (blockId != null) {
                        // Mostra TODOS os blocos do seu mod próximos
                        if (blockId.toString().contains("morebosses:")) {
                            player.displayClientMessage(
                                Component.literal("§7Bloco do mod: §f" + blockId.toString()),
                                false
                            );
                            
                            if (blockId.toString().equals("morebosses:copper_structure_detect")) {
                                foundBlock = true;
                                foundBlockId = blockId.toString();
                            }
                        }
                    }
                }
            }
        }
        
        if (foundBlock) {
            player.displayClientMessage(
                Component.literal("§a✅ ENCONTREI O BLOCO DETECTOR: " + foundBlockId),
                false
            );
            testedPlayers.add(playerId);
            
            // Tenta dar a conquista
            try {
                var adv = player.server.getAdvancements()
                    .getAdvancement(new ResourceLocation("morebosses:the_underground_workshop"));
                
                if (adv != null) {
                    var progress = player.getAdvancements().getOrStartProgress(adv);
                    for (String criterion : progress.getRemainingCriteria()) {
                        player.getAdvancements().award(adv, criterion);
                    }
                    player.displayClientMessage(Component.literal("§a🎉 Conquista dada!"), false);
                } else {
                    player.displayClientMessage(Component.literal("§c❌ Conquista NÃO encontrada!"), false);
                }
            } catch (Exception e) {
                player.displayClientMessage(Component.literal("§cErro: " + e.getMessage()), false);
            }
        } else {
            player.displayClientMessage(
                Component.literal("§cNenhum bloco 'copper_structure_detect' encontrado em 10 blocos"),
                false
            );
        }
    }
}