package edu.colorado.phet.licensing;

import java.io.File;

import edu.colorado.phet.build.PhetProject;

/**
 * Created by: Sam
 * Aug 4, 2008 at 7:10:23 PM
 */
public class OutputLicenseInfo extends ProcessData {

    protected ResourceAnnotation visitFile( PhetProject phetProject, ResourceAnnotationList resourceAnnotationList, File file ) {
        return resourceAnnotationList.getEntry( file.getName() );
    }

    public static boolean hideEntry( ResourceAnnotation entry ) {
        return entry.getAuthor() != null && entry.getAuthor().equalsIgnoreCase( "phet" )
               ||
               entry.getSource() != null && entry.getSource().toLowerCase().startsWith( "microsoft" )
               ||
               entry.getLicense() != null && entry.getLicense().equalsIgnoreCase( "PUBLIC DOMAIN" )
               ||
               entry.getSource() != null && entry.getSource().equalsIgnoreCase( "java" );
    }

}