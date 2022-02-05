package com.verdantartifice.primalmagick.common.commands;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.verdantartifice.primalmagick.common.attunements.AttunementManager;
import com.verdantartifice.primalmagick.common.attunements.AttunementType;
import com.verdantartifice.primalmagick.common.capabilities.IPlayerAttunements;
import com.verdantartifice.primalmagick.common.capabilities.IPlayerKnowledge;
import com.verdantartifice.primalmagick.common.capabilities.IPlayerStats;
import com.verdantartifice.primalmagick.common.capabilities.PrimalMagicCapabilities;
import com.verdantartifice.primalmagick.common.commands.arguments.AttunementTypeArgument;
import com.verdantartifice.primalmagick.common.commands.arguments.AttunementTypeInput;
import com.verdantartifice.primalmagick.common.commands.arguments.AttunementValueArgument;
import com.verdantartifice.primalmagick.common.commands.arguments.KnowledgeAmountArgument;
import com.verdantartifice.primalmagick.common.commands.arguments.KnowledgeTypeArgument;
import com.verdantartifice.primalmagick.common.commands.arguments.KnowledgeTypeInput;
import com.verdantartifice.primalmagick.common.commands.arguments.ResearchArgument;
import com.verdantartifice.primalmagick.common.commands.arguments.ResearchInput;
import com.verdantartifice.primalmagick.common.commands.arguments.SourceArgument;
import com.verdantartifice.primalmagick.common.commands.arguments.SourceInput;
import com.verdantartifice.primalmagick.common.commands.arguments.StatValueArgument;
import com.verdantartifice.primalmagick.common.research.ResearchEntries;
import com.verdantartifice.primalmagick.common.research.ResearchManager;
import com.verdantartifice.primalmagick.common.research.SimpleResearchKey;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.stats.Stat;
import com.verdantartifice.primalmagick.common.stats.StatsManager;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.ItemInput;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Definition of the /primalmagick debug command and its /pm alias.
 * 
 * @author Daedalus4096
 */
