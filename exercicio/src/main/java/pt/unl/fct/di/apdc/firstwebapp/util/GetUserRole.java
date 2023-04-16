package pt.unl.fct.di.apdc.firstwebapp.util;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import pt.unl.fct.di.apdc.firstwebapp.resources.LoginResource;

import java.util.logging.Logger;

public class GetUserRole {

    public String username;
    private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

    private final Gson g = new Gson();

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");
    public GetUserRole(String username){
        this.username=username;
    }

    public String getRole(){
        Key userKey = userKeyFactory.newKey(username);
        Transaction txn = datastore.newTransaction();
        Entity user = txn.get(userKey);
        return user.getString("user_role");
    }


}
