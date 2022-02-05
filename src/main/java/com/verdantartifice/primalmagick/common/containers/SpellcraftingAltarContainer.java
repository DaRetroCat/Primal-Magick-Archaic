package com.verdantartifice.primalmagick.common.containers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.common.blocks.BlocksPM;
import com.verdantartifice.primalmagick.common.containers.slots.SpellScrollBlankSlot;
import com.verdantartifice.primalmagick.common.containers.slots.SpellcraftingResultSlot;
import com.verdantartifice.primalmagick.common.containers.slots.WandSlot;
import com.verdantartifice.primalmagick.common.crafting.SpellcraftingRecipe;
import com.verdantartifice.primalmagick.common.crafting.WandInventory;
import com.verdantartifice.primalmagick.common.items.wands.SpellScrollItem;
import com.verdantartifice.primalmagick.common.sources.SourceList;
import com.verdantartifice.primalmagick.common.spells.SpellComponent;
import com.verdantartifice.primalmagick.common.spells.SpellFactory;
import com.verdantartifice.primalmagick.common.spells.SpellManager;
import com.verdantartifice.primalmagick.common.spells.SpellPackage;
import com.verdantartifice.primalmagick.common.spells.SpellProperty;
import com.verdantartifice.primalmagick.common.spells.mods.ISpellMod;
import com.verdantartifice.primalmagick.common.spells.payloads.ISpellPayload;
import com.verdantartifice.primalmagick.common.spells.vehicles.ISpellVehicle;
import com.verdantartifice.primalmagick.common.wands.IWand;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

/**
 * Server data container for the spellcrafting altar GUI.
 * 
 * @author Daedalus4096
 */
public class SpellcraftingAltarContainer extends Container {
    protected static final ResourceLocation RECIPE_LOC = new ResourceLocation(PrimalMagick.MODID, "spellcrafting");

    protected final CraftingInventory scrollInv = new CraftingInventory(this, 1, 1);
    protected final WandInventory wandInv = new WandInventory(this);
    protected final CraftResultInventory resultInv = new CraftResultInventory();
    protected final IWorldPosCallable worldPosCallable;
    protected final PlayerEntity player;
    protected final Slot wandSlot;
    protected final Slot scrollSlot;
    
    protected String spellName = "";
    protected int spellVehicleTypeIndex = 0;
    protected int spellPayloadTypeIndex = 0;
    protected int spellPrimaryModTypeIndex = 0;
    protected int spellSecondaryModTypeIndex = 0;
    protected SpellPackage spellPackageCache = null;
    protected Map<SpellComponent, Map<String, Integer>> spellPropertyCache = new HashMap<>();

    public SpellcraftingAltarContainer(int windowId, PlayerInventory inv) {
        this(windowId, inv, IWorldPosCallable.DUMMY);
    }

