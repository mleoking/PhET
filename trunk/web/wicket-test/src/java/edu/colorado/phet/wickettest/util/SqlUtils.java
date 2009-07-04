package edu.colorado.phet.wickettest.util;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletContext;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.SimulationSQL;
import edu.colorado.phet.wickettest.WebSimulation;

public class SqlUtils {

    private static final String ALL_SIMS_QUERY = "SELECT project.name as project, simulation.name as simulation, project.sim_type as sim_type, localized_simulation.title as title, localized_simulation.description as description, localized_simulation.locale as locale FROM project, simulation, localized_simulation WHERE (project.id = simulation.project AND simulation.id = localized_simulation.simulation);";
    private static final String SINGLE_SIM_QUERY = "SELECT project.name as project, simulation.name as simulation, project.sim_type as sim_type, localized_simulation.title as title, localized_simulation.description as description, localized_simulation.locale as locale FROM project, simulation, localized_simulation WHERE (project.id = simulation.project AND simulation.id = localized_simulation.simulation AND project.name = ? AND simulation.name = ? AND localized_simulation.locale = ?);";
    private static final String NOT_TRANSLATED_SIM_QUERY = "SELECT project.name as project, simulation.name as simulation, project.sim_type as sim_type, localized_simulation.title as title, localized_simulation.description as description, localized_simulation.locale as locale FROM (SELECT simulation.name as simulation FROM simulation LEFT JOIN localized_simulation ON (localized_simulation.simulation = simulation.id AND localized_simulation.locale = ?) WHERE (localized_simulation.locale IS NULL)) as x, project, simulation, localized_simulation WHERE (project.id = simulation.project AND simulation.id = localized_simulation.simulation AND x.simulation = simulation.name AND localized_simulation.locale = 'en');";

    private static Connection getConnection( ServletContext context ) throws IOException, SQLException, ClassNotFoundException {
        Properties props = new Properties();
        props.load( context.getResourceAsStream( "/mysql-login.properties" ) );
        String user = props.getProperty( "user" );
        String pass = props.getProperty( "pass" );
        String host = props.getProperty( "host" );
        String name = props.getProperty( "name" );

        Class.forName( "com.mysql.jdbc.Driver" );
        String url = "jdbc:mysql://" + host + ":3306/" + name;
        Connection con = DriverManager.getConnection( url, user, pass );
        return con;
    }

    private static File getSimBase( ServletContext context ) {
        return new File( new File( context.getRealPath( "/" ) ).getParentFile(), "sims" );
    }

    public static List<WebSimulation> getAllSimulations( ServletContext context ) {
        List<WebSimulation> ret = new LinkedList<WebSimulation>();
        try {
            Connection con = getConnection( context );
            PreparedStatement stmt = con.prepareStatement( ALL_SIMS_QUERY );
            ResultSet resultSet = stmt.executeQuery();
            while ( resultSet.next() ) {
                ret.add( new WebSimulation( getSimBase( context ), new SimulationSQL( resultSet ) ) );
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        catch( SQLException e ) {
            e.printStackTrace();
        }
        catch( ClassNotFoundException e ) {
            e.printStackTrace();
        }
        return ret;
    }

    public static List<WebSimulation> getAllSimulationsWithoutLocale( ServletContext context, Locale locale ) {
        List<WebSimulation> ret = new LinkedList<WebSimulation>();
        try {
            Connection con = getConnection( context );
            PreparedStatement stmt = con.prepareStatement( NOT_TRANSLATED_SIM_QUERY );
            stmt.setString( 1, LocaleUtils.localeToString( locale ) );
            ResultSet resultSet = stmt.executeQuery();
            while ( resultSet.next() ) {
                ret.add( new WebSimulation( getSimBase( context ), new SimulationSQL( resultSet ) ) );
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        catch( SQLException e ) {
            e.printStackTrace();
        }
        catch( ClassNotFoundException e ) {
            e.printStackTrace();
        }
        return ret;
    }

    public static List<WebSimulation> getSimulationsMatching( ServletContext context, String project, String simulation, Locale locale ) {
        List<WebSimulation> ret = new LinkedList<WebSimulation>();
        try {
            Connection con = getConnection( context );
            String str = "SELECT project.name as project, simulation.name as simulation, project.sim_type as sim_type, localized_simulation.title as title, localized_simulation.description as description, localized_simulation.locale as locale FROM project, simulation, localized_simulation WHERE (project.id = simulation.project AND simulation.id = localized_simulation.simulation";
            if ( project != null ) {
                str += " AND project.name = ?";
            }
            if ( simulation != null ) {
                str += " AND simulation.name = ?";
            }
            if ( locale != null ) {
                str += " AND localized_simulation.locale = ?";
            }
            str += ");";
            //System.out.println( "Preparing statement: " + str );
            PreparedStatement stmt = con.prepareStatement( str );
            int index = 1;
            if ( project != null ) {
                stmt.setString( index++, project );
            }
            if ( simulation != null ) {
                stmt.setString( index++, simulation );
            }
            if ( locale != null ) {
                stmt.setString( index++, LocaleUtils.localeToString( locale ) );
            }
            ResultSet resultSet = stmt.executeQuery();
            while ( resultSet.next() ) {
                ret.add( new WebSimulation( getSimBase( context ), new SimulationSQL( resultSet ) ) );
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        catch( SQLException e ) {
            e.printStackTrace();
        }
        catch( ClassNotFoundException e ) {
            e.printStackTrace();
        }
        return ret;
    }

    public static WebSimulation getSingleSimulation( ServletContext context, String project, String simulation, Locale locale ) {
        List<WebSimulation> ret = new LinkedList<WebSimulation>();
        try {
            Connection con = getConnection( context );
            PreparedStatement stmt = con.prepareStatement( SINGLE_SIM_QUERY );
            stmt.setString( 1, project );
            stmt.setString( 2, simulation );
            stmt.setString( 3, LocaleUtils.localeToString( locale ) );
            ResultSet resultSet = stmt.executeQuery();
            while ( resultSet.next() ) {
                ret.add( new WebSimulation( getSimBase( context ), new SimulationSQL( resultSet ) ) );
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        catch( SQLException e ) {
            e.printStackTrace();
        }
        catch( ClassNotFoundException e ) {
            e.printStackTrace();
        }

        if ( ret.isEmpty() ) {
            return null;
        }
        else if ( ret.size() > 1 ) {
            System.out.println( "WARNING: multiple sims correspond to one project / simulation / locale combo" );
            return ret.get( 0 );
        }
        else {
            return ret.get( 0 );
        }
    }

    public static WebSimulation getBestSimulation( ServletContext context, String project, String simulation, Locale locale ) {
        WebSimulation ret = getSingleSimulation( context, project, simulation, locale );
        if ( ret == null ) {
            System.out.println( "Could not find simulation " + simulation + " with locale " + LocaleUtils.localeToString( locale ) + ", resorting to default" );
            ret = getSingleSimulation( context, project, simulation, LocaleUtils.stringToLocale( "en" ) );
        }
        return ret;
    }

}
