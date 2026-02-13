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
public class STS {

    private static final ConcurrentHashMap<String, Boolean> playerAchievementStatus = new ConcurrentHashMap<>();
    private static final ResourceLocation SANCTUARY_ADVANCEMENT = new ResourceLocation("morebosses", "sanctuary");
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        tickCounter++;
        if (tickCounter % 20 == 0) { // Verifica a cada 1 segundo (20 ticks)
            // Pegamos o servidor diretamente do evento
            for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
                checkAndGrantAchievement(player);
            }
        }
    }

    private static void checkAndGrantAchievement(ServerPlayer player) {
        String playerId = player.getStringUUID();
        
        if (hasAchievementCached(player)) return;

        if (isNearSanctuary(player)) {
            grantAchievement(player);
            playerAchievementStatus.put(playerId, true);
        }
    }

    private static boolean isNearSanctuary(ServerPlayer player) {
        BlockPos center = player.blockPosition();
        // Escaneia uma área ao redor do jogador procurando o bloco 'sdb'
        for (int x = -15; x <= 15; x += 3) {
            for (int y = -5; y <= 5; y += 2) {
                for (int z = -15; z <= 15; z += 3) {
                    BlockPos check = center.offset(x, y, z);
                    var block = player.level().getBlockState(check).getBlock();
                    var blockId = ForgeRegistries.BLOCKS.getKey(block);
                    
                    if (blockId != null && blockId.toString().equals("morebosses:sdb")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // MÉTODO QUE ESTAVA FALTANDO: Verifica se o player já tem o avanço
    private static boolean hasAchievement(ServerPlayer player) {
        Advancement adv = player.getServer().getAdvancements().getAdvancement(SANCTUARY_ADVANCEMENT);
        if (adv == null) return false;
        return player.getAdvancements().getOrStartProgress(adv).isDone();
    }

    // MÉTODO QUE ESTAVA FALTANDO: Dá o avanço ao player
    private static void grantAchievement(ServerPlayer player) {
        Advancement adv = player.getServer().getAdvancements().getAdvancement(SANCTUARY_ADVANCEMENT);
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