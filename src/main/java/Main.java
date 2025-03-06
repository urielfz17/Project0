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

        // Inicialización de Javalin
        Javalin app = Javalin.create(config -> {
                    config.jsonMapper(new JavalinJackson());
                }
        );

        // Crear instancias de DAO
        UserDAO userDAO = new UserDAO();
        LoanDAO loanDAO = new LoanDAO();

        // Crear instancias de Service
        AuthService authService = new AuthService(userDAO);
        UserService userService = new UserService(userDAO);
        LoanService loanService = new LoanService(loanDAO);

        // Crear instancias de Controller
        AuthController authController = new AuthController(authService);
        UserController userController = new UserController(userService);
        LoanController loanController = new LoanController(loanService);

        // Registrar rutas en Javalin
        authController.registerRoutes(app);
        userController.registerRoutes(app);
        loanController.registerRoutes(app);

        // Iniciar el servidor en el puerto 7000
        app.start(7070);

        LoggerUtil.LOGGER.info("Server started on http://localhost:7000");
    }
}
