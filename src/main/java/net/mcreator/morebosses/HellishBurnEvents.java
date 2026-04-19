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

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.mcreator.morebosses.init.MorebossesModMobEffects;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingHealEvent;

@Mod.EventBusSubscriber
public class HellishBurnEvents {
    
    @SubscribeEvent
    public static void onEntityHeal(LivingHealEvent event) {
        // Verifica se a entidade que está tentando se curar tem o seu efeito
        // Substitua "MorebossesModMobEffects.HELLISH_BURN.get()" pelo local onde seu efeito está registrado
        if (event.getEntity().hasEffect(MorebossesModMobEffects.HELLISH_BURN.get())) {
            // Cancela a regeneração de vida
            event.setCanceled(true);
        }
    }
}
