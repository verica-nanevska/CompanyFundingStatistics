package coding.test.company.utils;

import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

@NoArgsConstructor
public class CurrencyConverter {

    public static BigDecimal convertCurrencyToNumber(String currency) throws ParseException {
        return deduceCurrency(currency)
                .multiply(BigDecimal.valueOf(deduceMultiplicationFactor(currency)))
                .setScale(1, RoundingMode.HALF_UP);
    }

    private static int deduceMultiplicationFactor(String amount) {
        int factor;
        switch (amount.substring(amount.length() - 1)) {
            case "k":
                factor = 1000;
                break;
            case "M":
                factor = 1000000;
                break;
            case "B":
                factor = 1000000000;
                break;
            default:
                factor = 1;
                break;
        }
        return factor;
    }

    private static BigDecimal deduceCurrency(String amountString) throws ParseException {
        BigDecimal amountInDollars;
        switch (amountString.charAt(0)) {
            case 'C':
                amountInDollars = parseCurrencyString(amountString, Locale.CANADA).multiply(BigDecimal.valueOf(0.83));
                break;
            case '£':
                amountInDollars = parseCurrencyString(amountString, Locale.UK).multiply(BigDecimal.valueOf(1.42));
                break;
            case '€':
                amountInDollars = parseCurrencyString(amountString, Locale.FRANCE).multiply(BigDecimal.valueOf(1.23));
                break;
            case '$':
                amountInDollars = parseCurrencyString(amountString, Locale.US);
                break;
            default:
                amountInDollars = BigDecimal.ZERO;
                break;
        }
        return amountInDollars;
    }

    private static BigDecimal parseCurrencyString(final String amount, final Locale locale) throws ParseException {
        final NumberFormat format = NumberFormat.getNumberInstance(locale);
        if (format instanceof DecimalFormat) {
            ((DecimalFormat) format).setParseBigDecimal(true);
        }
        return (BigDecimal) format.parse(amount.replaceAll("[^\\d.,]", ""));
    }

}
