package net.mcreator.morebosses;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = MorebossesMod.MODID)
public class CopperStructureAchievement {
    
    private static final Set<String> completedPlayers = new HashSet<>();
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Verificações básicas
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide()) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        
        String playerId = player.getStringUUID();
        if (completedPlayers.contains(playerId)) return;
        
        // Performance: verifica a cada 3 segundos
        if (player.tickCount % 60 != 0) return;
        
        // Detecta bloco
        boolean foundBlock = detectWorkshopBlock(player);
        
        if (foundBlock) {
            completedPlayers.add(playerId);
            grantAchievementWithRetry(player);
        }
    }
    
    private static boolean detectWorkshopBlock(ServerPlayer player) {
        BlockPos playerPos = player.blockPosition();
        
        // Raio de 25 blocos
        for (int x = -25; x <= 25; x++) {
            for (int y = -10; y <= 10; y++) {
                for (int z = -25; z <= 25; z++) {
                    BlockPos checkPos = playerPos.offset(x, y, z);
                    var blockState = player.level().getBlockState(checkPos);
                    var block = blockState.getBlock();
                    var blockId = net.minecraftforge.registries.ForgeRegistries.BLOCKS.getKey(block);
                    
                    if (blockId != null && 
                        blockId.toString().equals("morebosses:copper_structure_detect")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private static void grantAchievementWithRetry(ServerPlayer player) {
        // Lista de IDs possíveis (em ordem de prioridade)
        String[] achievementIds = {
            "morebosses:the_underground_workshop",
            "morebosses:underground_workshop", 
            "morebosses:workshop",
            "morebosses:find_workshop"
        };
        
        boolean granted = false;
        
        for (String achievementId : achievementIds) {
            try {
                var advancement = player.server.getAdvancements()
                    .getAdvancement(ResourceLocation.tryParse(achievementId));
                
                if (advancement != null) {
                    System.out.println("✅ Advancement found: " + achievementId);
                    
                    var progress = player.getAdvancements().getOrStartProgress(advancement);
                    
                    if (!progress.isDone()) {
                        // Concede TODOS os critérios
                        for (String criterion : progress.getRemainingCriteria()) {
                            player.getAdvancements().award(advancement, criterion);
                        }
                        
                        System.out.println("🎉 Achievement granted to: " + player.getScoreboardName());
                        granted = true;
                        break;
                    } else {
                        System.out.println("⚠️ Player already has: " + achievementId);
                        granted = true;
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ Error with " + achievementId + ": " + e.getMessage());
            }
        }
        
        if (!granted) {
            System.err.println("🚨 NO workshop advancement could be granted!");
            debugAllAdvancements(player);
        }
    }
    
    private static void debugAllAdvancements(ServerPlayer player) {
        System.out.println("\n🔍 === DEBUG ALL ADVANCEMENTS ===");
        
        var allAdvancements = player.server.getAdvancements().getAllAdvancements();
        int total = 0;
        int morebossesCount = 0;
        
        for (var adv : allAdvancements) {
            total++;
            String id = adv.id().toString();
            
            if (id.contains("morebosses:")) {
                morebossesCount++;
                System.out.println("📦 " + id);
                
                // Verifica progresso do jogador
                var progress = player.getAdvancements().getOrStartProgress(adv);
                System.out.println("   Status: " + (progress.isDone() ? "COMPLETED" : "NOT DONE"));
                
                if (!progress.isDone()) {
                    System.out.println("   Missing criteria: " + progress.getRemainingCriteria());
                }
            }
        }
        
        System.out.println("\n📊 Total advancements: " + total);
        System.out.println("📊 More Bosses advancements: " + morebossesCount);
        System.out.println("================================\n");
    }
}