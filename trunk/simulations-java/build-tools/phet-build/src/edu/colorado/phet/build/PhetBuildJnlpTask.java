/* Copyright 2007, University of Colorado */
package edu.colorado.phet.build;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang.StringEscapeUtils;

import edu.colorado.phet.build.util.FileUtils;
//todo: needs better error handling for loading flavors
//todo: test support for deploying with online url

//todo: see other todos below
public class PhetBuildJnlpTask extends AbstractPhetBuildTask {
    private static final File JNLP_TEMPLATE = new File( "build-tools/phet-build/templates/webstart-template.jnlp" );

    private volatile String flavorName;
    private volatile String deployUrl;
    private volatile String locale = "en";

    protected void executeImpl( PhetProject phetProject ) throws Exception {
        if ( flavorName == null ) {
            flavorName = phetProject.getFlavorNames()[0];
        }
        PhetProjectFlavor flavor = phetProject.getFlavor( flavorName, locale );
        echo( "loaded flavor=" + flavor );
        if ( deployUrl == null ) {
            deployUrl = phetProject.getDefaultDeployDir().toURL().toString();

            echo( "Deploy url is null -- using " + deployUrl );
        }
        FileUtils.filter( JNLP_TEMPLATE, getDestFile( phetProject ), createJNLPFilterMap( flavor, phetProject ), "UTF-16" );
    }

    private String getJNLPFileName() {
        String localeSuffix = locale.equals( "en" ) ? "" : "_" + locale;
        return "" + flavorName + localeSuffix + ".jnlp";
    }

    private File getDestFile( PhetProject phetProject ) {
        return new File( phetProject.getDefaultDeployDir(), getJNLPFileName() );
    }

    private HashMap createJNLPFilterMap( PhetProjectFlavor flavor, PhetProject phetProject ) {
        HashMap map = new HashMap();
        map.put( "PROJECT.NAME", StringEscapeUtils.escapeHtml( flavor.getTitle() ) );
        map.put( "JNLP.NAME", getJNLPFileName() );
        map.put( "PROJECT.DESCRIPTION", StringEscapeUtils.escapeHtml( flavor.getDescription() ) );
        map.put( "PROJECT.JAR", phetProject.getJarFile().getName() );
        map.put( "PROJECT.SCREENSHOT", "http://phet.colorado.edu/Design/Assets/images/Phet-Kavli-logo.jpg" );//todo: map this to correct sim-specific (possibly online) URL
        map.put( "PROJECT.MAINCLASS", flavor.getMainclass() );
        map.put( "PROJECT.ARGS", toJNLPArgs( getArgs( flavor ) ) );
        map.put( "PROJECT.PROPERTIES", getJNLPProperties() );
        map.put( "PROJECT.DEPLOY.PATH", deployUrl );
        echo( "JNLP filter map:\n" + map );
        return map;
    }

    private String[] getArgs( PhetProjectFlavor flavor ) {
        ArrayList args = new ArrayList( Arrays.asList( flavor.getArgs() ) );

        //optionally add a -dev parameter if this simulation is deployed to dev directory 
        String property = getOwningTarget() != null ? getOwningTarget().getProject().getProperty( "deploy.to.dev" ) : null;
        if ( property != null && property.equalsIgnoreCase( "true" ) ) {
            //todo: should use the constant for this arg from phetcommon
            args.add( "-dev" );
        }
        return (String[]) args.toArray( new String[0] );
    }

    private String getJNLPProperties() {//todo: locale support
        String properties = "";
        //explicitly request english for the default JNLP file
        properties += "<property name=\"javaws.phet.locale\" value=\"" + ( locale == null ? "en" : locale ) + "\" />";
        return properties;
    }

    private String toJNLPArgs( String[] args ) {
        String string = "";
        for ( int i = 0; i < args.length; i++ ) {
            string += "<argument>" + args[i] + "</argument>\n";
        }
        return string;
    }

    public void setFlavor( String flavorName ) {
        this.flavorName = flavorName;
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
        for ( int i = 0; i < project.getFlavorNames().length; i++ ) {
            PhetBuildJnlpTask phetBuildJnlpTask = new PhetBuildJnlpTask();
            phetBuildJnlpTask.setFlavor( project.getFlavorNames()[i] );
            phetBuildJnlpTask.setDeployUrl( "http://phet.colorado.edu/sims/" + project.getName() );
            phetBuildJnlpTask.setLocale( language );
            phetBuildJnlpTask.executeImpl( project );
        }
    }

}
