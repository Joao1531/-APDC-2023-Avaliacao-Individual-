package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/list")
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
        public Response listAllUsers(){
            Query<Entity> query = Query.newEntityQueryBuilder()
                    .setKind("User")
                    .build();
            QueryResults<Entity> users = datastore.run(query);
            if (!users.hasNext()){
                return Response.status(Response.Status.FORBIDDEN).entity("No users found. ").build();
            }else{

                while(users.hasNext()){
                    Entity user = users.next();
                    System.out.println(user.getString("user_id")+"; " + user.getString("user_email")+"; "+user.getString("user_name"));
                }
                return Response.status(Response.Status.OK).entity("Users Listed. ").build();
            }
        }

    }
