package dev.bourg.level2bot.data.util;

import org.slf4j.Logger;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.slf4j.LoggerFactory.getLogger;

public class DbSetup {

    private static final Logger log = getLogger(DbSetup.class);

    public static void initDb(DataSource dataSource) throws IOException, SQLException {

        String setup;
        try(InputStream in = DbSetup.class.getClassLoader().getResourceAsStream("dbsetup.sql")){
            setup = new String(in.readAllBytes());
        }catch (IOException e){
            log.error("Could not read db setup file", e);
            throw e;
        }

        String[] queries = setup.split(";");
        try (Connection conn = dataSource.getConnection()){
            conn.setAutoCommit(false);

            for(String query : queries){

                if(query.isBlank() || query.startsWith("//")) continue;
                try (PreparedStatement stmt = conn.prepareStatement(query)){
                    stmt.execute();
                }
            }
            conn.commit();
        }
        log.info("Database setup completed.");
    }

}
