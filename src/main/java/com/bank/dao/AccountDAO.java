package com.bank.dao;

import com.bank.db.DatabaseConnection;
import com.bank.db.SQLConstants;
import com.bank.exception.InsufficientBalanceException;
import com.bank.model.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    private final UserDAO userDAO;
    private final Connection connection;

    public AccountDAO(UserDAO userDAO) {
        this.userDAO =  userDAO;
        this.connection = DatabaseConnection.getConnection();
    }

    public boolean withdraw(long accountNumber, double amount) throws InsufficientBalanceException {
        try {
            connection.setAutoCommit(false);
            double balance = userDAO.getBalance(accountNumber);
            if (balance < amount) {
                connection.rollback();
                throw new InsufficientBalanceException("Insufficient Balance!");
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(SQLConstants.DEBIT_BALANCE)) {
                preparedStatement.setDouble(1, amount);
                preparedStatement.setLong(2, accountNumber);
                preparedStatement.executeUpdate();
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(SQLConstants.RECORD_TRANSACTION_WITHDRAW)) {
                preparedStatement.setLong(1, accountNumber);
                preparedStatement.setDouble(2, amount);
                preparedStatement.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean deposit(long accountNumber, double amount) {
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQLConstants.UPDATE_BALANCE)) {
                preparedStatement.setDouble(1, amount);
                preparedStatement.setLong(2, accountNumber);
                preparedStatement.executeUpdate();
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(SQLConstants.RECORD_TRANSACTION_DEBIT)) {
                preparedStatement.setLong(1, accountNumber);
                preparedStatement.setDouble(2, amount);
                preparedStatement.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean transferMoney(long senderAccountNumber, long receiverAccountNumber, double amount) throws InsufficientBalanceException {
        // SECURITY CHECK: you are not able to send money itself
        if (senderAccountNumber == receiverAccountNumber) {
            System.out.println("Error: you can not able to send money itself!");
            return false;
        }

        try {
            connection.setAutoCommit(false);
            double senderBalance = userDAO.getBalance(senderAccountNumber);
            if (senderBalance < amount) {
                connection.rollback();
                throw new InsufficientBalanceException("Insufficient Balance for transfer!");
            }

            double receiverBalance = userDAO.getBalance(receiverAccountNumber);
            if (receiverBalance == -1) {
                connection.rollback();
                System.out.println("Receiver account does not exist.");
                return false;
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(SQLConstants.DEBIT_BALANCE)) {
                preparedStatement.setDouble(1, amount);
                preparedStatement.setLong(2, senderAccountNumber);
                preparedStatement.executeUpdate();
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(SQLConstants.UPDATE_BALANCE)) {
                preparedStatement.setDouble(1, amount);
                preparedStatement.setLong(2, receiverAccountNumber);
                preparedStatement.executeUpdate();
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(SQLConstants.INSERT_TRANSACTION)) {
                preparedStatement.setLong(1, senderAccountNumber);
                preparedStatement.setLong(2, receiverAccountNumber);
                preparedStatement.setDouble(3, amount);
                preparedStatement.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public List<Transaction> getMiniStatement(long accountNumber) {
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQLConstants.MINI_STATEMENT)) {
            preparedStatement.setLong(1, accountNumber);
            preparedStatement.setLong(2, accountNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(resultSet.getInt("id"));

                long senderAcc = resultSet.getLong("sender_acc");
                if (!resultSet.wasNull()) {
                    transaction.setSenderAcc(senderAcc);
                }

                long receiverAcc = resultSet.getLong("receiver_acc");
                if (!resultSet.wasNull()) {
                    transaction.setReceiverAcc(receiverAcc);
                }

                transaction.setAmount(resultSet.getDouble("amount"));
                transaction.setType(resultSet.getString("type"));
                transaction.setTimestamp(resultSet.getTimestamp("timestamp"));

                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
}

