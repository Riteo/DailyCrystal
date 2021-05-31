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

package it.riteo.dailycrystal.configuration;

import java.util.List;
import java.util.SortedMap;

import org.bukkit.inventory.ItemStack;

/**
 * A class representing the settings for the reward, such as its items
 */
public class RewardSettings {
	private String windowTitle;
	private SortedMap<Integer, List<ItemStack>> rewardStreakItems;

	public RewardSettings(String windowTitle, SortedMap<Integer, List<ItemStack>> rewardStreakItems) {
		this.windowTitle = windowTitle;
		this.rewardStreakItems = rewardStreakItems;
	}

	public String getWindowTitle() {
		return windowTitle;
	}

	public SortedMap<Integer, List<ItemStack>> getRewardStreakItems() {
		return rewardStreakItems;
	}
}
