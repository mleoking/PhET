/* Copyright 2007, University of Colorado */
package edu.colorado.phet.build;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.tools.ant.BuildException;

import edu.colorado.phet.build.AbstractPhetBuildTask;
import edu.colorado.phet.build.FileUtils;
import edu.colorado.phet.build.PhetProject;
import edu.colorado.phet.build.PhetProjectFlavor;

public class InstallerBuildfileCreatorTask extends AbstractPhetBuildTask {
    private String flavor;
    private File inputFile;
    private File outputFile;

    public void executeImpl( PhetProject project ) throws BuildException {
        if ( flavor == null ) {
            flavor = project.getFlavorNames()[0];
            echo( "Flavor was null, using default flavor: " + flavor );
        }
        if ( inputFile == null || outputFile == null ) {
            throw new BuildException( "File not specified, inputFile=" + inputFile + ", outputFile=" + outputFile );
        }
        try {
            FileUtils.filter( inputFile, outputFile, createFilterMap( project ) );
        }
        catch( IOException e ) {
            throw new BuildException( e );
        }
    }

    public void setFlavor( String flavor ) {
        this.flavor = flavor;
    }

    public void setInputFile( File inputFile ) {
        this.inputFile = inputFile;
    }

    public void setOutputFile( File outputFile ) {
        this.outputFile = outputFile;
    }

    private String alphaNumeric( String input ) {
        return input.replaceAll( "[^\\w\\d]", "" );
    }

    private HashMap createFilterMap( PhetProject phetProject ) {
        PhetProjectFlavor projectFlavor = phetProject.getFlavor( flavor );
        HashMap map = new HashMap();
        map.put( "FLAVORDIR", alphaNumeric( flavor ) );
        map.put( "FLAVOR", projectFlavor.getTitle() );
        map.put( "DESCRIPTION", projectFlavor.getDescription() );
//        map.put( "JAR", format( phetProject.getJarFile().getAbsolutePath()) );
        map.put( "JAR", format( phetProject.getDefaultDeployJar().getAbsolutePath() ) );
        map.put( "JAR_FILENAME", phetProject.getJarFile().getName() );//JAR_FILENAME may contain dashes & spaces, unlike ${FLAVORDIR}.jar.  We may wish to rewwrite this to copy JAR_FILENAME to ${FLAVORDIR}.jar.
        map.put( "CLASSNAME", projectFlavor.getMainclass() );
        map.put( "ARGUMENTS", getArgs( projectFlavor ) );
//        map.put("WINDOWS_ICON_PATH",${ant-output}/projects/${sim.name}/${sim.flavor}.ico)
        map.put( "WINDOWS_ICON_PATH", format( new File( getProject().getProperty( "ant-output" ) + "/projects/" + phetProject.getName() + "/" + flavor + ".ico" ).getAbsolutePath() ) );
//        System.out.println( "getProject().getBaseDir() = " + getProject().getBaseDir() );
//        System.out.println( "getProject().getBaseDir().getAbsolutePath() = " + getProject().getBaseDir().getAbsolutePath() );
        map.put( "INSTALLER-DATA-DIR", format( getProject().getBaseDir().getAbsolutePath() ) + "/build-tools/phet-build/installer-data/" );
        map.put( "OUTFILE", format( new File( phetProject.getDefaultDeployDir(), flavor + ".exe" ).getAbsolutePath() ) );
        //map.put( "PROJECT.PROPERTIES", getJNLPProperties() );
        //map.put( "PROJECT.DEPLOY.PATH", deployUrl );
        return map;
    }

    /*
     * Paths are generated incorrectly when generating this under windows, for example:
     *   <licenseFile>C:phetsubversiontrunksimulations-java/build-tools/phet-build/installer-data/license.txt</licenseFile>
     *
     * This solution is a workaround; I'm not sure what is the root of the problem.
     */
    private String format( String dir ) {
        return dir.replace( '\\', '/' );
    }

    private String getArgs( PhetProjectFlavor projectFlavor ) {
        String[] args = projectFlavor.getArgs();
        StringBuffer buffer = new StringBuffer();
        for ( int i = 0; i < args.length; i++ ) {
            buffer.append( args[i] );
            if ( i < args.length - 1 ) {
                buffer.append( " " );
            }
        }
        return buffer.toString();
    }

    public static void main( String[] args ) throws IOException {
        PhetProject phetProject = new PhetProject( new File( "C:\\phet\\subversion\\trunk\\simulations-java\\simulations\\balloons" ) );
        System.out.println( "phetProject = " + phetProject );

        InstallerBuildfileCreatorTask installerBuildfileCreatorTask = new InstallerBuildfileCreatorTask();
        installerBuildfileCreatorTask.setFlavor( "balloons" );
        HashMap map = installerBuildfileCreatorTask.createFilterMap( phetProject );
        System.out.println( "map = " + map );
    }
}
