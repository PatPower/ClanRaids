package patpower.github.clanraids.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.multi.multiclan.MultiClan;
import de.multi.multiclan.api.MultiClanAPI;
import de.multi.multiclan.clan.ClanPlayer;
import de.multi.multiclan.commands.ClanCommand;
import patpower.github.clanraids.ClanRaids;
import patpower.github.clanraids.utils.SendMessage;

public class Info extends Claim implements ClanCommand {

	@Override
	public boolean onCommand(Player player, String[] args) {
		if (!player.hasPermission("multiclan.info")) {
			player.sendMessage("§cYou don't have any permission!");
			return true;
		}
		MultiClanAPI clanAPI = MultiClan.getMultiClanAPI();
		String id = null;

		if (!clanAPI.isPlayerInClan(player.getUniqueId())) {
			SendMessage.messagePlayer(player, "You have to be in a clan to use /clan info.", 0);
			return true;
		}
		if (args.length == 2) {
			if (clanAPI.getClan(args[1]) != null) {
				id = clanAPI.getClan(args[1]).getClan();
			} else {
				SendMessage.messagePlayer(player, ChatColor.YELLOW + "Clan does not exist!", 0);
				return true;
			}
		} else {
			id = clanAPI.getClan(player.getUniqueId()).getClan();
		}
		try {
			//TODO: LIMIT NON CLAN
			int tier = ClanRaids.getConfigMan().getClanTier(id.toLowerCase());
			int funds = ClanRaids.getConfigMan().getTotalFund(id.toLowerCase());
			List<ClanPlayer> playerList = clanAPI.getClan(id).getPlayer();
			SendMessage.messagePlayer(player, ChatColor.STRIKETHROUGH + "                  " + ChatColor.AQUA + "[Clan " + id + "]" + ChatColor.WHITE + ChatColor.STRIKETHROUGH + "                  ",
					0);
			SendMessage.messagePlayer(player, ChatColor.AQUA + "Clan Tier: " + ChatColor.GRAY + tier, 0);
			if (tier != 4) {
				SendMessage.messagePlayer(player, ChatColor.DARK_GREEN + "Clan Funds: " + ChatColor.GRAY + "$" + funds + "/" + ClanRaids.getConfigMan().getTierCost(tier), 0);
			} else {
				SendMessage.messagePlayer(player, ChatColor.DARK_GREEN + "Clan Funds: " + ChatColor.GRAY + "$" + funds, 0);
			}
			SendMessage.messagePlayer(player, ChatColor.GOLD + "[" + MultiClan.getInstance().getConfigSettings().getCfg().getString("settings.clan.rank.owner.name") + "]:", 0);

			for (ClanPlayer p : playerList) {
				if (p.getRank() == 2) {

					SendMessage.messagePlayer(player, ChatColor.WHITE + p.getPlayer() + " - " + ChatColor.GRAY + "Contributed: " + ChatColor.WHITE + "$"
							+ ClanRaids.getConfigMan().getContribute(p.getPlayer(), id.toLowerCase()), 0);
				}
			}
			SendMessage.messagePlayer(player, ChatColor.LIGHT_PURPLE + "[" + MultiClan.getInstance().getConfigSettings().getCfg().getString("settings.clan.rank.mod.name") + "]:", 0);
			for (ClanPlayer p : playerList) {
				if (p.getRank() == 1) {
					SendMessage.messagePlayer(player, ChatColor.WHITE + p.getPlayer() + " - " + ChatColor.GRAY + "Contributed: " + ChatColor.WHITE + "$"
							+ +ClanRaids.getConfigMan().getContribute(p.getPlayer(), id.toLowerCase()), 0);
				}
			}
			SendMessage.messagePlayer(player, ChatColor.GREEN + "[" + MultiClan.getInstance().getConfigSettings().getCfg().getString("settings.clan.rank.member.name") + "]:", 0);
			for (ClanPlayer p : playerList) {
				if (p.getRank() == 0) {
					SendMessage.messagePlayer(player, ChatColor.WHITE + p.getPlayer() + " - " + ChatColor.GRAY + "Contributed: " + ChatColor.WHITE + "$"
							+ +ClanRaids.getConfigMan().getContribute(p.getPlayer(), id.toLowerCase()), 0);
				}
			}
			SendMessage.messagePlayer(player, " ", 0);
			int daysLeft = ClanRaids.getConfigMan().getProtectionDay(id.toLowerCase());
			int timeLeft = ClanRaids.getConfigMan().getProtectionTime(id.toLowerCase());
			if (timeLeft > 0) {
				SendMessage.messagePlayer(player, ChatColor.RED + "Clan Protection: " + ChatColor.GREEN + "ON", 0);
				SendMessage.messagePlayer(player,
						ChatColor.RED + "Clan Protected For: " + ChatColor.GRAY + timeLeft / (60 * 60) + " hours " + ((timeLeft - 60 * 60 * (timeLeft / (60 * 60))) / 60) + " minutes", 0);
			} else {
				SendMessage.messagePlayer(player, ChatColor.RED + "Clan Protection: " + ChatColor.GRAY + "OFF", 0);
			}
			if (daysLeft > 0) {
				SendMessage.messagePlayer(player, ChatColor.RED + "Clan Protection Days Left: " + ChatColor.GRAY + (daysLeft - 1), 0);
			}
			SendMessage.messagePlayer(player, ChatColor.STRIKETHROUGH + "                  " + ChatColor.AQUA + "[Clan " + id + "]" + ChatColor.WHITE + ChatColor.STRIKETHROUGH + "                  ",
					0);
		} catch (NumberFormatException e) {
			SendMessage.messagePlayer(player, ChatColor.RED + "Invalid amount.", 0);
		}

		return false;
	}

}
