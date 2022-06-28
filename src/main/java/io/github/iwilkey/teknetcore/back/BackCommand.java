package io.github.iwilkey.teknetcore.back;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.TeknetCore;
import io.github.iwilkey.teknetcore.utils.LogType;

public class BackCommand implements CommandExecutor {
	
	private DeathUtils deathUtils;
	
	public BackCommand(DeathUtils deathUtils) {
		this.deathUtils = deathUtils;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player)sender;
		Location l = deathUtils.getPlayerLastDeathLocation(player);
		if(l == null) {
			TeknetCore.informPlayer(player, 
					"You haven't died during this server session "
					+ "so there is no place to teleport you to!", LogType.NOTICE);
			return true;
		}
		player.teleport(l);
		player.sendMessage("Try and be more careful!");
		return true;
	}

}
