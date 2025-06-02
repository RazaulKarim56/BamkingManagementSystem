import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Abstraction
abstract class BankAccount implements Serializable {
    private static int idCounter = 1000;
    private final int accountNumber;
    private String holderName;
    protected double balance;
    protected List<String> transactionHistory = new ArrayList<>();

    public BankAccount(String holderName, double initialDeposit) {
        this.accountNumber = idCounter++;
        this.holderName = holderName;
        this.balance = initialDeposit;
        transactionHistory.add("Account created with deposit: " + initialDeposit);
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public double getBalance() {
        return balance;
    }

    public List<String> getTransactionHistory() {
        return transactionHistory;
    }

    public void deposit(double amount) {
        if (amount >= 500) {
            balance += amount;
            transactionHistory.add("Deposited: " + amount);
        } else {
            transactionHistory.add("Failed deposit attempt: " + amount + " (minimum 500)");
        }
    }

    public abstract void withdraw(double amount);

    // Old method - used for console (you can keep it optionally)
    public void displayDetails() {
        System.out.println(getDetails()); // Reuse the getDetails method
    }

    // New GUI-compatible method
    public String getDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Account Number: ").append(accountNumber).append("\n");
        sb.append("Holder Name: ").append(holderName).append("\n");
        sb.append("Balance: ").append(balance).append("\n");
        sb.append("Transaction History:\n");
        for (String t : transactionHistory) {
            sb.append("- ").append(t).append("\n");
        }
        return sb.toString();
    }
}
