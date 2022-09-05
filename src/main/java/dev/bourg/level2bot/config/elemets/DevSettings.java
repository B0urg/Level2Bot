package dev.bourg.level2bot.config.elemets;

public class DevSettings {
    private String envrioment = "production";
    private Long devServer = null;
    public String envrioment(){
        return envrioment;
    }

    public Long devServer(){
        return devServer;
    }

}
