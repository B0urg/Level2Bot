package dev.bourg.level2bot.data.objects;

public class Guild {
    private final Boolean mention;
    private final Long guildId;
    private final Long channelId;
    private final Long userId;
    private Long messageId;


    public Guild(Boolean mention, Long guildId, Long channelId, Long userId) {
        this.mention = mention;
        this.guildId = guildId;
        this.channelId = channelId;
        this.userId = userId;
    }

    public Guild(Boolean mention, Long guildId, Long channelId, Long userId, Long messageId) {
        this.mention = mention;
        this.guildId = guildId;
        this.channelId = channelId;
        this.userId = userId;
        this.messageId = messageId;
    }

    public Boolean getMention() {
        return mention;
    }

    public Long getGuildId() {
        return guildId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public Long getUserId() {
        return userId;
    }
    public Long getMessageId(){
        return messageId;
    }
}
