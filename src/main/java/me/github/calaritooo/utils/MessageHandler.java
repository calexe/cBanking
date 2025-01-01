package me.github.calaritooo.utils;

import me.github.calaritooo.cBanking;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageHandler {

    private final cBanking plugin;
    private FileConfiguration messagesConfig;
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("&([0-9a-fk-or])");

    public MessageHandler(cBanking plugin) {
        this.plugin = plugin;
        loadMessagesConfig();
    }

    private void loadMessagesConfig() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdirs();
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public Component getFormattedMessage(String messageKey, Map<String, String> placeholders) {
        String message = getMessage(messageKey);
        if (message.isEmpty()) {
            return Component.empty();
        }
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }
        return formatMessage(message);
    }

    private String getMessage(String messageKey) {
        return messagesConfig.getString(messageKey, "");
    }

    public Component formatMessage(String message) {
        Component component = Component.empty();
        Matcher matcher = COLOR_CODE_PATTERN.matcher(message);
        int lastEnd = 0;

        while (matcher.find()) {
            String before = message.substring(lastEnd, matcher.start());
            component = component.append(Component.text(before));

            String colorCode = matcher.group(1);
            NamedTextColor color = getColorFromCode(colorCode);
            TextDecoration decoration = getDecorationFromCode(colorCode);

            if (color != null) {
                component = component.append(Component.text("", color));
            } else if (decoration != null) {
                component = component.decorate(decoration);
            }

            lastEnd = matcher.end();
        }

        String remaining = message.substring(lastEnd);
        component = component.append(Component.text(remaining));

        if (component.children().isEmpty()) {
            component = Component.text(message, NamedTextColor.WHITE);
        }

        return component;
    }

    private NamedTextColor getColorFromCode(String code) {
        switch (code) {
            case "0": return NamedTextColor.BLACK;
            case "1": return NamedTextColor.DARK_BLUE;
            case "2": return NamedTextColor.DARK_GREEN;
            case "3": return NamedTextColor.DARK_AQUA;
            case "4": return NamedTextColor.DARK_RED;
            case "5": return NamedTextColor.DARK_PURPLE;
            case "6": return NamedTextColor.GOLD;
            case "7":
            case "r":
                return NamedTextColor.GRAY;
            case "8": return NamedTextColor.DARK_GRAY;
            case "9": return NamedTextColor.BLUE;
            case "a": return NamedTextColor.GREEN;
            case "b": return NamedTextColor.AQUA;
            case "c": return NamedTextColor.RED;
            case "d": return NamedTextColor.LIGHT_PURPLE;
            case "e": return NamedTextColor.YELLOW;
            case "f": return NamedTextColor.WHITE;
            default: return null;
        }
    }

    private TextDecoration getDecorationFromCode(String code) {
        switch (code) {
            case "k": return TextDecoration.OBFUSCATED;
            case "l": return TextDecoration.BOLD;
            case "m": return TextDecoration.STRIKETHROUGH;
            case "n": return TextDecoration.UNDERLINED;
            case "o": return TextDecoration.ITALIC;
            default: return null;
        }
    }

    public String componentToString(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    public void sendNotAPlayerError(CommandSender sender) {
        Component notAPlayerMessage = getFormattedMessage("error-not-player", new HashMap<>());
        sender.sendMessage(componentToString(notAPlayerMessage));
    }

    public void sendNoPermissionError(CommandSender sender) {
        Component noPermissionMessage = getFormattedMessage("error-no-permission", new HashMap<>());
        sender.sendMessage(componentToString(noPermissionMessage));
    }

    public void sendInvalidPlayerError(CommandSender sender) {
        Component invalidPlayerMessage = getFormattedMessage("error-invalid-player", new HashMap<>());
        sender.sendMessage(componentToString(invalidPlayerMessage));
    }

    public void sendInvalidAmountError(CommandSender sender) {
        Component invalidAmountMessage = getFormattedMessage("error-invalid-amount", new HashMap<>());
        sender.sendMessage(componentToString(invalidAmountMessage));
    }

    public void sendInsufficientFundsError(CommandSender sender) {
        Component insufficientFundsMessage = getFormattedMessage("error-insufficient-funds", new HashMap<>());
        sender.sendMessage(componentToString(insufficientFundsMessage));
    }

    public boolean isValidAmount(String amountStr, CommandSender sender) {
        try {
            double amount = Double.parseDouble(amountStr);
            if (Math.round(amount * 100) != amount * 100) {
                sendInvalidAmountError(sender);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            sendInvalidAmountError(sender);
            return false;
        }
    }
}