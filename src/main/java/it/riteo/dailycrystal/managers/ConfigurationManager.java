/*
 * DailyCrystal
 * Copyright (C) 2021  Riteo Siuga
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package it.riteo.dailycrystal.managers;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import it.riteo.dailycrystal.configuration.CrystalSettings;
import it.riteo.dailycrystal.configuration.NotificationSettings;
import it.riteo.dailycrystal.configuration.RewardSettings;
import it.riteo.dailycrystal.utilities.ConfigUtils;
import it.riteo.dailycrystal.configuration.NotificationSettings.NotificationTrigger;

/**
 * A manager responsible for parsing and caching the configuration file.
 */
public class ConfigurationManager {
	public static final String REWARD_SETTINGS_CONFIGURATION_PATH = "reward";
	public static final String REWARD_WINDOW_TITLE_CONFIGURATION_PATH = "windowTitle";
	public static final String REWARD_ITEMS_CONFIGURATION_PATH = "items";

	public static final String CRYSTAL_SETTINGS_CONFIGURATION_PATH = "crystal";
	public static final String CRYSTAL_LOCATION_CONFIGURATION_PATH = "location";

	public static final String NOTIFICATION_SETTINGS_CONFIGURATION_PATH = "notification";
	public static final String NOTIFICATION_ENABLED_CONFIGURATION_PATH = "enabled";
	public static final String NOTIFICATION_TRIGGER_CONFIGURATION_PATH = "trigger";
	public static final String NOTIFICATION_TITLE_CONFIGURATION_PATH = "title";
	public static final String NOTIFICATION_SUBTITLE_CONFIGURATION_PATH = "subtitle";

	private FileConfiguration serverFileConfiguration;
	private Logger logger;

	private RewardSettings rewardSettings;
	private CrystalSettings crystalSettings;
	private NotificationSettings notificationSettings;

	/**
	 * Creates a new instance of {@link ConfigurationManager}.
	 *
	 * @param serverFileConfiguration - The server's file configuration.
	 * @param logger                  - The logger used to log the manager
	 *                                exceptions (can be null)
	 */
	public ConfigurationManager(FileConfiguration serverFileConfiguration, Logger logger) {
		this.serverFileConfiguration = serverFileConfiguration;
		this.logger = logger;
	}

	public void updateConfig(FileConfiguration fileConfiguration) {
		this.serverFileConfiguration = fileConfiguration;
	}

	public void invalidateCache() {
		rewardSettings = null;
		crystalSettings = null;
		notificationSettings = null;
	}

	public RewardSettings getRewardSettings() {
		if (rewardSettings == null) {
			rewardSettings = parseRewardSettings(serverFileConfiguration);
		}

		return rewardSettings;
	}

	public CrystalSettings getCrystalSettings() {
		if (crystalSettings == null) {
			crystalSettings = parseCrystalSettings(serverFileConfiguration);
		}

		return crystalSettings;
	}

	public NotificationSettings getNotificationSettings() {
		if (notificationSettings == null) {
			notificationSettings = parseNotificationSettings(serverFileConfiguration);
		}

		return notificationSettings;
	}

	public List<ItemStack> getStreakItems(int targetStreak) {
		List<ItemStack> lastStreakReward = null;

		/*
		 * Note that we assume that this set is ordered. This isn't guaranteed from the
		 * Set interface, but since this comes from a SortedMap, this should be fine, as
		 * probably the implementation comes from a LinkedList or whatever.
		 */
		for (Map.Entry<Integer, List<ItemStack>> streakRewardEntry : rewardSettings.getRewardStreakItems().entrySet()) {
			int currentStreak = streakRewardEntry.getKey().intValue();
			List<ItemStack> currentStreakReward = streakRewardEntry.getValue();

			if (currentStreak > targetStreak) {
				/*
				 * Since this reward's streak is greater than the target streak, the previous
				 * one must be what we want.
				 */
				if (lastStreakReward != null) {
					return lastStreakReward;
				} else {
					/*
					 * If this is the first reward, then there's no last one, so we'll just return
					 * an empty list.
					 */
					return new LinkedList<ItemStack>();
				}
			}

			lastStreakReward = currentStreakReward;
		}

		/*
		 * If there were no rewards with a streak greater than the target, we'll return
		 * the last one.
		 */
		if (lastStreakReward != null) {
			return lastStreakReward;
		} else {
			/* If this was an empty map, we'll just return an empty list. */
			return new LinkedList<ItemStack>();
		}
	}

