package edu.colorado.phet.simsharinganalysis.scripts.buildamoleculeseptember2012;

import java.util.concurrent.TimeUnit;

/**
 * @author Sam Reid
 */
public class JavaTiming {

    //http://stackoverflow.com/questions/625433/how-to-convert-milliseconds-to-x-mins-x-seconds-in-java/625624#625624
    public static String formatMillis( long millis ) {
        return String.format( "%d:%02d",
                              TimeUnit.MILLISECONDS.toMinutes( millis ),
                              TimeUnit.MILLISECONDS.toSeconds( millis ) -
                              TimeUnit.MINUTES.toSeconds( TimeUnit.MILLISECONDS.toMinutes( millis ) ) );
    }
}