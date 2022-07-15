package io.github.iwilkey.teknetcore.economy;

import java.math.BigInteger;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.TeknetCoreCommand;
import io.github.iwilkey.teknetcore.location.Locations.Home.HomeData;
import io.github.iwilkey.teknetcore.ranks.Ranks.Rank;
import io.github.iwilkey.teknetcore.utils.ChatUtilities;
import io.github.iwilkey.teknetcore.utils.FileUtilities;
import io.github.iwilkey.teknetcore.utils.ChatUtilities.CommandDocumentation;
import io.github.iwilkey.teknetcore.utils.MathUtilities;
import io.github.iwilkey.teknetcore.utils.PlayerUtilities;

public class Bank {
	
	public static class Currency {
		
		private BigInteger amount;
		
		public Currency(BigInteger amount) {
			this.amount = amount;
		}
		
		public Currency(long amount) {
			this.amount = BigInteger.valueOf(amount);
		}
		
		public BigInteger getDollars() {
	        return amount.divide(BigInteger.valueOf(100));
	    }

	    public BigInteger getCents() {
	    	return amount.mod(BigInteger.valueOf(100));
	    }
	    
	    public BigInteger get() {
	    	return amount;
	    }
		
		public String printValueColored() {
			String dol = getDollars().toString();
			if(getDollars().compareTo(BigInteger.valueOf(1000)) >= 0) dol = dollarCommas();
			return ChatColor.GREEN + "$" + dol + "." + displayCents() + ChatColor.RESET;
		}
		public String printValue() {
			String dol = getDollars().toString();
			if(getDollars().compareTo(BigInteger.valueOf(1000)) >= 0) dol = dollarCommas();
			return "$" + dol + "." + displayCents();
		}
		private String displayCents() {
			if(getCents().compareTo(BigInteger.valueOf(9)) <= 0) return "0" + getCents().toString();
			return getCents().toString();
		}
		private String dollarCommas() {
			String dol = getDollars().toString();
			StringBuffer str = new StringBuffer(dol);
			int c = 0;
			for(int i = dol.length() - 1; i >= 0; i--) {
				c++;
				if(c % 3 == 0) {
					if(i == 0) break;
					str.insert(i, ',');
					c = 0;
				}
			}
			return str.toString();
		}
	}
	
	public static Currency returnRandomCurrencyOfValue(int value, float variance) {
		// Base = 10 ^ value
		// Variance += random in range(0, variance)%
		long base = (long)Math.pow(10, value + 1);
		double var = MathUtilities.randomDoubleBetween(0, variance);
		base += (base * (var / 100.0f));
		return new Currency(BigInteger.valueOf(base));
	}
	
	public static Currency add(Currency one, Currency two) {
		return new Currency(one.get().add(two.get()));
	}
	
	public static Currency subtract(Currency one, Currency two) {
		return new Currency(one.get().subtract(two.get()));
	}
	
	public static Currency multiply(Currency one, long scalar) {
		return new Currency(one.get().multiply(BigInteger.valueOf(scalar)));
	}
	
	// TeknetTrust...
	
	private static ArrayList<Account> TEKNET_TRUST_STATE;
	
	public static class BankCommand extends TeknetCoreCommand {

		public BankCommand(Rank permissions) {
			super("bank", permissions);
			
			Function seeAccount = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					Account a = null;
					if(args.length == 1) {
						a = getPlayerTeknetTrustAccount("GENERAL CHECKING", sender);
						if(a == null) a = createPlayerTeknetTrustAccount("GENERAL CHECKING", sender);
					}
					else {
						String search = "";
						for(int i = 1; i < args.length; i++) {
							search += args[i].toUpperCase();
							if(i != args.length - 1) search += " ";
						}
						a = getPlayerTeknetTrustAccount(search, sender);
						if(a == null) {
							ChatUtilities.logTo(sender, "You do not have an account registered under this name.", ChatUtilities.LogType.FATAL);
							return;
						}
					}
					a.printStatus(sender);
				};
				
			};
			
