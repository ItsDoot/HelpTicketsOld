package me.dotdash.helptickets.command;

import com.flowpowered.math.vector.Vector3d;

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

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;

public class CommandTicketCreate implements CommandExecutor {

    private final SecureRandom rand = new SecureRandom();

    private final HelpTickets tickets;

    public CommandTicketCreate(HelpTickets tickets) {
        this.tickets = tickets;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.RED, "You must be a player to use that command."));
            return CommandResult.success();
        }

        Player player = (Player) src;
        String message = args.<String>getOne("message").get();
        String randId;

        do {
            randId = new BigInteger(130, rand).toString(32).substring(0, 7);
        } while(!tickets.getTickets().get(randId).isVirtual());

        Location<World> loc = player.getLocation();
        Vector3d rot = player.getRotation();

        tickets.getTickets().get(randId).setValue(new HashMap<>());
        tickets.getTickets().get(randId, "player").setValue(player.getUniqueId().toString());
        tickets.getTickets().get(randId, "message").setValue(message);
        tickets.getTickets().get(randId, "location").setValue(loc.getExtent().getUniqueId().toString() + ":"
                + loc.getX() + ":" + loc.getY() + ":" + loc.getZ());
        tickets.getTickets().get(randId, "rotation").setValue(rot.getX() + ":" + rot.getY() + ":" + rot.getZ());
        tickets.getTickets().save();

        player.sendMessage(Text.of(TextColors.GREEN, "Ticket created!"));
        return CommandResult.success();
    }
}