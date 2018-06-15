package patpower.github.clanraids.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.multi.multiclan.MultiClan;
import de.multi.multiclan.api.MultiClanAPI;
import de.multi.multiclan.api.MultiClanAPI.RankType;
import de.multi.multiclan.commands.ClanCommand;
import patpower.github.clanraids.ClanRaids;
import patpower.github.clanraids.utils.SendMessage;

public class Upgrade extends Claim implements ClanCommand {

	@Override
	public boolean onCommand(Player player, String[] args) {
		if (!player.hasPermission("multiclan.upgrade")) {
			player.sendMessage("§cYou don't have any permission!");
			return true;
		}
		MultiClanAPI clanAPI = MultiClan.getMultiClanAPI();
		String id = null;

		if (!clanAPI.isPlayerInClan(player.getUniqueId())) {
			SendMessage.messagePlayer(player, "You have to be an owner of a clan to upgrade it.", 0);
			return true;
		}
		String clanName = clanAPI.getClan(player.getUniqueId()).getClan();
		// Check if player is clan leader
		if (clanAPI.getRank(clanName.toLowerCase(), player.getName()) != RankType.OWNER) {
			SendMessage.messagePlayer(player, "You have to be the owner of the clan to upgrade it.", 0);
			return true;
		}
		try {
			id = clanAPI.getClan(player.getUniqueId()).getClan();
			int tier = ClanRaids.getConfigMan().getClanTier(id.toLowerCase());
			if (tier >= 4) {
				SendMessage.messagePlayer(player, "Your clan is already max tier!", 0);
				return true;
			}
			if (ClanRaids.getConfigMan().getTotalFund(id.toLowerCase()) >= ClanRaids.getConfigMan().getTierCost(tier)) {
				// Subtracts the cost from the total fund
				ClanRaids.getConfigMan().setTotalFund(id.toLowerCase(),
						ClanRaids.getConfigMan().getTotalFund(id.toLowerCase())
								- ClanRaids.getConfigMan().getTierCost(tier));
				SendMessage.messageClan(id, "Clan upgraded! Your clan is now tier: " + ChatColor.RED
						+ ClanRaids.getConfigMan().upClanTier(id.toLowerCase()));
				return false;
			} else {
				SendMessage.messagePlayer(player, "Your clan does not have enough funds", 0);
				return true;
			}

		} catch (NumberFormatException e) {
			SendMessage.messagePlayer(player, ChatColor.RED + "Invalid amount.", 0);
		}

		return false;
	}

}
