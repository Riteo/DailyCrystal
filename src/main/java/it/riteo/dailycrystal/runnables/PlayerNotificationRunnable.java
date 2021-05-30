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
