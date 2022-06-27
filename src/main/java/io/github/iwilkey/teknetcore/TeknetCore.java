package io.github.iwilkey.teknetcore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.iwilkey.teknetcore.go.DelposCommand;
import io.github.iwilkey.teknetcore.go.GotoCommand;
import io.github.iwilkey.teknetcore.go.GotoUtils;
import io.github.iwilkey.teknetcore.go.ListposCommand;
import io.github.iwilkey.teknetcore.go.SaveposCommand;
import io.github.iwilkey.teknetcore.home.HomeCommand;
import io.github.iwilkey.teknetcore.home.HomeUtils;
import io.github.iwilkey.teknetcore.home.SetHomeCommand;
import io.github.iwilkey.teknetcore.randompos.RandomposCommand;
import io.github.iwilkey.teknetcore.utils.LogType;

public final class TeknetCore extends JavaPlugin {
	
	@Override
	public void onEnable() {
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
