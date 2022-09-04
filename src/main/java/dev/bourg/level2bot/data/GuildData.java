package dev.bourg.level2bot.data;

import dev.bourg.level2bot.Level2Bot;
import dev.bourg.level2bot.config.ConfigFile;
import dev.bourg.level2bot.data.objects.Guild;
import dev.bourg.level2bot.data.util.DataHolder;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

public class  GuildData extends DataHolder {

    private static final Logger log = getLogger(Level2Bot.class);

    private final ConfigFile configFile;
    /**
     * Create a new {@link DataHolder} with a datasource to the given datasource
     *
     * @param source source for the connections
     */
    public GuildData(DataSource source, ConfigFile configFile) {
        super(source);
        this.configFile = configFile;
    }

    /**
     *
     * creating a new guild in the database
     *
     * @param guildId the id of the guild
     * @param channelId the channel id to send updates to
     * @param userId the id of the user who setuped the bot
     * @param messageId the last send message to the channel from the id above
     * @return if the query was successful or not
     */
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

    /**
     *
     * Getting all the guilds currently in the database
     *
     * @return if the query was successful or not
     */

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


    /**
     *
     * Getting guild by id
     *
     * @param guildId the id of the guild to get
     * @return The guild with the given id or null if it not exists
     */
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

    /**
     *
     * changing a setting of guild by its id
     *
     * @param guildId the id of the guild to change the setting from
     * @param key the setting name
     * @param value the new value of the setting
     * @return if the query was successful or not
     */

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

    /**
     *
     * Getting all setting of a guild
     *
     * @param guildId the id of the guild to get the settings form
     * @return a HashMap with the setting name as key and the content as value
     */

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

    /**
     *
     * change the laser message id of a guild
     *
     * @param guildId the id of the guild to change the message id from
     * @param messageId the new message id to change to
     * @return if the query was successful or not
     */

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

    /**
     *
     * Deleting a guild from ur database by its id
     *
     * @param guildId the id of the guild to delete id
     * @return if the query was successful or not
     */

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
