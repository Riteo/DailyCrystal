package it.riteo.dailycrystal;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;

import it.riteo.dailycrystal.commands.DailyCrystalCommandExecutor;
import it.riteo.dailycrystal.commands.DailyCrystalTabCompleter;
import it.riteo.dailycrystal.eventlisteners.CrystalLoadingEventListener;
import it.riteo.dailycrystal.eventlisteners.PlayerNotificationEventListener;
import it.riteo.dailycrystal.managers.ConfigurationManager;
import it.riteo.dailycrystal.managers.CrystalInteractionManager;
import it.riteo.dailycrystal.managers.FakeCrystalManager;
import it.riteo.dailycrystal.managers.GuiManager;
import it.riteo.dailycrystal.managers.PlayerDataManager;
import it.riteo.dailycrystal.managers.StringManager;
import it.riteo.dailycrystal.packetadapters.CrystalInteractionPacketAdapter;
import it.riteo.dailycrystal.packetadapters.CrystalLoadingPacketAdapter;
import it.riteo.dailycrystal.taskschedulers.FakeCrystalTaskScheduler;
import it.riteo.dailycrystal.taskschedulers.PlayerNotificationTaskScheduler;

public class DailyCrystal extends JavaPlugin {
	private ConfigurationManager configurationManager;
	private PlayerDataManager playerDataManager;
	private StringManager stringManager;

	private FakeCrystalManager fakeCrystalManager;
	private CrystalInteractionManager crystalInteractionManager;
	private GuiManager guiManager;

	private FakeCrystalTaskScheduler fakeCrystalTaskScheduler;
	private PlayerNotificationTaskScheduler playerNotificationTaskScheduler;

	@Override
	public void onEnable() {
		saveDefaultConfig();

		getServer().getPluginManager().registerEvents(new CrystalLoadingEventListener(getFakeCrystalTaskScheduler()),
				this);

		getServer().getPluginManager().registerEvents(new PlayerNotificationEventListener(getConfigurationManager(),
				getPlayerDataManager(), getFakeCrystalTaskScheduler(), getPlayerNotificationTaskScheduler()), this);

		getServer().getPluginManager().registerEvents(getGuiManager(), this);

		PluginCommand mainCommand = getServer().getPluginCommand("dailycrystal");
		mainCommand.setExecutor(new DailyCrystalCommandExecutor(this, getPlayerDataManager(), getStringManager()));
		mainCommand.setTabCompleter(new DailyCrystalTabCompleter());

		ProtocolLibrary.getProtocolManager().addPacketListener(new CrystalInteractionPacketAdapter(this,
				getFakeCrystalManager(), getCrystalInteractionManager(), ListenerPriority.NORMAL));

		ProtocolLibrary.getProtocolManager()
				.addPacketListener(new CrystalLoadingPacketAdapter(this, getConfigurationManager(),
						getFakeCrystalTaskScheduler(), getFakeCrystalManager(), getLogger(), ListenerPriority.NORMAL));
	}

	@Override
	public void onDisable() {
		try {
			getFakeCrystalManager().destroyAllCrystals();
		} catch (InvocationTargetException | NullPointerException exception) {
			getLogger().log(Level.SEVERE, "Exception thrown while destroying everyone's fake crystal.", exception);
		}
	}

	@Override
	public void reloadConfig() {
		super.reloadConfig();
		getConfigurationManager().updateConfig(getConfig());
		getConfigurationManager().invalidateCache();
		getPlayerDataManager().reload();
		getStringManager().reload();

		getPlayerNotificationTaskScheduler().unscheduleAllNotifications();
		getFakeCrystalTaskScheduler().cancelAllCrystalSpawns();
		try {
			getFakeCrystalManager().destroyAllCrystals();
		} catch (InvocationTargetException | NullPointerException exception) {
			getLogger().log(Level.SEVERE, "Exception thrown while destroying everybody's crystal", exception);
		}

		/* TODO: Make this simpler and more accurate */
		/*
		 * Schedules all crystals if they are in the visible range of the player. Kind
		 * of, as it actually doesn't care about the diagonal distance, so it check more
		 * for a circle than a square. It's still good enough though.
		 */
		Location crystalLocation = getConfigurationManager().getCrystalSettings().getLocation();
		long visibleBlockRadius = getServer().getViewDistance() * 16;
		for (Player player : getServer().getOnlinePlayers()) {
			if (player.getLocation().distance(crystalLocation) < visibleBlockRadius) {
				getFakeCrystalTaskScheduler().schedulePlayerCrystalSpawn(player);
			}
		}
	}

	public ConfigurationManager getConfigurationManager() {
		if (configurationManager == null) {
			configurationManager = new ConfigurationManager(getConfig(), getLogger());
		}

		return configurationManager;
	}

	public PlayerDataManager getPlayerDataManager() {
		if (playerDataManager == null) {
			playerDataManager = new PlayerDataManager(new File(getDataFolder(), "playerdata.yml"), getLogger());
		}

		return playerDataManager;
	}

	public StringManager getStringManager() {
		if (stringManager == null) {
			stringManager = new StringManager(this, new File(getDataFolder(), "strings.yml"), getLogger());
		}

		return stringManager;
	}

	public GuiManager getGuiManager() {
		if (guiManager == null) {
			guiManager = new GuiManager(getConfigurationManager(), getPlayerDataManager(), getLogger());
		}

		return guiManager;
	}

	public FakeCrystalManager getFakeCrystalManager() {
		if (fakeCrystalManager == null) {
			fakeCrystalManager = new FakeCrystalManager(getLogger());
		}

		return fakeCrystalManager;
	}

	public PlayerNotificationTaskScheduler getPlayerNotificationTaskScheduler() {
		if (playerNotificationTaskScheduler == null) {
			playerNotificationTaskScheduler = new PlayerNotificationTaskScheduler(this, getConfigurationManager(),
					getFakeCrystalTaskScheduler());
		}

		return playerNotificationTaskScheduler;
	}

	public FakeCrystalTaskScheduler getFakeCrystalTaskScheduler() {
		if (fakeCrystalTaskScheduler == null) {
			fakeCrystalTaskScheduler = new FakeCrystalTaskScheduler(this, getFakeCrystalManager(),
					getConfigurationManager(), getPlayerDataManager(), getLogger());
		}

		return fakeCrystalTaskScheduler;
	}

	public CrystalInteractionManager getCrystalInteractionManager() {
		if (crystalInteractionManager == null) {
			crystalInteractionManager = new CrystalInteractionManager(getFakeCrystalManager(),
					getFakeCrystalTaskScheduler(), getPlayerDataManager(), getGuiManager(), getLogger());
		}

		return crystalInteractionManager;
	}
}