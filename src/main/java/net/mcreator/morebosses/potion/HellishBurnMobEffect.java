package net.mcreator.morebosses.potion;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class HellishBurnMobEffect extends MobEffect {
    public HellishBurnMobEffect() {
        super(MobEffectCategory.HARMFUL, -3407872);
        // Mantém a redução de armadura original
        this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, "8d8cb9df-d8b2-35e2-bb4e-c88a141964aa", -1, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // Meio coração = 1.0F de dano. 
        // Aumenta em 1.0F por nível (amplifier), até o máximo de 3.0F (1 coração e meio).
        float damage = Math.min(3.0F, 1.0F + amplifier);
        
        // Aplica o dano mágico. 
        // Nota: Se estiver em uma versão anterior à 1.20, use: entity.hurt(DamageSource.MAGIC, damage);
        entity.hurt(entity.damageSources().burn(), damage);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Aplica o efeito (o dano) a cada 40 ticks (2 segundos)
        return duration % 40 == 0;
    }
}