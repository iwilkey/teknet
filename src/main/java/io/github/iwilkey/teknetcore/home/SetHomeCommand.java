package io.github.iwilkey.teknetcore.home;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.TeknetCore;
import io.github.iwilkey.teknetcore.home.HomeUtils.PlayerHomeData;
import io.github.iwilkey.teknetcore.utils.LogType;

public class SetHomeCommand implements CommandExecutor {
	
	private final HomeUtils homeCore;

    public SetHomeCommand(HomeUtils homeCore) {
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
		TeknetCore.informPlayer(player, "You have successfully set your home. Use '/home' to return here.", LogType.SUCCESS);
		return true;
	}

}
