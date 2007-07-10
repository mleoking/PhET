/* Copyright 2007, University of Colorado */
package edu.colorado.phet.build;

import java.io.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
//todo: needs better error handling for loading flavors
//todo: test support for deploying with online url

//todo: see other todos below
public class PhetBuildJnlpTask extends AbstractPhetBuildTask {
    private static final File JNLP_TEMPLATE = new File( "build-tools/phet-build/templates/webstart-template.jnlp" );

    private volatile String flavorName;
    private volatile String deployUrl;
    private volatile String locale = "en";

    protected void executeImpl( PhetProject phetProject ) throws Exception {
        if( flavorName == null ) {
            flavorName = phetProject.getFlavorNames()[0];
        }
        PhetProjectFlavor flavor = phetProject.getFlavor( flavorName);
        echo( "loaded flavor=" + flavor );
        if( deployUrl == null ) {
            deployUrl = phetProject.getDefaultDeployDir().toURL().toString();
        }
        FileUtils.filter(JNLP_TEMPLATE,getDestFile( phetProject ),createJNLPFilterMap( flavor, phetProject ) ,"UTF-16");
    }

    private String getJNLPFileName() {
        System.out.println( "locale = " + locale );
        String localeSuffix = locale.equals( "en" ) ? "" : "_" + locale;
        return "" + flavorName + localeSuffix + ".jnlp";
    }

    private File getDestFile( PhetProject phetProject ) {
        return new File( phetProject.getDefaultDeployDir(), getJNLPFileName() );
    }

    private HashMap createJNLPFilterMap( PhetProjectFlavor flavor, PhetProject phetProject ) {
        HashMap map = new HashMap();
        map.put( "PROJECT.NAME", flavor.getTitle() );
        map.put( "JNLP.NAME", getJNLPFileName() );
        map.put( "PROJECT.DESCRIPTION", flavor.getDescription() );
        map.put( "PROJECT.JAR", phetProject.getJarFile().getName() );
        map.put( "PROJECT.SCREENSHOT", "http://phet.colorado.edu/Design/Assets/images/Phet-Kavli-logo.jpg" );//todo: map this to correct sim-specific (possibly online) URL
        map.put( "PROJECT.MAINCLASS", flavor.getMainclass() );
        map.put( "PROJECT.ARGS", toJNLPArgs( getArgs(flavor ) ) );
        map.put( "PROJECT.PROPERTIES", getJNLPProperties() );
        map.put( "PROJECT.DEPLOY.PATH", deployUrl );
        return map;
    }

    private String[] getArgs( PhetProjectFlavor flavor ) {
        ArrayList args=new ArrayList( Arrays.asList( flavor.getArgs()));

        //optionally add a -dev parameter if this simulation is deployed to dev directory 
        String property = getOwningTarget().getProject().getProperty( "deploy.to.dev" );
        if ( property!=null&&property.equalsIgnoreCase( "true")){
            args.add("-dev");
        }
        return (String[])args.toArray(new String[0]);
    }

    private String getJNLPProperties() {//todo: locale support
        String properties = "";
        if( locale != null && !locale.equals( "en" ) ) {
            properties += "<property name=\"javaws.phet.locale\" value=\"" + locale + "\" />";
        }
        return properties;
    }

    private String toJNLPArgs( String[] args ) {
        String string = "";
        for( int i = 0; i < args.length; i++ ) {
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
    }

    public static void main( String[] args ) throws Exception {
        System.out.println( new File( "." ).getAbsolutePath() );

        PhetProject phetProject = new PhetProject( new File( "C:\\phet\\subversion\\trunk\\simulations-java\\simulations\\ideal-gas" ) );


        String[] idealGasFlavors = phetProject.getFlavorNames();
        for( int i = 0; i < idealGasFlavors.length; i++ ) {
            PhetBuildJnlpTask phetBuildJnlpTask = new PhetBuildJnlpTask();
//        phetBuildJnlpTask.setFlavor( "cck-ac" );
//        phetBuildJnlpTask.setLocale( "fr" );
            phetBuildJnlpTask.setFlavor( idealGasFlavors[i] );
            phetBuildJnlpTask.setDeployUrl( "http://phet-web.colorado.edu/simulations/gasses-buoyancy" );
            phetBuildJnlpTask.executeImpl( phetProject );

            phetBuildJnlpTask.setLocale( "es" );
            phetBuildJnlpTask.executeImpl( phetProject );
        }
    }
}
