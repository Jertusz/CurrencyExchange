package pl.szadejko.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import pl.szadejko.api.service.CurrencyService;

@RestController
@RequestMapping("/currency")
public class CurrencyController {

    @Autowired
    CurrencyService currencyService;

    @PostMapping("/deposit/{currency}/{amount}")
    public ResponseEntity deposit(Authentication authentication, @PathVariable(name = "currency") String currency, @PathVariable(name = "amount") double amount) {
        if(currencyService.deposit(authentication.getName(), currency, amount)){
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/withdraw/{currency}/{amount}")
    public ResponseEntity withdraw(Authentication authentication, @PathVariable(name = "currency") String currency, @PathVariable(name = "amount") double amount) {
        if(currencyService.withdraw(authentication.getName(), currency, amount)){
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/balance")
    public ResponseEntity balance(Authentication authentication) {
        return new ResponseEntity(currencyService.getBalance(authentication.getName()), HttpStatus.OK);
    }

    @PostMapping("/convert/{from}/{to}/{amount}")
    public ResponseEntity convert(Authentication authentication, @PathVariable(name = "from") String from, @PathVariable(name = "to") String to, @PathVariable(name = "amount") double amount) {
        String uri = "https://api.exchangeratesapi.io/latest?base=" + from;
        RestTemplate restTemplate = new RestTemplate();
        Object res = restTemplate.getForObject(uri, Object.class);
        double exchanged = currencyService.exchangeCurrency(authentication.getName(), from, amount, to);
        if (exchanged != 0.0) {
            return new ResponseEntity(exchanged, HttpStatus.OK);
        }
        return new ResponseEntity("Invalid amount, or currency does not exist", HttpStatus.BAD_REQUEST);
    }
}
