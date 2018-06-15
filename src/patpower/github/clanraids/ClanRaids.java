package patpower.github.clanraids;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import de.multi.multiclan.MultiClan;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import patpower.github.clanraids.commands.Claim;
import patpower.github.clanraids.commands.Contribute;
import patpower.github.clanraids.commands.Forge;
import patpower.github.clanraids.commands.Info;
import patpower.github.clanraids.commands.Protect;
import patpower.github.clanraids.commands.Regions;
import patpower.github.clanraids.commands.Unclaim;
import patpower.github.clanraids.commands.Upgrade;
import patpower.github.clanraids.listener.player.PlayerCommandPreprocess;
import patpower.github.clanraids.listener.player.PlayerDeath;
import patpower.github.clanraids.listener.player.PlayerInteract;
import patpower.github.clanraids.listener.player.PlayerPlaceBlock;
import patpower.github.clanraids.threads.ThreadController;

public class ClanRaids extends JavaPlugin {

	private static ClanRaids plugin;
	private static ThreadController threadControl;
	private static ConfigManager config;
	private static Economy econ = null;
	private static Permission perms = null;
	private static Chat chat = null;

	@Override
	public void onEnable() {
		plugin = this;

		PluginDescriptionFile pdfFile = getDescription();

		addClanCommands();

		// Config stuff
		config = new ConfigManager(this);
		config.loadConfig();

		// Vault
		if (!setupEconomy()) {
			System.out.println(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		setupPermissions();
		setupChat();

		registerEvents();
		threadControl = new ThreadController();
		getLogger().info(pdfFile.getName() + " has been enabled. Version. " + pdfFile.getVersion() + ".");
	}

	public void onDisable() {
		getLogger().info("Plugin Disabled.");
	}

	private void registerEvents() {
		getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
		getServer().getPluginManager().registerEvents(new PlayerCommandPreprocess(), this);
		getServer().getPluginManager().registerEvents(new PlayerDeath(), this);
		getServer().getPluginManager().registerEvents(new PlayerPlaceBlock(), this);
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	private boolean setupChat() {
		RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
		chat = rsp.getProvider();
		return chat != null;
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

	private void addClanCommands() {
		MultiClan.getMultiClanAPI().registerClanCommand(new Claim(), new String[] { "claim" });
		MultiClan.getMultiClanAPI().registerClanCommand(new Unclaim(), new String[] { "unclaim" });
		MultiClan.getMultiClanAPI().registerClanCommand(new Regions(), new String[] { "regions" });
		MultiClan.getMultiClanAPI().registerClanCommand(new Contribute(), new String[] { "contribute" });
		MultiClan.getMultiClanAPI().registerClanCommand(new Upgrade(), new String[] { "upgrade" });
		MultiClan.getMultiClanAPI().registerClanCommand(new Info(), new String[] { "info" });
		MultiClan.getMultiClanAPI().registerClanCommand(new Forge(), new String[] { "forge" });
		MultiClan.getMultiClanAPI().registerClanCommand(new Protect(), new String[] { "protect" });
	}

	public static Economy getEconomy() {
		return econ;
	}

	public static Permission getPermissions() {
		return perms;
	}

	public static Chat getChat() {
		return chat;
	}

	public static ThreadController getThreadController() {
		return threadControl;
	}

	public static ClanRaids getInstance() {
		return plugin;
	}

	public static WorldGuardPlugin getWorldG() {
		Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		if (p instanceof WorldGuardPlugin) {
			return (WorldGuardPlugin) p;
		} else {
			return null;
		}
	}

	public static ConfigManager getConfigMan() {
		return config;
	}
}
