package pl.szadejko.api.service;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.mysql.cj.xdevapi.JsonArray;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.szadejko.api.models.Currency;
import pl.szadejko.api.repository.CurrencyRepository;
import pl.szadejko.api.security.User;
import pl.szadejko.api.security.UserService;

import java.util.HashMap;
import java.util.Map;

import static pl.szadejko.api.utils.HelperFunctions.withdrawalPossible;

@Service
public class CurrencyService {

    private final String apiUri = "https://api.exchangeratesapi.io/latest?base=";
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

    public Double exchangeCurrency(String username, String from, double amount, String to) {
        String fullApiUri = apiUri + from;
        HashMap<String, Double> balance = getBalance(username);
        double baseAmount, targetRate;
        try {
            baseAmount = balance.get(from);
        } catch (Exception e) {
            return 0.0;
        }
        if (amount <= baseAmount) {
            RestTemplate restTemplate = new RestTemplate();
            HashMap<String, HashMap<String, Double>> res = restTemplate.getForObject(fullApiUri, HashMap.class);
            try {
                targetRate = res.get("rates").get(to);
            } catch (Exception e) {
                return 0.0;
            }
            double targetAmount = amount * targetRate;
            deposit(username, to, targetAmount);
            withdraw(username, from, amount);
            return targetAmount;
        }
        return 0.0;
    }

    public boolean deposit(String username, String currency, double amount) {
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
            default:
                return false;
        }
        currencyRepository.save(account);
        return true;
    }

    public boolean withdraw(String username, String currency, double amount) {
        User user = userService.loadUserByUsername(username);
        Currency account = loadByUser(user);
        switch (currency) {
            case "PLN":
                if (withdrawalPossible(account.getPln(), amount)) {
                    account.setPln(account.getPln() - amount);
                } else {
                    return false;
                }
                break;
            case "EUR":
                if (withdrawalPossible(account.getEur(), amount)) {
                    account.setEur(account.getEur() - amount);
                } else {
                    return false;
                }
                break;
            case "GBP":
                if (withdrawalPossible(account.getGbp(), amount)) {
                    account.setGbp(account.getGbp() - amount);
                } else {
                    return false;
                }
                break;
            case "USD":
                if (withdrawalPossible(account.getUsd(), amount)) {
                    account.setUsd(account.getUsd() - amount);
                } else {
                    return false;
                }
                break;
            default:
                return false;
        }
        currencyRepository.save(account);
        return true;
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
