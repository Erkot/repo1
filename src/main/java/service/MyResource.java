package service;

import com.mycompany.dbtest.User;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
 

@Path("/e")
public class MyResource extends AbstractFacade<User> {
    @PersistenceContext(unitName = "com.mycompany_dbtest_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    public MyResource() {
        super(User.class);
    }
    
    @GET
    @Path("/e")
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }
    
       @GET
    @Path("/count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}