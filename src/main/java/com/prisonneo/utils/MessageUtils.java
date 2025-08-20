package com.prisonneo.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

public class MessageUtils {
    
    public static final String PREFIX = "§6[Prison NEO] §r";
    
    public static void sendMessage(Player player, String message) {
        player.sendMessage(PREFIX + message);
    }
    
    public static void sendSuccess(Player player, String message) {
        player.sendMessage(PREFIX + "§a" + message);
    }
    
    public static void sendError(Player player, String message) {
        player.sendMessage(PREFIX + "§c" + message);
    }
    
    public static void sendWarning(Player player, String message) {
        player.sendMessage(PREFIX + "§e" + message);
    }
    
    public static void sendInfo(Player player, String message) {
        player.sendMessage(PREFIX + "§b" + message);
    }
    
    public static Component createComponent(String text, NamedTextColor color) {
        return Component.text(text).color(color);
    }
    
    public static Component createBoldComponent(String text, NamedTextColor color) {
        return Component.text(text).color(color).decoration(TextDecoration.BOLD, true);
    }
    
    public static String formatMoney(double amount) {
        return String.format("$%.0f", amount);
    }
    
    public static String formatTime(int minutes) {
        if (minutes < 60) {
            return minutes + " мин";
        } else {
            int hours = minutes / 60;
            int remainingMinutes = minutes % 60;
            return hours + "ч " + remainingMinutes + "м";
        }
    }
}
