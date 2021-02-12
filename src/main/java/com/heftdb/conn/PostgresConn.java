// 
// Decompiled by Procyon v0.5.36
// 

package com.heftdb.conn;

import com.heftdb.graphmk.Configuration;

import java.util.Properties;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class PostgresConn
{
    private Connection conn;
    
    public PostgresConn() {
        this.conn = null;
        if (establishDriver()) {
            this.conn = establishConnection();
        }
    }
    
    public PostgresConn(final String hostname, final String port, final String database, final String username, final String password) throws SQLException {
        this.conn = null;
        if (establishDriver()) {
            this.conn = establishConnection(hostname, port, database, username, password);
        }
    }
    
    private static Connection establishConnection(final String hostname, final String port, final String database, final String username, final String password) throws SQLException {
        Connection connection = null;
        final String con = "jdbc:postgresql://" + hostname + ":" + port + "/" + database;
        System.out.println("Connecting to " + con);
        connection = DriverManager.getConnection(con, username, password);
        return connection;
    }
    
    public Connection getConnection() {
        return this.conn;
    }
    
    static boolean establishDriver() {
        System.out.println("-------- PostgreSQL JDBC Connection ------------");
        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException e) {
            System.out.println("Where is your PostgreSQL JDBC Driver? Include in your library path!");
            e.printStackTrace();
            System.out.println("PostgreSQL JDBC Driver Registered!");
            return false;
        }
        return true;
    }
    
    static Connection establishConnection() {
        Connection connection = null;
        try {
            final Configuration properties = new Configuration();
            final Properties p = properties.getPropValues();
            final String con = "jdbc:postgresql://" + p.getProperty("hostname") + ":" + p.getProperty("port") + "/" + p.getProperty("database");
            System.out.println("Connecting to " + con);
            connection = DriverManager.getConnection(con, p.getProperty("username"), p.getProperty("password"));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
        return connection;
    }
}
