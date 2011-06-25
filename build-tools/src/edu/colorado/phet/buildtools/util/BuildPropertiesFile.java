package edu.colorado.phet.buildtools.util;

import java.io.File;
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
        super( getBuildPropertiesFile( project.getProjectDir() ) );
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
    public String getMXML( String simulationName ) {
        return getProperty( "project.flavor." + simulationName + ".mxml" );
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

    /**
     * Returns true if the build file specifies to use all-permissions in the security tag; this is specified
     * by putting security=all-permissions in the build file.
     *
     * @return true if this sim is requesting all permissions.
     */
    public boolean requestAllPermissions() {
        final String securityProperty = getProperty( "security" );
        return securityProperty != null && securityProperty.equals( "all-permissions" );
    }

    /**
     * Retrieves the build properties file for the dir and project name. This
     * file may not exist, if the directory does not describe a project.
     *
     * @param dir The project directory.
     * @return The build properties file.
     */
    public static File getBuildPropertiesFile( File dir ) {
        return new File( dir, dir.getName() + "-build.properties" );
    }

    /**
     * Determines whether a simulation has specified to generate a resource loading file
     *
     * @return true if resource file should be generated
     * @see edu.colorado.phet.buildtools.preprocessor.ResourceGenerator
     */
    public boolean getGenerateResourceFile() {
        final String generateResourceFileProperty = getProperty( "generateResourceFile" );
        return generateResourceFileProperty != null && generateResourceFileProperty.equals( "true" );
    }
}
