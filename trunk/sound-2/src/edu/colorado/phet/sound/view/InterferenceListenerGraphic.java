/**
 * Class: InterferenceListenerGraphic
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 11, 2004
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.sound.model.Wavefront;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.model.Listener;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;

import java.awt.geom.Point2D;
import java.awt.*;
import java.awt.event.MouseEvent;

public class InterferenceListenerGraphic extends ListenerGraphic {

    private Point2D.Double audioSourceA;
    private Point2D.Double audioSourceB;
    private Wavefront interferringWavefront;
    private Point2D.Double earLocation = new Point2D.Double();
    private SoundModel soundModel;

    /**
     *
     * @param image
     * @param x
     * @param y
     * @param minX
     * @param minY
     * @param maxX
     * @param maxY
     * @param audioSourceA
     * @param audioSourceB
     * @param interferringWavefront
     */
    public InterferenceListenerGraphic( SoundModel model, Listener listener,
                                        PhetImageGraphic image, double x, double y,
                  double minX, double minY,
                  double maxX, double maxY,
                  Point2D.Double audioSourceA,
                  Point2D.Double audioSourceB,
                  Wavefront interferringWavefront ) {
        super( model, listener, image, x, y, minX, minY, maxX, maxY );
        this.soundModel = model;
        this.audioSourceA = audioSourceA;
        this.audioSourceB = audioSourceB;
        this.interferringWavefront = interferringWavefront;
        updateAmplitude();
    }

    /**
     *
     * @param event
     */
    public void mouseDragged( MouseEvent event ) {
        super.mouseDragged( event );
        updateAmplitude();
    }

    /**
     *
     * @param e
     */
    public void mouseReleased( MouseEvent e ) {
        super.mouseReleased( e );
        updateAmplitude();
    }

    /**
     *
     */
    private void updateAmplitude() {

        // Determine the difference in distance of the listener's ear to
        // each audio source in units of phase angle of the current frequency
//        SoundApplication soundApplication = (SoundApplication)PhetApplication.instance();
        earLocation.setLocation( this.getLocation().getX() + s_earOffsetX,
                                 this.getLocation().getY() + s_earOffsetY );
        double distA = (float)earLocation.distance( audioSourceA );
        double distB = (float)earLocation.distance( audioSourceB );

        double lambda = interferringWavefront.getWavelengthAtTime( 0  );
        double theta = (( distA - distB ) / lambda ) * Math.PI;

        // The amplitude factor for max amplitude is the sum of the two wavefront
        // amplitudes times the cosine of the phase angle
        double amplitudeA = soundModel.getAmplitude();
//        float amplitudeA = soundApplication.getAmplitude();
        double maxAmplitude = amplitudeA * Math.abs( Math.cos( theta ));

        // HACK!!!!
        soundModel.getPrimaryOscillator().setAmplitude( maxAmplitude );
    }


    public void paint( Graphics2D g ) {
        super.paint( g );

        g.setColor( Color.RED );

        // The following lines paint dots on the screen at critical points for
        // debug purposes
//
//        // The point that sound is generated for
//        g.fillArc( (int)earLocation.getX(),
//                   (int)earLocation.getY(),
//                   5, 5, 0, 360 );
//        // A point one wavelength away
//        g.fillArc( (int)earLocation.getX() - (int)(interferringWavefront.getWavelengthAtTime( 0 )),
//                   (int)earLocation.getY(),
//                   5, 5, 0, 360 );
//        // The audio sources
//        g.fillArc( (int)audioSourceA.getX(), (int)audioSourceA.getY(), 5, 5, 0, 360 );
//        g.fillArc( (int)audioSourceB.getX(), (int)audioSourceB.getY(), 5, 5, 0, 360 );
    }

    //
    // Static fields and methods
    //
    private static int s_earOffsetX = 0;
    private static int s_earOffsetY = 78;
}
