package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;
import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.ChangePasswordData;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;


public class ChangePasswordResource {
    private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

    private final Gson g = new Gson();

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");
    private final KeyFactory tokenKeyFactory = datastore.newKeyFactory().setKind("AuthTokens");

    @PUT
    @Path("/changePassword")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // produz sempre dados em json
    public Response changePassword(ChangePasswordData data) {
        LOG.fine("Attempt to change password for user: " + data.username);

        Key userKey = userKeyFactory.newKey(data.username);
        Key tokenKey = tokenKeyFactory.newKey(data.username);


        Transaction txn = datastore.newTransaction();

        try {
            Entity user = txn.get(userKey);
            Entity userToken = txn.get(tokenKey);

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
            if (user == null) {
                //Username does not exist
                LOG.warning("Failed login attempt for username: " + data.username);
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            if (user.getString("user_state").equals(UserState.INACTIVE.toString())) {
                LOG.warning("User is not active.");
                return Response.status(Response.Status.FORBIDDEN).entity("User inativo").build();

            }

            String hashedNewPwd = DigestUtils.sha512Hex(data.newPassword);
            if (hashedNewPwd.equals(DigestUtils.sha512Hex(user.getString("user_pwd")))) {
                // Incorrect old password
                LOG.warning("The passwords are the same:");
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            if (!data.isValid()) {
                LOG.warning("The passwords don't match.");
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            user = Entity.newBuilder(userKey, user)
                    .set("user_pwd", hashedNewPwd)
                    .build();
            txn.update(user);
            txn.commit();
            LOG.info("Password changed successfully for user: " + data.username);
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
