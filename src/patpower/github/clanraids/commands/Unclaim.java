package patpower.github.clanraids.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;

import de.multi.multiclan.MultiClan;
import de.multi.multiclan.api.MultiClanAPI;
import de.multi.multiclan.api.MultiClanAPI.RankType;
import de.multi.multiclan.commands.ClanCommand;
import patpower.github.clanraids.ClanRaids;
import patpower.github.clanraids.utils.SendMessage;

public class Unclaim extends Claim implements ClanCommand {

	public boolean onCommand(Player player, String[] args) {
		if (!player.hasPermission("multiclan.unclaim")) {
			player.sendMessage("§cYou don't have any permission!");
			return true;
		}
		WorldGuardPlugin plugin = ClanRaids.getWorldG();
		MultiClanAPI clanAPI = MultiClan.getMultiClanAPI();
		String id = null;
		RegionManager manager = null;

		if (!clanAPI.isPlayerInClan(player.getUniqueId())) {
			SendMessage.messagePlayer(player, "You have to be an owner of a clan to unclaim a region.", 0);
			return true;
		}
		String clanName = clanAPI.getClan(player.getUniqueId()).getClan();
		// Check if player is clan leader
		if (clanAPI.getRank(clanName.toLowerCase(), player.getName()) != RankType.OWNER) {
			SendMessage.messagePlayer(player, "You have to the clan owner to unclaim a region.", 0);
			return true;
		}
		if (ClanRaids.getThreadController().isClanBusy(clanName.toLowerCase())) {
			SendMessage.messagePlayer(player,
					ChatColor.YELLOW + "You cannot modify your regions while being raided/extracted.", 0);
			return true;
		}
		try {
			if (args.length == 2) {
				manager = checkRegionManager(plugin, player.getWorld());
				id = clanAPI.getClan(player.getUniqueId()).getClan();
				if (args[1].equals("1")) {
					if (manager.hasRegion(id + "_1")) {
						if (!manager.hasRegion(id + "_2")) {
							manager.removeRegion(id + "_1");
							SendMessage.messagePlayer(player, "Clan region 1 has been deleted", 0);
							ClanRaids.getConfigMan().setFirstRegionSet(id, false);
							return false;
						} else {
							SendMessage.messagePlayer(player, "Please unclaim region 2 before attempting this!", 0);
						}
					} else {
						SendMessage.messagePlayer(player, "You haven't claimed region 1 yet!", 0);
						return true;
					}
				} else if (args[1].equals("2")) {
					if (manager.hasRegion(id + "_2")) {
						manager.removeRegion(id + "_2");
						SendMessage.messagePlayer(player, "Clan region 2 has been deleted", 0);
						return false;
					} else {
						SendMessage.messagePlayer(player, "You haven't claimed region 2 yet!", 0);
						return true;
					}
				}
			} else {
				SendMessage.messagePlayer(player, "Usage: /clan unclaim [1 or 2]", 0);
			}
		} catch (

		CommandException e) {
			SendMessage.messagePlayer(player, e.getMessage(), 0);
			return true;
		}
		return false;
	}
}