package coding.test.company;

import coding.test.company.utils.CurrencyConverter;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;

public class CurrencyConverterTest {

    @Test
    public void testCurrencyConverter() throws ParseException {
        Assert.assertEquals(BigDecimal.valueOf(12.6), CurrencyConverter.convertCurrencyToNumber("$12.6"));
        Assert.assertEquals(BigDecimal.valueOf(1460800.0), CurrencyConverter.convertCurrencyToNumber("C$1.76M"));
        Assert.assertEquals(BigDecimal.valueOf(1401540.0), CurrencyConverter.convertCurrencyToNumber("£987k"));
        Assert.assertEquals(BigDecimal.valueOf(3690.0), CurrencyConverter.convertCurrencyToNumber("€3.5k"));
        Assert.assertEquals(BigDecimal.valueOf(0.0), CurrencyConverter.convertCurrencyToNumber("#90.5M"));

    }
}
