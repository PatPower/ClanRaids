package patpower.github.clanraids;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import javax.naming.ConfigurationException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {
	private ClanRaids plugin;
	public double clanCreateCost;
	public Set<String> clanList = new HashSet<String>();

	private FileConfiguration customConfig = null;
	private File customConfigFile = null;

	public ConfigManager(ClanRaids plugin) {
		this.plugin = plugin;
		plugin.saveDefaultConfig();
		saveDefaultConfig();
	}

	public void loadConfig() {
		// Loads all the values from config into local variables
		clanCreateCost = plugin.getConfig().getDouble("clanCreateCost");

		// Gets all the clans and puts them into a list
		if (getCustomConfig().getConfigurationSection("clan") != null) {
			clanList = getCustomConfig().getConfigurationSection("clan").getKeys(false);
		}
	}

	public void reloadCustomConfig() {
		if (customConfigFile == null) {
			customConfigFile = new File(plugin.getDataFolder(), "data.yml");
		}
		customConfig = YamlConfiguration.loadConfiguration(customConfigFile);

		// Look for defaults in the jar
		Reader defConfigStream = null;
		try {
			defConfigStream = new InputStreamReader(plugin.getResource("data.yml"), "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			customConfig.setDefaults(defConfig);
		}
	}

	public FileConfiguration getCustomConfig() {
		if (customConfig == null) {
			reloadCustomConfig();
		}
		return customConfig;
	}

	public void saveCustomConfig() {
		if (customConfig == null || customConfigFile == null) {
			return;
		}
		try {
			getCustomConfig().save(customConfigFile);
		} catch (IOException ex) {
			plugin.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
		}
	}

	public void saveDefaultConfig() {
		if (customConfigFile == null) {
			customConfigFile = new File(plugin.getDataFolder(), "data.yml");
		}
		if (!customConfigFile.exists()) {
			plugin.saveResource("data.yml", false);
		}
	}

	public void addClan(String name) {
		getCustomConfig().set("clan." + name.toLowerCase() + ".name", name);
		// Set Tier
		getCustomConfig().set("clan." + name.toLowerCase() + ".tier", 0);
		// Is the first region set?
		getCustomConfig().set("clan." + name.toLowerCase() + ".firstRegionSet", false);
		// Clan hasn't enabled protections
		setProtected(name, false);
		clanList.add(name.toLowerCase());

		saveCustomConfig();
	}

	public void removeClan(String name) {
		// Set Tier
		getCustomConfig().set("clan." + name.toLowerCase(), null);

		clanList.remove(name);

		saveCustomConfig();
	}

	public void contribute(String id, String clan, int amount) {
		getCustomConfig().set("clan." + clan.toLowerCase() + ".contribute." + id, getContribute(id, clan) + amount);
		getCustomConfig().set("clan." + clan.toLowerCase() + ".totalFund", getTotalFund(clan) + amount);
		saveCustomConfig();
	}

	public int getContribute(String id, String clan) {
		if (!getCustomConfig().contains("clan." + clan.toLowerCase() + ".contribute." + id)) {
			return 0;
		} else {
			return getCustomConfig().getInt("clan." + clan.toLowerCase() + ".contribute." + id);
		}
	}

	public void setFirstRegionSet(String name, boolean val) {
		getCustomConfig().set("clan." + name.toLowerCase() + ".firstRegionSet", val);
		saveCustomConfig();
	}

	public int getTotalFund(String clan) {
		if (!getCustomConfig().contains("clan." + clan.toLowerCase() + ".totalFund")) {
			return 0;
		} else {
			return getCustomConfig().getInt("clan." + clan.toLowerCase() + ".totalFund");
		}
	}

	public void setTotalFund(String clan, int amount) {
		getCustomConfig().set("clan." + clan.toLowerCase() + ".totalFund", amount);
		saveCustomConfig();
	}

	public int getClanTier(String name) {
		if (!clanList.contains(name.toLowerCase())) {
			addClan(name);
		}
		return getCustomConfig().getInt("clan." + name.toLowerCase() + ".tier");
	}

	public int upClanTier(String name) {
		if (!clanList.contains(name.toLowerCase())) {
			addClan(name);
		}
		getCustomConfig().set("clan." + name.toLowerCase() + ".tier", getClanTier(name) + 1);
		saveCustomConfig();
		return getCustomConfig().getInt("clan." + name.toLowerCase() + ".tier");
	}

	public int getTierCost(int tier) {
		return plugin.getConfig().getInt("clanTierCost." + tier);
	}

	public boolean isFirstRegionSet(String name) throws ConfigurationException {
		return getCustomConfig().getBoolean("clan." + name.toLowerCase() + ".firstRegionSet");
	}

	public int decreaseExtractTimer(String name) {
		int time = getCustomConfig().getInt("raiding." + name.toLowerCase() + ".time");
		if (time == 0) {
			getCustomConfig().set("raiding." + name.toLowerCase() + ".time", getExtractTime());
			return getExtractTime();
		}
		getCustomConfig().set("raiding." + name.toLowerCase() + ".time", time - 1);
		saveCustomConfig();
		return getCustomConfig().getInt("raiding." + name.toLowerCase() + ".time");
	}

	public int getExtractTime() {
		return plugin.getConfig().getInt("raidTime");
	}

	public void removeExtractTime(String name) {
		getCustomConfig().set("raiding." + name.toLowerCase() + ".time", null);
		saveCustomConfig();
	}

	public void removeRaidTime(String name) {
		getCustomConfig().set("raiding." + name.toLowerCase() + ".raidTime", null);
		saveCustomConfig();
	}

	public int decreaseRaidTimer(String name) {
		int time = getCustomConfig().getInt("raiding." + name.toLowerCase() + ".raidTime");
		if (time == 0) {
			getCustomConfig().set("raiding." + name.toLowerCase() + ".raidTime", getExtractTime());
			return getExtractTime();
		}
		getCustomConfig().set("raiding." + name.toLowerCase() + ".raidTime", time - 1);
		saveCustomConfig();
		return getCustomConfig().getInt("raiding." + name.toLowerCase() + ".raidTime");
	}

	public int regionDisruptCounter(String region) {
		int time = getCustomConfig().getInt("regions." + region + ".disruptTimeLeft");
		if (time == 0) {
			getCustomConfig().set("regions." + region + ".disruptTimeLeft", 70);
			return 70;
		}
		getCustomConfig().set("regions." + region + ".disruptTimeLeft", time - 1);
		saveCustomConfig();
		return getCustomConfig().getInt("regions." + region + ".disruptTimeLeft");
	}

	public void removeDisruptTime(String region) {
		getCustomConfig().set("regions." + region + ".disruptTimeLeft", null);
		saveCustomConfig();
	}

	public String getClanName(String name) {
		return getCustomConfig().getString("clan." + name.toLowerCase() + ".name");
	}

	public ArrayList<String> getSettofProcTime() {
		if (getCustomConfig().getConfigurationSection("protection.timeLeft") != null) {
			return new ArrayList<String>(getCustomConfig().getConfigurationSection("protection.timeLeft").getKeys(false));
		} else { 
			return new ArrayList<String>();
		}
	}

	public HashMap<String, Integer> getHashMapOfTime() {
		HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
		if (getCustomConfig().getConfigurationSection("protection.hour") != null) {
			for (String clan : getCustomConfig().getConfigurationSection("protection.hour").getKeys(false)) {
				hashMap.put(clan, getCustomConfig().getInt("protection.hour." + clan.toLowerCase()));
			}

		}
		return hashMap;
	}

	public int decProtectionTimer(String name) {
		int time = getCustomConfig().getInt("protection.timeLeft." + name.toLowerCase());
		if (time == 0) {
			// 7 Hours
			getCustomConfig().set("protection.timeLeft." + name.toLowerCase(), 7 * 60 * 60);
			return 7 * 60 * 60;
		}
		getCustomConfig().set("protection.timeLeft." + name.toLowerCase(), time - 1);
		saveCustomConfig();
		return getCustomConfig().getInt("protection.timeLeft." + name.toLowerCase());
	}

	public int getProtectionTime(String name) {
		return getCustomConfig().getInt("protection.timeLeft." + name.toLowerCase());
	}

	public void removeProtectionTimer(String name) {
		getCustomConfig().set("protection.timeLeft." + name.toLowerCase(), null);
		saveCustomConfig();
	}

	public void setProtectionTime(String name, int time) {
		getCustomConfig().set("protection.hour." + name.toLowerCase(), time);
		saveCustomConfig();
	}

	public void removeProtection(String name) {
		getCustomConfig().set("protection.hour." + name.toLowerCase(), null);
		getCustomConfig().set("protection.daysLeft." + name.toLowerCase(), null);
		getCustomConfig().set("protection.timeLeft." + name.toLowerCase(), null);
		saveCustomConfig();
	}

	public int decProtectionDay(String name) {
		int time = getCustomConfig().getInt("protection.daysLeft." + name.toLowerCase());
		if (time == 0) {
			// 7 days
			getCustomConfig().set("protection.daysLeft." + name.toLowerCase(), 6);
			return 6;
		}
		getCustomConfig().set("protection.daysLeft." + name.toLowerCase(), time - 1);
		saveCustomConfig();
		return getCustomConfig().getInt("protection.daysLeft." + name.toLowerCase());
	}

	public int getProtectionDay(String name) {
		return getCustomConfig().getInt("protection.daysLeft." + name.toLowerCase());
	}
	
	public int setProtectionDay(String name) {
		// 7 days
		getCustomConfig().set("protection.daysLeft." + name.toLowerCase(), 7);
		saveCustomConfig();
		return 7;
	}

	public void setProtected(String name, boolean value) {
		getCustomConfig().set("clan." + name.toLowerCase() + ".protected", value);
		saveCustomConfig();
	}
	
	public void isClanProtected(String name) {
		getCustomConfig().getBoolean("clan." + name.toLowerCase() + ".protected");
	}
	
}
