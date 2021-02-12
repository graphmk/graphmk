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

public class SQLiteConn
{
    private Connection conn;
    
    protected SQLiteConn() {
        this.conn = null;
        if (establishDriver()) {
            this.conn = establishConnection();
        }
    }
    
    public SQLiteConn(final String database) throws SQLException {
        this.conn = null;
        if (establishDriver()) {
            this.conn = establishConnection(database);
        }
    }
    
    private static Connection establishConnection(final String database) throws SQLException {
        Connection connection = null;
        final String con = "jdbc:sqlite:" + database;
        System.out.println("Connecting to " + con);
        connection = DriverManager.getConnection(con);
        return connection;
    }
    
    public Connection getConnection() {
        return this.conn;
    }
    
    static boolean establishDriver() {
        System.out.println("-------- SQLite JDBC Connection ------------");
        try {
            Class.forName("org.sqlite.JDBC");
        }
        catch (ClassNotFoundException e) {
            System.out.println("Where is your SQLite JDBC Driver? Include in your library path!");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    static Connection establishConnection() {
        Connection connection = null;
        try {
            final Configuration properties = new Configuration();
            final Properties p = properties.getPropValues();
            final String con = "jdbc:sqlite://" + p.getProperty("hostname") + ":" + p.getProperty("port") + "/" + p.getProperty("database");
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
