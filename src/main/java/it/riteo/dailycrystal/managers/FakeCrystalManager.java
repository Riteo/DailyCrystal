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

package it.riteo.dailycrystal.managers;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import it.riteo.dailycrystal.utilities.EntityUtilities;

/**
 * A class which creates, destroys and registers fake crystals by sending ad hoc
 * packets only to specific players.
 */
public class FakeCrystalManager {
	/*
	 * This is a map of the id of each crystal every player sees. We have to track
	 * these since the game has no idea about them. This is needed to interact and
	 * destroy them when needed.
	 */
	private Map<Player, Integer> playerCrystalIdMap;
	private Map<Integer, Location> crystalIdLocationMap;

	private Logger logger;

	private static final int SHOW_CRYSTAL_BASE_INTEGER_ID = 8;

	/**
	 * Creates a new instance of {@link FakeCrystalManager}.
	 *
	 * @param logger - A {@link Logger} in which to log any exceptions. It can be
	 *               null.
	 */
	public FakeCrystalManager(Logger logger) {
		playerCrystalIdMap = new LinkedHashMap<Player, Integer>();
		crystalIdLocationMap = new LinkedHashMap<Integer, Location>();

		this.logger = logger;
	}

	/**
	 * Shows a fake crystal only to a single player rat the specified location.
	 *
	 * @param player - The player to whom spawn a fake crystal which they can only
	 *               see.
	 * @param xPos   - The x component of the location in which to spawn the fake
	 *               crystal.
	 * @param yPos   - The y component of the location in which to spawn the fake
	 *               crystal.
	 * @param zPos   - The z component of the location in which to spawn the fake
	 *               crystal.
	 * @throws InvocationTargetException if there an error occurs while sending the
	 *                                   fake crystal spawn packet to the player.
	 */
	public void spawnFakeCrystal(Player player, double xPos, double yPos, double zPos)
			throws InvocationTargetException {

		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

		/* Setup of a spawn packet for a new fake crystal */
		PacketContainer fakeCrystalSpawnPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);

		int crystalId;
		try {
			/*
			 * This is needed in order to avoid entity ID collisions in case a new entity
			 * with such ID might ever get spawned. That might confuse the client. I don't
			 * remember whether I confirmed this or not, but better safe than sorry.
			 */
			crystalId = EntityUtilities.fetchNewEntityId();
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException
				| IllegalAccessException exception) {
			if (logger != null) {
				logger.log(Level.SEVERE,
						"Exception thrown while fetching a new id for" + player.getName() + "'s crystal.");
			}

			return;
		}

		/* Entity ID */
		fakeCrystalSpawnPacket.getIntegers().write(0, crystalId);

		/* UUID */
		fakeCrystalSpawnPacket.getUUIDs().write(0, UUID.randomUUID());

		/* Entity type */
		fakeCrystalSpawnPacket.getEntityTypeModifier().write(0, EntityType.ENDER_CRYSTAL);

		/* Location */
		fakeCrystalSpawnPacket.getDoubles().write(0, xPos);
		fakeCrystalSpawnPacket.getDoubles().write(1, yPos);
		fakeCrystalSpawnPacket.getDoubles().write(2, zPos);

		/* Metadata packet creation */
		PacketContainer fakeCrystalMetadataPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
		fakeCrystalMetadataPacket.getIntegers().write(0, crystalId);

		/* Creating the metadata packet needed to hide the base */
		WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
		WrappedDataWatcher.WrappedDataWatcherObject dataWatcherObject = new WrappedDataWatcher.WrappedDataWatcherObject(
				SHOW_CRYSTAL_BASE_INTEGER_ID, WrappedDataWatcher.Registry.get(Boolean.class));

		/* Setting up the metadata packet */
		dataWatcher.setObject(dataWatcherObject, false);
		fakeCrystalMetadataPacket.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());

		protocolManager.sendServerPacket(player, fakeCrystalSpawnPacket);
		protocolManager.sendServerPacket(player, fakeCrystalMetadataPacket);

		/* Registering our new crystal */
		playerCrystalIdMap.put(player, crystalId);
		crystalIdLocationMap.put(crystalId, new Location(player.getWorld(), xPos, yPos, zPos));
	}

	/**
	 * Destroys the crystal registered to the speficied player.
	 *
	 * @param player - the player to whom destroy the crystal, if it has any.
	 * @throws InvocationTargetException if there's any exception coming from the
	 *                                   protocol manager.
	 * @throws NullPointerException      if the specified player doesn't have any
	 *                                   crystal registered. Use
	 *                                   {@link #hasPlayerLocalCrystal(Player)} to
	 *                                   check if there's any.
	 */
	public void destroyPlayerCrystal(Player player) throws InvocationTargetException {
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

		int crystalId = playerCrystalIdMap.get(player);

		/* Setup of an entity destroy packet */
		PacketContainer crystalDestroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);

		/* The `write` method requires an array of IDs */
		crystalDestroyPacket.getIntegerArrays().write(0, new int[] { crystalId });

		playerCrystalIdMap.remove(player);
		crystalIdLocationMap.remove(crystalId);

		protocolManager.sendServerPacket(player, crystalDestroyPacket);
	}

	public void destroyAllCrystals() throws InvocationTargetException, NullPointerException {
		for (Player player : playerCrystalIdMap.keySet()) {
			if (hasPlayerLocalCrystal(player)) {
				destroyPlayerCrystal(player);
			}
		}
	}

	/**
	 * Gets the location of a fake crystal specified by an ID.
	 *
	 * @param id - the ID of the crystal to query
	 * @return the location of the crystal with the ID specified
	 */
	public Location getCrystalLocation(Integer id) {
		return crystalIdLocationMap.get(id);
	}

	/**
	 * Gets the ID of the crystal spawned to a player.
	 *
	 * @param player - the player to query.
	 * @return the ID of the player's local crystal.
	 *
	 * @throws NullPointerException if the player doesn't have a local crystal.
	 *                              Check with
	 *                              {@link #hasPlayerLocalCrystal(Player)} if any
	 *                              exists before calling this.
	 */
	public Integer getPlayerCrystalId(Player player) throws NullPointerException {
		return playerCrystalIdMap.get(player);
	}

	/**
	 * Checks if the specified player has any crystal, without checking for its ID.
	 *
	 * @param player - the player to query.
	 * @return <code>true</code> if the player has a local crystal,
	 *         <code>false</code> otherwise.
	 */
	public boolean hasPlayerLocalCrystal(Player player) {
		return playerCrystalIdMap.containsKey(player);
	}

	/**
	 * Checks if the specified crystal id is local to the player.
	 *
	 * @param crystalId - the ID of the crystal to check.
	 * @param player    - the player to check for ownership of the crystal.
	 * @return <code>true</code> if the specified crystal is local to the player,
	 *         <code>false</code> otherwise.
	 */
	public boolean isCrystalOfPlayer(int crystalId, Player player) {
		return playerCrystalIdMap.get(player) == crystalId;
	}
}
