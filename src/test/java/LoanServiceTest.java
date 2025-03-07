import dao.LoanDAO;
import dto.LoanRequest;
import model.Loan;
import model.User;
import io.javalin.http.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import service.LoanService;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

class LoanServiceTest {
    private LoanService loanService;
    private LoanDAO loanDAO;
    private Context ctx;

    @BeforeEach
    void setUp() {
        loanDAO = mock(LoanDAO.class);
        loanService = new LoanService(loanDAO);
        ctx = mock(Context.class);
        when(ctx.status(anyInt())).thenReturn(ctx);
    }

    @Test
    void createLoan_UnauthorizedUser_ShouldReturn401() {
        when(ctx.sessionAttribute("user")).thenReturn(null);

        loanService.createLoan(ctx);

        verify(ctx).status(401);
        verify(ctx).result("Unauthorized: Please log in");
    }

    @Test
    void createLoan_MissingAmount_ShouldReturn400() {
        User user = new User(1, "testUser", "hashedPass", "user");
        when(ctx.sessionAttribute("user")).thenReturn(user);
        when(ctx.bodyAsClass(LoanRequest.class)).thenReturn(new LoanRequest(null, "personal"));

        loanService.createLoan(ctx);

        verify(ctx).status(400);
        verify(ctx).result("Invalid request: amount and loanType are required");
    }

    @Test
    void createLoan_MissingLoanType_ShouldReturn400() {
        User user = new User(1, "testUser", "hashedPass", "user");
        when(ctx.sessionAttribute("user")).thenReturn(user);
        when(ctx.bodyAsClass(LoanRequest.class)).thenReturn(new LoanRequest(BigDecimal.valueOf(5000), null));

        loanService.createLoan(ctx);

        verify(ctx).status(400);
        verify(ctx).result("Invalid request: amount and loanType are required");
    }

    @Test
    void createLoan_NegativeAmount_ShouldReturn400() {
        User user = new User(1, "testUser", "hashedPass", "user");
        when(ctx.sessionAttribute("user")).thenReturn(user);
        when(ctx.bodyAsClass(LoanRequest.class)).thenReturn(new LoanRequest(BigDecimal.valueOf(-100), "personal"));

        loanService.createLoan(ctx);

        verify(ctx).status(400);
        verify(ctx).result("Invalid loan amount");
    }

    @Test
    void createLoan_ZeroAmount_ShouldReturn400() {
        User user = new User(1, "testUser", "hashedPass", "user");
        when(ctx.sessionAttribute("user")).thenReturn(user);
        when(ctx.bodyAsClass(LoanRequest.class)).thenReturn(new LoanRequest(BigDecimal.ZERO, "personal"));

        loanService.createLoan(ctx);

        verify(ctx).status(400);
        verify(ctx).result("Invalid loan amount");
    }

    @Test
    void createLoan_ValidRequest_ShouldReturn201() throws SQLException {
        User user = new User(1, "testUser", "hashedPass", "user");
        when(ctx.sessionAttribute("user")).thenReturn(user);
        when(ctx.bodyAsClass(LoanRequest.class)).thenReturn(new LoanRequest(BigDecimal.valueOf(5000), "personal"));

        loanService.createLoan(ctx);

        verify(loanDAO).createLoan(any(Loan.class));
        verify(ctx).status(201);
        verify(ctx).result("Loan application submitted successfully");
    }

    @Test
    void createLoan_Exception_ShouldReturn400() {
        User user = new User(1, "testUser", "hashedPass", "user");
        when(ctx.sessionAttribute("user")).thenReturn(user);
        when(ctx.bodyAsClass(LoanRequest.class)).thenThrow(new RuntimeException("Invalid JSON"));

        loanService.createLoan(ctx);

        verify(ctx).status(400);
        verify(ctx).result("Invalid request format: Invalid JSON");
    }
}
