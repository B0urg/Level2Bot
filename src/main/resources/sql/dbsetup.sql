CREATE TABLE IF NOT EXISTS guilds(guildId varchar(255) PRIMARY KEY NOT NULL, channelId varchar(258) NOT NULL, userId varchar(255) NOT NULL, messageId varchar(255) NOT NULL, mention varchar(255) NOT NULL);

CREATE TABLE IF NOT EXISTS state(open varchar(255) NOT NULL PRIMARY KEY, people_present integer(255) NOT NULL);