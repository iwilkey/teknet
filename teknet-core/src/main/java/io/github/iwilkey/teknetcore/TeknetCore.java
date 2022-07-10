package io.github.iwilkey.teknetcore;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.iwilkey.teknetcore.cooldown.Cooldown;
import io.github.iwilkey.teknetcore.eventlistener.ServerEventListener;
import io.github.iwilkey.teknetcore.location.Locations;
import io.github.iwilkey.teknetcore.ranks.Ranks;
import io.github.iwilkey.teknetcore.ranks.Ranks.Rank;
import io.github.iwilkey.teknetcore.utils.PlayerServerUtilities;
import io.github.iwilkey.teknetcore.utils.SoundUtilities;

public final class TeknetCore extends JavaPlugin {
	public static float SERVER_TPS = 20;

	@Override
	public void onEnable() {
        loadResources();
        registerCommands();
        clock();
		getLogger().info(
			  "\n\n  1234567   _               _    _____               \n"
				+ " 289   34! 1 1             1 !  / ____!              \n"
				+ "    | | ___| | ___ __   ___| |_1 |     ___  _ __ ___ \n"
				+ "    | |/ _ \\ |/ / '_ \\ / _ \\ __| |    / _ \\| '__/ _ \\\n"
				+ "    | |  __/   22 2 | 2  __/ |_| |___| (_) 2 | 1  __/\n"
				+ "    2_|\\___|_|\\_\\_| |_|\\___1\\__|\\_____\\___/|_|  \\___|\n"
				+ "                                                     \n\n"
				+ "                                                     ");
	}
	
	@Override
	public void onDisable() {
		getLogger().info("TeknetCore terminated.");
	}
	
	private void loadResources() {
		ServerEventListener sel = new ServerEventListener();
		PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(sel, this);
        new Cooldown();
        new SoundUtilities();
        new Ranks();
        new Locations();
	}
	
	private void registerCommands() {
		// PlayerServerUtilities
		getCommand("lag").setExecutor(new PlayerServerUtilities.Lag(Rank.HOBBYIST));
		getCommand("cooldown").setExecutor(new PlayerServerUtilities.CooldownCommand(Rank.HOBBYIST));
		
		// Location
		getCommand("home").setExecutor(new Locations.Home.HomeCommand(Rank.HOBBYIST));
		getCommand("sethome").setExecutor(new Locations.Home.SetHome(Rank.HOBBYIST));
		getCommand("position").setExecutor(new Locations.Positions.PositionCommand(Rank.HOBBYIST));
		
		// Admin utilities
		getCommand("ranks").setExecutor(new Ranks.AdminRankUtilities(Rank.ADMIN));
	}
	
	private void clock() {
		getServer().getScheduler().scheduleSyncRepeatingTask(this, 
			new Runnable() {
				long now, last, delta = 0;
	            int ticks;
				public void run() {
					last = now;
					now = System.nanoTime();
					delta += now - last;
					ticks++;
					if(delta >= 1000000000) {
						SERVER_TPS = ticks;
						delta = 0;
						ticks = 0;
					}
					Cooldown.tick();
					Locations.tick();
				}
			}, 0l, 1l);
	}
}
