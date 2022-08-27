package dev.bourg.level2bot.data.util;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class DataHolder {

    private final DataSource source;

    /**
     * Create a new {@link DataHolder} with a datasource to the given datasource
     *
     * @param source source for the connections
     */

    public DataHolder(DataSource source){
        this.source = source;
    }

    /**
     * Trying to get a new connection to the data source that this {@link DataHolder} contains.
     *
     * @return a new connection from the current data source
     *
     * @throws SQLException if database access fails
     */

    protected Connection conn() throws SQLException {
        return source.getConnection();
    }

    protected DataSource source(){
        return source;
    }

}
