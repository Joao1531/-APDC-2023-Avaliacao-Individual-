package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Path("/listUsers")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // produz sempre dados em json
public class ListUsersResource {

    /**
     * Logger Object
     */
    private static final Logger LOG = Logger.getLogger(pt.unl.fct.di.apdc.firstwebapp.resources.LoginResource.class.getName());

    private final Gson g = new Gson();

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    private final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");


    public ListUsersResource() {
    }


    @GET
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // produz sempre dados em json
    public Response listAllUsers() {


        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind("User")
                .build();
        QueryResults<Entity> users = datastore.run(query);
        List<Entity> usersInfo = new ArrayList<>();

        if (!users.hasNext()) {
            return Response.status(Response.Status.FORBIDDEN).entity("No users found. ").build();
        } else {
            while (users.hasNext()) {
                Entity user = users.next();
                getUserInfo(user, usersInfo);
            }
            LOG.severe(String.valueOf(usersInfo.size()));
            return Response.status(Response.Status.OK).entity(g.toJson(usersInfo)).build();
        }

    }

    private void getUserInfo(Entity user, List<Entity> usersInfo) {
        Key userKey = datastore.newKeyFactory().setKind("User").newKey(user.getString("user_id"));


        Entity curr_UserRole = Entity.newBuilder(userKey)
                .set("user_id", user.getString("user_id"))
                .set("user_email", user.getString("user_email"))
                .set("user_name", user.getString("user_name"))
                .build();

        switch (user.getString("user_role")) {
            case "SU":
                usersInfo.add(user);
                break;
            case "GS":
                if (user.getString("user_role").equals(UserRole.GBO) || user.getString("user_role").equals(UserRole.USER))
                    usersInfo.add( user);
                break;
            case "GBO":
                if (user.getString("user_role").equals(UserRole.USER))
                    usersInfo.add(user);
                break;
            case "USER":
                if (user.getString("user_role").equals(UserRole.USER) && user.getString("user_state").equals(UserState.ACTIVE) && user.getBoolean("user_profile_status"))
                    usersInfo.add(curr_UserRole);
                break;
        }
    }


}
