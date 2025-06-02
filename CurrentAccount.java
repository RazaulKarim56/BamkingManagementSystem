import javax.swing.JOptionPane;

class CurrentAccount extends BankAccount {
    public CurrentAccount(String name, double deposit) {
        super(name, deposit);
    }

    @Override
    public void withdraw(double amount) {
        double limit = 1000; // overdraft limit
        if (amount >= 500 && amount <= 50000 && amount <= (balance + limit)) {
            balance -= amount;
            transactionHistory.add("Withdrawn: " + amount);
            JOptionPane.showMessageDialog(null, "Withdrawn from Current Account.");
        } else {
            JOptionPane.showMessageDialog(null, "Invalid withdrawal.\nAmount must be between 500 and 50000,\nand within available balance (including 1000 overdraft).");
            transactionHistory.add("Failed withdrawal attempt: " + amount);
        }
    }
}
