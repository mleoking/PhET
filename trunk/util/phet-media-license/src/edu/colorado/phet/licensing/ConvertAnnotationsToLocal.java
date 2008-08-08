package edu.colorado.phet.licensing;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.build.PhetProject;
import edu.colorado.phet.licensing.media.ImageEntry;

/**
 * Created by: Sam
 * Aug 4, 2008 at 7:10:23 PM
 */
public class ConvertAnnotationsToLocal extends ProcessData {

    protected ResourceAnnotation visitFile( PhetProject phetProject, ResourceAnnotationList resourceAnnotationList, File file ) {
        ResourceAnnotation annotation = createResourceAnnotation( phetProject, file );
        resourceAnnotationList.addResourceAnnotation( annotation );
        return annotation;
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


    public static void main( String[] args ) throws IOException {
        new ConvertAnnotationsToLocal().start();
    }

}