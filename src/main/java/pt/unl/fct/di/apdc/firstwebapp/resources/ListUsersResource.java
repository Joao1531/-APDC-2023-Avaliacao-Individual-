package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.cloud.storage.Acl;
import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;
import pt.unl.fct.di.apdc.firstwebapp.util.UserData;

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
        List<UserData> usersInfo = new ArrayList<UserData>();

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

    private void getUserInfo(Entity user, List<UserData> usersInfo) {

        switch (user.getString("user_role")) {
            case "SU":
                usersInfo.add(getUserData(user,""));
                break;
            case "GS":
                if (user.getString("user_role").equals(UserRole.GBO) || user.getString("user_role").equals(UserRole.USER))
                    usersInfo.add(getUserData(user,""));
                break;
            case "GBO":
                if (user.getString("user_role").equals(UserRole.USER))
                    usersInfo.add(getUserData(user,""));
                break;
            case "USER":
                if (user.getString("user_role").equals(UserRole.USER) && user.getString("user_state").equals(UserState.ACTIVE) && user.getBoolean("user_profile_status"))
                    usersInfo.add(getUserData(user,"user"));
                break;
        }
    }
    private UserData getUserData(Entity user,String flag){
        if(!flag.equals("")){
            return new UserData(user.getString("user_id"),user.getString("user_email"),user.getString("user_name"));
        }else{
            LOG.severe("AQUI");
            return new UserData(user.getString("user_id"),user.getString("user_pwd"),user.getString("user_email"),user.getString("user_name"), user.getString("user_phoneNum"), user.getString("user_role"),user.getString("user_state"),user.getBoolean("user_profile_status"),user.getString("user_NIF"),user.getString("user_job"),user.getString("user_workAddr"));
        }

    }

}
