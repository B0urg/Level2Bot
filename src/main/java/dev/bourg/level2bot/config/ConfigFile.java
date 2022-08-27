package dev.bourg.level2bot.config;

import dev.bourg.level2bot.config.elemets.BaseSettings;
import dev.bourg.level2bot.config.elemets.Database;
import dev.bourg.level2bot.config.elemets.MessageSettings;

public class ConfigFile {
    private Database database = new Database();
    private BaseSettings baseSettings = new BaseSettings();
    private MessageSettings messageSettings = new MessageSettings();

    public BaseSettings baseSettings(){
        return baseSettings;
    }

    public Database database(){
        return database;
    }

    public MessageSettings messageSettings(){
        return messageSettings;
    }

}
