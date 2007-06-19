package edu.colorado.phet.media;

import edu.colorado.phet.media.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Author: Sam Reid
 * Jun 19, 2007, 2:32:19 PM
 */
public class ExportSound {
    public static void main( String[] args ) throws IOException {
        File[] soundFiles = MediaFinder.getSoundFilesNoDuplicates();
        File soundDir = new File( "sound" );
        soundDir.mkdirs();
        for( int i = 0; i < soundFiles.length; i++ ) {
            File soundFile = soundFiles[i];
            FileUtils.copy( soundFile, new File( soundDir, soundFile.getName() ) );
        }
    }
}
