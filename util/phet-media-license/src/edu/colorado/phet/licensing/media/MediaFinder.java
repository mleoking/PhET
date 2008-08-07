package edu.colorado.phet.licensing.media;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.licensing.media.FileUtils;

/**
 * Author: Sam Reid
 * Jun 14, 2007, 8:35:26 PM
 */
public class MediaFinder {
    private static final String SVN_WORKING_COPY_ROOT = "C:\\reid-not-backed-up\\phet\\svn\\trunk2";

    public static File[] getDataDirectories() {
        ArrayList dataDirectories = new ArrayList();

        File[] roots = new File[]{
                new File( SVN_WORKING_COPY_ROOT + "\\simulations-java\\simulations" ),
                new File( SVN_WORKING_COPY_ROOT + "\\simulations-java\\common" ),
        };
        for ( int i = 0; i < roots.length; i++ ) {
            File root = roots[i];
            File[] f = root.listFiles();
            for ( int j = 0; j < f.length
                             && j < Integer.MAX_VALUE//debugging only
                    ; j++ ) {
                File file = f[j];
                File dataDir = new File( file, "data" );
                if ( dataDir.exists() ) {
                    dataDirectories.add( dataDir );
                }
            }
        }
        File buildFolder = new File( "C:\\phet\\subversion\\trunk\\simulations-java\\build-tools\\phet-build\\data" );
        if ( buildFolder.exists() ) {
            dataDirectories.add( buildFolder );
        }
        System.out.println( "dataDirectories = " + dataDirectories );
        return (File[]) dataDirectories.toArray( new File[0] );
    }

    public static File[] getImageFiles() {
        File[] dataDirectories = getDataDirectories();
        return (File[]) getImageFiles( dataDirectories ).toArray( new File[0] );
    }

    private static ArrayList getImageFiles( File[] dataDirectories ) {
        ArrayList imageFiles = new ArrayList();
        searchImageFiles( new ArrayList( Arrays.asList( dataDirectories ) ), imageFiles );
        return imageFiles;
    }

    private static void searchImageFiles( ArrayList dataDirectories, ArrayList imageFiles ) {
        searchFiles( dataDirectories, imageFiles, new FileFilter() {
            public boolean accept( File pathname ) {
                return isImage( pathname );
            }
        } );
    }

    public static File[] getAllSoundFiles() {
        File[] dataDirs = getDataDirectories();
        ArrayList result = new ArrayList();
        searchFiles( new ArrayList( Arrays.asList( dataDirs ) ), result, new FileFilter() {
            public boolean accept( File pathname ) {
                return pathname.isFile() && (
                        pathname.getAbsolutePath().toLowerCase().endsWith( "au" )
                        ||
                        pathname.getAbsolutePath().toLowerCase().endsWith( "wav" )
                );
            }
        } );
        return (File[]) result.toArray( new File[0] );
    }

    public static File[] getNonImageFiles() {
        File[] dataDirs = getDataDirectories();
        ArrayList result = new ArrayList();
        searchFiles( new ArrayList( Arrays.asList( dataDirs ) ), result, new FileFilter() {
            public boolean accept( File pathname ) {
                return pathname.isFile() && !isImage( pathname ) && !pathname.getAbsolutePath().endsWith( ".svn" )
                       && !pathname.getAbsolutePath().toLowerCase().endsWith( ".properties" )
                       && !pathname.getAbsolutePath().toLowerCase().endsWith( ".xml" )
                       && !pathname.getAbsolutePath().toLowerCase().endsWith( ".esp" )
                       && !pathname.getAbsolutePath().toLowerCase().endsWith( ".txt" )
                       && !pathname.getAbsolutePath().toLowerCase().endsWith( ".html" )
                        ;
            }
        } );
        return (File[]) result.toArray( new File[0] );
    }

    private static void searchFiles( ArrayList dataDirectories, ArrayList imageFiles, FileFilter fileFilter ) {
        for ( int i = 0; i < dataDirectories.size(); i++ ) {
            File file = (File) dataDirectories.get( i );
            if ( fileFilter.accept( file ) ) {
                imageFiles.add( file );
            }
            else if ( file.isDirectory() && !file.getAbsolutePath().endsWith( ".svn" ) ) {
                dataDirectories.addAll( Arrays.asList( file.listFiles() ) );
            }
        }
    }

    private static boolean isImage( File file ) {
        if ( !file.isDirectory() ) {
            String path = file.getAbsolutePath().toLowerCase();
            String[] suffixes = new String[]{"png", "gif", "jpg", "tif", "tiff", "jpeg"};
            for ( int i = 0; i < suffixes.length; i++ ) {
                String suffix = suffixes[i];
                if ( path.endsWith( "." + suffix ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main( String[] args ) {
        File[] nonImage = getNonImageFiles();
        System.out.println( "nonImage.length = " + nonImage.length );
//        System.out.println( "Arrays.asList( nonImage ) = " + Arrays.asList( nonImage ) );
        for ( int i = 0; i < nonImage.length; i++ ) {
            File file = nonImage[i];
            System.out.println( "file = " + file );
        }
    }

    public static File[] getSoundFilesNoDuplicates() {
        File[] soundFiles = getAllSoundFiles();

        ArrayList singleCopies = new ArrayList();
        for ( int i = 0; i < soundFiles.length; i++ ) {
            File soundFile = soundFiles[i];
            if ( !contains( singleCopies, soundFile ) ) {
                singleCopies.add( soundFile );
            }
        }
        return (File[]) singleCopies.toArray( new File[0] );
    }

    private static boolean contains( ArrayList fileList, File b ) {
        for ( int i = 0; i < fileList.size(); i++ ) {
            File a = (File) fileList.get( i );
            try {
                if ( FileUtils.contentEquals( a, b ) ) {
                    return true;
                }
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
