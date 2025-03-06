package middleware;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.User;

public class AuthMiddleware {
    public static Handler requireLogin(Handler handler) {
        return ctx -> {
            User user = ctx.sessionAttribute("user");
            if (user == null) {
                ctx.status(401).result("Unauthorized: Please log in");
            } else {
                handler.handle(ctx);
            }
        };
    }

    public static Handler requireManager(Handler handler) {
        return ctx -> {
            User user = ctx.sessionAttribute("user");
            if (user == null || !"manager".equals(user.getRole())) {
                ctx.status(403).result("Forbidden: Only managers can access this resource");
            } else {
                handler.handle(ctx);
            }
        };
    }
}
