package io.github.iwilkey.teknetcore.location;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.TeknetCore;
import io.github.iwilkey.teknetcore.TeknetCoreCommand;
import io.github.iwilkey.teknetcore.location.Locations.Positions.PositionData.Position;
import io.github.iwilkey.teknetcore.ranks.Ranks.Rank;
import io.github.iwilkey.teknetcore.utils.ChatUtilities;
import io.github.iwilkey.teknetcore.utils.ChatUtilities.CommandDocumentation;
import io.github.iwilkey.teknetcore.utils.FileUtilities;
import io.github.iwilkey.teknetcore.utils.PlayerUtilities;
import io.github.iwilkey.teknetcore.utils.SoundUtilities;

public class Locations {
	
	public Locations() {
		new Home();
		new Teleport();
		new Positions();
	}
	
	public static void tick() {
		Teleport.tick();
	}
	
	/*
	 * 
	 * TELEPORT
	 * 
	 */
	public static class Teleport {
		
		private static final int TELEPORT_WAIT_TIME = 5;
		
		public static class TeleportRequest {
			public String playerName;
			public Location destination;
			public int ticksSince,
				secLeftOld = 0,
				secLeftNew = (int)TELEPORT_WAIT_TIME;
			public float secondsSince;
			public TeleportRequest(String playerName, Location l) {
				this.playerName = playerName;
				this.destination = l;
				ticksSince = 0;
				secondsSince = 0.0f;
			}
		}
		
		private static ArrayList<TeleportRequest> TELEPORT_STATE,
			toDelete;
		
		public Teleport() {
			TELEPORT_STATE = new ArrayList<>();
			toDelete = new ArrayList<>();
		}
		
		public static boolean teleportTo(Player p, Location l) {
			for(TeleportRequest request : TELEPORT_STATE) {
				if(request.playerName.equals(p.getName())) {
					SoundUtilities.playSoundTo("NOTE_BASS_GUITAR", p);
					ChatUtilities.logTo(p, "You already have a teleportation in progress!", ChatUtilities.LogType.FATAL);
					return false;
				}
			}
			SoundUtilities.playSoundTo("WOOD_CLICK", p);
			ChatUtilities.messageTo(p, "Teleporting in " + ChatColor.GREEN + TELEPORT_WAIT_TIME + ChatColor.GOLD + " (s)...", ChatColor.GOLD);
			TELEPORT_STATE.add(new TeleportRequest(p.getName(), l));
			return true;
		}
		
