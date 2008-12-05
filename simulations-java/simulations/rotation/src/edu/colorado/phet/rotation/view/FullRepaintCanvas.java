package edu.colorado.phet.rotation.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

/**
 * This class repaints its full bounds every clock tick, circumventing the RepaintManager.
 * <p/>
 * Created by: Sam
 * Dec 4, 2007 at 8:55:12 AM
 */
public class FullRepaintCanvas extends PhetPCanvas {
    private boolean timedFullPaint = true;

    public FullRepaintCanvas( ConstantDtClock clock, Module introModule ) {
        final Timer timer = new Timer( clock.getDelay(), new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( timedFullPaint ) {
                    doPaintImm();
                }
            }
        } );
        clock.addConstantDtClockListener( new ConstantDtClock.ConstantDtClockListener() {
            public void delayChanged( ConstantDtClock.ConstantDtClockEvent event ) {
                timer.setDelay( event.getClock().getDelay() );
            }

            public void dtChanged( ConstantDtClock.ConstantDtClockEvent event ) {
            }
        } );
        introModule.addListener( new Module.Listener() {
            public void activated() {
                timer.start();
            }

            public void deactivated() {
                timer.stop();
            }
        } );
    }

    public boolean isTimedFullPaint() {
        return timedFullPaint;
    }

    public void setTimedFullPaint( boolean timedFullPaint ) {
        this.timedFullPaint = timedFullPaint;
    }

    public void doPaintImm() {
        if ( timedFullPaint ) {
            super.paintImmediately( 0, 0, getWidth(), getHeight() );
        }
    }

    public void paintImmediately() {
        if ( !timedFullPaint ) {
            super.paintImmediately();
        }
    }

    public void paintImmediately( int x, int y, int w, int h ) {
        if ( !timedFullPaint ) {
            super.paintImmediately( x, y, w, h );
        }
    }

    public void paintImmediately( Rectangle r ) {
        if ( !timedFullPaint ) {
            super.paintImmediately( r );
        }
    }
}
