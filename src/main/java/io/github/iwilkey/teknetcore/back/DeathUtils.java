package io.github.iwilkey.teknetcore.back;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.go.GotoUtils.Position;

public class DeathUtils {
	
	private static ArrayList<Position> lastDeathLocations;
	
	public DeathUtils() {
		lastDeathLocations = new ArrayList<Position>();
	}
	
	public static void logDeathOf(Player player) {
		for(Position p : lastDeathLocations)
			if(p.name.equals(player.getName())) {
				p.location = player.getLocation();
				return;
			}
		lastDeathLocations.add(new Position(player.getName(), player.getLocation()));
	}
	
	public Location getPlayerLastDeathLocation(Player player) {
		for(Position p : lastDeathLocations)
			if(p.name.equals(player.getName()))
				return p.location;
		return null;
	}
	
}
