package ru.deade.minetg;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdExecutor implements CommandExecutor{
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			if (cmd.getName().equalsIgnoreCase("cmd1")) {
				//to do
			}
			if (cmd.getName().equalsIgnoreCase("cmd2")) {
				//to do
			}
		}
		
		return true;
	}
}
