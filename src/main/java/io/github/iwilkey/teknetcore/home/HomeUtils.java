package io.github.iwilkey.teknetcore.home;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.utils.FileIO;

public class HomeUtils {
	
	private final String DATA_NAME = "register";
	
	public static class PlayerHomeData {
		public String playerName,
			worldName;
		public float x, y, z;
		public PlayerHomeData(String playerName, String worldName, float x, float y, float z) {
			this.playerName = playerName;
			this.worldName = worldName;
			this.x = x; this.y = y; this.z = z;
		}
	}
	
	private ArrayList<PlayerHomeData> homes;
	
	public HomeUtils() {
		homes = new ArrayList<PlayerHomeData>();
		translateRegister();
	}
	
	public void setHome(PlayerHomeData data) {
		if(hasHome(data.playerName)) {
			for(int i = 0; i < homes.size(); i++) 
				if(homes.get(i).playerName.equals(data.playerName))
					homes.set(i, data);
		} else homes.add(data);
		writeRegister();
	}
	
	public boolean hasHome(String name) {
		 for(PlayerHomeData entry : homes) 
			 if(entry.playerName.equals(name)) 
				 return true;
		 return false;
	}
	
	public void teleportHome(Player player, String name) {
		PlayerHomeData data = getPlayerHomeData(name);
		Location lpp = player.getLocation();
		lpp.setX(data.x);
		lpp.setY(data.y);
		lpp.setZ(data.z);
		lpp.setWorld(Bukkit.getServer().getWorld(data.worldName));
		player.teleport(lpp);
	}
	
	private PlayerHomeData getPlayerHomeData(String name) {
		for(PlayerHomeData entry : homes)
			if(entry.playerName.equals(name))
				return entry;
		return null;
	}
	
	private void translateRegister() {
		if(!FileIO.fileExists(DATA_NAME)) FileIO.createDataFile(DATA_NAME);
		homes.clear();
		ArrayList<String[]> data = FileIO.readDataFileLines(DATA_NAME);
		for(String[] lineDat : data) {
			float x = Float.parseFloat(lineDat[2]),
				y = Float.parseFloat(lineDat[3]),
				z = Float.parseFloat(lineDat[4]);
			PlayerHomeData phdat = new PlayerHomeData(lineDat[0], lineDat[1], x, y, z);
			homes.add(phdat);
		}
	}
	
	public void writeRegister() {
		FileIO.clearDataFile(DATA_NAME);
		for(PlayerHomeData entry : homes) {
			String data = entry.playerName + " " + entry.worldName + " " + entry.x + " " + entry.y + " " + entry.z;
			FileIO.appendDataEntryTo(DATA_NAME, data);
		}
	}
}
