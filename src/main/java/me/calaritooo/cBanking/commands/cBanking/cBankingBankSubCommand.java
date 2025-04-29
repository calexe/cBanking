package me.calaritooo.cBanking.commands.cBanking;

import me.calaritooo.cBanking.bank.BankSetting;
import me.calaritooo.cBanking.eco.EconomyService;
import me.calaritooo.cBanking.util.configuration.ConfigurationProvider;
import me.calaritooo.cBanking.util.messages.Message;
import me.calaritooo.cBanking.util.messages.MessageProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Stream;

public class cBankingBankSubCommand {

    private final ConfigurationProvider config;
    private final EconomyService eco;
    private final MessageProvider messages;

    public cBankingBankSubCommand(ConfigurationProvider config, EconomyService eco, MessageProvider messages) {
        this.config = config;
        this.eco = eco;
        this.messages = messages;
    }

    public void handle(CommandSender sender, String[] args) {
        if (args.length < 2) {
            messages.send(sender, Message.USAGE_CBANKING_BANK);
            return;
        }

        String bankID = args[0].toLowerCase();

        if (!eco.bankExists(bankID)) {
            messages.send(sender, Message.BANK_NOT_FOUND);
            return;
        }

        String action = args[1].toLowerCase();
        switch (action) {
            case "close" -> {
                eco.deleteBankAndTransferBalances(bankID);
                messages.send(sender, Message.BANK_DELETED);
            }
            case "info" -> {
                messages.send(sender, Message.BANK_INFO_HEADER,
                        "%bank%", eco.getBankName(bankID));
                messages.send(sender, Message.BANK_INFO_OWNER,
                        "%value%", eco.getBankOwnerName(bankID));
                messages.send(sender, Message.BANK_INFO_ASSETS,
                        "%value%", String.valueOf(eco.getBankSetting(bankID, BankSetting.ASSETS, Double.class)));
                messages.send(sender, Message.BANK_INFO_INTEREST,
                        "%value%", String.valueOf(eco.getBankSetting(bankID, BankSetting.INTEREST_RATE, Double.class)));
                messages.send(sender, Message.BANK_INFO_OPENING,
                        "%value%", String.valueOf(eco.getBankSetting(bankID, BankSetting.ACCOUNT_OPENING_FEE, Double.class)));
                messages.send(sender, Message.BANK_INFO_MAINTENANCE,
                        "%value%", String.valueOf(eco.getBankSetting(bankID, BankSetting.MAINTENANCE_FEE_RATE, Double.class)));
                messages.send(sender, Message.BANK_INFO_DEPOSIT,
                        "%value%", String.valueOf(eco.getBankSetting(bankID, BankSetting.DEPOSIT_FEE_RATE, Double.class)));
                messages.send(sender, Message.BANK_INFO_WITHDRAWAL,
                        "%value%", String.valueOf(eco.getBankSetting(bankID, BankSetting.WITHDRAWAL_FEE_RATE, Double.class)));
            }
            case "set" -> {
                if (args.length < 4) {
                    messages.send(sender, Message.USAGE_CBANKING_BANK_SET);
                    return;
                }

                String settingInput = args[2];
                String valueInput = args[3];

                BankSetting setting = BankSetting.fromString(settingInput);
                if (setting == null) {
                    messages.send(sender, Message.BANK_SETTING_INVALID);
                    return;
                }

                if (setting == BankSetting.OWNER_NAME || setting == BankSetting.OWNER_UUID || setting == BankSetting.NAME) {
                    if (setting == BankSetting.NAME) {
                        sender.sendMessage("§cYou may not change a bank's name!");
                        return;
                    }
                    OfflinePlayer newOwner = Bukkit.getOfflinePlayer(valueInput);
                    if (!newOwner.hasPlayedBefore()) {
                        messages.send(sender, Message.ERROR_INVALID_PLAYER);
                        return;
                    }
                    eco.setBankOwner(bankID, newOwner.getUniqueId());
                    messages.send(sender, Message.BANK_SETTING_UPDATED,
                            "%setting%", setting.toString(),
                            "%value%", valueInput);
                    return;
                }

                try {
                    double numericValue = Double.parseDouble(valueInput);
                    eco.setBankSetting(bankID, setting, numericValue);
                    messages.send(sender, Message.BANK_SETTING_UPDATED,
                            "%setting%", setting.toString(),
                            "%value%", valueInput);
                } catch (NumberFormatException e) {
                    messages.send(sender, Message.ERROR_INVALID_AMOUNT);
                }
            }

            default -> {
                messages.send(sender, Message.USAGE_CBANKING_BANK);
            }
        }
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return eco.getAllBankIDs().stream()
                    .filter(id -> id.startsWith(args[0].toLowerCase()))
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .toList();
        } else if (args.length == 2) {
            return Stream.of("info", "close", "set")
                    .filter(option -> option.startsWith(args[1].toLowerCase()))
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .toList();
        } else if (args.length == 3 && args[1].equalsIgnoreCase("set")) {
            return Arrays.stream(BankSetting.values())
                    .filter(setting -> setting != BankSetting.NAME)
                    .map(setting -> setting.name().toLowerCase())
                    .filter(option -> option.startsWith(args[2].toLowerCase()))
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .toList();
        }
        return Collections.emptyList();
    }
}
