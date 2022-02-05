package com.verdantartifice.primalmagick.common.tiles.mana;

import java.util.List;

import com.verdantartifice.primalmagick.common.blocks.mana.AbstractManaFontBlock;
import com.verdantartifice.primalmagick.common.blocks.mana.AncientManaFontBlock;
import com.verdantartifice.primalmagick.common.network.PacketHandler;
import com.verdantartifice.primalmagick.common.network.packets.fx.ManaSparklePacket;
import com.verdantartifice.primalmagick.common.research.ResearchManager;
import com.verdantartifice.primalmagick.common.research.SimpleResearchKey;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.stats.StatsManager;
import com.verdantartifice.primalmagick.common.tiles.TileEntityTypesPM;
import com.verdantartifice.primalmagick.common.tiles.base.TilePM;
import com.verdantartifice.primalmagick.common.util.EntityUtils;
import com.verdantartifice.primalmagick.common.wands.IInteractWithWand;
import com.verdantartifice.primalmagick.common.wands.IWand;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

/**
 * Definition of an ancient mana font tile entity.  Provides the recharge and wand interaction
 * functionality for the corresponding block.
 *
 * @author Daedalus4096
 * @see {@link com.verdantartifice.primalmagick.common.blocks.mana.AncientManaFontBlock}
 */
public class AncientManaFontTileEntity extends AbstractManaFontTileEntity {
    public AncientManaFontTileEntity() {
        super(TileEntityTypesPM.ANCIENT_MANA_FONT.get());
    }

        @Override
        public void tick() {
            this.ticksExisted++;
            if (!this.world.isRemote && this.ticksExisted % 10 == 0) {
                // Have players in range discover this font's shrine
                SimpleResearchKey research = SimpleResearchKey.parse("m_found_shrine");
                List<PlayerEntity> players = EntityUtils.getEntitiesInRange(this.world, this.pos, null, PlayerEntity.class, 5.0D);
                for (PlayerEntity player : players) {
                    if (!ResearchManager.isResearchComplete(player, research) && !ResearchManager.isResearchComplete(player, SimpleResearchKey.FIRST_STEPS)) {
                        ResearchManager.completeResearch(player, research);
                        player.sendMessage(new TranslationTextComponent("event.primalmagick.found_shrine").mergeStyle(TextFormatting.GREEN), Util.DUMMY_UUID);
                    }
                    if (this.getBlockState().getBlock() instanceof AbstractManaFontBlock) {
                        StatsManager.discoverShrine(player, ((AbstractManaFontBlock) this.getBlockState().getBlock()).getSource(), this.pos);
                    }
                }
            }
            if (!this.world.isRemote && this.ticksExisted % RECHARGE_TICKS == 0) {
                // Recharge the font over time
                this.mana++;
                if (this.mana > MANA_CAPACITY) {
                    this.mana = MANA_CAPACITY;
                } else {
                    // Sync the tile if its mana total changed
                    this.markDirty();
                    this.syncTile(true);
                }
            }
     }
}