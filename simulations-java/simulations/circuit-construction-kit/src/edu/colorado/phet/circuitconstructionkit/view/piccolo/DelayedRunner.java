package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Send sim sharing messages, but not too often and don't forget the last one (it is important)
 */
public class DelayedRunner {
    private Runnable runnable;
    private Timer timer = new Timer( 500, new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
            if ( runnable != null ) {
                runnable.run();
                runnable = null;
            }
            timer.stop();
        }
    } );

    public DelayedRunner() {
        timer.setRepeats( false );
    }

    public void set( Runnable runnable ) {
        this.runnable = runnable;
        if ( !timer.isRunning() ) {
            timer.start();
        }
    }
}
