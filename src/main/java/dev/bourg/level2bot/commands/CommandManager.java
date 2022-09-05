package dev.bourg.level2bot.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.bourg.level2bot.config.ConfigFile;
import dev.bourg.level2bot.data.GuildData;
import dev.bourg.level2bot.data.StateData;
import dev.bourg.level2bot.data.objects.Guild;
import dev.bourg.level2bot.data.objects.GuildDisplayData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class CommandManager extends ListenerAdapter {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final GuildData guildData;
    private final StateData stateData;
    private final ConfigFile configFile;

    public CommandManager(DataSource dataSource, ConfigFile configFile){
        this.configFile = configFile;
        this.guildData = new GuildData(dataSource, configFile);
        this.stateData = new StateData(dataSource);
    }

    /**
     * Listening for SlashCommandInteractionEvent to manage commands
     *
     * @param event The event to listen for
     */
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if(configFile.devSettings().envrioment().equals("dev") && event.getGuild().getIdLong() != configFile.devSettings().devServer()){
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("Maintance")
                    .setDescription("The bot is currently under maintance work and only available on dev Servers!")
                    .setColor(Color.ORANGE)
                    .build()
            ).setEphemeral(true).queue();
            return;
        }

        switch (event.getName()){
            case "setup":
                // Checking if already given a channel else creating one
                TextChannel textChannel;
                if(event.getOption("channel") == null){
                    textChannel = event.getGuild().createTextChannel("Level2").complete();
                }else {
                    textChannel = event.getOption("channel").getAsChannel().asTextChannel();
                }

                textChannel.getManager().putRolePermissionOverride(event.getGuild().getPublicRole().getIdLong(), null, List.of(Permission.MESSAGE_SEND)).queue();
                Message message = textChannel.sendMessageEmbeds(stateData.getCurrentState().getAsEmbed()).complete();

                // Checking for errors while querying to database
                if(!guildData.createGuild(event.getGuild().getIdLong(), textChannel.getIdLong(), event.getUser().getIdLong(), message.getIdLong())){
                    event.replyEmbeds(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("An Error happened while registering your guild in database! \n Maybe already registered? if not so contact developers([here](https://dj-ka.net))")
                            .setColor(Color.RED)
                            .build()
                    ).queue();
                    return;
                }

                // replying if process successfully done
                event.replyEmbeds(new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setTitle("Success!")
                        .setDescription("Setup successfully!")
                        .build()
                ).setEphemeral(true).queue();

                break;
            case "data":

                switch (event.getSubcommandName()) {
                    case "request" -> {
                        // checking if guild is registered and if so getting guild data by id
                        Guild guild = guildData.getGuildByGuildID(event.getGuild().getIdLong());
                        if (guild == null) {
                            event.replyEmbeds(new EmbedBuilder()
                                    .setTitle("No Data found")
                                    .setDescription("There is no data about your server in our databases")
                                    .setColor(Color.RED)
                                    .build()
                            ).queue();
                            return;
                        }
                        // Creating a json string with the guild data and sending it
                        String dataString = "```json\n" + gson.toJson(new GuildDisplayData(guild.getUserId(), guild.getGuildId(), guild.getChannelId(), guild.getMessageId(), guild.getMention())) + "\n ```";
                        event.replyEmbeds(new EmbedBuilder()
                                .setTitle("Data - Info")
                                .setDescription("We collected all your data stored in our database and send it to you! \n If you want the data to get deleted run the command `/data purge` or remove to bot! \n If you remove the data we will no longer store your data or any backups!")
                                .addField("Data - Request", dataString, false)
                                .setColor(Color.ORANGE)
                                .build()
                        ).queue();
                    }
                    case "purge" -> {
                        // Deleting guild data and sending a success embed if it was successful
                        if (!guildData.deleteDate(event.getGuild().getIdLong())) {
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
                    }
                }

                break;

            case "settings":

                switch (event.getSubcommandName()) {
                    case "get" -> {
                        // getting setting and sending it
                        String keyString = event.getOption("key").getAsString().toLowerCase();
                        event.replyEmbeds(
                                new EmbedBuilder()
                                        .setTitle("Success")
                                        .setDescription("The current value is `" + guildData.getSettings(event.getGuild().getIdLong()).get(keyString) + "`!")
                                        .setColor(Color.GREEN)
                                        .build()
                        ).queue();
                    }
                    case "set" -> {
                        // getting setting and value changing it and sending an error if in an error happens
                        String key = event.getOption("key").getAsString().toLowerCase();
                        Boolean value = event.getOption("value").getAsBoolean();
                        if (!guildData.changeSetting(event.getGuild().getIdLong(), key, value)) {
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
                                        .setDescription("Setting `" + key + "` change successfully to `" + value + "`")
                                        .setColor(Color.GREEN)
                                        .build()
                        ).queue();
                    }
                }

                break;
        }
    }

    /**
     *
     * Listening for GuildReadyEvent and updating all commands
     *
     * @param event the event to listen for
     */

    @Override
    public void onReady(@NotNull ReadyEvent event) {




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
        if(configFile.devSettings().envrioment().equals("dev")){
            event.getJDA().getGuildById(configFile.devSettings().devServer()).updateCommands().addCommands(commands).queue();
        }else {
            event.getJDA().updateCommands().addCommands(commands).queue();
        }
    }
}
