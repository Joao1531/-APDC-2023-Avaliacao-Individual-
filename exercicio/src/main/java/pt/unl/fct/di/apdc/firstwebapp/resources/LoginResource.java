package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.HttpHeaders;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;


import org.apache.commons.codec.digest.DigestUtils;
import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.LoginData;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // produz sempre dados em json
public class LoginResource {

    /**
     * Logger Object
     */
    private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

    private final Gson g = new Gson();

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");
    private final KeyFactory tokenKeyFactory = datastore.newKeyFactory().setKind("AuthTokens");


    public LoginResource() {
    } // Nothing to be done here

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // produz sempre dados em json
    public Response doLogin(LoginData data, @Context HttpServletRequest request, @Context HttpHeaders headers) {
        LOG.fine("Attempt to login user: " + data.username);

        // KEYS SHOULD BE GENERATED OUTSIDE TRANSACTIONS
        // Construct the key from the username

        Key userKey = userKeyFactory.newKey(data.username);
        Key ctrsKey = datastore.newKeyFactory().addAncestors(PathElement.of("User", data.username)).setKind("UserStats").newKey(data.username);
        // Generate automatically a key
        Key logKey = datastore.allocateId(datastore.newKeyFactory().addAncestors(PathElement.of("User", data.username)).setKind("UserLog").newKey());
        Key tokenKey = tokenKeyFactory.newKey(data.username);


        Entity user = datastore.get(userKey);

        if (user == null) {
            //Username does not exist
            LOG.warning("Failed login attempt for username: " + data.username);
            return Response.status(Status.FORBIDDEN).build();
        }

        // We get the user stats from the storage
        Entity stats = datastore.get(ctrsKey);
        if (stats == null) {
            stats = Entity.newBuilder(ctrsKey)
                    .set("user_stats_logins", 0L)
                    .set("user_stats_failed", 0L)
                    .set("user_first_login", Timestamp.now())
                    .set("user_last_login", Timestamp.now())
                    .build();
        }
        String hashedPWD = (String) user.getString("user_pwd");

        Transaction txn = datastore.newTransaction();

        try {
            if (hashedPWD.equals(DigestUtils.sha512Hex(data.password))) {
                AuthToken token = new AuthToken(data.username);
                Entity tokens = txn.get(tokenKey);
                if (tokens == null || System.currentTimeMillis() > tokens.getLong("token_expireDate")) {

                    tokens = Entity.newBuilder(tokenKey)
                            .set("token_id", token.tokenID)
                            .set("token_expireDate", token.expirationData)
                            .set("token_creationDate", token.creationData)
                            .build();
                    txn.put(tokens);
                    txn.commit();
                }
                LOG.info("User '" + data.username + "' logged in successfully with the token: " + token.tokenID + " .");
                return Response.ok(g.toJson(token)).build();
            } else {
                // Incorrect password
                // Copying here is even worse. Propose a better solution!
                copyStats(txn, stats, null, ctrsKey);
                LOG.warning("Wrong password for username: " + data.username);
                txn.commit();
                return Response.status(Status.FORBIDDEN).build();
            }

        } catch (Exception e) {
            txn.rollback();
            LOG.severe(e.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Exception").build();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Finally").build();
            }
        }
    }

    private void copyStats(Transaction txn, Entity stats, Entity log, Key ctrsKey) {
        Entity ustats = Entity.newBuilder(ctrsKey)
                .set("user_stats_logins", 1L + stats.getLong("user_stats_logins"))
                .set("user_stats_failed", 0L)
                .set("user_first_login", stats.getTimestamp("user_first_login"))
                .set("user_last_login", Timestamp.now())
                .build();
        // Batch operation
        if (log == null)
            txn.put(log, ustats);
        else
            txn.put(ustats);
        LOG.warning("");

    }


}
