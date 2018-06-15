package patpower.github.clanraids.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.multi.multiclan.MultiClan;
import de.multi.multiclan.api.MultiClanAPI;
import de.multi.multiclan.clan.ClanPlayer;

public class SendMessage {
	/**
	 * Handles the player messaging
	 * 
	 * @param m
	 * @param type:
	 *            0 for ClanRaids
	 */
	public static void messagePlayer(Player player, String m, int type) {
		if (type == 0) {
			player.sendMessage(ChatColor.RED + "[ClanRaids] " + ChatColor.WHITE + m);
		}
	}
	
	public static void messageLocalPlayers(Location loc, String m) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (loc.distance(player.getLocation()) <= 100) {
				SendMessage.messagePlayer(player, m, 0);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void messageClan(String id, String m) {
		MultiClanAPI clanAPI = MultiClan.getMultiClanAPI();
		for (ClanPlayer p : clanAPI.getClan(id).getPlayer()) {
			if (Bukkit.getOfflinePlayer(p.getPlayer()).isOnline()) {
				messagePlayer(Bukkit.getPlayer(p.getPlayer()), m, 0);
			}
		}
	}

}
