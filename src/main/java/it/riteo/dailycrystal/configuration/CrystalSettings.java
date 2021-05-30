package it.riteo.dailycrystal.configuration;

import org.bukkit.Location;

/**
 * A class representing the configuration for the crystals such as their
 * location.
 */
public class CrystalSettings {
	private Location location;

	public CrystalSettings(Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}
}
