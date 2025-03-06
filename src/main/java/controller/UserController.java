package controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import service.UserService;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void registerRoutes(Javalin app) {
        app.get("/users/{id_user}", this::getUser);
    }

    private void getUser(Context ctx) {
        userService.getUser(ctx);
    }
}

