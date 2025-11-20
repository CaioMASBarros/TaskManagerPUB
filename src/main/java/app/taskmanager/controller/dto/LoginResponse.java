package app.taskmanager.controller.dto;

public record LoginResponse(String acessToken, Long expiresIn) {
}
