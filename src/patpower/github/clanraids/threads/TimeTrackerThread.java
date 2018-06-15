package patpower.github.clanraids.threads;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import patpower.github.clanraids.ClanRaids;
import patpower.github.clanraids.utils.SendMessage;

public class TimeTrackerThread extends BukkitRunnable {

	private ArrayList<String> currProtectionClans;
	private HashMap<String, Integer> timeOfProtections;

	public TimeTrackerThread() {
		currProtectionClans = ClanRaids.getConfigMan().getSettofProcTime();
		timeOfProtections = ClanRaids.getConfigMan().getHashMapOfTime();
	}

	@Override
	public void run() {
		DateFormat dateformat = new SimpleDateFormat("mm:ss");
		DateFormat hourformat = new SimpleDateFormat("HH");
		Date date = new Date();
		if (dateformat.format(date).equals("00:00")) {
			for (String clan : timeOfProtections.keySet()) {
				if (getStringHour(timeOfProtections.get(clan)).equals(hourformat.format(date))) {
					currProtectionClans.add(clan);
					ClanRaids.getConfigMan().setProtectionDay(clan);
					ClanRaids.getConfigMan().decProtectionTimer(clan);
					ClanRaids.getConfigMan().setProtected(clan, true);
					SendMessage.messageClan(clan, ChatColor.AQUA + "Your clan protection is now activated for the next 7 hours.");
				}
			}

		}
		for (String clan : currProtectionClans) {
			int timeLeft = ClanRaids.getConfigMan().decProtectionTimer(clan);
			if (timeLeft <= 0) {
				// END Protection
				ClanRaids.getConfigMan().removeProtectionTimer(clan);
				ClanRaids.getConfigMan().setProtected(clan, false);
				SendMessage.messageClan(clan, ChatColor.AQUA + "Your clan protection has now ended!.");
				if (ClanRaids.getConfigMan().decProtectionDay(clan) <= 0) {
					// Week of protection is over
					ClanRaids.getConfigMan().removeProtection(clan);
					timeOfProtections.remove(clan);
					SendMessage.messageClan(clan, ChatColor.AQUA + "Your 7 days of protection has also ended!");
				}
			}
		}
		currProtectionClans = ClanRaids.getConfigMan().getSettofProcTime();
	}

	public boolean addProtection(String clan, int time) {
		if (!timeOfProtections.containsKey(clan)) {
			ClanRaids.getConfigMan().setProtectionTime(clan, time);
			timeOfProtections.put(clan, time);
			SendMessage.messageClan(clan, "The clan protection now starts at " + time + ":00 EST (24 HR)");
			return false;
		} else {
			return true;
		}
	}

	public boolean hasProtect(String clan) {
		if (timeOfProtections.containsKey(clan)) {
			return true;
		} else {
			return false;
		}
	}

	private String getStringHour(int i) {
		if (i == 0) {
			return "00";
		} else if (i == 1) {
			return "01";
		} else if (i == 2) {
			return "02";
		} else if (i == 3) {
			return "03";
		} else if (i == 4) {
			return "04";
		} else if (i == 5) {
			return "05";
		} else if (i == 6) {
			return "06";
		} else if (i == 7) {
			return "07";
		} else if (i == 8) {
			return "08";
		} else if (i == 9) {
			return "09";
		} else if (i == 10) {
			return "10";
		} else if (i == 11) {
			return "11";
		} else if (i == 12) {
			return "12";
		} else if (i == 13) {
			return "13";
		} else if (i == 14) {
			return "14";
		} else if (i == 15) {
			return "15";
		} else if (i == 16) {
			return "16";
		} else if (i == 17) {
			return "17";
		} else if (i == 18) {
			return "18";
		} else if (i == 19) {
			return "19";
		} else if (i == 20) {
			return "20";
		} else if (i == 21) {
			return "21";
		} else if (i == 22) {
			return "22";
		} else if (i == 23) {
			return "23";
		} else if (i == 24) {
			return "24";
		} else {
			System.out.println("Invalid Time!");
			return null;
		}
	}
}
