package edu.colorado.phet.media;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Author: Sam Reid
 * Jun 15, 2007, 12:15:36 AM
 */
public class ConvertAnnotatedRepository {

    public static void storeProperties( ImageEntry entry, String imageName ) {
        try {
            entry.toProperties().store( new FileOutputStream( getPropertiesFile( imageName ) ), null );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public static File getPropertiesFile( String dst ) {
        return new File( "annotated-data", dst + ".properties" );
    }

    public static File createNewRepositoryFile( File file ) {
        File dst = new File( "annotated-data", file.getName() );
        if ( !dst.exists() ) {
            return dst;
        }
        else {
            for ( int i = 0; i < Integer.MAX_VALUE; i++ ) {
                File try2 = new File( "annotated-data", "" + i + "_" + file.getName() );
                if ( !try2.exists() ) {
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
        for ( int i = 0; i < f.length; i++ ) {
            File file = f[i];
//            System.out.println( "file.getAbsolutePath() = " + file.getAbsolutePath() );
            Properties prop = new Properties();
            prop.load( new FileInputStream( file ) );
            ImageEntry entry = new ImageEntry( prop, file.getName().substring( 0, file.getName().length() - ".properties".length() ) );
            imageEntries.add( entry );
        }
        return (ImageEntry[]) imageEntries.toArray( new ImageEntry[imageEntries.size()] );
    }

    public static ImageEntry[] getNonPhetEntries() throws IOException {
        ImageEntry[] entries = loadAnnotatedEntries();
        ArrayList nonPhet = new ArrayList();
        for ( int i = 0; i < entries.length; i++ ) {
            ImageEntry entry = entries[i];
            if ( entry.isNonPhet() ) {
                nonPhet.add( entry );
            }
        }
        return (ImageEntry[]) nonPhet.toArray( new ImageEntry[nonPhet.size()] );
    }
}
