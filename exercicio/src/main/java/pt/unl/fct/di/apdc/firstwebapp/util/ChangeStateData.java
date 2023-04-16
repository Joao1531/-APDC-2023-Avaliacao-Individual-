package pt.unl.fct.di.apdc.firstwebapp.util;

public class ChangeStateData {
    public String currUsername;
    public String targetUsername;

    public AuthToken token;

    public ChangeStateData() {
    }

    public ChangeStateData(String currUsername, String targetUsername, AuthToken token) {
        this.currUsername = currUsername;
        this.targetUsername = targetUsername;
        this.token = token;
    }


}
