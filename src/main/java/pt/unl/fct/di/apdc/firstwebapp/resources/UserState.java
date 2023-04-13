package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.datastore.Entity;

public enum UserState {
    ACTIVE,
    INACTIVE;

    public static boolean canChangeState(Entity user, Entity targetUser) {
        switch (user.getString("user_role")) {
            case "SU":
                return true;
            case "GS":
                if (targetUser.getString("user_role").equals(UserRole.GA) || targetUser.getString("user_role").equals(UserRole.GBO))
                    return true;
                break;
            case "GA":
                if (targetUser.getString("user_role").equals(UserRole.GBO) || targetUser.getString("user_role").equals(UserRole.USER))
                    return true;
                break;
            case "GBO":
                if (targetUser.getString("user_role").equals(UserRole.USER))
                    return true;
                break;
            case "USER":
                if (user.equals(targetUser))
                    return true;
                break;
            default:
                return false;
        }
        return false;
    }
    public static String changeState(Entity targetUser){
        if (targetUser.getString("user_state").equals(UserState.ACTIVE))
            return UserState.INACTIVE.toString();
        else return UserState.ACTIVE.toString();
    }
}
