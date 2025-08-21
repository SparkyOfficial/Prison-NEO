package com.prisonneo.commands;

import com.prisonneo.PrisonNEO;
import com.prisonneo.managers.ScheduleManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TestScheduleCommand implements CommandExecutor {

    private final PrisonNEO plugin;

    public TestScheduleCommand(PrisonNEO plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эта команда только для игроков.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("prisonneo.admin")) {
            player.sendMessage("§cУ вас нет прав для использования этой команды.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("§cИспользование: /testschedule <событие>");
            player.sendMessage("§eДоступные события: " + Arrays.toString(ScheduleManager.ScheduleEvent.values()));
            return true;
        }

        try {
            ScheduleManager.ScheduleEvent event = ScheduleManager.ScheduleEvent.valueOf(args[0].toUpperCase());
            plugin.getScheduleManager().triggerScheduleEvent(event);
            player.sendMessage("§aСобытие '" + event.getName() + "' было успешно запущено.");
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cНеверное событие. Доступные события: " + Arrays.toString(ScheduleManager.ScheduleEvent.values()));
        }

        return true;
    }
}
