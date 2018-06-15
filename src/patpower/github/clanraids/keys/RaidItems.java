package patpower.github.clanraids.keys;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RaidItems implements Listener {

	public static void dropKey(Location location, String id) {
		ItemStack item = new ItemStack(Material.NETHER_STAR, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + id + " Core Fragment");
		meta.setLocalizedName(id + " Core Fragment");
		meta.setLore(Arrays.asList("Can be forged with other core fragments", "to create a Region Disruptor for clan " + id));
		item.setItemMeta(meta);
		location.getWorld().dropItemNaturally(location, item);
	}

	public static ItemStack getCore(String id, int amount) {
		ItemStack item = new ItemStack(Material.NETHER_STAR, amount);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + id + " Core Fragment");
		meta.setLocalizedName(id + " Core Fragment");
		meta.setLore(Arrays.asList("Can be forged with other core fragments", "to create a Region Disruptor for clan " + id));
		item.setItemMeta(meta);
		return item;
	}

	public static void giveKeyExtractor(Player player) {
		ItemStack item = new ItemStack(Material.ENDER_PORTAL_FRAME, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setLocalizedName("Core Extractor");
		meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Core Extractor");
		meta.setLore(Arrays.asList("Place this next to a clan's ", "region to start extracting", "their key."));
		item.setItemMeta(meta);
		player.getInventory().addItem(item);
	}

	public static ItemStack getKeyExtractor() {
		ItemStack item = new ItemStack(Material.ENDER_PORTAL_FRAME, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setLocalizedName("Core Extractor");
		meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Core Extractor");
		meta.setLore(Arrays.asList("Place this next to a clan's ", "region to start extracting", "their key."));
		item.setItemMeta(meta);
		return item;
	}

	public static void giveRegionDisruptor(Player player, String id) {
		ItemStack item = new ItemStack(Material.BEDROCK, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "Region Disruptor for " + id);
		meta.setLocalizedName("RD_" + id);
		meta.setLore(Arrays.asList("Used to disrupt the region of clan " + id));
		item.setItemMeta(meta);
		player.getWorld().dropItemNaturally(player.getLocation(), item);
	}
	
	public static ItemStack getRegionDisruptor(String id) {
		ItemStack item = new ItemStack(Material.BEDROCK, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "Region Disruptor for " + id);
		meta.setLocalizedName("RD_" + id);
		meta.setLore(Arrays.asList("Used to disrupt the region of clan " + id));
		item.setItemMeta(meta);
		return item;
	}

	public static void giveTNT(Player player) {
		ItemStack item = new ItemStack(Material.TNT, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setLocalizedName("Region TNT");
		meta.setDisplayName(ChatColor.RED + "Region TNT");
		meta.setLore(Arrays.asList("TNT that can destroy", "blocks in protected regions.", "Can only be placed in", "unprotected land."));
		item.setItemMeta(meta);
		player.getInventory().addItem(item);
	}
}
