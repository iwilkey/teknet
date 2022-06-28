package io.github.iwilkey.teknetcore.announcer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.TeknetCore;
import io.github.iwilkey.teknetcore.utils.LogType;

public class AnnounceCommand implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player)sender;
		if(!AnnounceUtils.isSilent(player.getName())) {
			TeknetCore.informPlayer(player, "You're already receiving all server-wide annoucements!", LogType.UTILITY);
			return true;
		}
		AnnounceUtils.silentPref.remove(player.getName());
		TeknetCore.informPlayer(player, "You are now receiving all server-wide annoucements.", LogType.SUCCESS);
		return true;
	}
}
