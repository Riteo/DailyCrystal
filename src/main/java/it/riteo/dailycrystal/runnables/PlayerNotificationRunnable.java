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

package it.riteo.dailycrystal.runnables;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import it.riteo.dailycrystal.taskschedulers.FakeCrystalTaskScheduler;

/**
 * A {@link Runnable} which shows a new reward notification to a specified
 * player.
 */
public class PlayerNotificationRunnable extends BukkitRunnable {

	private Player player;
	private String title;
	private String subtitle;
	private FakeCrystalTaskScheduler fakeCrystalTaskScheduler;

	public PlayerNotificationRunnable(Player player, String title, String subtitle,
			FakeCrystalTaskScheduler fakeCrystalTaskScheduler) {
		this.player = player;
		this.title = title;
		this.subtitle = subtitle;
		this.fakeCrystalTaskScheduler = fakeCrystalTaskScheduler;
	}

	@Override
	public void run() {
		/*
		 * TODO: Consider whether to remove this hacky solution in favor on something
		 * simpler instead on depending on the whole fake crystal task scheduler.
		 */
		if (fakeCrystalTaskScheduler.getPlayerRemainingMillis(player) > 0) {
			this.cancel();
			return;
		}

		player.sendTitle(title, subtitle, -1, -1, -1);
		cancel();
	}
}
