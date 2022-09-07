package dev.bourg.level2bot.listeners;

import dev.bourg.level2bot.data.GuildData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ChannelRemoveListener extends ListenerAdapter {

    private final GuildData guildData;

    public ChannelRemoveListener(GuildData guildData) {
        this.guildData = guildData;
    }

    @Override
    public void onChannelDelete(@NotNull ChannelDeleteEvent event) {
        if(guildData.getGuildByGuildID(event.getGuild().getIdLong()) == null) return;
        if(guildData.getGuildByGuildID(event.getGuild().getIdLong()).getChannelId() != event.getChannel().getIdLong()) return;

        if(!guildData.deleteData(event.getGuild().getIdLong())){
            event.getGuild().getTextChannels().get(0).sendMessageEmbeds(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("Could not delete your data from database \n Please contact our developers([here](https://dj-ka.net))!")
                    .setColor(Color.red)
                    .build()
            ).queue();
        }
    }
}
