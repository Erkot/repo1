package service;

import com.mycompany.dbtest.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Stateless
@Path("/auth")
public class AuthRepo extends AbstractFacade<User> {

    private EntityManager em;
    private List<String> authCodes = new ArrayList<>();

    ;
   
//    @Context 
//    private HttpServletRequest request;


    public AuthRepo() {
        super(User.class);
    }

    @GET
    @Path("/e")
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }

    @GET
    @Path("/home2")
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt2(@Context final HttpServletRequest request) {
        return "Got it!" + ((User) request.getSession().getAttribute("user")).getUser();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }

    @GET
    @Path("/authCodeBakarRepo")
    @Produces(MediaType.TEXT_PLAIN)
    public void authCodeBakarRepo(@QueryParam("authCode") String authCode) throws IOException {
        authCodes.add(authCode);
    }

    /**
     * Wait for the authorization code from the user, approve it, delete it and
     * send the token back
     */
    @GET
    @Path("/authCodeUserRepo")
    @Produces(MediaType.TEXT_PLAIN)
    public String authCodeUserRepo(@QueryParam("authCode") String authCode) throws IOException {
        if (authCodes.contains(authCode)) {
            authCodes.remove(authCode);
        }
        String token = authCode + "token";
        return token;
    }

    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String login(@FormParam("user") String username, @FormParam("pass") String pass, @Context final HttpServletRequest request, @Context final HttpServletResponse response) throws IOException {
        if (username.equalsIgnoreCase("q")) {
            User u = new User();
            u.setIduser(8888);
            u.setPass("pass8888");
            u.setUser("user8888");
            request.getSession().setAttribute("user", u);
        }
        response.sendRedirect("/bakar/home.html");
        return username + " " + pass;
    }

    @Path("/reg")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String testForm(@FormParam("user") String user, @FormParam("pass") String pass, @FormParam("repo") String repo) {
        return user + " " + pass + " " + repo;
    }

    @Override
    protected EntityManager getEntityManager() {
        em = Persistence.createEntityManagerFactory("com.mycompany_dbtest_war_1.0-SNAPSHOTPU").createEntityManager();
        return em;
    }
}
