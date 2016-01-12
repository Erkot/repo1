/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author Eitan
 */
public class Conn {

    private static Connection conn;

    public Conn() throws NamingException, SQLException {
        javax.naming.Context initContext = new InitialContext();
        javax.naming.Context ctx = (javax.naming.Context) initContext.lookup("java:/comp/env");
        DataSource ds = (DataSource) ctx.lookup("jdbc/repo1");
        Connection c = ds.getConnection();

        setConn(c);
        // wrap the connection with log4jdbc

    }

    public static Connection getConn() {
        return conn;
    }

    public static void setConn(Connection conn) {
        Conn.conn = conn;
    }

 

}
