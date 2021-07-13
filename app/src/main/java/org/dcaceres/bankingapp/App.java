package org.dcaceres.bankingapp;
import java.util.Scanner;

public class App {


    static Account[] generatedAccounts = new Account[100];
    static int accountsCounter = 0;
    static SqlController sqlController;
    static String url = "jdbc:sqlite:";
    public static void main(String[] args) {


        String dbName = args[1];
        url += dbName;
        sqlController = new SqlController(url);

        sqlController.createTable();
        //sqlController.showAccountRecords();

        Scanner scanner = new Scanner(System.in);
        while (true){
            displayMenu();
            int option = scanner.nextInt();
            switch (option) {
                case 0 -> exit();
                case 1 -> createAccount();
                case 2 -> login();
            }
        }
    }


    static void displayMenu() {
        System.out.println("1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");
        System.out.print("\r\n");
    }
    static void displayMenu2() {
        System.out.println("1. Balance");
        System.out.println("2. Add Income");
        System.out.println("3. Do Transfer");
        System.out.println("4. Close Account");
        System.out.println("5. Log out");
        System.out.println("0. Exit");
        System.out.print("\r\n");
    }
    static void createAccount() {
        Account account = new Account(new Card());
        sqlController.insertAccount(
                account.getCard().getCardNumber(),
                account.getCard().getPin() + "",
                "0");
        generatedAccounts[accountsCounter] = account;
        accountsCounter++;
        System.out.println("Your card has been created");
        System.out.println("Your card number:\n" + account.getCard().getCardNumber());
        System.out.println("Your card PIN:\n" + account.getCard().getPin());
        System.out.println();
    }

    static void login() {
        System.out.println("Enter your card number:\r");
        Scanner scanner = new Scanner(System.in);
        String cardNumber = scanner.next();
        System.out.println("Enter your PIN:\r");
        String pin = scanner.next();
        if (checkMatch(cardNumber, pin)){
            Account account = sqlController.getAccountByCardNum(cardNumber);
            System.out.println("You have successfully logged in!\n");
            boolean keep = true;
            while (keep){
                displayMenu2();
                int option = scanner.nextInt();
                switch (option) {
                    case 0 -> exit();
                    case 1 -> getBalanceByAccount(account);
                    case 2 -> addIncome(account);
                    case 3 -> doTransfer(account.getCard().getCardNumber());
                    case 4 -> closeAccount(account);
                    case 5 -> keep = false;
                }
            }
        } else {
            System.out.println("Wrong card number or PIN!\n");
        }
    }

    static void addIncome(Account account) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("[+] Enter income: \n");
        int income = scanner.nextInt();
        sqlController.addIncome(account, income);
        //System.out.println("[+] Adding " + income + " to account with cardnum: " + account.getCard().getCardNumber());
        System.out.println("[+] Income was added!\n");
    }

    static  void doTransfer(String fromAccount) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("[+] Enter card number:\n");
        String toAccount = scanner.next();
        if (!(toAccount.equals(fromAccount))) {
            Card card = new Card(toAccount);
            card.setBIN(Integer.parseInt(toAccount.substring(0,6)));
            card.setIdentifier(Integer.parseInt(toAccount.substring(6, 15)));

            //System.out.println("[+] Card identifier is: " + (Integer.parseInt(toAccount.substring(6, 15))) + "\n");
            if (card.passLuhnAlgorithm()) {
                //System.out.println("card " + card.getCardNumber() + "pass luhn algo");
                if (cardExists(toAccount)) {
                    System.out.println("[+] Enter how much money you want to transfer:\n");
                    int amount = scanner.nextInt();
                    int accountBalance = sqlController.getAccountBalanceByCardNum(fromAccount);
                    if (!(amount > accountBalance)) {
                        sqlController.doTransfer(fromAccount, toAccount, amount);
                        System.out.println("[+] Success!\n");
                    } else {
                        System.out.println("[+] Not enough money! \n");
                    }
                } else  {
                    System.out.println("[+] Card does not exist.\n");
                }

            } else {
                System.out.println("[+] Probably you made a mistake in the card number. Please try again!\n");
            }
        } else {
            System.out.println("[+] You can't transfer money to the same account!\n");
        }

    }

    static boolean cardExists(String cardNumb) {
        boolean exists = false;
        if (sqlController.getAccountByCardNum(cardNumb) != null) {
            exists = true;
        }
        return  exists;
    }
    static  void closeAccount(Account account) {
        System.out.println("[+] The account has been closed!\n");
        sqlController.closeAccount(account);
    }

    static boolean checkMatch(String cardNumber, String pin) {
        boolean match = false;
        Account account = sqlController.getAccountByCardNum(cardNumber);
        if (account != null && account.getCard().getPin() == Integer.parseInt(pin)) {
            match = true;
            //System.out.println("[+] Matched");
        }

        return match;
    }

    static Account getAccountByCardNumber(String cardNumber) {
        for (Account generatedAccount : generatedAccounts) {
            if (generatedAccount.getCard().getCardNumber().equals(cardNumber)) {
                return generatedAccount;
            }
        }
        return null;
    }

    static void getBalanceByAccount(Account account) {
        int balance = sqlController.getAccountBalanceByCardNum(account.getCard().getCardNumber());
        if (balance != -1) {
            System.out.println("[+] Account balance is: " + balance + "\n");
        } else {
            System.out.println("[+] Could not get account balance."+ "\n");
        }
    }

    static void exit() {
        System.out.println("Bye!");
        System.exit(0);
    }
}
