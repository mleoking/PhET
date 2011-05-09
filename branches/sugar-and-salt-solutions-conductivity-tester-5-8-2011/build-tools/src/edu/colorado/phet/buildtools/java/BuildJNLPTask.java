/* Copyright 2007, University of Colorado */
package edu.colorado.phet.buildtools.java;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;

import edu.colorado.phet.buildtools.BuildToolsPaths;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.Simulation;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.common.phetcommon.PhetCommonConstants;

/*
 * Do not change this task without changing the WebsiteTranslationDeployPublisher, which makes
  * assumptions about the structure of the generated JNLP files. 
 */
public class BuildJNLPTask {

    protected void buildJNLP( JavaProject project, String simulationName, Locale locale, boolean dev, String codebase ) throws Exception {
        buildJNLP( project, simulationName, locale, dev, codebase, "" );
    }

    protected void buildJNLP( JavaProject project, String simulationName, Locale locale, boolean dev, String codebase, String suffix ) throws Exception {
        Simulation simulation = project.getSimulation( simulationName, locale );
        File JNLP_TEMPLATE = new File( project.getTrunk(), BuildToolsPaths.WEBSTART_TEMPLATE );
        FileUtils.filter( JNLP_TEMPLATE, getDestFile( project, simulationName, locale, suffix ), createJNLPFilterMap( simulation, project, simulationName, locale, codebase, dev, suffix ), "UTF-16" );
    }

    private String getJNLPFileName( String simulationName, Locale locale, String suffix ) {
        return "" + simulationName + "_" + locale + suffix + ".jnlp";
    }

    private File getDestFile( PhetProject phetProject, String simulationName, Locale locale, String suffix ) {
        return new File( phetProject.getDeployDir(), getJNLPFileName( simulationName, locale, suffix ) );
    }

    private HashMap createJNLPFilterMap( Simulation simulation, JavaProject project, String simulationName, Locale locale, String codebase, boolean dev, String suffix ) {
        HashMap map = new HashMap();
        map.put( "PROJECT.NAME", StringEscapeUtils.escapeHtml( simulation.getTitle() ) );
        map.put( "JNLP.NAME", getJNLPFileName( simulationName, locale, suffix ) );
        map.put( "PROJECT.JAR", project.getJarFile().getName() );
        map.put( "PROJECT.SCREENSHOT", "http://phet.colorado.edu/Design/Assets/images/Phet-Kavli-logo.jpg" );//TODO: map this to correct sim-specific (possibly online) URL
        map.put( "PROJECT.MAINCLASS", simulation.getMainclass() );
        map.put( "PROJECT.ARGS", toJNLPArgs( getArgs( simulation, dev ) ) );
        map.put( "PROJECT.PROPERTIES", getJNLPProperties( locale ) );
        map.put( "PROJECT.DEPLOY.PATH", codebase );
        map.put( "SECURITY", getSecurity( project ) );
        return map;
    }

    private String getSecurity( PhetProject phetProject ) {
        if ( phetProject.requestAllPermissions() ) {
            return "<security>\n" +
                   "      <all-permissions/>\n" +
                   "</security>";
        }
        else {
            return "";
        }
    }

    private String[] getArgs( Simulation simulation, boolean dev ) {
        ArrayList args = new ArrayList( Arrays.asList( simulation.getArgs() ) );
        if ( dev ) {
            if ( !args.contains( "-dev" ) ) {
                args.add( "-dev" );
            }
        }
        return (String[]) args.toArray( new String[args.size()] );
    }

    private String getJNLPProperties( Locale locale ) {//TODO: locale support
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

}
