package dev.bourg.level2bot;

import dev.bourg.level2bot.commands.CommandManager;
import dev.bourg.level2bot.config.ConfigFile;
import dev.bourg.level2bot.config.Configuration;
import dev.bourg.level2bot.data.GuildData;
import dev.bourg.level2bot.data.StateData;
import dev.bourg.level2bot.data.util.DataSourceProvider;
import dev.bourg.level2bot.data.util.DbSetup;
import dev.bourg.level2bot.listeners.ChannelRemoveListener;
import dev.bourg.level2bot.listeners.GuildLeaveListener;
import dev.bourg.level2bot.listeners.ReadyListener;
import dev.bourg.level2bot.tasks.StateDetection;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;

import javax.security.auth.login.LoginException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import static org.slf4j.LoggerFactory.getLogger;

public class Level2Bot {

    private static final Logger log = getLogger(Level2Bot.class);
    private static Level2Bot instance;
    private ShardManager shardManager;
    private ConfigFile configuration;
    private DataSource dataSource;
    private GuildData guildData;
    private StateData stateData;


    public static void main(String[] args) {
        Level2Bot.instance = new Level2Bot();
        instance.start();
    }

    /**
     *
     * Starting all bot related modules
     *
     */

    private void start(){

        log.info("Initializing Configuration");
        initConfiguration();

        log.info("Initializing Database");
        initDatabase();

        log.info("Initializing JDA");
        try {
            initJDA();
        } catch (LoginException e) {
            log.error("Could not connect to discord", e);
            return;
        }

        log.info("Initializing Bot");
        initBot();

    }

    /**
     *
     * initializing the config
     *
     */

    private void initConfiguration(){

        configuration = new Configuration().readConfig();
    }

    /**
     *
     * initializing the database connection
     *
     */

    private void initDatabase(){
        try {
            dataSource = DataSourceProvider.initDataSource(configuration.database());
        } catch (SQLException e) {
            log.error("Could not establish database connection.", e);
            return;
        }
        try {
            DbSetup.initDb(dataSource);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

        if(!new StateData(dataSource).updateSavedState(new StateData(dataSource).getCurrentState().getAsState())){
            System.exit(1);;
        }

    }

    /**
     * Initializing JDA
     *
     * @throws LoginException if connection to discord fails
     */
    private void initJDA() throws LoginException {

        shardManager = DefaultShardManagerBuilder.createDefault(configuration.baseSettings().token())
                .build();

    }

    /**
     *
     * initializing the bot
     *
     */

    private void initBot(){

        this.guildData = new GuildData(dataSource, configuration);
        this.stateData = new StateData(dataSource);

        shardManager.addEventListener(new ReadyListener(configuration, shardManager), new CommandManager(guildData, stateData, configuration), new GuildLeaveListener(guildData), new ChannelRemoveListener(guildData));


        Timer timer = new Timer();
        TimerTask task = new StateDetection(guildData, stateData, configuration, shardManager);
        timer.schedule(task, 0, 10000);

    }

}