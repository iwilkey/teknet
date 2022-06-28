package io.github.iwilkey.teknetcore.security;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.utils.FileIO;


public class Security {
	
	public static final int SECURITY_CHECK_SECONDS = 3,
		MAX_WARNINGS_BEFORE_INFRACTION = 3,
		MAX_INFRACTIONS_BEFORE_KICK = 3;
	
	private static final String DATA_NAME = "infraction";
	
	public static class InfractionRecord {
		public String playerName;
		public int warnings, infractions;
		public ArrayList<String> notes;
		public InfractionRecord(String name) {
			this.playerName = name;
			this.infractions = 0;
			this.warnings = 0;
			this.notes = new ArrayList<String>();
		}
		public InfractionRecord(String name, int warnings, int infractions, ArrayList<String> notes) {
			this.playerName = name;
			this.infractions = infractions;
			this.warnings = warnings;
			this.notes = notes;
		}
	}
	
	public static ArrayList<InfractionRecord> records;
	
	public Security() {
		records = new ArrayList<InfractionRecord>();
		translateRegister();
	}
	
	public static void writeInfractionTo(Player player, String reason) {
		InfractionRecord infraction = infractionRecordOf(player);
		infraction.infractions++;
		inform(true, player, reason);
		writeRegister();
	}
	
	public static void writeWarningTo(Player player, String reason) {
		InfractionRecord infraction = infractionRecordOf(player);
		infraction.warnings++;
		infraction.notes.add(reason);
		inform(false, player, reason);
		writeRegister();
	}
	
	public static InfractionRecord infractionRecordOf(Player player) {
		for(InfractionRecord rec : records)
			if(rec.playerName.equals(player.getName()))
				return rec;
		records.add(new InfractionRecord(player.getName()));
		return records.get(records.size() - 1);
	}
	
	public static void policePlayer(Player sender, Player target, String reason) {
		if(sender.getName().equals(target.getName())) 
			sender.sendMessage("Interesting... you turning yourself in. Thank you for being honest, though.");
		reason += " signed, " + sender.getName();
		InfractionRecord infractions = infractionRecordOf(target);
		for(String reasons : infractions.notes)
			for(String s : reasons.split(" "))
				if(s.equals(sender.getName())) {
					Security.inform((Player)sender, "You have already sent this player a warning since last reset. We will take it from here.");
					return;
				}
		Security.writeWarningTo(target, reason);
		Security.inform(target, "Someone has called the police on you! Please be considerate of others.");
	}
	
	private static void inform(boolean infraction, Player player, String message) {
		String info = "";
		if(infraction) {
			info += ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Teknet Security System: Infraction] " + ChatColor.RESET;
			info += ChatColor.ITALIC + ""+ message + ChatColor.RESET;
		} else {
			info += ChatColor.YELLOW + "" + ChatColor.BOLD + "[Teknet Security System: Warning] " + ChatColor.RESET;
			info += ChatColor.ITALIC + ""+ message + ChatColor.RESET;
		}
		player.sendMessage(info);
		player.sendMessage(ChatColor.GRAY + "(If you believe this to be a mistake, please inform an admin right away and explain your side of the situation...)" + ChatColor.RESET);
	}
	
	public static void inform(Player player, String message) {
		String info = "";
		info += ChatColor.BLUE + "" + ChatColor.BOLD + "[Teknet Security Record System] " + ChatColor.RESET;
		info += ChatColor.ITALIC + ""+ message + ChatColor.RESET;
		player.sendMessage(info);
		player.sendMessage(ChatColor.GRAY + "(If you believe this to be a mistake, please inform an admin right away and explain your side of the situation...)" + ChatColor.RESET);
	}
	
	private static void translateRegister() {
		if(!FileIO.fileExists(DATA_NAME)) FileIO.createDataFile(DATA_NAME);
		records.clear();
		ArrayList<String[]> data = FileIO.readDataFileLines(DATA_NAME);
		for(String[] lineDat : data) {
			InfractionRecord rec = new InfractionRecord(lineDat[0]);
			rec.playerName = lineDat[0];
			rec.warnings = Integer.parseInt(lineDat[1]);
			rec.infractions = Integer.parseInt(lineDat[2]);
			ArrayList<String> notes = new ArrayList<String>();
			for(int i = 3; i < lineDat.length; i++) {
				lineDat[i] = lineDat[i].replace('-', ' ');
				notes.add(lineDat[i]);
			}
			records.add(rec);
		}
	}
	
	private static void writeRegister() {
		FileIO.clearDataFile(DATA_NAME);
		for(InfractionRecord rec : records) {
			String line = rec.playerName + " " + rec.warnings + " " + rec.infractions + " ";
			for(String notes : rec.notes) {
				notes = notes.replace(' ', '-');
				line += notes;
				notes = notes.replace('-', ' ');
			}
			FileIO.appendDataEntryTo(DATA_NAME, line);
		}
	}
}
