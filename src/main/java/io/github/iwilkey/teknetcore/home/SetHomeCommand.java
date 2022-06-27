package io.github.iwilkey.teknetcore.home;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.home.Home.PlayerHomeData;

public class SetHomeCommand implements CommandExecutor {
	
	private final Home homeCore;

    public SetHomeCommand(Home homeCore) {
        this.homeCore = homeCore;
    }

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player)sender;
		Location location = player.getLocation();
		String name = player.getName(),
			worldName = player.getWorld().getName();
		PlayerHomeData data = new PlayerHomeData(name, worldName, (float)location.getX(), (float)location.getY(), (float)location.getZ());
		homeCore.setHome(data);
		player.sendMessage("You have successfully set your home. Use '/home' to return here.");
		return true;
	}

}
