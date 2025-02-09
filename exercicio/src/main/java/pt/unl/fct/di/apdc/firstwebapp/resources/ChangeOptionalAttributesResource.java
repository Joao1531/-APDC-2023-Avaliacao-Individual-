package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.ChangeAttributesData;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/changeattributes")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // produz sempre dados em json
public class ChangeOptionalAttributesResource {
    private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

    private final Gson g = new Gson();

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");
    private final KeyFactory tokenKeyFactory = datastore.newKeyFactory().setKind("AuthTokens");

    public ChangeOptionalAttributesResource() {
    }

    @PUT
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // produz sempre dados em json
    public Response changeAttributes(ChangeAttributesData data) {
        Key userKey = userKeyFactory.newKey(data.token.username);
        Key targetKey = userKeyFactory.newKey(data.targetUser);
        Key tokenKey = tokenKeyFactory.newKey(data.token.username);

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

        if (!UserRole.canModifyUser(user, targetUser)) {
            LOG.warning("Not enough permissions to modify user.");
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        Transaction txn = datastore.newTransaction();
        try {
            Entity updatedUser = Entity.newBuilder(targetKey, targetUser)
                    .set("user_name", getAtribute(targetUser.getString("user_name"), data.name))
                    .set("user_email", getAtribute(targetUser.getString("user_email"), data.email))
                    .set("user_phoneNum", getAtribute(targetUser.getString("user_phoneNum"), data.phoneNum))
                    .set("user_NIF", getAtribute(targetUser.getString("user_NIF"), data.NIF))
                    .set("user_job", getAtribute(targetUser.getString("user_job"), data.job))
                    .set("user_workAddr", getAtribute(targetUser.getString("user_workAddr"), data.workAddress))
                    .build();
            LOG.severe("SUI " + updatedUser.getString("user_email") +" ..");
            txn.update(updatedUser);
            txn.commit();
            return Response.status(Response.Status.OK).entity("User's attributes modified.").build();
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

    private String getAtribute(String userAttribute, String newAttribute) {
        if (newAttribute.equals("") || newAttribute == null)
            return userAttribute;
        else
            return newAttribute;
    }



}
