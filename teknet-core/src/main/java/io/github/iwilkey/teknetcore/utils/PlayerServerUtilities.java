package io.github.iwilkey.teknetcore.utils;

import java.util.AbstractMap;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.TeknetCore;
import io.github.iwilkey.teknetcore.TeknetCoreCommand;
import io.github.iwilkey.teknetcore.cooldown.Cooldown;
import io.github.iwilkey.teknetcore.ranks.Ranks;
import io.github.iwilkey.teknetcore.ranks.Ranks.Rank;

public class PlayerServerUtilities {
	public static class Lag extends TeknetCoreCommand  {
		private static final int TARGET_TPS = 20;
		static ArrayList<AbstractMap.SimpleEntry<ChatColor, String>> lev;
		public Lag(Ranks.Rank permissions) {
			super(permissions);
			lev = new ArrayList<AbstractMap.SimpleEntry<ChatColor, String>>();
			lev.add(new AbstractMap.SimpleEntry<ChatColor, String>(ChatColor.GREEN, "pristine"));
			lev.add(new AbstractMap.SimpleEntry<ChatColor, String>(ChatColor.DARK_GREEN, "very good"));
			lev.add(new AbstractMap.SimpleEntry<ChatColor, String>(ChatColor.AQUA, "good"));
			lev.add(new AbstractMap.SimpleEntry<ChatColor, String>(ChatColor.DARK_AQUA, "average"));
			lev.add(new AbstractMap.SimpleEntry<ChatColor, String>(ChatColor.YELLOW, "slightly below average"));
			lev.add(new AbstractMap.SimpleEntry<ChatColor, String>(ChatColor.GOLD, "poor"));
			lev.add(new AbstractMap.SimpleEntry<ChatColor, String>(ChatColor.RED, "terrible"));
			lev.add(new AbstractMap.SimpleEntry<ChatColor, String>(ChatColor.DARK_RED, "unplayable"));
		}
		
		@Override
		public boolean logic(Player sender, Command command, String label, String[] args) {
			SoundUtilities.playSoundTo("NOTE_PIANO", sender);
			ChatUtilities.logTo(sender, "Teknet speeds are currently rated \"" + returnStatus() + "\" clocking in at " + ChatColor.GOLD
					+ "" + TeknetCore.SERVER_TPS + ChatColor.GRAY + " tick(s) per second.", ChatUtilities.LogType.UTILITY);
			return true;
		}
		private static String returnStatus() {
			int index = Math.min(lev.size() - 1, Math.max(0, lev.size() - (int)((TeknetCore.SERVER_TPS / TARGET_TPS) * lev.size())));
			return ChatColor.BOLD + "" + (lev.get(index)).getKey() + (lev.get(index)).getValue() + ChatColor.RESET + ChatColor.GRAY;
		}
	}
	
	public static class CooldownCommand extends TeknetCoreCommand {
		public CooldownCommand(Rank permissions) {
			super(permissions);
		}
		@Override
		public boolean logic(Player sender, Command command, String label, String[] args) {
			if(Cooldown.can(sender)) {
				ChatUtilities.logTo(sender, "You have not recently been given a cooldown warning. You are allowed to chat and execute commands.", 
						ChatUtilities.LogType.NOTICE);
				return true;
			}
			ChatUtilities.logTo(sender, "You have to wait " + ChatColor.GREEN + Cooldown.timeTillReset(sender) + ChatColor.GRAY +
					" (s) before you can execute a command or chat!", ChatUtilities.LogType.NOTICE);
			return true;
		}
		
	}
	
}
