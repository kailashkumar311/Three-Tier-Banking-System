package com.bank.dao;

import com.bank.db.DatabaseConnection;
import com.bank.db.SQLConstants;
import com.bank.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private final Connection connection;

    public UserDAO() {
        connection = DatabaseConnection.getConnection();
    }

    public boolean register(User user) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQLConstants.REGISTER_USER)) {
            preparedStatement.setLong(1, user.getAccountNumber());
            preparedStatement.setString(2, user.getFullName());
            preparedStatement.setString(3, user.getSecurityPin());
            preparedStatement.setDouble(4, user.getBalance());
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User login(long accountNumber, String securityPin) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQLConstants.LOGIN_USER)) {
            preparedStatement.setLong(1, accountNumber);
            preparedStatement.setString(2, securityPin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setAccountNumber(resultSet.getLong("account_number"));
                user.setFullName(resultSet.getString("full_name"));
                user.setSecurityPin(resultSet.getString("security_pin"));
                user.setBalance(resultSet.getDouble("balance"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getBalance(long accountNumber) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQLConstants.GET_BALANCE)) {
            preparedStatement.setLong(1, accountNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Indicates error or account not found
    }
}
