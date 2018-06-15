package patpower.github.clanraids.listener.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import de.multi.multiclan.MultiClan;
import de.multi.multiclan.api.MultiClanAPI;
import patpower.github.clanraids.keys.RaidItems;

public class PlayerDeath implements Listener {
	MultiClanAPI clanAPI = MultiClan.getMultiClanAPI();

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (clanAPI.getClan(player.getUniqueId()) != null) {
			RaidItems.dropKey(player.getLocation(), clanAPI.getClan(player.getUniqueId()).getClan());
		}
	}
}
