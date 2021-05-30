package it.riteo.dailycrystal.eventlisteners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import taskschedulers.FakeCrystalTaskScheduler;

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
