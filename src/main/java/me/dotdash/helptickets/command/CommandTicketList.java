package me.dotdash.helptickets.command;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import static org.spongepowered.api.text.format.TextColors.GOLD;
import static org.spongepowered.api.text.format.TextColors.GRAY;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;

import me.dotdash.helptickets.HelpTickets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class CommandTicketList implements CommandExecutor {

    private final HelpTickets tickets;

    public CommandTicketList(HelpTickets tickets) {
        this.tickets = tickets;
    }

    public Function<Map.Entry<Object, ? extends CommentedConfigurationNode>, Text> TICKET_LISTER = ticket -> {
        if(ticket.getValue().getNode("completed").getBoolean()) {
            return Text.of(GOLD, ticket.getKey().toString(), GRAY,
                    " - ", GOLD, uuidToName(UUID.fromString(ticket.getValue().getNode("player").getString())), GRAY, " - ", GOLD,
                    ticket.getValue().getNode("message").getString(), GRAY, " - ", GOLD, "COMPLETED");
        } else {
            return Text.of(GOLD, ticket.getKey().toString(), GRAY,
                    " - ", GOLD, uuidToName(UUID.fromString(ticket.getValue().getNode("player").getString())), GRAY, " - ", GOLD,
                    ticket.getValue().getNode("message").getString());
        }
    };

    public String uuidToName(UUID uuid) {
        return tickets.getUserStorage().get(uuid).get().getName();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        List<Text> texts = new ArrayList<>();
        texts.add(Text.of(GOLD, "Id", GRAY, " - ", GOLD, "Creator", GRAY,
                " - ", GOLD, "Message"));
        for(Map.Entry<Object, ? extends CommentedConfigurationNode> ticket
                : tickets.getTickets().get().getChildrenMap().entrySet()) {
            texts.add(TICKET_LISTER.apply(ticket));
        }

        Sponge.getGame().getServiceManager().provideUnchecked(PaginationService.class).builder()
                .contents(texts.size() > 1 ? texts
                        : Arrays.asList(Text.of(GRAY, "There are no pending tickets at this time.")))
                .title(Text.of(GRAY, "Tickets"))
                .paddingString("=")
                .sendTo(src);
        return CommandResult.success();
    }
}