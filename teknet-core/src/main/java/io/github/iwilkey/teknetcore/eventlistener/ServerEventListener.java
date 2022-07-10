package io.github.iwilkey.teknetcore.eventlistener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import io.github.iwilkey.teknetcore.cooldown.Cooldown;
import io.github.iwilkey.teknetcore.ranks.Ranks;
import io.github.iwilkey.teknetcore.utils.ChatUtilities;

public class ServerEventListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onPlayerChat(AsyncPlayerChatEvent e) { 
		Cooldown.registerActivity(e.getPlayer(), "CHAT_EVENT");
		if(!Cooldown.can(e.getPlayer())) {
			ChatUtilities.logTo(e.getPlayer(), "You need to wait " + ChatColor.GREEN + Cooldown.timeTillReset(e.getPlayer()) + 
					ChatColor.GRAY + " (s) before you can send more chats! Use [cooldown] to see how much time you have left to wait.", ChatUtilities.LogType.FATAL);
			e.setCancelled(true);
			return;
		}
		String out = Ranks.tag(e.getPlayer()) + ChatColor.RESET + e.getPlayer().getName() + ": " + ChatUtilities.highlightCommands(e.getMessage(), ChatColor.WHITE);
		Bukkit.broadcastMessage(out);
		e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onPlayerCommandRequest(PlayerCommandPreprocessEvent e) {
		if(e.getMessage().equals("/cooldown")) return;
		Cooldown.registerActivity(e.getPlayer(), e.getMessage());
		if(!Cooldown.can(e.getPlayer())) {
			ChatUtilities.logTo(e.getPlayer(), "You need to wait " + ChatColor.GREEN + Cooldown.timeTillReset(e.getPlayer()) + 
					ChatColor.GRAY + " (s) before you can execute more commands! Use [cooldown] to see how much time you have left to wait.", ChatUtilities.LogType.FATAL);
			e.setCancelled(true);
			return;
		}
	}
	
}
