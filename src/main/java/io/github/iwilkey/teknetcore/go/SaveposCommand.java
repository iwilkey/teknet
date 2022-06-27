package io.github.iwilkey.teknetcore.go;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.TeknetCore;
import io.github.iwilkey.teknetcore.utils.LogType;

public class SaveposCommand implements CommandExecutor {
	
	private GotoUtils gotoUtils;
	
	public SaveposCommand(GotoUtils gotoUtils) {
		this.gotoUtils = gotoUtils;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player)sender;
		if(gotoUtils.returnPositionsOf(player).size() + 1 > gotoUtils.MAX_SAVED_POSITIONS) {
			TeknetCore.informPlayer(player, "You cannot save anymore locations because you already have claimed "
					+ "the maximum amount. Try deleting one.", LogType.NOTICE);
		} else {
			if(args.length != 1) return false;
			Location pos = player.getLocation();
			if(gotoUtils.createNewPosition(player, args[0], pos)) 
				TeknetCore.informPlayer(player, "Current position saved as \"" + args[0] + "\". Revisit here with '/goto " + args[0] + "'.", LogType.SUCCESS);
		}
		return true;
	}
}
