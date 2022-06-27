package io.github.iwilkey.teknetcore;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.iwilkey.teknetcore.home.HomeCommand;
import io.github.iwilkey.teknetcore.home.Home;
import io.github.iwilkey.teknetcore.home.SetHomeCommand;
import io.github.iwilkey.teknetcore.randompos.RandomposCommand;

public final class TeknetCore extends JavaPlugin {
	
	@Override
	public void onEnable() {
		// HomeCore...
		Home homeCore = new Home();
		getCommand("sethome").setExecutor(new SetHomeCommand(homeCore));
		getCommand("home").setExecutor(new HomeCommand(homeCore));
		// Randompos...
		getCommand("randompos").setExecutor(new RandomposCommand());
		
		getLogger().info("TeknetCore initialized!");
	}
	@Override
	public void onDisable() {
		getLogger().info("TeknetCore terminated.");
	}
	
	public static void debug(String message) {
		try {
			String send = "[TeknetCore Debugger]: " + message; 
			Bukkit.getPlayer("yoimian").sendMessage(send);
		} catch(NullPointerException e) {
			System.out.println("Player(s) not online to inform of debug.");
		}
	}

}
