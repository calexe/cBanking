package me.calaritooo.cBanking.util.money;

import me.calaritooo.cBanking.util.messages.Message;
import me.calaritooo.cBanking.util.messages.MessageProvider;
import org.bukkit.command.CommandSender;

public class MoneyHandler {

    /**
     * Parses a String input into Money.
     * Sends an error message if invalid.
     */
    public static Money parseOrError(CommandSender sender, String input, MessageProvider messages) {
        Money money = Money.parse(input);
        if (money == null || money.value() < 0) {
            messages.send(sender, Message.ERROR_INVALID_AMOUNT);
            throw new IllegalArgumentException("Invalid or negative money input: " + input);
        }
        return money;
    }

    /**
     * Creates a Money object safely from a double (already trusted sources).
     */
    public static Money fromDouble(double amount) {
        return Money.of(amount);
    }
}
