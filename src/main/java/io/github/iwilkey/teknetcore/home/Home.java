package io.github.iwilkey.teknetcore.home;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Home {
	
	public static class PlayerHomeData {
		public String playerName,
			worldName;
		public Location location;
		public PlayerHomeData(String playerName, String worldName, Location location) {
			this.playerName = playerName;
			this.worldName = worldName;
			this.location = location;
		}
	}
	
	private final ArrayList<PlayerHomeData> homes = new ArrayList<PlayerHomeData>();
	
	public Home() {
		// translateRegister();
	}
	
	public void setHome(PlayerHomeData data) {
		if(hasHome(data.playerName)) {
			for(int i = 0; i < homes.size(); i++) 
				if(homes.get(i).playerName == data.playerName)
					homes.set(i, data);
		} else homes.add(data);
	}
	
	public boolean hasHome(String name) {
		 for(PlayerHomeData entry : homes)
			 if(entry.playerName == name) 
				 return true;
		 return false;
	}
	
	// The player has a home.
	public void teleportHome(String name) {
		PlayerHomeData data = getPlayerHomeData(name);
		Location pos = data.location;
		Player player = Bukkit.getPlayer(data.playerName);
		Location lpp = player.getLocation();
		float pitch = lpp.getPitch();
		float yaw = lpp.getYaw();
		lpp.setX(pos.getX());
		lpp.setY(pos.getY());
		lpp.setZ(pos.getZ());
		player.teleport(lpp);
		player.getLocation().setPitch(pitch);
		player.getLocation().setYaw(yaw);
	}
	
	private PlayerHomeData getPlayerHomeData(String name) {
		for(PlayerHomeData entry : homes)
			if(entry.playerName == name)
				return entry;
		return null;
	}
	
	/*
	// Update the HashMap with contents from register.
	private void translateRegister() {
		try {
			File reg = new File("./TeknetCore/home/register.tek");
			if(reg.createNewFile()) {
				return;
			} else {
				Scanner reader = new Scanner(reg);
				while (reader.hasNextLine()) {
				    String data = reader.nextLine();
				    String[] tokens = data.split(" ");
				    // Every line is a player
				    
				}
			}
		} catch(IOException e) {
			System.out.println("An internal server error occurred while trying to open the TeknetCore home register.");
		    e.printStackTrace();
		}
	}
	
	public void writeRegister() {
		
	}
	*/
	
	

}
