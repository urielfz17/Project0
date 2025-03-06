package dao;

import model.Loan;
import util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {
    public void createLoan(Loan loan) throws SQLException {
        String sql = "INSERT INTO loans (id_user, amount, loan_type, status, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, loan.getIdUser());
            stmt.setBigDecimal(2, loan.getAmount());
            stmt.setString(3, loan.getLoanType());
            stmt.setString(4, loan.getStatus());
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
        }
    }

    public List<Loan> getLoansByUser(int idUser) throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE id_user = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                loans.add(new Loan(
                        rs.getInt("id_loan"),
                        rs.getInt("id_user"),
                        rs.getBigDecimal("amount"),
                        rs.getString("loan_type"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                ));
            }
        }
        return loans;
    }
    public List<Loan> getAllLoans() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                loans.add(new Loan(
                        rs.getInt("id_loan"),
                        rs.getInt("id_user"),
                        rs.getBigDecimal("amount"),
                        rs.getString("loan_type"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                ));
            }
        }
        return loans;
    }

    public Loan getLoanById(int idLoan) throws SQLException {
        String sql = "SELECT * FROM loans WHERE id_loan = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idLoan);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Loan(
                        rs.getInt("id_loan"),
                        rs.getInt("id_user"),
                        rs.getBigDecimal("amount"),
                        rs.getString("loan_type"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                );
            }
        }
        return null;
    }

    public void updateLoanStatus(int idLoan, String status) throws SQLException {
        String sql = "UPDATE loans SET status = ? WHERE id_loan = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, idLoan);
            stmt.executeUpdate();
        }
    }
    public void updateLoan(Loan loan) throws SQLException {
        String sql = "UPDATE loans SET amount = ?, loan_type = ? WHERE id_loan = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, loan.getAmount());
            stmt.setString(2, loan.getLoanType());
            stmt.setInt(3, loan.getIdLoan());

            stmt.executeUpdate();
        }
    }

}

