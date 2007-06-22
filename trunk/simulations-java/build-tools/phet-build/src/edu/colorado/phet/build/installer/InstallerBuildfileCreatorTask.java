/* Copyright 2007, University of Colorado */
package edu.colorado.phet.build.installer;

import edu.colorado.phet.build.AbstractPhetBuildTask;
import edu.colorado.phet.build.FileUtils;
import edu.colorado.phet.build.PhetProject;
import edu.colorado.phet.build.PhetProjectFlavor;
import org.apache.tools.ant.BuildException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class InstallerBuildfileCreatorTask extends AbstractPhetBuildTask {
    private String flavor;
    private File inputFile;
    private File outputFile;

    public void executeImpl( PhetProject project ) throws BuildException {
        if( flavor == null ) {
            flavor = project.getFlavorNames()[0];
        }
        if( inputFile == null || outputFile == null ) {
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

    private String alphaNumeric(String input) {
        return input.replaceAll( "[^\\w\\d]", "" );
    }

    private HashMap createFilterMap( PhetProject phetProject ) {
        PhetProjectFlavor projectFlavor = phetProject.getFlavor( flavor );
        HashMap map = new HashMap();
        map.put( "FLAVORDIR", alphaNumeric(flavor) );
        map.put( "FLAVOR", projectFlavor.getTitle() );
        map.put( "DESCRIPTION", projectFlavor.getDescription() );
        map.put( "JAR", phetProject.getJarFile().getAbsolutePath() );
        map.put( "CLASSNAME", projectFlavor.getMainclass() );
        map.put( "ARGUMENTS", getArgs( projectFlavor ) );
        map.put( "INSTALLER-DATA-DIR", getProject().getBaseDir( )+"/build-tools/phet-build/installer-data/");
        //map.put( "PROJECT.PROPERTIES", getJNLPProperties() );
        //map.put( "PROJECT.DEPLOY.PATH", deployUrl );
        return map;
    }

    private String getArgs( PhetProjectFlavor projectFlavor ) {
        String[] args = projectFlavor.getArgs();
        StringBuffer buffer = new StringBuffer();
        for( int i = 0; i < args.length; i++ ) {
            buffer.append( args[i] );
            if( i < args.length - 1 ) {
                buffer.append( " " );
            }
        }
        return buffer.toString();
    }
}
