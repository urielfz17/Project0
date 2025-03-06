package model;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class Loan {
    private int idLoan;
    private int idUser;
    private BigDecimal amount;
    private String loanType;
    private String status;
    private Timestamp createdAt;

    public Loan() {
    }

    public Loan(int idLoan, int idUser, BigDecimal amount, String loanType, String status, Timestamp createdAt) {
        this.idLoan = idLoan;
        this.idUser = idUser;
        this.amount = amount;
        this.loanType = loanType;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getIdLoan() {
        return idLoan;
    }

    public void setIdLoan(int idLoan) {
        this.idLoan = idLoan;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Loan{" +
                "idLoan=" + idLoan +
                ", idUser=" + idUser +
                ", amount=" + amount +
                ", loanType='" + loanType + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
