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
