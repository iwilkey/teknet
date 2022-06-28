package io.github.iwilkey.teknetcore.security;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.TeknetCore;
import io.github.iwilkey.teknetcore.utils.LogType;

public class WarningCommand implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		if(args.length == 0) {
			Security.inform((Player)sender, "You have " 
			   + Security.infractionRecordOf((Player)sender).warnings + 
			   " warning(s). If you get " + (Security.MAX_WARNINGS_BEFORE_INFRACTION - 
					   Security.infractionRecordOf((Player)sender).warnings + 1) + " more warning(s) you will recieve an infraction!");
		} else if(args.length == 1 && args[0].equals("why")) {
			TeknetCore.informPlayer((Player)sender, "Here are the reasons for your current warnings...", LogType.UTILITY);
			Security.InfractionRecord record = Security.infractionRecordOf((Player)sender);
			int i = 0;
			for(String line : record.notes) {
				((Player)sender).sendMessage("   [" + i + "] " + line);
				i++;
			}
		} else return false;
		return true;
	}
}
