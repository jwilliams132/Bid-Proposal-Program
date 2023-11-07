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

    public String convertToWords(BigDecimal number) {
        
        // Separate the number into integer and fraction parts
        BigDecimal[] parts = number.divideAndRemainder(BigDecimal.ONE);
        int dollars = parts[0].intValue();
        int cents = parts[1].multiply(new BigDecimal("100")).intValue();

        String dollarsInWords = convertToWords(dollars);
        String centsInWords = convertToWords(cents);

        String result = dollarsInWords + " Dollars";

        if (!centsInWords.isEmpty()) {
            result += " and " + centsInWords + "/100";
        } else {
            result += " and No/100";
        }
        result += String.format(" (%,.2f)", number);
        return result;
    }

    public String convertToWords(int number) {

        if (number < 10) {

            return units[number];
        } else if (number < 20) {

            return teens[number - 10];
        } else if (number < 100) {

            return tens[number / 10] + " " + units[number % 10];
        } else if (number < 1000) {

            return units[number / 100] + " Hundred " + convertToWords(number % 100);
        } else if (number < 1000000) {

            return convertToWords(number / 1000) + " Thousand " + convertToWords(number % 1000);
        } else {

            return "Number too large to convert";
        }
    }
}
