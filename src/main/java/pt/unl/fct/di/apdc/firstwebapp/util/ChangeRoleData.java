package pt.unl.fct.di.apdc.firstwebapp.util;

public class ChangeRoleData {
        public String currUsername;
        public String targetUsername;
        public String newRole;

        public ChangeRoleData() {}

        public ChangeRoleData(String currUsername, String targetUsername, String newRole) {
            this.currUsername = currUsername;
            this.targetUsername = targetUsername;
            this.newRole = newRole;
        }



}
