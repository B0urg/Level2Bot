package dev.bourg.level2bot.data.util;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import dev.bourg.level2bot.Level2Bot;
import dev.bourg.level2bot.config.elemets.Database;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.slf4j.LoggerFactory.getLogger;

public class DataSourceProvider {

    private static final Logger log = getLogger(DataSourceProvider.class);

    /**
     *
     * Initializing a dataSource with the given data and returning it
     *
     * @param database the login data to log in to the database
     * @return The dataSource that gets created with the given data
     * @throws SQLException if the login to the database fails
     */

    public static DataSource initDataSource(Database database) throws SQLException {

        MysqlDataSource mysqlDataSource = new MysqlConnectionPoolDataSource();
        mysqlDataSource.setPortNumber(database.port());
        mysqlDataSource.setDatabaseName(database.database());
        mysqlDataSource.setServerName(database.host());
        mysqlDataSource.setUser(database.user());
        mysqlDataSource.setPassword(database.password());

        testDataSource(mysqlDataSource);

        return mysqlDataSource;

    }

    /**
     *
     * Testing if DataSource is valid
     *
     * @param dataSource the dataSource to test
     * @throws SQLException if the connection from the dataSource is not valid
     */

    public static void testDataSource(DataSource dataSource) throws SQLException {
        try (Connection conn = dataSource.getConnection()){
            if(!conn.isValid(1000)){
                throw new SQLException("Could not establish database connection.");
            }
        }

        log.info("Database connection established.");

    }

}