	/* Reward stuff */
	private RewardSettings parseRewardSettings(FileConfiguration fileConfiguration) {
		ConfigurationSection rewardSettingsConfigurationSection = serverFileConfiguration
				.getConfigurationSection(REWARD_SETTINGS_CONFIGURATION_PATH);

		String windowTitle;
		SortedMap<Integer, List<ItemStack>> rewardItems;

		windowTitle = rewardSettingsConfigurationSection.getString(REWARD_WINDOW_TITLE_CONFIGURATION_PATH, "");
		rewardItems = parseRewardItemsMap(rewardSettingsConfigurationSection);

		return new RewardSettings(windowTitle, rewardItems);
	}

	private SortedMap<Integer, List<ItemStack>> parseRewardItemsMap(ConfigurationSection configurationSection) {
		SortedMap<Integer, List<ItemStack>> rewardItemsMap = new TreeMap<Integer, List<ItemStack>>();

		if (configurationSection != null) {
			Map<String, Object> rewardItemsConfigMap = configurationSection
					.getConfigurationSection(REWARD_ITEMS_CONFIGURATION_PATH).getValues(false);

			for (Map.Entry<String, Object> streakItemListEntry : rewardItemsConfigMap.entrySet()) {
				Integer streakNumber;
				List<ItemStack> streakRewards = new LinkedList<ItemStack>();

				/* Streak parsing */
				try {
					streakNumber = Integer.valueOf(streakItemListEntry.getKey());
				} catch (NumberFormatException exception) {
					continue;
				}

				/* Reward casting and validation */
				if (streakItemListEntry.getValue() instanceof Collection<?>) {
					for (Object uncastedItem : (Collection<?>) streakItemListEntry.getValue()) {
						if (uncastedItem instanceof ItemStack) {
							streakRewards.add((ItemStack) uncastedItem);
						}
					}
				}

				rewardItemsMap.put(streakNumber, streakRewards);
			}
		}

		return rewardItemsMap;
	}

	/* Crystal stuff */
	private CrystalSettings parseCrystalSettings(FileConfiguration fileConfiguration) {
		ConfigurationSection crystalSettingsConfigurationSection = fileConfiguration
				.getConfigurationSection(CRYSTAL_SETTINGS_CONFIGURATION_PATH);

		ConfigurationSection crystalLocationConfigurationSection = crystalSettingsConfigurationSection
				.getConfigurationSection(CRYSTAL_LOCATION_CONFIGURATION_PATH);

		Location location = null;

		/*
		 * This mess is the cleanliest way I found to avoid an enourmous exception in
		 * the console. Yes, it's still shit but it's cleaner than my older approach.
		 */
		location = ConfigUtils.deserializeLocation(crystalLocationConfigurationSection.getValues(false));

		if (location == null) {
			location = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);

			if (logger != null) {
				logger.log(Level.WARNING,
						"Invalid crystal location specified! Placing the crystal at 0,0,0 in the first world.");
			}
		}

		return new CrystalSettings(location);
	}

	/* Notification stuff */
	private NotificationSettings parseNotificationSettings(FileConfiguration fileConfiguration) {
		ConfigurationSection notificationSettingsConfigurationSection = fileConfiguration
				.getConfigurationSection(NOTIFICATION_SETTINGS_CONFIGURATION_PATH);

		boolean enabled = false;
		String title = "";
		String subtitle = "";
		NotificationTrigger trigger = NotificationTrigger.ON_JOIN;

		enabled = notificationSettingsConfigurationSection.getBoolean(NOTIFICATION_ENABLED_CONFIGURATION_PATH);
		title = notificationSettingsConfigurationSection.getString(NOTIFICATION_TITLE_CONFIGURATION_PATH);
		subtitle = notificationSettingsConfigurationSection.getString(NOTIFICATION_SUBTITLE_CONFIGURATION_PATH);

		try {
			trigger = NotificationTrigger.valueOf(
					notificationSettingsConfigurationSection.getString(NOTIFICATION_TRIGGER_CONFIGURATION_PATH));
		} catch (IllegalArgumentException | NullPointerException exception) {
			if (logger != null) {
				logger.log(Level.WARNING, "Missing or invalid notification trigger: using default value of 'ON_JOIN'.");
			}

		}

		return new NotificationSettings(enabled, title, subtitle, trigger);
	}
}
