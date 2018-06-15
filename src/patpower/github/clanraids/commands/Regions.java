package patpower.github.clanraids.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;

import de.multi.multiclan.MultiClan;
import de.multi.multiclan.api.MultiClanAPI;
import de.multi.multiclan.commands.ClanCommand;
import patpower.github.clanraids.ClanRaids;
import patpower.github.clanraids.utils.SendMessage;

public class Regions extends Claim implements ClanCommand {

	@Override
	public boolean onCommand(Player player, String[] args) {
		if (!player.hasPermission("multiclan.regions")) {
			player.sendMessage("§cYou don't have any permission!");
			return true;
		}
		WorldGuardPlugin plugin = ClanRaids.getWorldG();
		MultiClanAPI clanAPI = MultiClan.getMultiClanAPI();
		String id = null;
		try {
			RegionManager manager = checkRegionManager(plugin, player.getWorld());
			if (!clanAPI.isPlayerInClan(player.getUniqueId())) {
				SendMessage.messagePlayer(player, "You have to be in a clan to use this command.", 0);
				return true;
			}
			id = clanAPI.getClan(player.getUniqueId()).getClan();
			SendMessage.messagePlayer(player, ChatColor.STRIKETHROUGH + "                  " + ChatColor.GREEN
					+ "[Clan Regions]" + ChatColor.WHITE + ChatColor.STRIKETHROUGH + "                  ", 0);
			int tier = ClanRaids.getConfigMan().getClanTier(id);
			SendMessage.messagePlayer(player, "Clan Tier: " + ChatColor.GRAY + tier, 0);

			if (manager.hasRegion(id + "_1")) {
				SendMessage.messagePlayer(player, "Region 1: " + ChatColor.GREEN + "Exists", 0);
				SendMessage.messagePlayer(player, "     Size: " + ChatColor.GRAY + manager.getRegion(id + "_1").volume()
						+ "/" + getTierVolume(id), 0);
			} else {
				SendMessage.messagePlayer(player, "Region 1: " + ChatColor.RED + "No", 0);
				SendMessage.messagePlayer(player, "     Size: " + ChatColor.GRAY + "0/" + getTierVolume(id), 0);
			}

			if (manager.hasRegion(id + "_2")) {
				SendMessage.messagePlayer(player, "Region 2: " + ChatColor.GREEN + "Exists", 0);
				SendMessage.messagePlayer(player, "     Size: " + ChatColor.GRAY + manager.getRegion(id + "_2").volume()
						+ "/" + getTierVolume(id) / 10, 0);
			} else {
				SendMessage.messagePlayer(player, "Region 2: " + ChatColor.RED + "No", 0);
				SendMessage.messagePlayer(player, "     Size: " + ChatColor.GRAY + "0/" + getTierVolume(id) / 10, 0);
			}
			SendMessage.messagePlayer(player, ChatColor.STRIKETHROUGH + "                  " + ChatColor.GREEN
					+ "[Clan Regions]" + ChatColor.WHITE + ChatColor.STRIKETHROUGH + "                  ", 0);
		} catch (CommandException e) {
			SendMessage.messagePlayer(player, e.getMessage(), 0);
			return true;
		}

		return false;
	}

}
