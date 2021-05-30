package taskschedulers;

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
