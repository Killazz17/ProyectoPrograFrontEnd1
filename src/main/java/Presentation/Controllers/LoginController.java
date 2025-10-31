package Presentation.Controllers;

import Domain.Dtos.ResponseDto;
import Services.AuthService;
import javax.swing.*;

public class LoginController {
    private final AuthService authService;

    public LoginController() {
        this.authService = new AuthService();
    }

    public ResponseDto login(int id, String password) {
        return authService.login(id, password);
    }

    public AuthService.UserSession getCurrentUser() {
        return authService.getCurrentUser();
    }

    public AuthService getAuthService() {
        return authService;
    }
}
