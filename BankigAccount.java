import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class BankigAccount {
    static List<BankAccount> accounts = new ArrayList<>();
    static BankAccount currentAccount = null;
    static final String FILE_NAME = "accounts.ser";

    public static void main(String[] args) {
        loadAccounts();
        while (true) {
            String[] options = {"Admin Login", "User Login", "Exit"};
            int choice = JOptionPane.showOptionDialog(null, "Login Menu", "Banking System",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            if (choice == 0 && login("Admin")) {
                adminMenu();
            } else if (choice == 1 && login("User")) {
                userMenu();
            } else if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) {
                saveAccounts();
                break;
            } else {
                JOptionPane.showMessageDialog(null, "Login failed.");
            }
        }
    }

   static boolean login(String role) {
    String username = JOptionPane.showInputDialog(role + " Username:");
    if (username == null) return false;

    String password = JOptionPane.showInputDialog(role + " Password:");
    if (password == null) return false;

    if (!isValidUsername(username)) {
        JOptionPane.showMessageDialog(null, "Invalid username. Must start with a capital letter.");
        return false;
    }

    if (!isValidPassword(password)) {
        JOptionPane.showMessageDialog(null, "Invalid password. Must be strong (letter, number, symbol).");
        return false;
    }

    String otp = generateOTP();
    JOptionPane.showMessageDialog(null, "OTP sent (Simulation): " + otp);
    String enteredOtp = JOptionPane.showInputDialog("Enter OTP:");

    if (enteredOtp == null || !enteredOtp.equals(otp)) {
        JOptionPane.showMessageDialog(null, "Invalid OTP. Login failed.");
        return false;
    }

    JOptionPane.showMessageDialog(null, role + " login successful.");
    return true;
}

static String generateOTP() {
    Random random = new Random();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 6; i++) {
        sb.append(random.nextInt(10));
    }
    return sb.toString();
}


    static boolean isValidUsername(String username) {
        return username.matches("^[A-Z][a-zA-Z]{0,7}$");
    }

    static boolean isValidPassword(String password) {
        return password.length() >= 8 && password.length() <= 32 &&
                Pattern.compile("[a-zA-Z]").matcher(password).find() &&
                Pattern.compile("[0-9]").matcher(password).find() &&
                Pattern.compile("[^a-zA-Z0-9]").matcher(password).find();
    }

    static void adminMenu() {
        while (true) {
            String[] options = {"Create Account", "Update Account", "Delete Account", "View All Accounts", "Logout"};
            int choice = JOptionPane.showOptionDialog(null, "Admin Menu", "Admin Options",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            switch (choice) {
                case 0 -> createAccount();
                case 1 -> updateAccount();
                case 2 -> deleteAccount();
                case 3 -> viewAllAccounts();
                case 4, JOptionPane.CLOSED_OPTION -> { return; }
                default -> JOptionPane.showMessageDialog(null, "Invalid choice.");
            }
        }
    }

    static void userMenu() {
        currentAccount = findAccountByInput();
        if (currentAccount == null) return;

        while (true) {
            String[] options = {"View Account Details", "Deposit Money", "Withdraw Money", "Balance Enquiry", "Logout"};
            int choice = JOptionPane.showOptionDialog(null, "User Menu", "User Options",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            switch (choice) {
                case 0 -> JOptionPane.showMessageDialog(null, currentAccount.getDetails());
                case 1 -> depositMoney();
                case 2 -> withdrawMoney();
                case 3 -> JOptionPane.showMessageDialog(null, "Balance: " + currentAccount.getBalance());
                case 4, JOptionPane.CLOSED_OPTION -> { return; }
                default -> JOptionPane.showMessageDialog(null, "Invalid choice.");
            }
        }
    }

    static void createAccount() {
        String name = JOptionPane.showInputDialog("Enter account holder name:");
        if (name == null) return;

        String depositStr = JOptionPane.showInputDialog("Enter initial deposit amount:");
        if (depositStr == null) return;

        try {
            double deposit = Double.parseDouble(depositStr);
            BankAccount account = new CurrentAccount(name, deposit);
            accounts.add(account);
            saveAccounts();
            JOptionPane.showMessageDialog(null, "Account created.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid deposit amount.");
        }
    }

    static void updateAccount() {
        BankAccount account = findAccountByInput();
        if (account == null) return;

        String name = JOptionPane.showInputDialog("Enter new holder name:");
        if (name != null) {
            account.setHolderName(name);
            saveAccounts();
            JOptionPane.showMessageDialog(null, "Account updated.");
        }
    }

    static void deleteAccount() {
        BankAccount account = findAccountByInput();
        if (account == null) return;

        accounts.remove(account);
        saveAccounts();
        JOptionPane.showMessageDialog(null, "Account deleted.");
    }

    static void viewAllAccounts() {
        if (accounts.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No accounts found.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (BankAccount account : accounts) {
            sb.append(account.getDetails()).append("\n---------------------\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(500, 400));
        JOptionPane.showMessageDialog(null, scrollPane, "All Accounts", JOptionPane.INFORMATION_MESSAGE);
    }

    static void depositMoney() {
        String amountStr = JOptionPane.showInputDialog("Enter deposit amount:");
        if (amountStr == null) return;

        try {
            double amount = Double.parseDouble(amountStr);
            currentAccount.deposit(amount);
            saveAccounts();
            JOptionPane.showMessageDialog(null, "Deposit successful.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    static void withdrawMoney() {
        String amountStr = JOptionPane.showInputDialog("Enter withdrawal amount:");
        if (amountStr == null) return;

        try {
            double amount = Double.parseDouble(amountStr);
            currentAccount.withdraw(amount);
            saveAccounts();
            JOptionPane.showMessageDialog(null, "Withdrawal successful.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    static BankAccount findAccountByInput() {
        String name = JOptionPane.showInputDialog("Enter account holder name to search:");
        if (name == null) return null;

        for (BankAccount acc : accounts) {
            if (acc.getHolderName().equalsIgnoreCase(name)) {
                return acc;
            }
        }

        JOptionPane.showMessageDialog(null, "Account not found.");
        return null;
    }

    static void saveAccounts() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(accounts);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to save accounts.");
        }
    }

    static void loadAccounts() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            accounts = (List<BankAccount>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            accounts = new ArrayList<>();
        }
    }
}