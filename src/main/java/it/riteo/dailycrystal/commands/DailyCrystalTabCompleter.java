package it.riteo.dailycrystal.commands;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

/**
 * A very simple class managing what to suggest while writing the command in the
 * game chat
 */
public class DailyCrystalTabCompleter implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] arguments) {
		List<String> suggestions = new LinkedList<String>();

		if (arguments.length == 1) {
			if (shouldPrintSuggestion(DailyCrystalCommandExecutor.NOTIFICATION_SUBCOMMAND, arguments[0])) {
				suggestions.add(DailyCrystalCommandExecutor.NOTIFICATION_SUBCOMMAND);
			}

			if (shouldPrintSuggestion(DailyCrystalCommandExecutor.RELOAD_SUBCOMMAND, arguments[0])) {
				suggestions.add(DailyCrystalCommandExecutor.RELOAD_SUBCOMMAND);
			}
		}

		if (arguments.length > 1) {
			if (arguments[0].equalsIgnoreCase(DailyCrystalCommandExecutor.NOTIFICATION_SUBCOMMAND)) {
				if (shouldPrintSuggestion(DailyCrystalCommandExecutor.ENABLE_NOTIFICATION_SUBCOMMAND, arguments[1])) {
					suggestions.add(DailyCrystalCommandExecutor.ENABLE_NOTIFICATION_SUBCOMMAND);
				}

				if (shouldPrintSuggestion(DailyCrystalCommandExecutor.DISABLE_NOTIFICATION_SUBCOMMAND, arguments[1]))
					suggestions.add(DailyCrystalCommandExecutor.DISABLE_NOTIFICATION_SUBCOMMAND);
			}
		}

		return suggestions;
	}

	/* This stuff is needed to avoid having stuff already written suggested. */
	private boolean shouldPrintSuggestion(String suggestion, String currentArgument) {
		return suggestion.startsWith(currentArgument) && !suggestion.equalsIgnoreCase(currentArgument);
	}
}