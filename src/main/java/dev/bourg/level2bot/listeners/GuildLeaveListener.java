package dev.bourg.level2bot.listeners;

import dev.bourg.level2bot.data.GuildData;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class GuildLeaveListener extends ListenerAdapter{

    private final GuildData guildData;

    public GuildLeaveListener(GuildData guildData) {
        this.guildData = guildData;
    }

    /**
     * Deleting all the data when getting removed from a guild
     *
     * @param event the event to listen for
     */

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        if(guildData.getGuildByGuildID(event.getGuild().getIdLong()) != null){
            guildData.deleteData(event.getGuild().getIdLong());
        }
    }
}
