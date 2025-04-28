package me.calaritooo.cBanking.util.money;

import me.calaritooo.cBanking.util.configuration.ConfigurationOption;
import me.calaritooo.cBanking.util.configuration.ConfigurationProvider;

public class MoneyProvider {

    private final ConfigurationProvider config;

    public MoneyProvider(ConfigurationProvider config) {
        this.config = config;
    }

    /**
     * Gets the starting balance for new players.
     */
    public Money getStartingBalance() {
        double value = config.get(ConfigurationOption.ECONOMY_STARTING_BALANCE);
        return Money.of(value);
    }

    /**
     * Gets the current currency symbol for formatting.
     */
    public String getCurrencySymbol() {
        return config.get(ConfigurationOption.ECONOMY_CURRENCY_SYMBOL);
    }
}
