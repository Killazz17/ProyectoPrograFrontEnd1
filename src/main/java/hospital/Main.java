package hospital;

import Presentation.Controllers.LoginController;
import Presentation.Views.LoginView;
import Services.AuthService;

import javax.swing.*;

/**
 * Punto de entrada principal de la aplicación del Hospital.
 * Inicia la interfaz gráfica con la pantalla de login.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            AuthService authService = new AuthService("localhost", 7070);
            LoginController loginController = new LoginController(loginView, authService);
            loginController.addObserver(loginView);
            loginView.setController(loginController);

            loginView.setVisible(true);
        });
    }
}