package patpower.github.clanraids.threads;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import patpower.github.clanraids.ClanRaids;
import patpower.github.clanraids.ConfigManager;
import patpower.github.clanraids.utils.SendMessage;

public class RegionDisruptThread extends BukkitRunnable {

	private String clan;
	private ProtectedRegion region;
	private Location loc;
	private int counter;
	Hologram hologram;
	ConfigManager conMan = ClanRaids.getConfigMan();

	public RegionDisruptThread(String clan, ProtectedRegion region, Location loc) {
		this.clan = clan;
		this.region = region;
		this.loc = loc;
		region.setFlag(DefaultFlag.BUILD, State.ALLOW);
		hologram = HologramsAPI.createHologram(ClanRaids.getInstance(), this.loc);
	}

	@Override
	public void run() {
		counter = ClanRaids.getConfigMan().regionDisruptCounter(region.getId());
		if (counter == 0) {
			endDisruput();
		} else {
			if (counter <= 60) {
				if (counter == 60) {
					SendMessage.messageLocalPlayers(loc, ChatColor.LIGHT_PURPLE + "" + "1 minuite left till region "
							+ region.getId() + " reactivates.");
				} else if (counter == 30) {
					SendMessage.messageLocalPlayers(loc, ChatColor.LIGHT_PURPLE + "" + "30 seconds left till region "
							+ region.getId() + " reactivates.");
				} else if (counter <= 10) {
					SendMessage.messageLocalPlayers(loc, ChatColor.LIGHT_PURPLE + "" + counter
							+ " seconds left till region " + region.getId() + " reactivates.");
				}
			} else if (counter % 60 == 0) {
				SendMessage.messageLocalPlayers(loc, ChatColor.LIGHT_PURPLE + "" + (counter / 60)
						+ " minutes left till region " + region.getId() + " reactivates.");
			}
			hologram.clearLines();
			hologram.appendTextLine(ChatColor.LIGHT_PURPLE + "Region: " + region.getId());
			hologram.appendTextLine(ChatColor.LIGHT_PURPLE + "Distruption time left: " + counter);
		}
	}

	private void endDisruput() {
		SendMessage.messageLocalPlayers(loc, "Region " + region.getId() + " has been re-enabled!");
		region.setFlag(DefaultFlag.BUILD, null);
		ClanRaids.getThreadController().endRegionDisrupt(region.getId(), clan);
		hologram.delete();
		this.cancel();
		conMan.removeDisruptTime(region.getId());
	}
}
