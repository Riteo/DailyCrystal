package it.riteo.dailycrystal.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import it.riteo.dailycrystal.managers.PlayerDataManager;
import it.riteo.dailycrystal.managers.StringManager;

public class DailyCrystalCommandExecutor implements CommandExecutor {

	public static String RELOAD_SUBCOMMAND = "reload";
	public static String NOTIFICATION_SUBCOMMAND = "notification";
	public static String ENABLE_NOTIFICATION_SUBCOMMAND = "enable";
	public static String DISABLE_NOTIFICATION_SUBCOMMAND = "disable";

	Plugin plugin;
	PlayerDataManager playerDataManager;
	StringManager stringManager;

	public DailyCrystalCommandExecutor(Plugin plugin, PlayerDataManager playerDataManager,
			StringManager stringManager) {
		this.plugin = plugin;
		this.playerDataManager = playerDataManager;
		this.stringManager = stringManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
		if (arguments.length != 0) {
			if (arguments[0].equalsIgnoreCase(RELOAD_SUBCOMMAND)
					&& sender.hasPermission("dailycrystal.command.dailycrystal.reload")) {
				plugin.reloadConfig();
				sender.sendMessage(stringManager.getString(StringManager.COMMAND_RELOAD_SUCCESS));
				return true;
			}

			if (arguments[0].equalsIgnoreCase(NOTIFICATION_SUBCOMMAND)) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(stringManager.getString(StringManager.COMMAND_PLAYER_ONLY));
					return true;
				}

				Player playerSender = (Player) sender;

				if (arguments.length >= 2) {
					if (arguments[1].equalsIgnoreCase(ENABLE_NOTIFICATION_SUBCOMMAND)) {
						playerDataManager.setPlayerNotificationPreference(playerSender, true);
						playerDataManager.write();

						sender.sendMessage(stringManager.getString(StringManager.COMMAND_NOTIFICATION_ENABLE_SUCCESS));

						return true;
					} else if (arguments[1].equalsIgnoreCase(DISABLE_NOTIFICATION_SUBCOMMAND)) {
						playerDataManager.setPlayerNotificationPreference(playerSender, false);
						playerDataManager.write();
						sender.sendMessage(stringManager.getString(StringManager.COMMAND_NOTIFICATION_DISABLE_SUCCESS));

						return true;
					}
				}

				sender.sendMessage(
						String.format(stringManager.getString(StringManager.COMMAND_NOTIFICATION_USAGE), label));
				return true;
			}
		}

		sender.sendMessage(String.format(stringManager.getString(StringManager.COMMAND_USAGE), label));
		return true;
	}
}