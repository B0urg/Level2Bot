package dev.bourg.level2bot.data.displayData;

public class Settings {
    private final Boolean mention;


    public Settings(Boolean mention) {
        this.mention = mention;
    }

    public Boolean mention(){
        return mention;
    }

}
