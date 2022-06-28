package io.github.iwilkey.teknetcore.announcer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.TeknetCore;
import io.github.iwilkey.teknetcore.utils.LogType;

public class SilentCommand implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player)sender;
		if(AnnounceUtils.isSilent(player.getName())) {
			TeknetCore.informPlayer(player, 
					"You have already opted for silent announcements! Use '/announce' to see them again. ", 
					LogType.UTILITY);
			return true;
		}
		AnnounceUtils.silentPref.add(player.getName());
		TeknetCore.informPlayer(player, "Okay, we'll silent those announcements for you!", LogType.SUCCESS);
		return true;
	}
}
