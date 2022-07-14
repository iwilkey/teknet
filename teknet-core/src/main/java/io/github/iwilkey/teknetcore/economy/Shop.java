package io.github.iwilkey.teknetcore.economy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import io.github.iwilkey.teknetcore.TeknetCoreCommand;
import io.github.iwilkey.teknetcore.economy.Shop.Store.Catagory.ShopItem;
import io.github.iwilkey.teknetcore.ranks.Ranks.Rank;
import io.github.iwilkey.teknetcore.utils.ChatUtilities;
import io.github.iwilkey.teknetcore.utils.ChatUtilities.CommandDocumentation;
import io.github.iwilkey.teknetcore.utils.ChatUtilities.CommandDocumentation.Page;
import io.github.iwilkey.teknetcore.utils.FileUtilities;

public class Shop {
	
	public static class ShopCommand extends TeknetCoreCommand {

		public ShopCommand(Rank permissions) {
			super("shop", permissions);
			
			Function prices = new Function() {

				@Override
				public void func(Player sender, String[] args) {
					if(args.length == 1) Store.showItemsForSale(sender, 1);
					else {
						int page;
						try {
							page = Integer.parseInt(args[1]);
							Store.showItemsForSale(sender, page);
						} catch(Exception e) {
							ChatUtilities.logTo(sender, 
									"Searching catalog for " + args[1].toUpperCase() + "...", ChatUtilities.LogType.UTILITY);
							int pp = Store.catalog.searchFor(args[1].toUpperCase());
							if(pp == -1) {
								ChatUtilities.logTo(sender, 
										"TeknetCore could not find your catalog search inquiry. Please check spelling and try again.", ChatUtilities.LogType.FATAL);
							} else {
								Store.showItemsForSale(sender, pp + 1);
							}
						}
					}
				}
			};
			
			registerFunction("prices", prices, 1);
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
	
	public static class Store {
		public final static String PATH_TO_SHOP_DATA = "shop";
		public static class Catagory {
			public static class ShopItem {
				public String materialName;
				public Catagory catagory;
				public Bank.Currency price,
					sellValue;
				public ShopItem(String materialName, Catagory catagory, Bank.Currency price, Bank.Currency sell) {
					this.materialName = materialName;
					this.catagory = catagory;
					this.price = price;
					this.sellValue = sell;
				}
			}
			public enum Value {
				MINECRAFT(3, 1),
				BUILDCRAFTCORE(2, 1),
				BUILDCRAFTTRANSPORT(2, 1),
				BUILDCRAFTSILICON(2, 1),
				BUILDCRAFTFACTORY(2, 1),
				COMPUTERCRAFT(5, 1),
				IC2(3, 1),
				FORESTRY(2, 1),
				BIGREACTORS(5, 1),
				BUILDCRAFTBUILDERS(5, 1),
				BUILDCRAFTENERGY(5, 1),
				BUILDCRAFTROBOTICS(5, 1),
				CARPENTERSBLOCKS(1, 0),
				COMPUTERCRAFTEDU(2, 0),
				RAILCRAFT(2, 1),
				COMPUTRONICS(4, 1),
				FORGEMULTIPART(1, 0),
				GENDUSTRY(3, 1),
				IMMIBISPERIPHERALS(4, 1),
				IRONCHEST(5, 1),
				JABBA(3, 1),
				NETHERORES(4, 1),
				PROJECTE(5, 2),
				QUIVERCHEVSKY(4, 2),
				REDSTONEARSENAL(3, 1),
				JAKJ_REDSTONEINMOTION(2, 1),
				SOLARFLUX(4, 1),
				TUBESTUFF(4, 1),
				ADDITIONALPIPES(4, 0),
				QMUNITYLIB(4, 1),
				BLUEPOWER(2, 1),
				CHICKENCHUNKS(9, 1),
				ENDERSTORAGE(4, 1),
				ENG_TOOLBOX(4, 1),
				IC2NUCLEARCONTROL(5, 1),
				POWERSUITS(7, 3),
				OCS(4, 1),
				POWERCONVERTERS3(4, 1),
				ZETTAINDUSTRIES(3, 1),
				LOGISTICSPIPES(5, 1),
				BUILDCRAFTCOMPAT(5, 1),
				FORGEMICROBLOCK(1, 1),
				AOBD(3, 1),
				UNKNOWN(4, 1);
				public final int value, sell;
				private Value(int value, int sell) {
					this.value = value;
					this.sell = sell;
				}
			}
			public String name;
			public Value value;
			public ArrayList<ShopItem> items;
			public Catagory(String name) {
				this.name = name;
				boolean NOT_FOUND = true;
				for(Value v : Value.values())
					if(v.name().equals(name)) {
						value = v;
						NOT_FOUND = false;
						break;
					}
				if(NOT_FOUND) value = Value.UNKNOWN;
				items = new ArrayList<>();
			}
		}
		
		private static ArrayList<Catagory> catagory;
		public Store() {
			catagory = new ArrayList<>();
			init();
		}
		private Catagory findCatagory(String name) {
			for(Catagory c : catagory)
				if(c.name.equals(name))
					return c;
			return null;
		}
		
		public static CommandDocumentation catalog = new CommandDocumentation("Catalog");
		private void init() {
			ArrayList<String[]> lines = FileUtilities.readDataFileLines(PATH_TO_SHOP_DATA);
			Catagory cat = new Catagory("MINECRAFT");
			for(String[] line : lines) {
				if(line[0].substring(0, 2).equals("::")) {
					cat = findCatagory(line[0].substring(2, line[0].length()));
					if(cat == null) {
						catagory.add(new Catagory(line[0].substring(2, line[0].length())));
						cat = findCatagory(line[0].substring(2, line[0].length()));
					}
				} else { 
					// Minecraft items.
					if(cat.name.equals("MINECRAFT")) {
						Material mat = Material.matchMaterial(line[0]);
						String tag = "";
						if(mat.isBlock()) tag = "[BLOCK]";
						String[] iden = mat.name().split("_");
						String identity = iden[0],
								type = iden[iden.length - 1];
						switch(identity) {
							case "DIAMOND": tag = "[DIAMOND]"; break;
							case "IRON": tag = "[IRON]"; break;
							case "GOLD": tag = "[GOLD]"; break;
							case "REDSTONE": tag = "[REDSTONE]"; break;
							case "COAL": tag = "[COAL]"; break;
						}
						if(tag.equals("")) tag = "[ITEM]";
						if(mat.name().equals("ENCHANTMENT_TABLE") || mat.name().equals("BEDROCK") || mat.name().equals("TNT")) tag = "[DIAMOND]";
						
						int value = 0, sell = 0;
						switch(tag) {
							case "[BLOCK]": value = 1; sell = 0; break;
							case "[ITEM]": value = 1; sell = 0; break;
							case "[DIAMOND]": value = 4; sell = 1; break;
							case "[REDSTONE]": value = 3; sell = 1; break;
							case "[IRON]": value = 2; sell = 1; break;
							case "[GOLD]": value = 3; sell = 1; break;
							case "[COAL]": value = 2; sell = 1; break;
						}
						switch(tag) {
							case "BLOCK": value++; sell++; break;
							case "SWORD": value++; break;
							case "PICKAXE": value++; break;
							case "AXE": value++; break;
							case "SHOVEL": value++; break;
							case "HOE": value++; break;
							case "HELMET": value++; break;
							case "CHESTPLATE": value++; break;
							case "LEGGINGS": value++; break;
							case "BOOTS": value++; break;
						}
						if(mat.name().equals("MOB_SPAWNER")) value = 8; sell = 1;
						cat.items.add(new ShopItem(line[0], cat, Bank.returnRandomCurrencyOfValue(value, 75.0f), 
								Bank.returnRandomCurrencyOfValue(sell, 5.0f)));
					} else {
						// Modded items.
						cat.items.add(new ShopItem(line[0], cat, Bank.returnRandomCurrencyOfValue(cat.value.value + 1, 250.0f), 
							Bank.returnRandomCurrencyOfValue(cat.value.value - 2, 200.0f)));
					}
				}
			}
			ArrayList<String> itemsForSale = new ArrayList<>();
			
			for(Catagory c : catagory) {
				for(ShopItem s : c.items) {
					String[] tok = s.materialName.split("_");
					String name = "";
					if(tok.length >= 2) {
						for(int i = 1; i < tok.length; i++)
							name += tok[i];
						name = name.replace('_', ' ');
					} else name = s.materialName.replace('_', ' ');
					itemsForSale.add(name + ChatColor.GRAY + ", Buy: " + s.price.printValueColored() + ChatColor.GRAY + " Sell: " + s.sellValue.printValueColored());
				}
			}
			Collections.sort(itemsForSale);
			boolean toggle = true;
			int pages = (int)(itemsForSale.size() / 9) + 1,
					item = 0;
			outter: for(int i = 1; i <= pages; i++) {
				catalog.addPage(new Page());
				for(int ii = 0; ii < 9; ii++) {
					if(item >= itemsForSale.size()) break outter;
					catalog.editPage(i - 1).write(((toggle) ? ChatColor.BLUE : ChatColor.DARK_AQUA) + itemsForSale.get(item), ii);
					toggle = !toggle;
					item++;
				}
			}
		}

		public static void showItemsForSale(Player player, int page) {
			catalog.renderPageTo(player, page - 1);
		}
	}
	
	public static ArrayList<ShopSession> SHOP_SESSION_STATE;
	private static Store STORE;
	
	public Shop() {
		STORE = new Store();
		SHOP_SESSION_STATE = new ArrayList<>();
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
