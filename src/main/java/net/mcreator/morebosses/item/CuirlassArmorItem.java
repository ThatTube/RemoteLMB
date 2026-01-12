
package net.mcreator.morebosses.item;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.init.MorebossesModItems;

public abstract class CuirlassArmorItem extends ArmorItem {
	public CuirlassArmorItem(ArmorItem.Type type, Item.Properties properties) {
		super(new ArmorMaterial() {
			@Override
			public int getDurabilityForType(ArmorItem.Type type) {
				return new int[]{13, 15, 16, 11}[type.getSlot().getIndex()] * 90;
			}

			@Override
			public int getDefenseForType(ArmorItem.Type type) {
				return new int[]{12, 30, 36, 12}[type.getSlot().getIndex()];
			}

			@Override
			public int getEnchantmentValue() {
				return 54;
			}

			@Override
			public SoundEvent getEquipSound() {
				return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.stone.place"));
			}

			@Override
			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(MorebossesModItems.STONE_CUIRLASS.get()));
			}

			@Override
			public String getName() {
				return "cuirlass_armor";
			}

			@Override
			public float getToughness() {
				return 5f;
			}

			@Override
			public float getKnockbackResistance() {
				return 2f;
			}
		}, type, properties);
	}

	public static class Helmet extends CuirlassArmorItem {
		public Helmet() {
			super(ArmorItem.Type.HELMET, new Item.Properties().fireResistant());
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "morebosses:textures/models/armor/cuirlass_layer_1.png";
		}
	}

	public static class Chestplate extends CuirlassArmorItem {
		public Chestplate() {
			super(ArmorItem.Type.CHESTPLATE, new Item.Properties().fireResistant());
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "morebosses:textures/models/armor/cuirlass_layer_1.png";
		}
	}

	public static class Leggings extends CuirlassArmorItem {
		public Leggings() {
			super(ArmorItem.Type.LEGGINGS, new Item.Properties().fireResistant());
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "morebosses:textures/models/armor/cuirlass_layer_2.png";
		}
	}

	public static class Boots extends CuirlassArmorItem {
		public Boots() {
			super(ArmorItem.Type.BOOTS, new Item.Properties().fireResistant());
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "morebosses:textures/models/armor/cuirlass_layer_1.png";
		}
	}
}
