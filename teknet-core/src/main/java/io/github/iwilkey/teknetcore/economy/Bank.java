package io.github.iwilkey.teknetcore.economy;

import java.math.BigInteger;

import org.bukkit.ChatColor;

import io.github.iwilkey.teknetcore.utils.MathUtilities;

public class Bank {
	
	public static class Currency {
		
		private BigInteger amount;
		
		public Currency(BigInteger amount) {
			this.amount = amount;
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
	
}
