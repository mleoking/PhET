package edu.colorado.phet.tomcattest;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FullSimulationList extends HttpServlet {
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        response.setContentType( "text/html" );
        PrintWriter out = response.getWriter();


        Properties props = new Properties();
        props.load( getServletContext().getResourceAsStream( "/mysql-login.properties" ) );
        String user = props.getProperty( "user" );
        String pass = props.getProperty( "pass" );
        String host = props.getProperty( "host" );
        String name = props.getProperty( "name" );

        try {
            Class.forName( "com.mysql.jdbc.Driver" );
            String url = "jdbc:mysql://" + host + ":3306/" + name;
            Connection con = DriverManager.getConnection( url, user, pass );
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM simulation ORDER BY sim_sorting_name" );

            File simBase = new File( new File( getServletContext().getRealPath( "/" ) ).getParentFile(), "sims" );

            List<WebsiteSimulation> simulations = WebsiteSimulation.fromResultSet( simBase, resultSet );

            out.println( "<html><head><title>Full Simulation List (TEST)</title></head><body>" );

            for ( WebsiteSimulation simulation : simulations ) {
                if ( !simulation.isReal() ) {
                    continue;
                }
                out.println( "<a href='/simulation/" + simulation.getProject() + "/" + simulation.getSimulation() + "'>" + simulation.getDisplayName() + "</a><br>" );
            }

            out.println( "</body></html>" );
        }
        catch( ClassNotFoundException e ) {
            e.printStackTrace();
        }
        catch( SQLException e ) {
            e.printStackTrace();
        }
    }
}
