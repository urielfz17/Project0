import dao.UserDAO;
import dto.UserRequest;
import model.User;
import io.javalin.http.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import java.sql.SQLException;

import static org.mockito.Mockito.*;

class UserServiceTest {
    private UserService userService;
    private UserDAO userDAO;
    private Context ctx;

    @BeforeEach
    void setUp() {
        userDAO = mock(UserDAO.class);
        userService = new UserService(userDAO);
        ctx = mock(Context.class);

        when(ctx.status(anyInt())).thenReturn(ctx);
    }

    @Test
    void updateUser_UnauthorizedUser_ShouldReturn401() {
        when(ctx.sessionAttribute("user")).thenReturn(null);

        userService.updateUser(ctx);

        verify(ctx).status(401);
        verify(ctx).result("Unauthorized: Please log in");
    }

    @Test
    void updateUser_UserNotFound_ShouldReturn404() throws SQLException {
        User sessionUser = new User(1, "testUser", "hashedPass", "user");
        when(ctx.sessionAttribute("user")).thenReturn(sessionUser);
        when(userDAO.getUserById(1)).thenReturn(null);

        userService.updateUser(ctx);

        verify(ctx).status(404);
        verify(ctx).result("User not found");
    }

    @Test
    void updateUser_InvalidPassword_ShouldReturn400() throws SQLException {
        User sessionUser = new User(1, "testUser", "hashedPass", "user");
        when(ctx.sessionAttribute("user")).thenReturn(sessionUser);
        when(userDAO.getUserById(1)).thenReturn(sessionUser);
        when(ctx.bodyAsClass(UserRequest.class)).thenReturn(new UserRequest("newUser", "123", null));

        userService.updateUser(ctx);

        verify(ctx).status(400);
        verify(ctx).result("Password must be at least 6 characters long");
    }

    @Test
    void updateUser_ValidPassword_ShouldReturn200() throws SQLException {
        User sessionUser = new User(1, "testUser", "hashedPass", "user");
        when(ctx.sessionAttribute("user")).thenReturn(sessionUser);
        when(userDAO.getUserById(1)).thenReturn(sessionUser);
        when(ctx.bodyAsClass(UserRequest.class)).thenReturn(new UserRequest(null, "securePass123", null));

        userService.updateUser(ctx);

        verify(userDAO).updateUser(any(User.class));
        verify(ctx).status(200);
        verify(ctx).result("User updated successfully");
    }

    @Test
    void updateUser_ChangeRoleAsUser_ShouldReturn403() throws SQLException {
        User sessionUser = new User(1, "testUser", "hashedPass", "user");
        when(ctx.sessionAttribute("user")).thenReturn(sessionUser);
        when(userDAO.getUserById(1)).thenReturn(sessionUser);
        when(ctx.bodyAsClass(UserRequest.class)).thenReturn(new UserRequest(null, null, "manager"));

        userService.updateUser(ctx);

        verify(ctx).status(403);
        verify(ctx).result("Forbidden: Only managers can change roles");
    }

    @Test
    void updateUser_ChangeRoleAsManager_ShouldReturn200() throws SQLException {
        User sessionUser = new User(2, "adminUser", "hashedPass", "manager");
        User targetUser = new User(1, "testUser", "hashedPass", "user");
        when(ctx.sessionAttribute("user")).thenReturn(sessionUser);
        when(userDAO.getUserById(2)).thenReturn(sessionUser);
        when(userDAO.getUserById(1)).thenReturn(targetUser);
        when(ctx.bodyAsClass(UserRequest.class)).thenReturn(new UserRequest(null, null, "admin"));

        userService.updateUser(ctx);

        verify(userDAO).updateUser(any(User.class));
        verify(ctx).status(200);
        verify(ctx).result("User updated successfully");
    }

    @Test
    void updateUser_DatabaseError_ShouldReturn400() throws SQLException {
        User sessionUser = new User(1, "testUser", "hashedPass", "user");
        when(ctx.sessionAttribute("user")).thenReturn(sessionUser);
        when(userDAO.getUserById(1)).thenReturn(sessionUser);
        when(ctx.bodyAsClass(UserRequest.class)).thenReturn(new UserRequest("newUser", null, null));
        doThrow(new SQLException("Database error")).when(userDAO).updateUser(any(User.class));

        userService.updateUser(ctx);

        verify(ctx).status(400);
        verify(ctx).result("Invalid request format: Database error");
    }
}

