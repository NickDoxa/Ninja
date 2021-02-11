package com.doxa.ninja;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class TabClass implements TabCompleter {

	List<String> arguments = new ArrayList<String>();
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (arguments.isEmpty()) {
			arguments.add("substitution"); arguments.add("clear");
			arguments.add("kunai"); arguments.add("clone");
			arguments.add("list"); arguments.add("agility");
			arguments.add("help"); arguments.add("bind");
			arguments.add("rasengan"); arguments.add("chidori");
			arguments.add("meditate"); arguments.add("errors");
			arguments.add("taijutsu"); arguments.add("level");
		}
		
		List<String> result = new ArrayList<String>();
		
		if (args.length > 1) {
			for (String a : arguments) {
				if (a.toLowerCase().startsWith(args[1].toLowerCase()))
					result.add(a);
			}
			return result;
		}
		return arguments;
	}
}