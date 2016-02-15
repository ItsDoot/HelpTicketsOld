package me.dotdash.helptickets.command;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import static org.spongepowered.api.text.format.TextColors.GOLD;
import static org.spongepowered.api.text.format.TextColors.GRAY;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import me.dotdash.helptickets.HelpTickets;
import me.dotdash.helptickets.util.MiscUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandTicketList implements CommandExecutor {

    private final HelpTickets plugin;

    public CommandTicketList(HelpTickets plugin) {
        this.plugin = plugin;
    }

    public Function<Map.Entry<Object, ? extends CommentedConfigurationNode>, Text> TICKET_LISTER = ticket -> {
        if(ticket.getValue().getNode("completed").getBoolean()) {
            return Text.of(GOLD, ticket.getKey().toString(), GRAY,
                    " - ", GOLD, MiscUtil.uuidToName(ticket.getValue().getNode("player").getString()), GRAY, " - ",
                    GOLD, ticket.getValue().getNode("message").getString(), GRAY, " - ", GOLD, "COMPLETED");
        } else {
            return Text.of(GOLD, ticket.getKey().toString(), GRAY,
                    " - ", GOLD, MiscUtil.uuidToName(ticket.getValue().getNode("player").getString()), GRAY, " - ",
                    GOLD, ticket.getValue().getNode("message").getString());
        }
    };

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<String> player = args.<String>getOne("player");

        List<Text> texts = new ArrayList<>();
        texts.add(Text.of(GOLD, "Id", GRAY, " - ", GOLD, "Creator", GRAY,
                " - ", GOLD, "Message"));

        if (player.isPresent()) {
            try {
                if(!plugin.getUserStorage().get(player.get()).isPresent()) {
                    src.sendMessage(Text.of(TextColors.RED, "That player doesn't exist."));
                    return CommandResult.success();
                }
            } catch (IllegalArgumentException e) {
                src.sendMessage(Text.of(TextColors.RED, "That player doesn't exist."));
                return CommandResult.success();
            }
            texts.addAll(plugin.getTickets().get().getChildrenMap().entrySet().stream()
                    .filter(ticket -> UUID.fromString(ticket.getValue().getNode("player").getString())
                            .equals(plugin.getUserStorage().get(player.get()).get().getUniqueId()))
                    .map(ticket -> TICKET_LISTER.apply(ticket))
                    .collect(Collectors.toList()));
        } else {
            texts.addAll(plugin.getTickets().get().getChildrenMap().entrySet().stream()
                    .map(ticket -> TICKET_LISTER.apply(ticket))
                    .collect(Collectors.toList()));
        }

        plugin.getPagination().builder()
                .contents(texts.size() > 1
                        ? texts
                        : !player.isPresent()
                        ? Arrays.asList(Text.of(GOLD, "There are no pending tickets at this time."))
                        : Arrays.asList(Text.of(GOLD, "That player has no pending tickets at this time.")))
                .title(Text.of(GRAY, "Tickets"))
                .paddingString("=")
                .sendTo(src);
        return CommandResult.success();
    }
}