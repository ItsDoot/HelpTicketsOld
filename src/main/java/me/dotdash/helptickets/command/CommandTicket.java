package me.dotdash.helptickets.command;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class CommandTicket implements CommandExecutor {

    private static Function<CommandSource, List<Text>> COMMAND_LIST = src -> {
        List<Text> texts = new ArrayList<>();
        if(src.hasPermission("helptickets.cmd.create")) texts.add(Text.of(TextColors.GREEN, "- /ticket create <message>"));
        if(src.hasPermission("helptickets.cmd.list")) texts.add(Text.of(TextColors.GREEN, "- /ticket list"));
        if(src.hasPermission("helptickets.cmd.tp")) texts.add(Text.of(TextColors.GREEN, "- /ticket tp <id>"));
        if(src.hasPermission("helptickets.cmd.complete")) texts.add(Text.of(TextColors.GREEN, "- /ticket complete <id>"));
        return texts;
    };

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Sponge.getGame().getServiceManager().provideUnchecked(PaginationService.class).builder()
                .contents(!COMMAND_LIST.apply(src).isEmpty() ? COMMAND_LIST.apply(src)
                        : Arrays.asList(Text.of(TextColors.RED, "You do not have permission to use that command.")))
                .title(Text.of(TextColors.GRAY, "HelpTickets Commands"))
                .paddingString("=")
                .sendTo(src);
        return CommandResult.success();
    }
}