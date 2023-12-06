package gearworks;

import java.math.BigDecimal;

public class BigDecimalToWordsConverter {

    private final String[] units = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"
    };

    private final String[] teens = {
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
            "Seventeen", "Eighteen", "Nineteen"
    };

    private final String[] tens = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    public String convertCurrencyToWords(BigDecimal number) {
        
        // Separate the number into integer and fraction parts
        BigDecimal[] parts = number.divideAndRemainder(BigDecimal.ONE);
        int dollars = parts[0].intValue();
        int cents = parts[1].multiply(new BigDecimal("100")).intValue();

        String dollarsInWords = convertIntToWords(dollars);

        String result = dollarsInWords + " Dollars";

        if (cents != 0) {
            result += " and " + cents + "/100";
        } else {
            result += " and No/100";
        }
        result += String.format(" (%,.2f)", number);
        return result;
    }

    public String convertIntToWords(int number) {

        if (number < 10) {

            return units[number];
        } else if (number < 20) {

            return teens[number - 10];
        } else if (number < 100) {

            return tens[number / 10] + "-" + units[number % 10];
        } else if (number < 1000) {

            return units[number / 100] + " Hundred " + convertIntToWords(number % 100);
        } else if (number < 1000000) {

            return convertIntToWords(number / 1000) + " Thousand, " + convertIntToWords(number % 1000);
        } else {

            return "Number too large to convert";
        }
    }
}
