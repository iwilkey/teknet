package io.github.iwilkey.teknetcore.go;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.TeknetCore;
import io.github.iwilkey.teknetcore.utils.FileIO;
import io.github.iwilkey.teknetcore.utils.LogType;

public class GotoUtils {
	
	private final String DATA_NAME = "posreg";
	
	public final int MAX_SAVED_POSITIONS = 5;
	
	public static class Position {
		public String name;
		public Location location;
		public Position(String name, Location location) {
			this.name = name;
			this.location = location;
		}
	}
	
	public static class PlayerSavedPositions {
		public String playerName;
		public ArrayList<Position> positions;
		public PlayerSavedPositions(String name) {
			this.playerName = name;
			positions = new ArrayList<Position>();
		}
		public PlayerSavedPositions(String name, ArrayList<Position> positions) {
			this.playerName = name;
			this.positions = positions;
		}
	}
	
	ArrayList<PlayerSavedPositions> positions;
	
	public GotoUtils() {
		positions = new ArrayList<PlayerSavedPositions>();
		translateRegister();
	}
	
	public void seePostionsOf(Player player) {
		ArrayList<Position> positions = returnPositionsOf(player);
		if(positions.size() == 0) TeknetCore.informPlayer(player, "You do not have any positions saved. "
				+ "Create one at current location with '/savepos [name]'...", LogType.NOTICE);
		else {
			TeknetCore.informPlayer(player, "Here is a list of all your saved positions...", LogType.UTILITY);
			player.sendMessage(" ");
			player.sendMessage(ChatColor.ITALIC + "" + "Return to a position using the name or index like '/goto [name]' or '/goto [number-in-list]'." 
					+ ChatColor.RESET + "");
			player.sendMessage(" ");
			for(int i = 0; i < positions.size(); i++)
				player.sendMessage(ChatColor.ITALIC + "   [" + (i + 1) + "] \"" + ChatColor.GOLD + "" + positions.get(i).name + 
						ChatColor.RESET + "" + "\" at " + 
						(int)positions.get(i).location.getX() + " " + 
						(int)positions.get(i).location.getY() + " " + 
						(int)positions.get(i).location.getZ() + " in world \"" 
						+ positions.get(i).location.getWorld().getName() + "\""+ ChatColor.RESET + "...");
		}
	}
	
	public boolean deletePosition(Player player, String name) {
		if(!checkPositionExistance(player, name)) {
			TeknetCore.informPlayer(player, 
					"You cannot delete this position because it does not exist!", 
					LogType.FATAL);
			return false;
		}
		int i = 0;
		for(Position p : returnPositionsOf(player)) {
			if(p.name.equals(name))
				break;
			i++;
		}
		returnPositionsOf(player).remove(i);
		TeknetCore.informPlayer(player, 
				"Location successfully deleted.", 
				LogType.SUCCESS);
		return true;
	}
	
	public boolean gotoPostion(Player player, String name) {
		if(!checkPositionExistance(player, name)) {
			TeknetCore.informPlayer(player, 
					"You cannot go to this position because it does not exist. Create it with '/savepos " + name + "'...", 
					LogType.FATAL);
			return false;
		}
		Location l = returnLocationOfPosition(player, name);
		player.teleport(l);
		player.sendMessage("Poof!");
		return true;
	}
	
	public boolean createNewPosition(Player player, String name, Location l) {
		if(checkPositionExistance(player, name)) {
			TeknetCore.informPlayer(player, "You cannot create a new position called by this name, it already exists. "
					+ "Try '/delpos [name]' to delete it...", LogType.FATAL);
			return false;
		}
		returnPositionsOf(player).add(new Position(name, l));
		writeRegister();
		return true;
	}
	
	public boolean checkPositionExistance(Player player, String name) {
		for(Position p : returnPositionsOf(player))
			if(p.name.equals(name))
				return true;
		return false;
	}
	
	public ArrayList<Position> returnPositionsOf(Player player) {
		for(PlayerSavedPositions p : positions)
			if(p.playerName.equals(player.getName()))
				return p.positions;
		positions.add(new PlayerSavedPositions(player.getName()));
		return positions.get(positions.size() - 1).positions;
	}
	
	public Location returnLocationOfPosition(Player player, String name) {
		for(Position p : returnPositionsOf(player))
			if(p.name.equals(name))
				return p.location;
		return null;
	}
	
	private void translateRegister() {
		if(!FileIO.fileExists(DATA_NAME)) FileIO.createDataFile(DATA_NAME);
		positions.clear();
		ArrayList<String[]> data = FileIO.readDataFileLines(DATA_NAME);
		for(String[] lineDat : data) {
			ArrayList<Position> ppositions = new ArrayList<Position>();
			for(int i = 1; i < data.size(); i++) {
				String[] tokens = lineDat[i].split(";");
				Location l = new Location(Bukkit.getServer().getWorld(tokens[4]), Integer.parseInt(tokens[1]), 
						Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
				Position pos = new Position(tokens[1], l);
				ppositions.add(pos);
			}
			PlayerSavedPositions psp = new PlayerSavedPositions(lineDat[0], ppositions);
			positions.add(psp);
		}
	}
	
	private void writeRegister() {
		FileIO.clearDataFile(DATA_NAME);
		for(PlayerSavedPositions psp : positions) {
			String line = psp.playerName + " ";
			for(Position p : psp.positions)
				line += p.name + ";" + (int)p.location.getX() + ";" 
					+ (int)p.location.getY() + ";" + (int)p.location.getZ() 
					+ ";" + p.location.getWorld().getName() + " ";
			FileIO.appendDataEntryTo(DATA_NAME, line);
		}
	}
}
