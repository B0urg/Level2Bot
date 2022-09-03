package dev.bourg.level2bot.tasks;

import dev.bourg.level2bot.config.ConfigFile;
import dev.bourg.level2bot.data.GuildData;
import dev.bourg.level2bot.data.StateData;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.TimerTask;

public class StateDetection extends TimerTask {

    private final GuildData guildData;
    private final StateData stateData;
    private final ShardManager shardManager;

    public StateDetection(DataSource dataSource, ConfigFile configFile, ShardManager shardManager){
        this.guildData = new GuildData(dataSource, configFile);
        this.stateData = new StateData(dataSource);
        this.shardManager = shardManager;
    }

    /**
     *
     * Creating a run method that runs every 10 seconds and checks for changes
     *
     */

    @Override
    public void run() {
        if(stateData.getSavedState().getOpen() != stateData.getCurrentState().getAsState().getOpen()){
            stateData.updateSavedState(stateData.getCurrentState().getAsState());
            guildData.getGuilds().forEach(guildDatas -> {
                Guild guild = shardManager.getGuildById(guildDatas.getGuildId());
                TextChannel channel = guild.getTextChannelById(guildDatas.getChannelId());
                Message message = channel.sendMessageEmbeds(stateData.getCurrentState().getAsEmbed()).complete();
                guildData.updateMessageId(guild.getIdLong(), message.getIdLong());
                if(guildDatas.getMention()){
                    Message ping = channel.sendMessage(guild.getPublicRole().getAsMention()).complete();
                    ping.delete().queue();
                }
            });
        }
        if(!Objects.equals(stateData.getSavedState().getPeoplePresent(), stateData.getCurrentState().getAsState().getPeoplePresent()) && stateData.getCurrentState().getAsState().getOpen()){
            guildData.getGuilds().forEach(guildDatas -> {
                Guild guild = shardManager.getGuildById(guildDatas.getGuildId());
                TextChannel channel = guild.getTextChannelById(guildDatas.getChannelId());
                Message message = channel.getHistory().getMessageById(guildDatas.getMessageId());
                message.editMessageEmbeds(stateData.getCurrentState().getAsEmbed());
            });
        }
    }
}
