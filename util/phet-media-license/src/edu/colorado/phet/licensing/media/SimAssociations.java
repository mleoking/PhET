package edu.colorado.phet.licensing.media;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common_force1d.util.QuickTimer;
import edu.colorado.phet.licensing.media.FileUtils;

/**
 * Author: Sam Reid
 * Jun 15, 2007, 11:03:21 AM
 */
public class SimAssociations {
    private ArrayList[] simAssociations;

    public SimAssociations( ImageEntry[] imageEntries ) {
        this.simAssociations = new ArrayList[imageEntries.length];
        for ( int i = 0; i < imageEntries.length; i++ ) {
            ImageEntry imageEntry = imageEntries[i];
            if ( imageEntry.isNonPhet() ) {
                File[] sims = getAssociations( imageEntry );
                simAssociations[i] = new ArrayList( Arrays.asList( sims ) );
                System.out.println( "(" + i + "/" + imageEntries.length + "): simAssociations[" + imageEntry.getImageName() + "] = " + simAssociations[i] );
            }
            else {
                simAssociations[i] = new ArrayList();
            }
        }
    }

    private File[] dataDirs = MediaFinder.getDataDirectories();

    public File[] getAssociations( ImageEntry imageEntry ) {
        return getAssociations( dataDirs, new File( "annotated-data", imageEntry.getImageName() ) );
    }

    private File[] getAssociations( File[] f, File annotatedDataFile ) {
        ArrayList associations = new ArrayList();
        for ( int i = 0; i < f.length; i++ ) {
//            System.out.println( "f[i].getAbsolutePath() = " + f[i].getAbsolutePath() );
            try {
                if ( f[i].isFile() && FileUtils.contentEquals( f[i], annotatedDataFile ) ) {
                    associations.add( f[i] );
                }
                else if ( f[i].isDirectory() ) {
                    associations.addAll( Arrays.asList( getAssociations( f[i].listFiles( new FileFilter() {
                        public boolean accept( File pathname ) {
                            return !pathname.getAbsolutePath().endsWith( ".svn" );
                        }
                    } ), annotatedDataFile ) ) );
                }
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        return (File[]) associations.toArray( new File[0] );
    }

    public static void main( String[] args ) throws IOException {
        QuickTimer quickTimer = new QuickTimer();
        ImageEntry[] nonPhetEntries = AnnotatedRepository.getNonPhetEntries();
        SimAssociations simAssociations = new SimAssociations(
//                new ImageEntry[]{new ImageEntry( "dog.gif" ),new ImageEntry( "dollarbill.gif" )}
//nonPhetEntries
//new ImageEntry[]{new ImageEntry( "0_faucet.png")}
new ImageEntry[0]
        );
        System.out.println( "simAssociations = " + simAssociations );
        System.out.println( "quickTimer.getTime() = " + quickTimer.getTime() );
        for ( int i = 0; i < nonPhetEntries.length; i++ ) {
            ImageEntry nonPhetEntry = nonPhetEntries[i];

            File[] assoc = simAssociations.getAssociations( nonPhetEntry );
            System.out.println( ( assoc.length == 0 ? "###" : "" ) + "entry=" + nonPhetEntry.getImageName() + ", assoc = " + Arrays.asList( assoc ) );
        }
    }

    public String toString() {
        return Arrays.asList( simAssociations ).toString();
    }
}
