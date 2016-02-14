package me.dotdash.helptickets;

import com.google.inject.Inject;
import ninja.leaping.configurate.objectmapping.GuiceObjectMapperFactory;
import org.slf4j.Logger;

import org.spongepowered.api.Game;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import me.dotdash.helptickets.command.CommandTicket;
import me.dotdash.helptickets.command.CommandTicketComplete;
import me.dotdash.helptickets.command.CommandTicketCreate;
import me.dotdash.helptickets.command.CommandTicketList;
import me.dotdash.helptickets.command.CommandTicketTeleport;
import me.dotdash.helptickets.configuration.HoconConfig;

import java.io.File;

@Plugin(id = "HelpTickets", name = "HelpTickets", version = "1.0.0")
public class HelpTickets {

    @Inject private Game game;
    @Inject public Logger logger;
    @Inject @ConfigDir(sharedRoot = false) private File configDir;
    @Inject private GuiceObjectMapperFactory factory;

    private HoconConfig config, tickets;
    private UserStorageService userStorage;

    public HoconConfig getConfig() {
        return config;
    }

    public HoconConfig getTickets() {
        return tickets;
    }

    public UserStorageService getUserStorage() {
        return userStorage;
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        config = new HoconConfig(logger, new File(configDir, "config.conf"));
        tickets = new HoconConfig(logger, new File(configDir, "tickets.conf"));
        userStorage = game.getServiceManager().provideUnchecked(UserStorageService.class);

        game.getCommandManager().register(this, CommandSpec.builder()
                .permission("helptickets.cmd")
                .executor(new CommandTicket())
                .child(CommandSpec.builder()
                        .permission("helptickets.cmd.create")
                        .executor(new CommandTicketCreate(this))
                        .arguments(GenericArguments.remainingJoinedStrings(Text.of("message")))
                        .build(), "create", "new")
                .child(CommandSpec.builder()
                        .permission("helptickets.cmd.list")
                        .executor(new CommandTicketList(this))
                        .build(), "list")
                .child(CommandSpec.builder()
                        .permission("helptickets.cmd.tp")
                        .executor(new CommandTicketTeleport(this))
                        .arguments(GenericArguments.string(Text.of("id")))
                        .build(), "teleport", "tp")
                .child(CommandSpec.builder()
                        .permission("helptickets.cmd.complete")
                        .executor(new CommandTicketComplete(this))
                        .arguments(GenericArguments.string(Text.of("id")))
                        .build(), "complete", "done")
                .build(), "ticket", "tickets", "helptickets");

        logger.info("Loaded.");
    }

    @Listener
    public void onStopping(GameStoppingServerEvent event) {
        logger.info("Unloaded.");
    }
}