		public static void tick() {
			if(TELEPORT_STATE.size() == 0) return;
			if(toDelete.size() != 0) toDelete.clear();
			for(TeleportRequest request : TELEPORT_STATE) {
				Player p = PlayerUtilities.get(request.playerName);
				if(p == null) {
					toDelete.add(request);
					continue;
				}
				request.secLeftOld = request.secLeftNew;
				request.ticksSince++;
				request.secondsSince = (1.0f / TeknetCore.SERVER_TPS) * request.ticksSince;
				request.secLeftNew = (int)(TELEPORT_WAIT_TIME - request.secondsSince) + 1;
				if(request.secLeftNew != request.secLeftOld) {
					SoundUtilities.playSoundTo("WOOD_CLICK", p);
					ChatUtilities.messageTo(p, "Teleporting in " + ChatColor.GREEN + request.secLeftNew + ChatColor.GOLD + " (s)...", ChatColor.GOLD);
				}
				if(request.secondsSince >= TELEPORT_WAIT_TIME) {
					Location l = p.getLocation();
					l.setX(request.destination.getX());
					l.setY(request.destination.getY());
					l.setZ(request.destination.getZ());
					l.setWorld(request.destination.getWorld());
					p.teleport(l);
					p.getLocation().getWorld().playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 512);
					SoundUtilities.playSoundTo("LEVEL_UP", p);
					ChatUtilities.messageTo(Bukkit.getPlayer(request.playerName), "Poof!", ChatColor.LIGHT_PURPLE);
					toDelete.add(request);
				}
			}
			if(toDelete.size() != 0) for(TeleportRequest r : toDelete) TELEPORT_STATE.remove(r);
		}
	}
	
	/*
	 * 
	 * POSITIONS
	 * 
	 */
	public static class Positions {
		
		public static final int MAX_POSITIONS = 3;
		
		public static class PositionCommand extends TeknetCoreCommand {
			
			final static CommandDocumentation doc = new CommandDocumentation("position");

			public PositionCommand(Rank permissions) {
				super(permissions);
				doc.editPage(0).write(ChatColor.GOLD + "This utility is used for managing saved positions" + ChatColor.RESET, 1);
				doc.editPage(0).write("Use [position-help] to show this help page again.", 2);
				doc.editPage(0).write("Use [position-save-<name>] to save and name your position!", 3);
				doc.editPage(0).write("Use [position-go-<name>] to teleport to a saved position.", 4);
				doc.editPage(0).write("Use [position-delete-<name-OR-'all'>] to delete an existing", 5);
				doc.editPage(0).write("    (or all) position(s).", 6);
				doc.editPage(0).write("Use [position-list] to show a list of all of your current", 7);
				doc.editPage(0).write("    positions.", 8);
			}

			@Override
			public boolean logic(Player sender, Command command, String label, String[] args) {
				if(args.length < 1) {
					ChatUtilities.logTo(sender, "Incorrect position command syntax!", ChatUtilities.LogType.FATAL);
					printPositionHelp(sender, doc);
					return true;
				}
				if(args[0].equals("save")) {
					if(args.length != 2) {
						ChatUtilities.logTo(sender, "Incorrect position command syntax!", ChatUtilities.LogType.FATAL);
						printPositionHelp(sender, doc);
						return true;
					}
					Positions.createPosition(sender, args[1], sender.getLocation());
				} else if(args[0].equals("delete")) {
					if(args.length != 2) {
						ChatUtilities.logTo(sender, "Incorrect position command syntax!", ChatUtilities.LogType.FATAL);
						printPositionHelp(sender, doc);
						return true;
					}
					Positions.deletePosition(sender, args[1]);
				} else if(args[0].equals("list")) {
					if(args.length != 1) {
						ChatUtilities.logTo(sender, "Incorrect position command syntax!", ChatUtilities.LogType.FATAL);
						printPositionHelp(sender, doc);
						return true;
					}
					Positions.showPositionsOf(sender);
				} else if (args[0].equals("help")) {
					printPositionHelp(sender, doc);
					return true;
				} else if(args[0].equals("go")) {
					if(args.length != 2) {
						ChatUtilities.logTo(sender, "Incorrect position command syntax!", ChatUtilities.LogType.FATAL);
						printPositionHelp(sender, doc);
						return true;
					}
					Positions.goTo(sender, args[1]);
				}
				return true;
			}
			
		}
		
		public static class PositionData {
			
			public static class Position {
				String name;
				Location position;
				public Position(String name, Location position) {
					this.name = name;
					this.position = position;
				}
			}
			
			String playerName;
			ArrayList<Position> locations;
			public PositionData(String playerName) {
				this.playerName = playerName;
				locations = new ArrayList<>();
			}
			
		}
		
		private static ArrayList<PositionData> POSITION_STATE;
		
		public Positions() {
			POSITION_STATE = new ArrayList<>();
		}
		
		public static void createPosition(Player player, String name, Location location) {
			PositionData data = getPlayerPositionData(player);
			if(data == null) {
				POSITION_STATE.add(new PositionData(player.getName()));
				createPosition(player, name, location);
				return;
			}
			if(data.locations.size() + 1 <= MAX_POSITIONS) {
				for(Position p : data.locations)
					if(p.name.equals(name)) {
						ChatUtilities.logTo(player, "You have already saved a position with this name!", ChatUtilities.LogType.FATAL);
						return;
					}
				data.locations.add(new Position(name, location));
				ChatUtilities.logTo(player, "Position saved!", ChatUtilities.LogType.SUCCESS);
			} else ChatUtilities.logTo(player, "You cannot create a new position because you already"
					+ " have been granted the maximum amount of allotted positions. Try [position-delete-<name>]...", ChatUtilities.LogType.FATAL);
		}
		
		public static void deletePosition(Player player, String name) {
			PositionData data = getPlayerPositionData(player);
			if(data == null) {
				ChatUtilities.logTo(player, "You have saved no positions to delete!", ChatUtilities.LogType.FATAL);
				return;
			}
			for(Position p : data.locations) 
				if(p.name.equals(name)) {
					data.locations.remove(p);
					ChatUtilities.logTo(player, "Position deleted.", ChatUtilities.LogType.SUCCESS);
					return;
				}
			ChatUtilities.logTo(player, "You cannot delete a position that doesn't exist!", ChatUtilities.LogType.FATAL);
		}
		
		public static void showPositionsOf(Player player) {
			PositionData data = getPlayerPositionData(player);
			if(data == null) {
				ChatUtilities.logTo(player, "You have no positions to show! Use\n [position-save-<name>] to save one!", ChatUtilities.LogType.FATAL);
				return;
			}
			if(data.locations.size() == 0) {
				ChatUtilities.logTo(player, "You have no positions to show! Use\n [position-save-<name>] to save one!", ChatUtilities.LogType.UTILITY);
				return;
			} else ChatUtilities.logTo(player, "Saved positions...", ChatUtilities.LogType.UTILITY);
			for(Position p : data.locations) {
				String[] out = new String[3];
				out[0] = ChatColor.AQUA + "    (Name) " + ChatColor.RESET + ChatColor.WHITE + p.name;
				out[1] = ChatColor.AQUA + "    (World) " + ChatColor.RESET + ChatColor.WHITE + p.position.getWorld().getName();
				out[2] = ChatColor.AQUA + "    (Location {x, y, z}) " + ChatColor.RESET + ChatColor.WHITE + ChatColor.ITALIC + p.position.getBlockX() + " " + p.position.getBlockY() + " " + p.position.getBlockZ();
				int lineSize = 0;
				for(int ii = 0; ii < 3; ii++)
					if(out[ii].length() > lineSize) 
						lineSize = out[ii].length();
				String lines = "";
				for(int ii = 0; ii < lineSize + 2; ii++)
					lines += '-';
				ChatUtilities.messageTo(player, "    " + lines, ChatColor.GRAY);
				ChatUtilities.messageTo(player, "    " + out[0], ChatColor.GRAY);
				ChatUtilities.messageTo(player, "    " + out[1], ChatColor.GRAY);
				ChatUtilities.messageTo(player, "    " + out[2], ChatColor.GRAY);
				ChatUtilities.messageTo(player, "    " + lines, ChatColor.GRAY);
			}
		}
		
		public static void goTo(Player player, String name) {
			PositionData data = getPlayerPositionData(player);
			if(data == null) {
				ChatUtilities.logTo(player, "You have no positions to go to!", ChatUtilities.LogType.FATAL);
				return;
			}
			for(Position p : data.locations)
				if(p.name.equals(name)) {
					Locations.Teleport.teleportTo(player, p.position);
					return;
				}
			ChatUtilities.logTo(player, "You do not have a saved location with this name!", ChatUtilities.LogType.FATAL);
		}
		
		private static PositionData getPlayerPositionData(Player player) {
			for(PositionData data : POSITION_STATE) 
				if(data.playerName.equals(player.getName()))
					return data;
			return null;
		}
		
		public static void printPositionHelp(Player player, CommandDocumentation doc) {
			doc.renderPageTo(player, 0);
		}
	}
	
	/*
	 * 
	 * HOME
	 * 
	 */
	public static class Home {
		
		public static class SetHome extends TeknetCoreCommand {
			public SetHome(Rank permissions) {
				super(permissions);
			}
			@Override
			public boolean logic(Player sender, Command command, String label, String[] args) {
				setHome(sender);
				return true;
			}
		}
		
		public static class HomeCommand extends TeknetCoreCommand {
			public HomeCommand(Rank permissions) {
				super(permissions);
			}
			@Override
			public boolean logic(Player sender, Command command, String label, String[] args) {
				Location home = returnHome(sender);
				if(home == null) {
					ChatUtilities.logTo(sender, "You do not have a home to go to! Use [sethome] to set one.", ChatUtilities.LogType.FATAL);
					return true;
				}
				Teleport.teleportTo(sender, home);
				return true;
			}
			
		}
		
		public Home() {
			HOME_STATE = new ArrayList<>();
			translateRegister();
		}
		
		public static class HomeData {
			public String playerName;
			public Location home;
			public HomeData(String playerName) {
				this.playerName = playerName;
			}
		}
		
		private static ArrayList<HomeData> HOME_STATE;
		
		public static void setHome(Player player) {
			for(HomeData data : HOME_STATE) 
				if(data.playerName.equals(player.getName())) {
					data.home = player.getLocation();
					SoundUtilities.playSoundTo("ANVIL_LAND", player);
					ChatUtilities.logTo(player, "Home set.", ChatUtilities.LogType.SUCCESS);
					writeRegister();
					return;
				}
			HOME_STATE.add(new HomeData(player.getName()));
			setHome(player);
		}
		
		public static Location returnHome(Player p) {
			for(HomeData data : HOME_STATE) 
				if(data.playerName.equals(p.getName()))
					return data.home;
			return null;
		}
		
		private static void translateRegister() {
			if(!FileUtilities.fileExists("register")) FileUtilities.createDataFile("register");
			HOME_STATE.clear();
			ArrayList<String[]> data = FileUtilities.readDataFileLines("register");
			for(String[] lineDat : data) {
				HomeData create = new HomeData(lineDat[0]);
				create.home = new Location(Bukkit.getServer().getWorld(lineDat[1]), Float.parseFloat(lineDat[2]), 
						Float.parseFloat(lineDat[3]), Float.parseFloat(lineDat[4]));
				HOME_STATE.add(create);
			}
		}
		
		private static void writeRegister() {
			FileUtilities.clearDataFile("register");
			for(HomeData entry : HOME_STATE) {
				String data = entry.playerName + " " + Bukkit.getServer().getWorld(entry.home.getWorld().getName()).getName()
						+ " " + entry.home.getX() + " " + entry.home.getY() + " " + entry.home.getZ();
				FileUtilities.appendDataEntryTo("register", data);
			}
		}
	}
}
