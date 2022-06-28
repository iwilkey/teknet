package io.github.iwilkey.teknetcore.security;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfractionCommand implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Security.inform((Player)sender, "You have " 
		   + Security.infractionRecordOf((Player)sender).infractions + 
		   " infraction(s). If you get " + (Security.MAX_INFRACTIONS_BEFORE_KICK - 
				   Security.infractionRecordOf((Player)sender).infractions + 1) + " more infraction(s) you will by kicked and possibly banned!");
		return true;
	}
}
