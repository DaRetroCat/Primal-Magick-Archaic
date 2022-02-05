package com.verdantartifice.primalmagick.common.items.wands;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;
import com.verdantartifice.primalmagick.common.wands.IStaff;
import com.verdantartifice.primalmagick.common.wands.WandCap;
import com.verdantartifice.primalmagick.common.wands.WandCore;
import com.verdantartifice.primalmagick.common.wands.WandGem;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Item definition for a modular staff.  Modular staves are made up of cores, caps, and gems, and their
 * properties are determined by those components.  Staves also serve as basic melee weapons.
 * 
 * @author Daedalus4096
 */
public class ModularStaffItem extends ModularWandItem implements IStaff {
    protected final float attackDamage;
    protected final float attackSpeed;
    protected final Multimap<Attribute, AttributeModifier> attributeModifiers;

    public ModularStaffItem(int attackDamage, float attackSpeed, Properties properties) {
        super(properties);
        this.attackDamage = (float)attackDamage;
        this.attackSpeed = attackSpeed;
        Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double)this.attackSpeed, AttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }
    
    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        // A modular staff's display name is determined by its components (e.g. "Apprentice's Iron-Shod Heartwood Staff")
        WandCore core = this.getWandCore(stack);
        ITextComponent coreName = (core == null) ? new TranslationTextComponent("primalmagick.wand_core.unknown.name") : new TranslationTextComponent(core.getNameTranslationKey());
        WandCap cap = this.getWandCap(stack);
        ITextComponent capName = (cap == null) ? new TranslationTextComponent("primalmagick.wand_cap.unknown.name") : new TranslationTextComponent(cap.getNameTranslationKey());
        WandGem gem = this.getWandGem(stack);
        ITextComponent gemName = (gem == null) ? new TranslationTextComponent("primalmagick.wand_gem.unknown.name") : new TranslationTextComponent(gem.getNameTranslationKey());
        return new TranslationTextComponent("item.primalmagick.modular_staff", gemName, capName, coreName);
    }
    
    @Override
    protected int getCoreSpellSlotCount(WandCore core) {
        // Staves get double the normal spell slots provided by their core
        return (core == null) ? 0 : (2 * core.getSpellSlots());
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
        return equipmentSlot == EquipmentSlotType.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(equipmentSlot);
    }
}
