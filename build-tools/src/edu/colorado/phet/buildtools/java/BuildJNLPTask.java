/* Copyright 2007, University of Colorado */
package edu.colorado.phet.buildtools.java;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;

import edu.colorado.phet.buildtools.AbstractPhetBuildTask;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.PhetServer;
import edu.colorado.phet.buildtools.Simulation;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.common.phetcommon.PhetCommonConstants;

//TODO: see other TODOs below
public class BuildJNLPTask extends AbstractPhetBuildTask {


    private volatile String simulationName;
    private volatile String deployUrl;
    private volatile Locale locale = new Locale( "en" );
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

        final File JNLP_TEMPLATE = new File( phetProject.getProjectDir().getParentFile().getParentFile().getParentFile(), "build-tools/templates/webstart-template.jnlp" );
        FileUtils.filter( JNLP_TEMPLATE, getDestFile( phetProject ), createJNLPFilterMap( simulation, phetProject ), "UTF-16" );
    }

    private String getJNLPFileName() {
        return "" + simulationName + "_" + locale + ".jnlp";
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
        map.put( "PROJECT.SCREENSHOT", "http://phet.colorado.edu/Design/Assets/images/Phet-Kavli-logo.jpg" );//TODO: map this to correct sim-specific (possibly online) URL
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

        //TODO: rewrite/remove this if clause
        if ( property != null && property.equalsIgnoreCase( "true" ) ) {
            //TODO: should use the constant for this arg from phetcommon
            args.add( "-dev" );
        }


        if ( dev ) {
            if ( !args.contains( "-dev" ) ) {
                args.add( "-dev" );
            }
        }
        return (String[]) args.toArray( new String[args.size()] );
    }

    private String getJNLPProperties() {//TODO: locale support
        String properties = "";
        //explicitly request English for the default JNLP file

        //XXX #1057, backward compatibility, delete after IOM
        properties += "<property name=\"javaws.phet.locale\" value=\"" + locale.getLanguage() + "\" />\n";

        properties += "<property name=\"" + PhetCommonConstants.PROPERTY_PHET_LANGUAGE + "\" value=\"" + locale.getLanguage() + "\" />";
        if ( locale.getCountry() != null && locale.getCountry().trim().length() > 0 ) {
            properties += "<property name=\"" + PhetCommonConstants.PROPERTY_PHET_COUNTRY + "\" value=\"" + locale.getCountry() + "\" />";
        }
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
    public void setLocale( Locale locale ) {
        assert locale != null;
        this.locale = locale;
    }

    public void setDeployUrl( String deployUrl ) {
        this.deployUrl = deployUrl;

        echo( "Setting deploy URL to " + deployUrl );
    }

    public static void buildJNLPForSimAndLocale( PhetProject project, Locale locale ) throws Exception {
        for ( int i = 0; i < project.getSimulationNames().length; i++ ) {
            BuildJNLPTask phetBuildJnlpTask = new BuildJNLPTask();
            phetBuildJnlpTask.setSimulation( project.getSimulationNames()[i] );
            phetBuildJnlpTask.setDeployUrl( PhetServer.PRODUCTION.getWebDeployURL( project ) );
            phetBuildJnlpTask.setLocale( locale );
            phetBuildJnlpTask.executeImpl( project );
        }
    }

    public void setDev( boolean dev ) {
        this.dev = dev;
    }
}
