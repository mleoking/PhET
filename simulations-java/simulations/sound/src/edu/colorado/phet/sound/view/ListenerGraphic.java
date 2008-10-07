/**
 * Class: ListenerGraphic
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 6, 2004
 */
package edu.colorado.phet.sound.view;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.LinkedList;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationEvent;
import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationListener;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.sound.SoundConfig;
import edu.colorado.phet.sound.SoundModule;
import edu.colorado.phet.sound.model.SoundListener;
import edu.colorado.phet.sound.model.SoundModel;

public class ListenerGraphic extends CompositePhetGraphic {
//public class ListenerGraphic extends DefaultInteractiveGraphic {

    //
    // Static fields and methods
    //
    protected static int s_earOffsetX = 10;
    protected static int s_earOffsetY = 53;
    private float s_dopplerShiftScaleFactor = 10f;

    private double lastEventX;
    private long lastEventTime;
    private double clockScaleFactor;
    private double nonDopplerFrequency;
    private SoundListener listener;
    private SoundModel model;
    private Point2D.Double location;
    private PhetImageGraphic image;
    private SoundModule module;
    private Point2D.Double earLocation = new Point2D.Double();
    private boolean isMovable = true;

    /**
     *
     */
    public ListenerGraphic( SoundModule module, SoundListener listener, PhetImageGraphic image,
                            double x, double y,
                            double minX, double minY,
                            double maxX, double maxY ) {
        super( module.getSimulationPanel() );
        addGraphic( image );
        this.location = new Point2D.Double( x, y );
        this.module = module;
        this.model = module.getSoundModel();
        this.image = image;
        this.listener = listener;

        this.setCursorHand();
        ListenerTranslationBehavior target = new ListenerTranslationBehavior( minX, minY, maxX, maxY );
        this.addTranslationListener( target );

        // Do this to make the listener update
        target.translate( 0, 0 );
    }

    public boolean isMovable() {
        return isMovable;
    }

    public void setMovable( boolean movable ) {
        isMovable = movable;
        if( movable ) {
            this.setCursorHand();
        }
        else {
            this.removeCursor();
        }
    }


    public Point getLocation() {
        return new Point( (int)location.getX(), (int)location.getY() );
    }

    private class ListenerTranslationBehavior implements TranslationListener {
        private double minX;
        private double minY;
        private double maxX;
        private double maxY;

        ListenerTranslationBehavior( double minX, double minY, double maxX, double maxY ) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }

        public void translationOccurred( TranslationEvent translationEvent ) {
            translate( translationEvent.getDx(), translationEvent.getDy() );
        }

        public void translate( double dx, double dy ) {
            if( isMovable ) {
                double x = Math.max( minX, Math.min( maxX, location.getX() + dx ) );
                double y = Math.max( minY, Math.min( maxY, location.getY() + dy ) );
                location.setLocation( x, y );
                image.setLocation( (int)x, (int)y );

                ListenerGraphic.this.earLocation.setLocation( ListenerGraphic.this.getLocation().getX() + s_earOffsetX,
                                                              ListenerGraphic.this.getLocation().getY() + s_earOffsetY );
                // The hard-coded 100 is to account for the width of the speaker graphic. It should be done in a
                // different way
                listener.setLocation( new Point2D.Double( earLocation.x - ( SoundConfig.s_wavefrontBaseX + 100 ),
                                                          earLocation.y - SoundConfig.s_wavefrontBaseY ) );
            }
        }
    }

    /**
     *
     */
    //    private int numSamples = 3;
    private int numSamples = 5;
    private LinkedList samples = new LinkedList();


    /**
     * @param event
     */
    public void mousePressed( MouseEvent event ) {
        // Record the frequency when we started dragging, and the point that
        // the graphic is at in the X axis. These will be used to compute
        // the Doppler-shifted frequency
        nonDopplerFrequency = model.getPrimaryWavefront().getFrequency() * SoundConfig.s_frequencyDisplayFactor;
        lastEventX = location.getX();
        lastEventTime = event.getWhen();
        clockScaleFactor = SoundConfig.s_timeStep / SoundConfig.s_waitTime;
    }

    /**
     * @param event
     */
    public void mouseDragged( final MouseEvent event ) {

        // Drawing the graphic later seems to smooth out the waveform graphic drawing somewhat
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
//                ListenerGraphic.super.mouseDragged( event );
            }
        } );

        // Compute the Doppler-shifted frequency based on the displacement
        // of the graphic. Note that we can't use the location of the mouse
        // event because the mouse can move after the graphic has hit the
        // end of its allowed range of motion.
        double vx = 0;
        double dx = location.getX() - lastEventX;
        lastEventX = location.getX();
        long now = event.getWhen();
        long dt = now - lastEventTime;
        lastEventTime = now;
        if( dt > 0 ) {
            vx = dx / ( dt * clockScaleFactor );
            samples.add( new Double( vx ) );
            if( samples.size() >= numSamples ) {
                float aveVx = 0;
                for( int i = 0; i < samples.size(); i++ ) {
                    Double aDouble = (Double)samples.get( i );
                    aveVx += aDouble.floatValue();
                }
                samples.remove( 0 );

                double dopplerFrequency = nonDopplerFrequency - ( aveVx / numSamples ) * s_dopplerShiftScaleFactor;
                if( module.getCurrentListener() == listener ) {
                    module.setOscillatorDopplerFrequency( dopplerFrequency );
                }

                // We must yield so the rest of the system can get something done. In particular,
                // if we don't yield, the waveform graphic stalls.
                try {
                    Thread.sleep( 20 );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * When the mouse is released, restore the oscillator frequency to its nono-doppler value
     *
     * @param e
     */
    public void mouseReleased( MouseEvent e ) {
//        super.mouseReleased( e );
        if( module.getCurrentListener() == this.getListener() ) {
            module.setOscillatorDopplerFrequency( nonDopplerFrequency );
        }
    }

    protected SoundListener getListener() {
        return listener;
    }

    // DEBUG
    //    public void paint( Graphics2D g ) {
    //        super.paint( g );
    //        g.setColor( Color.red );
    //        g.drawArc( (int)earLocation.x, (int)earLocation.y, 5, 5, 0, 360 );
    //    }
}
