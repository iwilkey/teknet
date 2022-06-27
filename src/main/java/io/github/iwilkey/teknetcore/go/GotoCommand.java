package io.github.iwilkey.teknetcore.go;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.TeknetCore;
import io.github.iwilkey.teknetcore.utils.LogType;

public class GotoCommand implements CommandExecutor {
	
	private GotoUtils gotoUtils;
	
	public GotoCommand(GotoUtils gotoUtils) {
		this.gotoUtils = gotoUtils;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player)sender;
		if(args.length != 1) return false;
		try {
			int index = Integer.parseInt(args[0]) - 1;
			if(index >= gotoUtils.returnPositionsOf(player).size()) {
				TeknetCore.informPlayer(player, "This is an invalid position index!", LogType.FATAL);
				return true;
			}
			gotoUtils.gotoPostion(player, gotoUtils.returnPositionsOf(player).get(index).name);
		} catch(Exception e) {
			gotoUtils.gotoPostion(player, args[0]);
		}
		return true;
	}

}
