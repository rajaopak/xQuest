package id.rajaopak.xquest.file;

import id.rajaopak.xquest.XQuest;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;

public class CacheFile extends FileBuilder {

    public CacheFile(String fileName) {
        super(XQuest.getInstance(), fileName);
        setFolderPath(new File(XQuest.getInstance().getDataFolder().getPath() + File.separator + "cache"));
    }

    public ConfigurationSection getSection(String path) {
        return this.getConfiguration().getConfigurationSection(path);
    }

    public String getString(String path) {
        return this.getConfiguration().getString(path);
    }

    public int getInt(String path) {
        return this.getConfiguration().getInt(path);
    }

    public boolean getBoolean(String path) {
        return this.getConfiguration().getBoolean(path);
    }

    public void set(String path, Object value) {
        this.getConfiguration().set(path, value);
        this.saveConfig();
    }
}
