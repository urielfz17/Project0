package service;

import dao.UserDAO;
import dto.UserRequest;
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
        User sessionUser = ctx.sessionAttribute("user");

        if (sessionUser == null) {
            ctx.status(401).result("Unauthorized: Please log in");
            return;
        }

        try {
            // 1️⃣ Leer JSON desde el body y convertirlo en UserRequest
            UserRequest userRequest = ctx.bodyAsClass(UserRequest.class);

            // 2️⃣ Obtener usuario desde la base de datos
            User user = userDAO.getUserById(sessionUser.getIdUser());
            if (user == null) {
                ctx.status(404).result("User not found");
                return;
            }

            // 3️⃣ Actualizar solo los campos enviados en el JSON
            if (userRequest.getUsername() != null && !userRequest.getUsername().trim().isEmpty()) {
                user.setUsername(userRequest.getUsername());
            }

            if (userRequest.getPassword() != null && userRequest.getPassword().length() >= 6) {
                user.setPasswordHash(userRequest.getPassword()); // 🔹 Se puede aplicar hashing aquí
            } else if (userRequest.getPassword() != null) {
                ctx.status(400).result("Password must be at least 6 characters long");
                return;
            }

            // 4️⃣ Si se envía un nuevo `role`, solo los managers pueden modificarlo
            if (userRequest.getRole() != null) {
                if (!"manager".equals(sessionUser.getRole())) {
                    ctx.status(403).result("Forbidden: Only managers can change roles");
                    return;
                }
                user.setRole(userRequest.getRole());
            }

            // 5️⃣ Guardar cambios en la base de datos
            userDAO.updateUser(user);
            ctx.status(200).result("User updated successfully");
        } catch (Exception e) {
            ctx.status(400).result("Invalid request format: " + e.getMessage());
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

