package com.verdantartifice.primalmagick.common.events;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.common.enchantments.EnchantmentsPM;
import com.verdantartifice.primalmagick.common.misc.BlockBreaker;
import com.verdantartifice.primalmagick.common.misc.InteractionRecord;
import com.verdantartifice.primalmagick.common.stats.StatsManager;
import com.verdantartifice.primalmagick.common.stats.StatsPM;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handlers for block related events.
 * 
 * @author Daedalus4096
 */
@Mod.EventBusSubscriber(modid= PrimalMagick.MODID)
public class BlockEvents {
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        PlayerEntity player = event.getPlayer();
        World world = (event.getWorld() instanceof World) ? (World)event.getWorld() : null;
        if (!event.isCanceled() && world != null && !world.isRemote && !player.isSecondaryUseActive() && !BlockBreaker.hasBreakerQueued(world, event.getPos())) {
            triggerReverberation(world, event.getPos(), event.getState(), player, player.getHeldItemMainhand());
            triggerDisintegration(world, event.getPos(), event.getState(), player, player.getHeldItemMainhand());
        }
    }
    
    private static void triggerReverberation(World world, BlockPos pos, BlockState state, PlayerEntity player, ItemStack tool) {
        // Trigger block breakers if the player has a Reverberation tool in the main hand
        int level = EnchantmentHelper.getEnchantmentLevel(EnchantmentsPM.REVERBERATION.get(), tool);
        if (level <= 0) {
            return;
        }
        
        // Calculate the direction from which the block was broken
        InteractionRecord interact = PlayerEvents.LAST_BLOCK_LEFT_CLICK.get(player.getUniqueID());
        Direction dir;
        if (interact == null) {
            Vector3d startPos = player.getEyePosition(1.0F);
            Vector3d endPos = startPos.add(player.getLook(1.0F).scale(player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue()));
            BlockRayTraceResult rayTraceResult = world.rayTraceBlocks(new RayTraceContext(startPos, endPos, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player));
            if (rayTraceResult.getType() == RayTraceResult.Type.MISS) {
                return;
            }
            dir = rayTraceResult.getFace();
        } else {
            dir = interact.getFace();
        }
        
        // Iterate through the affected blocks
        float durability = (float)Math.sqrt(100.0F * state.getBlockHardness(world, pos));
        int xLimit = level * (dir.getXOffset() == 0 ? 1 : 0);
        int yLimit = level * (dir.getYOffset() == 0 ? 1 : 0);
        int zLimit = level * (dir.getZOffset() == 0 ? 1 : 0);
        for (int dx = -xLimit; dx <= xLimit; dx++) {
            for (int dy = -yLimit; dy <= yLimit; dy++) {
                for (int dz = -zLimit; dz <= zLimit; dz++) {
                    // If this isn't the center block, schedule a block breaker for it
                    BlockPos targetPos = new BlockPos(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    if (!targetPos.equals(pos)) {
                        BlockState targetState = world.getBlockState(targetPos);
                        float targetDurability = (float)Math.sqrt(100.0F * targetState.getBlockHardness(world, pos));
                        float newDurability = Math.max(0.0F, targetDurability - durability);
                        BlockBreaker breaker = new BlockBreaker.Builder().target(targetPos, targetState).durability(newDurability, targetDurability).player(player).tool(tool).oneShot().skipEvent().build();
                        BlockBreaker.schedule(world, targetPos.manhattanDistance(pos), breaker);
                    }
                }
            }
        }
    }
    
    private static void triggerDisintegration(World world, BlockPos pos, BlockState state, PlayerEntity player, ItemStack tool) {
        // Trigger block breakers if the player has a Reverberation tool in the main hand
        int level = EnchantmentHelper.getEnchantmentLevel(EnchantmentsPM.DISINTEGRATION.get(), tool);
        if (level <= 0) {
            return;
        }
        
        float durability = (float)Math.sqrt(100.0F * state.getBlockHardness(world, pos));
        int breakerCount = (10 * level) - 1;
        Set<BlockPos> examinedPositions = new HashSet<>();
        Queue<BlockPos> processingQueue = new LinkedList<>();
        
        // Set up initial conditions
        examinedPositions.add(pos);
        for (Direction dir : Direction.values()) {
            BlockPos setupPos = pos.offset(dir);
            examinedPositions.add(setupPos);
            processingQueue.offer(setupPos);
        }

        // Iterate through the affected blocks
        while (!processingQueue.isEmpty() && breakerCount > 0) {
            BlockPos curPos = processingQueue.poll();
            BlockState curState = world.getBlockState(curPos);
            if (curState.getBlock().equals(state.getBlock())) {
                // If the currently examined block is of the same type as the original block, schedule a breaker and enqueue its neighbors for examination
                breakerCount--;
                BlockBreaker breaker = new BlockBreaker.Builder().target(curPos, curState).durability(0.0F, durability).player(player).tool(tool).oneShot().skipEvent().build();
                BlockBreaker.schedule(world, curPos.manhattanDistance(pos), breaker);
                for (Direction dir : Direction.values()) {
                    BlockPos nextPos = curPos.offset(dir);
                    if (!examinedPositions.contains(nextPos)) {
                        examinedPositions.add(nextPos);
                        processingQueue.offer(nextPos);
                    }
                }
            }
        }
    }
    
    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void onBlockBreakLowest(BlockEvent.BreakEvent event) {
        // Record the block break statistic
        if (!event.isCanceled() && event.getState().getBlockHardness(event.getWorld(), event.getPos()) >= 2.0F && event.getPlayer().getHeldItemMainhand().isEmpty() && 
                event.getPlayer().getHeldItemOffhand().isEmpty()) {
            StatsManager.incrementValue(event.getPlayer(), StatsPM.BLOCKS_BROKEN_BAREHANDED);
        }
    }
}
