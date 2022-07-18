package io.github.iwilkey.teknetcore.estate;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.TeknetCoreCommand;
import io.github.iwilkey.teknetcore.ranks.Ranks.Rank;
import io.github.iwilkey.teknetcore.utils.ChatUtilities;
import io.github.iwilkey.teknetcore.utils.ChatUtilities.CommandDocumentation;

public class Estate {
	
	public static class EstateCommand extends TeknetCoreCommand {
		public EstateCommand(Rank permissions) {
			super("estate", permissions);
			
			Function estateCreate = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					if(!createEstateInstance(sender, args[1])) {
						ChatUtilities.logTo(sender, "You already have an estate by this name!", ChatUtilities.LogType.FATAL);
						return;
					}
					ChatUtilities.logTo(sender, "Estate created. Use [estate-manage-" + args[1] + "] to proceed!", 
							ChatUtilities.LogType.SUCCESS);
				}	
			};
			
			Function estateManage = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					if(args.length == 1) {
						ChatUtilities.logTo(sender, "You must specify the estate you are managing!", ChatUtilities.LogType.FATAL);
						return;
					}
					EstateInstance s = getEstateInstance(sender, args[1]);
					if(s == null) { 
						ChatUtilities.logTo(sender, "You do not own an estate by this name!", ChatUtilities.LogType.FATAL);
						return;
					}
					if(args.length == 2) {
						// Show estate properties and help here...
						
					} else {
						switch(args[2]) {
							case "setsize":
								
								break;
							default:
								ChatUtilities.logTo(sender, "Not a valid estate property! [", ChatUtilities.LogType.FATAL);
								return;
						}
					}
				}
			};
			
			registerFunction("create", estateCreate, 1);
			registerFunction("manage", estateManage);
		}

		@Override
		protected void documentation(CommandDocumentation doc) {
			
		}

		@Override
		public boolean logic(Player sender, Command command, String label, String[] args) {
			return false;
		}
	}
	
	public static class EstateInstance {
		public String owner,
			estateName;
		public ArrayList<String> members;
		public Location centerLocation;
		public long size;
		public EstateInstance(Player player, String name) {
			this.owner = player.getName();
			this.estateName = name;
			this.centerLocation = player.getLocation();
			members = new ArrayList<>();
			size = 32;
		}
	}
	
	private static ArrayList<EstateInstance> ESTATE_STATE;
	
	public Estate() {
		ESTATE_STATE = new ArrayList<>();
	}
	
	public static EstateInstance getEstateInstance(Player player, String name) {
		for(EstateInstance e : ESTATE_STATE)
			if(e.estateName.equals(name) && e.owner.equals(player.getName()))
				return e;
		return null;
	}
	
	public static boolean createEstateInstance(Player player, String name) {
		for(EstateInstance e : ESTATE_STATE)
			if(e.estateName.equals(name) && e.owner.equals(player.getName()))
				return false;
		ESTATE_STATE.add(new EstateInstance(player, name));
		return true;
	}
}
