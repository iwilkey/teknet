package io.github.iwilkey.teknetcore.eventlistener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import io.github.iwilkey.teknetcore.announcer.AnnounceUtils;
import io.github.iwilkey.teknetcore.back.DeathUtils;
import io.github.iwilkey.teknetcore.security.Security;

public class ServerEventListener implements Listener {
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if(event.getEntity() instanceof Player) {
			Player player = (Player)event.getEntity();
			AnnounceUtils.required(player.getName() + " just ate shit!", player);
			player.sendMessage("Return to the location of your death with " + ChatColor.RED + "/back" + ChatColor.RESET + "!");
			DeathUtils.logDeathOf(player);
		}
	}
	
	@EventHandler
    public void onQuestionableItemPlace(BlockPlaceEvent e){
		if(e.getBlockPlaced().getType().equals(Material.LAVA)) {
			Security.writeWarningTo(e.getPlayer(), "While lava isn't a banned liquid, "
					+ "it can still be used unscrupulously. Please be considerate of others.");
		} else if(e.getBlock().getType().toString().equals("IC2_NUKE") || e.getBlock().getType().toString().equals("TNT") 
				|| e.getBlock().getType().toString().equals("IC2_INDUSTRIALTNT")) {
			e.getBlock().setType(Material.DIRT);
			Security.writeWarningTo(e.getPlayer(), "While explosives are not banned items to create, "
					+ "more often than not they are used to be extremely destructive. "
					+ "Please be considerate of others.");
		}
    }
	
	@EventHandler
	public void onSpread(BlockSpreadEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onLiquidSpread(BlockFromToEvent e) {
		e.setCancelled(true);
	}
	
}
