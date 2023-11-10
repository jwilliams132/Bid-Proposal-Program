import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class BigDecimalToWordsConverterTest {

    BigDecimalToWordsConverter bigDecimalToWordsConverter = new BigDecimalToWordsConverter();

    @Test
    void testConvertCurrencyToWords() {

        BigDecimal number = new BigDecimal("1234.56");
        String actual = bigDecimalToWordsConverter.convertCurrencyToWords(number);
        String expected = "One Thousand, Two Hundred Thirty-Four Dollars and 56/100 (1,234.56)";
        assertEquals(expected, actual);
    }

    @Test
    void testConvertIntToWords() {

        int number = 0;
        String actual = bigDecimalToWordsConverter.convertIntToWords(number);
        String expected = "";
        assertEquals(expected, actual);
    }
}
