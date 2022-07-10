package io.github.iwilkey.teknetcore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.ranks.Ranks;

public abstract class TeknetCoreCommand implements CommandExecutor {
	private Ranks.Rank permissions;
	public TeknetCoreCommand(Ranks.Rank permissions) {
		this.permissions = permissions;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		if(!Ranks.canUseFeature((Player)sender, permissions)) return true;
		return logic((Player)sender, command, label, args);
	}
	public abstract boolean logic(Player sender, Command command, String label, String[] args);
}
