package io.github.iwilkey.teknetcore.announcer;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.utils.FileIO;

public class AnnounceUtils {
	
	private static final String PATH_TO_ANNOUNCEMENTS = "announcements";
	public static final int SECONDS_PER_ANNOUCEMENT = 30;
	
	public static ArrayList<String> silentPref;
	
	public static void init() {
		silentPref = new ArrayList<String>();
		refreshAnnouncements();
	}
	
	public static final ArrayList<String> ANNOUNCEMENTS = new ArrayList<String>();
	
	public static String command(String command) {
		command = command.replace('-', ' ');
		return ChatColor.RED + "/" + command + "" + ChatColor.RESET + "" + ChatColor.ITALIC;
	}
	
	public static boolean isSilent(String playerName) {
		for(String n : silentPref)
			if(n.equals(playerName))
				return true;
		return false;
	}
	
	public static void required(String message, Player... excluded) {
		String announcement = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "" + "[Teknet] " + ChatColor.RESET + "";
		announcement += ChatColor.ITALIC + "" + message + ChatColor.RESET + "";
		top: for(Player p : Bukkit.getOnlinePlayers()) {
			for(Player ex : excluded)
				if(ex.getName().equals(p.getName()))
					continue top;
			p.sendMessage(announcement);
		}
	}
	
	public static void refreshAnnouncements() {
		ANNOUNCEMENTS.clear();
		ArrayList<String[]> lines = FileIO.readDataFileLines(PATH_TO_ANNOUNCEMENTS);
		for(String[] lineTokens : lines) {
			String line = "";
			for(String token : lineTokens) {
				if(token.length() == 0) continue;
				if(token.charAt(0) == '$' && token.charAt(token.length() - 1) == '$')
					line += command(token.substring(1, token.length() - 1)) + " ";
				/* TODO: Fix this...
				else if(token.substring(token.length() - 2, token.length()).equals("$?") ||
					token.substring(token.length() - 2, token.length()).equals("$!") ||
					token.substring(token.length() - 2, token.length()).equals("$."))
					line += command(token.substring(1, token.length() - 2)) + " ";
				 */
				else line += token + " ";
			}
			ANNOUNCEMENTS.add(line);
		}
	}
}
