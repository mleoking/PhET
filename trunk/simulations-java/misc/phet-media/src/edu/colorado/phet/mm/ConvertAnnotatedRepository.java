package edu.colorado.phet.mm;

import edu.colorado.phet.mm.util.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Author: Sam Reid
 * Jun 15, 2007, 12:15:36 AM
 */
public class ConvertAnnotatedRepository {

    public static void storeProperties(ImageEntry entry,File repositoryFile){
        try {
            entry.getProperties().store( new FileOutputStream( getPropertiesFile( repositoryFile )),null );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
    public static File getPropertiesFile( File dst ) {
        return new File( dst.getAbsolutePath() + ".properties" );
    }

    public static File createNewRepositoryFile( File file ) {
        File dst = new File( "annotated-data", file.getName() );
        if( !dst.exists() ) {
            return dst;
        }
        else {
            for( int i = 0; i < Integer.MAX_VALUE; i++ ) {
                File try2 = new File( "annotated-data", "" + i + "_" + file.getName() );
                if( !try2.exists() ) {
                    return try2;
                }
            }
        }
        throw new RuntimeException( "couldn't find suitable dest file" );
    }

    public static ImageEntry[] loadAnnotatedEntries() throws IOException {
        File dir = new File( "annotated-data" );
        File[] f = dir.listFiles( new FilenameFilter() {
            public boolean accept( File dir, String name ) {
                return name.toLowerCase().endsWith( ".properties" );
            }
        } );
        ArrayList imageEntries = new ArrayList();
        for( int i = 0; i < f.length; i++ ) {
            File file = f[i];

            Properties prop = new Properties();
            prop.load( new FileInputStream( file ) );
            ImageEntry entry = new ImageEntry( prop,file );
            imageEntries.add( entry );
        }
        return (ImageEntry[])imageEntries.toArray( new ImageEntry[0] );
    }
}
