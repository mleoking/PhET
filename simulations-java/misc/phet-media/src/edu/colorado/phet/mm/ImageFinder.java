package edu.colorado.phet.mm;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Author: Sam Reid
 * Jun 14, 2007, 8:35:26 PM
 */
public class ImageFinder {

    public static void main( String[] args ) {
        ArrayList dataDirectories = new ArrayList();
        File[] roots = new File[]{
                new File( "C:\\phet\\subversion\\trunk\\simulations-java\\simulations" ),
                new File( "C:\\phet\\subversion\\trunk\\simulations-java\\common" ),
        };
        for( int i = 0; i < roots.length; i++ ) {
            File root = roots[i];
            File[] f = root.listFiles();
            for( int j = 0; j < f.length; j++ ) {
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


        ArrayList imageFiles = new ArrayList();
        searchImageFiles( dataDirectories, imageFiles );

        System.out.println( "imageFiles.size = " + imageFiles.size() );
        System.out.println( "imageFiles = " + imageFiles );

        ArrayList singleCopies = discardDuplicates( imageFiles );
        System.out.println( "singleCopies.size() = " + singleCopies.size() );
        System.out.println( "singleCopies = " + singleCopies );
//        for( int i = 0; i < roots.length; i++ ) {
//            File root = roots[i];
//        }
//        String[] projects = PhetListSimTask.getSimNames( new File( "C:\\phet\\subversion\\trunk\\simulations-java\\simulations" ) );
//        System.out.println( "Arrays.asList( projects ) = " + Arrays.asList( projects ) );
    }

    private static ArrayList discardDuplicates( ArrayList imageFiles ) {
        ArrayList solo = new ArrayList();
        for( int i = 0; i < imageFiles.size(); i++ ) {
            File file = ( (File)imageFiles.get( i ) );
            if( !listContains( solo, file ) ) {
                solo.add( file );
            }
        }
        return solo;
    }

    private static boolean listContains( ArrayList list, File f ) {
        for( int i = 0; i < list.size(); i++ ) {
            File a = (File)list.get( i );
            if( imageFileEquals( a, f ) ) {
                return true;
            }
        }
        return false;
    }

    private static boolean imageFileEquals( File a, File b ) {
        return a.length() == b.length()
//               && a.getName().equals( b.getName() )
               && imagesEqual( a, b );
    }

    private static boolean imagesEqual( File a, File b ) {
        try {
            BufferedImage im1 = ImageLoader.loadBufferedImage( a.toURL() );
            BufferedImage im2 = ImageLoader.loadBufferedImage( b.toURL() );
            return equals( im1, im2 );
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }

    }

    /*
     * http://www.kclee.de/clemens/java/ccl/index.html
     * GPL or LGPL
         * Compares two images pixel by pixel. At the current state,
         * no special consideration for transparency is taken into
         * account. Be aware that transient images might show black
         * pixels where you don't expect them.
         *
         * @return   true   if all pixels equal each other in both
         *                  images.
         */
    static public boolean equals( BufferedImage imageA, BufferedImage imageB ) {
        if( imageA == null && imageB == null ) {
            return true;
        }
        if( imageA == null || imageB == null ) {
            return false;
        }
        if( imageA.getWidth( null ) != imageB.getWidth( null )
            || imageA.getHeight( null ) != imageB.getHeight( null ) ) {
            return false;
        }

        Raster rasterA = imageA.getRaster();
        Raster rasterB = imageB.getRaster();
        SampleModel sampleModelA = rasterA.getSampleModel();
        SampleModel sampleModelB = rasterB.getSampleModel();
        DataBuffer dataBufferA = rasterA.getDataBuffer();
        DataBuffer dataBufferB = rasterB.getDataBuffer();

        int imageWidth = imageA.getWidth( null );
        int imageHeight = imageA.getHeight( null );
        for( int y = 0; y < imageHeight; y++ ) {
            for( int x = 0; x < imageWidth; x++ ) {
                int pix[] = null;
                int[] pixelA = sampleModelA.getPixel( x, y, pix, dataBufferA );
                int[] pixelB = sampleModelB.getPixel( x, y, pix, dataBufferB );
                if( pixelA.length != pixelB.length ) {
                    return false;
                }
                for( int pixel = 0; pixel < pixelA.length; pixel++ ) {
                    if( pixelA[pixel] != pixelB[pixel] ) {
                        return false;
                    }
                }
            }
        }

//            Util.debug( GraphicsUtil.class, "equals(..).EXIT" );

        return true;
    }


    private static void searchImageFiles( ArrayList dataDirectories, ArrayList imageFiles ) {
        for( int i = 0; i < dataDirectories.size(); i++ ) {
            File file = (File)dataDirectories.get( i );
            if( isImage( file ) ) {
                imageFiles.add( file );
            }
            else if( file.isDirectory() ) {
                dataDirectories.addAll( Arrays.asList( file.listFiles() ) );
            }
        }
    }

    private static boolean isImage( File file ) {
        String path = file.getAbsolutePath().toLowerCase();
        String[] suffixes = new String[]{"png", "gif", "jpg", "tif", "tiff", "jpeg"};
        for( int i = 0; i < suffixes.length; i++ ) {
            String suffix = suffixes[i];
            if( path.endsWith( suffix ) ) {
                return true;
            }
        }
        return false;
    }
}
