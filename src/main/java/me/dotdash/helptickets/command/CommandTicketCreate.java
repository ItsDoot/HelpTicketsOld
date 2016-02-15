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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class CommandTicketCreate implements CommandExecutor {

    private final HelpTickets plugin;

    public CommandTicketCreate(HelpTickets plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.RED, "You must be a player to use that command."));
            return CommandResult.success();
        }

        Player player = (Player) src;
        String message = args.<String>getOne("message").get();
        int counter = 0;
        String randId;

        do {
            randId = player.getName().substring(0, 3) + counter;
            counter++;
        } while(!plugin.getTickets().get(randId).isVirtual());

        Location<World> loc = player.getLocation();
        Vector3d rot = player.getRotation();

        plugin.getTickets().get(randId).setValue(new HashMap<>());
        plugin.getTickets().get(randId, "player").setValue(player.getUniqueId().toString());
        plugin.getTickets().get(randId, "message").setValue(message);
        plugin.getTickets().get(randId, "location").setValue(loc.getExtent().getUniqueId().toString() + ":"
                + loc.getX() + ":" + loc.getY() + ":" + loc.getZ());
        plugin.getTickets().get(randId, "rotation").setValue(rot.getX() + ":" + rot.getY() + ":" + rot.getZ());
        plugin.getTickets().get(randId, "completed").setValue(false);
        plugin.getTickets().get(randId, "created").setValue(new SimpleDateFormat("MM/dd/yyyy hh:mm a")
                .format(new Date(System.currentTimeMillis())));
        plugin.getTickets().save();

        player.sendMessage(Text.of(TextColors.GREEN, "Ticket created!"));
        return CommandResult.success();
    }
}