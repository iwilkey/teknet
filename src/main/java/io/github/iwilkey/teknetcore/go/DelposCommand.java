package io.github.iwilkey.teknetcore.go;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelposCommand implements CommandExecutor {

	private GotoUtils gotoUtils;
	
	public DelposCommand(GotoUtils gotoUtils) {
		this.gotoUtils = gotoUtils;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		if(args.length != 1) return false;
		Player player = (Player)sender;
		gotoUtils.deletePosition(player, args[0]);
		return true;
	}
	
}
