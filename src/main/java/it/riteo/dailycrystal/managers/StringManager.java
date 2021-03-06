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

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * A class responsible for creating and parsing the strings file.
 */
public class StringManager {
	public static String COMMAND_PLAYER_ONLY = "command.player_only";
	public static String COMMAND_USAGE = "command.usage";

	public static String COMMAND_RELOAD_SUCCESS = "command.reload.success";

	public static String COMMAND_NOTIFICATION_ENABLE_SUCCESS = "command.notification.enable.success";
	public static String COMMAND_NOTIFICATION_DISABLE_SUCCESS = "command.notification.disable.success";
	public static String COMMAND_NOTIFICATION_USAGE = "command.notification.usage";

	private Plugin plugin;
	private File file;
	private Logger logger;

	private FileConfiguration fileConfiguration;

	/**
	 * Creates a new instance of a {@link StringManager}.
	 *
	 * @param plugin - A {@link Plugin}, used to fetch the <code>strings.yml</code>
	 *               file.
	 * @param file   - A {@link File} pointing to the strings file.
	 * @param logger - A {@link Logger} with which to log any exception. It can be
	 *               null.
	 */
	public StringManager(Plugin plugin, File file, Logger logger) {
		this.plugin = plugin;
		this.file = file;
		this.logger = logger;
	}

	public void reload() {
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			plugin.saveResource("strings.yml", false);
		}

		fileConfiguration = YamlConfiguration.loadConfiguration(file);
	}

	public void write() {
		if (!file.exists() || fileConfiguration == null) {
			return;
		}

		try {
			fileConfiguration.save(file);
		} catch (IOException exception) {
			if (logger != null) {
				logger.log(Level.SEVERE, "Exception thrown while saving the strings file.");
			}
		}
	}

	public String getString(String path) {
		return fileConfiguration.getString(path);
	}
}
