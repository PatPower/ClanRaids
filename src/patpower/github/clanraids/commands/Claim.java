package patpower.github.clanraids.commands;

import javax.naming.ConfigurationException;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.commands.task.RegionAdder;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.util.DomainInputResolver.UserLocatorPolicy;

import de.multi.multiclan.MultiClan;
import de.multi.multiclan.api.MultiClanAPI;
import de.multi.multiclan.api.MultiClanAPI.RankType;
import de.multi.multiclan.commands.ClanCommand;
import patpower.github.clanraids.ClanRaids;
import patpower.github.clanraids.utils.SendMessage;

public class Claim implements ClanCommand {

	public boolean onCommand(Player player, String[] args) {
		if (!player.hasPermission("multiclan.claim")) {
			player.sendMessage("§cYou don't have any permission!");
			return true;
		}

		WorldGuardPlugin plugin = ClanRaids.getWorldG();
		MultiClanAPI clanAPI = MultiClan.getMultiClanAPI();
		String id = null;
		RegionManager manager = null;
		ProtectedRegion region = null;
		LocalPlayer localPlayer;

		if (!clanAPI.isPlayerInClan(player.getUniqueId())) {
			SendMessage.messagePlayer(player, ChatColor.YELLOW + "You have to be an owner of a clan to claim a region.", 0);
			return true;
		}
		String clanName = clanAPI.getClan(player.getUniqueId()).getClan();
		// Check if player is clan leader
		if (clanAPI.getRank(clanName.toLowerCase(), player.getName()) != RankType.OWNER) {
			SendMessage.messagePlayer(player, ChatColor.YELLOW + "You have to the clan owner to claim a region.", 0);
			return true;
		}
		if (ClanRaids.getThreadController().isClanBusy(clanName.toLowerCase())) {
			SendMessage.messagePlayer(player, ChatColor.YELLOW + "You cannot modify your regions while being raided/extracted.", 0);
			return true;
		}
		try {
			if (args.length == 1 || args[1].equals("1")) {
				localPlayer = plugin.wrapPlayer(player);

				id = checkRegionId(clanName, false);

				manager = checkRegionManager(plugin, player.getWorld());

				region = checkRegionFromSelection(player, id + "_1");

				// We have to check whether this region violates the space of any other region
				ApplicableRegionSet regions = manager.getApplicableRegions(region);

				// Check if this region overlaps any other region
				if (regions.size() > 0) {
					if (manager.hasRegion(id + "_2")) {
						for (ProtectedRegion r : regions.getRegions()) {
							if (r.getId().equals(id.toLowerCase() + "_2")) {
								break;
							}
							throw new CommandException("This has to overlapping with your second clan region.");
						}
					}
					if (!regions.isMemberOfAll(localPlayer)) {
						throw new CommandException("This region overlaps with someone else's region.");
					}
				}
				// Gets the volume given the clan name
				int regionOneArea = getTierVolume(id);

				// Keep just in case
				if (regionOneArea >= Integer.MAX_VALUE) {
					throw new CommandException("The maximum claim volume get in the configuration is higher than is supported. " + "Currently, it must be " + Integer.MAX_VALUE
							+ " or smaller. Please contact a server administrator.");
				}

				// Check claim volume
				if (region.volume() > regionOneArea) {
					SendMessage.messagePlayer(player, ChatColor.YELLOW + "This region is too large to claim.", 0);
					SendMessage.messagePlayer(player, ChatColor.YELLOW + "Max. volume: " + regionOneArea + ", your volume: " + region.volume(), 0);
					return true;
				}
				region.setFlag(DefaultFlag.GREET_MESSAGE, ChatColor.RED + "[ClanRaids] " + ChatColor.WHITE + "Entering the first region of clan " + id);
				region.setFlag(DefaultFlag.PVP, com.sk89q.worldguard.protection.flags.StateFlag.State.ALLOW);
				region.setFlag(DefaultFlag.DENY_MESSAGE, "");
				region.setPriority(1);
				if (manager.hasRegion(id + "_1")) {
					SendMessage.messagePlayer(player, "Your clan region has been updated!", 0);
					manager.removeRegion(id + "_1");
				} else {
					SendMessage.messagePlayer(player, "Your clan region has been created!", 0);
				}
				ClanRaids.getConfigMan().setFirstRegionSet(id.toLowerCase(), true);
			} else if (args[1].equals("2")) {

				localPlayer = plugin.wrapPlayer(player);

				id = checkRegionId(clanName, false);

				manager = checkRegionManager(plugin, player.getWorld());

				region = checkRegionFromSelection(player, id + "_2");

				// We have to check whether this region violates the space of any other region
				ApplicableRegionSet regions = manager.getApplicableRegions(region);

				// Check if the first region was created
				if (!ClanRaids.getConfigMan().isFirstRegionSet(id.toLowerCase())) {
					throw new CommandException("You have to set your first region before you set this one.");
				}
				boolean containsFirst = false;
				// Check if this region overlaps any other region
				if (regions.size() > 0) {
					for (ProtectedRegion r : regions.getRegions()) {
						if (r.getId().equals(id.toLowerCase() + "_1")) {
							if (!regions.isMemberOfAll(localPlayer)) {
								throw new CommandException("This region overlaps with someone else's region.");
							}
							containsFirst = true;
						}
					}
					if (!containsFirst) {
						throw new CommandException("This has to be in your first clan region.");
					}
				} else {
					throw new CommandException("This has to be in your first clan region.");
				}

				int regionTwoArea = getTierVolume(id) / 10;

				// Keep just in case
				if (regionTwoArea >= Integer.MAX_VALUE) {
					throw new CommandException("The maximum claim volume get in the configuration is higher than is supported. " + "Currently, it must be " + Integer.MAX_VALUE
							+ " or smaller. Please contact a server administrator.");
				}

				// Check claim volume; less than the first one
				if (region.volume() > regionTwoArea) {
					SendMessage.messagePlayer(player, ChatColor.RED + "This region is too large to claim.", 0);
					SendMessage.messagePlayer(player, ChatColor.RED + "Max. volume: " + regionTwoArea + ", your volume: " + region.volume(), 0);
					return true;
				}
				region.setPriority(10);
				region.setFlag(DefaultFlag.GREET_MESSAGE, ChatColor.RED + "[ClanRaids] " + ChatColor.WHITE + "Entering the second region of clan " + id);
				region.setFlag(DefaultFlag.DENY_MESSAGE, "");
				region.setFlag(DefaultFlag.PVP, com.sk89q.worldguard.protection.flags.StateFlag.State.ALLOW);
				if (manager.hasRegion(id + "_2")) {
					SendMessage.messagePlayer(player, "Your clan region has been updated!", 0);
					manager.removeRegion(id + "_2");
				} else {
					SendMessage.messagePlayer(player, "Your clan region has been created!", 0);
				}

			} else {
				return true;
			}
		} catch (CommandException e) {
			SendMessage.messagePlayer(player, ChatColor.YELLOW + e.getMessage(), 0);
			return true;
		} catch (ConfigurationException e) {
			SendMessage.messagePlayer(player, ChatColor.YELLOW + e.getMessage(), 0);
			return true;
		}
		DefaultDomain newList = region.getMembers();
		newList.addPlayer(player.getUniqueId());
		RegionAdder task = new RegionAdder(plugin, manager, region);
		task.setLocatorPolicy(UserLocatorPolicy.UUID_ONLY);

		RegionContainer container = plugin.getRegionContainer();
		RegionManager regions = container.get(player.getWorld());
		regions.addRegion(region);
		return false;
	}

