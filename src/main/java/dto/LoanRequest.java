package dto;
import java.math.BigDecimal;

public class LoanRequest {
    private BigDecimal amount;
    private String loanType;

    public LoanRequest() {}

    public LoanRequest(BigDecimal amount, String loanType) {
        this.amount = amount;
        this.loanType = loanType;
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
}
