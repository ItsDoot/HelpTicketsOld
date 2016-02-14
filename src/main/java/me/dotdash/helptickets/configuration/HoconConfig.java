package me.dotdash.helptickets.configuration;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class HoconConfig {

    private final Logger logger;
    private final File file;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private CommentedConfigurationNode node;

    public HoconConfig(Logger logger, File file) {
        this.logger = logger;
        this.file = file;
        reload();
    }

    public HoconConfig(Logger logger, File file, URL defaultLoader) {
        this(logger, file);

        if(defaultLoader != null) {
            try {
                node.mergeValuesFrom(HoconConfigurationLoader.builder().setURL(defaultLoader).build().load());
            } catch (IOException e) {
                logger.error("Could not merge default values into config.");
            }
        }
    }

    public void reload() {
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        if(!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                logger.error("Could not create new file for config.");
            }

        loader = HoconConfigurationLoader.builder().setFile(file).build();

        try {
            node = loader.load();
        } catch (IOException e) {
            node = loader.createEmptyNode();
            logger.warn("Could not correctly load config data.");
        }
    }

    public void save() {
        try {
            loader.save(node);
        } catch (IOException e) {
            logger.error("Could not save config.");
        }
    }

    public CommentedConfigurationNode get(Object... path) {
        return node.getNode(path);
    }

    public CommentedConfigurationNode get() {
        return node;
    }
}