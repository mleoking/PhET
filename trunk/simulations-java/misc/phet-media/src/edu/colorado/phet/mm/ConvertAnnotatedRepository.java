package edu.colorado.phet.mm;

import edu.colorado.phet.mm.util.FileCopy;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Author: Sam Reid
 * Jun 15, 2007, 12:15:36 AM
 */
public class ConvertAnnotatedRepository {
    public static void main( String[] args ) throws IOException {
        ArrayList list = ImageFinder.getAnnotatedImageEntries();

        convertRepository( list );
    }

    private static void convertRepository( ArrayList list ) throws IOException {
        for( int i = 0; i < list.size(); i++ ) {
            ImageEntry entry = (ImageEntry)list.get( i );
            File file = entry.getFile();
            File dst = getDestFile( file );
            FileCopy.copy( file, dst );
            Properties properties = entry.getProperties();
            properties.store( new FileOutputStream( new File( dst.getAbsolutePath() + ".properties" ) ), null );
        }
    }

    private static File getDestFile( File file ) {
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
            ImageEntry entry = new ImageEntry( prop );
            imageEntries.add( entry );
        }
        return (ImageEntry[])imageEntries.toArray( new ImageEntry[0] );
    }
}
