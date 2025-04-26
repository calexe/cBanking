package me.calaritooo.cBanking.commands.cBanking;

import me.calaritooo.cBanking.eco.EconomyService;
import me.calaritooo.cBanking.util.configuration.ConfigurationOption;
import me.calaritooo.cBanking.util.configuration.ConfigurationProvider;
import me.calaritooo.cBanking.util.messages.Message;
import me.calaritooo.cBanking.util.messages.MessageProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Stream;

public class cBankingPlayerSubCommand {

    private final ConfigurationProvider config;
    private final MessageProvider messages;
    private final EconomyService eco;

    public cBankingPlayerSubCommand(ConfigurationProvider config, EconomyService eco, MessageProvider messages) {
        this.config = config;
        this.eco = eco;
        this.messages = messages;
    }

    public void handle(CommandSender sender, String[] args) {
        if (args.length == 0) {
            messages.send(sender, Message.USAGE_CBANKING_PLAYER);
            return;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (!target.hasPlayedBefore()) {
            messages.send(sender, Message.ERROR_INVALID_PLAYER);
            return;
        }

        if (args.length < 2) {
            messages.send(sender, Message.USAGE_CBANKING_PLAYER);
            return;
        }

        String action = args[1].toLowerCase();
        String currencySymbol = config.getString(ConfigurationOption.ECONOMY_CURRENCY_SYMBOL);
        switch (action) {
            case "balance" -> {
                if (args.length < 3) {
                    messages.send(sender, Message.BALANCE_OTHER,
                            "%player%", target.getName(),
                            "%amt%", String.valueOf(eco.getBalance(target.getUniqueId())));
                    return;
                }
                String option = args[2].toLowerCase();
                if (args.length < 4) {
                    messages.send(sender, Message.USAGE_CBANKING_PLAYER_BALANCE);
                    return;
                }
                double amount;
                try {
                    amount = Double.parseDouble(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid amount.");
                    return;
                }

                switch (option) {
                    case "set" -> {
                        if (eco.setBalance(target.getUniqueId(), amount)) {
                            sender.sendMessage("§7" + target.getName() + "'s balance has been set to §a" + currencySymbol + amount + "§7.");
                        }
                    }
                    case "give" -> {
                        if (eco.deposit(target.getUniqueId(), amount)) {
                            sender.sendMessage("§7Gave §a" + currencySymbol + amount + "§7 to " + target.getName() + "'s balance.");
                        }
                    }
                    case "take" -> {
                        if (eco.withdraw(target.getUniqueId(), amount)) {
                            sender.sendMessage("§7Took §c" + currencySymbol + amount + "§7 from " + target.getName() + "'s balance.");
                        }
                    }
                    default -> messages.send(sender, Message.USAGE_CBANKING_PLAYER_BALANCE);
                }
            }
            case "account" -> {
                if (args.length < 3) {
                    messages.send(sender, Message.USAGE_CBANKING_PLAYER_ACCOUNT);
                    return;
                }

                String option = args[2].toLowerCase();

                if (option.equals("list")) {
                    Set<String> playerBankIDs = eco.getPlayerBankAccounts(target.getUniqueId());
                    if (playerBankIDs.isEmpty()) {
                        sender.sendMessage("§cNo bank accounts found for " + target.getName() + ".");
                    } else {
                        sender.sendMessage("§7Bank accounts for §a" + target.getName() + "§7:");
                        for (String bankID : playerBankIDs) {
                            String bankName = eco.getBankName(bankID);
                            double bankBalance = eco.getBankBalance(bankID, target.getUniqueId());
                            sender.sendMessage("§f- §6" + bankName + " §7(§f" + bankID + "§7): §a" + currencySymbol + bankBalance);
                        }
                    }
                    return;
                }
                String bankID = option;
                if (!eco.bankExists(bankID)) {
                    sender.sendMessage("§cBank §e" + bankID + "§c not found.");
                    return;
                }
                if (args.length < 4) {
                    messages.send(sender, Message.USAGE_CBANKING_PLAYER_ACCOUNT);
                    return;
                }

                String accountAction = args[3].toLowerCase();
                if (accountAction.equals("open") || accountAction.equals("close")) {
                    switch (accountAction) {
                        case "open" -> {
                            if (eco.createBankAccount(bankID, target.getUniqueId(), 0.0)) {
                                String bankName = eco.getBankName(bankID);
                                sender.sendMessage("§7Opened new account for §a" + target.getName() + "§7 at §6" + bankName + "§7.");
                            } else {
                                sender.sendMessage("§cAccount already exists for " + target.getName() + " at this bank.");
                            }
                        }
                        case "close" -> {
                            if (eco.deleteBankAccount(bankID, target.getUniqueId())) {
                                String bankName = eco.getBankName(bankID);
                                sender.sendMessage("§7Closed account for §a" + target.getName() + "§7 at §6" + bankName + "§7.");
                            } else {
                                sender.sendMessage("§cNo account found to close for " + target.getName() + " at this bank.");
                            }
                        }
                    }
                    return;
                }
                if (args.length < 5 && (accountAction.equals("set") || accountAction.equals("take") || accountAction.equals("give"))) {
                    messages.send(sender, Message.USAGE_CBANKING_PLAYER_ACCOUNT);
                    return;
                }
                double amount;
                try {
                    amount = Double.parseDouble(args[4]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid amount.");
                    return;
                }
                switch (accountAction) {
                    case "set" -> {
                        if (eco.setBankBalance(bankID, target.getUniqueId(), amount)) {
                            String bankName = eco.getBankName(bankID);
                            sender.sendMessage("§7Set §a" + target.getName() + "§7's account balance with §6" + bankName + "§7 to §a" + currencySymbol + amount + "§7.");
                        }
                    }
                    case "give" -> {
                        if (eco.depositBank(bankID, target.getUniqueId(), amount)) {
                            String bankName = eco.getBankName(bankID);
                            sender.sendMessage("§7Gave §a" + currencySymbol + amount + "§7 to §a" + target.getName() + "'s account with §6" + bankName + "§7.");
                        }
                    }
                    case "take" -> {
                        if (eco.withdrawBank(bankID, target.getUniqueId(), amount)) {
                            String bankName = eco.getBankName(bankID);
                            sender.sendMessage("§7Took §c" + currencySymbol + amount + "§7 from §a" + target.getName() + "'s account with §6" + bankName + "§7.");
                        } else {
                            sender.sendMessage("§cPlayer does not have enough in their account balance to withdraw that amount!");
                        }
                    }
                    default -> messages.send(sender, Message.USAGE_CBANKING_PLAYER_ACCOUNT);
                }
            }
        }
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .toList();
        } else if (args.length == 2) {
            return Stream.of("balance", "account", "loan")
                    .filter(option -> option.startsWith(args[1].toLowerCase()))
                    .toList();
        } else if (args.length == 3) {
            String sub = args[1].toLowerCase();
            switch (sub) {
                case "balance" -> {
                    return Stream.of("set", "give", "take")
                            .filter(option -> option.startsWith(args[2].toLowerCase()))
                            .toList();
                }
                case "account" -> {
                    List<String> options = new ArrayList<>();
                    options.add("list");
                    options.addAll(eco.getAllBankIDs());
                    return options.stream()
                            .filter(option -> option.startsWith(args[2].toLowerCase()))
                            .sorted(String.CASE_INSENSITIVE_ORDER)
                            .toList();
                }
                case "loan" -> {
                    return eco.getAllBankIDs().stream()
                            .filter(id -> id.startsWith(args[2].toLowerCase()))
                            .sorted(String.CASE_INSENSITIVE_ORDER)
                            .toList();
                }
            }
        } else if (args.length == 4) {
            String sub = args[1].toLowerCase();
            if (sub.equals("account")) {
                return Stream.of("open", "close", "set", "give", "take")
                        .filter(option -> option.startsWith(args[3].toLowerCase()))
                        .toList();
            } else if (sub.equals("loan")) {
                return Stream.of("approve", "deny", "extend", "cancel")
                        .filter(option -> option.startsWith(args[3].toLowerCase()))
                        .toList();
            }
        }
        return Collections.emptyList();
    }

}
