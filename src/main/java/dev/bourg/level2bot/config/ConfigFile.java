package dev.bourg.level2bot.config;

import dev.bourg.level2bot.config.elemets.BaseSettings;
import dev.bourg.level2bot.config.elemets.Database;
import dev.bourg.level2bot.config.elemets.DevSettings;
import dev.bourg.level2bot.config.elemets.MessageSettings;

public class ConfigFile {
    private final Database database = new Database();
    private final BaseSettings baseSettings = new BaseSettings();
    private final MessageSettings messageSettings = new MessageSettings();
    private final DevSettings devSettings = new DevSettings();

    public BaseSettings baseSettings(){
        return baseSettings;
    }

    public Database database(){
        return database;
    }

    public MessageSettings messageSettings(){
        return messageSettings;
    }
    public DevSettings devSettings(){
        return devSettings;
    }

}
