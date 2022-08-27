package dev.bourg.level2bot.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.bourg.level2bot.Level2Bot;
import dev.bourg.level2bot.config.ConfigFile;
import dev.bourg.level2bot.data.objects.Guild;
import dev.bourg.level2bot.data.objects.StateManager;
import dev.bourg.level2bot.data.util.DataHolder;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

public class GuildData extends DataHolder {

    private static final Logger log = getLogger(Level2Bot.class);
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private ConfigFile configFile;
    /**
     * Create a new {@link DataHolder} with a datasource to the given datasource
     *
     * @param source source for the connections
     */
    public GuildData(DataSource source, ConfigFile configFile) {
        super(source);
        this.configFile = configFile;
    }

    public boolean createGuild(Long guildId, Long channelId, Long userId, Long messageId){
        if(getGuildByGuildID(guildId) != null){
            return false;
        }
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO guilds(guildId, channelId, userId, messageId, mention) VALUES(?, ?, ?, ?, ?);"
        )){
            stmt.setLong(1, guildId);
            stmt.setLong(2, channelId);
            stmt.setLong(3, userId);
            stmt.setLong(4, messageId);
            stmt.setString(5, configFile.messageSettings().mention().toString());
            stmt.execute();
            return true;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Guild> getGuilds(){
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM guilds;"
        )) {
            ResultSet resultSet = stmt.executeQuery();
            List<Guild> guilds = new ArrayList<>();
            while (resultSet.next()) {
                if(resultSet.getObject("messageId") == null){
                    guilds.add(
                            new Guild(
                                    Boolean.parseBoolean(resultSet.getString("mention")),
                                    resultSet.getLong("guildId"),
                                    resultSet.getLong("channelId"),
                                    resultSet.getLong("userId")
                            )
                    );
                    continue;
                }
                guilds.add(
                        new Guild(
                                Boolean.parseBoolean(resultSet.getString("mention")),
                                resultSet.getLong("guildId"),
                                resultSet.getLong("channelId"),
                                resultSet.getLong("userId"),
                                resultSet.getLong("messageId")
                        )
                );

            }
            return guilds;
        } catch (SQLException e) {
            log.error("Could not execute query", e);
            return new ArrayList<>();
        }
    }

    public Guild getGuildByGuildID(Long guildId) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM guilds WHERE guildId = ?;"
        )) {
            stmt.setLong(1, guildId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                    return new Guild(
                            Boolean.parseBoolean(resultSet.getString("mention")),
                            resultSet.getLong("guildId"),
                            resultSet.getLong("channelId"),
                            resultSet.getLong("userId"),
                            resultSet.getLong("messageId")
                    );
            }else {
                return null;
            }
        } catch (SQLException e) {
            log.error("Could not execute query", e);
            return null;
        }
    }

    public boolean changeSetting(Long guildId, String key, Boolean value){

        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
                "UPDATE guilds SET " + key + " = ? WHERE guildId = ?;"
        )){
            stmt.setString(1, value.toString());
            stmt.setLong(2, guildId);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            log.error("Could not execute query", e);
            return false;
        }
    }

    public Map<String, Boolean> getSettings(Long guildId) {
        Map<String, Boolean> settings = new HashMap<>();


            try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM guilds WHERE guildId = ?;"
            )) {
                stmt.setLong(1, guildId);
                ResultSet resultSet = stmt.executeQuery();
                if (resultSet.next()) {
                    settings.put(
                            "mention",
                            resultSet.getBoolean("mention")
                    );
                } else {
                    return null;
                }
                return settings;
            } catch (SQLException e) {
                log.error("Could not execute query", e);
                return null;
            }
        }
        public boolean updateMessageId(Long guildId, Long messageId){

            try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE guilds SET messageId = ? WHERE guildId = ?;"
            )){
                stmt.setLong(1, messageId);
                stmt.setLong(2, guildId);
                stmt.execute();
                return true;
            } catch (SQLException e) {
                log.error("Could not execute query", e);
                return false;
            }
    }

    public boolean deleteDate(Long guildId){
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM guilds WHERE guildId = ?;"
        )){
            stmt.setLong(1, guildId);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            log.error("Could not execute query", e);
            return false;
        }
    }

}
