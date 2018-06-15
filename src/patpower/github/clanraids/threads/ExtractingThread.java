package patpower.github.clanraids.threads;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import patpower.github.clanraids.ClanRaids;
import patpower.github.clanraids.ConfigManager;
import patpower.github.clanraids.keys.RaidItems;
import patpower.github.clanraids.utils.SendMessage;

public class ExtractingThread extends BukkitRunnable {

	private String clan;
	private Location loc;
	ConfigManager conMan = ClanRaids.getConfigMan();
	Hologram hologram;
	private int currTime, counter = 0;
	private int hp = 100;

	public ExtractingThread(String clan, Location loc) {
		this.clan = clan;
		this.loc = loc;
		this.loc.add(0.5, 2, 0.5);
		hologram = HologramsAPI.createHologram(ClanRaids.getInstance(), this.loc);
	}

	@Override
	public void run() {
		currTime = conMan.decreaseExtractTimer(clan.toLowerCase());
		if (currTime <= 0) {
			RaidItems.dropKey(loc, conMan.getClanName(clan));
			endExtract();
		} else {
			new Location(loc.getWorld(), loc.getX(), loc.getY()-2, loc.getZ()).getBlock().setType(Material.ENDER_PORTAL_FRAME);
			if (currTime <= 10) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (loc.distance(player.getLocation()) <= 100) {
						SendMessage.messagePlayer(player, "Time left: " + currTime, 0);
					}
				}
			}
			hologram.clearLines();
			if (counter == 0) {
				hologram.appendTextLine("Time remaining: " + currTime);
				if (hp < 100) {
					if (hp + 5 > 100) {
						hp = 100;
					} else {
						hp += 5;
					}
				}
			} else {
				counter--;
				hologram.appendTextLine("Time remaining: " + currTime);
				hologram.appendTextLine(ChatColor.AQUA + "Regen in: " + counter);
			}
			showHealth(hologram);
		}

	}

	public void damage(int dmg) {
		if ((hp - dmg) <= 0) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (loc.distance(player.getLocation()) <= 100) {
					SendMessage.messagePlayer(player, ChatColor.LIGHT_PURPLE + "Extractor has been broken!", 0);
				}
			}
			endExtract();
			return;
		} else {
			counter = 10;
			hp = hp - dmg;
			hologram.clearLines();
			hologram.appendTextLine("Time remaining: " + currTime);
			hologram.appendTextLine(ChatColor.AQUA + "Regen in: " + counter);
			showHealth(hologram);
		}
	}

	private void showHealth(Hologram hologram) {
		if (hp >= 80) {
			hologram.appendTextLine(ChatColor.GREEN + "Health: " + hp);
		} else if (hp >= 30) {
			hologram.appendTextLine(ChatColor.YELLOW + "Health: " + hp);
		} else if (hp >= 0) {
			hologram.appendTextLine(ChatColor.RED + "Health: " + hp);
		}
	}

	private void endExtract() {
		this.cancel();
		conMan.removeExtractTime(clan);
		// Remove from list too
		hologram.delete();
		loc.add(-0.5, -2, -0.5).getBlock().breakNaturally();
		ClanRaids.getThreadController().removeExtract(clan);
	}
}
