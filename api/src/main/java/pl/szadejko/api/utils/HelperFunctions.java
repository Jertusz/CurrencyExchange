package pl.szadejko.api.utils;

public class HelperFunctions {

    public static boolean withdrawalPossible(double current, double amount) {
        if (current - amount < 0) {
            return false;
        }
        return true;
    }
}
