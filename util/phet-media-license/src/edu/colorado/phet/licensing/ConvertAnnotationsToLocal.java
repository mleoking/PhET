package edu.colorado.phet.licensing;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import edu.colorado.phet.build.PhetProject;
import edu.colorado.phet.licensing.media.ImageEntry;

/**
 * Created by: Sam
 * Aug 4, 2008 at 7:10:23 PM
 */
public class ConvertAnnotationsToLocal {
    private File trunk;

    public static void main( String[] args ) throws IOException {
        new ConvertAnnotationsToLocal().start();
    }

    private void start() throws IOException {
        trunk = new File( "C:\\reid-not-backed-up\\phet\\svn\\trunk2" );

        System.out.println( "PhET Java Software Dependencies\n" +
                            "" + new Date() + "\n" );

        File baseDir = new File( trunk, "simulations-java" );
        String[] simNames = PhetProject.getSimNames( baseDir );
        for ( int i = 0; i < simNames.length; i++ ) {
            String simName = simNames[i];
//            System.out.println( "name=" + simName );
            visitSim( simName );
        }
    }

    private void visitSim( String simName ) throws IOException {
        System.out.println( simName + ":" );
        PhetProject phetProject = new PhetProject( new File( trunk, "simulations-java/simulations/" + simName ) );

        visitDirectory( phetProject, phetProject.getDataDirectory() );
    }

    private void visitDirectory( PhetProject phetProject, File dir ) {
        ResourceAnnotationList resourceAnnotationList = new ResourceAnnotationList();
        resourceAnnotationList.addTextLine( new ResourceAnnotationTextLine( "###################################" ) );
        resourceAnnotationList.addTextLine( new ResourceAnnotationTextLine( "# License info for " + phetProject.getName() ) );
        resourceAnnotationList.addTextLine( new ResourceAnnotationTextLine( "# Automatically generated on " + new Date() ) );
        resourceAnnotationList.addTextLine( new ResourceAnnotationTextLine( "###################################" ) );
        resourceAnnotationList.addTextLine( new ResourceAnnotationTextLine( "" ) );
        File[] f = dir.listFiles();
        for ( int i = 0; i < f.length; i++ ) {
            File file = f[i];
            if ( file.isDirectory() ) {
                if ( !ignoreDirectory( phetProject, file ) ) {
                    visitDirectory( phetProject, file );
                }
            }
            else {
                if ( !ignoreFile( phetProject, file ) ) {
                    resourceAnnotationList.addResourceAnnotation( createResourceAnnotation( phetProject, file ) );
                }
            }
        }
        if ( resourceAnnotationList.getAnnotationCount() > 0 ) {
            System.out.println( resourceAnnotationList.toText() );
        }
    }

    private ResourceAnnotation createResourceAnnotation( PhetProject phetProject, File file ) {
        ResourceAnnotation resourceAnnotation = new ResourceAnnotation( file.getName() );
//        resourceAnnotation.setNotes( "auto-generated" );
        File annotatedFile = new File( "C:\\reid-not-backed-up\\phet\\svn\\trunk2\\util\\phet-media-license\\annotated-data", file.getName() );
        if ( annotatedFile.exists() ) {
            ImageEntry imageEntry = new ImageEntry( annotatedFile );
            if ( imageEntry.getSource() != null && imageEntry.getSource().trim().length() > 0 && !imageEntry.getSource().trim().equals( "?" ) ) {
                resourceAnnotation.setSource( imageEntry.getSource() );
            }
            if ( !imageEntry.isNonPhet() ) {
                resourceAnnotation.setAuthor( "PhET" );
            }
            if ( imageEntry.getNotes() != null && imageEntry.getNotes().trim().length() > 0 && !imageEntry.getNotes().trim().equals( "?" ) ) {
                resourceAnnotation.setNotes( imageEntry.getNotes() );
            }
        }
        return resourceAnnotation;
    }

    private boolean ignoreFile( PhetProject project, File file ) {
        return file.getName().equals( project.getName() + ".properties" );
    }

    private boolean ignoreDirectory( PhetProject project, File dir ) {
        return dir.getName().equalsIgnoreCase( ".svn" ) || dir.getName().equalsIgnoreCase( "localization" );
    }
}