/**
 * Class: SoundApparatusPanel
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 4, 2004
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.model.Wavefront;
import edu.colorado.phet.sound.SoundConfig;
import edu.colorado.phet.common.view.ApparatusPanel;

import java.awt.geom.Point2D;
import java.awt.*;

public class SoundApparatusPanel extends ApparatusPanel {
    private int audioSource = SPEAKER_SOURCE;
    private double frequency = 0;
    private double amplitude = 0;
    // The point for which audio shoud be generated
    Point2D.Double audioReferencePt;

    //
    // Static fields and methods
    //
    public static int s_speakerConeOffsetX = 34;
    public static int s_speakerConeOffsetY = 2;
    public static int s_maxSpeakcerConeExcursion = 13;
    public final static int SPEAKER_SOURCE = 1;
    public final static int LISTENER_SOURCE = 2;
    SoundModel model;

    public SoundApparatusPanel( SoundModel model ) {
        this.model = model;
        this.setPreferredSize( new Dimension( 600, SoundConfig.s_speakerBaseY * 3 ));
    }

    /**
     * Gets the amplitude at the speaker or the listener, depending on what is
     * specified by the control panel
     * @param waveFront
     * @return
     */
    public double getCurrentMaxAmplitude( Wavefront waveFront ) {
        return waveFront.getMaxAmplitude();
    }

    /**
     *
     */
    public void setAudioSource( int audioSource ) {
        this.audioSource = audioSource;
    }

    /**
     *
     * @return
     */
    protected int getAudioSource() {
        return audioSource;
    }

    public Point2D.Double getAudioReferencePt() {
        return audioReferencePt;
    }

    /**
     * Sets the point in the wave medium for which audio is to be generated. The point is specified relative
     * to the origin of the wavefront.
     */
    public void determineAudioReferencPt() {
        switch( model.getAudioSource() ) {
            case SPEAKER_SOURCE:
                this.audioReferencePt = new Point2D.Double( 0, 0 );
                break;
            case LISTENER_SOURCE:
//                ListenerGraphic listenerGraphic = (ListenerGraphic)getGraphicOfType( ListenerGraphic.class );
//                if( listenerGraphic != null ) {
//                    this.audioReferencePt = listenerGraphic.getLocationInRange();
//                }
                break;
            default:
                throw new RuntimeException( "Bad parameter value" );
        }
        model.setAudioReferencePoint( this.audioReferencePt );
    }

    public void setPrimaryOscillatorFrequency( double frequency ) {
        if( model.getAudioSource() == LISTENER_SOURCE ) {
            model.setOscillatorFrequency( frequency );
        }
    }
}