			registerFunction("account", seeAccount);
		}

		@Override
		protected void documentation(CommandDocumentation doc) {
			
		}

		@Override
		public boolean logic(Player sender, Command command, String label, String[] args) {
			return true;
		}
		
	}
	
	public Bank() {
		TEKNET_TRUST_STATE = new ArrayList<>();
		translateRegister();
	}
	
	public static class Account {
		public String name,
			playerName;
		public Currency amount;
		public Account(String name, String playerName) {
			this.playerName = playerName;
			this.name = name;
			amount = new Currency(0L);
		}
		public void printStatus(Player player) {
			ChatUtilities.tagAndMessageTo(player, "TeknetTrust", 
					"► " + ChatColor.GOLD + name + ": " + amount.printValueColored(),
					ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.GRAY);
		}
		
		public boolean add(Player player, Currency second) {
			amount = Bank.add(amount, second);
			ChatUtilities.tagAndMessageTo(player, "TeknetTrust", 
					"" + ChatColor.GREEN + " +$ -> " + ChatColor.GOLD + "" 
			+ ChatColor.LIGHT_PURPLE + name + ChatColor.GOLD, 
					ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.GOLD);
			writeRegister();
			return true;
		}
		
		public boolean subtract(Player player, Currency second) {
			Currency buffer = Bank.subtract(amount, second);
			if(buffer.get().compareTo(BigInteger.valueOf(0)) < 0) return false;
			amount = buffer;
			ChatUtilities.tagAndMessageTo(player, "TeknetTrust", 
					"" + ChatColor.DARK_RED + " -$ <- " + ChatColor.GOLD + "" 
			+ ChatColor.LIGHT_PURPLE + name + ChatColor.GOLD, 
					ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.GOLD);
			writeRegister();
			return true;
		}
		
	}
	
	public static Account createPlayerTeknetTrustAccount(String name, Player player) {
		if(accountExists(name, player)) {
			ChatUtilities.logTo(player, "You already own an account by this name!", ChatUtilities.LogType.FATAL);
			return null;
		}
		TEKNET_TRUST_STATE.add(new Account(name, player.getName()));
		writeRegister();
		return TEKNET_TRUST_STATE.get(TEKNET_TRUST_STATE.size() - 1);
	}
	
	public static Account createPlayerTeknetTrustAccount(String name, String playerName) {
		if(accountExists(name, playerName)) {
			ChatUtilities.logTo(PlayerUtilities.get(playerName), "You already own an account by this name!", ChatUtilities.LogType.FATAL);
			return null;
		}
		TEKNET_TRUST_STATE.add(new Account(name, playerName));
		writeRegister();
		return TEKNET_TRUST_STATE.get(TEKNET_TRUST_STATE.size() - 1);
	}
	
	public static Account createPlayerTeknetTrustAccount(String name, String playerName, Currency startFunds) {
		if(accountExists(name, playerName)) {
			ChatUtilities.logTo(PlayerUtilities.get(playerName), "You already own an account by this name!", ChatUtilities.LogType.FATAL);
			return null;
		}
		TEKNET_TRUST_STATE.add(new Account(name, playerName));
		Account a = TEKNET_TRUST_STATE.get(TEKNET_TRUST_STATE.size() - 1);
		a.amount = startFunds;
		writeRegister();
		return TEKNET_TRUST_STATE.get(TEKNET_TRUST_STATE.size() - 1);
	}
	
	public static Account getPlayerTeknetTrustAccount(String name, Player player) {
		for(Account a : TEKNET_TRUST_STATE) 
			if(a.name.equals(name) && a.playerName.equals(player.getName()))
				return a;
		return null;
	}
	
	private static boolean accountExists(String name, Player player) {
		for(Account a : TEKNET_TRUST_STATE) 
			if(a.name.equals(name) && a.playerName.equals(player.getName()))
				return true;
		return false;
	}
	
	private static boolean accountExists(String name, String playerName) {
		for(Account a : TEKNET_TRUST_STATE) 
			if(a.name.equals(name) && a.playerName.equals(playerName))
				return true;
		return false;
	}
	
	private static void translateRegister() {
		if(!FileUtilities.fileExists("trust")) FileUtilities.createDataFile("trust");
		TEKNET_TRUST_STATE.clear();
		ArrayList<String[]> data = FileUtilities.readDataFileLines("trust");
		for(String[] lineDat : data) {
			String name = "";
			for(int i = 2; i < lineDat.length; i++) {
				name += lineDat[i];
				if(i != lineDat.length - 1) 
					name += " ";
			}
			Account a = createPlayerTeknetTrustAccount(name, lineDat[0], new Currency(new BigInteger(lineDat[1], 10)));
			TEKNET_TRUST_STATE.add(a);
		}
	}
	
	private static void writeRegister() {
		FileUtilities.clearDataFile("trust");
		for(Account a : TEKNET_TRUST_STATE) {
			String data = a.playerName + " " + a.amount.get().toString(10) + " " + a.name;
			FileUtilities.appendDataEntryTo("trust", data);
		}
	}
}
