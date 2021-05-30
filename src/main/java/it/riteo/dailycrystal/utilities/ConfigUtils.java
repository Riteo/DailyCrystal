package it.riteo.dailycrystal.utilities;

import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * An utility class capable of managing configurations operations, such as
 * deserializing.
 */
public class ConfigUtils {
	/* These are static utilities, we don't want to be able to instance them. */
	private ConfigUtils() {
	}

	/**
	 * Deserializes a {@link Location}.
	 *
	 * @param values - A {@link Map} of values representing the serialized location.
	 * @return the deserialized location if valid, otherwise <code>null</code>
	 */
	public static Location deserializeLocation(Map<String, Object> values) {
		/*
		 * I had to make this simple function because the official API had no sane way
		 * to serialize a location with an invalid world without printing a shitton of
		 * lines in the terminal.
		 */

		World world = null;
		Double x = 0.0;
		Double y = 0.0;
		Double z = 0.0;

		Object worldObject = values.get("world");
		Object xObject = values.get("x");
		Object yObject = values.get("y");
		Object zObject = values.get("z");

		if (worldObject instanceof String) {
			world = Bukkit.getWorld((String) worldObject);
		}

		if (world == null) {
			return null;
		}

		if (xObject instanceof Double) {
			x = (Double) xObject;
		} else {
			return null;
		}

		if (yObject instanceof Double) {
			y = (Double) yObject;
		} else {
			return null;
		}

		if (zObject instanceof Double) {
			z = (Double) zObject;
		} else {
			return null;
		}

		return new Location(world, x, y, z);
	}
}
