package edu.colorado.phet.tomcattest;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

import javax.servlet.ServletContext;

public class SqlUtils {
    public static ResultSet run( ServletContext context, String query ) {
        ResultSet ret = null;
        try {
            Properties props = new Properties();
            props.load( context.getResourceAsStream( "/mysql-login.properties" ) );
            String user = props.getProperty( "user" );
            String pass = props.getProperty( "pass" );
            String host = props.getProperty( "host" );
            String name = props.getProperty( "name" );

            Class.forName( "com.mysql.jdbc.Driver" );
            String url = "jdbc:mysql://" + host + ":3306/" + name;
            Connection con = DriverManager.getConnection( url, user, pass );
            Statement stmt = con.createStatement();
            ret = stmt.executeQuery( query );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        catch( ClassNotFoundException e ) {
            e.printStackTrace();
        }
        catch( SQLException e ) {
            e.printStackTrace();
        }
        return ret;
    }

    public static ResultSet getSimulationSql( ServletContext context, String project, String simulation ) {
        String str = "SELECT * FROM simulation WHERE (sim_dirname = '" + project + "' AND sim_flavorname = '" + simulation + "');";
        System.out.println( str );
        return run( context, str );
    }

}
