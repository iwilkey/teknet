package io.github.iwilkey.teknetcore.economy;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.iwilkey.teknetcore.TeknetCoreCommand;
import io.github.iwilkey.teknetcore.economy.Bank.Currency;
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
							ArrayList<Integer> pp = Store.catalog.searchFor(args[1].toUpperCase());
							if(pp.size() == 0) {
								ChatUtilities.logTo(sender, 
										"TeknetCore could not find your catalog search inquiry! "
										+ ChatColor.GOLD + "Please check spelling and make sure your inquiry has no spaces or special characters.", ChatUtilities.LogType.FATAL);
							} else {
								ChatUtilities.logTo(sender, 
										"Found " + pp.size() + " result(s) for inquiry\n" + 
												ChatColor.GOLD + args[1].toUpperCase() + ChatColor.GRAY + 
												"...", ChatUtilities.LogType.SUCCESS);
								String results = "To visit pages, use [shop-catalog-<page>] with <page>: ";
								boolean toggle = false;
								int ps = 0;
								for(int i : pp) {
									results += ((toggle) ? ChatColor.BLUE : ChatColor.DARK_AQUA) + Integer.toString(i + 1);
									if(ps != pp.size() - 1) results += ChatColor.GRAY + ", ";
									toggle = !toggle;
									ps++;
								}
								ChatUtilities.messageTo(sender, results, ChatColor.GRAY);
							}
						}
					}
				}
			};
			
			Function startSession = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					startShopSession(sender);
				}
			};
			
			Function endSession = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					Bank.Currency total = getCurrentShopSessionSubtotal(sender, false);
					if(total == null) return;
					Bank.Account a = Bank.getPlayerTeknetTrustAccount("GENERAL CHECKING", sender);
					ChatUtilities.messageTo(sender, 
							"+ Account chosen: " + a.name + " -> " + a.amount.printValueColored(), 
							ChatColor.GRAY);
					ChatUtilities.messageTo(sender, 
							"- Total due: " + total.printValueColored(), 
							ChatColor.GRAY);
					if(!a.subtract(sender, total)) {
						ChatUtilities.messageTo(sender, 
								"Payment declined! Please try a different method of payment or [shop-quit]", 
								ChatColor.DARK_RED);
						return;
					} else {

						ItemStack[] items = sender.getInventory().getContents();
						for(ItemStack i : items) {
							try {
								sender.getWorld().dropItem(sender.getLocation(), i);
							} catch(Exception e) {}
						}
					}
					stopShopSession(sender);
				}
			};
			
			Function subtotal = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					getCurrentShopSessionSubtotal(sender, true);
				}
			};
			
			Function quit = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					sender.getInventory().clear();
					stopShopSession(sender);
				}
			};
			
			registerFunction("catalog", prices, "search", "items");
			registerFunction("buy", startSession, 0, "b");
			registerFunction("checkout", endSession, 0, "c");
			registerFunction("subtotal", subtotal, 0, "sub", "s");
			registerFunction("quit", quit, 0, "q");
		}

		@Override
		protected void documentation(CommandDocumentation doc) {
			
		}

		@Override
		public boolean logic(Player sender, Command command, String label, String[] args) {
			return true;
		}
		
	}
	
	public static class ShopSession {
		public String playerName;
		public Location startedAt;
		public Inventory shopBasket;
		public ItemStack[] survivalInventory,
			survivalArmor;
		public ShopSession(Player player) {
			this.playerName = player.getName();
			startedAt = player.getLocation();
			survivalInventory = player.getInventory().getContents();
			survivalArmor = player.getInventory().getArmorContents();
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
				BUILDCRAFTCORE(1, 1),
				BUILDCRAFTTRANSPORT(2, 1),
				BUILDCRAFTSILICON(2, 1),
				BUILDCRAFTFACTORY(2, 1),
				COMPUTERCRAFT(5, 1),
				IC2(2, 1),
				FORESTRY(1, 1),
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
				BLUEPOWER(1, 1),
				CHICKENCHUNKS(9, 1),
				ENDERSTORAGE(4, 1),
				ENG_TOOLBOX(2, 1),
				IC2NUCLEARCONTROL(5, 1),
				POWERSUITS(7, 3),
				OCS(4, 1),
				POWERCONVERTERS3(4, 1),
				ZETTAINDUSTRIES(3, 1),
				LOGISTICSPIPES(2, 1),
				BUILDCRAFTCOMPAT(2, 1),
				FORGEMICROBLOCK(1, 1),
				AOBD(1, 1),
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
		private static ArrayList<ShopItem> itemsForSale = new ArrayList<>();
		public Store() {
			catagory = new ArrayList<>();
			itemsForSale = new ArrayList<>();
			init();
		}
		private Catagory findCatagory(String name) {
			for(Catagory c : catagory)
				if(c.name.equals(name))
					return c;
			return null;
		}
		public static ShopItem getShopItem(String itemName) {
			for(ShopItem i : itemsForSale) 
				if(i.materialName.equals(itemName))
					return i;
			return null;
		}
		
		public static CommandDocumentation catalog = new CommandDocumentation("TeknetCore Shop Catalog");
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
						if(mat.name().equals("ENCHANTMENT_TABLE") 
								|| mat.name().equals("BEDROCK") || mat.name().equals("TNT")) tag = "[DIAMOND]";
						int value = 0, sell = 0;
						switch(tag) {
							case "[BLOCK]": value = 1; sell = 0; break;
							case "[ITEM]": value = 1; sell = 0; break;
							case "[DIAMOND]": value = 3; sell = 2; break;
							case "[REDSTONE]": value = 1; sell = 0; break;
							case "[IRON]": value = 2; sell = 1; break;
							case "[GOLD]": value = 3; sell = 2; break;
							case "[COAL]": value = 2; sell = 1; break;
						}
						switch(type) {
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
						if(mat.name().equals("MOB_SPAWNER")) {
							value = 8; sell = 1;
						}
						cat.items.add(new ShopItem(line[0], cat, Bank.returnRandomCurrencyOfValue(value, 75.0f), 
								Bank.returnRandomCurrencyOfValue(sell, 30.0f)));
					} else {
						// Modded items.
						String[] tok = line[0].split("_");
						int value = cat.value.value + 1;
						for(String s : tok) {
							if(s.equals("COPPER") || s.equals("CABLE") 
									|| s.equals("WIRE") || s.equals("GEAR")) value = 1;
							if(s.equals("DIAMOND")) value = 3;
						}
						cat.items.add(new ShopItem(line[0], cat, Bank.returnRandomCurrencyOfValue(value, 250.0f), 
							Bank.returnRandomCurrencyOfValue(value - 1, 200.0f)));
					}
				}
			}
			for(Catagory c : catagory) {
				for(ShopItem s : c.items) {
					itemsForSale.add(s);
				}
			}
			boolean toggle = true;
			int pages = (int)((itemsForSale.size() * 2) / 8) + 1,
					item = 0;
			catalog.editPage(0).write(ChatColor.GOLD + "►►► Welcome to the TeknetCore Shop Catalog! ◄◄◄" + ChatColor.RESET, 0);
			catalog.editPage(0).write(" ► Use [shop-buy] or [shop-sell] to begin shopping!", 1);
			catalog.editPage(0).write(" ► To find a specific item, use the search feature!", 2);
			catalog.editPage(0).write(" ► " + ChatColor.GRAY + " Ex. [shop-catalog-diamond] " + ChatColor.GRAY + "will find all pages with", 3);
			catalog.editPage(0).write(ChatColor.GRAY + " the word or phrase " + ChatColor.GOLD + "\"DIAMOND\"" + ChatColor.GRAY + "...", 4);
			catalog.editPage(0).write(ChatColor.GRAY + " Then, simply use [shop-catalog-<page>] " + ChatColor.GRAY + "specifying one of the", 5);
			catalog.editPage(0).write(ChatColor.GRAY + " page numbers listed to find your desired buy and sell prices!", 6);
			catalog.editPage(0).write(ChatColor.RED + " Please do not use spaces or special characters in search!", 7);
			catalog.editPage(0).write("------- Search for an item! [shop-catalog-<item-name>] -------", 8);
			outter: for(int i = 1; i <= pages; i++) {
				catalog.addPage(new Page());
				for(int ii = 0; ii < 8; ii += 2) {
					if(item >= itemsForSale.size()) break outter;
					catalog.editPage(i).write(ChatColor.WHITE + " ► " + ((toggle) ? ChatColor.BLUE : ChatColor.DARK_AQUA) 
							+ itemsForSale.get(item).materialName.replace("_", " "), ii);
					catalog.editPage(i).write(ChatColor.GOLD + "    Buy: " + itemsForSale.get(item).price.printValueColored() 
							+ ChatColor.GOLD + ", Sell: " + itemsForSale.get(item).sellValue.printValueColored(), ii + 1);
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
	@SuppressWarnings("unused")
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
			ChatUtilities.logTo(player, "You're already in a shop session! Use [shop-checkout] to end it!", ChatUtilities.LogType.FATAL);
			return;
		}
		SHOP_SESSION_STATE.add(new ShopSession(player));
		ChatUtilities.logTo(player, "Shop session started!", ChatUtilities.LogType.SUCCESS);
	}
	
	public static void stopShopSession(Player player) {
		ShopSession s = getShopSessionOf(player);
		if(s == null) {
			ChatUtilities.logTo(player, "You're not in an active shop session! Use [shop-buy] or [shop-sell] to begin!", ChatUtilities.LogType.FATAL);
			return;
		}
		// Checkout function here...
		player.getInventory().setContents(s.survivalInventory);
		player.getInventory().setArmorContents(s.survivalArmor);
		player.setGameMode(GameMode.SURVIVAL);
		SHOP_SESSION_STATE.remove(s);
		ChatUtilities.logTo(player, "Shop checkout complete. Thank you for your patronage!", ChatUtilities.LogType.SUCCESS);
	}
	
	public static Bank.Currency getCurrentShopSessionSubtotal(Player player, boolean verbose) {
		ShopSession s = getShopSessionOf(player);
		if(s == null) {
			ChatUtilities.logTo(player, "You are not currently in a buy session!\nUse [shop-buy] to begin one.", 
					ChatUtilities.LogType.FATAL);
			return null;
		}
		ItemStack[] stack = player.getInventory().getContents();
		Currency subtotal = new Currency(0);
		for(ItemStack ss : stack) {
			try {
				Material mat = ss.getType();
				ShopItem item = Store.getShopItem(mat.name());
				if(item == null) continue;
				int amount = ss.getAmount();
				Currency full = Bank.multiply(item.price, amount);
				ChatUtilities.messageTo(player, "Item: " + mat.name() + 
						" is " + item.price.printValueColored() + ChatColor.GRAY + " x " + ChatColor.GOLD + amount + 
						ChatColor.GOLD + " = " + full.printValueColored(), 
						ChatColor.GRAY);
				subtotal = Bank.add(subtotal, full);
			} catch (Exception e) { continue; }
		}
		if(verbose) ChatUtilities.messageTo(player, ChatColor.DARK_GREEN + "Current subtotal: " + subtotal.printValueColored(),
				ChatColor.GRAY);
		return subtotal;
	}
	 
	public static ShopSession getShopSessionOf(Player player) {
		for(ShopSession s : SHOP_SESSION_STATE) 
			if(s.playerName.equals(player.getName()))
				return s;
		return null;
	}
}
