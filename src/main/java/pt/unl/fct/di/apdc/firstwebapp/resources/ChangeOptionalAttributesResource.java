package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
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
        Key userKey = userKeyFactory.newKey(data.username);
        Key targetKey = userKeyFactory.newKey(data.targetUser);
        Key tokenKey = tokenKeyFactory.newKey(data.username);
        Transaction txn = datastore.newTransaction();
        try {
            LOG.severe("QUIE22");

            Entity userToken = txn.get(tokenKey);
            LOG.severe("QUIE33 ");

            if (userToken == null) {
                LOG.warning("Token not found. Please login.");
                return Response.status(Response.Status.FORBIDDEN).build();
            }

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
            LOG.severe("QUIE0");

            if (!UserRole.canModifyUser(user, targetUser)) {
                LOG.warning("Not enough permissions to modify user.");
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            LOG.severe("QUIE1");
            txn.update(updateUser(user, targetUser, targetKey, data));
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

    private Entity updateUser(Entity user, Entity targetUser, Key targetKey, ChangeAttributesData data) {
        Entity updatedUser = null;
        switch (user.getString("user_role")) {
            case "SU":
                updatedUser = Entity.newBuilder(targetKey, targetUser)
                        .set("user_name", getAtribute(targetUser.getString("user_name"), data.name))
                        .set("user_email", getAtribute(targetUser.getString("user_email"), data.email))
                        .set("user_phoneNum", getAtribute(targetUser.getString("user_phoneNum"), data.phoneNum))
                        .set("user_NIF", getAtribute(targetUser.getString("user_NIF"), data.NIF))
                        .set("user_job", getAtribute(targetUser.getString("user_job"), data.job))
                        .set("user_workAddr", getAtribute(targetUser.getString("user_workAddr"), data.workAddress))
                        .build();
            case "GS":
                if (targetUser.getString("user_role").equals(UserRole.GBO.toString()) || targetUser.getString("user_role").equals(UserRole.USER.toString()))
                    updatedUser = Entity.newBuilder(targetKey, targetUser)
                            .set("user_name", getAtribute(targetUser.getString("user_name"), data.name))
                            .set("user_email", getAtribute(targetUser.getString("user_email"), data.email))
                            .set("user_phoneNum", getAtribute(targetUser.getString("user_phoneNum"), data.phoneNum))
                            .set("user_NIF", getAtribute(targetUser.getString("user_NIF"), data.NIF))
                            .set("user_job", getAtribute(targetUser.getString("user_job"), data.job))
                            .set("user_workAddr", getAtribute(targetUser.getString("user_workAddr"), data.workAddress))
                            .build();
                break;
            case "GBO":
                if (targetUser.getString("user_role").equals(UserRole.USER.toString()))
                    updatedUser = Entity.newBuilder(targetKey, targetUser)
                            .set("user_name", getAtribute(targetUser.getString("user_name"), data.name))
                            .set("user_email", getAtribute(targetUser.getString("user_email"), data.email))
                            .set("user_phoneNum", getAtribute(targetUser.getString("user_phoneNum"), data.phoneNum))
                            .set("user_NIF", getAtribute(targetUser.getString("user_NIF"), data.NIF))
                            .set("user_job", getAtribute(targetUser.getString("user_job"), data.job))
                            .set("user_workAddr", getAtribute(targetUser.getString("user_workAddr"), data.workAddress))
                            .build();
                break;
            case "USER":
                if (targetUser.equals(UserRole.USER.toString()))
                    updatedUser = Entity.newBuilder(targetKey, targetUser)
                            .set("user_phoneNum", getAtribute(targetUser.getString("user_phoneNum"), data.phoneNum))
                            .set("user_NIF", getAtribute(targetUser.getString("user_NIF"), data.NIF))
                            .set("user_job", getAtribute(targetUser.getString("user_job"), data.job))
                            .set("user_workAddr", getAtribute(targetUser.getString("user_workAddr"), data.workAddress))
                            .build();
                break;
            default:
                break;

        }
        return updatedUser;
    }


}
