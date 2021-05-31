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

import it.riteo.dailycrystal.managers.CrystalInteractionManager;

/**
 * A {@link Runnable} which makes a player interact with a crystal crystal.
 */
public class CrystalInteractionRunnable extends BukkitRunnable {
	private CrystalInteractionManager crystalInteractionManager;
	private Player player;

	public CrystalInteractionRunnable(CrystalInteractionManager crystalInteractionManager, Player player) {
		this.crystalInteractionManager = crystalInteractionManager;
		this.player = player;
	}

	@Override
	public void run() {
		crystalInteractionManager.interactPlayerCrystal(player);
	}
}
