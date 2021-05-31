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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import it.riteo.dailycrystal.managers.ConfigurationManager;
import it.riteo.dailycrystal.managers.FakeCrystalManager;
import it.riteo.dailycrystal.managers.PlayerDataManager;
import it.riteo.dailycrystal.runnables.CrystalSpawnRunnable;
import it.riteo.dailycrystal.utilities.TimeUtilities;

/**
 * A class which schedules the spawn of fake crystals.
 */
public class FakeCrystalTaskScheduler {
	private Plugin plugin;
	private FakeCrystalManager fakeCrystalManager;
	private ConfigurationManager configurationManager;
	private PlayerDataManager playerDataManager;
	private Logger logger;
	private Map<Player, BukkitTask> playerSpawnTaskMap;

	public FakeCrystalTaskScheduler(Plugin plugin, FakeCrystalManager fakeCrystalManager,
			ConfigurationManager configurationManager, PlayerDataManager playerDataManager, Logger logger) {
		this.plugin = plugin;
		this.fakeCrystalManager = fakeCrystalManager;
		this.configurationManager = configurationManager;
		this.playerDataManager = playerDataManager;
		this.logger = logger;

		playerSpawnTaskMap = new LinkedHashMap<Player, BukkitTask>();
	}

	/**
	 * Schedules a crystal "spawn" local only to the specified player for the next
	 * time they unlock a crystal.
	 *
	 * @param player    - the player to whom send the fake crystal.
	 * @param tickDelay - how much time to wait before actually spawing the crystal.
	 */
	public void schedulePlayerCrystalSpawn(Player player) {
		schedulePlayerCrystalSpawn(player, TimeUtilities.millisToTicks(getPlayerRemainingMillis(player)));
	}

	/**
	 * Schedules a crystal "spawn" local only to the specified player after the
	 * specified delay.
	 *
	 * @param player    - the player to whom send the fake crystal.
	 * @param tickDelay - how much time to wait before actually spawing the crystal.
	 */
	public void schedulePlayerCrystalSpawn(Player player, long tickDelay) {
		Location crystalLocation = configurationManager.getCrystalSettings().getLocation();

		BukkitTask crystalSpawnTask = new CrystalSpawnRunnable(player, fakeCrystalManager, crystalLocation.getX(),
				crystalLocation.getY(), crystalLocation.getZ(), logger).runTaskLater(plugin, tickDelay);

		playerSpawnTaskMap.put(player, crystalSpawnTask);
	}

	/**
	 * Cancels the crystal spawn task, if any.
	 *
	 * @param player - the player to whom cancel the spawing task.
	 */
	public void cancelPlayerCrystalSpawn(Player player) {
		if (playerSpawnTaskMap.containsKey(player)) {
			playerSpawnTaskMap.get(player).cancel();
			playerSpawnTaskMap.remove(player);
		}
	}

	public void cancelAllCrystalSpawns() {
		for (BukkitTask task : playerSpawnTaskMap.values()) {
			task.cancel();
		}
	}

	/**
	 * Chekcks whether a crystal spawn has been planned for the player.
	 *
	 * @param player - the player to check for any planned task.
	 * @return <code>true</code> if there's a task planned for this player,
	 *         <code>false</code> otherwise.
	 */
	public boolean isPlayerCrystalSpawnPlanned(Player player) {
		return playerSpawnTaskMap.containsKey(player) && !playerSpawnTaskMap.get(player).isCancelled();
	}

	/**
	 * Calculates how much time is left in milliseconds for generating a crystal.
	 *
	 * This accounts for the system's timezone
	 *
	 * @param player
	 * @return
	 */

	public long getPlayerRemainingMillis(Player player) {
		long currentTimeMillis = System.currentTimeMillis();

		long currentTimeMillisOffset = TimeZone.getDefault().getOffset(currentTimeMillis);

		long playerLastInteractionMidnightMillis = TimeUtilities
				.getMidnightMillis(playerDataManager.getPlayerLastInteraction(player), currentTimeMillisOffset);

		return playerLastInteractionMidnightMillis + TimeUtilities.MILLISECONDS_IN_A_DAY - currentTimeMillis;
	}
}
