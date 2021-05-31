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

package it.riteo.dailycrystal.eventlisteners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

import it.riteo.dailycrystal.configuration.NotificationSettings;
import it.riteo.dailycrystal.managers.ConfigurationManager;
import it.riteo.dailycrystal.managers.PlayerDataManager;
import it.riteo.dailycrystal.taskschedulers.FakeCrystalTaskScheduler;
import it.riteo.dailycrystal.taskschedulers.PlayerNotificationTaskScheduler;

/**
 * An event listener that decides when to show a new reward notification to a
 * player.
 */
public class PlayerNotificationEventListener implements Listener {
	private ConfigurationManager configurationManager;
	private PlayerDataManager playerDataManager;
	private FakeCrystalTaskScheduler fakeCrystalTaskScheduler;
	private PlayerNotificationTaskScheduler playerNotificationTaskScheduler;

	public PlayerNotificationEventListener(ConfigurationManager configurationManager,
			PlayerDataManager playerDataManager, FakeCrystalTaskScheduler fakeCrystalTaskScheduler,
			PlayerNotificationTaskScheduler playerNotificationTaskScheduler) {
		this.playerDataManager = playerDataManager;
		this.configurationManager = configurationManager;
		this.fakeCrystalTaskScheduler = fakeCrystalTaskScheduler;
		this.playerNotificationTaskScheduler = playerNotificationTaskScheduler;
	}

	@EventHandler
	public void playerJoined(PlayerJoinEvent event) {
		if (configurationManager.getNotificationSettings()
				.getMode() != NotificationSettings.NotificationTrigger.ON_JOIN) {
			return;
		}

		Player player = event.getPlayer();

		scheduleNotificationWithPreference(player);
	}

	@EventHandler
	public void resourcePackLoaded(PlayerResourcePackStatusEvent event) {
		if (configurationManager.getNotificationSettings()
				.getMode() != NotificationSettings.NotificationTrigger.ON_RESOURCE_PACK_LOAD) {
			return;
		}

		if (event.getStatus() != Status.ACCEPTED) {
			scheduleNotificationWithPreference(event.getPlayer());
		}
	}

	public void playerQuitted(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		playerNotificationTaskScheduler.unschedulePlayerNotification(player);
	}

	private void scheduleNotificationWithPreference(Player player) {
		if (playerDataManager.getPlayerNotificationPreference(player)) {
			if (fakeCrystalTaskScheduler.getPlayerRemainingMillis(player) <= 0) {
				playerNotificationTaskScheduler.schedulePlayerNotification(player);
			}
		}
	}

}
