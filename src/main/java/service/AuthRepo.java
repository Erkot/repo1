package service;

import com.repo1.entity.User;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
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
public class AuthRepo {
//extends AbstractFacade<User>
//    @PersistenceContext(unitName = "com.repo1")
//    private EntityManager em;
//    @Resource(name = "jdbc/repo1")
//    private DataSource ds;

    private List<String> authCodes = new ArrayList<>();

//    @Context 
//    private HttpServletRequest request;
//    public AuthRepo() {
//        super(User.class);
//    }
    
    public Connection getDs() throws NamingException, SQLException{
        javax.naming.Context initContext = new InitialContext();
        javax.naming.Context ctx = (javax.naming.Context) initContext.lookup("java:/comp/env");
        DataSource ds = (DataSource) ctx.lookup("jdbc/repo1");
        return ds.getConnection();
    }
    @GET
    @Path("/e")
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() throws SQLException, NamingException {
        int executeUpdate;
        try (PreparedStatement pstmt = getDs().prepareStatement("select * from user")) {
            executeUpdate = pstmt.getFetchSize();
        }
        return "Got it!" + executeUpdate;
    }

    @GET
    @Path("/home2")
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt2(@Context final HttpServletRequest request) {
        return "Got it!" + ((User) request.getSession().getAttribute("user")).getUsername();
    }

//    @GET
//    @Path("/count")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String countREST() {
//        return String.valueOf(super.count());
//    }
    /**
     * Triggered by the Bakar
     *
     * @param authCode
     * @throws IOException
     */
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

    /**
     * Login by the repo form
     *
     * @param username
     * @param pass
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @Path("/repoLogin")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String repoLogin(@FormParam("user") String username, @FormParam("pass") String pass, @Context final HttpServletRequest request, @Context final HttpServletResponse response) throws IOException {

        User u = new User();
        u.setPassword(pass);
        u.setUsername(username);
        request.getSession().setAttribute("user", u);
        response.sendRedirect("/repo1/home.html");
        return username + " " + pass;
    }

    /**
     * Register by the repo form
     *
     * @param username
     * @param password
     */
    @Path("/repoReg")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void repoReg(@FormParam("Username") String username, @FormParam("password") String password) throws SQLException, NamingException {
        PreparedStatement ps = getDs().prepareStatement("Select id from user where username = ?");
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        int fetchSize = rs.getFetchSize();
        if (fetchSize==0) {
        // new user
           ps = getDs().prepareStatement("insert into user (username,password) values (?,?)");
           ps.setString(1, username);
           ps.setString(2, password);
           ps.executeUpdate();
//            User user = new User();
//            user.setUsername(username);
//            user.setPassword(password);
//            EntityTransaction etx = em.getTransaction();
//            etx.begin();
//            super.create(user);
//                    getEntityManager().persist(user);
//            etx.commit();
        } else {
        // notify that the user already exist
        }
    }

    /**
     * Register by the repo form
     *
     * @param user
     * @param pass
     * @param repo
     * @return
     */
    @Path("/bakarReg")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String bakarReg(@FormParam("user") String user, @FormParam("pass") String pass, @FormParam("repo") String repo) {
        return user + " " + pass + " " + repo;
    }

//    @Override
//    protected EntityManager getEntityManager() {
//        em = Persistence.createEntityManagerFactory("com.repo1").createEntityManager();
//        return em;
//    }
}
