package io.github.iwilkey.teknetcore;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.ranks.Ranks;
import io.github.iwilkey.teknetcore.ranks.Ranks.Rank;
import io.github.iwilkey.teknetcore.utils.ChatUtilities;
import io.github.iwilkey.teknetcore.utils.SoundUtilities;
import io.github.iwilkey.teknetcore.utils.ChatUtilities.CommandDocumentation;

public abstract class TeknetCoreCommand implements CommandExecutor {
	public static abstract class AdminTeknetCoreCommand extends TeknetCoreCommand {
		public AdminTeknetCoreCommand(String name, Rank permissions) {
			super(name, permissions);
		}
	}
	protected enum CommandError {
		INVALID_SYNTAX,
		INVALID_ARGUMENTS,
		NO_ARGUMENTS,
		NO_SUCH_COMMAND
	}
	protected static interface Function {
		public void func(Player sender, String[] args);
	}
	protected static class CommandFunction {
		public String command;
		public Function function;
		int subArgs;
		public CommandFunction(String name, Function function, int subArgs) {
			this.command = name;
			this.subArgs = subArgs;
			this.function = function;
		}
	}
	protected ArrayList<CommandFunction> registeredFunctions;
	private static Ranks.Rank permissions; 
	protected CommandDocumentation doc;
	protected String commandName;
	@SuppressWarnings("static-access")
	public TeknetCoreCommand(String name, Ranks.Rank permissions) {
		this.permissions = permissions;
		this.commandName = name;
		doc = new CommandDocumentation(name);
		documentation(doc);
		registeredFunctions = new ArrayList<>();
	}
	protected void registerFunction(String name, Function function, int subArgs) {
		registeredFunctions.add(new CommandFunction(name, function, subArgs));
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		if(!Ranks.canUseFeature((Player)sender, permissions)) {
			if(!(sender.getName().equals("iwilkey"))) {
				SoundUtilities.playSoundTo("VILLAGER_NO", (Player)sender);
				ChatUtilities.logTo((Player)sender, "You cannot use this feature because you are not a high enough rank.", ChatUtilities.LogType.FATAL);
				return true;
			}
		}
		if(args.length == 0) return invoke((Player)sender, command, label, args);
		if(args[0].equals("help")) {
			if(args.length == 1) printHelp((Player)sender, 1);
			else if(args.length == 2)  {
				if(args[1].equals("all")) {
					printAllHelp((Player)sender);
					return true;
				}
				printHelp((Player)sender, Integer.parseInt(args[1]));
			} else throwError((Player)sender, CommandError.INVALID_ARGUMENTS);
			return true;
		}
		return invoke((Player)sender, command, label, args);
	}
	protected abstract void documentation(CommandDocumentation doc);
	protected boolean invoke(Player sender, Command command, String label, String[] args) {
		if(registeredFunctions.size() != 0 && args.length != 0) {
			for(CommandFunction f : registeredFunctions) 
				if(f.command.equals(args[0])) {
					if(args.length != f.subArgs + 1) {
						throwError(sender, CommandError.INVALID_SYNTAX);
						return true;
					}
					f.function.func(sender, args);
					return true;
				}
			throwError((Player)sender, CommandError.NO_SUCH_COMMAND);
			return true;
		} else if(registeredFunctions.size() != 0 && args.length == 0) {
			throwError((Player)sender, CommandError.INVALID_ARGUMENTS);
			return true;
		}
		if(registeredFunctions.size() == 0 && args.length != 0) {
			throwError((Player)sender, CommandError.NO_ARGUMENTS);
			return true;
		}
		return logic(sender, command, label, args);
	}
	public abstract boolean logic(Player sender, Command command, String label, String[] args);
	protected void throwError(Player player, CommandError error) {
		switch(error) {
			case INVALID_SYNTAX:
				ChatUtilities.logTo(player, "Incorrect [" + commandName + "] command syntax!", ChatUtilities.LogType.FATAL);
				break;
			case INVALID_ARGUMENTS:
				ChatUtilities.logTo(player, "Invalid [" + commandName + "] argument(s)!", ChatUtilities.LogType.FATAL);
				break;
			case NO_ARGUMENTS:
				ChatUtilities.logTo(player, "[" + commandName + "] does not accept any additional arguments besides [" + commandName + "-help]!", ChatUtilities.LogType.FATAL);
				break;
			case NO_SUCH_COMMAND:
				ChatUtilities.logTo(player, "[" + commandName + "] does not contain the command function you have entered.", ChatUtilities.LogType.FATAL);
				break;
		}
		ChatUtilities.messageTo(player, "   Please use [" + commandName + "-help] for instructions on how to use the command correctly.", ChatColor.GRAY);
	}
	public void printHelp(Player player, int page) {
		doc.renderPageTo(player, page - 1);
	}
	public void printAllHelp(Player player) {
		for(int i = doc.pages.size() - 1; i >= 0; i--)
			doc.renderPageTo(player, i + 1);
	}
}
