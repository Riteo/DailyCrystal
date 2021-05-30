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
