package me.calaritooo.cBanking.commands.cBanking;

import me.calaritooo.cBanking.eco.EconomyService;
import me.calaritooo.cBanking.util.configuration.ConfigurationOption;
import me.calaritooo.cBanking.util.configuration.ConfigurationProvider;
import me.calaritooo.cBanking.util.messages.Message;
import me.calaritooo.cBanking.util.messages.MessageProvider;
import me.calaritooo.cBanking.util.money.Money;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Stream;

public class cBankingPlayerSubCommand {

    private final ConfigurationProvider config;
    private final EconomyService eco;
    private final MessageProvider messages;

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

        String inputName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(inputName);

        if (target.getName() == null || !target.hasPlayedBefore()) {
            messages.send(sender, Message.ERROR_INVALID_PLAYER);
            return;
        }

        String targetName = target.getName();

        if (args.length < 2) {
            messages.send(sender, Message.USAGE_CBANKING_PLAYER);
            return;
        }

        String action = args[1].toLowerCase();
        String currencySymbol = config.get(ConfigurationOption.ECONOMY_CURRENCY_SYMBOL);

        switch (action) {
            case "balance" -> {
                if (args.length == 2) {
                    messages.send(sender, Message.BALANCE_OTHER,
                            "%player%", targetName,
                            "%amt%", eco.getBalance(target.getUniqueId()).format());
                    return;
                }

                if (args.length < 4) {
                    messages.send(sender, Message.USAGE_CBANKING_PLAYER_BALANCE);
                    return;
                }

                String option = args[2].toLowerCase();
                Money amount = Money.parse(args[3]);
                if (amount == null) {
                    messages.send(sender, Message.ERROR_INVALID_AMOUNT);
                    return;
                }

                switch (option) {
                    case "set" -> {
                        if (eco.setBalance(target.getUniqueId(), amount)) {
                            sender.sendMessage("§7" + targetName + "'s balance set to §a" + currencySymbol + amount.format() + "§7.");
                        }
                    }
                    case "give" -> {
                        if (eco.deposit(target.getUniqueId(), amount)) {
                            sender.sendMessage("§7Gave §a" + currencySymbol + amount.format() + "§7 to " + targetName + "§7.");
                        }
                    }
                    case "take" -> {
                        if (eco.withdraw(target.getUniqueId(), amount)) {
                            sender.sendMessage("§7Took §c" + currencySymbol + amount.format() + "§7 from " + targetName + "§7.");
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
                        sender.sendMessage("§cNo bank accounts found for " + targetName + ".");
                    } else {
                        sender.sendMessage("§7Bank accounts for §a" + targetName + "§7:");
                        for (String bankID : playerBankIDs) {
                            String bankName = eco.getBankName(bankID);
                            Money bankBalance = eco.getBankBalance(bankID, target.getUniqueId());
                            sender.sendMessage("§f- §6" + bankName + " §7(§f" + bankID + "§7): §a" + currencySymbol + bankBalance.format());
                        }
                    }
                    return;
                }

                if (!eco.bankExists(option)) {
                    sender.sendMessage("§cBank §e" + option + "§c not found.");
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
                            if (eco.createBankAccount(option, target.getUniqueId(), Money.zero())) {
                                sender.sendMessage("§7Opened account for §a" + targetName + "§7 at §6" + eco.getBankName(option) + "§7.");
                            } else {
                                sender.sendMessage("§cAccount already exists for " + targetName + ".");
                            }
                        }
                        case "close" -> {
                            if (eco.deleteBankAccount(option, target.getUniqueId())) {
                                sender.sendMessage("§7Closed account for §a" + targetName + "§7 at §6" + eco.getBankName(option) + "§7.");
                            } else {
                                sender.sendMessage("§cNo account found for " + targetName + ".");
                            }
                        }
                    }
                    return;
                }

                if (args.length < 5 && (accountAction.equals("set") || accountAction.equals("give") || accountAction.equals("take"))) {
                    messages.send(sender, Message.USAGE_CBANKING_PLAYER_ACCOUNT);
                    return;
                }

                Money amount = Money.parse(args[4]);
                if (amount == null) {
                    messages.send(sender, Message.ERROR_INVALID_AMOUNT);
                    return;
                }

                switch (accountAction) {
                    case "set" -> {
                        if (eco.setBankBalance(option, target.getUniqueId(), amount)) {
                            sender.sendMessage("§7Set §a" + targetName + "§7's account with §6" + eco.getBankName(option) + "§7 to §a" + currencySymbol + amount.format());
                        }
                    }
                    case "give" -> {
                        if (eco.depositBank(option, target.getUniqueId(), amount)) {
                            sender.sendMessage("§7Gave §a" + currencySymbol + amount.format() + "§7 to §a" + targetName + "§7's account at §6" + eco.getBankName(option));
                        }
                    }
                    case "take" -> {
                        if (eco.withdrawBank(option, target.getUniqueId(), amount)) {
                            sender.sendMessage("§7Took §c" + currencySymbol + amount.format() + "§7 from §a" + targetName + "§7's account at §6" + eco.getBankName(option));
                        } else {
                            sender.sendMessage("§cNot enough funds in " + targetName + "'s account.");
                        }
                    }
                    default -> messages.send(sender, Message.USAGE_CBANKING_PLAYER_ACCOUNT);
                }
            }
        }
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1 -> {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                        .sorted(String.CASE_INSENSITIVE_ORDER)
                        .toList();
            }
            case 2 -> {
                return Stream.of("balance", "account", "loan")
                        .filter(option -> option.startsWith(args[1].toLowerCase()))
                        .sorted(String.CASE_INSENSITIVE_ORDER)
                        .toList();
            }
            case 3 -> {
                String sub = args[1].toLowerCase();
                if (sub.equals("balance")) {
                    return Stream.of("set", "give", "take")
                            .filter(option -> option.startsWith(args[2].toLowerCase()))
                            .sorted(String.CASE_INSENSITIVE_ORDER)
                            .toList();
                } else if (sub.equals("account")) {
                    List<String> options = new ArrayList<>();
                    options.add("list");
                    options.addAll(eco.getAllBankIDs());
                    return options.stream()
                            .filter(option -> option.startsWith(args[2].toLowerCase()))
                            .sorted(String.CASE_INSENSITIVE_ORDER)
                            .toList();
                } else if (sub.equals("loan")) {
                    return eco.getAllBankIDs().stream()
                            .filter(id -> id.startsWith(args[2].toLowerCase()))
                            .sorted(String.CASE_INSENSITIVE_ORDER)
                            .toList();
                }
            }
            case 4 -> {
                String sub = args[1].toLowerCase();
                if (sub.equals("account")) {
                    return Stream.of("open", "close", "set", "give", "take")
                            .filter(option -> option.startsWith(args[3].toLowerCase()))
                            .sorted(String.CASE_INSENSITIVE_ORDER)
                            .toList();
                } else if (sub.equals("loan")) {
                    return Stream.of("approve", "deny", "extend", "cancel")
                            .filter(option -> option.startsWith(args[3].toLowerCase()))
                            .sorted(String.CASE_INSENSITIVE_ORDER)
                            .toList();
                }
            }
        }
        return Collections.emptyList();
    }

}
