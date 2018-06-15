package patpower.github.clanraids.commands;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.multi.multiclan.commands.ClanCommand;
import patpower.github.clanraids.ClanRaids;
import patpower.github.clanraids.keys.RaidItems;
import patpower.github.clanraids.utils.SendMessage;

public class Forge extends Claim implements ClanCommand {

	@Override
	public boolean onCommand(Player player, String[] args) {
		if (!player.hasPermission("multiclan.forge")) {
			player.sendMessage("§cYou don't have any permission!");
			return true;
		}
		if (args.length == 2) {
			String clan = args[1].toLowerCase();
			for (ItemStack item : player.getInventory().getContents()) {
				if (item != null && item.getType() == Material.NETHER_STAR) {
					if (item.getItemMeta().getLocalizedName().toLowerCase()
							.equals(clan.toLowerCase() + " Core Fragment".toLowerCase())) {
						if (item.getAmount() >= 4) {
							player.getInventory()
									.removeItem(RaidItems.getCore(ClanRaids.getConfigMan().getClanName(clan), 4));
							RaidItems.giveRegionDisruptor(player, ClanRaids.getConfigMan().getClanName(clan));
							return true;
						}
					}
				}
			}
			SendMessage.messagePlayer(player, "You don't have enough Core Fragments to create a Region Disruptor", 0);
		} else {
			SendMessage.messagePlayer(player, "Usage: /clan forge [clan name]", 0);
		}

		return false;
	}

}
