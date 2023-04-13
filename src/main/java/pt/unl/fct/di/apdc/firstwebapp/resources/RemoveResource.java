package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import pt.unl.fct.di.apdc.firstwebapp.util.LoginData;
import pt.unl.fct.di.apdc.firstwebapp.util.RemoveData;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/remove")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // produz sempre dados em json
public class RemoveResource {
    /**
     * Logger Object
     */
    private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

    private final Gson g = new Gson();

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    private final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");

    private final KeyFactory tokenKeyFactory = datastore.newKeyFactory().setKind("AuthTokens");




    public RemoveResource() {
    }

    @DELETE
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeUser( RemoveData data){
        // verifies if the user data is existent and valid
        Key userKey = userKeyFactory.newKey(data.currUser);
        Key targetKey = userKeyFactory.newKey(data.targetUser);
        Key tokenKey = tokenKeyFactory.newKey(data.currUser);

        Transaction txn = datastore.newTransaction();

        try{
            Entity userToken = txn.get(tokenKey);
            if (System.currentTimeMillis() > userToken.getLong("token_expireDate")) {
                LOG.warning("Login has expired. Login again ");
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            Entity user = txn.get(userKey);
            Entity userToDelete = txn.get(targetKey);
            if(userToDelete == null || user == null){
                LOG.warning("One of the users doesn't exist.");
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            if(!UserRole.canRemoveUser(user,userToDelete)){
                LOG.warning("Not enough permissions to remove user.");
                return Response.status(Response.Status.FORBIDDEN).build();
            }else {
                txn.delete(targetKey);
                txn.delete(tokenKey);
                txn.commit();
                return Response.status(Response.Status.OK).entity("User " + data.targetUser + " removed.").build();
            }
        } catch(Exception e){
            txn.rollback();
            LOG.severe(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Exception").build();
        }


    }

}
