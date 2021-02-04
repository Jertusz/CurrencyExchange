package pl.szadejko.api.service;

import org.springframework.stereotype.Service;
import pl.szadejko.api.models.Currency;
import pl.szadejko.api.repository.CurrencyRepository;
import pl.szadejko.api.security.User;
import pl.szadejko.api.security.UserService;

import java.util.HashMap;

import static pl.szadejko.api.utils.HelperFunctions.withdrawalPossible;

@Service
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final UserService userService;

    public CurrencyService(CurrencyRepository currencyRepository, UserService userService) {
        this.currencyRepository = currencyRepository;
        this.userService = userService;
    }

    public HashMap<String, Double> getBalance(String username) {
        User user = userService.loadUserByUsername(username);
        Currency account = loadByUser(user);
        HashMap<String, Double> balance = new HashMap<>();
        balance.put("PLN", account.getPln());
        balance.put("USD", account.getUsd());
        balance.put("EUR", account.getEur());
        balance.put("GBP", account.getGbp());
        return balance;
    }

    public boolean exchangeCurrency(User user, String from, double amount, String to) {
        return true;
    }

    public void deposit(String username, String currency, double amount) {
        User user = userService.loadUserByUsername(username);
        Currency account = loadByUser(user);
        switch (currency) {
            case "PLN":
                account.setPln(account.getPln() + amount);
                break;
            case "EUR":
                account.setEur(account.getEur() + amount);
                break;
            case "GBP":
                account.setGbp(account.getGbp() + amount);
                break;
            case "USD":
                account.setUsd(account.getUsd() + amount);
                break;
        }
        currencyRepository.save(account);
    }

    public void withdraw(String username, String currency, double amount) {
        User user = userService.loadUserByUsername(username);
        Currency account = loadByUser(user);
        switch (currency) {
            case "PLN":
                if (withdrawalPossible(account.getPln(), amount)) {
                    account.setPln(account.getPln() - amount);
                }
                break;
            case "EUR":
                if (withdrawalPossible(account.getEur(), amount)) {
                    account.setEur(account.getEur() - amount);
                }
                break;
            case "GBP":
                if (withdrawalPossible(account.getGbp(), amount)) {
                    account.setGbp(account.getGbp() - amount);
                }
                break;
            case "USD":
                if (withdrawalPossible(account.getUsd(), amount)) {
                    account.setUsd(account.getUsd() - amount);
                }
                break;
        }
        currencyRepository.save(account);
    }

    public Currency loadByUser(User user) {
        return currencyRepository.findAll()
                .stream()
                .filter(currency -> currency.getUser().equals(user))
                .findAny()
                .orElse(null);
    }

    public void newAccount(User user) {
        Currency account = new Currency();
        account.setUser(user);
        account.setUsd(0);
        account.setPln(0);
        account.setEur(0);
        account.setGbp(0);
        currencyRepository.save(account);
    }
}
