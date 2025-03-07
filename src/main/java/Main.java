import controller.AuthController;
import controller.LoanController;
import controller.UserController;
import dao.LoanDAO;
import dao.UserDAO;
import io.javalin.json.JavalinJackson;
import service.AuthService;
import service.LoanService;
import service.UserService;
import util.LoggerUtil;
import io.javalin.Javalin;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Main {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT); //

        LoggerUtil.LOGGER.info("Starting Loan Management System...");

        Javalin app = Javalin.create(config -> {
                    config.jsonMapper(new JavalinJackson());
                }
        );

        UserDAO userDAO = new UserDAO();
        LoanDAO loanDAO = new LoanDAO();

        AuthService authService = new AuthService(userDAO);
        UserService userService = new UserService(userDAO);
        LoanService loanService = new LoanService(loanDAO);

        AuthController authController = new AuthController(authService);
        UserController userController = new UserController(userService);
        LoanController loanController = new LoanController(loanService);

        authController.registerRoutes(app);
        userController.registerRoutes(app);
        loanController.registerRoutes(app);

        app.start(7000);

        LoggerUtil.LOGGER.info("Server started on http://localhost:7000");
    }
}
