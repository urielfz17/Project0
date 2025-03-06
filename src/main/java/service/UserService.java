package service;

import dao.UserDAO;
import model.User;
import io.javalin.http.Context;

import java.sql.SQLException;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void getUser(Context ctx) {
        // Obtener el ID del usuario desde la URL
        String idParam = ctx.pathParam("id_user");
        int idUser;

        try {
            idUser = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid user ID format");
            return;
        }

        try {
            User user = userDAO.getUserById(idUser);
            if (user == null) {
                ctx.status(404).result("User not found");
                return;
            }
            ctx.status(200).json(user);
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }

    public void updateUser(Context ctx) {
        String idParam = ctx.pathParam("id_user");
        int idUser;

        try {
            idUser = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid user ID format");
            return;
        }

        String newUsername = ctx.formParam("username");
        String newPassword = ctx.formParam("password");

        if (newUsername == null || newPassword == null || newPassword.length() < 6) {
            ctx.status(400).result("Invalid data: username and password required (password min 6 chars)");
            return;
        }

        try {
            User user = userDAO.getUserById(idUser);
            if (user == null) {
                ctx.status(404).result("User not found");
                return;
            }

            // Actualizar los datos del usuario
            user.setUsername(newUsername);
            user.setPasswordHash(newPassword);
            userDAO.updateUser(user);

            ctx.status(200).result("User updated successfully");
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }

    public void deleteUser(Context ctx) {
        String idParam = ctx.pathParam("id_user");
        int idUser;

        try {
            idUser = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid user ID format");
            return;
        }

        try {
            boolean deleted = userDAO.deleteUser(idUser);
            if (deleted) {
                ctx.status(200).result("User deleted successfully");
            } else {
                ctx.status(404).result("User not found");
            }
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }
}

