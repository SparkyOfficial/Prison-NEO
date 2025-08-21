package com.prisonneo.commands;

import com.prisonneo.PrisonNEO;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DoctorCommand implements CommandExecutor {
    
    private final PrisonNEO plugin;
    
    public DoctorCommand(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Только игроки могут обращаться к доктору!");
            return true;
        }
        
        Player player = (Player) sender;
        plugin.getDoctorManager().openDoctorMenu(player);
        return true;
    }
}
