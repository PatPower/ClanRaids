package patpower.github.clanraids.listener.player;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.multi.multiclan.MultiClan;
import de.multi.multiclan.api.MultiClanAPI;
import patpower.github.clanraids.ClanRaids;
import patpower.github.clanraids.commands.Claim;
import patpower.github.clanraids.keys.RaidItems;
import patpower.github.clanraids.utils.RegionHelp;
import patpower.github.clanraids.utils.SendMessage;

public class PlayerPlaceBlock extends Claim implements Listener {
	MultiClanAPI clanAPI = MultiClan.getMultiClanAPI();

	@EventHandler
	public void onPlayerPlaceBlockEvent(BlockPlaceEvent event) {
		// TODO: Make it not useable on admin protections
		Player player = event.getPlayer();
		if ((event.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) && event.getBlock().getType() == Material.ENDER_PORTAL_FRAME
				&& (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLocalizedName().equals("Core Extractor"))) {
			ProtectedRegion region;
			try {
				region = RegionHelp.getRegion(event.getBlock().getLocation());
				if (region == null) {
					event.setCancelled(true);
					SendMessage.messagePlayer(player, ChatColor.YELLOW + "Place this inside a clan's region", 0);
					return;
				}
				String clan = region.getId().substring(0, region.getId().length() - 2);
				if (!ClanRaids.getThreadController().isClanRaistracted(clan)) {
					// If the person is not raiding their own clan

					String placerClan = clanAPI.getClan(player.getUniqueId()).getClan().toLowerCase();
					if ((placerClan == null) || !(region.getId().equals(placerClan + "_1") && !region.getId().equals(placerClan + "_2"))) {
						player.getInventory().removeItem(RaidItems.getKeyExtractor());
						Location loc = event.getBlock().getLocation();
						ClanRaids.getThreadController().startExtract(clan, new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()));
					} else {
						SendMessage.messagePlayer(player, ChatColor.YELLOW + "You can't raid/extract your own clan base!", 0);
						event.setCancelled(true);
						return;
					}
					return;
				} else {
					event.setCancelled(true);
					SendMessage.messagePlayer(player, ChatColor.YELLOW + "There is already a Raid/Core Extraction happening to this clan", 0);
					return;
				}
			} catch (CommandException e) {
				event.setCancelled(true);
				SendMessage.messagePlayer(player, e.getMessage(), 0);
				System.out.println(e.getMessage());
				return;
			}
		} else if ((event.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) && event.getBlock().getType() == Material.BEDROCK) {
			// Is a region disrupter
			if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLocalizedName().substring(0, 3).equals("RD_")) {
				RegionManager manager = null;
				try {
					manager = checkRegionManager(ClanRaids.getWorldG(), event.getBlock().getWorld());
				} catch (CommandException e) {
					System.out.println("ERROR");
				}
				if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLocalizedName()
						.equals("RD_" + ClanRaids.getConfigMan().getClanName(clanAPI.getClan(player.getUniqueId()).getClan()))) {
					SendMessage.messagePlayer(player, ChatColor.YELLOW + "You can't disrupt your own clan base!", 0);
					event.setCancelled(true);
					return;
				}
				// Checks the regions around the placed block to find a region
				Location loc = event.getBlock().getLocation();
				Location tempLoc;
				ProtectedRegion region = null;
				Set<ProtectedRegion> regionList = new HashSet<ProtectedRegion>();
				for (int x = -2; x <= 2; x++) {
					for (int y = -2; y <= 2; y++) {
						for (int z = -2; z <= 2; z++) {
							tempLoc = new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y, loc.getZ() + z);
							ApplicableRegionSet regions = manager.getApplicableRegions(tempLoc);
							if (regions.getRegions().size() > 0) {
								regionList.addAll(regions.getRegions());
							}
						}
					}
				}
				if (regionList.size() > 0) {
					// Look through all of the regions in the 3x3x3 area
					String clan = null;
					for (ProtectedRegion r : regionList) {
						if (r.getId().endsWith("_1")) {
							region = r;
							clan = region.getId().substring(0, region.getId().length() - 2);
							if (!event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLocalizedName().equals("RD_" + ClanRaids.getConfigMan().getClanName(clan))) {
								region = null;
							} else {
								if (ClanRaids.getThreadController().isRegionDisrupted(region.getId())) {
									region = null;
								} else {
									break;
								}
							}
						}
					}
					if (region == null) {
						for (ProtectedRegion r : regionList) {
							if (r.getId().endsWith("_2")) {
								region = r;
								clan = region.getId().substring(0, region.getId().length() - 2);
								if (!event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLocalizedName().equals("RD_" + ClanRaids.getConfigMan().getClanName(clan))) {
									region = null;
								} else {
									if (ClanRaids.getThreadController().isRegionDisrupted(region.getId())) {
										region = null;
									} else {
										break;
									}
								}
							}
						}
					}
					if (region == null) {
						event.setCancelled(true);
						SendMessage.messagePlayer(player, ChatColor.YELLOW + "You cant disrupt this region or your region disruptor does not match the region!", 0);
						return;
					}

					if (ClanRaids.getThreadController().isClanRaistracted(clan)) {
						event.setCancelled(true);
						SendMessage.messagePlayer(player, ChatColor.YELLOW + "There is already a Raid/Core Extraction happening to this clan", 0);
						return;
					}
					event.setCancelled(true);
					event.getBlock().setType(Material.BEDROCK);
					player.getInventory().removeItem(RaidItems.getRegionDisruptor(ClanRaids.getConfigMan().getClanName(clan)));
					ClanRaids.getThreadController().startRaid(clan, event.getBlock().getLocation(), region);
				} else {
					event.setCancelled(true);
					SendMessage.messagePlayer(player, ChatColor.YELLOW + "Please place this within a 2 block radius of a protected clan region.", 0);
				}
			}
		} else if (event.getPlayer().getInventory().getItemInMainHand().hasItemMeta() && event.getBlock().getType() == Material.TNT) {

			if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLocalizedName().equals("Region TNT")) {
				// TODO: Check if in their own clan
				ProtectedRegion region;
				try {
					region = RegionHelp.getRegion(event.getBlock().getLocation());
					if (region != null) {
						throw new CommandException("Cannot be placed in a protected region. Try placing it outside of the region.");
					} else {
						Location loc = event.getBlock().getLocation();
						loc.getBlock().setType(Material.AIR);
						loc.add(0.5, 1, 0.5);
						Hologram hologram = HologramsAPI.createHologram(ClanRaids.getInstance(), loc);
						loc.add(-0.5, -1, -0.5);
						new BukkitRunnable() {
							int timer = 6;

							Location tempLoc;

							@Override
							public void run() {
								try {
									if (timer == 1) {
										loc.getWorld().createExplosion(loc, 0.0F, false);
										for (int x = -1; x <= 1; x++) {
											for (int y = -1; y <= 1; y++) {
												for (int z = -1; z <= 1; z++) {
													tempLoc = new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y, loc.getZ() + z);
													// If this block is unbreakable
													if (tempLoc.getBlock().getType().equals(Material.ENDER_PORTAL_FRAME) || tempLoc.getBlock().getType().equals(Material.BEDROCK)) {
														continue;
													}
													ProtectedRegion region = RegionHelp.getRegion(tempLoc);
													// If this block is part of a region
													if (region != null) {
														// If not a clan region
														if (!region.getId().endsWith("_1") && !region.getId().endsWith("_2")) {
															continue;
														}
													}
													tempLoc.getBlock().breakNaturally();
												}
											}
										}
										hologram.delete();
										this.cancel();
										return;
									}
									timer--;

									hologram.clearLines();
									hologram.appendTextLine(ChatColor.LIGHT_PURPLE + "" + timer + "!");
									loc.getWorld().playEffect(loc, Effect.SMOKE, 50);
									loc.getWorld().playEffect(loc, Effect.CLICK1, 20);
								} catch (CommandException e) {
									event.setCancelled(true);
									SendMessage.messagePlayer(player, ChatColor.YELLOW + e.getMessage(), 0);
									return;
								}
							}
						}.runTaskTimer(ClanRaids.getInstance(), 0, 20);
					}
				} catch (CommandException e) {
					event.setCancelled(true);
					SendMessage.messagePlayer(player, ChatColor.YELLOW + e.getMessage(), 0);
					return;
				}
			}
		}
	}

}
