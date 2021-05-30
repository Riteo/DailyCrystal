package it.riteo.dailycrystal.managers;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * A class responsible for creating and parsing the players data file.
 */
public class PlayerDataManager {
	public static final String PLAYER_DATA_CONFIGURATION_PATH = "player";
	public static final String PLAYER_LAST_INTERACTION_CONFIGURATION_PATH = "%s.lastInteraction";
	public static final String PLAYER_REWARD_STREAK_CONFIGURATION_PATH = "%s.rewardStreak";
	public static final String PLAYER_NOTIFICATION_PREFERENCE_CONFIGURATION_PATH = "%s.rewardNotificationPreference";

	private File playerDataFile;
	private Logger logger;

	private FileConfiguration playerDataFileConfiguration;
	private ConfigurationSection playerDataConfiguractionSection;

	/**
	 * Creates a new instance of a {@link PlayerDataManager}.
	 *
	 * @param playerDataFile - A {@link File} pointing to the player data file.
	 * @param logger         - A {@link Logger} with which to log any exception. It
	 *                       can be null.
	 */
	public PlayerDataManager(File playerDataFile, Logger logger) {
		this.playerDataFile = playerDataFile;
		this.logger = logger;
	}

	public long getPlayerLastInteraction(Player player) {
		String configPath = String.format(PLAYER_LAST_INTERACTION_CONFIGURATION_PATH, player.getUniqueId().toString());
		return playerDataConfiguractionSection.getLong(configPath);
	}

	public void setPlayerLastInteraction(Player player, long lastInteraction) {
		String configPath = String.format(PLAYER_LAST_INTERACTION_CONFIGURATION_PATH, player.getUniqueId().toString());
		playerDataConfiguractionSection.set(configPath, lastInteraction);
	}

	public int getPlayerRewardStreak(Player player) {
		String configPath = String.format(PLAYER_REWARD_STREAK_CONFIGURATION_PATH, player.getUniqueId().toString());
		return playerDataConfiguractionSection.getInt(configPath);
	}

	public void setPLayerRewardStreak(Player player, int rewardStreak) {
		String configPath = String.format(PLAYER_REWARD_STREAK_CONFIGURATION_PATH, player.getUniqueId().toString());
		playerDataConfiguractionSection.set(configPath, rewardStreak);
	}

	public boolean getPlayerNotificationPreference(Player player) {
		String configPath = String.format(PLAYER_NOTIFICATION_PREFERENCE_CONFIGURATION_PATH,
				player.getUniqueId().toString());

		return playerDataConfiguractionSection.getBoolean(configPath);
	}

	public void setPlayerNotificationPreference(Player player, boolean preference) {
		String configPath = String.format(PLAYER_NOTIFICATION_PREFERENCE_CONFIGURATION_PATH,
				player.getUniqueId().toString());

		playerDataConfiguractionSection.set(configPath, preference);
	}

	public void write() {
		if (playerDataFile == null || playerDataFileConfiguration == null) {
			return;
		}

		try {
			playerDataFileConfiguration.save(playerDataFile);
		} catch (IOException exception) {
			if (logger != null) {
				logger.log(Level.SEVERE, "Exception thrown wile writing the player data file.", exception);
			}
		}
	}

	public void reload() {
		if (!playerDataFile.exists()) {
			try {
				playerDataFile.getParentFile().mkdirs();
				playerDataFile.createNewFile();
			} catch (IOException exception) {
				if (logger != null) {
					logger.log(Level.SEVERE, "Exception thrown while creating the player data file.", exception);
				}
			}
		}

		playerDataFileConfiguration = YamlConfiguration.loadConfiguration(playerDataFile);
		playerDataConfiguractionSection = playerDataFileConfiguration
				.getConfigurationSection(PLAYER_DATA_CONFIGURATION_PATH);

		if (playerDataConfiguractionSection == null) {
			playerDataConfiguractionSection = playerDataFileConfiguration.createSection(PLAYER_DATA_CONFIGURATION_PATH);
		}
	}
}
