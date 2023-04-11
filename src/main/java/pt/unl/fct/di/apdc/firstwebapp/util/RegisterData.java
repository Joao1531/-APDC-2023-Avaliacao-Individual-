package pt.unl.fct.di.apdc.firstwebapp.util;

import pt.unl.fct.di.apdc.firstwebapp.resources.RegisterResource;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class RegisterData {




    //private static final String ACTIVE = "ACTIVE";

    private static final String INACTIVE = "INACTIVE";
    NetworkInterface network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());


    // Mandatory User Attributes

    public String username;
    public String password;

    public String confirmation;
    public String email;

    public String name;
    public RegisterResource.Role role;


    public String state;

    // Optional User Attributes - Regarding  profile.

    public boolean isPrivate;
    public String phoneNum;
    public String job;
    public String NIF;
    public String workAddress;
    public boolean hasPhoto;


    public RegisterData() throws SocketException, UnknownHostException {
    }
    // in case the user doesn't want to customize its profile yet.

    /**
     * public RegisterData(String username, String password, String confirmation, String email, String name) throws SocketException, UnknownHostException {
     * this.username = username;
     * this.password = password;
     * this.email = email;
     * this.name = name;
     * this.confirmation = confirmation;
     * this.role = getRole();
     * this.state = INACTIVE;
     * this.phoneNum = "";
     * this.isPrivate = false;
     * this.NIF = "";
     * this.job="";
     * this.workAddress = "";
     * this.hasPhoto = false;
     * }
     **/

    // in case the user wants to add optional attributes to the profile
    public RegisterData(String username, String password, String confirmation, String email, String name, String phoneNum, boolean isPrivate, String NIF, String job, String workAddress) throws SocketException, UnknownHostException {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.confirmation = confirmation;
        this.role = RegisterResource.Role.USER;
        this.state = INACTIVE;
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

    public boolean checkConfirmation() {
        return password.equals(confirmation);
    }

    public boolean validRegistration() {
        return !username.equals("") && !password.equals("") && checkEmail();
    }




    /**
     public void changeProfileStatus(boolean isPrivate) {
     if (isPrivate)
     isPrivate = false;
     else
     isPrivate = true;
     }

     public void setPhoto() {
     if (!hasPhoto)
     hasPhoto = true;
     }

     public void setRole(Role newRole) {
     if (newRole.equals(Role.USER) || newRole.equals(Role.SU) || newRole.equals(Role.GBO) || newRole.equals(Role.GA) || newRole.equals(Role.GS)) {
     role = newRole;
     }
     }

     public void setState(String newState) {
     if (newState.equals(ACTIVE) || newState.equals(INACTIVE)) {
     if (state.equals(ACTIVE))
     state = INACTIVE;
     else
     state = ACTIVE;
     }
     }
     public boolean canDelete(RegisterData targetUser){
     return this.getRole().ordinal() >= targetUser.getRole().ordinal();
     }**/
}
