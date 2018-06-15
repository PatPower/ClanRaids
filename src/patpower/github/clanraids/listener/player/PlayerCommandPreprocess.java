package patpower.github.clanraids.listener.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import de.multi.multiclan.MultiClan;
import de.multi.multiclan.api.MultiClanAPI;
import de.multi.multiclan.api.MultiClanAPI.RankType;
import patpower.github.clanraids.ClanRaids;
import patpower.github.clanraids.keys.RaidItems;
import patpower.github.clanraids.utils.SendMessage;

public class PlayerCommandPreprocess implements Listener {
	MultiClanAPI clanAPI = MultiClan.getMultiClanAPI();

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		String[] parse = e.getMessage().split(" ");
		if (parse.length == 3) {
			if (parse[0].equals("/clan")) {
				if (parse[1].equals("create")) {
					Player player = e.getPlayer();
					double cost = ClanRaids.getConfigMan().clanCreateCost;
					if (clanAPI.getClan(e.getPlayer().getUniqueId()) == null) {
						if (ClanRaids.getEconomy().has(player, cost)) {
							if (parse[2].length() <= 10) {
								if (clanAPI.getClan(parse[2]) == null) {
									ClanRaids.getConfigMan().addClan(parse[2]);
									ClanRaids.getEconomy().withdrawPlayer(player, cost);
									SendMessage.messagePlayer(player,
											"$" + cost + " has been removed from your balance.", 0);
									SendMessage.messagePlayer(player,
											"You can get a half refund by doing '/clan delete'.", 0);
									return;
								} else {
									return;
								}
							} else {
								return;
							}
						} else {
							e.setCancelled(true);
							SendMessage.messagePlayer(player, "Error: Not enough funds!", 0);
							return;
						}
					}
				} else if (parse[1].equals("delete")) {
					Player player = e.getPlayer();

					if (clanAPI.getClan(player.getUniqueId()) == null || !clanAPI.getClan(player.getUniqueId())
							.getClan().toLowerCase().equals(parse[2].toLowerCase())) {
						return;
					}
					if (clanAPI.getRank(clanAPI.getClan(player.getUniqueId()).getClan().toLowerCase(),
							player.getName()) == RankType.OWNER) {
						if (delete(player)) {
							e.setCancelled(true);
						}
					}
				} else if (parse[1].equals("promote")) {
					// TODO
				}
			}
		} else if (parse.length == 2) {
			if (parse[1].equals("delete")) {
				Player player = e.getPlayer();
				MultiClanAPI clanAPI = MultiClan.getMultiClanAPI();
				if (clanAPI.getClan(player.getUniqueId()) == null) {
					return;
				}
				if (clanAPI.getRank(clanAPI.getClan(player.getUniqueId()).getClan().toLowerCase(),
						player.getName()) == RankType.OWNER) {
					if (delete(player)) {
						e.setCancelled(true);
					}
				}
				// TODO: remove
			} else if (parse[1].equals("getkey")) {
				e.setCancelled(true);
				Player player = e.getPlayer();
				RaidItems.giveKeyExtractor(player);
			} else if (parse[1].equals("gettnt")) {
				e.setCancelled(true);
				Player player = e.getPlayer();
				RaidItems.giveTNT(player);
			}
		}
	}

	private boolean delete(Player player) {
		// Check if there is protections still intact for the base
		String clan = clanAPI.getClan(player.getUniqueId()).getClan();
		if (ClanRaids.getWorldG().getRegionManager(player.getWorld()).hasRegion(clan.toLowerCase() + "_1")
				|| ClanRaids.getWorldG().getRegionManager(player.getWorld()).hasRegion(clan.toLowerCase() + "_2")) {
			SendMessage.messagePlayer(player,
					"Please remove all of the regions belonging to your clan before deleting.", 0);
			return true;
		}
		ClanRaids.getConfigMan().removeClan(clan);
		double cost = ClanRaids.getConfigMan().clanCreateCost;
		ClanRaids.getEconomy().depositPlayer(player, cost / 2);
		SendMessage.messagePlayer(player, "You have you have been refunded $" + cost / 2 + ".", 0);
		return false;
	}
}
