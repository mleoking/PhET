package edu.colorado.phet.media;

import java.io.File;

/**
 * Author: Sam Reid
 * Jun 15, 2007, 3:11:22 AM
 */
public class FindOtherMedia {
    public static void main( String[] args ) {
        File[] dataDir = MediaFinder.getDataDirectories();
        for ( int i = 0; i < dataDir.length; i++ ) {
            File file = dataDir[i];

        }
    }
}
