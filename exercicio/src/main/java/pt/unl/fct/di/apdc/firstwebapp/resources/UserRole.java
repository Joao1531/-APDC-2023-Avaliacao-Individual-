package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.datastore.Entity;

public enum UserRole {
    SU,
    GS,
    GA,
    GBO,
    USER;

    public static boolean canChangeRole(Entity user, Entity targetUser) {
        switch (user.getString("user_role")) {
            case "SU":
                return true;
            case "GS":
                if (targetUser.getString("user_role").equals(GBO.toString()) || targetUser.getString("user_role").equals(USER.toString()))
                    return true;
                break;

        }
        return false;
    }

    public static boolean canRemoveUser(Entity user, Entity targetUser) {
        switch (user.getString("user_role")) {
            case "SU":
                return true;
            case "GS":
                if (targetUser.getString("user_role").equals(GBO.toString()) || targetUser.getString("user_role").equals(GA.toString())||targetUser.getString("user_role").equals(USER.toString()))
                    return true;
                break;
            case "GA":
                if (targetUser.getString("user_role").equals(GBO.toString()) || targetUser.getString("user_role").equals(USER.toString()))
                    return true;
                break;
            case "GBO":
                if (targetUser.getString("user_role").equals(USER.toString()))
                    return true;
                break;
            case "USER":
                if (user.equals(targetUser))
                    return true;
                break;

        }
        return false;
    }

    public static boolean canModifyUser(Entity user, Entity targetUser) {
        switch (user.getString("user_role")) {
            case "SU":
                return true;
            case "GS":
                if (targetUser.getString("user_role").equals(GBO.toString()) || targetUser.getString("user_role").equals(USER.toString()))
                    return true;
                break;
            case "GBO":
                if (targetUser.getString("user_role").equals(USER.toString()))
                    return true;
                break;
            case "USER":
                if (user.equals(targetUser))
                    return true;
                break;
            default:
                break;

        }
        return false;
    }

}
