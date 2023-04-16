package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.ChangeRoleData;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/changeRole")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // produz sempre dados em json
public class ChangeRoleResource {

    private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

    private final Gson g = new Gson();

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");
    private final KeyFactory tokenKeyFactory = datastore.newKeyFactory().setKind("AuthTokens");

    public ChangeRoleResource() {
    }

    @PUT
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // produz sempre dados em json
    public Response changeRole(ChangeRoleData data) {
        Key userKey = userKeyFactory.newKey(data.currUsername);
        Key targetKey = userKeyFactory.newKey(data.targetUsername);
        Key tokenKey = tokenKeyFactory.newKey(data.currUsername);

        Entity userToken = datastore.get(tokenKey);
        if (userToken == null) {
            LOG.warning("Token not found. Please login.");
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if (!data.token.tokenID.equals(userToken.getString("token_id"))) {
            LOG.warning("Tokens don't match.");
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if (System.currentTimeMillis() > userToken.getLong("token_expireDate")) {
            LOG.warning("Login has expired. Login again ");
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        Entity user = datastore.get(userKey);
        Entity targetUser = datastore.get(targetKey);
        if (targetUser == null || user == null) {
            LOG.warning("One of the users doesn't exist.");
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if (user.getString("user_state").equals(UserState.INACTIVE.toString())) {
            LOG.warning("User is not active.");
            return Response.status(Response.Status.FORBIDDEN).entity("User inativo").build();

        }

        if (!UserRole.canChangeRole(user, targetUser)) {
            LOG.warning("User has no permissions to change target role.");
            return Response.status(Response.Status.FORBIDDEN).build();
        } else {
            Transaction txn = datastore.newTransaction();

            try {
                targetUser = Entity.newBuilder(targetKey, targetUser)
                        .set("user_role", data.newRole)
                        .build();
                txn.update(targetUser);
                txn.commit();
                LOG.info("Role changed successfully for user: " + data.targetUsername);
                return Response.ok().build();

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
}








