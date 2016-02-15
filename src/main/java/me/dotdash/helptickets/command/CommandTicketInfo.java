package me.dotdash.helptickets.command;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import static org.spongepowered.api.text.format.TextColors.GRAY;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import me.dotdash.helptickets.HelpTickets;
import me.dotdash.helptickets.util.MiscUtil;

import java.util.UUID;
import java.util.function.BiFunction;

public class CommandTicketInfo implements CommandExecutor {

    private final HelpTickets plugin;

    public CommandTicketInfo(HelpTickets plugin) {
        this.plugin = plugin;
    }

    private static BiFunction<String, String, Text> info = (title, content) -> Text.of(TextColors.GRAY, " - ",
            TextColors.GOLD, title, TextColors.GRAY, ": ", TextColors.GOLD, content);

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String id = args.<String>getOne("id").get();
        CommentedConfigurationNode ticket = plugin.getTickets().get(id);

        if (ticket.isVirtual()) {
            src.sendMessage(Text.of(TextColors.RED, "That ticket does not exist!"));
            return CommandResult.success();
        }

        String[] locSplit = ticket.getNode("location").getString().split(":");
        String[] rotSplit = ticket.getNode("rotation").getString().split(":");

        String player = MiscUtil.uuidToName(ticket.getNode("player").getString());
        String message = ticket.getNode("message").getString();
        String created = ticket.getNode("created").getString();
        String world = Sponge.getServer().getWorld(UUID.fromString(locSplit[0])).get().getName();
        String x = locSplit[1], y = locSplit[2], z = locSplit[3];
        String pitch = rotSplit[0], yaw = rotSplit[1], roll = rotSplit[2];
        boolean completed = ticket.getNode("completed").getBoolean();

        plugin.getPagination().builder()
                .contents(
                        info.apply("Id", id),
                        info.apply("Player", player),
                        info.apply("Message", message),
                        info.apply("Created-At", created),
                        Text.of(TextColors.GRAY, "--------------------"),
                        info.apply("World", world),
                        info.apply("X", x),
                        info.apply("Y", y),
                        info.apply("Z", z),
                        Text.of(TextColors.GRAY, "--------------------"),
                        info.apply("Pitch", pitch),
                        info.apply("Yaw", yaw),
                        info.apply("Roll", roll),
                        Text.of(TextColors.GRAY, "--------------------"),
                        info.apply("Completed", completed ? "YES" : "NO")
                )
                .title(Text.of(GRAY, "Ticket Info"))
                .paddingString("=")
                .sendTo(src);

        return CommandResult.success();
    }
}