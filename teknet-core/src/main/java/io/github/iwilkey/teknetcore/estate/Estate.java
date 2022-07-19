package io.github.iwilkey.teknetcore.estate;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.TeknetCoreCommand;
import io.github.iwilkey.teknetcore.economy.Bank.Currency;
import io.github.iwilkey.teknetcore.ranks.Ranks.Rank;
import io.github.iwilkey.teknetcore.utils.ChatUtilities;
import io.github.iwilkey.teknetcore.utils.FileUtilities;
import io.github.iwilkey.teknetcore.utils.SoundUtilities;
import io.github.iwilkey.teknetcore.utils.ChatUtilities.CommandDocumentation;
import io.github.iwilkey.teknetcore.utils.MathUtilities;
import io.github.iwilkey.teknetcore.utils.SequenceUtilities;
import io.github.iwilkey.teknetcore.utils.SequenceUtilities.Sequence;
import io.github.iwilkey.teknetcore.utils.SequenceUtilities.SequenceFunction;

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
						printEstateInformationTo(s, sender);
						ChatUtilities.messageTo(sender, " ► Use [estate-manage-" + s.estateName + "-resize-<number>] to change the size (more rent).", ChatColor.GRAY);
						ChatUtilities.messageTo(sender, " ► Use [estate-manage-" + s.estateName + "-(un)trust-<player-name>] to (dis)allow another player use of the space.", ChatColor.GRAY);
					} else {
						switch(args[2]) {
							case "resize":
								ChatUtilities.logTo(sender, "This function is not currently implemented.", ChatUtilities.LogType.NOTICE);
								break;
							case "trust":
								ChatUtilities.logTo(sender, "This function is not currently implemented.", ChatUtilities.LogType.NOTICE);
								break;
							case "untrust":
								ChatUtilities.logTo(sender, "This function is not currently implemented.", ChatUtilities.LogType.NOTICE);
								break;
							default:
								ChatUtilities.logTo(sender, "Not a valid estate property! [estate-manage-" + s.estateName + "]", ChatUtilities.LogType.FATAL);
								return;
						}
					}
				}
			};
			
			Function estateDelete = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					ChatUtilities.logTo(sender, "This function is not currently implemented.", ChatUtilities.LogType.NOTICE);
				}
			};
			
			Function estateList = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					ChatUtilities.logTo(sender, "This function is not currently implemented.", ChatUtilities.LogType.NOTICE);
				}
			};
			
			Function estateRename = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					ChatUtilities.logTo(sender, "This function is not currently implemented.", ChatUtilities.LogType.NOTICE);
				}
			};
			
			Function estateCurrent = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					EstateInstance s = getCurrentInstanceOf(sender);
					if(s == null) {
						ChatUtilities.logTo(sender, "You are not currently inside of an estate. "
								+ "Create one with [estate-create-<name>]", ChatUtilities.LogType.NOTICE);
						return;
					}
					printEstateInformationTo(s, sender);
				}
			};

			registerFunction("create", estateCreate, 1, "make");
			registerFunction("delete", estateDelete, 1, "remove");
			registerFunction("rename", estateRename, 1, "name");
			registerFunction("list", estateList, 1);
			registerFunction("current", estateCurrent, 0);
			registerFunction("manage", estateManage, "edit");
			
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
		public ArrayList<String> members, visitors;
		public Location centerLocation;
		public long size;
		public Currency rent;
		public EstateInstance(Player player, String name) {
			this.owner = player.getName();
			this.estateName = name;
			this.centerLocation = player.getLocation();
			members = new ArrayList<>();
			visitors = new ArrayList<>();
			members.add(owner);
			size = 3;
			rent = new Currency(10025); // Calculate rent based off size...
			Estate.showEstateTo(this, player);
		}
		public EstateInstance(String playerName, String name, Location center, int size) {
			this.owner = playerName;
			this.estateName = name;
			this.centerLocation = center;
			visitors = new ArrayList<>();
			this.size = size;
			rent = new Currency(10025); // Calculate rent based off size...
		}
		public boolean inRegion(Player player) {
			return visitors.contains(player.getName());
		}
	}
	
	public static ArrayList<EstateInstance> ESTATE_STATE;
	
	public Estate() {
		ESTATE_STATE = new ArrayList<>();
		translateRegister();
	}
	
	public static void tick() {
		for(EstateInstance e : ESTATE_STATE) {
			for(Player p : Bukkit.getOnlinePlayers()) {
				Location l = p.getLocation();
				long x = l.getBlockX(), z = l.getBlockZ();
				if(MathUtilities.locationInEstateRegion(e.centerLocation.getBlockX(), e.centerLocation.getBlockZ(), x, z, e.size)) {
					if(!e.inRegion(p)) {
						ChatUtilities.logTo(p, ChatColor.GREEN + 
								"You have just entered the Teknet Estate \"" + ChatColor.GOLD 
								+ e.estateName.toUpperCase() + ChatColor.GREEN + "\" owned by " + ChatColor.GOLD + e.owner + ChatColor.GREEN + "! Use [estate-current] " + ChatColor.GREEN + "to see it's information.", ChatUtilities.LogType.NOTICE);
						e.visitors.add(p.getName());
					}
				} else {
					if(e.inRegion(p)) {
						ChatUtilities.logTo(p, ChatColor.GRAY + 
								"You have just left the Teknet Estate \"" + ChatColor.GOLD 
								+ e.estateName.toUpperCase() + ChatColor.GRAY + "\" owned by " + ChatColor.GOLD + e.owner + ChatColor.GRAY + "!", ChatUtilities.LogType.NOTICE);
						e.visitors.remove(p.getName());
					}
				}
			}
		}
	}
	
	public static EstateInstance getCurrentInstanceOf(Player player) {
		for(EstateInstance e : ESTATE_STATE) {
			Location l = player.getLocation();
			long x = l.getBlockX(), z = l.getBlockZ();
			if(MathUtilities.locationInEstateRegion(e.centerLocation.getBlockX(), 
					e.centerLocation.getBlockZ(), x, z, e.size))
				return e;
		}
		return null;
	}
	
	public static void printEstateInformationTo(EstateInstance s, Player sender) {
		SoundUtilities.playSoundTo("ARROW_HIT", sender);
		ChatUtilities.messageTo(sender, "------- Teknet Estates Listing -------", ChatColor.WHITE);
		ChatUtilities.messageTo(sender, " ► Estate name: " + ChatColor.GOLD + s.estateName.toUpperCase(), ChatColor.GRAY);
		ChatUtilities.messageTo(sender, " ► Estate owner: " + ChatColor.GOLD + s.owner, ChatColor.GRAY);
		ChatUtilities.messageTo(sender, " ► Estate size: " + ChatColor.GOLD + ((s.size * 2) + 1) + " x " + ((s.size * 2) + 1) + ChatColor.GRAY + " (Center: " + (s.size + 1) + ", " + (s.size + 1) + ")", ChatColor.GRAY);
		ChatUtilities.messageTo(sender, " ► Estate location (center): " + ChatColor.GOLD 
				+ "x: " + s.centerLocation.getBlockX() + ", z: " + s.centerLocation.getBlockZ() 
				+ ChatColor.GRAY + "\n    (Your location): " + ChatColor.YELLOW + "x: " + sender.getLocation().getBlockX() + ", z: " +
				sender.getLocation().getBlockZ(), ChatColor.GRAY);
		ChatUtilities.messageTo(sender, " ► Estate rent ($/day): " + s.rent.printValueColored(), ChatColor.GRAY);
		String trusted = ChatColor.GOLD + "";
		int n = 0;
		for(String name : s.members) {
			trusted += name;
			if(n != s.members.size() - 1) trusted += ChatColor.GRAY + ", " + ChatColor.GOLD;
			n++;
		}
		ChatUtilities.messageTo(sender, " ► Trusted players: " + trusted, ChatColor.GRAY);
		showEstateTo(s, sender);
	}
	
	public static void showEstateTo(EstateInstance inst, Player player) {
		SequenceFunction f = new SequenceFunction() {
			@Override
			public void onIteration(Object... objects) {
				for(long xx = -inst.size; xx <= inst.size; xx++) {
					for(long zz = -inst.size; zz <= inst.size; zz++) {
						Location l = new Location(inst.centerLocation.getWorld(), 
								inst.centerLocation.getX() + xx, player.getLocation().getY() - 1, 
								inst.centerLocation.getZ() + zz);
						player.playEffect(l, Effect.MOBSPAWNER_FLAMES, null);
					}
				}
			}
		};
		Sequence show = new Sequence(25, 0.5f, f);
		SequenceUtilities.startSequence(show);
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
		// Check overlap.
		ESTATE_STATE.add(new EstateInstance(player, name));
		writeRegister();
		return true;
	}
	
	private static void translateRegister() {
		if(!FileUtilities.fileExists("estate")) FileUtilities.createDataFile("estate");
		ESTATE_STATE.clear();
		ArrayList<String[]> data = FileUtilities.readDataFileLines("estate");
		for(String[] lineDat : data) {
			EstateInstance inst = new EstateInstance(lineDat[1], lineDat[0], 
					new Location(Bukkit.getServer().getWorld(lineDat[5]), Integer.parseInt(lineDat[2]), 
							Integer.parseInt(lineDat[3]), Integer.parseInt(lineDat[4])), Integer.parseInt(lineDat[6]));
			inst.members = new ArrayList<>();
			for(int i = 7; i < lineDat.length; i++)
				inst.members.add(lineDat[i]);
			ESTATE_STATE.add(inst);
		}
			
	}
	
	private static void writeRegister() {
		FileUtilities.clearDataFile("estate");
		for(EstateInstance e : ESTATE_STATE) {
			String data = e.estateName + " " + e.owner + " " + e.centerLocation.getBlockX() + " " 
					+ e.centerLocation.getBlockY() + " " + e.centerLocation.getBlockZ() + " " + e.centerLocation.getWorld().getName() + " " + e.size + " ";
			String trusted = "";
			for(int i = 0; i < e.members.size(); i++)
				trusted += e.members.get(i) + " ";
			FileUtilities.appendDataEntryTo("estate", data + trusted);
		}
	}
}
