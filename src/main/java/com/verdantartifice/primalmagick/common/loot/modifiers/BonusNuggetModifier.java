package com.verdantartifice.primalmagick.common.loot.modifiers;

import java.util.List;

import com.google.gson.JsonObject;
import com.verdantartifice.primalmagick.common.enchantments.EnchantmentsPM;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

/**
 * Global loot modifier that gives a chance for bonus nuggets when mining quartz or metal ores.
 * 
 * @author Daedalus4096
 */
public class BonusNuggetModifier extends LootModifier {
    protected final float chance;
    protected final Item nugget;
    
    public BonusNuggetModifier(ILootCondition[] conditions, float chance, Item nugget) {
        super(conditions);
        this.chance = chance;
        this.nugget = nugget;
    }

    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        int count = 0;
        int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(EnchantmentsPM.LUCKY_STRIKE.get(), context.get(LootParameters.TOOL));
        for (int index = 0; index < enchantmentLevel; index++) {
            if (context.getRandom().nextFloat() < this.chance) {
                count++;
            }
        }
        if (count > 0) {
            generatedLoot.add(new ItemStack(this.nugget, count));
        }
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<BonusNuggetModifier> {
        @Override
        public BonusNuggetModifier read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition) {
            float chance = object.getAsJsonPrimitive("chance").getAsFloat();
            Item nugget = JSONUtils.getItem(object, "nugget");
            return new BonusNuggetModifier(ailootcondition, chance, nugget);
        }

        @Override
        public JsonObject write(BonusNuggetModifier instance) {
            JsonObject obj = this.makeConditions(instance.conditions);
            obj.addProperty("chance", instance.chance);
            
            ResourceLocation nuggetLoc = instance.nugget.getRegistryName();
            if (nuggetLoc == null) {
                throw new IllegalArgumentException("Invalid nugget " + instance.nugget);
            } else {
                obj.addProperty("nugget", nuggetLoc.toString());
            }
            
            return obj;
        }
    }
}
