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
