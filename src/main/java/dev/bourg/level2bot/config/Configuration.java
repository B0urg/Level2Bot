package dev.bourg.level2bot.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class Configuration {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File file = new File("config.json");

    /**
     * Saving config and writing default value to it
     */
    private void saveConfig() {
        try (FileWriter fileWriter = new FileWriter(file)){
            if(!file.exists()) file.createNewFile();
            fileWriter.write(gson.toJson(new ConfigFile()));
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reading config and returning it if it exists else creating it
     *
     * @return The config content in form of a custom class(ConfigFile)
     */

    public ConfigFile readConfig(){
        if(!file.exists()) saveConfig();
        try {
            return gson.fromJson(new FileReader(file), ConfigFile.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }




    }
