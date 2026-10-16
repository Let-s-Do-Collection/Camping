package net.satisfy.camping.core.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.satisfy.camping.platform.PlatformHelper;

import java.util.List;

public class GrillingUtil {

    public record FoodValue(int nutrition, float saturationModifier) {
        @Override
        public String toString() {
            return "{Nutrition:" + nutrition + ",Saturation:" + saturationModifier + "}";
        }
    }

    private static final String GRILLED_KEY = "Grilled";
    private static final String NUTRITION_KEY = "GrilledNutrition";
    private static final String SATURATION_KEY = "GrilledSaturation";

    public static boolean isGrilled(ItemStack itemStack) {
        CustomData data = itemStack.get(DataComponents.CUSTOM_DATA);
        return data != null && data.contains(GRILLED_KEY);
    }

    public static void setGrilled(ItemStack itemStack) {
        CompoundTag tag = getOrCreateData(itemStack);
        tag.putBoolean(GRILLED_KEY, true);
        increaseFoodValue(itemStack);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static void increaseFoodValue(ItemStack itemStack) {
        FoodProperties food = itemStack.get(DataComponents.FOOD);
        if (food != null) {
            int newNutrition = (int) (food.nutrition() * PlatformHelper.getGrilledFoodMultiplier());
            float newSaturation = food.saturation() / (food.nutrition() * 2); // satpts = nutr * satmod * 2

            FoodProperties boosted = new FoodProperties.Builder()
                    .nutrition(newNutrition)
                    .saturationModifier(newSaturation)
                    .build();

            itemStack.set(DataComponents.FOOD, boosted);

            CompoundTag tag = getOrCreateData(itemStack);
            tag.putInt(NUTRITION_KEY, newNutrition);
            tag.putFloat(SATURATION_KEY, newSaturation);
            itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    public static FoodValue getAdditionalFoodValue(ItemStack itemStack) {
        CustomData data = itemStack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            CompoundTag tag = data.copyTag();
            if (tag.contains(NUTRITION_KEY) && tag.contains(SATURATION_KEY)) {
                int nutrition = tag.getInt(NUTRITION_KEY);
                float saturation = tag.getFloat(SATURATION_KEY);
                return new FoodValue(nutrition, saturation);
            }
        }
        return new FoodValue(0, 0.0F);
    }

    public static void addGrilledTooltip(ItemStack itemStack, List<Component> tooltip) {
        if (isGrilled(itemStack)) {
            int bonus = Math.round((PlatformHelper.getGrilledFoodMultiplier() - 1.0F) * 100);
            tooltip.add(Component.translatable("tooltip.camping.grilled").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.camping.grilled.nutrition", bonus).withStyle(ChatFormatting.BLUE));
        }
    }

    private static CompoundTag getOrCreateData(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? new CompoundTag() : data.copyTag();
    }
}
