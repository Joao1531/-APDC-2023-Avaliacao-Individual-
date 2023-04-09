package pt.unl.fct.di.apdc.firstwebapp.util;

public class RegisterData {

    private static final String USER = "user";
    private static final String GBO = "gbo";
    private static final String GA = "ga";

    private static final String GS = "gs";
    private static final String SU = "su";

    private static final String ACTIVE = "active";

    private static final String INACTIVE = "inactive";


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


    public RegisterData() {
    }
    // in case the user doesn't want to customize its profile yet.
    public RegisterData(String username, String password, String confirmation, String email, String name) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.confirmation = confirmation;
        this.role = USER;
        this.state = INACTIVE;
        this.phoneNum = "";
        this.isPrivate = false;
        this.NIF = "";
        this.workAddress = "";
        this.hasPhoto = false;
    }

    // in case the user wants to add optional attributes to the profile
    public RegisterData(String username, String password, String confirmation, String email, String name, String phoneNum, boolean isPrivate, String NIF, String workAddress) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.confirmation = confirmation;
        this.role = USER;
        this.state = INACTIVE;
        this.phoneNum = phoneNum;
        this.isPrivate = isPrivate;
        this.NIF = NIF;
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


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmation() {
        return confirmation;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getState() {
        return state;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getJob() {
        return job;
    }

    public String getNIF() {
        return NIF;
    }

    public boolean hasPhoto() {
        return hasPhoto;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    public void setPhone(String phone) {
        this.phoneNum = phone;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public void setNIF(String NIF) {
        this.NIF = NIF;
    }

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

    public void setRole(String newRole) {
        if (newRole.equals(USER) || newRole.equals(SU) || newRole.equals(GBO) || newRole.equals(GA) || newRole.equals(GS)) {
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
}
