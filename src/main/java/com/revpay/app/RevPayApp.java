package com.revpay.app;

import com.revpay.model.User;
import com.revpay.service.AuthService;
import com.revpay.service.UserService;
import com.revpay.util.InputUtil;

public class RevPayApp {

    private static final UserService userService = new UserService();
    private static final AuthService authService = new AuthService();

    public static void main(String[] args) {

        while (true) {
            System.out.println("\n==== RevPay ====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            int choice = InputUtil.readInt("Enter choice: ");

            switch (choice) {
                case 1 -> handleRegister();
                case 2 -> handleLogin();
                case 3 -> {
                    System.out.println("Goodbye.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void handleRegister() {

        System.out.println("\n---- Register ----");

        String fullName = InputUtil.readLine("Full name: ");
        String email = InputUtil.readLine("Email: ");
        String phone = InputUtil.readLine("Phone: ");
        String password = InputUtil.readLine("Password: ");
        String pin = InputUtil.readLine("PIN: ");

        try {
            userService.register(fullName, email, phone, password, pin);
            System.out.println("Registration successful.");

        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private static void handleLogin(){
        System.out.println("\n--------Login--------");

        String input = InputUtil.readLine("Email or Phone: ");
        String password = InputUtil.readLine("Password: ");

        try {
            User user = authService.login(input, password);
            System.out.println("Password verified");

            String pin = InputUtil.readLine("Enter PIN: ");
            System.out.println("Access granted.");
        } catch (Exception e) {
            System.out.println("Login failed: "+e.getMessage());
        }
    }
}
