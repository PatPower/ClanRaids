package patpower.github.clanraids.listener.player;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.multi.multiclan.MultiClan;
import de.multi.multiclan.api.MultiClanAPI;
import de.multi.multiclan.api.MultiClanAPI.RankType;
import de.multi.multiclan.clan.ClanPlayer;
import de.multi.multiclan.database.local.ConfigSettings;
import patpower.github.clanraids.ClanRaids;
import patpower.github.clanraids.utils.RegionHelp;
import patpower.github.clanraids.utils.SendMessage;

public class PlayerInteract implements Listener {

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		try {
			if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {

				if (event.getClickedBlock().getType() == Material.BEDROCK) {
					event.setCancelled(true);
					ClanRaids.getThreadController().damageDisruptor(event.getClickedBlock().getLocation(), 1);
				}
				if (event.getClickedBlock().getType() == Material.ENDER_PORTAL_FRAME) {
					event.setCancelled(true);
					ProtectedRegion region = RegionHelp.getRegion(event.getClickedBlock().getLocation());
					String clan = region.getId().substring(0, region.getId().length() - 2);
					ClanRaids.getThreadController().damageExtractor(clan, 1);
				}

				return;
			}
		} catch (CommandException e) {
			event.setCancelled(true);
			SendMessage.messagePlayer(event.getPlayer(), e.getMessage(), 0);
			System.out.println(e.getMessage());
		}
		if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.GOLD_AXE) {
			Player player = event.getPlayer();
			MultiClanAPI clanAPI = MultiClan.getMultiClanAPI();
			if (event.getAction().equals(Action.LEFT_CLICK_AIR)) {
				if (ClanRaids.getEconomy().has(player, 2)) {

					ConfigSettings cfg = new ConfigSettings();
					player.sendMessage("User added " + cfg.getCfg().getString("settings.clan.rank.owner.name"));
				} else {
					SendMessage.messagePlayer(event.getPlayer(), "You dont got enough.", 0);
				}
				return;
			} else {
				List<ClanPlayer> list = clanAPI.getClan("ice").getPlayer();
				list.add(new ClanPlayer("Fred", 0));
				clanAPI.getClan("ice").setPlayer(list);
				ClanRaids.getEconomy().withdrawPlayer(player, 2);
				player.sendMessage("User added " + RankType.MOD.toString());
			}
		}
	}
}
