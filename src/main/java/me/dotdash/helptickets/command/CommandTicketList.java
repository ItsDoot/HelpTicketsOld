package me.dotdash.helptickets.command;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import me.dotdash.helptickets.HelpTickets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CommandTicketList implements CommandExecutor {

    private final HelpTickets tickets;

    public CommandTicketList(HelpTickets tickets) {
        this.tickets = tickets;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        List<Text> texts = new ArrayList<>();
        for(Map.Entry<Object, ? extends CommentedConfigurationNode> e : tickets.getTickets().get().getChildrenMap().entrySet()) {
            texts.add(Text.of(TextColors.GOLD, e.getKey().toString(), TextColors.GRAY, " - ", TextColors.GOLD,
                    tickets.getUserStorage().get(UUID.fromString(e.getValue().getNode("player").getString())).get().getName(),
                    TextColors.GRAY, " - ", TextColors.GOLD, e.getValue().getNode("message").getString()));
        }

        Sponge.getGame().getServiceManager().provideUnchecked(PaginationService.class).builder()
                .contents(!texts.isEmpty() ? texts
                        : Arrays.asList(Text.of(TextColors.GRAY, "There are no pending tickets at this time.")))
                .title(Text.of(TextColors.GRAY, "Tickets"))
                .paddingString("=")
                .sendTo(src);
        return CommandResult.success();
    }
}