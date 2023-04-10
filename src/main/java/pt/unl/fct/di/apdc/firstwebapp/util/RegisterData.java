package pt.unl.fct.di.apdc.firstwebapp.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class RegisterData {

    public enum Role {
        SU ("SU") ,
        GS ("GS"),
        GA ("GA"),
        GBO ("GBO"),
        USER ("USER");

        private final String role;

        private Role(String role){
            this.role = role;
        }
        public String getRole(){
            return role;
        }



    }


    //private static final String ACTIVE = "ACTIVE";

    private static final String INACTIVE = "INACTIVE";
    NetworkInterface network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());


    // Mandatory User Attributes

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
        this.role = "";
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

    private String getMacAddress() throws UnknownHostException, SocketException {
        String macAddress;
        NetworkInterface network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
        byte[] mac = network.getHardwareAddress();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }
        macAddress = sb.toString();
        return macAddress;
    }

    public Role getRole(Role role) throws SocketException, UnknownHostException {
        if (username.equals(getMacAddress())) {
            role = Role.SU;
        } else {
            role = Role.GBO;
        }
        return role;
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
