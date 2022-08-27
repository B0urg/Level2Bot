package dev.bourg.level2bot.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.bourg.level2bot.Level2Bot;
import dev.bourg.level2bot.config.ConfigFile;
import dev.bourg.level2bot.data.GuildData;
import dev.bourg.level2bot.data.StateData;
import dev.bourg.level2bot.data.objects.Guild;
import dev.bourg.level2bot.data.objects.GuildDisplayData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class CommandManager extends ListenerAdapter {

    private static final Logger log = getLogger(Level2Bot.class);
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private DataSource dataSource;
    private ConfigFile configFile;
    private GuildData guildData;
    private StateData stateData;
    public CommandManager(DataSource dataSource, ConfigFile configFile){
        this.dataSource = dataSource;
        this.configFile = configFile;
        this.guildData = new GuildData(this.dataSource, this.configFile);
        this.stateData = new StateData(this.dataSource);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getName()){
            case "setup":
                TextChannel textChannel;
                if(event.getOption("channel") == null){
                    textChannel = event.getGuild().createTextChannel("Level").complete();
                }else {
                    textChannel = event.getOption("channel").getAsChannel().asTextChannel();
                }

                textChannel.getManager().putRolePermissionOverride(event.getGuild().getPublicRole().getIdLong(), null, List.of(Permission.MESSAGE_SEND)).queue();
                Message message = textChannel.sendMessageEmbeds(stateData.getCurrentState().getAsEmbed()).complete();

                if(!guildData.createGuild(event.getGuild().getIdLong(), textChannel.getIdLong(), event.getUser().getIdLong(), message.getIdLong())){
                    event.replyEmbeds(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("An Error happened while registering your guild in database! \n Maybe already registered? if not so contact developers([here](https://dj-ka.net))")
                            .setColor(Color.RED)
                            .build()
                    ).queue();
                    return;
                }

                event.replyEmbeds(new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setTitle("Success!")
                        .setDescription("Setup successfully!")
                        .build()
                ).setEphemeral(true).queue();

                break;
            case "data":

                switch (event.getSubcommandName()){
                    case "request":
                        Guild guild = guildData.getGuildByGuildID(event.getGuild().getIdLong());
                        if(guild == null){
                            event.replyEmbeds(new EmbedBuilder()
                                    .setTitle("No Data found")
                                    .setDescription("There is no data about your server in our databases")
                                    .setColor(Color.RED)
                                    .build()
                            ).queue();
                            return;
                        }
                        String dataString = "```json\n" + gson.toJson(new GuildDisplayData(guild.getUserId(), guild.getGuildId(), guild.getChannelId(), guild.getMessageId(), guild.getMention())) + "\n ```";

                        event.replyEmbeds(new EmbedBuilder()
                                        .setTitle("Data - Info")
                                        .setDescription("We collected all your data stored in our database and send it to you! \n If you want the data to get deleted run the command `/data purge` or remove to bot! \n If you remove the data we will no longer store your data or any backups!")
                                        .addField("Data - Request", dataString, false)
                                        .setColor(Color.ORANGE)
                                        .build()
                                ).queue();

                        break;
                    case "purge":

                        if(!guildData.deleteDate(event.getGuild().getIdLong())){
                            event.replyEmbeds(new EmbedBuilder()
                                    .setTitle("Error")
                                    .setDescription("An Error happened while querying to data base \n Try again later or contact developers([here](https://dj-ka.net))")
                                    .setColor(Color.RED)
                                    .build()
                            ).queue();
                            return;
                        }

                        event.replyEmbeds(new EmbedBuilder()
                                .setTitle("Success")
                                .setDescription("Successfully deleted all your data in our databases")
                                .setColor(new Color(133, 2, 0))
                                .build()
                        ).queue();

                        break;
                }

                break;

            case "settings":

                switch (event.getSubcommandName()){

                    case "get":

                        String keyString = event.getOption("key").getAsString().toLowerCase();

                        event.replyEmbeds(
                                new EmbedBuilder()
                                        .setTitle("Success")
                                        .setDescription("The current value is `" + guildData.getSettings(event.getGuild().getIdLong()).get(keyString) + "`!")
                                        .setColor(Color.GREEN)
                                        .build()
                        ).queue();

                        break;
                    case "set":

                        String key = event.getOption("key").getAsString().toLowerCase();
                        Boolean value = event.getOption("value").getAsBoolean();

                        if(!guildData.changeSetting(event.getGuild().getIdLong(), key, value)){
                            event.replyEmbeds(
                                    new EmbedBuilder()
                                            .setColor(Color.RED)
                                            .setTitle("Error")
                                            .setDescription("An Error happened while querying to database \n Try again later or contact developers([here](https://dj-ka.net))")
                                            .build()
                            ).queue();
                            return;
                        }

                        event.replyEmbeds(
                                new EmbedBuilder()
                                        .setTitle("Success")
                                        .setDescription("Setting `" + key  + "` change successfully to `" + value + "`")
                                        .setColor(Color.GREEN)
                                        .build()
                        ).queue();

                        break;

                }

                break;
        }
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> commands = new ArrayList<>();
        commands.add(Commands.slash("setup", "Setup the bot for your server!").addOption(OptionType.CHANNEL, "channel", "The update channel").setDefaultPermissions(DefaultMemberPermissions.DISABLED));
        commands.add(Commands.slash("data", "Manage your data stored in our database").addSubcommands(
                new SubcommandData("request", "Request all your data stored by us"),
                new SubcommandData("purge", "Remove all tracked data from your server"))
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED));
        commands.add(Commands.slash("settings", "Manage your bot Settings").addSubcommands(
                new SubcommandData("set", "Set the value of a setting").addOptions(
                        new OptionData(OptionType.STRING, "key", "The Name of the Setting", true)
                                .addChoice("Mention", "mention"),
                        new OptionData(OptionType.BOOLEAN, "value", "The new Value", true)
                ),
                new SubcommandData("get", "Get the value of a setting").addOptions(
                        new OptionData(OptionType.STRING, "key", "The Name of the setting", true)
                                .addChoice("Mention", "mention")
                )
        ).setDefaultPermissions(DefaultMemberPermissions.DISABLED));
        event.getGuild().updateCommands().addCommands(commands).queue();
    }
}
