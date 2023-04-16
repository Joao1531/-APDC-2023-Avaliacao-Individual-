package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import pt.unl.fct.di.apdc.firstwebapp.util.UserData;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Path("/getUser")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // produz sempre dados em json
public class GetUser {
    /**
     * Logger Object
     */
    private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

    private final Gson g = new Gson();

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");
    private final KeyFactory tokenKeyFactory = datastore.newKeyFactory().setKind("AuthTokens");


    public GetUser() {
    }

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // produz sempre dados em json
    public Response getUser(@PathParam("username") String username){
        Key userKey = userKeyFactory.newKey(username);

        Transaction txn = datastore.newTransaction();
        try{
            Entity user = txn.get(userKey);
            return Response.status(Response.Status.OK).entity(g.toJson(getUserData(user))).build();
        }catch (Exception e) {
            txn.rollback();
            LOG.severe(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Exception").build();
        }
    }
    private UserData getUserData(Entity user) {
            return new UserData(user.getString("user_id"), user.getString("user_pwd"), user.getString("user_email"), user.getString("user_name"), user.getString("user_phoneNum"), user.getString("user_role"), user.getString("user_state"), user.getBoolean("user_profile_status"), user.getString("user_NIF"), user.getString("user_job"), user.getString("user_workAddr"));
    }
}
