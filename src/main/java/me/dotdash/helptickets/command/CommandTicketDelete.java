package me.dotdash.helptickets.command;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import static org.spongepowered.api.text.format.TextColors.*;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import me.dotdash.helptickets.HelpTickets;

import java.util.UUID;

public class CommandTicketDelete implements CommandExecutor {

    private final HelpTickets plugin;

    public CommandTicketDelete(HelpTickets plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String id = args.<String>getOne("id").get();
        CommentedConfigurationNode ticket = plugin.getTickets().get(id);

        if (ticket.isVirtual()) {
            src.sendMessage(Text.of(RED, "That ticket does not exist!"));
            return CommandResult.success();
        }

        if(!ticket.getNode("completed").getBoolean())
            Sponge.getServer().getPlayer(UUID.fromString(ticket.getNode("player").getString()))
                    .ifPresent(player -> player.sendMessage(Text.of(GREEN, "Your ticket has been completed by ",
                            WHITE, src.getName()))
                    );

        boolean completed = ticket.getNode("completed").getBoolean();
        ticket.setValue(null);
        src.sendMessage(Text.of(GREEN, completed ? "Ticket deleted." : "Ticket completed & deleted."));

        return CommandResult.success();
    }
}