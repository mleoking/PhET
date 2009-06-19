package edu.colorado.phet.tomcattest;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MySQLTestServlet extends HttpServlet {
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        Properties props = new Properties();
        props.load( getServletContext().getResourceAsStream( "/mysql-login.properties" ) );
        response.setContentType( "text/html" );
        PrintWriter out = response.getWriter();
        //out.println( "user = " + props.getProperty( "user" ) + "<br>" );
        //out.println( "pass = " + props.getProperty( "pass" ) + "<br>" );
        String user = props.getProperty( "user" );
        String pass = props.getProperty( "pass" );
        String host = props.getProperty( "host" );
        String name = props.getProperty( "name" );
        try {
            Class.forName( "com.mysql.jdbc.Driver" );

            String url = "jdbc:mysql://" + host + ":3306/" + name;

            Connection con = DriverManager.getConnection( url, user, pass );

            System.out.println( "URL: " + url );
            System.out.println( "Connection: " + con );
            out.println( url + "<br>" );
            out.println( con + "<br>" );

            Statement stmt = con.createStatement();

            ResultSet ret;

            ret = stmt.executeQuery( "SELECT * FROM testrow" );

            while ( ret.next() ) {
                int id = ret.getInt( "id" );
                String value = ret.getString( "value" );
                out.println( "id=" + String.valueOf( id ) + ", value=" + value + "<br>" );
            }

            con.close();
        }
        catch( ClassNotFoundException e ) {
            e.printStackTrace();
        }
        catch( SQLException e ) {
            e.printStackTrace();
        }
    }
}
