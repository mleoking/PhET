// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.simsharing.test1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;
import edu.colorado.phet.gravityandorbits.simsharing.gravityandorbits.GravityAndOrbitsApplicationState;

import static edu.colorado.phet.simsharing.test1.SimHistory.loadHistory;

/**
 * main code used for this mode
 * if ( Arrays.asList( commandLineArgs ).contains( "-history" ) ) {//load and play back history
 * int index = Arrays.asList( commandLineArgs ).indexOf( "-history" );
 * String historyFile = commandLineArgs[index + 1];
 * SimHistoryPlayback.playHistory( application, new File( historyFile ) );
 * }
 *
 * @author Sam Reid
 */
public class SimHistoryPlayback {
    static int increment = 1;
    static int index = 0;

    public static void playHistory( final GravityAndOrbitsApplication gravityAndOrbitsApplication, File file ) {
        try {
            final ArrayList<Object> history = loadHistory( file );

            Timer timer = new Timer( 20, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    GravityAndOrbitsApplicationState s = (GravityAndOrbitsApplicationState) history.get( index );
                    s.apply( gravityAndOrbitsApplication );
                    index = index + increment;
                    if ( index >= history.size() ) {
                        increment = -1;
                        index = index + increment;
                    }
                    if ( index < 0 ) {
                        increment = 1;//go back in time after playing the recording, just for testing
                        index = index + increment;
                    }
                }
            } );
            timer.start();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        catch ( ClassNotFoundException e ) {
            e.printStackTrace();
        }
    }
}
