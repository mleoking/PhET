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
import edu.colorado.phet.sound.model.Listener;
import edu.colorado.phet.sound.model.SoundModel;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.LinkedList;

public class ListenerGraphic extends DefaultInteractiveGraphic {

    private double lastEventX;
    private long lastEventTime;
    private double clockScaleFactor;
    private double nonDopplerFrequency;
    private Listener listener;
    private SoundModel model;
    private Point2D.Double location;
    private PhetImageGraphic image;

    /**
     *
     */
    public ListenerGraphic( SoundModel model, Listener listener, PhetImageGraphic image,
                            double x, double y,
                            double minX, double minY,
                            double maxX, double maxY ) {
        super( image );
        this.location = new Point2D.Double( x, y );
        this.model = model;
        this.image = image;

        this.addTranslationBehavior( new ListenerTranslationBehavior( minX, minY, maxX, maxY ));
        this.addCursorHandBehavior();
        this.listener = listener;
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
            double x = Math.max( minX, Math.min( maxX, location.getX() + dx ));
            double y = Math.max( minY, Math.min( maxY, location.getY() + dy ));
            location.setLocation( x, y );
            image.setPosition( (int)x, (int)y );
            listener.setLocation( location );
            model.setListenerLocation( location.getX() - SoundConfig.s_speakerBaseX, location.getY() );
        }
    }

    /**
     * Tells the sound application where the graphic is on the screen
     */
//    private void notifySoundApplication() {
//        SoundApplication soundApplication = (SoundApplication)PhetApplication.instance();
//        soundApplication.setListenerLocation( (float)this.getLocationPoint2D().getX() - this.getMinX(), 0 );
//        soundApplication.updateOscillators();
//    }

//    private SoundApplication getSoundApplication() {
//        return (SoundApplication)PhetApplication.instance();
//    }


    /**
     *
     */
    private int numSamples = 5;
    private LinkedList samples = new LinkedList();


    /**
     *
     * @param event
     */
    public void mousePressed( MouseEvent event ) {
        super.mousePressed( event );

        // Record the frequency when we started dragging, and the point that
        // the graphic is at in the X axis. These will be used to compute
        // the Doppler-shifted frequency
        nonDopplerFrequency = model.getPrimaryWavefront().getFrequency();
        lastEventX = location.getX();
        lastEventTime = event.getWhen();
        clockScaleFactor = SoundConfig.s_timeStep/ SoundConfig.s_waitTime;
    }

    /**
     *
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

                // The following two lines must occur in the order shown. Otherwise, the frequency
                // change will get stepped on

                // todo: this appears to be an important line of code, but I'm not sure what it does
//                ( (SingleSourceApparatusPanel)getApparatusPanel() ).determineAudioReferencPt();

                double dopplerFrequency = nonDopplerFrequency - ( aveVx / numSamples ) * s_dopplerShiftScaleFactor;
                if( model.getAudioSource() == SoundApparatusPanel.LISTENER_SOURCE ) {
                    model.setOscillatorFrequency( dopplerFrequency );
                }
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

    /**
     *
     * @param e
     */
    public void mouseReleased( MouseEvent e ) {

        super.mouseReleased( e );
//        notifySoundApplication();

        // Reset the frequency to eliminate Doppler shift
//        ( (SingleSourceApparatusPanel)getApparatusPanel() ).determineAudioReferencPt();
    }

    //
    // Static fields and methods
    //
    private float s_dopplerShiftScaleFactor = 0.01f;
}