    public SpellcraftingAltarContainer(int windowId, PlayerInventory inv, IWorldPosCallable callable) {
        super(ContainersPM.SPELLCRAFTING_ALTAR.get(), windowId);
        this.worldPosCallable = callable;
        this.player = inv.player;
        for (SpellComponent comp : SpellComponent.values()) {
            this.spellPropertyCache.put(comp, new HashMap<>());
        }
        
        // Slot 0: Result
        this.addSlot(new SpellcraftingResultSlot(this.player, this.scrollInv, this.wandInv, this::getManaCosts, this.resultInv, 0, 206, 8));
        
        // Slot 1: Input wand
        this.wandSlot = this.addSlot(new WandSlot(this.wandInv, 0, 8, 8, false));

        // Slot 2: Blank scroll
        this.scrollSlot = this.addSlot(new SpellScrollBlankSlot(this.scrollInv, 0, 160, 8));
        
        // Slots 3-29: Player backpack
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inv, j + (i * 9) + 9, 35 + (j * 18), 140 + (i * 18)));
            }
        }
        
        // Slots 30-38: Player hotbar
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inv, i, 35 + (i * 18), 198));
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(this.worldPosCallable, playerIn, BlocksPM.SPELLCRAFTING_ALTAR.get());
    }

    public SourceList getManaCosts() {
        return this.getSpellPackage().getManaCost();
    }
    
    public PlayerEntity getPlayer() {
        return this.player;
    }
    
    public SpellPackage getSpellPackage() {
        if (this.spellPackageCache == null) {
            this.spellPackageCache = this.makeFinalSpellPackage();
        }
        return this.spellPackageCache;
    }
    
    protected SpellPackage makeFinalSpellPackage() {
        // Assemble the final spell package from the input types and properties
        SpellPackage spell = new SpellPackage();
        spell.setName(this.getSpellName());
        spell.setVehicle(this.getSpellVehicleComponent());
        spell.setPayload(this.getSpellPayloadComponent());
        spell.setPrimaryMod(this.getSpellPrimaryModComponent());
        spell.setSecondaryMod(this.getSpellSecondaryModComponent());
        return spell;
    }
    
    public String getSpellName() {
        return (this.spellName == null || this.spellName.isEmpty()) ? this.getDefaultSpellName().getString() : this.spellName;
    }
    
    @Nonnull
    public ITextComponent getDefaultSpellName() {
        // Don't use getSpellPackage here, or it will cause infinite recursion
        ITextComponent vehiclePiece = this.getSpellVehicleComponent().getDefaultNamePiece();
        ITextComponent payloadPiece = this.getSpellPayloadComponent().getDefaultNamePiece();
        ITextComponent primaryModPiece = this.getSpellPrimaryModComponent().getDefaultNamePiece();
        ITextComponent secondaryModPiece = this.getSpellSecondaryModComponent().getDefaultNamePiece();
        boolean primaryActive = this.getSpellPrimaryModComponent().isActive();
        boolean secondaryActive = this.getSpellSecondaryModComponent().isActive();
        if (vehiclePiece == null || payloadPiece == null || vehiclePiece.getString().isEmpty() || payloadPiece.getString().isEmpty()) {
            // If the constructed spell is invalid, don't show a default name
            return new StringTextComponent("");
        } else if (!primaryActive && !secondaryActive) {
            // No mods selected
            return new TranslationTextComponent("primalmagick.spell.default_name_format.mods.0", vehiclePiece, payloadPiece);
        } else if (primaryActive && secondaryActive) {
            // Two mods selected
            return new TranslationTextComponent("primalmagick.spell.default_name_format.mods.2", vehiclePiece, payloadPiece, primaryModPiece, secondaryModPiece);
        } else if (primaryActive) {
            // Only a primary mod selected
            return new TranslationTextComponent("primalmagick.spell.default_name_format.mods.1", vehiclePiece, payloadPiece, primaryModPiece);
        } else {
            // Only a secondary mod selected
            return new TranslationTextComponent("primalmagick.spell.default_name_format.mods.1", vehiclePiece, payloadPiece, secondaryModPiece);
        }
    }
    
    public void setSpellName(String name) {
        // Clear the spell package cache and trigger a regeneration of the output item on change
        this.spellName = name;
        this.spellPackageCache = null;
        this.worldPosCallable.consume((world, blockPos) -> {
            this.slotChangedCraftingGrid(world);
        });
    }
    
    protected ISpellVehicle getSpellVehicleComponent() {
        // Construct a new spell vehicle from the saved type index and populate it with any cached properties
        ISpellVehicle retVal = SpellFactory.getVehicleFromType(SpellManager.getVehicleTypes(this.player).get(this.getSpellVehicleTypeIndex()));
        if (retVal != null) {
            for (Map.Entry<String, Integer> entry : this.spellPropertyCache.get(SpellComponent.VEHICLE).entrySet()) {
                if (retVal.getProperty(entry.getKey()) != null) {
                    retVal.getProperty(entry.getKey()).setValue(entry.getValue().intValue());
                }
            }
        }
        return retVal;
    }
    
    public int getSpellVehicleTypeIndex() {
        return this.spellVehicleTypeIndex;
    }
    
    public void setSpellVehicleTypeIndex(int index) {
        // Clear the spell package cache and trigger a regeneration of the output item on change
        index = MathHelper.clamp(index, 0, SpellManager.getVehicleTypes(this.player).size() - 1);
        this.spellVehicleTypeIndex = index;
        this.spellPackageCache = null;
        this.worldPosCallable.consume((world, blockPos) -> {
            this.slotChangedCraftingGrid(world);
        });
    }
    
    protected ISpellPayload getSpellPayloadComponent() {
        // Construct a new spell payload from the saved type index and populate it with any cached properties
        ISpellPayload retVal = SpellFactory.getPayloadFromType(SpellManager.getPayloadTypes(this.player).get(this.getSpellPayloadTypeIndex()));
        if (retVal != null) {
            for (Map.Entry<String, Integer> entry : this.spellPropertyCache.get(SpellComponent.PAYLOAD).entrySet()) {
                if (retVal.getProperty(entry.getKey()) != null) {
                    retVal.getProperty(entry.getKey()).setValue(entry.getValue().intValue());
                }
            }
        }
        return retVal;
    }
    
    public int getSpellPayloadTypeIndex() {
        return this.spellPayloadTypeIndex;
    }
    
    public void setSpellPayloadTypeIndex(int index) {
        // Clear the spell package cache and trigger a regeneration of the output item on change
        index = MathHelper.clamp(index, 0, SpellManager.getPayloadTypes(this.player).size() - 1);
        this.spellPayloadTypeIndex = index;
        this.spellPackageCache = null;
        this.worldPosCallable.consume((world, blockPos) -> {
            this.slotChangedCraftingGrid(world);
        });
    }
    
    protected ISpellMod getSpellPrimaryModComponent() {
        // Construct a new spell mod from the saved type index and populate it with any cached properties
        ISpellMod retVal = SpellFactory.getModFromType(SpellManager.getModTypes(this.player).get(this.getSpellPrimaryModTypeIndex()));
        if (retVal != null) {
            for (Map.Entry<String, Integer> entry : this.spellPropertyCache.get(SpellComponent.PRIMARY_MOD).entrySet()) {
                if (retVal.getProperty(entry.getKey()) != null) {
                    retVal.getProperty(entry.getKey()).setValue(entry.getValue().intValue());
                }
            }
        }
        return retVal;
    }
    
    public int getSpellPrimaryModTypeIndex() {
        return this.spellPrimaryModTypeIndex;
    }
    
    public void setSpellPrimaryModTypeIndex(int index) {
        // Clear the spell package cache and trigger a regeneration of the output item on change
        index = MathHelper.clamp(index, 0, SpellManager.getModTypes(this.player).size() - 1);
        this.spellPrimaryModTypeIndex = index;
        this.spellPackageCache = null;
        this.worldPosCallable.consume((world, blockPos) -> {
            this.slotChangedCraftingGrid(world);
        });
    }
    
    protected ISpellMod getSpellSecondaryModComponent() {
        // Construct a new spell mod from the saved type index and populate it with any cached properties
        ISpellMod retVal = SpellFactory.getModFromType(SpellManager.getModTypes(this.player).get(this.getSpellSecondaryModTypeIndex()));
        if (retVal != null) {
            for (Map.Entry<String, Integer> entry : this.spellPropertyCache.get(SpellComponent.SECONDARY_MOD).entrySet()) {
                if (retVal.getProperty(entry.getKey()) != null) {
                    retVal.getProperty(entry.getKey()).setValue(entry.getValue().intValue());
                }
            }
        }
        return retVal;
    }
    
    public int getSpellSecondaryModTypeIndex() {
        return this.spellSecondaryModTypeIndex;
    }
    
    public void setSpellSecondaryModTypeIndex(int index) {
        // Clear the spell package cache and trigger a regeneration of the output item on change
        index = MathHelper.clamp(index, 0, SpellManager.getModTypes(this.player).size() - 1);
        this.spellSecondaryModTypeIndex = index;
        this.spellPackageCache = null;
        this.worldPosCallable.consume((world, blockPos) -> {
            this.slotChangedCraftingGrid(world);
        });
    }
    
    public void setSpellPropertyValue(SpellComponent component, String name, int value) {
        SpellPackage spell = this.getSpellPackage();
        SpellProperty property = null;
        
        // Determine which property is to be changed
        if (component == SpellComponent.VEHICLE && spell.getVehicle() != null) {
            property = spell.getVehicle().getProperty(name);
        } else if (component == SpellComponent.PAYLOAD && spell.getPayload() != null) {
            property = spell.getPayload().getProperty(name);
        } else if (component == SpellComponent.PRIMARY_MOD && spell.getPrimaryMod() != null) {
            property = spell.getPrimaryMod().getProperty(name);
        } else if (component == SpellComponent.SECONDARY_MOD && spell.getSecondaryMod() != null) {
            property = spell.getSecondaryMod().getProperty(name);
        }
        
        // Set and cache the changed value, then trigger a regeneration of the output item
        if (property != null) {
            property.setValue(value);
            this.spellPropertyCache.get(component).put(name, value);
            this.worldPosCallable.consume((world, blockPos) -> {
                this.slotChangedCraftingGrid(world);
            });
        }
    }
    
    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        // Return input scroll and wand to the player's inventory when the GUI is closed
        super.onContainerClosed(playerIn);
        this.worldPosCallable.consume((world, blockPos) -> {
            this.clearContainer(playerIn, world, this.wandInv);
            this.clearContainer(playerIn, world, this.scrollInv);
        });
    }
    
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if (index == 0) {
                // If transferring the output item, move it into the player's backpack or hotbar
                if (!this.mergeItemStack(slotStack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(slotStack, stack);
            } else if (index >= 3 && index < 30) {
                // If transferring from the backpack, move wands or blank scrolls to the appropriate slot, and anything else to the hotbar
                if (this.wandSlot.isItemValid(slotStack)) {
                    if (!this.mergeItemStack(slotStack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.scrollSlot.isItemValid(slotStack)) {
                    if (!this.mergeItemStack(slotStack, 2, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.mergeItemStack(slotStack, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (index >= 30 && index < 39) {
                // If transferring from the hotbar, move wands or blank scrolls to the appropriate slot, and anything else to the backpack
                if (this.wandSlot.isItemValid(slotStack)) {
                    if (!this.mergeItemStack(slotStack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.scrollSlot.isItemValid(slotStack)) {
                    if (!this.mergeItemStack(slotStack, 2, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.mergeItemStack(slotStack, 3, 30, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.mergeItemStack(slotStack, 3, 39, false)) {
                // Move all other transfers to the backpack or hotbar
                return ItemStack.EMPTY;
            }
            
            if (slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
            
            if (slotStack.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }
            
            ItemStack taken = slot.onTake(playerIn, slotStack);
            if (index == 0) {
                playerIn.dropItem(taken, false);
            }
        }
        return stack;
    }
    
    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return slotIn.inventory != this.resultInv && super.canMergeSlot(stack, slotIn);
    }
    
    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        super.onCraftMatrixChanged(inventoryIn);
        this.worldPosCallable.consume((world, blockPos) -> {
            this.slotChangedCraftingGrid(world);
        });
    }

    protected void slotChangedCraftingGrid(World world) {
        if (!world.isRemote && this.player instanceof ServerPlayerEntity) {
            ServerPlayerEntity spe = (ServerPlayerEntity)this.player;
            ItemStack stack = ItemStack.EMPTY;
            Optional<? extends IRecipe<?>> opt = world.getServer().getRecipeManager().getRecipe(RECIPE_LOC);
            if (opt.isPresent() && opt.get() instanceof SpellcraftingRecipe) {
                // If the ingredients are present, enough mana is had, and the spell is valid, show the filled scroll in the output
                SpellcraftingRecipe recipe = (SpellcraftingRecipe)opt.get();
                if (recipe.matches(this.scrollInv, world) && this.wandContainsEnoughMana(spe) && this.getSpellPackage().isValid()) {
                    stack = recipe.getCraftingResult(this.scrollInv);
                    if (stack != null && stack.getItem() instanceof SpellScrollItem) {
                        ((SpellScrollItem)stack.getItem()).setSpell(stack, this.getSpellPackage());
                    }
                }
            }

            // Send a packet to the client to update its GUI with the shown output
            this.resultInv.setInventorySlotContents(0, stack);
            spe.connection.sendPacket(new SSetSlotPacket(this.windowId, 0, stack));
        }
    }
    
    protected boolean wandContainsEnoughMana(PlayerEntity player) {
        ItemStack stack = this.wandInv.getStackInSlot(0);
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof IWand)) {
            return false;
        }
        IWand wand = (IWand)stack.getItem();
        return wand.containsRealMana(stack, player, this.getManaCosts());
    }
}
