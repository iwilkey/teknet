package io.github.iwilkey.teknetcore.utils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtilities {
	
	/*
	 * [03:55:25 INFO]: AMBIENCE_CAVE
		[03:55:25 INFO]: AMBIENCE_RAIN
		[03:55:25 INFO]: AMBIENCE_THUNDER
		[03:55:25 INFO]: ANVIL_BREAK
		[03:55:25 INFO]: ANVIL_LAND
		[03:55:25 INFO]: ANVIL_USE
		[03:55:25 INFO]: ARROW_HIT
		[03:55:25 INFO]: BURP
		[03:55:25 INFO]: CHEST_CLOSE
		[03:55:25 INFO]: CHEST_OPEN
		[03:55:25 INFO]: CLICK
		[03:55:25 INFO]: DOOR_CLOSE
		[03:55:25 INFO]: DOOR_OPEN
		[03:55:25 INFO]: DRINK
		[03:55:25 INFO]: EAT
		[03:55:25 INFO]: EXPLODE
		[03:55:25 INFO]: FALL_BIG
		[03:55:25 INFO]: FALL_SMALL
		[03:55:25 INFO]: FIRE
		[03:55:25 INFO]: FIRE_IGNITE
		[03:55:25 INFO]: FIZZ
		[03:55:25 INFO]: FUSE
		[03:55:25 INFO]: GLASS
		[03:55:25 INFO]: HURT_FLESH
		[03:55:25 INFO]: ITEM_BREAK
		[03:55:25 INFO]: ITEM_PICKUP
		[03:55:25 INFO]: LAVA
		[03:55:25 INFO]: LAVA_POP
		[03:55:25 INFO]: LEVEL_UP
		[03:55:25 INFO]: MINECART_BASE
		[03:55:25 INFO]: MINECART_INSIDE
		[03:55:25 INFO]: NOTE_BASS
		[03:55:25 INFO]: NOTE_PIANO
		[03:55:25 INFO]: NOTE_BASS_DRUM
		[03:55:25 INFO]: NOTE_STICKS
		[03:55:25 INFO]: NOTE_BASS_GUITAR
		[03:55:25 INFO]: NOTE_SNARE_DRUM
		[03:55:25 INFO]: NOTE_PLING
		[03:55:25 INFO]: ORB_PICKUP
		[03:55:25 INFO]: PISTON_EXTEND
		[03:55:25 INFO]: PISTON_RETRACT
		[03:55:25 INFO]: PORTAL
		[03:55:25 INFO]: PORTAL_TRAVEL
		[03:55:25 INFO]: PORTAL_TRIGGER
		[03:55:25 INFO]: SHOOT_ARROW
		[03:55:25 INFO]: SPLASH
		[03:55:25 INFO]: SPLASH2
		[03:55:25 INFO]: STEP_GRASS
		[03:55:25 INFO]: STEP_GRAVEL
		[03:55:25 INFO]: STEP_LADDER
		[03:55:25 INFO]: STEP_SAND
		[03:55:25 INFO]: STEP_SNOW
		[03:55:25 INFO]: STEP_STONE
		[03:55:25 INFO]: STEP_WOOD
		[03:55:25 INFO]: STEP_WOOL
		[03:55:25 INFO]: SWIM
		[03:55:25 INFO]: WATER
		[03:55:25 INFO]: WOOD_CLICK
		[03:55:25 INFO]: BAT_DEATH
		[03:55:25 INFO]: BAT_HURT
		[03:55:25 INFO]: BAT_IDLE
		[03:55:25 INFO]: BAT_LOOP
		[03:55:25 INFO]: BAT_TAKEOFF
		[03:55:25 INFO]: BLAZE_BREATH
		[03:55:25 INFO]: BLAZE_DEATH
		[03:55:25 INFO]: BLAZE_HIT
		[03:55:25 INFO]: CAT_HISS
		[03:55:25 INFO]: CAT_HIT
		[03:55:25 INFO]: CAT_MEOW
		[03:55:25 INFO]: CAT_PURR
		[03:55:25 INFO]: CAT_PURREOW
		[03:55:25 INFO]: CHICKEN_IDLE
		[03:55:25 INFO]: CHICKEN_HURT
		[03:55:25 INFO]: CHICKEN_EGG_POP
		[03:55:25 INFO]: CHICKEN_WALK
		[03:55:25 INFO]: COW_IDLE
		[03:55:25 INFO]: COW_HURT
		[03:55:25 INFO]: COW_WALK
		[03:55:25 INFO]: CREEPER_HISS
		[03:55:25 INFO]: CREEPER_DEATH
		[03:55:25 INFO]: ENDERDRAGON_DEATH
		[03:55:25 INFO]: ENDERDRAGON_GROWL
		[03:55:25 INFO]: ENDERDRAGON_HIT
		[03:55:25 INFO]: ENDERDRAGON_WINGS
		[03:55:25 INFO]: ENDERMAN_DEATH
		[03:55:25 INFO]: ENDERMAN_HIT
		[03:55:25 INFO]: ENDERMAN_IDLE
		[03:55:25 INFO]: ENDERMAN_TELEPORT
		[03:55:25 INFO]: ENDERMAN_SCREAM
		[03:55:25 INFO]: ENDERMAN_STARE
		[03:55:25 INFO]: GHAST_SCREAM
		[03:55:25 INFO]: GHAST_SCREAM2
		[03:55:25 INFO]: GHAST_CHARGE
		[03:55:25 INFO]: GHAST_DEATH
		[03:55:25 INFO]: GHAST_FIREBALL
		[03:55:25 INFO]: GHAST_MOAN
		[03:55:25 INFO]: IRONGOLEM_DEATH
		[03:55:25 INFO]: IRONGOLEM_HIT
		[03:55:25 INFO]: IRONGOLEM_THROW
		[03:55:25 INFO]: IRONGOLEM_WALK
		[03:55:25 INFO]: MAGMACUBE_WALK
		[03:55:25 INFO]: MAGMACUBE_WALK2
		[03:55:25 INFO]: MAGMACUBE_JUMP
		[03:55:25 INFO]: PIG_IDLE
		[03:55:25 INFO]: PIG_DEATH
		[03:55:25 INFO]: PIG_WALK
		[03:55:25 INFO]: SHEEP_IDLE
		[03:55:25 INFO]: SHEEP_SHEAR
		[03:55:25 INFO]: SHEEP_WALK
		[03:55:25 INFO]: SILVERFISH_HIT
		[03:55:25 INFO]: SILVERFISH_KILL
		[03:55:25 INFO]: SILVERFISH_IDLE
		[03:55:25 INFO]: SILVERFISH_WALK
		[03:55:25 INFO]: SKELETON_IDLE
		[03:55:25 INFO]: SKELETON_DEATH
		[03:55:25 INFO]: SKELETON_HURT
		[03:55:25 INFO]: SKELETON_WALK
		[03:55:25 INFO]: SLIME_ATTACK
		[03:55:25 INFO]: SLIME_WALK
		[03:55:25 INFO]: SLIME_WALK2
		[03:55:25 INFO]: SPIDER_IDLE
		[03:55:25 INFO]: SPIDER_DEATH
		[03:55:25 INFO]: SPIDER_WALK
		[03:55:25 INFO]: WITHER_DEATH
		[03:55:25 INFO]: WITHER_HURT
		[03:55:25 INFO]: WITHER_IDLE
		[03:55:25 INFO]: WITHER_SHOOT
		[03:55:25 INFO]: WITHER_SPAWN
		[03:55:25 INFO]: WOLF_BARK
		[03:55:25 INFO]: WOLF_DEATH
		[03:55:25 INFO]: WOLF_GROWL
		[03:55:25 INFO]: WOLF_HOWL
		[03:55:25 INFO]: WOLF_HURT
		[03:55:25 INFO]: WOLF_PANT
		[03:55:25 INFO]: WOLF_SHAKE
		[03:55:25 INFO]: WOLF_WALK
		[03:55:25 INFO]: WOLF_WHINE
		[03:55:25 INFO]: ZOMBIE_METAL
		[03:55:25 INFO]: ZOMBIE_WOOD
		[03:55:25 INFO]: ZOMBIE_WOODBREAK
		[03:55:25 INFO]: ZOMBIE_IDLE
		[03:55:25 INFO]: ZOMBIE_DEATH
		[03:55:25 INFO]: ZOMBIE_HURT
		[03:55:25 INFO]: ZOMBIE_INFECT
		[03:55:25 INFO]: ZOMBIE_UNFECT
		[03:55:25 INFO]: ZOMBIE_REMEDY
		[03:55:25 INFO]: ZOMBIE_WALK
		[03:55:25 INFO]: ZOMBIE_PIG_IDLE
		[03:55:25 INFO]: ZOMBIE_PIG_ANGRY
		[03:55:25 INFO]: ZOMBIE_PIG_DEATH
		[03:55:25 INFO]: ZOMBIE_PIG_HURT
		[03:55:25 INFO]: DIG_WOOL
		[03:55:25 INFO]: DIG_GRASS
		[03:55:25 INFO]: DIG_GRAVEL
		[03:55:25 INFO]: DIG_SAND
		[03:55:25 INFO]: DIG_SNOW
		[03:55:25 INFO]: DIG_STONE
		[03:55:25 INFO]: DIG_WOOD
		[03:55:25 INFO]: FIREWORK_BLAST
		[03:55:25 INFO]: FIREWORK_BLAST2
		[03:55:25 INFO]: FIREWORK_LARGE_BLAST
		[03:55:25 INFO]: FIREWORK_LARGE_BLAST2
		[03:55:25 INFO]: FIREWORK_TWINKLE
		[03:55:25 INFO]: FIREWORK_TWINKLE2
		[03:55:25 INFO]: FIREWORK_LAUNCH
		[03:55:25 INFO]: SUCCESSFUL_HIT
		[03:55:25 INFO]: HORSE_ANGRY
		[03:55:25 INFO]: HORSE_ARMOR
		[03:55:25 INFO]: HORSE_BREATHE
		[03:55:25 INFO]: HORSE_DEATH
		[03:55:25 INFO]: HORSE_GALLOP
		[03:55:25 INFO]: HORSE_HIT
		[03:55:25 INFO]: HORSE_IDLE
		[03:55:25 INFO]: HORSE_JUMP
		[03:55:25 INFO]: HORSE_LAND
		[03:55:25 INFO]: HORSE_SADDLE
		[03:55:25 INFO]: HORSE_SOFT
		[03:55:25 INFO]: HORSE_WOOD
		[03:55:25 INFO]: DONKEY_ANGRY
		[03:55:25 INFO]: DONKEY_DEATH
		[03:55:25 INFO]: DONKEY_HIT
		[03:55:25 INFO]: DONKEY_IDLE
		[03:55:25 INFO]: HORSE_SKELETON_DEATH
		[03:55:25 INFO]: HORSE_SKELETON_HIT
		[03:55:25 INFO]: HORSE_SKELETON_IDLE
		[03:55:25 INFO]: HORSE_ZOMBIE_DEATH
		[03:55:25 INFO]: HORSE_ZOMBIE_HIT
		[03:55:25 INFO]: HORSE_ZOMBIE_IDLE
		[03:55:25 INFO]: VILLAGER_DEATH
		[03:55:25 INFO]: VILLAGER_HAGGLE
		[03:55:25 INFO]: VILLAGER_HIT
		[03:55:25 INFO]: VILLAGER_IDLE
		[03:55:25 INFO]: VILLAGER_NO
		[03:55:25 INFO]: VILLAGER_YES
	 */
	
	public static boolean playSoundTo(String name, Player to) {
		for(Sound f : Sound.values()) 
			if(f.name().contains(name)) {
				to.playSound(to.getLocation(), f, 100.0f, 0.0f);
				return true;
			}
		System.out.println("Sound not found!");
		return false;
	}
	
	public static boolean playSoundToOnline(String name) {
		for(Sound f : Sound.values()) 
			if(f.name().contains(name)) {
				for(Player p : Bukkit.getOnlinePlayers())
					p.playSound(p.getLocation(), f, 100.0f, 0.0f);
				return true;
			}
		System.out.println("Sound not found!");
		return false;
	}

}
