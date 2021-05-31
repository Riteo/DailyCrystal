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

package it.riteo.dailycrystal.packetadapters;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import it.riteo.dailycrystal.managers.CrystalInteractionManager;
import it.riteo.dailycrystal.managers.FakeCrystalManager;
import it.riteo.dailycrystal.runnables.CrystalInteractionRunnable;

/**
 * A packet adapter which intercepts the packet needed to interact with the
 * crystal. The client sends a packet for whatever entity it hits and, since it
 * believes there's actually an ender crystal, it will send its id to the server
 * and, by comparing it with the one registered in {@link FakeCrystalManager},
 * this class can determine whether the client hit its fake crystal.
 */
public class CrystalInteractionPacketAdapter extends PacketAdapter {
	Plugin plugin;
	FakeCrystalManager fakeCrystalManager;
	CrystalInteractionManager crystalInteractionManager;

	public CrystalInteractionPacketAdapter(Plugin plugin, FakeCrystalManager fakeCrystalManager,
			CrystalInteractionManager crystalInteractionManager, ListenerPriority priority) {
		super(plugin, priority, PacketType.Play.Client.USE_ENTITY);

		this.plugin = plugin;
		this.fakeCrystalManager = fakeCrystalManager;
		this.crystalInteractionManager = crystalInteractionManager;
	}

	@Override
	public void onPacketReceiving(PacketEvent event) {
		Player player = event.getPlayer();

		EntityUseAction entityUseAction = event.getPacket().getEntityUseActions().read(0);

		if (entityUseAction.equals(EntityUseAction.ATTACK)) {
			Integer interactedEntityId = event.getPacket().getIntegers().read(0);

			if (fakeCrystalManager.hasPlayerLocalCrystal(player)
					&& fakeCrystalManager.isCrystalOfPlayer(interactedEntityId, player)) {

				/*
				 * The interaction gets scheduled in order to use Bukkit's API, since this event
				 * is called from another method.
				 */
				new CrystalInteractionRunnable(crystalInteractionManager, player).runTask(plugin);
			}
		}
	}
}
