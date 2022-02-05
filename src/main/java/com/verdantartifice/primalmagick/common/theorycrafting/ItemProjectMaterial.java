package com.verdantartifice.primalmagick.common.theorycrafting;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.verdantartifice.primalmagick.common.research.CompoundResearchKey;
import com.verdantartifice.primalmagick.common.util.InventoryUtils;
import com.verdantartifice.primalmagick.common.util.ItemUtils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

/**
 * Definition of a project material that requires a specific item stack, which may or may not be
 * consumed as part of the project.
 * 
 * @author Daedalus4096
 */
public class ItemProjectMaterial extends AbstractProjectMaterial {
    public static final String TYPE = "item";
    public static final IProjectMaterialSerializer<ItemProjectMaterial> SERIALIZER = new ItemProjectMaterial.Serializer();
    
    protected ItemStack stack;
    protected boolean consumed;
    protected boolean matchNBT;
    
    public ItemProjectMaterial() {
        super();
        this.stack = ItemStack.EMPTY;
        this.consumed = false;
        this.matchNBT = false;
    }
    
    public ItemProjectMaterial(@Nonnull ItemStack stack, boolean consumed, boolean matchNBT) {
        super();
        this.stack = stack;
        this.consumed = consumed;
        this.matchNBT = matchNBT;
    }
    
    public ItemProjectMaterial(@Nonnull ItemStack stack, boolean consumed) {
        this(stack, consumed, false);
    }
    
    public ItemProjectMaterial(@Nonnull IItemProvider item, boolean consumed, boolean matchNBT) {
        this(new ItemStack(item), consumed, matchNBT);
    }
    
    public ItemProjectMaterial(@Nonnull IItemProvider item, boolean consumed) {
        this(item, consumed, false);
    }
    
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = super.serializeNBT();
        tag.put("Stack", this.stack.write(new CompoundNBT()));
        tag.putBoolean("Consumed", this.consumed);
        tag.putBoolean("MatchNBT", this.matchNBT);
        return tag;
    }
    
    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        this.stack = ItemStack.read(nbt.getCompound("Stack"));
        this.consumed = nbt.getBoolean("Consumed");
        this.matchNBT = nbt.getBoolean("MatchNBT");
    }

    @Override
    protected String getMaterialType() {
        return TYPE;
    }

    @Override
    public boolean isSatisfied(PlayerEntity player) {
        return InventoryUtils.isPlayerCarrying(player, this.stack, this.matchNBT);
    }

    @Override
    public boolean consume(PlayerEntity player) {
        // Remove this material's item from the player's inventory if it's supposed to be consumed
        if (this.consumed) {
            return InventoryUtils.consumeItem(player, this.stack, this.matchNBT);
        } else {
            return true;
        }
    }
    
    @Nonnull
    public ItemStack getItemStack() {
        return this.stack;
    }
    
    @Override
    public boolean isConsumed() {
        return this.consumed;
    }
    
    @Override
    public AbstractProjectMaterial copy() {
        ItemProjectMaterial material = new ItemProjectMaterial();
        material.stack = this.stack.copy();
        material.consumed = this.consumed;
        material.matchNBT = this.matchNBT;
        material.selected = this.selected;
        material.weight = this.weight;
        if (this.requiredResearch != null) {
            material.requiredResearch = this.requiredResearch.copy();
        }
        return material;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (consumed ? 1231 : 1237);
        result = prime * result + (matchNBT ? 1231 : 1237);
        result = prime * result + ((stack == null) ? 0 : stack.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ItemProjectMaterial other = (ItemProjectMaterial) obj;
        if (consumed != other.consumed)
            return false;
        if (matchNBT != other.matchNBT)
            return false;
        if (stack == null) {
            if (other.stack != null)
                return false;
        } else if (!ItemStack.areItemStacksEqual(this.stack, other.stack))
            return false;
        return true;
    }

    public static class Serializer implements IProjectMaterialSerializer<ItemProjectMaterial> {
        @Override
        public ItemProjectMaterial read(ResourceLocation projectId, JsonObject json) {
            ItemStack stack = ItemUtils.parseItemStack(json.getAsJsonPrimitive("stack").getAsString());
            if (stack == null || stack.isEmpty()) {
                throw new JsonSyntaxException("Invalid item stack for material in project " + projectId.toString());
            }
            
            boolean consumed = json.getAsJsonPrimitive("consumed").getAsBoolean();
            boolean matchNbt = json.has("match_nbt") ? json.getAsJsonPrimitive("match_nbt").getAsBoolean() : false;
            
            ItemProjectMaterial retVal = new ItemProjectMaterial(stack, consumed, matchNbt);
            
            retVal.setWeight(json.getAsJsonPrimitive("weight").getAsDouble());
            if (json.has("required_research")) {
                retVal.setRequiredResearch(CompoundResearchKey.parse(json.getAsJsonPrimitive("required_research").getAsString()));
            }
            
            return retVal;
        }
    }
}
