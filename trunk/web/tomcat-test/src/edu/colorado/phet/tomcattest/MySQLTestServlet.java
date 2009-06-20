package edu.colorado.phet.tomcattest;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

public class MySQLTestServlet extends HttpServlet {
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        Properties props = new Properties();
        props.load( getServletContext().getResourceAsStream( "/mysql-login.properties" ) );
        response.setContentType( "text/html" );
        PrintWriter out = response.getWriter();
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

            ret = stmt.executeQuery( "SELECT * FROM simulation ORDER BY sim_sorting_name" );

            File simBase = new File( new File( getServletContext().getRealPath( "/" ) ).getParentFile(), "sims" );
            System.out.println( "simBase: " + simBase );

            while ( ret.next() ) {
                int simId = ret.getInt( "sim_id" );
                String simName = ret.getString( "sim_name" );
                System.out.println( "Loading sim: " + simId + " " + simName );
                String simDirName = ret.getString( "sim_dirname" );
                String simFlavorName = ret.getString( "sim_flavorname" );
                int simRating = ret.getInt( "sim_rating" );
                int simType = ret.getInt( "sim_type" );
                String simLaunchUrl = ret.getString( "sim_launch_url" );
                String simImageUrl = ret.getString( "sim_image_url" );
                String simDescription = ret.getString( "sim_desc" );
                boolean simIsReal = ret.getBoolean( "sim_is_real" );

                final String project = simDirName;
                String simulation = simFlavorName;

                File projectDir = new File( simBase, project );

                if ( simType == 0 ) {
                    // Java

                }
                else {
                    // Flash
                    File[] htmlFiles = projectDir.listFiles( new FilenameFilter() {
                        public boolean accept( File file, String name ) {
                            return ( name.startsWith( project + "_" ) && name.endsWith( ".html" ) );
                        }
                    } );
                    List<Locale> locales = new LinkedList<Locale>();
                    for ( File htmlFile : htmlFiles ) {
                        String htmlName = htmlFile.getName();
                        String localeString = htmlName.substring( project.length() + 1, htmlName.indexOf( ".html" ) );
                        if ( localeString.equals( "en" ) ) {
                            continue;
                        }
                        locales.add( LocaleUtils.stringToLocale( localeString ) );
                    }
                    Collections.sort( locales, new Comparator<Locale>() {
                        public int compare( Locale a, Locale b ) {
                            return LocaleUtils.localeToString( a ).compareTo( LocaleUtils.localeToString( b ) );
                        }
                    } );
                    locales.add( 0, LocaleUtils.stringToLocale( "en" ) );

                    String str = "<a href='/sims/" + project + "/" + project + "_en.html'>" + simName + "</a>";
                    str += " (";
                    for ( Locale locale : locales ) {
                        str += " <a href='/sims/" + project + "/" + project + "_" + LocaleUtils.localeToString( locale ) + ".html'>" + LocaleUtils.localeToString( locale ) + "</a>";
                    }
                    str += " )";
                    out.println( str + "<br>" );
                }
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
