package dev.bourg.level2bot.listeners;

import dev.bourg.level2bot.config.ConfigFile;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class ReadyListener extends ListenerAdapter {

    private static final Logger log = getLogger(ReadyListener.class);
    private final ConfigFile configFile;
    private final ShardManager shardManager;

    public ReadyListener(ConfigFile configFile, ShardManager shardManager) {
        this.configFile = configFile;
        this.shardManager = shardManager;
    }

    /**
     * Logging a message with the guild amount and the tag when ready
     *
     * @param event the event to listen form
     */

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        log.info(configFile.devSettings().envrioment());
        switch (configFile.devSettings().envrioment()) {
            case "production" -> {
                shardManager.setPresence(OnlineStatus.ONLINE, Activity.watching("Level2"));
                log.info("Logged in as " + event.getJDA().getSelfUser().getAsTag() + "! I'm currently on " + event.getJDA().getGuilds().size() + " guild(s).");
            }
            case "dev" -> {
                if (configFile.devSettings().devServer() == null || event.getJDA().getGuildById(configFile.devSettings().devServer()) == null) {
                    log.warn("DevServer is null. Please set a existing server id that the bot is currently on.");
                    return;
                }
                shardManager.setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.playing("Maintance"));
                log.info("Logged in with dev enviroment bot only works on guild: " + shardManager.getGuildById(configFile.devSettings().devServer()).getName());
            }
        }
    }
}
