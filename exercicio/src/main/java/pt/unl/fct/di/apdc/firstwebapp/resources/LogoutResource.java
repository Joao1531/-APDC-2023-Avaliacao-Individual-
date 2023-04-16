package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.LogoutData;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/logout")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // produz sempre dados em json
public class LogoutResource {
    private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

    private final Gson g = new Gson();

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");
    private final KeyFactory tokenKeyFactory = datastore.newKeyFactory().setKind("AuthTokens");

    public LogoutResource() {
    }

    @DELETE
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // produz sempre dados em json
    public Response logoutUser(LogoutData data) {
        LOG.fine("Attempt to logout user: " + data.username);
        Key userKey = userKeyFactory.newKey(data.username);
        Key tokenKey = tokenKeyFactory.newKey(data.username);


        Entity userToken = datastore.get(tokenKey);
        if (userToken == null) {
            LOG.warning("Token not found.");
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if (!data.token.tokenID.equals(userToken.getString("token_id"))) {
            LOG.warning("Tokens don't match.");
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if (System.currentTimeMillis() > userToken.getLong("token_expireDate")) {
            LOG.warning("Login has already expired.");
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        Entity user = datastore.get(userKey);
        if (user == null) {
            LOG.warning("User doesn't exist.");
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        Transaction txn = datastore.newTransaction();

        try {
            txn.delete(tokenKey);
            txn.commit();
            return Response.status(Response.Status.OK).entity("Token has been revoked and User has been logged out.").build();
        } catch (Exception e) {
            txn.rollback();
            LOG.severe(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Exception").build();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Finally").build();
            }
        }

    }
}
