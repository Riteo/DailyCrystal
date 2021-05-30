package it.riteo.dailycrystal.packetadapters;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import it.riteo.dailycrystal.managers.ConfigurationManager;
import it.riteo.dailycrystal.managers.FakeCrystalManager;
import it.riteo.dailycrystal.utilities.TimeUtilities;
import taskschedulers.FakeCrystalTaskScheduler;

/**
 * A packet adapter which, by intercepting certain packets, tries to mimick the
 * vanilla entity tracker by loading and unloading the crystal depending on the
 * loaded chunk. This is needed as we can't use the server's entity tracker,
 * since it would allow others to see all crystals even with their reward taken.
 */
public class CrystalLoadingPacketAdapter extends PacketAdapter {
	private ConfigurationManager configurationManager;
	private FakeCrystalTaskScheduler fakeCrystalTaskScheduler;
	private FakeCrystalManager fakeCrystalManager;
	private Logger logger;

	/**
	 * Creates a new instance of a {@link CrystalLoadingPacketAdapter}.
	 *
	 * @param plugin                   - The {@link Plugin} to which attach this
	 *                                 packet adapter.
	 * @param configurationManager     - A {@link ConfigurationManager} used for
	 *                                 fetching the crystal location.
	 * @param fakeCrystalTaskScheduler - A {@link FakeCrystalTaskScheduler} used for
	 *                                 scheduling a crystal generation on chunk
	 *                                 load.
	 * @param fakeCrystalManager       - A {@link FakeCrystalManager} used for
	 *                                 creating the crystal on chunk load.
	 * @param logger                   - A {@link Logger} with which to log any
	 *                                 exception. It can be null.
	 * @param priority                 - The priority of this packet adapter.
	 */
	public CrystalLoadingPacketAdapter(Plugin plugin, ConfigurationManager configurationManager,
			FakeCrystalTaskScheduler fakeCrystalTaskScheduler, FakeCrystalManager fakeCrystalManager, Logger logger,
			ListenerPriority priority) {
		super(plugin, priority, PacketType.Play.Server.MAP_CHUNK, PacketType.Play.Server.UNLOAD_CHUNK);

		this.configurationManager = configurationManager;
		this.fakeCrystalTaskScheduler = fakeCrystalTaskScheduler;
		this.fakeCrystalManager = fakeCrystalManager;
		this.logger = logger;
	}

	@Override
	public void onPacketSending(PacketEvent event) {
		if (event.getPacketType() == PacketType.Play.Server.MAP_CHUNK) {
			onChunkLoad(event);
		} else if (event.getPacketType() == PacketType.Play.Server.UNLOAD_CHUNK) {
			onChunkUnload(event);
		}
	}

	public void onChunkLoad(PacketEvent event) {
		Player player = event.getPlayer();
		Location crystalLocation = configurationManager.getCrystalSettings().getLocation();

		if (player.getWorld().getName().equals(crystalLocation.getWorld().getName())) {
			/* Reading the packet data */
			PacketContainer packet = event.getPacket();
			int xChunkPos = packet.getIntegers().read(0);
			int zChunkPos = packet.getIntegers().read(1);

			if (player.getWorld().getName().equals(crystalLocation.getWorld().getName())
					&& xChunkPos == crystalLocation.getChunk().getX()
					&& zChunkPos == crystalLocation.getChunk().getZ()) {

				if (!fakeCrystalTaskScheduler.isPlayerCrystalSpawnPlanned(player)) {
					fakeCrystalTaskScheduler.schedulePlayerCrystalSpawn(player,
							TimeUtilities.millisToTicks(fakeCrystalTaskScheduler.getPlayerRemainingMillis(player)));
				}
			}
		}
	}

	public void onChunkUnload(PacketEvent event) {
		Player player = event.getPlayer();
		Location crystalLocation = configurationManager.getCrystalSettings().getLocation();

		if (fakeCrystalManager.hasPlayerLocalCrystal(player)) {

			/* Reading the packet data */
			PacketContainer packet = event.getPacket();
			int xChunkPos = packet.getIntegers().read(0);
			int zChunkPos = packet.getIntegers().read(1);

			if (player.getWorld().getName().equals(crystalLocation.getWorld().getName())
					&& xChunkPos == crystalLocation.getChunk().getX()
					&& zChunkPos == crystalLocation.getChunk().getZ()) {

				try {
					fakeCrystalManager.destroyPlayerCrystal(player);
				} catch (InvocationTargetException | NullPointerException exception) {
					if (logger != null) {
						logger.log(Level.SEVERE,
								"Exception thrown while destroying " + player.getName() + "'s crystal.", exception);
					}
				}

				if (fakeCrystalTaskScheduler.isPlayerCrystalSpawnPlanned(player)) {
					fakeCrystalTaskScheduler.cancelPlayerCrystalSpawn(player);
				}
			}
		}
	}
}