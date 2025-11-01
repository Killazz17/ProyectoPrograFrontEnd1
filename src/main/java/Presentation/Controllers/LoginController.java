package Presentation.Controllers;

import Domain.Dtos.LoginRequestDto;
import Domain.Dtos.LoginResponseDto;
import Presentation.Observable;
import Presentation.Views.LoginView;
import Services.AuthService;
import Utilities.EventType;

/**
 * Controlador de login compatible con la arquitectura nueva (AuthService + sockets)
 */
public class LoginController extends Observable {
    private final AuthService authService;
    private final LoginView loginView;

    public LoginController(LoginView loginView, AuthService authService) {
        this.loginView = loginView;
        this.authService = authService;
    }

    // Método para hacer login usando ID (int) y contraseña
    public LoginResponseDto login(int id, String password) {
        try {
            LoginRequestDto request = new LoginRequestDto(id, password);
            LoginResponseDto response = authService.login(request);
            
            // Notificar a los observadores sobre el resultado del login
            if (response.isSuccess()) {
                notifyObservers(EventType.CREATED, response);
            } else {
                notifyObservers(EventType.DELETED, response);
            }
            
            return response;
        } catch (Exception e) {
            LoginResponseDto errorResponse = new LoginResponseDto(false, null, null, "Error al conectar con el servidor: " + e.getMessage());
            notifyObservers(EventType.DELETED, errorResponse);
            return errorResponse;
        }
    }
}