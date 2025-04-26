package me.calaritooo.cBanking.util.messages;

import me.calaritooo.cBanking.util.configuration.ConfigurationOption;
import me.calaritooo.cBanking.util.configuration.ConfigurationProvider;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class MessageProvider {

    private final FileConfiguration messages;
    private final ConfigurationProvider config;
    private final Map<String, String> GLOBAL_PLACEHOLDERS = new HashMap<>();

    public MessageProvider(FileConfiguration messages, ConfigurationProvider config) {
        this.messages = messages;
        this.config = config;
        populateDefaults();
        loadGlobalPlaceholders();
    }

    public void populateDefaults() {
        for (Message message : Message.values()) {
            if (!messages.contains(message.path())) {
                messages.set(message.path(), message.defaultMessage());
            }
        }
    }

    public String get(Message message, String... placeholders) {
        if (messages == null) {
            return ChatColor.RED + "Messages not loaded!";
        }

        String raw = messages.getString(message.path());
        if (raw == null) {
            return ChatColor.RED + "Missing message: " + message.path();
        }

        raw = applyPlaceholders(raw, placeholders);
        return ChatColor.translateAlternateColorCodes('&', raw);
    }

    public void send(CommandSender target, Message message, String... placeholders) {
        target.sendMessage(get(message, placeholders));
    }

    private String applyPlaceholders(String raw, String... placeholders) {
        for (Map.Entry<String, String> entry : GLOBAL_PLACEHOLDERS.entrySet()) {
            raw = raw.replace(entry.getKey(), entry.getValue());
        }

        for (int i = 0; i < placeholders.length - 1; i += 2) {
            String placeholder = placeholders[i];
            String value = placeholders[i + 1];

            if (placeholder.equals("%amt%") || placeholder.equals("%bal%")) {
                try {
                    double number = Double.parseDouble(value);
                    value = formatCurrency(number);
                } catch (NumberFormatException ignored) {
                    // If parsing fails, just use original value
                }
            }

            raw = raw.replace(placeholder, value);
        }

        return raw;
    }

    private void loadGlobalPlaceholders() {
        GLOBAL_PLACEHOLDERS.clear();
        GLOBAL_PLACEHOLDERS.put("%currency-symbol%", config.getString(ConfigurationOption.ECONOMY_CURRENCY_SYMBOL));
        GLOBAL_PLACEHOLDERS.put("%currency-name%", config.getString(ConfigurationOption.ECONOMY_CURRENCY_NAME));
        GLOBAL_PLACEHOLDERS.put("%currency-name-plural%", config.getString(ConfigurationOption.ECONOMY_CURRENCY_NAME_PLURAL));
    }

    public String formatCurrency(double amount) {
        if (amount % 1 == 0) {
            return String.format("%.0f", amount);
        } else {
            return String.format("%.2f", amount);
        }
    }
}

