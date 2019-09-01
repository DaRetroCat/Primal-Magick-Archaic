package com.verdantartifice.primalmagic.common.commands;

import java.util.Set;
import java.util.stream.Collectors;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.verdantartifice.primalmagic.common.capabilities.IPlayerKnowledge;
import com.verdantartifice.primalmagic.common.capabilities.PrimalMagicCapabilities;
import com.verdantartifice.primalmagic.common.commands.arguments.DisciplineArgument;
import com.verdantartifice.primalmagic.common.commands.arguments.DisciplineInput;
import com.verdantartifice.primalmagic.common.commands.arguments.KnowledgeTypeArgument;
import com.verdantartifice.primalmagic.common.commands.arguments.KnowledgeTypeInput;
import com.verdantartifice.primalmagic.common.commands.arguments.ResearchArgument;
import com.verdantartifice.primalmagic.common.commands.arguments.ResearchInput;
import com.verdantartifice.primalmagic.common.research.ResearchDiscipline;
import com.verdantartifice.primalmagic.common.research.ResearchDisciplines;
import com.verdantartifice.primalmagic.common.research.ResearchEntries;
import com.verdantartifice.primalmagic.common.research.ResearchManager;
import com.verdantartifice.primalmagic.common.research.SimpleResearchKey;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class PrimalMagicCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> node = dispatcher.register(Commands.literal("primalmagic")
            .requires((source) -> { return source.hasPermissionLevel(2); })
            .then(Commands.literal("help").executes((context) -> { return showHelp(context.getSource()); }))
            .then(Commands.literal("research")
                .then(Commands.argument("target", EntityArgument.player())
                    .then(Commands.literal("list").executes((context) -> { return listResearch(context.getSource(), EntityArgument.getPlayer(context, "target")); }))
                    .then(Commands.literal("reset").executes((context) -> { return resetResearch(context.getSource(), EntityArgument.getPlayer(context, "target")); }))
                    .then(Commands.literal("grant")
                        .then(Commands.argument("research", ResearchArgument.research()).executes((context) -> { return grantResearch(context.getSource(), EntityArgument.getPlayer(context, "target"), ResearchArgument.getResearch(context, "research")); }))
                    )
                    .then(Commands.literal("details")
                        .then(Commands.argument("research", ResearchArgument.research()).executes((context) -> { return detailResearch(context.getSource(), EntityArgument.getPlayer(context, "target"), ResearchArgument.getResearch(context, "research")); }))
                    )
                    .then(Commands.literal("progress")
                        .then(Commands.argument("research", ResearchArgument.research()).executes((context) -> { return progressResearch(context.getSource(), EntityArgument.getPlayer(context, "target"), ResearchArgument.getResearch(context, "research")); }))
                    )
                )
            )
            .then(Commands.literal("knowledge")
                .then(Commands.argument("target", EntityArgument.player())
                    .then(Commands.literal("get")
                        .then(Commands.argument("knowledge_type", KnowledgeTypeArgument.knowledgeType())
                            .then(Commands.argument("discipline", DisciplineArgument.discipline()).executes((context) -> { return getKnowledge(context.getSource(), EntityArgument.getPlayer(context, "target"), KnowledgeTypeArgument.getKnowledgeType(context, "knowledge_type"), DisciplineArgument.getDiscipline(context, "discipline")); }))
                        )
                    )
                )
            )
        );
        dispatcher.register(Commands.literal("pm").requires((source) -> {
            return source.hasPermissionLevel(2);
        }).redirect(node));
    }
    
    private static int showHelp(CommandSource source) {
        source.sendFeedback(new TranslationTextComponent("commands.primalmagic.help.1").applyTextStyle(TextFormatting.DARK_GREEN), true);
        source.sendFeedback(new TranslationTextComponent("commands.primalmagic.help.2").applyTextStyle(TextFormatting.DARK_GREEN), true);
        source.sendFeedback(new TranslationTextComponent("commands.primalmagic.help.3"), true);
        source.sendFeedback(new TranslationTextComponent("commands.primalmagic.help.4").applyTextStyle(TextFormatting.DARK_GREEN), true);
        source.sendFeedback(new TranslationTextComponent("commands.primalmagic.help.5"), true);
        source.sendFeedback(new TranslationTextComponent("commands.primalmagic.help.6").applyTextStyle(TextFormatting.DARK_GREEN), true);
        source.sendFeedback(new TranslationTextComponent("commands.primalmagic.help.7"), true);
        return 0;
    }
    
    private static int listResearch(CommandSource source, ServerPlayerEntity target) {
        IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(target);
        if (knowledge == null) {
            source.sendFeedback(new TranslationTextComponent("commands.primalmagic.error").applyTextStyle(TextFormatting.RED), true);
        } else {
            Set<SimpleResearchKey> researchSet = knowledge.getResearchSet();
            String[] researchList = researchSet.stream()
                                        .map(k -> k.getRootKey())
                                        .collect(Collectors.toSet())
                                        .toArray(new String[researchSet.size()]);
            String output = String.join(", ", researchList);
            source.sendFeedback(new TranslationTextComponent("commands.primalmagic.research.list", target.getName(), output), true);
        }
        return 0;
    }
    
    private static int resetResearch(CommandSource source, ServerPlayerEntity target) {
        IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(target);
        if (knowledge == null) {
            source.sendFeedback(new TranslationTextComponent("commands.primalmagic.error").applyTextStyle(TextFormatting.RED), true);
        } else {
            knowledge.clear();
            knowledge.sync(target);
            source.sendFeedback(new TranslationTextComponent("commands.primalmagic.research.reset", target.getName()), true);
        }
        return 0;
    }
    
    private static int grantResearch(CommandSource source, ServerPlayerEntity target, ResearchInput input) {
        IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(target);
        SimpleResearchKey key = input.getKey();
        if (knowledge == null) {
            source.sendFeedback(new TranslationTextComponent("commands.primalmagic.error").applyTextStyle(TextFormatting.RED), true);
        } else if (ResearchEntries.getEntry(key) == null) {
            source.sendFeedback(new TranslationTextComponent("commands.primalmagic.research.noexist", key.toString()).applyTextStyle(TextFormatting.RED), true);
        } else {
            ResearchManager.forceGrantWithAllParents(target, key);
            knowledge.sync(target);
            source.sendFeedback(new TranslationTextComponent("commands.primalmagic.research.grant", target.getName(), key.toString()), true);
        }
        return 0;
    }
    
    private static int detailResearch(CommandSource source, ServerPlayerEntity target, ResearchInput input) {
        IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(target);
        SimpleResearchKey key = input.getKey();
        if (knowledge == null) {
            source.sendFeedback(new TranslationTextComponent("commands.primalmagic.error").applyTextStyle(TextFormatting.RED), true);
        } else if (ResearchEntries.getEntry(key) == null) {
            source.sendFeedback(new TranslationTextComponent("commands.primalmagic.research.noexist", key.toString()).applyTextStyle(TextFormatting.RED), true);
        } else {
            IPlayerKnowledge.ResearchStatus status = knowledge.getResearchStatus(key);
            int stage = knowledge.getResearchStage(key);
            Set<IPlayerKnowledge.ResearchFlag> flagSet = knowledge.getResearchFlags(key);
            String[] flagStrs = flagSet.stream()
                                    .map(f -> f.name())
                                    .collect(Collectors.toSet())
                                    .toArray(new String[flagSet.size()]);
            String flagOutput = (flagStrs.length == 0) ? "none" : String.join(", ", flagStrs);
            source.sendFeedback(new TranslationTextComponent("commands.primalmagic.research.details.1", key.toString(), target.getName()), true);
            source.sendFeedback(new TranslationTextComponent("commands.primalmagic.research.details.2", status.name()), true);
            source.sendFeedback(new TranslationTextComponent("commands.primalmagic.research.details.3", stage), true);
            source.sendFeedback(new TranslationTextComponent("commands.primalmagic.research.details.4", flagOutput), true);
        }
        return 0;
    }
    
    private static int progressResearch(CommandSource source, ServerPlayerEntity target, ResearchInput input) {
        IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(target);
        SimpleResearchKey key = input.getKey();
        if (knowledge == null) {
            source.sendFeedback(new TranslationTextComponent("commands.primalmagic.error").applyTextStyle(TextFormatting.RED), true);
        } else if (ResearchEntries.getEntry(key) == null) {
            source.sendFeedback(new TranslationTextComponent("commands.primalmagic.research.noexist", key.toString()).applyTextStyle(TextFormatting.RED), true);
        } else {
            int oldStage = knowledge.getResearchStage(key);
            if (ResearchManager.progressResearch(target, key)) {
                int newStage = knowledge.getResearchStage(key);
                source.sendFeedback(new TranslationTextComponent("commands.primalmagic.research.progress.success", key.toString(), oldStage, newStage), true);
            } else {
                source.sendFeedback(new TranslationTextComponent("commands.primalmagic.research.progress.failure", key.toString(), oldStage), true);
            }
        }
        return 0;
    }
    
    private static int getKnowledge(CommandSource source, ServerPlayerEntity target, KnowledgeTypeInput knowledgeTypeInput, DisciplineInput disciplineInput) {
        IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(target);
        IPlayerKnowledge.KnowledgeType type = knowledgeTypeInput.getType();
        String disciplineKey = disciplineInput.getDisciplineKey();
        if (knowledge == null) {
            source.sendFeedback(new TranslationTextComponent("commands.primalmagic.error").applyTextStyle(TextFormatting.RED), true);
        } else if (type == null) {
            source.sendFeedback(new TranslationTextComponent("commands.primalmagic.knowledge_type.noexist").applyTextStyle(TextFormatting.RED), true);
        } else {
            ResearchDiscipline discipline = ResearchDisciplines.getDiscipline(disciplineKey);
            if (discipline == null) {
                source.sendFeedback(new TranslationTextComponent("commands.primalmagic.discipline.noexist", disciplineKey).applyTextStyle(TextFormatting.RED), true);
            } else {
                int levels = knowledge.getKnowledge(type, discipline);
                source.sendFeedback(new TranslationTextComponent("commands.primalmagic.knowledge.get", levels, discipline.getKey(), type.name()), true);
            }
        }
        return 0;
    }
}