package net.mcreator.morebosses.procedures;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.Minecraft;

import net.mcreator.morebosses.init.MorebossesModEntities;
import net.mcreator.morebosses.entity.YakEntity;

public class ReturnyakProcedure {
    // Mantemos o método aceitando Level para o MCreator não reclamar
    public static Entity execute(Level world) {
        // Se o mundo vier nulo, pegamos o mundo do lado do cliente (GUI)
        Level targetWorld = world;
        if (targetWorld == null) {
            targetWorld = Minecraft.getInstance().level;
        }

        if (targetWorld != null) {
            return new YakEntity(MorebossesModEntities.YAK.get(), targetWorld);
        }
        return null;
    }

    // Criamos uma versão SEM argumentos para corrigir o erro no YakGuyScreen.java
    public static Entity execute() {
        return execute(null);
    }
}