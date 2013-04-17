package edu.colorado.phet.licensing;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import edu.colorado.phet.buildtools.PhetProject;

/**
 * Created by: Sam
 * Aug 4, 2008 at 7:10:23 PM
 */
public class DataProcessor {

    public AnnotatedFile[] visitDirectory( PhetProject phetProject, File dir ) {
        ArrayList<AnnotatedFile> annotatedFiles = new ArrayList<AnnotatedFile>();
        File licenseFile = new File( dir, "license.txt" );
        ResourceAnnotationList resourceAnnotationList;
        if ( licenseFile.exists() ) {
            resourceAnnotationList = ResourceAnnotationList.read( licenseFile );
        }
        else {
            resourceAnnotationList = new ResourceAnnotationList();
            resourceAnnotationList.addTextLine( new ResourceAnnotationTextLine( "###################################" ) );
            resourceAnnotationList.addTextLine( new ResourceAnnotationTextLine( "# License info for " + phetProject.getName() ) );
            resourceAnnotationList.addTextLine( new ResourceAnnotationTextLine( "# Automatically generated on " + new Date() ) );
            resourceAnnotationList.addTextLine( new ResourceAnnotationTextLine( "###################################" ) );
            resourceAnnotationList.addTextLine( new ResourceAnnotationTextLine( "" ) );
        }

        File[] f = dir.listFiles();
        for ( int i = 0; f != null && i < f.length; i++ ) {
            File file = f[i];
            if ( file.isDirectory() ) {
                if ( !ignoreDirectory( file ) ) {
                    AnnotatedFile[] annotatedFile = visitDirectory( phetProject, file );
                    annotatedFiles.addAll( Arrays.asList( annotatedFile ) );
                }
            }
            else {
                if ( !ignoreFile( phetProject, file ) ) {
                    ResourceAnnotation annotatedFile = visitFile( resourceAnnotationList, file );
                    annotatedFiles.add( new AnnotatedFile( file, annotatedFile ) );
                }
            }
        }
        if ( resourceAnnotationList.getAnnotationCount() > 0 ) {
            if ( !licenseFile.exists() ) {
                resourceAnnotationList.save( new File( dir, "license.txt" ) );
                System.out.println( "Wrote " + licenseFile.getAbsolutePath() );
                System.out.println( resourceAnnotationList.toText() );
            }
        }
        return annotatedFiles.toArray( new AnnotatedFile[annotatedFiles.size()] );
    }

    protected ResourceAnnotation visitFile( ResourceAnnotationList resourceAnnotationList, File file ) {
        return resourceAnnotationList.getEntry( file.getName() );
    }

    private boolean ignoreFile( PhetProject project, File file ) {
        return file.getName().equals( project.getName() + ".properties" ) || file.getName().equalsIgnoreCase( "license.txt" ) || file.getName().equalsIgnoreCase( "license-orig.txt" ) || file.getName().equalsIgnoreCase( "sun-license.txt" );
    }

    private boolean ignoreDirectory( File dir ) {
        return dir.getName().equalsIgnoreCase( ".svn" ) || dir.getName().equalsIgnoreCase( "localization" );
    }
}