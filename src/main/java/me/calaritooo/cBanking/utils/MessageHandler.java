package me.calaritooo.cBanking.utils;

import me.calaritooo.cBanking.cBanking;
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

    public String getMessage(String messageKey) {
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
        return switch (code) {
            case "0" -> NamedTextColor.BLACK;
            case "1" -> NamedTextColor.DARK_BLUE;
            case "2" -> NamedTextColor.DARK_GREEN;
            case "3" -> NamedTextColor.DARK_AQUA;
            case "4" -> NamedTextColor.DARK_RED;
            case "5" -> NamedTextColor.DARK_PURPLE;
            case "6" -> NamedTextColor.GOLD;
            case "7", "r" -> NamedTextColor.GRAY;
            case "8" -> NamedTextColor.DARK_GRAY;
            case "9" -> NamedTextColor.BLUE;
            case "a" -> NamedTextColor.GREEN;
            case "b" -> NamedTextColor.AQUA;
            case "c" -> NamedTextColor.RED;
            case "d" -> NamedTextColor.LIGHT_PURPLE;
            case "e" -> NamedTextColor.YELLOW;
            case "f" -> NamedTextColor.WHITE;
            default -> null;
        };
    }

    private TextDecoration getDecorationFromCode(String code) {
        return switch (code) {
            case "k" -> TextDecoration.OBFUSCATED;
            case "l" -> TextDecoration.BOLD;
            case "m" -> TextDecoration.STRIKETHROUGH;
            case "n" -> TextDecoration.UNDERLINED;
            case "o" -> TextDecoration.ITALIC;
            default -> null;
        };
    }

    public Map<String, String> getFixedPlaceholders(double bal, double amt, String player) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("plugin-prefix", plugin.getConfig().getString("plugin-settings.plugin-prefix"));
        placeholders.put("currency-symbol", plugin.getConfig().getString("economy-settings.currency-symbol"));
        placeholders.put("currency-name", plugin.getConfig().getString("economy-settings.currency-name"));
        placeholders.put("currency-name-plural", plugin.getConfig().getString("economy-settings.currency-name-plural"));
        placeholders.put("{bal}", String.format("%.2f", bal));
        placeholders.put("{amt}", String.format("%.2f", amt));
        placeholders.put("{player}", player);
        return placeholders;
    }

    public String componentToString(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    public void sendNotAPlayerError(CommandSender sender) {
        Component notAPlayerMessage = getFormattedMessage("error-not-player", new HashMap<>());
        sender.sendMessage(componentToString(notAPlayerMessage));
    }

    public void sendNoPermissionError(CommandSender sender) {
        Component noPermissionMessage = getFormattedMessage("error-no-perm", new HashMap<>());
        sender.sendMessage(componentToString(noPermissionMessage));
    }

    public void sendInvalidPlayerError(CommandSender sender) {
        Component invalidPlayerMessage = getFormattedMessage("error-invalid-player", new HashMap<>());
        sender.sendMessage(componentToString(invalidPlayerMessage));
    }

    public void sendInvalidAmountError(CommandSender sender) {
        Component invalidAmountMessage = getFormattedMessage("error-invalid-amt", new HashMap<>());
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