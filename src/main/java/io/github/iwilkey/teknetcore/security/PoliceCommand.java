package io.github.iwilkey.teknetcore.security;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PoliceCommand implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		if(args.length < 2) return false;
		if(!Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[0]))) {
			Security.inform((Player)sender, "The player you have specified is either not online or doesn't exist. "
					+ "Check your spelling and in the meantime, we still logged this complaint for review.", false);
			return true;
		}
		Player target = Bukkit.getPlayer(args[0]);
		String message = "";
		for(int i = 1; i < args.length; i++)
			message += args[i] + " ";
		Security.policePlayer((Player)sender, target, message);
		return true;
	}
}
