package Presentation.Controllers;

import Domain.Dtos.LoginRequestDto;
import Domain.Dtos.LoginResponseDto;
import Services.AuthService;

/**
 * AuthController actúa como intermediario entre la vista (LoginView)
 * y el servicio AuthService que se comunica con el backend.
 */
public class AuthController {
    private final AuthService authService;

    public AuthController() {
        this.authService = new AuthService();
    }

    public LoginResponseDto login(int id, String password) {
        try {
            LoginRequestDto request = new LoginRequestDto(id, password);
            return authService.login(request);
        } catch (Exception e) {
            return new LoginResponseDto(false, null, null, "Error al conectar con el servidor: " + e.getMessage());
        }
    }
}