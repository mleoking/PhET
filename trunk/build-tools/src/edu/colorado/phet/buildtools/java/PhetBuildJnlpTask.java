/* Copyright 2007, University of Colorado */
package edu.colorado.phet.buildtools.java;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang.StringEscapeUtils;

import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.buildtools.AbstractPhetBuildTask;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.Simulation;
import edu.colorado.phet.buildtools.PhetServer;
//todo: needs better error handling for loading simulations
//todo: test support for deploying with online url

//todo: see other todos below
public class PhetBuildJnlpTask extends AbstractPhetBuildTask {


    private volatile String simulationName;
    private volatile String deployUrl;
    private volatile String locale = "en";
    private boolean dev = true;

    protected void executeImpl( PhetProject phetProject ) throws Exception {
        if ( simulationName == null ) {
            simulationName = phetProject.getSimulationNames()[0];
        }
        Simulation simulation = phetProject.getSimulation( simulationName, locale );
        echo( "loaded simulation=" + simulation );
        if ( deployUrl == null ) {
            deployUrl = phetProject.getDeployDir().toURL().toString();

            echo( "Deploy url is null -- using " + deployUrl );
        }

        final File JNLP_TEMPLATE = new File( phetProject.getProjectDir().getParentFile().getParentFile().getParentFile(),"build-tools/templates/webstart-template.jnlp" );
        FileUtils.filter( JNLP_TEMPLATE, getDestFile( phetProject ), createJNLPFilterMap( simulation, phetProject ), "UTF-16" );
    }

    private String getJNLPFileName() {
        String localeSuffix = locale.equals( "en" ) ? "" : "_" + locale;
        return "" + simulationName + localeSuffix + ".jnlp";
    }

    private File getDestFile( PhetProject phetProject ) {
        return new File( phetProject.getDeployDir(), getJNLPFileName() );
    }

    private HashMap createJNLPFilterMap( Simulation simulation, PhetProject phetProject ) {
        HashMap map = new HashMap();
        map.put( "PROJECT.NAME", StringEscapeUtils.escapeHtml( simulation.getTitle() ) );
        map.put( "JNLP.NAME", getJNLPFileName() );
        map.put( "PROJECT.DESCRIPTION", StringEscapeUtils.escapeHtml( simulation.getDescription() ) );
        map.put( "PROJECT.JAR", phetProject.getJarFile().getName() );
        map.put( "PROJECT.SCREENSHOT", "http://phet.colorado.edu/Design/Assets/images/Phet-Kavli-logo.jpg" );//todo: map this to correct sim-specific (possibly online) URL
        map.put( "PROJECT.MAINCLASS", simulation.getMainclass() );
        map.put( "PROJECT.ARGS", toJNLPArgs( getArgs( simulation ) ) );
        map.put( "PROJECT.PROPERTIES", getJNLPProperties() );
        map.put( "PROJECT.DEPLOY.PATH", deployUrl );
        echo( "JNLP filter map:\n" + map );
        return map;
    }

    private String[] getArgs( Simulation simulation ) {
        ArrayList args = new ArrayList( Arrays.asList( simulation.getArgs() ) );

        //optionally add a -dev parameter if this simulation is deployed to dev directory 
        String property = getOwningTarget() != null ? getOwningTarget().getProject().getProperty( "deploy.to.dev" ) : null;

        //todo: rewrite/remove this if clause
        if ( property != null && property.equalsIgnoreCase( "true" ) ) {
            //todo: should use the constant for this arg from phetcommon
            args.add( "-dev" );
        }


        if ( dev ) {
            if ( !args.contains( "-dev" ) ) {
                args.add( "-dev" );
            }
        }
        return (String[]) args.toArray( new String[0] );
    }

    private String getJNLPProperties() {//todo: locale support
        String properties = "";
        //explicitly request English for the default JNLP file
        properties += "<property name=\"javaws.phet.locale\" value=\"" + ( locale == null ? "en" : locale ) + "\" />\n"; //XXX #1057, backward compatibility, delete after IOM
        properties += "<property name=\"javaws.user.language\" value=\"" + ( locale == null ? "en" : locale ) + "\" />";
        return properties;
    }
    
    private String toJNLPArgs( String[] args ) {
        String string = "";
        for ( int i = 0; i < args.length; i++ ) {
            string += "<argument>" + args[i] + "</argument>\n";
        }
        return string;
    }

    public void setSimulation( String simulation ) {
        this.simulationName = simulation;
    }

    /**
     * Sets the 2-character locale code to be used for reading title and description for jnlp file.
     *
     * @param locale
     */
    public void setLocale( String locale ) {//todo: not supported yet
        this.locale = locale;
    }

    public void setDeployUrl( String deployUrl ) {
        this.deployUrl = deployUrl;

        echo( "Setting deploy URL to " + deployUrl );
    }

    public static void buildJNLPForSimAndLanguage( PhetProject project, String language ) throws Exception {
        for ( int i = 0; i < project.getSimulationNames().length; i++ ) {
            PhetBuildJnlpTask phetBuildJnlpTask = new PhetBuildJnlpTask();
            phetBuildJnlpTask.setSimulation( project.getSimulationNames()[i] );
            phetBuildJnlpTask.setDeployUrl( PhetServer.PRODUCTION.getWebDeployURL( project ) );
            phetBuildJnlpTask.setLocale( language );
            phetBuildJnlpTask.executeImpl( project );
        }
    }

    public void setDev( boolean dev ) {
        this.dev = dev;
    }
}
