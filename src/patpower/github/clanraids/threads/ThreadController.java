package patpower.github.clanraids.threads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import patpower.github.clanraids.ClanRaids;

public class ThreadController implements Listener {
	Map<String, ExtractingThread> extractList = new HashMap<String, ExtractingThread>();
	Map<String, RaidingThread> raidingList = new HashMap<String, RaidingThread>();
	Map<String, RegionDisruptThread> disruptList = new HashMap<String, RegionDisruptThread>();
	Map<Location, RaidingThread> locDisruptMap = new HashMap<Location, RaidingThread>();
	ArrayList<String> listOfDisrupted = new ArrayList<String>();
	Plugin plugin = ClanRaids.getInstance();
	TimeTrackerThread tracker;

	public ThreadController() {
		tracker = new TimeTrackerThread();
		tracker.runTaskTimer(plugin, 0L, 20L);
	}

	public TimeTrackerThread getTracker() {
		return tracker;
	}

	public void startExtract(String clan, Location loc) {
		ExtractingThread eThread = new ExtractingThread(clan, loc);
		eThread.runTaskTimer(plugin, 0, 20);
		extractList.put(clan.toLowerCase(), eThread);
	}

	public void startRegionDisrupt(ProtectedRegion region, String clan, Location loc) {
		RegionDisruptThread dThread = new RegionDisruptThread(clan, region, loc);
		dThread.runTaskTimer(plugin, 0, 20);
		disruptList.put(region.getId(), dThread);
		listOfDisrupted.add(clan.toLowerCase());
	}

	public void endRegionDisrupt(String region, String clan) {
		if (disruptList.containsKey(region)) {
			disruptList.remove(region);
		} else {
			System.out.println("Error: No disrupt found");
		}
		if (listOfDisrupted.contains(clan)) {
			listOfDisrupted.remove(clan);
		} else {
			System.out.println("Error: No disrupt clan found");
		}
	}

	public boolean isRegionDisrupted(String region) {
		return disruptList.containsKey(region);
	}

	public void startRaid(String clan, Location loc, ProtectedRegion region) {
		RaidingThread rThread = new RaidingThread(clan, loc, region);
		rThread.runTaskTimer(plugin, 0, 20);
		raidingList.put(clan.toLowerCase(), rThread);
		locDisruptMap.put(loc, rThread);
	}

	public void damageExtractor(String clan, int dmg) {
		if (extractList.containsKey(clan)) {
			extractList.get(clan).damage(dmg);
		} else {
			System.out.println("Error: No extractor found");
		}
	}

	public void damageDisruptor(String clan, int dmg) {
		if (raidingList.containsKey(clan)) {
			raidingList.get(clan).damage(dmg);
		} else {
			System.out.println("Error: No raid found");
		}
	}

	public void damageDisruptor(Location loc, int dmg) {
		loc.add(0.5, 2.5, 0.5);
		if (locDisruptMap.containsKey(loc)) {
			locDisruptMap.get(loc).damage(dmg);
		} else {
			System.out.println("Error: No dis found");
		}
	}

	public boolean isClanBusy(String clan) {
		if (extractList.containsKey(clan) || raidingList.containsKey(clan) || listOfDisrupted.contains(clan)) {
			return true;
		} else {
			return false;
		}
	}

	// Either raided or Extracted
	public boolean isClanRaistracted(String clan) {
		if (extractList.containsKey(clan) || raidingList.containsKey(clan)) {
			return true;
		} else {
			return false;
		}
	}

	public void removeExtract(String clan) {
		if (extractList.containsKey(clan)) {
			extractList.remove(clan);
		} else {
			System.out.println("Error: Does not contain in extract list!");
		}
	}

	public void removeRaid(String clan, Location loc) {
		if (raidingList.containsKey(clan)) {
			raidingList.remove(clan);
		} else {
			System.out.println("Error: Does not contain in raiding list!");
		}
		loc.add(0.5, 2.5, 0.5);
		if (locDisruptMap.containsKey(loc)) {
			locDisruptMap.remove(loc);
		} else {
			System.out.println("Error: No dis map clan found");
		}
	}
}
