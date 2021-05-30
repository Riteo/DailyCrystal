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
