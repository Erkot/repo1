/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.repo1.helper;

import com.mysql.jdbc.ConnectionImpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author Eitan
 */
public class Conn {

    private static Conn conn;
    public Connection MysqlConn;

    public static synchronized Conn getInstance() {
        if (conn == null) {
            conn = new Conn();
        }
        return conn;
    }

    private Conn() {
        try {
            javax.naming.Context initContext = new InitialContext();
            javax.naming.Context ctx = (javax.naming.Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) ctx.lookup("jdbc/repo1");
            MysqlConn = ds.getConnection();
            
// wrap the connection with log4jdbc
        } catch (NamingException ex) {
            Logger.getLogger(Conn.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Conn.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
//    public static Connection getConn() {
//        return conn;
//    }
//
//    public static void setConn(Connection conn) {
//        Conn.conn = conn;
//    }
}
