package net.mcreator.morebosses;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = "morebosses")
public class StructureTest {
    
    // Thread-safe maps para multiplayer
    private static final ConcurrentHashMap<String, Boolean> playerAchievementStatus = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Integer> lastPositionCheck = new ConcurrentHashMap<>();
    
    @SubscribeEvent
    public static void monitorPlayer(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide()) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        
        String playerId = player.getStringUUID();
        int currentTick = player.tickCount;
        
        // Verifica a cada 3 segundos (60 ticks) quando fora da workshop
        // Verifica a cada 1 segundo (20 ticks) quando dentro da workshop
        Integer lastCheck = lastPositionCheck.get(playerId);
        boolean shouldCheck = false;
        
        if (lastCheck == null) {
            shouldCheck = true;
        } else {
            int checkInterval = isNearWorkshop(player) ? 20 : 60;
            shouldCheck = (currentTick - lastCheck) >= checkInterval;
        }
        
        if (!shouldCheck) return;
        
        lastPositionCheck.put(playerId, currentTick);
        
        // Se está perto da workshop e não tem a conquista
        if (isNearWorkshop(player) && !hasAchievement(player)) {
            grantAchievement(player);
            playerAchievementStatus.put(playerId, true);
        }
        
        // Opcional: atualiza cache periodicamente
        if (currentTick % 600 == 0) { // A cada 30 segundos
            playerAchievementStatus.put(playerId, hasAchievement(player));
        }
    }
    
    private static boolean isNearWorkshop(ServerPlayer player) {
        BlockPos pos = player.blockPosition();
        
        // Checagem RÁPIDA - amostra aleatória
        for (int i = 0; i < 50; i++) {
            int x = (int) (Math.random() * 50) - 25;
            int y = (int) (Math.random() * 20) - 5;
            int z = (int) (Math.random() * 50) - 25;
            
            BlockPos check = pos.offset(x, y, z);
            var block = player.level().getBlockState(check).getBlock();
            var blockId = net.minecraftforge.registries.ForgeRegistries.BLOCKS.getKey(block);
            
            if (blockId != null && 
                blockId.toString().equals("morebosses:copper_structure_detect")) {
                return true;
            }
        }
        
        return false;
    }
    
    private static boolean hasAchievement(ServerPlayer player) {
        // Primeiro tenta cache
        Boolean cached = playerAchievementStatus.get(player.getStringUUID());
        if (cached != null && cached) {
            return true;
        }
        
        // Se não em cache, verifica
        try {
            var adv = player.server.getAdvancements()
                .getAdvancement(new ResourceLocation("morebosses:the_underground_workshop"));
            
            if (adv != null) {
                var progress = player.getAdvancements().getOrStartProgress(adv);
                boolean hasIt = progress.isDone();
                playerAchievementStatus.put(player.getStringUUID(), hasIt);
                return hasIt;
            }
        } catch (Exception ignored) {}
        
        return false;
    }
    
    private static void grantAchievement(ServerPlayer player) {
        try {
            var adv = player.server.getAdvancements()
                .getAdvancement(new ResourceLocation("morebosses:the_underground_workshop"));
            
            if (adv != null) {
                var progress = player.getAdvancements().getOrStartProgress(adv);
                if (!progress.isDone()) {
                    for (String criterion : progress.getRemainingCriteria()) {
                        player.getAdvancements().award(adv, criterion);
                    }
                }
            }
        } catch (Exception ignored) {}
    }
    
    @SubscribeEvent
    public static void onPlayerJoin(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Atualiza cache ao entrar
            playerAchievementStatus.put(player.getStringUUID(), hasAchievement(player));
        }
    }
    
    @SubscribeEvent
    public static void onPlayerLeave(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            String id = event.getEntity().getStringUUID();
            playerAchievementStatus.remove(id);
            lastPositionCheck.remove(id);
        }
    }
}