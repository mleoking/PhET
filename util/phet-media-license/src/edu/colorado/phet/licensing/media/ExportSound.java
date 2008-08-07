package edu.colorado.phet.licensing.media;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.licensing.media.FileUtils;

/**
 * Author: Sam Reid
 * Jun 19, 2007, 2:32:19 PM
 */
public class ExportSound {
    public static void main( String[] args ) throws IOException {
        File[] soundFiles = MediaFinder.getSoundFilesNoDuplicates();
        File soundDir = new File( "sound" );
        soundDir.mkdirs();
        for ( int i = 0; i < soundFiles.length; i++ ) {
            File soundFile = soundFiles[i];
            FileUtils.copy( soundFile, new File( soundDir, soundFile.getName() ) );
        }
    }
}
