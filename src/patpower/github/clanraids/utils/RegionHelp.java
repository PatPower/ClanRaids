package patpower.github.clanraids.utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import patpower.github.clanraids.ClanRaids;
import patpower.github.clanraids.commands.Claim;

public class RegionHelp extends Claim {

	public static ProtectedRegion getRegion(Location loc) throws CommandException {
		RegionManager manager = null;
		ProtectedRegion region = null;
		try {
			manager = checkRegionManager(ClanRaids.getWorldG(), loc.getWorld());
		} catch (CommandException e) {
			System.out.println("ERROR");
		}
		ApplicableRegionSet regions = manager.getApplicableRegions(loc);
		if (regions.getRegions().size() > 0) {
			for (ProtectedRegion r : regions.getRegions()) {
				if (r.getId().endsWith("_1")) {
					region = r;
					break;
				}
			}
			if (region == null) {
				for (ProtectedRegion r : regions.getRegions()) {
					if (r.getId().endsWith("_2")) {
						region = r;
						break;
					}
				}
			}
			if (region == null) {
				for (ProtectedRegion r : regions.getRegions()) {
					// If an admin region
					if (!r.getId().endsWith("_1") && !r.getId().endsWith("_2")) {
						region = r;
						break;
					}
				}
			}
			if (region == null) {
				throw new CommandException(ChatColor.RED + "Something has gone wrong! Please message the admins about this!");
			}
			return region;
		} else {
			return null;
		}
	}
}
