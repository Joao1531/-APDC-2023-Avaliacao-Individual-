package pt.unl.fct.di.apdc.firstwebapp.resources;


import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;

import org.apache.commons.codec.digest.DigestUtils;
import pt.unl.fct.di.apdc.firstwebapp.util.RegisterData;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // produz sempre dados em json
public class RegisterResource {
    /**
     * Logger Object
     */
    private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

    private final Gson g = new Gson();

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();


    public RegisterResource() {

    }


    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doRegistration(RegisterData data) {
        LOG.fine("Attempting to register user " + data.username);
        // Checks input data
        if (!data.validRegistration())
            return Response.status(Response.Status.BAD_REQUEST).entity(" Missing or wrong parameter. ").build();
        if(!data.checkPassword()){
            return Response.status(Response.Status.BAD_REQUEST).entity(" Password too short. ").build();
        }

        Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
        Entity user = datastore.get(userKey);
        if (user != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("User already exists.").build();
        } else {
            Transaction txn = datastore.newTransaction();
            try {
                setupAccount(data);
                user = Entity.newBuilder(userKey)
                        .set("user_id", data.username)
                        .set("user_name", data.name)
                        .set("user_pwd", DigestUtils.sha512Hex(data.password))
                        .set("user_email", data.email)
                        .set("user_phoneNum", data.phoneNum)
                        .set("user_profile_status", data.isPrivate)
                        .set("user_role", data.role)
                        .set("user_state", data.state)
                        .set("user_NIF", data.NIF)
                        .set("user_job", data.job)
                        .set("user_workAddr", data.workAddress)
                        .set("user_creation_time", Timestamp.now())
                        .build();
                txn.add(user);
                LOG.info("User registered " + data.username);
                txn.commit();
                return Response.ok("{}").build();

            } finally {
                if (txn.isActive())
                    txn.rollback();
            }
        }
    }


    private void setupAccount(RegisterData data) {
        if (data.username.equals("su")) {
            data.role = UserRole.SU.toString();
            data.state = UserState.ACTIVE.toString();
        } else {
            data.role = UserRole.USER.toString();
            data.state = UserState.INACTIVE.toString();
        }

    }
}


