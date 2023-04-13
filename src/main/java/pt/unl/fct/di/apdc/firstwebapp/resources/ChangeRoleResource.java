package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
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
        Transaction txn = datastore.newTransaction();

        try {
            Entity userToken = txn.get(tokenKey);
            if (System.currentTimeMillis() > userToken.getLong("token_expireDate")) {
                LOG.warning("Login has expired. Login again ");
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            Entity user = txn.get(userKey);
            Entity targetUser = txn.get(targetKey);
            if (targetUser == null || user == null) {
                LOG.warning("One of the users doesn't exist.");
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            if (!UserRole.canChangeRole(user, targetUser)) {
                LOG.warning("User has no permissions to change target role.");
                return Response.status(Response.Status.FORBIDDEN).build();
            } else {
                targetUser = Entity.newBuilder(targetKey, targetUser)
                        .set("user_role", data.newRole)
                        .build();
                txn.update(targetUser);
                txn.commit();
                LOG.info("Role changed successfully for user: " + data.targetUsername);
                return Response.ok().build();
            }
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








