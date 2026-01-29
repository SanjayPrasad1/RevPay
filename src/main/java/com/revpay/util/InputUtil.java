package com.revpay.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

public class InputUtil {

    private static final Scanner scanner = new Scanner(System.in);

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }
    public static BigDecimal readBigDecimal(String prompt){
        while (true){
            try {
                String input = readLine(prompt);
                BigDecimal value = new BigDecimal(input);
                if (value.compareTo(BigDecimal.ZERO)<0){
                    System.out.println("Value cannot be negative. Try again.");
                    continue;
                }
                return value;
            }catch (NumberFormatException e){
                System.out.println("Invalid decimal number. Try again.");
            }
        }
    }
    public static long readLong(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public static LocalDate readDate(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid date format. Use YYYY-MM-DD");
            }
        }
    }


}
