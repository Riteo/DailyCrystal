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
import org.bukkit.event.player.PlayerQuitEvent;

import it.riteo.dailycrystal.taskschedulers.FakeCrystalTaskScheduler;

/**
 * A {@link Listener} responsible for unloading the players' crystals.
 */
public class CrystalLoadingEventListener implements Listener {
	private FakeCrystalTaskScheduler fakeCrystalTaskScheduler;

	public CrystalLoadingEventListener(FakeCrystalTaskScheduler fakeCrystalTaskScheduler) {
		this.fakeCrystalTaskScheduler = fakeCrystalTaskScheduler;
	}

	@EventHandler
	public void playerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if (fakeCrystalTaskScheduler.isPlayerCrystalSpawnPlanned(player)) {
			fakeCrystalTaskScheduler.cancelPlayerCrystalSpawn(player);
		}
	}
}
