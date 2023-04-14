package pt.unl.fct.di.apdc.firstwebapp.util;

public class ChangeRoleData {
    public String currUsername;
    public String targetUsername;
    public String newRole;
    public AuthToken token;

    public ChangeRoleData() {
    }

    public ChangeRoleData(String currUsername, String targetUsername, String newRole, AuthToken token) {
        this.currUsername = currUsername;
        this.targetUsername = targetUsername;
        this.newRole = newRole;
        this.token = token;
    }


}
