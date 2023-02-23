package id.rajaopak.xquest.file;

import id.rajaopak.xquest.XQuest;
import id.rajaopak.xquest.util.Utils;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;

public class FileBuilder {
    private final XQuest plugin;
    private final String fileName;
    private File configFile;
    private File folderPath;

    private YamlConfiguration configuration;

    public FileBuilder(XQuest plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;

        try {
            loadConfig();
        } catch (IOException e) {
            plugin.getLogger().severe("Cannot load the Config!");
            e.printStackTrace();
        }
    }

    public void setFolderPath(File folderPath) {
        this.folderPath = folderPath;
    }

    public void loadConfig() throws IOException {
        configFile = new File(Objects.requireNonNullElseGet(folderPath, plugin::getDataFolder), fileName);

        if (!plugin.getDataFolder().exists()) {
            if (!configFile.getParentFile().mkdirs()) {
                configFile.getParentFile().createNewFile();
            }
        }

        if (!configFile.exists()) {
            if (plugin.getResource(fileName) == null) {
                configFile.createNewFile();
                return;
            }

            try (InputStream in = plugin.getResource(fileName)) {
                Files.copy(in, configFile.toPath());
            }
        }

        configuration = YamlConfiguration.loadConfiguration(configFile);
    }

    public void reloadConfig() {
        configuration = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveConfig() {
        try {
            configuration.save(configFile);
        } catch (IOException e) {
            Utils.logSevere("An error occurred while saving the file " + fileName + ".");
        }
    }

    public File getFile() {
        return configFile;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
