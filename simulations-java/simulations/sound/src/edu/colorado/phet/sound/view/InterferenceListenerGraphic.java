/**
 * Class: InterferenceListenerGraphic
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 11, 2004
 */
package edu.colorado.phet.sound.view;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationEvent;
import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationListener;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.sound.TwoSpeakerInterferenceModule;
import edu.colorado.phet.sound.model.Listener;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.model.Wavefront;

public class InterferenceListenerGraphic extends ListenerGraphic {

    private Point2D.Double audioSourceA;
    private Point2D.Double audioSourceB;
    private Wavefront interferringWavefront;
    private Point2D.Double earLocation = new Point2D.Double();
    private TwoSpeakerInterferenceModule soundModule;
    private SoundModel soundModel;

    /**
     *
     */
    public InterferenceListenerGraphic( TwoSpeakerInterferenceModule module, Listener listener,
                                        PhetImageGraphic image, double x, double y,
                                        double minX, double minY,
                                        double maxX, double maxY,
                                        final Point2D.Double audioSourceA,
                                        Point2D.Double audioSourceB,
                                        Wavefront interferringWavefront,
                                        InteractiveSpeakerGraphic moveableSpeaker ) {
        super( module, listener, image, x, y, minX, minY, maxX, maxY );
        this.soundModule = module;
        this.soundModel = (SoundModel)module.getModel();
        this.audioSourceA = audioSourceA;
        this.audioSourceB = audioSourceB;
        this.interferringWavefront = interferringWavefront;

        // If this graphic moves, update the amplitude
        addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent translationEvent ) {
                updateAmplitude();
            }
        } );

        // If the moveable speaker moves, we need to update the amplitude
        moveableSpeaker.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateAmplitude();
            }
        } );

        updateAmplitude();
    }

    /**
     * Used for debug. Draws small red circles at the audio sources and the ear location
     *
     * @param g
     */
    public void paint( Graphics2D g ) {
        super.paint( g );

        // debug
//        g.setColor( Color.red );
//        g.drawArc( (int)earLocation.x - 2, (int)earLocation.y - 2, 4, 4, 0, 360 );
//        g.drawArc( (int)audioSourceA.x - 2, (int)audioSourceA.y - 2, 4, 4, 0, 360 );
//        g.drawArc( (int)audioSourceB.x - 2, (int)audioSourceB.y - 2, 4, 4, 0, 360 );
    }

    /**
     *
     */
    private synchronized void updateAmplitude() {
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

//    public void mouseReleased( final InteractiveSpeakerGraphic interactiveGraphic ) {
//
//        System.out.println( "InterferenceListenerGraphic.mouseReleased" );
//
//        // Determine how far away the speaker that moved is, then set a
//        // timer to wait until the first wave leaving it now will reach us.
//        // When the timer pops, update our amplitude
//        double dist = this.getLocation().distance( interactiveGraphic.getAudioSourceLocation() );
//        final double numTimeSteps = dist / SoundConfig.PROPOGATION_SPEED;
//        Thread sleeper = new Thread( new Runnable() {
//            int counter;
//
//            public void run() {
//                ModelElement counterModelElement = new ModelElement() {
//                    public synchronized void stepInTime( double dt ) {
//                        counter++;
//                    }
//                };
//                InterferenceListenerGraphic.this.soundModel.addModelElement( counterModelElement );
//                while( counter < numTimeSteps ) {
//                    try {
//                        Thread.sleep( 50 );
//                    }
//                    catch( InterruptedException e ) {
//                        e.printStackTrace();
//                    }
//                }
//                soundModel.removeModelElement( counterModelElement );
//                audioSourceA = interactiveGraphic.getAudioSourceLocation();
//                InterferenceListenerGraphic.this.updateAmplitude();
//                System.out.println( "!!!" );
//            }
//        } );
//        sleeper.start();
//    }
}
