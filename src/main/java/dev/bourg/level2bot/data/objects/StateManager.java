package dev.bourg.level2bot.data.objects;

import dev.bourg.level2bot.data.objects.states.State;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.Instant;
import java.util.Date;

public class StateManager {

    private final Integer peoplePresent;
    private final Boolean open;

    public StateManager(Boolean open, Integer peoplePresent){
        this.open = open;
        this.peoplePresent = peoplePresent;
    }

    public State getAsState(){
        return new State(peoplePresent, open);
    }

    public MessageEmbed getAsEmbed() {
        if(open){
            return new EmbedBuilder()
                    .setTitle("Level2 open now!")
                    .setDescription("Level2 just opened")
                    .addField("People now present", peoplePresent.toString(), true)
                    .addField("Open", "yes", true)
                    .setAuthor("Bourg", "https://discord.com/users/933699621878906921", "https://cdn.discordapp.com/avatars/933699621878906921/4c70016a9e6181ab1cee7cb9670d8138.png?size=1024")
                    .setFooter("Level2", "https://raw.githubusercontent.com/syn2cat/design/master/lvl2/pixel/level2_round_grey.png")
                    .setColor(Color.GREEN)
                    .setTimestamp(Instant.now())
                    .build();
        }
        return new EmbedBuilder()
                .setTitle("Level2 closed!")
                .setDescription("Level2 is currently closed")
                .setAuthor("Bourg", "https://discord.com/users/933699621878906921", "https://cdn.discordapp.com/avatars/933699621878906921/4c70016a9e6181ab1cee7cb9670d8138.png?size=1024")
                .setFooter("Level2", "https://raw.githubusercontent.com/syn2cat/design/master/lvl2/pixel/level2_round_grey.png")
                .setTimestamp(Instant.now())
                .setColor(Color.RED)
                .build();
    }

}
