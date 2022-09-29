package dev.bourg.level2bot.tasks;

import dev.bourg.level2bot.config.ConfigFile;
import dev.bourg.level2bot.data.GuildData;
import dev.bourg.level2bot.data.StateData;
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
    private final ConfigFile configFile;

    public StateDetection(GuildData guildData, StateData stateData, ConfigFile configFile, ShardManager shardManager){
        this.guildData = guildData;
        this.stateData = stateData;
        this.shardManager = shardManager;
        this.configFile = configFile;
    }

    /**
     *
     * Creating a run method that runs every 10 seconds and checks for changes
     *
     */

    @Override
    public void run() {
        if(stateData.getCurrentState().getAsState().getOpen() == null || stateData.getCurrentState().getAsState().getPeoplePresent() == null) return;
        if(stateData.getSavedState().getOpen() != stateData.getCurrentState().getAsState().getOpen()){
            stateData.updateSavedState(stateData.getCurrentState().getAsState());
            guildData.getGuilds().forEach(guildDatas -> {
                if(configFile.devSettings().envrioment().equals("dev") && !guildDatas.getGuildId().equals(configFile.devSettings().devServer())) return;
                Guild guild = shardManager.getGuildById(guildDatas.getGuildId());
                TextChannel channel = guild.getTextChannelById(guildDatas.getChannelId());
                if(channel.retrieveMessageById(guildDatas.getMessageId()).complete().getEmbeds().get(0).getDescription().equals("Level2 is currently closed")) return;
                Message message = channel.sendMessageEmbeds(stateData.getCurrentState().getAsEmbed()).complete();
                if(!guildData.updateMessageId(guild.getIdLong(), message.getIdLong())){
                    System.exit(1);
                }
                if(guildDatas.getMention()){
                    Message ping = channel.sendMessage(guild.getPublicRole().getAsMention()).complete();
                    ping.delete().queue();
                }
            });
        }
        if(!stateData.getSavedState().getPeoplePresent().equals(stateData.getCurrentState().getAsState().getPeoplePresent()) && stateData.getCurrentState().getAsState().getOpen()){
            guildData.getGuilds().forEach(guildDatas -> {
                if(configFile.devSettings().envrioment().equals("dev") && !guildDatas.getGuildId().equals(configFile.devSettings().devServer())) return;
                Guild guild = shardManager.getGuildById(guildDatas.getGuildId());
                TextChannel channel = guild.getTextChannelById(guildDatas.getChannelId());
                channel.editMessageEmbedsById(guildDatas.getMessageId(), stateData.getCurrentState().getAsEmbed()).queue();
            });
        }
    }
}
