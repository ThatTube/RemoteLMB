package net.mcreator.morebosses.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;

import net.mcreator.morebosses.procedures.DashLifeEnderProcedure;
import net.mcreator.morebosses.init.MorebossesModItems;

import java.util.List;

public class TheLifeEnderItem extends SwordItem {
    public TheLifeEnderItem() {
        super(new Tier() {
            public int getUses() {
                return 3000;
            }
            public float getSpeed() {
                return 5f;
            }
            public float getAttackDamageBonus() {
                return 8f;
            }
            public int getLevel() {
                return 4;
            }
            public int getEnchantmentValue() {
                return 18;
            }
            public Ingredient getRepairIngredient() {
                return Ingredient.of(new ItemStack(MorebossesModItems.OLD_STEEL_INGOT.get()));
            }
        }, 3, -2.2f, new Item.Properties());
    }

    @Override
    public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, level, list, flag);
        list.add(Component.translatable("item.morebosses.the_life_ender.description_0"));
        list.add(Component.translatable("item.morebosses.the_life_ender.description_1"));
        list.add(Component.translatable("item.morebosses.the_life_ender.description_2"));
    }

    // 1. GATILHO: Diz ao jogo para começar a carregar o item ao clicar
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
        entity.startUsingItem(hand);
        return InteractionResultHolder.consume(entity.getItemInHand(hand));
    }

    // 2. DURAÇÃO: 32 é o tempo padrão (cerca de 1.5s)
    @Override
    public int getUseDuration(ItemStack itemstack) {
        return 32;
    }

    // 3. ANIMAÇÃO: Estilo Arco
    @Override
    public UseAnim getUseAnimation(ItemStack itemstack) {
        return UseAnim.BOW;
    }

    // 4. CONCLUSÃO: O que acontece quando termina de carregar
    @Override
    public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
        ItemStack retval = super.finishUsingItem(itemstack, world, entity);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        DashLifeEnderProcedure.execute(world, x, y, z, entity);
        return retval;
    }
}