package patpower.github.clanraids.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.multi.multiclan.MultiClan;
import de.multi.multiclan.api.MultiClanAPI;
import de.multi.multiclan.commands.ClanCommand;
import patpower.github.clanraids.ClanRaids;
import patpower.github.clanraids.utils.SendMessage;

public class Contribute extends Claim implements ClanCommand {

	@Override
	public boolean onCommand(Player player, String[] args) {
		if (!player.hasPermission("multiclan.contribute")) {
			player.sendMessage("§cYou don't have any permission!");
			return true;
		}
		MultiClanAPI clanAPI = MultiClan.getMultiClanAPI();
		String id = null;

		if (!clanAPI.isPlayerInClan(player.getUniqueId())) {
			SendMessage.messagePlayer(player, "You have to be in a clan to contribute to the fund.", 0);
			return true;
		}
		try {
			id = clanAPI.getClan(player.getUniqueId()).getClan();
			if (args.length == 2) {
				int amount = Integer.parseInt(args[1]);
				if (amount > 0) {
					if (ClanRaids.getEconomy().has(player, amount)) {
						ClanRaids.getEconomy().withdrawPlayer(player, amount);
						ClanRaids.getConfigMan().contribute(player.getName(), id.toLowerCase(), amount);
						SendMessage.messageClan(id, ChatColor.GREEN + player.getName() + " has contributed $" + amount + " to the clan.");
						return false;
					} else {
						SendMessage.messagePlayer(player, "Not enough funds!", 0);
						return true;
					}
				} else {
					SendMessage.messagePlayer(player, "The amount contributed must be greater than $0", 0);
				}
			} else {
				SendMessage.messagePlayer(player, "Usage: /clan contribute [amount]", 0);
			}

		} catch (NumberFormatException e) {
			SendMessage.messagePlayer(player, ChatColor.RED + "Invalid amount.", 0);
		}

		return false;
	}

}
