package pt.unl.fct.di.apdc.firstwebapp.util;

import pt.unl.fct.di.apdc.firstwebapp.resources.UserRole;
import pt.unl.fct.di.apdc.firstwebapp.resources.UserState;

public class UserData {
    public String username;
    public String password;

    public String confirmation;
    public String email;

    public String name;
    public String role;


    public String state;

    // Optional User Attributes - Regarding  profile.

    public boolean isPrivate;
    public String phoneNum;
    public String job;
    public String NIF;
    public String workAddress;
    public boolean hasPhoto;

    public UserData(String username,String password, String email, String name, String phoneNum,String role,String state, boolean isPrivate, String NIF, String job, String workAddress){
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.role = role;
        this.state = state;
        this.phoneNum = phoneNum;
        this.isPrivate = isPrivate;
        this.NIF = NIF;
        this.job = job;
        this.workAddress = workAddress;

    }
    public UserData(String username,String email,String name){
        this.username = username;
        this.email = email;
        this.name = name;
    }
}
