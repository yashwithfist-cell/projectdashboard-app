package com.pmtool.backend.util;

public class NumberToWords {
	 private static final String[] ones = {
	            "", "One", "Two", "Three", "Four", "Five",
	            "Six", "Seven", "Eight", "Nine", "Ten",
	            "Eleven", "Twelve", "Thirteen", "Fourteen",
	            "Fifteen", "Sixteen", "Seventeen", "Eighteen",
	            "Nineteen"
	    };

	    private static final String[] tens = {
	            "", "", "Twenty", "Thirty", "Forty",
	            "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
	    };

	    private static String convertTwoDigits(int number) {
	        if (number < 20) {
	            return ones[number];
	        }
	        return tens[number / 10] + (number % 10 != 0 ? " " + ones[number % 10] : "");
	    }

	    private static String convertThreeDigits(int number) {
	        if (number < 100) {
	            return convertTwoDigits(number);
	        }
	        return ones[number / 100] + " Hundred" +
	                (number % 100 != 0 ? " " + convertTwoDigits(number % 100) : "");
	    }

	    public static String convert(double amount) {
	        if (amount <= 0) {
	            return "Rupees Zero Only";
	        }

	        long rupees = Math.round(amount);

	        int crore = (int) (rupees / 10000000);
	        rupees %= 10000000;

	        int lakh = (int) (rupees / 100000);
	        rupees %= 100000;

	        int thousand = (int) (rupees / 1000);
	        rupees %= 1000;

	        int rest = (int) rupees;

	        StringBuilder words = new StringBuilder("Rupees ");

	        if (crore > 0) words.append(convertThreeDigits(crore)).append(" Crore ");
	        if (lakh > 0) words.append(convertThreeDigits(lakh)).append(" Lakh ");
	        if (thousand > 0) words.append(convertThreeDigits(thousand)).append(" Thousand ");
	        if (rest > 0) words.append(convertThreeDigits(rest));

	        words.append(" Only");

	        return words.toString().replaceAll("\\s+", " ").trim();
	    }
}
