package net.mcreator.morebosses.procedures;

import virtuoel.pehkui.api.ScaleTypes;
import virtuoel.pehkui.api.ScaleData;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingEvent;

import net.minecraft.world.entity.Entity;

import net.mcreator.morebosses.entity.MaxolotEntity;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class MacholoteNadaDiminuirHitBoxProcedure {
    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        execute(event, event.getEntity());
    }

    public static void execute(Entity entity) {
        execute(null, entity);
    }

    private static void execute(@Nullable Event event, Entity entity) {
        if (entity == null || !(entity instanceof MaxolotEntity))
            return;
        
        ScaleData hitboxHeight = ScaleTypes.HITBOX_HEIGHT.getScaleData(entity);
        
        if (entity.isSwimming()) {
            // Define a escala para 1/3 da altura original (para ter altura 1)
            hitboxHeight.setTargetScale(1.0F / 3.0F);
        } else {
            // Restaura para altura normal (escala 1.0 = altura 3)
            hitboxHeight.setTargetScale(1.0F);
        }
    }
}