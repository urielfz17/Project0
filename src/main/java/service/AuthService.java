package service;

import io.javalin.http.Context;
import dao.UserDAO;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class AuthService {
    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void register(Context ctx) {
        try {
            User user = ctx.bodyAsClass(User.class);

            if (user.getUsername() == null || user.getPasswordHash() == null) {
                ctx.status(400).result("Username and password are required");
                return;
            }
            if (user.getRole() == null) {
                user.setRole("user");
            }


            String hashedPassword = BCrypt.hashpw(user.getPasswordHash(), BCrypt.gensalt(12));
            user.setPasswordHash(hashedPassword);

            userDAO.createUser(user);
            ctx.status(201).result("User registered successfully");
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }

    public void login(Context ctx) {
        try {
            User user = ctx.bodyAsClass(User.class);
            User storedUser = userDAO.getUserByUsername(user.getUsername());

            if (storedUser == null || !BCrypt.checkpw(user.getPasswordHash(), storedUser.getPasswordHash())) {
                ctx.status(401).result("Invalid username or password");
                return;
            }

            ctx.sessionAttribute("user", storedUser);
            ctx.status(200).result("Login successful");
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }

    public void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.status(200).result("Logged out successfully");
    }
}

