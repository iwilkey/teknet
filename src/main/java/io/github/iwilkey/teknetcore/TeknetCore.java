package io.github.iwilkey.teknetcore;

import org.bukkit.plugin.java.JavaPlugin;

import io.github.iwilkey.teknetcore.home.HomeCommand;
import io.github.iwilkey.teknetcore.home.Home;
import io.github.iwilkey.teknetcore.home.SetHomeCommand;

public final class TeknetCore extends JavaPlugin {
	
	// For commands such as sethome, home, ect...
	private final Home homeCore = new Home();
	
	@Override
	public void onEnable() {
		// HomeCore...
		getCommand("sethome").setExecutor(new SetHomeCommand(homeCore));
		getCommand("home").setExecutor(new HomeCommand(homeCore));

		getLogger().info("TeknetCore initialized!");
	}
	@Override
	public void onDisable() {
		getLogger().info("TeknetCore terminated.");
	}

}
