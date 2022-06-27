package io.github.iwilkey.teknetcore.randompos;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.utils.Mathematics;

public class RandomposCommand implements CommandExecutor {
	
	public static final int RANGE = 10000;
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player)sender;
		teleportPlayerToRandomLocation(player);
		player.sendMessage("Is this location good enough for you?");
		return true;
	}
	
	private void teleportPlayerToRandomLocation(Player player) {
		int x = Mathematics.randomIntBetween(player.getLocation().getBlockX() - RANGE, 
				player.getLocation().getBlockX() + RANGE);
	    int y = 156;
	    int z = Mathematics.randomIntBetween(player.getLocation().getBlockZ() - RANGE, 
	    		player.getLocation().getBlockZ() + RANGE);
	    if(player.getWorld().getBlockAt(x, y, z).isEmpty()) {
			while(player.getWorld().getBlockAt(x, y - 1, z).isEmpty() && y > 0) y--;
			Location lpp = player.getLocation();
			lpp.setX(x);
			lpp.setY(y + 2);
			lpp.setZ(z);
			player.teleport(lpp);
	    }
	    return;
	}	
}
