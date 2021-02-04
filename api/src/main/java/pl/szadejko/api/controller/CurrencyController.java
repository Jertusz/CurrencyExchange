package pl.szadejko.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.szadejko.api.service.CurrencyService;

@RestController
@RequestMapping("/currency")
public class CurrencyController {

    @Autowired
    CurrencyService currencyService;

    @PostMapping("/deposit/{currency}/{amount}")
    public ResponseEntity deposit(Authentication authentication, @PathVariable(name = "currency") String currency, @PathVariable(name = "amount") double amount) {
        currencyService.deposit(authentication.getName(), currency, amount);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @PostMapping("/withdraw/{currency}/{amount}")
    public ResponseEntity withdraw(Authentication authentication, @PathVariable(name = "currency") String currency, @PathVariable(name = "amount") double amount) {
        currencyService.withdraw(authentication.getName(), currency, amount);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }
}
