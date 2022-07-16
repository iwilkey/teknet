package io.github.iwilkey.teknetcore.eventlistener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

import io.github.iwilkey.teknetcore.TeknetCore;
import io.github.iwilkey.teknetcore.cooldown.Cooldown;
import io.github.iwilkey.teknetcore.economy.Shop;
import io.github.iwilkey.teknetcore.economy.Shop.ShopBuySession;
import io.github.iwilkey.teknetcore.ranks.Ranks;
import io.github.iwilkey.teknetcore.utils.ChatUtilities;

public class ServerEventListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
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
	
	@EventHandler(priority = EventPriority.HIGH)
	public static void onPlayerCommandRequest(PlayerCommandPreprocessEvent e) {
		if(e.getMessage().substring(0, 5).equals("/stop")) return;
		e.setMessage(e.getMessage().toLowerCase());
		if(e.getMessage().equals("/help") || e.getMessage().equals("/teknetcore help")) {
			TeknetCore.printAllHelp(e.getPlayer());
			e.setCancelled(true);
			return;
		}
		if(e.getMessage().equals("/cooldown")) return;
		Cooldown.registerActivity(e.getPlayer(), e.getMessage());
		if(!Cooldown.can(e.getPlayer())) {
			ChatUtilities.logTo(e.getPlayer(), "You need to wait " + ChatColor.GREEN + Cooldown.timeTillReset(e.getPlayer()) + 
					ChatColor.GRAY + " (s) before you can execute more commands! Use [cooldown] to see how much time you have left to wait.", ChatUtilities.LogType.FATAL);
			e.setCancelled(true);
			return;
		}
	}
	
	// Shop related events.
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onPlayerMove(PlayerMoveEvent e) {
		ShopBuySession s = Shop.getShopSessionOf(e.getPlayer());
		if(s != null) {
			if(e.getPlayer().getLocation().getX() != s.startedAt.getX() ||
					e.getPlayer().getLocation().getY() != s.startedAt.getY() ||
					e.getPlayer().getLocation().getZ() != s.startedAt.getZ()) {
				e.getPlayer().teleport(s.startedAt);
				ChatUtilities.messageTo(e.getPlayer(), 
						"You cannot move during while buying items!\n Done? [shop-checkout]", ChatColor.GRAY);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onPlayerItemDrop(PlayerDropItemEvent e) {
		ShopBuySession s = Shop.getShopSessionOf(e.getPlayer());
		if(s != null) {
			ChatUtilities.messageTo(e.getPlayer(), "You cannot drop items while buying items!\n Done? [shop-checkout]", ChatColor.GRAY);
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onPlayerInteraction(PlayerInteractEvent e) {
		ShopBuySession s = Shop.getShopSessionOf(e.getPlayer());
		if(s != null) {
			ChatUtilities.messageTo(e.getPlayer(), "You cannot interact with anything while buying items!\n Done? [shop-checkout]", ChatColor.GRAY);
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onPlayerLogin(PlayerLoginEvent e) {
		e.getPlayer().setGameMode(GameMode.SURVIVAL);
		ShopBuySession s = Shop.getShopSessionOf(e.getPlayer());
		if(s != null) Shop.stopShopSession(e.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onPlayerJoin(PlayerJoinEvent e) {
		e.getPlayer().setGameMode(GameMode.SURVIVAL);
		ShopBuySession s = Shop.getShopSessionOf(e.getPlayer());
		if(s != null) Shop.stopShopSession(e.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onPlayerKick(PlayerKickEvent e) {
		e.getPlayer().setGameMode(GameMode.SURVIVAL);
		ShopBuySession s = Shop.getShopSessionOf(e.getPlayer());
		if(s != null) Shop.stopShopSession(e.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onPlayerLeave(PlayerQuitEvent e) {
		ShopBuySession s = Shop.getShopSessionOf(e.getPlayer());
		if(s != null) Shop.stopShopSession(e.getPlayer());
	}
	
	// TODO: Patch a shop cheat where if you "lag" out while in a shop session, you will respawn with all the items.
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onPluginDisable(PluginDisableEvent e) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			ShopBuySession s = Shop.getShopSessionOf(p);
			if(s != null) {
				Shop.stopShopSession(p);
				ChatUtilities.logTo(p, ChatColor.GOLD + "To prevent from an undesirable or unintended occurance during routine TeknetCore maintenance, your shop session has been safely ended. Please wait about 10 (s) and try again.", ChatUtilities.LogType.NOTICE);
			}
		}
	}
	
}
