package controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import service.LoanService;
import middleware.AuthMiddleware;

public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    public void registerRoutes(Javalin app) {
        app.get("/loans", AuthMiddleware.requireLogin(this::getLoans));
        app.get("/loans/{id_loan}", AuthMiddleware.requireLogin(this::getLoanById));
        app.put("/loans/{id_loan}", AuthMiddleware.requireLogin(this::updateLoan));

        app.post("/loans", AuthMiddleware.requireLogin(this::createLoan));

        app.put("/loans/{id_loan}/approve", AuthMiddleware.requireManager(this::approveLoan));
        app.put("/loans/{id_loan}/reject", AuthMiddleware.requireManager(this::rejectLoan));
    }

    private void createLoan(Context ctx) {
        loanService.createLoan(ctx);
    }

    private void getLoanById(Context ctx) {
        loanService.getLoanById(ctx);
    }

    private void getLoans(Context ctx) {
        loanService.getLoans(ctx);
    }

    private void approveLoan(Context ctx) {
        loanService.approveLoan(ctx);
    }

    private void rejectLoan(Context ctx) {
        loanService.rejectLoan(ctx);
    }
    private void updateLoan(Context ctx) {
        loanService.updateLoan(ctx);
    }
}



