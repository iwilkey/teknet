package io.github.iwilkey.teknetcore.utils;

import java.util.Random;

public class MathUtilities {
	public static int randomIntBetween(int low, int high) {
		Random r = new Random();
		return r.nextInt(high - low) + low;
	}
}