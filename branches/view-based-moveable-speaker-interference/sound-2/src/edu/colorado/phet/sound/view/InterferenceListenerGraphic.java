/**
 * Class: InterferenceListenerGraphic
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 11, 2004
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.sound.TwoSpeakerInterferenceModule;
import edu.colorado.phet.sound.model.Listener;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.model.Wavefront;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class InterferenceListenerGraphic extends ListenerGraphic {

    private Point2D.Double audioSourceA;
    private Point2D.Double audioSourceB;
    private Wavefront interferringWavefront;
    private Point2D.Double earLocation = new Point2D.Double();
    private TwoSpeakerInterferenceModule soundModule;
    private SoundModel soundModel;

    /**
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
    public InterferenceListenerGraphic( TwoSpeakerInterferenceModule module, Listener listener,
                                        PhetImageGraphic image, double x, double y,
                                        double minX, double minY,
                                        double maxX, double maxY,
                                        Point2D.Double audioSourceA,
                                        Point2D.Double audioSourceB,
                                        Wavefront interferringWavefront ) {
        super( module, listener, image, x, y, minX, minY, maxX, maxY );
        this.soundModule = module;
        this.soundModel = (SoundModel)module.getModel();
        this.audioSourceA = audioSourceA;
        this.audioSourceB = audioSourceB;
        this.interferringWavefront = interferringWavefront;
        updateAmplitude();
    }

    /**
     * @param event
     */
    public void mouseDragged( MouseEvent event ) {
        super.mouseDragged( event );
        if( soundModule.getCurrentListener() == getListener() ) {
            updateAmplitude();
        }
    }

    /**
     * @param e
     */
    public void mouseReleased( MouseEvent e ) {
        super.mouseReleased( e );
        if( soundModule.getCurrentListener() == getListener() ) {
            updateAmplitude();
        }
    }

    /**
     *
     */
    private void updateAmplitude() {
        // todo: this should be part of the model, not figured out here!!!
        // Determine the difference in distance of the listener's ear to
        // each audio source in units of phase angle of the current frequency
        earLocation.setLocation( this.getLocation().getX() + s_earOffsetX,
                                 this.getLocation().getY() + s_earOffsetY );
        double distA = (float)earLocation.distance( audioSourceA );
        double distB = (float)earLocation.distance( audioSourceB );

        double lambda = interferringWavefront.getWavelengthAtTime( 0 );
        double theta = ( ( distA - distB ) / lambda ) * Math.PI;

        // The amplitude factor for max amplitude is the sum of the two wavefront
        // amplitudes times the cosine of the phase angle
        double amplitudeA = soundModel.getAmplitude();
        double maxAmplitude = amplitudeA * Math.abs( Math.cos( theta ) );
        soundModule.getPrimaryOscillator().setAmplitude( maxAmplitude );
    }

    public void setAudioSourceA( Point2D.Double audioSourceA ) {
        this.audioSourceA = audioSourceA;
    }

    public void setAudioSourceB( Point2D.Double audioSourceB ) {
        this.audioSourceB = audioSourceB;
    }

    //
    // Static fields and methods
    //
    private static int s_earOffsetX = 0;
    private static int s_earOffsetY = 78;
}
