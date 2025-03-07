package br.com.servicos_auto.models;

public class LoginResponse {

    private String token;

    // Construtor
    public LoginResponse(String token) {
        this.token = token;
    }

    // Getter
    public String getToken() {
        return token;
    }

    // Setter
    public void setToken(String token) {
        this.token = token;
    }

}
