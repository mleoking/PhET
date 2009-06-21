package edu.colorado.phet.tomcattest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import javax.servlet.ServletContext;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

public class WebsiteSimulation {

    private File simBase;
    private SimulationSQL sql;

    public WebsiteSimulation( File simBase, SimulationSQL sql ) {
        this.simBase = simBase;
        this.sql = sql;
    }

    public static List<WebsiteSimulation> fromResultSet( File simBase, ResultSet resultSet ) throws SQLException {
        List<WebsiteSimulation> ret = new LinkedList<WebsiteSimulation>();
        if ( resultSet == null ) {
            System.out.println( "Warning: no simulations in fromResultSet" );
            return ret;
        }
        while ( resultSet.next() ) {
            ret.add( new WebsiteSimulation( simBase, new SimulationSQL( resultSet ) ) );
        }
        return ret;
    }

    public SimulationSQL getSql() {
        return sql;
    }

    public void setSql( SimulationSQL sql ) {
        this.sql = sql;
    }

    public String getDisplayName() {
        return sql.getSimName();
    }

    public String getProject() {
        return sql.getSimDirName();
    }

    public String getSimulation() {
        return sql.getSimFlavorName();
    }

    public File getProjectDir() {
        return new File( simBase, getProject() );
    }

    public File getPropertiesFile() {
        return new File( getProjectDir(), getProject() + ".properties" );
    }

    public String getProjectProperty( String key ) {
        Properties props = new Properties();
        try {
            props.load( new FileInputStream( getPropertiesFile() ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
            return null;
        }
        return props.getProperty( key );
    }

    public int getVersionRevision() {
        return Integer.valueOf( getProjectProperty( "version.revision" ) );
    }

    public int getVersionMajor() {
        return Integer.valueOf( getProjectProperty( "version.major" ) );
    }

    public int getVersionMinor() {
        return Integer.valueOf( getProjectProperty( "version.minor" ) );
    }

    public int getVersionDev() {
        return Integer.valueOf( getProjectProperty( "version.dev" ) );
    }

    public int getVersionTimestamp() {
        return Integer.valueOf( getProjectProperty( "version.timestamp" ) );
    }

    public String getVersionString() {
        String ret = getProjectProperty( "version.major" ) + "." + getProjectProperty( "version.minor" );
        if ( getVersionDev() != 0 ) {
            ret += "." + getProjectProperty( "version.dev" );
        }
        return ret;
    }

    public boolean isJava() {
        return sql.getSimType() == 0;
    }

    public boolean isFlash() {
        return sql.getSimType() == 1;
    }

    public List<Locale> getLocales() {
        final String patt;
        if ( isJava() ) {
            patt = ".jnlp";
        }
        else if ( isFlash() ) {
            patt = ".html";
        }
        else {
            throw new RuntimeException( "getLocales() doesn't handle all cases?" );
        }

        File[] htmlFiles = getProjectDir().listFiles( new FilenameFilter() {
            public boolean accept( File file, String name ) {
                return ( name.startsWith( getSimulation() + "_" ) && name.endsWith( patt ) );
            }
        } );
        List<Locale> locales = new LinkedList<Locale>();
        for ( File htmlFile : htmlFiles ) {
            String htmlName = htmlFile.getName();
            String localeString = htmlName.substring( getSimulation().length() + 1, htmlName.indexOf( patt ) );
            if ( localeString.equals( LocaleUtils.localeToString( getDefaultLocale() ) ) ) {
                continue;
            }
            locales.add( LocaleUtils.stringToLocale( localeString ) );
        }
        Collections.sort( locales, new Comparator<Locale>() {
            public int compare( Locale a, Locale b ) {
                return LocaleUtils.localeToString( a ).compareTo( LocaleUtils.localeToString( b ) );
            }
        } );
        locales.add( 0, getDefaultLocale() );
        return locales;
    }

    public Locale getDefaultLocale() {
        return LocaleUtils.stringToLocale( "en" );
    }

    public boolean isReal() {
        return sql.isSimIsReal();
    }

    public String getImageUrl() {
        String ret = sql.getSimImageUrl().replace( "http://phet.colorado.edu", "" );
        if ( ret == null || ret.length() == 0 ) {
            ret = "/sims/" + getProject() + "/" + getSimulation() + "-screenshot.png";
        }
        return ret;
    }

    public static WebsiteSimulation getSimulation( ServletContext context, String project, String simulation ) {
        ResultSet resultSet = SqlUtils.getSimulationSql( context, project, simulation );
        File simBase = new File( new File( context.getRealPath( "/" ) ).getParentFile(), "sims" );
        WebsiteSimulation ret = null;
        try {
            List<WebsiteSimulation> sims = fromResultSet( simBase, resultSet );
            if ( sims.size() == 1 ) {
                ret = sims.get( 0 );
            }
            else if ( sims.isEmpty() ) {
                System.out.println( "No simulations that match" );
            }
            else {
                System.out.println( "WARNING: sim / project combo match two sims!" );
            }
        }
        catch( SQLException e ) {
            e.printStackTrace();
        }
        return ret;
    }

}