	protected static RegionManager checkRegionManager(WorldGuardPlugin plugin, World world) throws CommandException {
		if (!plugin.getGlobalStateManager().get(world).useRegions) {
			throw new CommandException("Region support is disabled in the target world. " + "It can be enabled per-world in WorldGuard's configuration files. "
					+ "However, you may need to restart your server afterwards.");
		}

		RegionManager manager = plugin.getRegionContainer().get(world);
		if (manager == null) {
			throw new CommandException("Region data failed to load for this world. " + "Please ask a server administrator to read the logs to identify the reason.");
		}
		return manager;
	}

	/**
	 * Create a {@link ProtectedRegion} from the player's selection.
	 *
	 * @param player
	 *            the player
	 * @param id
	 *            the ID of the new region
	 * @return a new region
	 * @throws CommandException
	 *             thrown on an error
	 */
	protected static ProtectedRegion checkRegionFromSelection(Player player, String id) throws CommandException {
		Selection selection = checkSelection(player);
		// Detect the type of region from WorldEdit
		if (selection instanceof Polygonal2DSelection) {
			Polygonal2DSelection polySel = (Polygonal2DSelection) selection;
			int minY = polySel.getNativeMinimumPoint().getBlockY();
			int maxY = polySel.getNativeMaximumPoint().getBlockY();
			return new ProtectedPolygonalRegion(id, polySel.getNativePoints(), minY, maxY);
		} else if (selection instanceof CuboidSelection) {
			BlockVector min = selection.getNativeMinimumPoint().toBlockVector();
			BlockVector max = selection.getNativeMaximumPoint().toBlockVector();
			return new ProtectedCuboidRegion(id, min, max);
		} else {
			throw new CommandException("Sorry, you can only use cuboids and polygons for regions.");
		}
	}

	/**
	 * Get a WorldEdit selection for a player, or emit an exception if there is none
	 * available.
	 *
	 * @param player
	 *            the player
	 * @return the selection
	 * @throws CommandException
	 *             thrown on an error
	 */
	protected static Selection checkSelection(Player player) throws CommandException {
		WorldEditPlugin worldEdit = WorldGuardPlugin.inst().getWorldEdit();
		Selection selection = worldEdit.getSelection(player);

		if (selection == null) {
			throw new CommandException("Please select an area first. " + "Use WorldEdit to make a selection! " + "(wiki: http://wiki.sk89q.com/wiki/WorldEdit).");
		}

		return selection;
	}

	protected static String checkRegionId(String id, boolean allowGlobal) throws CommandException {
		if (!ProtectedRegion.isValidId(id)) {
			throw new CommandException("The region name of '" + id + "' contains characters that are not allowed.");
		}

		if (!allowGlobal && id.equalsIgnoreCase("__global__")) { // Sorry, no global
			throw new CommandException("Sorry, you can't use __global__ here.");
		}

		return id;
	}

	protected int getTierVolume(String clan) {
		// TODO: Make this config friendly
		int tier = ClanRaids.getConfigMan().getClanTier(clan);
		if (tier == 0) {
			return 10000;
		} else if (tier == 1) {
			return 20000;
		} else if (tier == 2) {
			return 30000;
		} else if (tier == 3) {
			return 40000;
		} else if (tier == 4) {
			return 50000;
		} else {
			return -1;
		}
	}
}