public class PrimalMagicCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> node = dispatcher.register(Commands.literal("primalmagick")
            .requires((source) -> { return source.hasPermissionLevel(2); })
            .then(Commands.literal("reset")
                .then(Commands.argument("target", EntityArgument.player()).executes((context) -> { return resetAll(context.getSource(), EntityArgument.getPlayer(context, "target")); }))
            )
            .then(Commands.literal("research")
                .then(Commands.argument("target", EntityArgument.player())
                    // /pm research <target> list
                    .then(Commands.literal("list").executes((context) -> { return listResearch(context.getSource(), EntityArgument.getPlayer(context, "target")); }))
                    // /pm research <target> reset
                    .then(Commands.literal("reset").executes((context) -> { return resetResearch(context.getSource(), EntityArgument.getPlayer(context, "target")); }))
                    .then(Commands.literal("grant")
                        // /pm research <target> grant <research>
                        .then(Commands.argument("research", ResearchArgument.research()).executes((context) -> { return grantResearch(context.getSource(), EntityArgument.getPlayer(context, "target"), ResearchArgument.getResearch(context, "research")); }))
                    )
                    .then(Commands.literal("revoke")
                        // /pm research <target> revoke <research>
                        .then(Commands.argument("research", ResearchArgument.research()).executes((context) -> { return revokeResearch(context.getSource(), EntityArgument.getPlayer(context, "target"), ResearchArgument.getResearch(context, "research")); }))
                    )
                    .then(Commands.literal("details")
                        // /pm research <target> details <research>
                        .then(Commands.argument("research", ResearchArgument.research()).executes((context) -> { return detailResearch(context.getSource(), EntityArgument.getPlayer(context, "target"), ResearchArgument.getResearch(context, "research")); }))
                    )
                    .then(Commands.literal("progress")
                        // /pm research <target> progress <research>
                        .then(Commands.argument("research", ResearchArgument.research()).executes((context) -> { return progressResearch(context.getSource(), EntityArgument.getPlayer(context, "target"), ResearchArgument.getResearch(context, "research")); }))
                    )
                )
            )
            .then(Commands.literal("knowledge")
                .then(Commands.argument("target", EntityArgument.player())
                    // /pm knowledge <target> reset
                    .then(Commands.literal("reset").executes((context) -> { return resetKnowledge(context.getSource(), EntityArgument.getPlayer(context, "target")); }))
                    .then(Commands.literal("get")
                        // /pm knowledge <target> get <knowledge_type>
                        .then(Commands.argument("knowledge_type", KnowledgeTypeArgument.knowledgeType()).executes((context) -> { return getKnowledge(context.getSource(), EntityArgument.getPlayer(context, "target"), KnowledgeTypeArgument.getKnowledgeType(context, "knowledge_type")); }))
                    )
                    .then(Commands.literal("add")
                        .then(Commands.argument("knowledge_type", KnowledgeTypeArgument.knowledgeType())
                            // /pm knowledge <target> add <knowledge_type> <points>
                            .then(Commands.argument("points", KnowledgeAmountArgument.amount()).executes((context) -> { return addKnowledge(context.getSource(), EntityArgument.getPlayer(context, "target"), KnowledgeTypeArgument.getKnowledgeType(context, "knowledge_type"), IntegerArgumentType.getInteger(context, "points")); }))
                        )
                    )
                )
            )
            .then(Commands.literal("scans")
                .then(Commands.argument("target", EntityArgument.player())
                    .then(Commands.literal("grant")
                        // /pm scans <target> grant <item>
                        .then(Commands.argument("item", ItemArgument.item()).executes((context) -> { return grantScanResearch(context.getSource(), EntityArgument.getPlayer(context, "target"), ItemArgument.getItem(context, "item")); }))
                    )
                    // /pm scans <target> grant_all
                    .then(Commands.literal("grant_all").executes((context) -> { return grantAllScanResearch(context.getSource(), EntityArgument.getPlayer(context, "target")); }))
                )
            )
            .then(Commands.literal("sources")
                .then(Commands.argument("target", EntityArgument.player())
                    // /pm sources <target> list
                    .then(Commands.literal("list").executes((context) -> { return listUnlockedSources(context.getSource(), EntityArgument.getPlayer(context, "target")); }))
                    // /pm sources <target> unlock_all
                    .then(Commands.literal("unlock_all").executes((context) -> { return unlockAllSources(context.getSource(), EntityArgument.getPlayer(context, "target")); }))
                    .then(Commands.literal("unlock")
                        // /pm sources <target> unlock <source>
                        .then(Commands.argument("source", SourceArgument.source()).executes((context) -> { return unlockSource(context.getSource(), EntityArgument.getPlayer(context, "target"), SourceArgument.getSource(context, "source")); }))
                    )
                )
            )
            .then(Commands.literal("stats")
                .then(Commands.argument("target", EntityArgument.player())
                    // /pm stats <target> reset
                    .then(Commands.literal("reset").executes((context) -> { return resetStats(context.getSource(), EntityArgument.getPlayer(context, "target")); }))
                    .then(Commands.literal("get")
                        // /pm stats <target> get <stat>
                        .then(Commands.argument("stat", ResourceLocationArgument.resourceLocation()).suggests((ctx, sb) -> ISuggestionProvider.suggest(StatsManager.getStatLocations().stream().map(ResourceLocation::toString), sb)).executes((context) -> { return getStatValue(context.getSource(), EntityArgument.getPlayer(context, "target"), ResourceLocationArgument.getResourceLocation(context, "stat")); }))
                    )
                    .then(Commands.literal("set")
                        .then(Commands.argument("stat", ResourceLocationArgument.resourceLocation()).suggests((ctx, sb) -> ISuggestionProvider.suggest(StatsManager.getStatLocations().stream().map(ResourceLocation::toString), sb))
                            // /pm stats <target> set <stat> <value>
                            .then(Commands.argument("value", StatValueArgument.value()).executes((context) -> { return setStatValue(context.getSource(), EntityArgument.getPlayer(context, "target"), ResourceLocationArgument.getResourceLocation(context, "stat"), IntegerArgumentType.getInteger(context, "value")); }))
                        )
                    )
                )
            )
            .then(Commands.literal("attunements")
                .then(Commands.argument("target", EntityArgument.player())
                    // /pm attunements <target> reset
                    .then(Commands.literal("reset").executes((context) -> { return resetAttunements(context.getSource(), EntityArgument.getPlayer(context, "target")); }))
                    .then(Commands.literal("get")
                        // /pm attunements <target> get <source>
                        .then(Commands.argument("source", SourceArgument.source()).executes((context) -> { return getAttunements(context.getSource(), EntityArgument.getPlayer(context, "target"), SourceArgument.getSource(context, "source")); }))
                    )
                    .then(Commands.literal("set")
                        .then(Commands.argument("source", SourceArgument.source())
                            .then(Commands.argument("type", AttunementTypeArgument.attunementType())
                                // /pm attunements <target> set <source> <type> <value>
                                .then(Commands.argument("value", AttunementValueArgument.value()).executes((context) -> { return setAttunement(context.getSource(), EntityArgument.getPlayer(context, "target"), SourceArgument.getSource(context, "source"), AttunementTypeArgument.getAttunementType(context, "type"), IntegerArgumentType.getInteger(context, "value")); }))
                            )
                        )
                    )
                )
            )
        );
        dispatcher.register(Commands.literal("pm").requires((source) -> {
            return source.hasPermissionLevel(2);
        }).redirect(node));
    }

    private static int resetAll(CommandSource source, ServerPlayerEntity player) {
        resetResearch(source, player);
        resetKnowledge(source, player);
        resetAttunements(source, player);
        resetStats(source, player);
        return 0;
    }

    private static int listResearch(CommandSource source, ServerPlayerEntity target) {
        IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(target);
        if (knowledge == null) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.error"));
        } else {
            // List all unlocked research entries for the target player
            Set<SimpleResearchKey> researchSet = knowledge.getResearchSet();
            String[] researchList = researchSet.stream()
                                        .map(k -> k.getRootKey())
                                        .collect(Collectors.toSet())
                                        .toArray(new String[researchSet.size()]);
            String output = String.join(", ", researchList);
            source.sendFeedback(new TranslationTextComponent("commands.primalmagick.research.list", target.getName(), output), true);
        }
        return 0;
    }
    
    private static int resetResearch(CommandSource source, ServerPlayerEntity target) {
        IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(target);
        if (knowledge == null) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.error"));
        } else {
            // Remove all unlocked research entries from the target player
            knowledge.clearResearch();
            ResearchManager.scheduleSync(target);
            source.sendFeedback(new TranslationTextComponent("commands.primalmagick.research.reset", target.getName()), true);
            target.sendMessage(new TranslationTextComponent("commands.primalmagick.research.reset.target", source.getName()), Util.DUMMY_UUID);
        }
        return 0;
    }
    
    private static int grantResearch(CommandSource source, ServerPlayerEntity target, ResearchInput input) {
        IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(target);
        SimpleResearchKey key = input.getKey();
        if (knowledge == null) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.error"));
        } else if (ResearchEntries.getEntry(key) == null) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.research.noexist", key.toString()));
        } else {
            // Grant the specified research to the target player, along with all its parents
            ResearchManager.forceGrantWithAllParents(target, key);
            source.sendFeedback(new TranslationTextComponent("commands.primalmagick.research.grant", target.getName(), key.toString()), true);
            target.sendMessage(new TranslationTextComponent("commands.primalmagick.research.grant.target", source.getName(), key.toString()), Util.DUMMY_UUID);
        }
        return 0;
    }
    
    private static int revokeResearch(CommandSource source, ServerPlayerEntity target, ResearchInput input) {
        IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(target);
        SimpleResearchKey key = input.getKey();
        if (knowledge == null) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.error"));
        } else if (ResearchEntries.getEntry(key) == null) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.research.noexist", key.toString()));
        } else {
            // Revoke the specified research from the target player, along with all its children
            ResearchManager.forceRevokeWithAllChildren(target, key);
            source.sendFeedback(new TranslationTextComponent("commands.primalmagick.research.revoke", target.getName(), key.toString()), true);
            target.sendMessage(new TranslationTextComponent("commands.primalmagick.research.revoke.target", source.getName(), key.toString()), Util.DUMMY_UUID);
        }
        return 0;
    }
    
    private static int detailResearch(CommandSource source, ServerPlayerEntity target, ResearchInput input) {
        IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(target);
        SimpleResearchKey key = input.getKey();
        if (knowledge == null) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.error"));
        } else if (ResearchEntries.getEntry(key) == null) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.research.noexist", key.toString()));
        } else {
            // List the status, current stage, and attached flags of the given research for the target player
            IPlayerKnowledge.ResearchStatus status = knowledge.getResearchStatus(key);
            int stage = knowledge.getResearchStage(key);
            Set<IPlayerKnowledge.ResearchFlag> flagSet = knowledge.getResearchFlags(key);
            String[] flagStrs = flagSet.stream()
                                    .map(f -> f.name())
                                    .collect(Collectors.toSet())
                                    .toArray(new String[flagSet.size()]);
            String flagOutput = (flagStrs.length == 0) ? "none" : String.join(", ", flagStrs);
            source.sendFeedback(new TranslationTextComponent("commands.primalmagick.research.details.1", key.toString(), target.getName()), true);
            source.sendFeedback(new TranslationTextComponent("commands.primalmagick.research.details.2", status.name()), true);
            source.sendFeedback(new TranslationTextComponent("commands.primalmagick.research.details.3", stage), true);
            source.sendFeedback(new TranslationTextComponent("commands.primalmagick.research.details.4", flagOutput), true);
        }
        return 0;
    }
    
    private static int progressResearch(CommandSource source, ServerPlayerEntity target, ResearchInput input) {
        IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(target);
        SimpleResearchKey key = input.getKey();
        if (knowledge == null) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.error"));
        } else if (ResearchEntries.getEntry(key) == null) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.research.noexist", key.toString()));
        } else {
            // Advance the given research to its next stage for the target player
            int oldStage = knowledge.getResearchStage(key);
            if (ResearchManager.progressResearch(target, key)) {
                int newStage = knowledge.getResearchStage(key);
                source.sendFeedback(new TranslationTextComponent("commands.primalmagick.research.progress.success", key.toString(), target.getName(), oldStage, newStage), true);
                target.sendMessage(new TranslationTextComponent("commands.primalmagick.research.progress.target", key.toString(), source.getName(), oldStage, newStage), Util.DUMMY_UUID);
            } else {
                source.sendFeedback(new TranslationTextComponent("commands.primalmagick.research.progress.failure", key.toString(), oldStage), true);
            }
        }
        return 0;
    }
    
    private static int resetKnowledge(CommandSource source, ServerPlayerEntity target) {
        IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(target);
        if (knowledge == null) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.error"));
        } else {
            // Remove all observations and theories from the target player
            knowledge.clearKnowledge();
            source.sendFeedback(new TranslationTextComponent("commands.primalmagick.knowledge.reset", target.getName()), true);
            target.sendMessage(new TranslationTextComponent("commands.primalmagick.knowledge.reset.target", source.getName()), Util.DUMMY_UUID);
        }
        return 0;
    }
    
    private static int getKnowledge(CommandSource source, ServerPlayerEntity target, KnowledgeTypeInput knowledgeTypeInput) {
        IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(target);
        IPlayerKnowledge.KnowledgeType type = knowledgeTypeInput.getType();
        if (knowledge == null) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.error"));
        } else if (type == null) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.knowledge_type.noexist"));
        } else {
            // Get the current levels and points for the given knowledge type for the target player
            int levels = knowledge.getKnowledge(type);
            int points = knowledge.getKnowledgeRaw(type);
            source.sendFeedback(new TranslationTextComponent("commands.primalmagick.knowledge.get", target.getName(), levels, type.name(), points), true);
        }
        return 0;
    }
    
    private static int addKnowledge(CommandSource source, ServerPlayerEntity target, KnowledgeTypeInput knowledgeTypeInput, int points) {
        IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(target);
        IPlayerKnowledge.KnowledgeType type = knowledgeTypeInput.getType();
        if (knowledge == null) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.error"));
        } else if (type == null) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.knowledge_type.noexist"));
        } else {
            // Add the given number of points to the given knowledge type for the target player
            if (ResearchManager.addKnowledge(target, type, points)) {
                source.sendFeedback(new TranslationTextComponent("commands.primalmagick.knowledge.add.success", points, type.name(), target.getName()), true);
                target.sendMessage(new TranslationTextComponent("commands.primalmagick.knowledge.add.target", points, type.name(), source.getName()), Util.DUMMY_UUID);
            } else {
                source.sendFeedback(new TranslationTextComponent("commands.primalmagick.knowledge.add.failure", target.getName()), true);
            }
        }
        return 0;
    }
    
    private static int grantScanResearch(CommandSource source, ServerPlayerEntity target, ItemInput item) {
        ItemStack stack;
        try {
            stack = item.createStack(1, false);
        } catch (CommandSyntaxException e) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.scans.grant.failure", target.getName()));
            return 0;
        }
        // Scan the given item for the target player and grant them its research
        if (ResearchManager.setScanned(stack, target)) {
            source.sendFeedback(new TranslationTextComponent("commands.primalmagick.scans.grant.success", target.getName(), item.getItem().getRegistryName().toString()), true);
            target.sendMessage(new TranslationTextComponent("commands.primalmagick.scans.grant.target", source.getName(), item.getItem().getRegistryName().toString()), Util.DUMMY_UUID);
        } else {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.scans.grant.failure", target.getName()));            
        }
        return 0;
    }
    
    private static int grantAllScanResearch(CommandSource source, ServerPlayerEntity target) {
        // Scan all possible items for the target player and grant them their research
        int count = ResearchManager.setAllScanned(target);
        source.sendFeedback(new TranslationTextComponent("commands.primalmagick.scans.grant_all", count, target.getName()), true);
        target.sendMessage(new TranslationTextComponent("commands.primalmagick.scans.grant_all.target", count, source.getName()), Util.DUMMY_UUID);
        return 0;
    }

    private static int listUnlockedSources(CommandSource source, ServerPlayerEntity target) {
        // List the unlocked sources for the target player in prescribed order
        List<String> unlockedTags = Source.SORTED_SOURCES.stream()
                                        .filter((s) -> s.isDiscovered(target))
                                        .map((s) -> s.getTag().toUpperCase())
                                        .collect(Collectors.toList());
        String tagStr = String.join(", ", unlockedTags);
        source.sendFeedback(new TranslationTextComponent("commands.primalmagick.sources.list", target.getName(), tagStr), true);
        return 0;
    }

    private static int unlockAllSources(CommandSource source, ServerPlayerEntity target) {
        // Grant the target player the unlock research for all sources.  Can't do this with grantResearch because source unlocks aren't in the grimoire.
        int unlocked = 0;
        for (Source toUnlock : Source.SOURCES.values()) {
            if (!toUnlock.isDiscovered(target)) {
                if (ResearchManager.completeResearch(target, toUnlock.getDiscoverKey())) {
                    unlocked++;
                }
            }
        }
        source.sendFeedback(new TranslationTextComponent("commands.primalmagick.sources.unlock_all", target.getName(), unlocked), true);
        target.sendMessage(new TranslationTextComponent("commands.primalmagick.sources.unlock_all.target", source.getName(), unlocked), Util.DUMMY_UUID);
        return 0;
    }

    private static int unlockSource(CommandSource source, ServerPlayerEntity target, SourceInput input) {
        // Grant the target player the unlock research for the given source.  Can't do this with grantResearch because source unlocks aren't in the grimoire.
        String tag = input.getSourceTag();
        Source toUnlock = Source.getSource(tag.toLowerCase());
        if (toUnlock == null) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.source.noexist", tag));
        } else if (toUnlock.isDiscovered(target)) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.sources.unlock.already_unlocked", target.getName(), tag.toUpperCase()));
        } else if (ResearchManager.completeResearch(target, toUnlock.getDiscoverKey())) {
            source.sendFeedback(new TranslationTextComponent("commands.primalmagick.sources.unlock.success", target.getName(), tag.toUpperCase()), true);
            target.sendMessage(new TranslationTextComponent("commands.primalmagick.sources.unlock.target", source.getName(), tag.toUpperCase()), Util.DUMMY_UUID);
        } else {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.sources.unlock.failure", target.getName(), tag.toUpperCase()));
        }
        return 0;
    }

    private static int getStatValue(CommandSource source, ServerPlayerEntity target, ResourceLocation statLoc) {
        // Look up the requested stat value for the given player and display it
        Stat stat = StatsManager.getStat(statLoc);
        if (stat == null) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.stats.noexist", statLoc));
        } else {
            ITextComponent statName = new TranslationTextComponent(stat.getTranslationKey());
            ITextComponent statValue = StatsManager.getFormattedValue(target, stat);
            source.sendFeedback(new TranslationTextComponent("commands.primalmagick.stats.get", target.getName(), statName, statValue), true);
        }
        return 0;
    }

    private static int setStatValue(CommandSource source, ServerPlayerEntity target, ResourceLocation statLoc, int value) {
        // Set the given value for the given stat for the given player
        Stat stat = StatsManager.getStat(statLoc);
        if (stat == null) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.stats.noexist", statLoc));
        } else {
            StatsManager.setValue(target, stat, value);
            ITextComponent statName = new TranslationTextComponent(stat.getTranslationKey());
            ITextComponent statValue = StatsManager.getFormattedValue(target, stat);
            source.sendFeedback(new TranslationTextComponent("commands.primalmagick.stats.set", target.getName(), statName, statValue), true);
            target.sendMessage(new TranslationTextComponent("commands.primalmagick.stats.set.target", source.getName(), statName, statValue), Util.DUMMY_UUID);
        }
        return 0;
    }

    private static int resetStats(CommandSource source, ServerPlayerEntity target) {
        // Remove all accrued stats from the player
        IPlayerStats stats = PrimalMagicCapabilities.getStats(target);
        if (stats == null) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.error"));
        } else {
            stats.clear();
            StatsManager.scheduleSync(target);
            source.sendFeedback(new TranslationTextComponent("commands.primalmagick.stats.reset", target.getName()), true);
            target.sendMessage(new TranslationTextComponent("commands.primalmagick.stats.reset.target", source.getName()), Util.DUMMY_UUID);
        }
        return 0;
    }

    private static int resetAttunements(CommandSource source, ServerPlayerEntity target) {
        // Remove all accrued attunements from the player
        IPlayerAttunements attunements = PrimalMagicCapabilities.getAttunements(target);
        if (attunements == null) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.error"));
        } else {
            attunements.clear();
            AttunementManager.scheduleSync(target);
            source.sendFeedback(new TranslationTextComponent("commands.primalmagick.attunements.reset", target.getName()), true);
            target.sendMessage(new TranslationTextComponent("commands.primalmagick.attunements.reset.target", source.getName()), Util.DUMMY_UUID);
        }
        return 0;
    }

    private static int getAttunements(CommandSource source, ServerPlayerEntity target, SourceInput input) {
        // Get the partial and total attunements for the given source for the target player
        String tag = input.getSourceTag();
        Source toQuery = Source.getSource(tag.toLowerCase());
        if (toQuery == null) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.source.noexist", tag));
        } else {
            ITextComponent sourceText = new TranslationTextComponent(toQuery.getNameTranslationKey());
            source.sendFeedback(new TranslationTextComponent("commands.primalmagick.attunements.get.total", sourceText, source.getName(), AttunementManager.getTotalAttunement(target, toQuery)), true);
            for (AttunementType type : AttunementType.values()) {
                ITextComponent typeText = new TranslationTextComponent(type.getNameTranslationKey());
                source.sendFeedback(new TranslationTextComponent("commands.primalmagick.attunements.get.partial", typeText, AttunementManager.getAttunement(target, toQuery, type)), true);
            }
        }
        return 0;
    }

    private static int setAttunement(CommandSource source, ServerPlayerEntity target, SourceInput input, AttunementTypeInput attunementType, int value) {
        // Set the partial attunement for the target player
        String tag = input.getSourceTag();
        Source toSet = Source.getSource(tag.toLowerCase());
        AttunementType type = attunementType.getType();
        if (toSet == null) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.source.noexist", tag));
        } else if (type == null) {
            source.sendErrorMessage(new TranslationTextComponent("commands.primalmagick.attunement_type.noexist"));
        } else {
            AttunementManager.setAttunement(target, toSet, type, value);
            ITextComponent sourceText = new TranslationTextComponent(toSet.getNameTranslationKey());
            ITextComponent typeText = new TranslationTextComponent(type.getNameTranslationKey());
            if (type.isCapped() && value > type.getMaximum()) {
                source.sendFeedback(new TranslationTextComponent("commands.primalmagick.attunements.set.success.capped", target.getName(), typeText, sourceText, type.getMaximum(), value), true);
                target.sendMessage(new TranslationTextComponent("commands.primalmagick.attunements.set.target.capped", target.getName(), typeText, sourceText, type.getMaximum(), value), Util.DUMMY_UUID);
            } else {
                source.sendFeedback(new TranslationTextComponent("commands.primalmagick.attunements.set.success", target.getName(), typeText, sourceText, value), true);
                target.sendMessage(new TranslationTextComponent("commands.primalmagick.attunements.set.target", target.getName(), typeText, sourceText, value), Util.DUMMY_UUID);
            }
        }
        return 0;
    }
}
