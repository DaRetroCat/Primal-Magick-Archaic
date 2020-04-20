package com.verdantartifice.primalmagic.client.events;

import com.verdantartifice.primalmagic.PrimalMagic;
import com.verdantartifice.primalmagic.common.blocks.BlocksPM;
import com.verdantartifice.primalmagic.common.blocks.misc.StainedSkyglassBlock;
import com.verdantartifice.primalmagic.common.blocks.rituals.SaltTrailBlock;
import com.verdantartifice.primalmagic.common.items.ItemsPM;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Respond to client-only block/item color events.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid=PrimalMagic.MODID, value=Dist.CLIENT, bus=Mod.EventBusSubscriber.Bus.MOD)
public class BlockColorEvents {
    @SubscribeEvent
    public static void onBlockColorInit(ColorHandlerEvent.Block event) {
        event.getBlockColors().register((state, lightReader, pos, dummy) -> {
            return SaltTrailBlock.colorMultiplier(state.get(SaltTrailBlock.POWER));
        }, BlocksPM.SALT_TRAIL.get());
        
        event.getBlockColors().register((state, lightReader, pos, dummy) -> {
            return (state.getBlock() instanceof StainedSkyglassBlock) ? 
                    ((StainedSkyglassBlock)state.getBlock()).getColor().getColorValue() : 
                    DyeColor.WHITE.getColorValue();
        }, BlocksPM.STAINED_SKYGLASS_RED.get());
    }
    
    @SubscribeEvent
    public static void onItemColorInit(ColorHandlerEvent.Item event) {
        event.getItemColors().register((stack, dummy) -> {
            Item item = stack.getItem();
            if (item instanceof BlockItem) {
                Block block = ((BlockItem)item).getBlock();
                if (block instanceof StainedSkyglassBlock) {
                    return ((StainedSkyglassBlock)block).getColor().getColorValue();
                }
            }
            return DyeColor.WHITE.getColorValue();
        }, ItemsPM.STAINED_SKYGLASS_RED.get());
    }
}
