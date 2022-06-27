package io.github.iwilkey.teknetcore.home;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.TeknetCore;
import io.github.iwilkey.teknetcore.utils.LogType;

public class HomeCommand implements CommandExecutor {
	
	private final HomeUtils homeCore;

    public HomeCommand(HomeUtils homeCore) {
        this.homeCore = homeCore;
    }
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player)sender;
		String name = player.getName();
		if(!homeCore.hasHome(name)) 
			TeknetCore.informPlayer(player, "You do not have a home to return to! Please use '/sethome' to set your home.", LogType.FATAL);
		else {
			homeCore.teleportHome(player, name);
			player.sendMessage("Welcome home.");
		}
		return true;
	}
	
}
