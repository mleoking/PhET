package edu.colorado.phet.tomcattest.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

public class WebSimulation {

    private File simBase;
    private SimulationSQL sql;

    public WebSimulation( File simBase, SimulationSQL sql ) {
        this.simBase = simBase;
        this.sql = sql;
    }

    public SimulationSQL getSql() {
        return sql;
    }

    public void setSql( SimulationSQL sql ) {
        this.sql = sql;
    }

    public String getProject() {
        return sql.getProject();
    }

    public String getSimulation() {
        return sql.getSimulation();
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
        return sql.getType() == 0;
    }

    public boolean isFlash() {
        return sql.getType() == 1;
    }

    public Locale getLocale() {
        return LocaleUtils.stringToLocale( sql.getLocaleString() );
    }

    public String getLocaleString() {
        return sql.getLocaleString();
    }

    public String getImageUrl() {
        return "/sims/" + getProject() + "/" + getSimulation() + "-screenshot.png";
    }

    public String getThumbnailUrl() {
        return "/sims/" + getProject() + "/" + getSimulation() + "-thumbnail.jpg";
    }

    public String getTitle() {
        return sql.getTitle();
    }

    public String getDescription() {
        return sql.getDescription();
    }

    public String getRunUrl() {
        String str = "/sims/" + getProject() + "/" + getSimulation() + "_" + getLocaleString();
        if ( isJava() ) {
            str += ".jnlp";
        }
        else if ( isFlash() ) {
            str += ".html";
        }
        else {
            throw new RuntimeException( "Handle more than java and flash" );
        }
        return str;
    }

    /**
     * Sort a list of localized simulations for a particular locale. This means simulations will be sorted
     * first by the title in the locale parameter (if there is a title), then by locale.
     *
     * @param list   The list of simulations to order
     * @param locale The locale to use for ordering
     */
    public static void orderSimulations( List<WebSimulation> list, final Locale locale ) {
        final HashMap<String, String> map = new HashMap<String, String>();

        for ( WebSimulation sim : list ) {
            boolean correctLocale = locale.equals( sim.getLocale() );
            if ( !map.containsKey( sim.getSimulation() ) || correctLocale ) {
                if ( correctLocale ) {
                    map.put( sim.getSimulation(), sim.getTitle() );
                }
                else {
                    map.put( sim.getSimulation(), null );
                }
            }
        }

        Collections.sort( list, new Comparator<WebSimulation>() {
            public int compare( WebSimulation a, WebSimulation b ) {

                if ( a.getSimulation().equals( b.getSimulation() ) ) {
                    if ( a.getLocale().equals( locale ) ) {
                        return -1;
                    }
                    if ( b.getLocale().equals( locale ) ) {
                        return 1;
                    }
                    return a.getLocale().getDisplayName( locale ).compareToIgnoreCase( b.getLocale().getDisplayName( locale ) );
                }

                String aGlobalTitle = map.get( a.getSimulation() );
                String bGlobalTitle = map.get( b.getSimulation() );

                boolean aGlobal = aGlobalTitle != null;
                boolean bGlobal = bGlobalTitle != null;

                if ( aGlobal && bGlobal ) {
                    final String[] ignoreWords = {"The", "La", "El"};
                    for ( String ignoreWord : ignoreWords ) {
                        if ( aGlobalTitle.startsWith( ignoreWord + " " ) ) {
                            aGlobalTitle = aGlobalTitle.substring( ignoreWord.length() + 1 );
                        }
                        if ( bGlobalTitle.startsWith( ignoreWord + " " ) ) {
                            bGlobalTitle = bGlobalTitle.substring( ignoreWord.length() + 1 );
                        }
                    }
                    return aGlobalTitle.compareToIgnoreCase( bGlobalTitle );
                }
                else if ( aGlobal ) {
                    return -1;
                }
                else if ( bGlobal ) {
                    return 1;
                }
                else {
                    return a.getSimulation().compareToIgnoreCase( b.getSimulation() );
                }
            }
        } );
    }

}
