package controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import service.AuthService;

public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public void registerRoutes(Javalin app) {
        app.post("/auth/register", this::register);
        app.post("/auth/login", this::login);
        app.post("/auth/logout", this::logout);
    }

    private void register(Context ctx) {
        authService.register(ctx);
    }

    private void login(Context ctx) {
        authService.login(ctx);
    }

    private void logout(Context ctx) {
        authService.logout(ctx);
    }
}
