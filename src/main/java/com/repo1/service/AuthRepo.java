package com.repo1.service;

import com.repo1.constraint.NotEmptySearchField;
import com.repo1.entity.User;
import com.repo1.helper.Conn;
import com.repo1.helper.TokenUserCache;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import org.hibernate.validator.constraints.NotBlank;

@Stateless
@Path("/auth")
public class AuthRepo {

    private List<String> authCodes = new ArrayList<>();
    private Map tokenMap = new HashMap();
    String token = null;

    public Connection getDs() {
        return Conn.getInstance().MysqlConn;
    }

//    public AuthRepo() {
//        if (tokenMap.size() == 0) {
//            tokenMap = TokenUserCache.getInstance().tokenMap;
//        }
//    }

    public void putUserByToken(String token, User user){
        TokenUserCache.putUserOnToken(token, user);
    }
    
    public User getUserByToken(String token){
        return TokenUserCache.getUserByToken(token);
    }
    
    @GET
    @Path("/e")
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() throws SQLException, NamingException, ClassNotFoundException {
//                Class myClass = Class.forName("net.sf.log4jdbc.DriverSpy");
//System.out.println("Number of public methods: " + myClass.getMethods().length);
        int executeUpdate;
        try (PreparedStatement pstmt = getDs().prepareStatement("select * from user")) {
            executeUpdate = pstmt.getFetchSize();
        }
        return "Got it!" + executeUpdate;
    }

    @GET
    @Path("/cookie")
    @Produces(MediaType.TEXT_PLAIN)
    public Response testCookie() throws URISyntaxException {
        System.out.println("xxxxx");
        Cookie cookie = new Cookie("xxxxx", "eeeee!");
        return Response.seeOther(new URI("/e"))
                //.ok()
                .cookie(new NewCookie(cookie, "ccc", 60 * 24, false))
                .build();
    }

    @GET
    @Path("/home2")
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt2(@QueryParam("token") String token) {
        User u = getUserByToken(token);
        return "Got it!" + u.getUsername();
    }

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
    @NotNull
//    @NotEmptySearchField
//    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//     ,@Context final HttpServletRequest request, @Context final HttpServletResponse response
    public Response repoLogin(@FormParam("username") String username, @FormParam("pass") String pass) {
        System.out.println("repoLogin");

        User u = new User();
        u.setPassword(pass);
        u.setUsername(username);
//        request.getSession().setAttribute("user", u);
        //create the token
        token = username + new BigInteger(130, new SecureRandom()).toString(32);
        putUserByToken(token, u);
//        tokenMap.put(token, u);
        //send token back to user
//        response.sendRedirect("/repo1/home.html?t=" + token);
//        return username + " " + pass;
        Cookie cookie = new Cookie("token", token, "/repo1", "localhost");
        return Response
                //                .seeOther(new URI("/repo1/home.html?t=" + token))
                .ok().entity("ytyytyt")
                .cookie(new NewCookie(cookie, "ccc", 60 * 24, false))
                .build();
    }

//    @Path("/tester")
//    @POST
//    @NotNull
////    @NotEmptySearchField
//    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//    public String tester(@NotBlank(message = "search.string.empty") @FormParam("username") String username, @FormParam("pass") String pass) throws IOException {
//
//        System.out.println("sdfsd");
//        return username + " " + pass;
//    }
    /**
     * Register by the repo form
     *
     * @param username
     * @param password
     */
    @Path("/repoReg")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void repoReg(@FormParam("Username") String username, @FormParam("password") String password, @Context final HttpServletRequest request, @Context final HttpServletResponse response) throws SQLException, NamingException, IOException {
        PreparedStatement ps = getDs().prepareStatement("Select id from user where username = ?");
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        int fetchSize = rs.getFetchSize();
        if (fetchSize == 0) {
            // new user
            ps = getDs().prepareStatement("insert into user (username,password) values (?,?)");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            //create the user object
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            //create the token
            token = username + new BigInteger(130, new SecureRandom()).toString(32);
            tokenMap.put(token, user);
            //send token back to user
            response.sendRedirect("/repo1/home.html?t=" + token);

        } else {
            // notify that the user already exist
        }
    }

    /**
     * Register by the bakar form
     *
     * @param user
     * @param pass
     * @param repo
     * @return
     */
    @Path("/bakarReg")
    @POST
//    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String bakarReg(@FormParam("user") String user, @FormParam("pass") String pass, @FormParam("repo") String repo) {
        System.out.println("bakarReg");
        testPost("asdas");
        return user + " " + pass + " " + repo;
    }

    @Path("/testPost")
    @POST
//    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response testPost(String repo) {
        System.out.println("testPost");
        Cookie cookie = new Cookie("xxxxx", "eeeee!");
        return Response
                //                .seeOther(new URI("/repo1/home.html?t=" + token))
                .ok()
                .cookie(new NewCookie(cookie, "ccc", 60 * 24, false))
                .build();
    }
}
