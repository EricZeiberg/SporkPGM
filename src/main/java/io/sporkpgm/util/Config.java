package io.sporkpgm.util;

import io.sporkpgm.Spork;
import io.sporkpgm.Spork.StartupType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {

	private static File configFile;
	private static FileConfiguration config;

	public static void init() {
		configFile = new File(Spork.get().getDataFolder(), "config.yml");
		if(!configFile.exists())
			Spork.get().saveResource("config.yml", true);

		config = YamlConfiguration.loadConfiguration(configFile);
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(String path) {
		return (T) config.get(path);
	}

	@SuppressWarnings("unchecked")
	private static <T> T get(String path, Object def) {
		return (T) config.get(path, def);
	}

	public static void set(String path, Object value) {
		config.set(path, value);
		try {
			config.save(configFile);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static class General {

		public static final boolean DEBUG = get("settings.debug", false);

	}

	public static class Map {

		public static final File DIRECTORY = new File((String) get("settings.maps.repository", "maps/"));
		public static final StartupType STARTUP = StartupType.getType((String) get("settings.maps.startup", "all"));
		
		public static File getDirectory() {
			if(!DIRECTORY.exists()) {
				DIRECTORY.mkdirs();
			}

			return DIRECTORY;
		}

	}

	public static class Rotation {

		public static final File ROTATION = new File((String) get("settings.rotation.file", "rotation.txt"));

	}

	private static class Match {

		public static final String PREFIX = get("settings.match.prefix", "match-");

	}

}
