package service;

import dao.LoanDAO;
import dto.LoanRequest;
import model.Loan;
import model.User;
import io.javalin.http.Context;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class LoanService {
    private final LoanDAO loanDAO;

    public LoanService(LoanDAO loanDAO) {
        this.loanDAO = loanDAO;
    }

    public void getLoans(Context ctx) {
        User user = ctx.sessionAttribute("user");

        if (user == null) {
            ctx.status(401).result("Unauthorized: Please log in");
            return;
        }

        try {
            List<Loan> loans;
            if ("manager".equals(user.getRole())) {
                loans = loanDAO.getAllLoans(); // ✅ CORRECCIÓN: Método implementado en LoanDAO
            } else {
                loans = loanDAO.getLoansByUser(user.getIdUser());
            }
            ctx.status(200).json(loans);
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }

    public void approveLoan(Context ctx) {
        String idParam = ctx.pathParam("id_loan");
        int idLoan;

        try {
            idLoan = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid loan ID format");
            return;
        }

        try {
            Loan loan = loanDAO.getLoanById(idLoan); // ✅ CORRECCIÓN: Método agregado en LoanDAO
            if (loan == null) {
                ctx.status(404).result("Loan not found");
                return;
            }

            if (!"pending".equals(loan.getStatus())) {
                ctx.status(400).result("Loan cannot be approved (must be pending)");
                return;
            }

            loanDAO.updateLoanStatus(idLoan, "approved"); // ✅ CORRECCIÓN: Método agregado en LoanDAO
            ctx.status(200).result("Loan approved successfully");
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }

    public void rejectLoan(Context ctx) {
        String idParam = ctx.pathParam("id_loan");
        int idLoan;

        try {
            idLoan = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid loan ID format");
            return;
        }

        try {
            Loan loan = loanDAO.getLoanById(idLoan); // ✅ CORRECCIÓN
            if (loan == null) {
                ctx.status(404).result("Loan not found");
                return;
            }

            if (!"pending".equals(loan.getStatus())) {
                ctx.status(400).result("Loan cannot be rejected (must be pending)");
                return;
            }

            loanDAO.updateLoanStatus(idLoan, "rejected"); // ✅ CORRECCIÓN
            ctx.status(200).result("Loan rejected successfully");
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }
    public void createLoan(Context ctx) {
        User user = ctx.sessionAttribute("user");

        if (user == null) {
            ctx.status(401).result("Unauthorized: Please log in");
            return;
        }

        try {
            LoanRequest loanRequest = ctx.bodyAsClass(LoanRequest.class);

            if (loanRequest.getAmount() == null || loanRequest.getLoanType() == null) {
                ctx.status(400).result("Invalid request: amount and loanType are required");
                return;
            }

            BigDecimal amount = loanRequest.getAmount();
            String loanType = loanRequest.getLoanType();

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                ctx.status(400).result("Invalid loan amount");
                return;
            }

            Loan loan = new Loan(0, user.getIdUser(), amount, loanType, "pending", null);
            loanDAO.createLoan(loan);

            ctx.status(201).result("Loan application submitted successfully");
        } catch (Exception e) {
            ctx.status(400).result("Invalid request format: " + e.getMessage());
        }
    }

    public void getLoanById(Context ctx) {
        String idParam = ctx.pathParam("id_loan");
        int idLoan;

        try {
            idLoan = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid loan ID format");
            return;
        }

        try {
            Loan loan = loanDAO.getLoanById(idLoan);
            if (loan == null) {
                ctx.status(404).result("Loan not found");
                return;
            }

            User user = ctx.sessionAttribute("user");
            if (user == null) {
                ctx.status(401).result("Unauthorized: Please log in");
                return;
            }

            if (!"manager".equals(user.getRole()) && user.getIdUser() != loan.getIdUser()) {
                ctx.status(403).result("Forbidden: You cannot access this loan");
                return;
            }

            ctx.status(200).json(loan);
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }
    public void updateLoan(Context ctx) {
        User user = ctx.sessionAttribute("user");

        if (user == null) {
            ctx.status(401).result("Unauthorized: Please log in");
            return;
        }

        String idParam = ctx.pathParam("id_loan");
        int idLoan;

        try {
            idLoan = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid loan ID format");
            return;
        }

        try {
            // 1️⃣ Obtener el préstamo desde la base de datos
            Loan loan = loanDAO.getLoanById(idLoan);
            if (loan == null) {
                ctx.status(404).result("Loan not found");
                return;
            }

            // 2️⃣ Verificar si el usuario es el dueño del préstamo
            if (!"manager".equals(user.getRole()) && user.getIdUser() != loan.getIdUser()) {
                ctx.status(403).result("Forbidden: You cannot update this loan");
                return;
            }

            // 3️⃣ Obtener los datos del body
            LoanRequest loanRequest = ctx.bodyAsClass(LoanRequest.class);

            // 4️⃣ Validar y actualizar los campos permitidos
            if (loanRequest.getAmount() != null && loanRequest.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                loan.setAmount(loanRequest.getAmount());
            } else if (loanRequest.getAmount() != null) {
                ctx.status(400).result("Invalid loan amount");
                return;
            }

            if (loanRequest.getLoanType() != null && !loanRequest.getLoanType().trim().isEmpty()) {
                loan.setLoanType(loanRequest.getLoanType());
            }

            // 5️⃣ Guardar los cambios en la base de datos
            loanDAO.updateLoan(loan);
            ctx.status(200).result("Loan updated successfully");
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(400).result("Invalid request format: " + e.getMessage());
        }
    }


}
