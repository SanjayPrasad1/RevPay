package com.revpay.app;

import com.revpay.db.DBConnection;
import com.revpay.db.InvoiceDao;
import com.revpay.model.*;
import com.revpay.service.*;
import com.revpay.util.InputUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public class RevPayApp {

    private static final UserService userService = new UserService();
    private static final AuthService authService = new AuthService();
    private static User loggedInUser = null;
    private static final WalletService walletService = new WalletService();
    private static final MoneyRequestService moneyRequestService = new MoneyRequestService();
    private static final CardService cardService = new CardService();
    private static final InvoiceDao invoiceDao = new InvoiceDao();
    private static final InvoiceService invoiceService = new InvoiceService();

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

    /*private static void handleRegister() {

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
    }*/
    private static void handleRegister() {

        System.out.println("\n---- Register ----");

        String accountType = InputUtil.readLine("Account Type (PERSONAL / BUSINESS): ").toUpperCase();

        try {
            if ("PERSONAL".equals(accountType)) {

                String fullName = InputUtil.readLine("Full name: ");
                String email = InputUtil.readLine("Email: ");
                String phone = InputUtil.readLine("Phone: ");
                String password = InputUtil.readLine("Password: ");
                String pin = InputUtil.readLine("PIN: ");

                userService.registerPersonal(
                        fullName,
                        email,
                        phone,
                        password,
                        pin
                );

                System.out.println("Personal account registered successfully.");

            } else if ("BUSINESS".equals(accountType)) {

                String businessName = InputUtil.readLine("Business Name: ");
                String email = InputUtil.readLine("Email: ");
                String phone = InputUtil.readLine("Phone: ");
                String password = InputUtil.readLine("Password: ");
                String pin = InputUtil.readLine("PIN: ");

                String businessType = InputUtil.readLine("Business Type: ");
                String taxId = InputUtil.readLine("Tax ID: ");
                String address = InputUtil.readLine("Business Address: ");

                userService.registerBusiness(
                        businessName,
                        email,
                        phone,
                        password,
                        pin,
                        businessType,
                        taxId,
                        address
                );

                System.out.println("Business account registered successfully.");

            } else {
                System.out.println("Invalid account type.");
            }

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
            authService.verifyPin(user.getId(), pin);

            loggedInUser = user;

            System.out.println("Access granted.");
            System.out.println("Welcome "+user.getFullName());

//            showUserMenu();
            if ("BUSINESS".equalsIgnoreCase(user.getUserType())){
                showBusinessMenu();
            }else {
                showUserMenu();
            }
        } catch (Exception e) {
            System.out.println("Login failed: "+e.getMessage());
        }
    }

    private static void showUserMenu() {

        while (true) {
            System.out.println("\n==== User Menu ====");
            System.out.println("1. View Profile");
            System.out.println("2. View Balance");
            System.out.println("3. Send Money");
            System.out.println("4. Request Money");
            System.out.println("5. View Pending Requests");
            System.out.println("6. Accept Money Request");
            System.out.println("7. View Transaction");
            System.out.println("8. Manage Cards");
            System.out.println("9. Add Money to Wallet");
            System.out.println("10. WithDraw Money");
            System.out.println("11. View Pending Invoices");
            System.out.println("12. Accept Invoice");
            System.out.println("13. Logout");

            int choice = InputUtil.readInt("Enter choice: ");

            switch (choice) {
                case 1 -> handleViewProfile();
                case 2 -> handleViewBalance();
                case 3 -> handleSendMoney();
                case 4 -> handleRequestMoney();
                case 5 -> handleViewPendingRequests();
                case 6 -> handleAcceptRequest();
                case 7 -> handleViewTransactions();
                case 8 -> handleCardMenu();
                case 9 -> handleAddMoney();
                case 10 -> handleWithdrawMoney();
                case 11 -> handleViewPendingInvoices();
                case 12 -> handleAcceptInvoice();
                case 13 -> {
                    loggedInUser = null;
                    System.out.println("Logged out.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void handleViewProfile() {

        System.out.println("\n---- Profile ----");
        System.out.println("Name: " + loggedInUser.getFullName());
        System.out.println("Email: " + loggedInUser.getEmail());
        System.out.println("Phone: " + loggedInUser.getPhone());
        System.out.println("Type: " + loggedInUser.getUserType());
    }

    private static void handleViewBalance() {

        try(Connection con = DBConnection.getConnection()) {
            Wallet wallet = walletService.getWallet(loggedInUser.getId(),con);
            System.out.println("\n---- Wallet ----");
            System.out.println("Balance: " + wallet.getBalance());
        } catch (Exception e) {
            System.out.println("Failed to load wallet.");
        }
    }
    private static void handleSendMoney() {

        System.out.println("\n---- Send Money ----");

        String to = InputUtil.readLine("Send to (email / phone / userId): ");
        BigDecimal amount = InputUtil.readBigDecimal("Amount: ");
        String note = InputUtil.readLine("Note (optional): ");

        try(Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            MoneyTransferService mts = new MoneyTransferService();
            mts.transferMoney(loggedInUser.getId(), to, amount, note, con);
            con.commit();

            System.out.println("Transfer successful.");
        } catch (Exception e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
    }

    private static void handleViewTransactions() {

        TransactionService ts = new TransactionService();

        try {
            List<TransactionView> list =
                    ts.getUserTransactionViews(loggedInUser.getId());

            if (list.isEmpty()) {
                System.out.println("No transactions found.");
                return;
            }

            System.out.println("\n---- Transactions ----");

            for (TransactionView t : list) {
                System.out.println(
                        t.getDisplayType() + " | " +
                        t.getAmount() + " | " +
                        t.getCreatedAt()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load transactions.");
        }
    }

    private static void handleRequestMoney() {

        try {
//            long requesteeId = InputUtil.readLong("Enter user id to request from: ");
            String to = InputUtil.readLine("Request from (email / phone): ");
            BigDecimal amount = InputUtil.readBigDecimal("Enter amount: ");

            MoneyRequestService moneyRequestService = new MoneyRequestService();
            moneyRequestService.requestMoney(
                    loggedInUser.getId(),
//                    requesteeId,
                    to,
                    amount
            );

            System.out.println("Money request sent successfully.");

        } catch (Exception e) {
            System.out.println("Failed to request money: " + e.getMessage());
        }
    }

    private static void handleAcceptRequest() {

        long requestId = InputUtil.readLong("Enter request ID to accept: ");

        try {
            moneyRequestService.acceptRequest(
                    requestId,
                    loggedInUser.getId()
            );
            System.out.println("Request accepted.");
        } catch (Exception e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }

    private static void handleViewPendingRequests() {

        try {
            List<MoneyRequest> list =
                    moneyRequestService.viewPendingRequests(loggedInUser.getId());

            if (list.isEmpty()) {
                System.out.println("No pending money requests.");
                return;
            }

            System.out.println("\n---- Pending Money Requests ----");

            for (MoneyRequest r : list) {
                System.out.println(
                        "Request ID: " + r.getId() +
                                " | From: " + r.getRequesterName() +
                                " | Amount: " + r.getAmount() +
                                " | Date: " + r.getCreatedAt()
                );
            }

        } catch (Exception e) {
            System.out.println("Failed to load requests: " + e.getMessage());
        }
    }

    private static void handleCardMenu() {
        CardService cardService = new CardService();

        while (true) {
            System.out.println("\n==== Card Management ====");
            System.out.println("1. Add Card");
            System.out.println("2. View Cards");
            System.out.println("3. Set Default Card");
            System.out.println("4. Back");

            int choice = InputUtil.readInt("Enter choice: ");

            try (Connection con = DBConnection.getConnection()) {
                switch (choice) {
                    case 1 -> handleAddCard(cardService, con);
                    case 2 -> handleViewCards(cardService, con);
                    case 3 -> handleSetDefaultCard(cardService, con);
                    case 4 -> { return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void handleAddCard(CardService cardService, Connection con) throws Exception {
        System.out.println("\n---- Add Card ----");
        String holderName = InputUtil.readLine("Card Holder Name: ");
        String number = InputUtil.readLine("Card Number: ");
        String cvv = InputUtil.readLine("CVV: ");
        int month = InputUtil.readInt("Expiry Month (1-12): ");
        int year = InputUtil.readInt("Expiry Year (YYYY): ");
        boolean setDefault = InputUtil.readLine("Set as default? (y/n): ").equalsIgnoreCase("y");

        cardService.addCard(
                loggedInUser.getId(),
                holderName,
                number,
                cvv,
                month,
                year,
                setDefault,
                con
        );

        System.out.println("Card added successfully.");
    }
    private static void handleViewCards(CardService cardService, Connection con) throws Exception {
        System.out.println("\n---- Your Cards ----");
        var cards = cardService.getUserCards(loggedInUser.getId(), con);
        if (cards.isEmpty()) {
            System.out.println("No cards found.");
            return;
        }

        for (var c : cards) {
            System.out.println(
                    "ID: " + c.getId() +
                            " | Holder: " + c.getCardHolderName() +
                            " | Expiry: " + c.getExpiryMonth() + "/" + c.getExpiryYear() +
                            " | Default: " + (c.isDefault() ? "Yes" : "No")
            );
        }
    }
    private static void handleSetDefaultCard(CardService cardService, Connection con) throws Exception {
        long cardId = InputUtil.readLong("Enter Card ID to set as default: ");

        // fetch all cards and check ownership
        var cards = cardService.getUserCards(loggedInUser.getId(), con);
        boolean found = false;
        for (var c : cards) {
            if (c.getId() == cardId) {
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("Card not found or not owned by you.");
            return;
        }

        cardService.setDefaultCard(loggedInUser.getId(), cardId, con);
        System.out.println("Card set as default.");
    }

    private static void handleAddMoney() {
        BigDecimal amount = InputUtil.readBigDecimal("Amount to add: ");

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            walletService.addMoneyFromCard(loggedInUser.getId(), amount, con);
            con.commit();
            System.out.println("Money added successfully");
        } catch (Exception e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }

    private static void handleWithdrawMoney() {

        System.out.println("\n---- Withdraw Money ----");

        BigDecimal amount = InputUtil.readBigDecimal("Amount to withdraw: ");
        String note = InputUtil.readLine("Note (optional): ");

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            WalletService walletService = new WalletService();
            walletService.withdrawMoney(
                    loggedInUser,
                    amount,
                    note,
                    con
            );

            con.commit();
            System.out.println("Withdrawal successful.");

        } catch (Exception e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }
    private static void showBusinessMenu() {
        while (true) {
            System.out.println("\n==== Business Menu ====");
            System.out.println("1. Create Invoice");
            System.out.println("2. View My Invoices");
            System.out.println("3. Back");

            int choice = InputUtil.readInt("Enter choice: ");

            try {
                switch (choice) {
                    case 1:
                        handleCreateInvoice();
                        break;
                    case 2:
                        handleViewBusinessInvoices();
                        break;
                    case 3:
                        return;
                    default:
                        System.out.println("Invalid option");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void handleCreateInvoice() throws Exception {

        long customerId = InputUtil.readLong("Enter customer user ID: ");
        BigDecimal amount = InputUtil.readBigDecimal("Total amount: ");
        LocalDate dueDate = InputUtil.readDate("Due date (YYYY-MM-DD): ");

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            invoiceService.createInvoice(
                    loggedInUser.getId(),
                    customerId,
                    amount,
                    dueDate,
                    con
            );

            con.commit();
            System.out.println("Invoice created successfully");
        }
    }

    private static void handleViewBusinessInvoices() throws Exception {

        try (Connection con = DBConnection.getConnection()) {
            List<Invoice> invoices =
                    invoiceDao.findByBusinessUserId(loggedInUser.getId(), con);

            if (invoices.isEmpty()) {
                System.out.println("No invoices found");
                return;
            }

            for (Invoice i : invoices) {
                System.out.println(
                        "ID: " + i.getId() +
                                " | Customer: " + i.getCustomerUserId() +
                                " | Amount: " + i.getTotalAmount() +
                                " | Status: " + i.getStatus() +
                                " | Due: " + i.getDueDate()
                );
            }
        }
    }

    private static void handleViewMyInvoices() throws Exception {

        try (Connection con = DBConnection.getConnection()) {
            List<Invoice> invoices =
                    invoiceDao.findUnpaidByCustomerId(loggedInUser.getId(), con);

            if (invoices.isEmpty()) {
                System.out.println("No unpaid invoices");
                return;
            }

            for (Invoice i : invoices) {
                System.out.println(
                        "Invoice ID: " + i.getId() +
                                " | Business: " + i.getBusinessUserId() +
                                " | Amount: " + i.getTotalAmount() +
                                " | Due: " + i.getDueDate()
                );
            }
        }
    }

    private static void handlePayInvoice() throws Exception {

        long invoiceId = InputUtil.readLong("Enter Invoice ID to pay: ");

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            invoiceService.payInvoice(
                    invoiceId,
                    loggedInUser.getId(),
                    con
            );

            con.commit();
            System.out.println("Invoice paid successfully");
        }
    }

    private static void handleViewPendingInvoices() {
        try {
            InvoiceService service = new InvoiceService();
            List<Invoice> list = service.getPendingInvoicesForCustomer(loggedInUser.getId());

            if (list.isEmpty()) {
                System.out.println("No pending invoices.");
                return;
            }

            for (Invoice i : list) {
                System.out.println(
                        "Invoice ID: " + i.getId() +
                                " | Business: " + i.getBusinessUserId() +
                                " | Amount: " + i.getTotalAmount() +
                                " | Due: " + i.getDueDate()
                );
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleAcceptInvoice() {
        long invoiceId = InputUtil.readLong("Enter Invoice ID to accept: ");

        try {
            InvoiceService service = new InvoiceService();
            service.acceptInvoice(invoiceId, loggedInUser.getId());
            System.out.println("Invoice accepted successfully.");

        } catch (Exception e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }

}
