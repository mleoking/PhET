package edu.colorado.phet.buildtools.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.common.phetcommon.util.AbstractPropertiesFile;

/**
 * Abstraction of the project build properties files (eg, faraday-build.properties).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BuildPropertiesFile extends AbstractPropertiesFile {

    private final PhetProject project;

    public BuildPropertiesFile( PhetProject project ) {
        super( PhetBuildUtils.getBuildPropertiesFile( project.getProjectDir(), project.getName() ) );
        this.project = project;
    }

    public String getSource() {
        return getProperty( "project.depends.source" );
    }

    public String getScalaSource() {
        return getProperty( "project.depends.scala.source" );
    }

    public String getLib() {
        return getProperty( "project.depends.lib" );
    }

    public String getData() {
        return getProperty( "project.depends.data" );
    }

    public String[] getKeepMains() {
        String v = getProperty( "project.keepmains" );
        if ( v == null ) {
            v = "";
        }
        return split( v, ": " );
    }

    /**
     * Returns the key values [simulation], not the titles for all simulations declared in this project.
     * If no simulations are declared, the project name is returned as the sole simulation.
     *
     * @return
     */
    public String[] getSimulationNames() {
        ArrayList simulationNames = new ArrayList();
        Enumeration e = getPropertyNames();
        while ( e.hasMoreElements() ) {
            String s = (String) e.nextElement();
            String prefix = "project.flavor.";
            if ( s.startsWith( prefix ) ) {
                String lastPart = s.substring( prefix.length() );
                int lastIndex = lastPart.indexOf( '.' );
                String simulationName = lastPart.substring( 0, lastIndex );
                if ( !simulationNames.contains( simulationName ) ) {
                    simulationNames.add( simulationName );
                }
            }
        }
        if ( simulationNames.size() == 0 ) {
            simulationNames.add( project.getName() );
        }

        return (String[]) simulationNames.toArray( new String[0] );
    }

    public String getMainClass( String simulationName ) {
        return getProperty( "project.flavor." + simulationName + ".mainclass" );
    }

    public String getMainClassDefault() {
        return getProperty( "project.mainclass" );
    }

    public String[] getArgs( String simulationName ) {
        String argsString = getProperty( "project.flavor." + simulationName + ".args" );
        return PhetBuildUtils.toStringArray( argsString == null ? "" : argsString, " " );
    }

    //TODO: factor this into a subclass
    public String getMXML(String simulationName){
        return getProperty( "project.flavor." + simulationName + ".mxml" );
    }

    public String getScreenshot( String simulationName ) {
        return getProperty( "project.flavor." + simulationName + ".screenshot" );
    }

    public String getTitleDefault() {
        return getProperty( "project.name" );
    }

    public String getDescriptionDefault() {
        return getProperty( "project.description" );
    }

    //TODO: this should be moved to some general utility class
    private String[] split( String str, String delimiters ) {
        ArrayList out = new ArrayList();
        StringTokenizer stringTokenizer = new StringTokenizer( str, delimiters );
        while ( stringTokenizer.hasMoreTokens() ) {
            out.add( stringTokenizer.nextToken() );
        }
        return (String[]) out.toArray( new String[0] );
    }
}
