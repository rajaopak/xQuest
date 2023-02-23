package id.rajaopak.xquest.file;

import id.rajaopak.xquest.XQuest;

public class ConfigFile extends FileBuilder {

    public ConfigFile(String configName) {
        super(XQuest.getInstance(), configName);
    }

    public String getServerName() {
        return this.getConfiguration().getString("server-name");
    }

    public String getRedisHost() {
        return this.getConfiguration().getString("redis.host");
    }

    public Integer getRedisPort() {
        return this.getConfiguration().getInt("redis.port");
    }

    public String getRedisPassword() {
        return this.getConfiguration().getString("redis.password");
    }

    public String getRedisChannel() {
        return this.getConfiguration().getString("redis.channel");
    }

}
