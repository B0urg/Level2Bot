package dev.bourg.level2bot.config.elemets;

public class Database {

    private String host = "localhost";
    private Integer port = 3306;
    private String database = "level2bot";
    private String user = "root";
    private String password = "pw";

    public String host() {
        return host;
    }

    public Integer port() {
        return port;
    }

    public String database() {
        return database;
    }

    public String user() {
        return user;
    }

    public String password() {
        return password;
    }
}
