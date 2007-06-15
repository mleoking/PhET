package edu.colorado.phet.mm;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Author: Sam Reid
 * Jun 14, 2007, 8:35:26 PM
 */
public class MediaFinder {

    public static File[] getDataDirectories() {
        ArrayList dataDirectories = new ArrayList();
        File[] roots = new File[]{
                new File( "C:\\phet\\subversion\\trunk\\simulations-java\\simulations" ),
                new File( "C:\\phet\\subversion\\trunk\\simulations-java\\common" ),
        };
        for( int i = 0; i < roots.length; i++ ) {
            File root = roots[i];
            File[] f = root.listFiles();
            for( int j = 0; j < f.length
                            && j < Integer.MAX_VALUE//debugging only
                    ; j++ ) {
                File file = f[j];
                File dataDir = new File( file, "data" );
                if( dataDir.exists() ) {
                    dataDirectories.add( dataDir );
                }
            }
        }
        File buildFolder = new File( "C:\\phet\\subversion\\trunk\\simulations-java\\build-tools\\phet-build\\data" );
        if( buildFolder.exists() ) {
            dataDirectories.add( buildFolder );
        }
        System.out.println( "dataDirectories = " + dataDirectories );
        return (File[])dataDirectories.toArray( new File[0] );
    }

    public static File[] getImageFiles() {
        File[] dataDirectories = getDataDirectories();
        return (File[])getImageFiles( dataDirectories ).toArray( new File[0] );
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

    public static File[]getNonImageFiles(){
        File[]dataDirs=getDataDirectories();
        ArrayList result=new ArrayList( );
        searchFiles( new ArrayList( Arrays.asList( dataDirs )),result, new FileFilter() {
            public boolean accept( File pathname ) {
                return pathname.isFile()&&!isImage( pathname )&&!pathname.getAbsolutePath().endsWith( ".svn")
                        &&!pathname.getAbsolutePath().toLowerCase().endsWith( ".properties");
            }
        } );
        return (File[])result.toArray( new File[0]);
    }

    private static void searchFiles( ArrayList dataDirectories, ArrayList imageFiles, FileFilter fileFilter ) {
        for( int i = 0; i < dataDirectories.size(); i++ ) {
            File file = (File)dataDirectories.get( i );
            if( fileFilter.accept( file ) ) {
                imageFiles.add( file );
            }
            else if( file.isDirectory()&&!file.getAbsolutePath().endsWith( ".svn") ) {
                dataDirectories.addAll( Arrays.asList( file.listFiles() ) );
            }
        }
    }

    private static boolean isImage( File file ) {
        if( !file.isDirectory() ) {
            String path = file.getAbsolutePath().toLowerCase();
            String[] suffixes = new String[]{"png", "gif", "jpg", "tif", "tiff", "jpeg"};
            for( int i = 0; i < suffixes.length; i++ ) {
                String suffix = suffixes[i];
                if( path.endsWith( "." + suffix ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main( String[] args ) {
        File[]nonImage=getNonImageFiles();
        System.out.println( "nonImage.length = " + nonImage.length );
        System.out.println( "Arrays.asList( nonImage ) = " + Arrays.asList( nonImage ) );
    }
}
