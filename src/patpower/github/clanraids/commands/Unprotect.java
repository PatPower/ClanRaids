package patpower.github.clanraids.commands;

import org.bukkit.entity.Player;

import de.multi.multiclan.MultiClan;
import de.multi.multiclan.api.MultiClanAPI;
import de.multi.multiclan.api.MultiClanAPI.RankType;
import de.multi.multiclan.commands.ClanCommand;
import patpower.github.clanraids.ClanRaids;
import patpower.github.clanraids.utils.SendMessage;

public class Unprotect extends Claim implements ClanCommand {

	@Override
	public boolean onCommand(Player player, String[] args) {
		if (!player.hasPermission("multiclan.upgrade")) {
			player.sendMessage("§cYou don't have any permission!");
			return true;
		}
		MultiClanAPI clanAPI = MultiClan.getMultiClanAPI();
		String id = null;

		if (!clanAPI.isPlayerInClan(player.getUniqueId())) {
			SendMessage.messagePlayer(player, "You have to be an owner of a clan to unset a protection time.", 0);
			return true;
		}
		String clanName = clanAPI.getClan(player.getUniqueId()).getClan();
		// Check if player is clan leader
		if (clanAPI.getRank(clanName.toLowerCase(), player.getName()) != RankType.OWNER) {
			SendMessage.messagePlayer(player, "You have to be an owner of a clan unset protection time.", 0);
			return true;
		}
		id = clanAPI.getClan(player.getUniqueId()).getClan();
		if (ClanRaids.getThreadController().getTracker().hasProtect(id)) {
			ClanRaids.getConfigMan().removeProtection(id);
			
		}

		return false;
	}

}
