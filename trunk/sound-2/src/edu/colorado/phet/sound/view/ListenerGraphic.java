/**
 * Class: ListenerGraphic
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 6, 2004
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.sound.SoundConfig;
import edu.colorado.phet.sound.SoundModule;
import edu.colorado.phet.sound.model.Listener;
import edu.colorado.phet.sound.model.SoundModel;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.LinkedList;

public class ListenerGraphic extends DefaultInteractiveGraphic {

    private float s_dopplerShiftScaleFactor = 10f;

    private double lastEventX;
    private long lastEventTime;
    private double clockScaleFactor;
    private double nonDopplerFrequency;
    private Listener listener;
    private SoundModel model;
    private Point2D.Double location;
    private PhetImageGraphic image;
    private SoundModule module;

    /**
     *
     */
    public ListenerGraphic( SoundModule module, Listener listener, PhetImageGraphic image,
                            double x, double y,
                            double minX, double minY,
                            double maxX, double maxY ) {
        super( image );
        this.location = new Point2D.Double( x, y );
        this.module = module;
        this.model = (SoundModel)module.getModel();
        this.image = image;

        this.addTranslationBehavior( new ListenerTranslationBehavior( minX, minY, maxX, maxY ) );
        this.addCursorHandBehavior();
        this.listener = listener;
    }

    protected Point2D.Double getLocation() {
        return location;
    }

    private class ListenerTranslationBehavior implements Translatable {
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

        public void translate( double dx, double dy ) {
            double x = Math.max( minX, Math.min( maxX, location.getX() + dx ) );
            double y = Math.max( minY, Math.min( maxY, location.getY() + dy ) );
            location.setLocation( x, y );
            image.setPosition( (int)x, (int)y );

            // todo: minX and minY here aren't the right way to do this.
            listener.setLocation( new Point2D.Double( x - minX, y - minY ) );
        }
    }

    /**
     *
     */
    private int numSamples = 3;
    //    private int numSamples = 5;
    private LinkedList samples = new LinkedList();


    /**
     * @param event
     */
    public void mousePressed( MouseEvent event ) {
        super.mousePressed( event );

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
    public void mouseDragged( MouseEvent event ) {
        super.mouseDragged( event );

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
                if( module.getCurrentListener() == this.listener ) {
                    module.setOscillatorFrequency( dopplerFrequency );
                }

                // We must yield so the PhysicalSystem thread can get the update.
                //                Thread.yield();
                try {
                    Thread.sleep( 20 );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }
    }
}
