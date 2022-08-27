package dev.bourg.level2bot.data.objects;

import dev.bourg.level2bot.data.displayData.Settings;

public class GuildDisplayData {
    private final Long ownerId;
    private final Long guildId;
    private final Long channelId;
    private final Long messageId;
    private final Settings settings;


    public GuildDisplayData(Long ownerId, Long guildId, Long channelId, Long messageId, Boolean mention) {
        this.ownerId = ownerId;
        this.guildId = guildId;
        this.channelId = channelId;
        this.messageId = messageId;
        this.settings = new Settings(mention);
    }

    public Long ownerId() {
        return ownerId;
    }

    public Long guildId() {
        return guildId;
    }

    public Long channelId() {
        return channelId;
    }

    public Long messageId() {
        return messageId;
    }

    public Settings settings() {
        return settings;
    }
}
