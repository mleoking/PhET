package edu.colorado.phet.tomcattest;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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

}
