package pt.unl.fct.di.apdc.firstwebapp.util;

public class ChangePasswordData {
    public String username;
    public String oldPassword;
    public String newPassword;

    public String confirmation;
    public ChangePasswordData() {}

    public ChangePasswordData(String username, String oldPassword, String newPassword,String confirmation) {
        this.username = username;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmation=confirmation;
    }

    public boolean isValid(){
        return newPassword.equals(confirmation);
    }
}
