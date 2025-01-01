package me.github.calaritooo.utils;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TransactionLogger {

    private final File logFile;
    private final YamlConfiguration logConfig;
    private final boolean loggingEnabled;

    public TransactionLogger(File dataFolder, boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
        if (loggingEnabled) {
            this.logFile = new File(dataFolder, "transactions.yml");
            this.logConfig = YamlConfiguration.loadConfiguration(logFile);
        } else {
            this.logFile = null;
            this.logConfig = null;
        }
    }

    public void logTransaction(String sender, String recipient, double amount, String transactionType) {
        if (!loggingEnabled) {
            return;
        }

        String transactionId = "transaction-" + System.currentTimeMillis();
        Map<String, Object> transactionData = new HashMap<>();
        transactionData.put("sender", sender);
        transactionData.put("recipient", recipient);
        transactionData.put("amount", amount);
        transactionData.put("transaction-type", transactionType);
        transactionData.put("timestamp", System.currentTimeMillis());

        logConfig.createSection(transactionId, transactionData);
        saveLog();
    }

    private void saveLog() {
        try {
            logConfig.save(logFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}