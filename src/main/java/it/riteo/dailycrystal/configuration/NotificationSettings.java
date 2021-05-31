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

package it.riteo.dailycrystal.configuration;

/**
 * A class representing the new reward notification's configuration, such as it
 * title.
 */
public class NotificationSettings {
	public enum NotificationTrigger {
		ON_JOIN, ON_RESOURCE_PACK_LOAD
	}

	private boolean enabled;
	private String title;
	private String subtitle;
	private NotificationTrigger notificationTrigger;

	public NotificationSettings(boolean enabled, String title, String subtitle, NotificationTrigger mode) {
		this.enabled = enabled;
		this.title = title;
		this.subtitle = subtitle;
		this.notificationTrigger = mode;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public String getTitle() {
		return title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public NotificationTrigger getMode() {
		return notificationTrigger;
	}
}
