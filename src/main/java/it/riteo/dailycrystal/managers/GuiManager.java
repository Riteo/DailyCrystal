package it.riteo.dailycrystal.managers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/**
 * A class responsible for creating fake inventories used as a GUI.
 */
public class GuiManager implements Listener {
	private ConfigurationManager configurationManager;
	private PlayerDataManager playerDataManager;
	private Logger logger;

	private Map<HumanEntity, InventoryView> playerOpenWindowMap = null;

	/**
	 * Creates a new instance of a {@link GuiManager}-
	 *
	 * @param configurationManager - A {@link ConfigurationManager}, used to fetch
	 *                             the rewards.
	 * @param playerDataManager    - A {@link PlayerDataManager}, used to fetch the
	 *                             player's streak.
	 * @param logger               - A {@link Logger} with which to log any
	 *                             exception. Can be null.
	 */
	public GuiManager(ConfigurationManager configurationManager, PlayerDataManager playerDataManager, Logger logger) {
		playerOpenWindowMap = new LinkedHashMap<HumanEntity, InventoryView>();

		this.configurationManager = configurationManager;
		this.playerDataManager = playerDataManager;
		this.logger = logger;
	}

	public void openPlayerRewardGui(Player player) {
		int playerStreak = playerDataManager.getPlayerRewardStreak(player);

		String windowTitle = configurationManager.getRewardSettings().getWindowTitle();
		windowTitle = windowTitle.replace("%s", String.valueOf(playerStreak + 1));

		Inventory guiInventory = Bukkit.createInventory(player, InventoryType.HOPPER, windowTitle);

		List<ItemStack> rewardItemsList = configurationManager.getStreakItems(playerStreak);

		/*
		 * TODO: Maybe fill the player's inventory with the missing items instead of
		 * just discarding them?
		 */
		if (rewardItemsList.size() > guiInventory.getSize()) {
			if (logger != null) {
				logger.log(Level.WARNING, "The amount of reward items specified in the config file exceeds the"
						+ "amount of free slots in the reward window! Some items might not be given to the player.");
			}
		}

		for (ItemStack rewardItem : rewardItemsList) {
			guiInventory.addItem(rewardItem);
		}

		InventoryView playerInventoryView = player.openInventory(guiInventory);
		playerOpenWindowMap.put(player, playerInventoryView);
	}

	@EventHandler
	public void onCloseInventory(InventoryCloseEvent event) {
		HumanEntity playerHumanEntity = event.getPlayer();

		/* Check if there's a reward GUI open for the player */
		if (playerOpenWindowMap.containsKey(playerHumanEntity)) {
			InventoryView playerGuiInventoryView = playerOpenWindowMap.get(playerHumanEntity);

			if (event.getView().equals(playerGuiInventoryView)) {

				World playerHumanEntityWorld = playerHumanEntity.getWorld();

				ItemStack[] playerGuiInventoryViewContents = playerGuiInventoryView.getTopInventory()
						.getStorageContents();

				for (ItemStack oggetto : playerGuiInventoryViewContents) {
					if (oggetto != null) {
						playerHumanEntityWorld.dropItem(playerHumanEntity.getLocation(), oggetto);
					}
				}

				playerOpenWindowMap.remove(playerHumanEntity);
			}
		}
	}
}
