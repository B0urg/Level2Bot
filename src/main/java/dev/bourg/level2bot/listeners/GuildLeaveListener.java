package dev.bourg.level2bot.listeners;

import dev.bourg.level2bot.config.ConfigFile;
import dev.bourg.level2bot.data.GuildData;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;

public class GuildLeaveListener extends ListenerAdapter{

    private final GuildData guildData;

    public GuildLeaveListener(DataSource dataSource, ConfigFile configFile) {
        this.guildData = new GuildData(dataSource, configFile);
    }

    /**
     * Deleting all the data when getting removed from a guild
     *
     * @param event the event to listen for
     */

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        if(guildData.getGuildByGuildID(event.getGuild().getIdLong()) != null){
            guildData.deleteDate(event.getGuild().getIdLong());
        }
    }
}
