package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.objects.PlayerLocale;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigManager {
    private final PunishManager plugin;
    private final ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    private Configuration config;
    private Map<Locale, Configuration> locales;
    private Map<String, String> embeds;
    private java.util.Locale defaultLocale;
    private List<String> exemptPlayers;

    public ConfigManager(PunishManager plugin) {
        this.plugin = plugin;
        setup();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private Configuration loadConfigFile(File file) {
        try {
            if (!plugin.getDataFolder().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists() && !file.isDirectory()) {
                Files.copy(plugin.getResourceAsStream(file.getName()), file.toPath());
                return provider.load(file);
            }
            return provider.load(file);
        } catch (IOException ex) {
            plugin.getLogger().severe(String.format("Error while trying to load file {0}: " + ex.getMessage(), file.getName()));
        }
        return null;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void loadFile(File file) {
        try {
            if (!plugin.getDataFolder().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists() && !file.isDirectory()) {
                Files.copy(plugin.getResourceAsStream(file.getName()), file.toPath());
            }
        } catch (IOException ex) {
            plugin.getLogger().severe(String.format("Error while trying to load file {0}: " + ex.getMessage(), file.getName()));
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void loadFolder(File file) {
        file.mkdirs();
    }

    public Map<Locale, Configuration> getLocales() {
        Map<Locale, Configuration> locales = new HashMap<>();
        for (File file : getLocaleFiles()) {
            Locale locale = Utils.stringToLocale(file.getName().split("\\.")[0]);
            locales.put(locale, new Configuration(loadConfigFile(file)));
        }
        plugin.getLogger().info("Found " + locales.size() + " language files.");
        return locales;
    }

    public List<File> getLocaleFiles() {
        List<File> files = new ArrayList<>();
        File directoryPath = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "locales");
        FilenameFilter ymlFilter = (dir, name) -> {
            String lowercaseName = name.toLowerCase();
            return lowercaseName.endsWith(".yml");
        };
        File[] filesList = directoryPath.listFiles(ymlFilter);
        Objects.requireNonNull(filesList, "Locales folder not found!");
        Collections.addAll(files, filesList);
        return files;
    }

    public List<Locale> getAvailableLocales() {
        List<Locale> locales = new ArrayList<>();
        for (Map.Entry<Locale, Configuration> locale : this.locales.entrySet()){
            locales.add(locale.getKey());
        }
        return locales;
    }

    public List<String> getLayout(String path, String playerName) {
        Locale locale = new PlayerLocale(playerName).getLocale();
        if (locales.containsKey(locale)) {
            List<String> messages = locales.get(locale).getStringList(path);
            if (messages.size() != 0) {
                return locales.get(locale).getStringList(path).stream().map(Utils::color).collect(Collectors.toList());
            } else {
                return locales.get(defaultLocale).getStringList(path).stream().map(Utils::color).collect(Collectors.toList());
            }
        } else {
            return locales.get(defaultLocale).getStringList(path).stream().map(Utils::color).collect(Collectors.toList());
        }
    }

    public String getMessage(String path, String playerName) {
        Locale locale = new PlayerLocale(playerName).getLocale();
        if (locales.containsKey(locale)) {
            String msg = locales.get(locale).getString(path);
            if (msg != null && msg.length() != 0) {
                return Utils.color(locales.get(locale).getString(path));
            } else {
                return Utils.color(locales.get(defaultLocale).getString(path));
            }
        } else {
            return Utils.color(locales.get(defaultLocale).getString(path));
        }
    }

    public String getMessage(String path) {
        if (locales.containsKey(defaultLocale)) {
            String msg = locales.get(defaultLocale).getString(path);
            if (msg != null && msg.length() != 0) {
                return Utils.color(locales.get(defaultLocale).getString(path));
            }
        }
        plugin.getLogger().warning("The searched value was not found in the language file and the default language file: " + path);
        return null;
    }

    public Configuration getConfig() {
        return config;
    }

    public void setup() {
        config = loadConfigFile(new File(plugin.getDataFolder() + File.separator + "config.yml"));
        loadFolder(new File(plugin.getDataFolder() + File.separator + "locales"));
        loadFolder(new File(plugin.getDataFolder() + File.separator + "embeds"));
        loadFile(new File(plugin.getDataFolder() + File.separator + "embeds" + File.separator + "ban.json"));
        loadFile(new File(plugin.getDataFolder() + File.separator + "embeds" + File.separator + "mute.json"));
        loadFile(new File(plugin.getDataFolder() + File.separator + "embeds" + File.separator + "tempban.json"));
        loadFile(new File(plugin.getDataFolder() + File.separator + "embeds" + File.separator + "tempmute.json"));
        loadFile(new File(plugin.getDataFolder() + File.separator + "embeds" + File.separator + "ipban.json"));
        loadFile(new File(plugin.getDataFolder() + File.separator + "embeds" + File.separator + "kick.json"));
        loadFile(new File(plugin.getDataFolder() + File.separator + "locales" + File.separator + "en_US.yml"));
        loadFile(new File(plugin.getDataFolder() + File.separator + "locales" + File.separator + "tr_TR.yml"));
        this.locales = getLocales();
        this.embeds = getEmbeds();
        defaultLocale = Utils.stringToLocale(getConfig().getString("default-server-language"));
        this.exemptPlayers = getConfig().getStringList("exempt-players");
    }

    private Set<File> getEmbedFiles() {
        Set<File> files = new HashSet<>();
        File directoryPath = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "embeds");
        FilenameFilter jsonFilter = (dir, name) -> {
            String lowercaseName = name.toLowerCase();
            return lowercaseName.endsWith(".json");
        };
        File[] filesList = directoryPath.listFiles(jsonFilter);
        Objects.requireNonNull(filesList, "Embeds folder not found!");
        Collections.addAll(files, filesList);
        return files;
    }

    private Map<String, String> getEmbeds() {
        Map<String, String> embeds = new HashMap<>();
        for (File file : getEmbedFiles()) {
            try {
                embeds.put(file.getName().split("\\.")[0].toUpperCase(java.util.Locale.ENGLISH), fileToString(file, "UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return embeds;
    }

    public String fileToString(File file, String charset) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while (result != -1) {
            buf.write((byte) result);
            result = bis.read();
        }
        return buf.toString(charset);
    }

    public String getEmbed(String path) {
        return embeds.get(path);
    }

    public java.util.Locale getDefaultLocale() {
        return defaultLocale;
    }

    public List<String> getExemptPlayers() {
        return exemptPlayers;
    }
}
