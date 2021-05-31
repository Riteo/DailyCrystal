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
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;

import it.riteo.dailycrystal.taskschedulers.FakeCrystalTaskScheduler;
import it.riteo.dailycrystal.utilities.TimeUtilities;

/**
 * A class managing the interactions between players and their crystals.
 */
public class CrystalInteractionManager {
	private FakeCrystalManager fakeCrystalManager;
	private FakeCrystalTaskScheduler fakeCrystalTaskScheduler;
	private PlayerDataManager playerDataManager;
	private GuiManager guiManager;
	private Logger logger;

	/**
	 * Creates a new instance of {@link CrystalInteractionManager}
	 *
	 * @param fakeCrystalManager       - A {@link FakeCrystalManager}, needed in
	 *                                 order to destroy the crystal.
	 * @param fakeCrystalTaskScheduler - A {@link FakeCrystalTaskScheduler}, needed
	 *                                 in order to schedule a new crystal.
	 * @param playerDataManager        - A {@link PlayerDataManager}, needed to
	 *                                 fetch and update the players' reward streak.
	 * @param guiManager               - A {@link GuiManager}, needed to show a GUI
	 *                                 to the player.
	 * @param logger                   - A {@link Logger} with which to log any
	 *                                 exception. Can be null.
	 */
	public CrystalInteractionManager(FakeCrystalManager fakeCrystalManager,
			FakeCrystalTaskScheduler fakeCrystalTaskScheduler, PlayerDataManager playerDataManager,
			GuiManager guiManager, Logger logger) {
		this.fakeCrystalManager = fakeCrystalManager;
		this.fakeCrystalTaskScheduler = fakeCrystalTaskScheduler;
		this.playerDataManager = playerDataManager;
		this.guiManager = guiManager;
		this.logger = logger;
	}

	/**
	 * Acts as if the player interacted with its crystal by managing the reward
	 * streak and destroying it with a nice effect.
	 *
	 * @param player - the player to have interact with their crystal.
	 */
	public void interactPlayerCrystal(Player player) {
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

		try {
			int crystalId = fakeCrystalManager.getPlayerCrystalId(player);
			Location crystalLocation = fakeCrystalManager.getCrystalLocation(crystalId);

			/* Destruction of the crystal with an explosion */
			fakeCrystalManager.destroyPlayerCrystal(player);

			/* Setup of the explosion packet */
			PacketContainer explosionPacket = new PacketContainer(PacketType.Play.Server.EXPLOSION);

			/* Explosion location */
			explosionPacket.getDoubles().write(0, crystalLocation.getX());
			explosionPacket.getDoubles().write(1, crystalLocation.getY());
			explosionPacket.getDoubles().write(2, crystalLocation.getZ());

			/* Explosion radius */
			explosionPacket.getFloat().write(0, 2f);

			/*
			 * Modified blocks by the explosion. Since this explosion is just for the looks,
			 * we set it as an empty list.
			 */
			explosionPacket.getBlockPositionCollectionModifier().write(0, new LinkedList<BlockPosition>());

			/*
			 * The player's x, y and z velocity after the explosion. Since this is a fake
			 * explosion we just set it to 0.
			 */
			explosionPacket.getFloat().write(1, 0f);
			explosionPacket.getFloat().write(2, 0f);
			explosionPacket.getFloat().write(3, 0f);

			protocolManager.sendServerPacket(player, explosionPacket);

			long lastPlayerInteraction = playerDataManager.getPlayerLastInteraction(player);
			int playerRewardStreak = playerDataManager.getPlayerRewardStreak(player);

			long currentTimeMillis = System.currentTimeMillis();
			long currentMillisTimeZoneOffset = TimeZone.getDefault().getOffset(currentTimeMillis);

			long midnightMillis = TimeUtilities.getMidnightMillis(currentTimeMillis, currentMillisTimeZoneOffset);

			/*
			 * If the player forgot to redeem their reward yesterday the streak we'll reset
			 * the streak.
			 */
			if (lastPlayerInteraction < (midnightMillis - TimeUtilities.MILLISECONDS_IN_A_DAY)) {
				playerRewardStreak = 0;
			} else {
				playerRewardStreak++;
			}

			playerDataManager.setPLayerRewardStreak(player, playerRewardStreak);

			/* Update of the player's last interaction */
			playerDataManager.setPlayerLastInteraction(player, System.currentTimeMillis());
			playerDataManager.write();

			/* Rescheduling the spawning of the player's crystal */
			if (!fakeCrystalTaskScheduler.isPlayerCrystalSpawnPlanned(player)) {
				fakeCrystalTaskScheduler.schedulePlayerCrystalSpawn(player);
			}

			guiManager.openPlayerRewardGui(player);
		} catch (InvocationTargetException exception) {
			if (logger != null) {
				logger.log(Level.SEVERE,
						"Exception thrown during " + player.getName() + "'s interaction with their crystal.",
						exception);
			}
		}
	}
}