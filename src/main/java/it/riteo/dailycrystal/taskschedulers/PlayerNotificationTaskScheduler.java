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

package it.riteo.dailycrystal.taskschedulers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import it.riteo.dailycrystal.configuration.NotificationSettings;
import it.riteo.dailycrystal.managers.ConfigurationManager;
import it.riteo.dailycrystal.runnables.PlayerNotificationRunnable;

public class PlayerNotificationTaskScheduler {
	private Plugin plugin;
	private ConfigurationManager configurationManager;
	private FakeCrystalTaskScheduler fakeCrystalTaskScheduler;

	private Map<Player, BukkitTask> playerNotificationTaskMap;

	public PlayerNotificationTaskScheduler(Plugin plugin, ConfigurationManager configurationManager,
			FakeCrystalTaskScheduler fakeCrystalTaskScheduler) {
		this.plugin = plugin;
		this.configurationManager = configurationManager;
		this.fakeCrystalTaskScheduler = fakeCrystalTaskScheduler;
		playerNotificationTaskMap = new HashMap<Player, BukkitTask>();
	}

	/**
	 * Schedules the notification of a new reward to the player
	 *
	 * @param player - the player to schedule the notification to.
	 */
	public void schedulePlayerNotification(Player player) {
		if (playerNotificationTaskMap.containsKey(player)) {
			playerNotificationTaskMap.get(player).cancel();
		}

		NotificationSettings notificationSettings = configurationManager.getNotificationSettings();
		BukkitTask notificationTask = new PlayerNotificationRunnable(player, notificationSettings.getTitle(),
				notificationSettings.getSubtitle(), fakeCrystalTaskScheduler).runTaskLater(plugin, 600);

		playerNotificationTaskMap.put(player, notificationTask);
	}

	public void unschedulePlayerNotification(Player player) {
		if (playerNotificationTaskMap.containsKey(player)) {
			playerNotificationTaskMap.get(player).cancel();
		}
	}

	public void unscheduleAllNotifications() {
		for (BukkitTask task : playerNotificationTaskMap.values()) {
			task.cancel();
		}
	}
}
