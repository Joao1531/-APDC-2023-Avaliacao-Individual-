package pt.unl.fct.di.apdc.firstwebapp.util;

import pt.unl.fct.di.apdc.firstwebapp.resources.RegisterResource;
import pt.unl.fct.di.apdc.firstwebapp.resources.UserRole;
import pt.unl.fct.di.apdc.firstwebapp.resources.UserState;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class RegisterData {


    // Mandatory User Attributes

    public String username;
    public String password;

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


    public RegisterData() {
    }

    public RegisterData(String username, String password, String email, String name, String phoneNum, boolean isPrivate, String NIF, String job, String workAddress) throws SocketException, UnknownHostException {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.role = UserRole.USER.toString();
        this.state = UserState.INACTIVE.toString();
        this.phoneNum = phoneNum;
        this.isPrivate = isPrivate;
        this.NIF = NIF;
        this.job = job;
        this.workAddress = workAddress;
        this.hasPhoto = false;
    }


    public boolean checkEmail() {
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    public boolean checkPassword(){
        return password.length()>=4;
    }

    public boolean validRegistration() {
        return !username.equals("") && !password.equals("") && checkEmail();
    }


}
