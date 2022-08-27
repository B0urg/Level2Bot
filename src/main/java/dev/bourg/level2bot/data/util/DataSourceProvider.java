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


    public static DataSource initDataSource(Database database) throws SQLException {

        MysqlDataSource mysqlDataSource = new MysqlConnectionPoolDataSource();

        mysqlDataSource.setServerName(database.host());
        mysqlDataSource.setUser(database.user());
        mysqlDataSource.setPassword(database.password());
        mysqlDataSource.setDatabaseName(database.database());
        mysqlDataSource.setPortNumber(database.port());

        testDataSource(mysqlDataSource);

        return mysqlDataSource;

    }

    public static void testDataSource(DataSource dataSource) throws SQLException {
        try (Connection conn = dataSource.getConnection()){
            if(!conn.isValid(1000)){
                throw new SQLException("Could not establish database connection.");
            }
        }

        log.info("Database connection established.");

    }

}
