package io.github.iwilkey.teknetcore.economy;

import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
// import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import io.github.iwilkey.teknetcore.TeknetCoreCommand;
import io.github.iwilkey.teknetcore.ranks.Ranks.Rank;
import io.github.iwilkey.teknetcore.utils.ChatUtilities;
import io.github.iwilkey.teknetcore.utils.ChatUtilities.CommandDocumentation;

public class Shop {
	
	public static class ShopCommand extends TeknetCoreCommand {

		public ShopCommand(Rank permissions) {
			super("shop", permissions);
		}

		@Override
		protected void documentation(CommandDocumentation doc) {
			
		}

		@Override
		public boolean logic(Player sender, Command command, String label, String[] args) {
			startShopSession(sender);
			ChatUtilities.logTo(sender, "Shop session started!", ChatUtilities.LogType.SUCCESS);
			return true;
		}
		
	}
	
	public static class ShopSession {
		public String playerName;
		public Location startedAt;
		public Inventory survivalInventory,
			shopBasket;
		public ShopSession(Player player) {
			this.playerName = player.getName();
			startedAt = player.getLocation();
			survivalInventory = player.getInventory();
			shopBasket = Bukkit.createInventory(player, InventoryType.PLAYER, "Shop Basket");
			player.getInventory().setContents(shopBasket.getContents());
			player.setGameMode(GameMode.CREATIVE);
		}
	}
	
	public static ArrayList<ShopSession> SHOP_SESSION_STATE;
	
	public Shop() {
		SHOP_SESSION_STATE = new ArrayList<>();
		// List<Material> list = Arrays.asList(Material.values());
		/*
		for(Material m : list) {
			m.getCreativeCategory();
		}
		*/
	}
	
	public static void tick() {
		
	}
	
	public static void startShopSession(Player player) {
		ShopSession s = getShopSessionOf(player);
		if(s != null) {
			ChatUtilities.logTo(player, "You're already in a shop session!", ChatUtilities.LogType.FATAL);
			return;
		}
		SHOP_SESSION_STATE.add(new ShopSession(player));
	}
	 
	public static ShopSession getShopSessionOf(Player player) {
		for(ShopSession s : SHOP_SESSION_STATE) 
			if(s.playerName.equals(player.getName()))
				return s;
		return null;
	}
	
}
