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
