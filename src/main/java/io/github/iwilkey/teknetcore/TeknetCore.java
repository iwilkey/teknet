package io.github.iwilkey.teknetcore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.iwilkey.teknetcore.announcer.AnnounceCommand;
import io.github.iwilkey.teknetcore.announcer.AnnounceUtils;
import io.github.iwilkey.teknetcore.announcer.SilentCommand;
import io.github.iwilkey.teknetcore.back.BackCommand;
import io.github.iwilkey.teknetcore.back.DeathUtils;
import io.github.iwilkey.teknetcore.eventlistener.ServerEventListener;
import io.github.iwilkey.teknetcore.go.DelposCommand;
import io.github.iwilkey.teknetcore.go.GotoCommand;
import io.github.iwilkey.teknetcore.go.GotoUtils;
import io.github.iwilkey.teknetcore.go.ListposCommand;
import io.github.iwilkey.teknetcore.go.SaveposCommand;
import io.github.iwilkey.teknetcore.home.HomeCommand;
import io.github.iwilkey.teknetcore.home.HomeUtils;
import io.github.iwilkey.teknetcore.home.SetHomeCommand;
import io.github.iwilkey.teknetcore.randompos.RandomposCommand;
import io.github.iwilkey.teknetcore.security.InfractionCommand;
import io.github.iwilkey.teknetcore.security.PoliceCommand;
import io.github.iwilkey.teknetcore.security.Security;
import io.github.iwilkey.teknetcore.security.WarningCommand;
import io.github.iwilkey.teknetcore.utils.LogType;

public final class TeknetCore extends JavaPlugin {
	
	public static float SERVER_TPS = 0;
	
	@Override
	public void onEnable() {
		
		// Listener
		ServerEventListener sel = new ServerEventListener();
		PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(sel, this);
        
        // Back
        DeathUtils deathUtils = new DeathUtils();
        getCommand("back").setExecutor(new BackCommand(deathUtils));
		
		// HomeCore...
		HomeUtils homeCore = new HomeUtils();
		getCommand("sethome").setExecutor(new SetHomeCommand(homeCore));
		getCommand("home").setExecutor(new HomeCommand(homeCore));
		
		// Randompos...
		getCommand("randompos").setExecutor(new RandomposCommand());
		
		// Goto, savepos, listpos...
		GotoUtils gotoCore = new GotoUtils();
		getCommand("savepos").setExecutor(new SaveposCommand(gotoCore));
		getCommand("listpos").setExecutor(new ListposCommand(gotoCore));
		getCommand("delpos").setExecutor(new DelposCommand(gotoCore));
		getCommand("goto").setExecutor(new GotoCommand(gotoCore));
		
		// Announcer
		getCommand("silent").setExecutor(new SilentCommand());
		getCommand("announce").setExecutor(new AnnounceCommand());
		
		// Security
		getCommand("warnings").setExecutor(new WarningCommand());
		getCommand("infractions").setExecutor(new InfractionCommand());
		getCommand("police").setExecutor(new PoliceCommand());
		
		initSyncEvents();

		getLogger().info(
			  "\n\n  12345__   _               _    _____               \n"
				+ " 2__   __! 1 1             1 !  / ____!              \n"
				+ "    | | ___| | ___ __   ___| |_1 |     ___  _ __ ___ \n"
				+ "    | |/ _ \\ |/ / '_ \\ / _ \\ __| |    / _ \\| '__/ _ \\\n"
				+ "    | |  __/   22 2 | 2  __/ |_| |___| (_) 2 | 1  __/\n"
				+ "    2_|\\___|_|\\_\\_| |_|\\___1\\__|\\_____\\___/|_|  \\___|\n"
				+ "                                                     \n\n"
				+ "                                                     ");
		Bukkit.broadcastMessage("TeknetCore has been updated. Water can now flow freely!");
	}
	
	@Override
	public void onDisable() {
		getLogger().info("TeknetCore terminated.");
	}
	
	private void initSyncEvents() {
		AnnounceUtils.init();
		new Security();
		getServer().getScheduler().scheduleSyncRepeatingTask(this, 
			new Runnable() {
				long sec, currentSec;
	            int ticks, secsSinceAnnounce = 0, secsSinceSecurity = 0;
				int ann = 0;
				public void run() {
	                sec = (System.currentTimeMillis() / 1000);
	                if(currentSec == sec) ticks++;
	                else { 
	                    currentSec = sec;
	                    SERVER_TPS = (SERVER_TPS == 0 ? ticks : ((SERVER_TPS + ticks) / 2.0f));
	                    secsSinceAnnounce++;
	                    secsSinceSecurity++;
	                    ticks = 0;
	                }
	                
	                // Security system...
	                if(secsSinceSecurity >= Security.SECURITY_CHECK_SECONDS) {
	                	for(Security.InfractionRecord rec : Security.records) {
	                		if(!Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(rec.playerName))) continue;
	                		if(rec.warnings > Security.MAX_WARNINGS_BEFORE_INFRACTION) {
	                			rec.warnings = 0;
	                			rec.notes.clear();
	                			Security.writeInfractionTo(Bukkit.getPlayer(rec.playerName), 
	                				"You have more than " + Security.MAX_WARNINGS_BEFORE_INFRACTION + " warnings "
	                						+ "so you are being written an infraction. "
	                						+ "Please refrain from the behavior that is causing warning!");
	                		}
	                		if(rec.infractions > Security.MAX_INFRACTIONS_BEFORE_KICK) {
	                			Bukkit.getPlayer(rec.playerName).kickPlayer("You have too many infractions to play right now! Check back later.");
	                		}
	                	}
	                	secsSinceSecurity = 0;
	                }
	                
	                // Announcement System...
	                if(secsSinceAnnounce >= AnnounceUtils.SECONDS_PER_ANNOUCEMENT) {
						String announcement = ChatColor.GOLD + "" + ChatColor.BOLD + "" + "[Teknet Announcement] " + ChatColor.RESET + "";
						announcement += ChatColor.ITALIC + "" + AnnounceUtils.ANNOUNCEMENTS.get(ann) + ChatColor.RESET + "";
						top: for(Player p : Bukkit.getOnlinePlayers()) {
							for(String n : AnnounceUtils.silentPref)
								if(p.getName().equals(n))
									continue top;
							p.sendMessage(announcement);
						}
						ann++;
						ann %= AnnounceUtils.ANNOUNCEMENTS.size();
						secsSinceAnnounce = 0;
	                }
				}
			}, 0l, 1l);
	}
	
	public static void informPlayer(Player player, String message, LogType type) {
		String prefix = "";
		prefix += ChatColor.BOLD + "";
		switch(type) {
			case SUCCESS: prefix += ChatColor.GREEN + ""; break;
			case NOTICE: prefix += ChatColor.YELLOW + ""; break;
			case FATAL: prefix += ChatColor.RED + ""; break;
			case UTILITY: prefix += ChatColor.BLUE + ""; break;
		}
		prefix += "[TeknetCore] " + ChatColor.RESET + "" + ChatColor.ITALIC;
		try {
			player.sendMessage(prefix + message + ChatColor.RESET + "");
		} catch(Exception e) {
			System.out.println("Error in TeknetCore.informPlayer(). Probably the user is not online.");
		}
	}

}
