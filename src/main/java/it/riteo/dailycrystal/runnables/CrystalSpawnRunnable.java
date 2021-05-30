package it.riteo.dailycrystal.runnables;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import it.riteo.dailycrystal.managers.ConfigurationManager;
import it.riteo.dailycrystal.managers.FakeCrystalManager;

/**
 * A {@link Runnable} which spawns a crystal at the location specified in the
 * given {@link ConfigurationManager}.
 */
public class CrystalSpawnRunnable extends BukkitRunnable {
	private Player player;
	private FakeCrystalManager fakeCrystalManager;
	private double x;
	private double y;
	private double z;
	private Logger logger;

	/**
	 * Creates a new instance of a {@link CrystalSpawnRunnable}.
	 *
	 * @param player               - The {@link Player} to whitch show the crystal.
	 * @param fakeCrystalManager   - A {@link FakeCrystalManager} with which to
	 *                             generate the crystal.
	 * @param configurationManager - A {@link ConfigurationManager} with which to
	 *                             fetch the crystal location.
	 * @param logger               - A {@link Logger} with which to log any
	 *                             exception. It can be null.
	 */
	public CrystalSpawnRunnable(Player player, FakeCrystalManager fakeCrystalManager, double x, double y, double z,
			Logger logger) {
		this.player = player;
		this.fakeCrystalManager = fakeCrystalManager;

		this.x = x;
		this.y = y;
		this.z = z;

		this.logger = logger;
	}

	@Override
	public void run() {
		try {
			fakeCrystalManager.spawnFakeCrystal(player, x, y, z);
		} catch (InvocationTargetException exception) {
			if (logger != null) {
				logger.log(Level.SEVERE, "Exception thrown while spawning" + player.getUniqueId() + "'s daily crystal",
						exception);
			}
		}

		/* We want this to run only once */
		this.cancel();
	}
}
