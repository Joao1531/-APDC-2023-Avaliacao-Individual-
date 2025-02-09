package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.UserData;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Path("/searchUser")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // produz sempre dados em json
public class SearchUserResource {
    /**
     * Logger Object
     */
    private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

    private final Gson g = new Gson();

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");
    private final KeyFactory tokenKeyFactory = datastore.newKeyFactory().setKind("AuthTokens");


    public SearchUserResource() {
    }

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // produz sempre dados em json
    public Response searchUser(@PathParam("username") String username, @QueryParam("authToken") String authToken) {
        AuthToken token = g.fromJson(authToken, AuthToken.class);
        Key userKey = userKeyFactory.newKey(token.username);
        Key tokenKey = tokenKeyFactory.newKey(token.username);


        Entity userToken = datastore.get(tokenKey);
        Entity user = datastore.get(userKey);
        LOG.fine("Attempt to list users of role: " + user.getString("user_role"));
        if (userToken == null) {
            LOG.warning("Token not found.");
            return Response.status(Response.Status.FORBIDDEN).entity("Token nulo").build();
        }
        if (!token.tokenID.equals(userToken.getString("token_id"))) {
            LOG.warning("Tokens don't match.");
            return Response.status(Response.Status.FORBIDDEN).entity("Token nao sao iguais").build();
        }
        if (System.currentTimeMillis() > userToken.getLong("token_expireDate")) {
            LOG.warning("Login has already expired.");
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if (user == null) {
            LOG.warning("User doesn't exist.");
            return Response.status(Response.Status.FORBIDDEN).entity("User nulo").build();
        }
        if (user.getString("user_state").equals(UserState.INACTIVE.toString())) {
            LOG.warning("User is not active.");
            return Response.status(Response.Status.FORBIDDEN).entity("User nulo").build();
        }

        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind("User")
                .setFilter(StructuredQuery.PropertyFilter.eq("user_id", username))
                .build();
        QueryResults<Entity> users = datastore.run(query);
        List<UserData> usersInfo = new ArrayList<UserData>();
        if (!users.hasNext()) {
            return Response.status(Response.Status.FORBIDDEN).entity("No users found. ").build();
        } else {
            Entity currUser = users.next();
            usersInfo.add(getUserData(currUser, ""));
            //getUser(currUser, usersInfo);
            LOG.severe(String.valueOf(usersInfo.size()));
            return Response.status(Response.Status.OK).entity(g.toJson(usersInfo)).build();
        }

    }

    private void getUser(Entity currUser, List<UserData> usersInfo) {

        switch (currUser.getString("user_role")) {
            case "SU":
                usersInfo.add(getUserData(currUser, ""));
                break;
            case "GS":
                usersInfo.add(getUserData(currUser, ""));
                break;
            case "GBO":
                usersInfo.add(getUserData(currUser, ""));
                break;
            case "USER":
                usersInfo.add(getUserData(currUser, "user"));
                break;
        }
    }

    private UserData getUserData(Entity user, String flag) {
        if (!flag.equals("")) {
            return new UserData(user.getString("user_id"), user.getString("user_email"), user.getString("user_name"));
        } else {
            return new UserData(user.getString("user_id"), user.getString("user_pwd"), user.getString("user_email"), user.getString("user_name"), user.getString("user_phoneNum"), user.getString("user_role"), user.getString("user_state"), user.getBoolean("user_profile_status"), user.getString("user_NIF"), user.getString("user_job"), user.getString("user_workAddr"));
        }
    }
}
