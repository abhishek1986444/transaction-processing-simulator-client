package com.example.version1.client.helpermodule;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


import java.sql.*;



public class DatabaseManager {

    private static final String URL =
            "jdbc:sqlite:banking_client.db";


    public static Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(URL);
    }


public static boolean transactionExists(
        String requestId)
        throws SQLException {

    String sql = """
            SELECT 1
            FROM transactions
            WHERE request_id = ?
            LIMIT 1
            """;

    try (Connection con = getConnection();
         PreparedStatement ps =
                 con.prepareStatement(sql)) {

        ps.setString(1, requestId);

        try (ResultSet rs =
                     ps.executeQuery()) {

            return rs.next();
        }
    }
}




public static void showTransactionHistory() throws SQLException {

    String sql = """
            SELECT
                id,
                request_id,
                transaction_id,
                from_username,
                to_username,
                from_account,
                to_account,
                amount,
                status,
                reason,
                created_at
            FROM transactions
            ORDER BY id DESC
            """;

    try (Connection con = getConnection();
         java.sql.PreparedStatement ps = con.prepareStatement(sql);
         java.sql.ResultSet rs = ps.executeQuery()) {

        boolean found = false;

        System.out.println("\n========== TRANSACTION HISTORY ==========\n");

        while (rs.next()) {
            found = true;

            System.out.println("ID              : " + rs.getInt("id"));
            System.out.println("Request ID      : " + rs.getString("request_id"));
            System.out.println("Transaction ID  : " + rs.getString("transaction_id"));
            System.out.println("From User       : " + rs.getString("from_username"));
            System.out.println("To User         : " + rs.getString("to_username"));
            System.out.println("From Account    : " + rs.getString("from_account"));
            System.out.println("To Account      : " + rs.getString("to_account"));
            System.out.println("Amount          : " + rs.getDouble("amount"));
            System.out.println("Status          : " + rs.getString("status"));
            System.out.println("Reason          : " + rs.getString("reason"));
            System.out.println("Created At      : " + rs.getString("created_at"));

            System.out.println("-----------------------------------------");
        }

        if (!found) {
            System.out.println("No transactions found.");
        }
    }
}


public static void showTransaction(String requestId)
        throws SQLException {

    String sql = """
            SELECT *
            FROM transactions
            WHERE request_id = ?
            """;

    try (Connection con = getConnection();
         java.sql.PreparedStatement ps =
                 con.prepareStatement(sql)) {

        ps.setString(1, requestId);

        try (java.sql.ResultSet rs = ps.executeQuery()) {

            if (!rs.next()) {
                System.out.println("\nTransaction NOT found in local database.");
                return;
            }

            System.out.println("\n========== TRANSACTION RECORD ==========\n");

            System.out.println("ID              : " + rs.getInt("id"));
            System.out.println("Request ID      : " + rs.getString("request_id"));
            System.out.println("Transaction ID  : " + rs.getString("transaction_id"));
            System.out.println("From User       : " + rs.getString("from_username"));
            System.out.println("To User         : " + rs.getString("to_username"));
            System.out.println("From Account    : " + rs.getString("from_account"));
            System.out.println("To Account      : " + rs.getString("to_account"));
            System.out.println("Amount          : " + rs.getDouble("amount"));
            System.out.println("Status          : " + rs.getString("status"));
            System.out.println("Reason          : " + rs.getString("reason"));
            System.out.println("Created At      : " + rs.getString("created_at"));
        }
    }
}





public static void updateTransactionStatus(
        String requestId,
        String transactionId,
        String status,
        String reason)
        throws SQLException {

    String sql = """
            UPDATE transactions
            SET
                transaction_id = ?,
                status = ?,
                reason = ?
            WHERE request_id = ?
            """;

    try (Connection con = getConnection();
         java.sql.PreparedStatement ps =
                 con.prepareStatement(sql)) {

        ps.setString(1, transactionId);
        ps.setString(2, status);
        ps.setString(3, reason);
        ps.setString(4, requestId);

        ps.executeUpdate();
    }
}



public static void saveTransaction(
        String requestId,
        String fromUsername,
        String toUsername,
        String fromAccount,
        String toAccount,
        double amount,
        String status)
        throws SQLException {

    String sql = """
            INSERT INTO transactions
            (
                request_id,
                from_username,
                to_username,
                from_account,
                to_account,
                amount,
                status,
                created_at
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, datetime('now'))
            """;

    try (Connection con = getConnection();
         java.sql.PreparedStatement ps =
                 con.prepareStatement(sql)) {

        ps.setString(1, requestId);
        ps.setString(2, fromUsername);
        ps.setString(3, toUsername);
        ps.setString(4, fromAccount);
        ps.setString(5, toAccount);
        ps.setDouble(6, amount);
        ps.setString(7, status);

        ps.executeUpdate();
    }
}




    public static void createTables()
            throws SQLException {

        String sql = """
                CREATE TABLE IF NOT EXISTS transactions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    request_id TEXT UNIQUE NOT NULL,
                    transaction_id TEXT,
                    from_username TEXT,
                    to_username TEXT,
                    from_account TEXT,
                    to_account TEXT,
                    amount REAL,
                    status TEXT,
                    reason TEXT,
                    created_at TEXT
                )
                """;


        try (Connection con = getConnection();
             Statement stmt = con.createStatement()) {

            stmt.executeUpdate(sql);
        }
    }
}