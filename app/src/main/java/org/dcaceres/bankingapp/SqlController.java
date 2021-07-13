package org.dcaceres.bankingapp;
import org.sqlite.SQLiteDataSource;

import java.sql.*;

public class SqlController {
    SQLiteDataSource dataSource = new SQLiteDataSource();


    public SqlController (String connStr) {
        this.dataSource.setUrl(connStr);
    }

    public int createTable() {
        String createStr = "CREATE TABLE IF NOT EXISTS card (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " number TEXT," +
                " pin TEXT," +
                " balance INTEGER DEFAULT 0)";

        try (Connection conn = dataSource.getConnection()) {
            try(PreparedStatement createStatement = conn.prepareStatement(createStr)) {
                createStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  0;
    }



    public int insertAccount(String number, String pin, String balance) {
        String insertStr = "INSERT INTO card (number, pin, balance) VALUES(?, ?, ?)";
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement pstm = conn.prepareStatement(insertStr)) {

                pstm.setObject(1, number);
                pstm.setObject(2, pin);
                pstm.setObject(3, balance);
                pstm.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int showAccountRecords() {
        String selectStr = "SELECT * FROM card";
        try (Connection conn = dataSource.getConnection()) {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(selectStr);
            System.out.println("[+][+][+][+][+][+][+][+]");
            while (rs.next()) {
                System.out.print("ID: " + rs.getString("id"));
                System.out.print("\tNUMBER: " + rs.getString("number"));
                System.out.print("\tPIN: " + rs.getString("pin"));
                System.out.print("\tBALANCE: " + rs.getString("balance"));
                System.out.println();
            }
            System.out.println("[+][+][+][+][+][+][+][+]");
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  0;
    }

    public int getAccountBalanceByCardNum(String cardNum) {
        int balance = -1;
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement selectBalance =
                    conn.prepareStatement("SELECT * FROM card WHERE number = ?");

            selectBalance.setString(1, cardNum);

            ResultSet rs = selectBalance.executeQuery();
            balance = rs.getInt("balance");
            //System.out.println(balance);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  balance;
    }

    public Account getAccountByCardNum(String cardNum) {
        //System.out.println("Testing getAccountByCardNum method: ");
        //System.out.println("Parameter  " + cardNum);

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement selectAccount =
                         conn.prepareStatement("SELECT * FROM card WHERE number = ?")){
                selectAccount.setString(1, cardNum);


                ResultSet rs = selectAccount.executeQuery();

                Card card = new Card(rs.getString("number"),rs.getString("pin") );
                return new Account(card, Integer.parseInt(rs.getString("balance")));
            } catch (SQLException e) {
                System.out.print("");
            }





        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  null;
    }

    public void addIncome(Account account, int income) {
        String accCardNum = account.getCard().getCardNumber();
        String accIncomeStmt = "UPDATE card SET balance = balance + ? " +
                "WHERE number = ?";
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement updateIncome = conn.prepareStatement(accIncomeStmt)) {
                updateIncome.setInt(1, income);
                updateIncome.setString(2, accCardNum);
                updateIncome.executeUpdate();
                //System.out.println("[+] Adding " + income + "to account " + accCardNum);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void doTransfer(String fromAccount, String toAccount,  int amount) {
        String updateFromAccountStr = "UPDATE card SET balance = balance - ? " +
                "WHERE number = ?";
        String updateToAccountStr = "UPDATE card SET balance = balance + ? " +
                "WHERE number = ?";
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement updateFromAccountStmt = conn.prepareStatement(updateFromAccountStr);
                 PreparedStatement updateToAccountStmt = conn.prepareStatement(updateToAccountStr)) {

                updateFromAccountStmt.setInt(1, amount);
                updateFromAccountStmt.setString(2, fromAccount);
                updateFromAccountStmt.executeUpdate();

                updateToAccountStmt.setInt(1, amount);
                updateToAccountStmt.setString(2, toAccount);
                updateToAccountStmt.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e ) {
            e.printStackTrace();
        }
    }

    public void closeAccount(Account account) {
        String cardNum = account.getCard().getCardNumber();
        String deleteStr = "DELETE FROM card WHERE number = ?";
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement pstm = conn.prepareStatement(deleteStr)) {
                pstm.setString(1, cardNum);
                pstm.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
