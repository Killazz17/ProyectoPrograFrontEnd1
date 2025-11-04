package Presentation.Controllers;

import Domain.Dtos.LoginRequestDto;
import Domain.Dtos.LoginResponseDto;
import Presentation.Observable;
import Presentation.Views.LoginView;
import Services.AuthService;
import Utilities.EventType;

public class LoginController extends Observable {
    private final AuthService authService;
    private final LoginView loginView;

    public LoginController(LoginView loginView, AuthService authService) {
        this.loginView = loginView;
        this.authService = authService;
    }

    public LoginResponseDto loginByNombre(String nombreUsuario, String clave) {
        try {
            LoginResponseDto response = authService.loginByNombre(nombreUsuario, clave);

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

    public LoginResponseDto login(int id, String password) {
        try {
            LoginRequestDto request = new LoginRequestDto(id, password);
            LoginResponseDto response = authService.login(request);

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