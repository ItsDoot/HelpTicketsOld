package me.dotdash.helptickets.command;

import static org.spongepowered.api.text.format.TextColors.GRAY;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandPermissionException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import me.dotdash.helptickets.HelpTickets;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CommandTicket implements CommandExecutor {

    private final HelpTickets plugin;

    public CommandTicket(HelpTickets plugin) {
        this.plugin = plugin;
    }

    private static Function<CommandSource, List<Text>> COMMAND_LIST = src -> {
        List<Text> texts = new ArrayList<>();
        if(src.hasPermission("helptickets.cmd.create")) texts.add(Text.of(TextColors.GREEN, "- /ticket create <message>"));
        if(src.hasPermission("helptickets.cmd.list")) texts.add(Text.of(TextColors.GREEN, "- /ticket list [player]"));
        if(src.hasPermission("helptickets.cmd.tp")) texts.add(Text.of(TextColors.GREEN, "- /ticket tp <id>"));
        if(src.hasPermission("helptickets.cmd.complete")) texts.add(Text.of(TextColors.GREEN, "- /ticket complete <id>"));
        if(src.hasPermission("helptickets.cmd.delete")) texts.add(Text.of(TextColors.GREEN, "- /ticket delete <id>"));
        if(src.hasPermission("helptickets.cmd.info")) texts.add(Text.of(TextColors.GREEN, "- /ticket info <id>"));
        return texts;
    };

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        List<Text> cmdList = COMMAND_LIST.apply(src);

        if(cmdList.isEmpty())
            throw new CommandPermissionException();

        plugin.getPagination().builder()
                .contents(cmdList)
                .title(Text.of(GRAY, "HelpTickets Commands"))
                .paddingString("=")
                .sendTo(src);
        return CommandResult.success();
    }
}