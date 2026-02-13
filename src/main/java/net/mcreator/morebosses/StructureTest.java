package net.mcreator.morebosses;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;

import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = "morebosses")
public class StructureTest {

    private static final ConcurrentHashMap<String, Boolean> playerAchievementStatus = new ConcurrentHashMap<>();
    private static final ResourceLocation WORKSHOP_ADVANCEMENT = new ResourceLocation("morebosses", "the_underground_workshop");
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        tickCounter++;
        if (tickCounter % 20 == 0) { // Verifica a cada 1 segundo
            // Usa o servidor vindo do próprio evento para evitar erros de símbolo
            for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
                checkAndGrantAchievement(player);
            }
        }
    }

    private static void checkAndGrantAchievement(ServerPlayer player) {
        String playerId = player.getStringUUID();
        
        if (hasAchievementCached(player)) return;

        if (isNearWorkshop(player)) {
            grantAchievement(player);
            playerAchievementStatus.put(playerId, true);
        }
    }

    private static boolean isNearWorkshop(ServerPlayer player) {
        BlockPos center = player.blockPosition();
        
        for (int x = -15; x <= 15; x += 3) {
            for (int y = -5; y <= 5; y += 2) {
                for (int z = -15; z <= 15; z += 3) {
                    BlockPos check = center.offset(x, y, z);
                    var block = player.level().getBlockState(check).getBlock();
                    var blockId = ForgeRegistries.BLOCKS.getKey(block);
                    
                    if (blockId != null && blockId.toString().equals("morebosses:copper_structure_detect")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Método para verificar se o player já completou o Advancement
    private static boolean hasAchievement(ServerPlayer player) {
        Advancement adv = player.getServer().getAdvancements().getAdvancement(WORKSHOP_ADVANCEMENT);
        if (adv == null) return false;
        return player.getAdvancements().getOrStartProgress(adv).isDone();
    }

    // Método para conceder o Advancement ao player
    private static void grantAchievement(ServerPlayer player) {
        Advancement adv = player.getServer().getAdvancements().getAdvancement(WORKSHOP_ADVANCEMENT);
        if (adv != null) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(adv);
            if (!progress.isDone()) {
                for (String criteria : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(adv, criteria);
                }
            }
        }
    }

    private static boolean hasAchievementCached(ServerPlayer player) {
        String playerId = player.getStringUUID();
        return playerAchievementStatus.computeIfAbsent(playerId, id -> hasAchievement(player));
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            playerAchievementStatus.put(player.getStringUUID(), hasAchievement(player));
        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        playerAchievementStatus.remove(event.getEntity().getStringUUID());
    }
}