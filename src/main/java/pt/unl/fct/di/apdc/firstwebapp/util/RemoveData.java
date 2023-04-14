package pt.unl.fct.di.apdc.firstwebapp.util;


public class RemoveData {
    public String currUser;

    public String targetUser;

    public AuthToken token;

    public RemoveData(){}

    public RemoveData(String currUser, String targetUser,AuthToken token){
        this.currUser = currUser;
        this.targetUser= targetUser;
        this.token=token;

    }



}
