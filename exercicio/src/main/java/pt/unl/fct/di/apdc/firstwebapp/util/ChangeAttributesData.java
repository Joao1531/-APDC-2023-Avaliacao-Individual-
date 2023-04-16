package pt.unl.fct.di.apdc.firstwebapp.util;


public class ChangeAttributesData {

    // Mandatory User Attributes

    public String username;

    public String targetUser;

    public String email;

    public String name;


    // Optional User Attributes - Regarding  profile.

    public String phoneNum;
    public String job;
    public String NIF;
    public String workAddress;
    public AuthToken token;

    public ChangeAttributesData() {
    }

    public ChangeAttributesData( String targetUser, String email, String name, String phoneNum, String NIF, String job, String workAddress, AuthToken token) {
        this.targetUser = targetUser;
        this.email = email;
        this.name = name;
        this.phoneNum = phoneNum;
        this.NIF = NIF;
        this.job = job;
        this.workAddress = workAddress;
        this.token = token;
    }
}
