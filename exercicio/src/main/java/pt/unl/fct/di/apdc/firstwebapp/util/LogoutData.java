package pt.unl.fct.di.apdc.firstwebapp.util;

public class LogoutData {
    public String username;

    public AuthToken token;

    public LogoutData() {
    }

    public LogoutData(String username, AuthToken token) {
        this.username = username;
        this.token = token;

    }
}
