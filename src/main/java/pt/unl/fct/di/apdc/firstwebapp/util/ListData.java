package pt.unl.fct.di.apdc.firstwebapp.util;

public class ListData {
    public String username;

    public AuthToken token;

    public ListData() {
    }

    public ListData(String username, AuthToken token) {
        this.username = username;
        this.token = token;

    }
}
