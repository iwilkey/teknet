package io.github.iwilkey.teknetcore.utils;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtilities {
	
	public static class CommandDocumentation {
		public static class Page {
			public String[] lines;
			public Page() {
				lines = new String[9];
				for(int i = 0; i < 9; i++) lines[i] = " ";
			}
			public void write(String content, int line) {
				if(line >= 9) return;
				if(content.length() >= 60) content = content.substring(0, 60);
				lines[line] = highlightCommands(content, ChatColor.WHITE);
			}
			public void renderTo(Player player) {
				for(int i = 0; i < 9; i++) 
					messageTo(player, lines[i], ChatColor.WHITE);
			}
		}
		// 10 lines to show.
		// <=60 characters per line
		// A document can have up to several pages.
		public String name;
		private ArrayList<Page> pages;
		public CommandDocumentation(String name) {
			this.name = name;
			pages = new ArrayList<>();
			Page page = new Page();
			page.write(ChatColor.GRAY + "Use [" + name.toLowerCase() + "-help-<n>] " + ChatColor.GRAY + " for page n of " + name.toLowerCase() + " help!" + ChatColor.RESET, 0);
			pages.add(page);
		}
		public void addPage(Page page) {
			pages.add(page);
		}
		public Page editPage(int page) {
			if(page >= pages.size()) return null;
			return pages.get(page);
		}
		public void renderPageTo(Player player, int page) {
			if(page >= pages.size()) return;
			messageTo(player, "------- " + name + ": Index (" + (page + 1) + "/" + pages.size() + ") -------", ChatColor.WHITE);
			pages.get(page).renderTo(player);
		}
	}
	
	public enum LogType {
		SUCCESS, NOTICE, FATAL, UTILITY
	}
	
	static final ChatColor r = ChatColor.RESET,
		b = ChatColor.BOLD;
	
	public static void tagAndMessageTo(Player player, String tag, String message, 
			ChatColor body, ChatColor outline, ChatColor messageColor) {
		String m = outline + "" + b + "[" + r + body + b + tag + r + outline + b + "] " + r + messageColor 
				+ highlightCommands(message, messageColor) + r;
		player.sendMessage(m);
	}
	
	public static void tagAndMessageOnline(String tag, String message, ChatColor body, 
			ChatColor outline, ChatColor messageColor) {
		String m = outline + "" + b + "[" + r + body + b + tag + r + outline + b + "] " + r + messageColor 
				+ highlightCommands(message, messageColor) + r;
		for(Player p : Bukkit.getOnlinePlayers())
			p.sendMessage(m);
	}
	
	public static void messageTo(Player player, String message, ChatColor textColor) {
		String m = textColor + highlightCommands(message, textColor) + r;
		player.sendMessage(m);
	}
	
	public static void messageOnline(String message, ChatColor textColor) {
		String m = textColor + highlightCommands(message, textColor) + r;
		for(Player p : Bukkit.getOnlinePlayers())
			p.sendMessage(m);
	}
	
	public static void logTo(Player player, String message, LogType type) {
		String m = highlightCommands(message, ChatColor.GRAY);
		switch(type) {
			case SUCCESS: ChatUtilities.tagAndMessageTo(player, "TeknetCore Success", m, ChatColor.GREEN, 
					ChatColor.DARK_GREEN, ChatColor.GRAY); break;
			case NOTICE: ChatUtilities.tagAndMessageTo(player, "TeknetCore Notice", m, ChatColor.YELLOW, 
					ChatColor.GOLD, ChatColor.GRAY); break;
			case FATAL: ChatUtilities.tagAndMessageTo(player, "TeknetCore Error", m, ChatColor.RED, 
					ChatColor.DARK_RED, ChatColor.GRAY); break;
			case UTILITY: ChatUtilities.tagAndMessageTo(player, "TeknetCore Utilities", m, ChatColor.BLUE, 
					ChatColor.DARK_BLUE, ChatColor.GRAY); break;
		}
	}
	
	public static String highlightCommands(String message, ChatColor reset) {
		String ret = "";
		String[] tok = message.split(" ");
		for(String t : tok) {
			if(t.length() == 0) {
				ret += " ";
				continue;
			}
			if(t.charAt(0) == '[' && t.charAt(t.length() - 1) == ']') {
				t = ChatColor.AQUA + "/" + t.substring(1, t.length() - 1) + reset;
				t = t.replace('-', ' ');
				t = t.replace('_', ' ');
			}
			ret += t + " ";
		}
		return ret;
	}
}