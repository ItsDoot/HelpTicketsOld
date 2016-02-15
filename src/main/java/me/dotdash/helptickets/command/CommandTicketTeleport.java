package me.dotdash.helptickets.command;

import com.flowpowered.math.vector.Vector3d;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import me.dotdash.helptickets.HelpTickets;

import java.util.UUID;

public class CommandTicketTeleport implements CommandExecutor {

    private final HelpTickets plugin;

    public CommandTicketTeleport(HelpTickets plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.RED, "You must be a player to use that command."));
            return CommandResult.success();
        }

        Player player = (Player) src;
        String id = args.<String>getOne("id").get();

        CommentedConfigurationNode ticket = plugin.getTickets().get(id);

        if (ticket.isVirtual()) {
            src.sendMessage(Text.of(TextColors.RED, "That ticket does not exist!"));
            return CommandResult.success();
        }

        String[] locSplit = ticket.getNode("location").getString().split(":");
        Location<World> loc = new Location<>(Sponge.getGame().getServer().getWorld(UUID.fromString(locSplit[0])).get(),
                Double.valueOf(locSplit[1]), Double.valueOf(locSplit[2]), Double.valueOf(locSplit[3]));
        player.setLocation(loc);

        String[] rotSplit = ticket.getNode("rotation").getString().split(":");
        Vector3d rot = new Vector3d(Double.valueOf(rotSplit[0]), Double.valueOf(rotSplit[1]),
                Double.valueOf(rotSplit[2]));
        player.setRotation(rot);

        player.sendMessage(Text.of(TextColors.GREEN, "Teleported to location of ticket."));
        player.sendMessage(Text.of(TextColors.GRAY, "Ticket message: ", TextColors.WHITE,
                ticket.getNode("message").getString()));

        return CommandResult.success();
    }
}