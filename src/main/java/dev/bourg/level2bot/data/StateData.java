package dev.bourg.level2bot.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.bourg.level2bot.Level2Bot;
import dev.bourg.level2bot.data.objects.StateManager;
import dev.bourg.level2bot.data.objects.states.State;
import dev.bourg.level2bot.data.util.DataHolder;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import static org.slf4j.LoggerFactory.getLogger;

public class StateData extends DataHolder {

    private static final Logger log = getLogger(Level2Bot.class);

    /**
     * Create a new {@link DataHolder} with a datasource to the given datasource
     *
     * @param source source for the connections
     */
    public StateData(DataSource source) {
        super(source);
    }

    public StateManager getCurrentState() {
        try {
            URL url = new URL("https://level2.lu/spaceapi");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                log.error("Response Code : " + responseCode);
                return null;
            }

            StringBuilder stringBuilder = new StringBuilder();
            Scanner scanner = new Scanner(url.openStream());

            while (scanner.hasNext()) {
                stringBuilder.append(scanner.nextLine());
            }

            scanner.close();

            JsonObject jsonObject = (JsonObject) JsonParser.parseString(stringBuilder.toString());
            Boolean state = jsonObject.getAsJsonObject("state").get("open").getAsBoolean();
            Integer peoplePresent = jsonObject.getAsJsonObject("sensors").getAsJsonArray("people_now_present").get(0).getAsJsonObject().get("value").getAsInt();
            return new StateManager(state, peoplePresent);

        } catch (IOException e) {
            log.error("Error happened while calling rest api", e);
            return null;
        }
    }
    public boolean updateSavedState(State state){
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM state;"
        )){
            stmt.execute();
        } catch (SQLException e) {
            log.error("Could not execute query", e);
            return false;
        }
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO state(open, people_present) VALUES(?, ?);"
        )){
            stmt.setString(1, state.getOpen().toString());
            stmt.setLong(2, state.getPeoplePresent());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            log.error("Could not execute query", e);
            return false;
        }
    }

    public State getSavedState(){
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM state;"
        )) {
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return new State(resultSet.getInt("people_present"), Boolean.parseBoolean(resultSet.getString("open")));
            } else {
                return null;
            }
        } catch (SQLException e) {
            log.error("Could not execute query", e);
            return null;
        }
    }

    }
