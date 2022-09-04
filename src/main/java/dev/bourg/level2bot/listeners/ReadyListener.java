package dev.bourg.level2bot.listeners;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class ReadyListener extends ListenerAdapter {

    private static final Logger log = getLogger(ReadyListener.class);

    /**
     * Logging a message with the guild amount and the tag when ready
     *
     * @param event the event to listen form
     */

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        log.info("Logged in as " + event.getJDA().getSelfUser().getAsTag() + "! I'm currently on " + event.getJDA().getGuilds().size() + " guild(s).");
    }
}
