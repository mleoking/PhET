/**
 * Class: SingleSourceApparatusPanel
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 4, 2004
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.sound.SoundConfig;
import edu.colorado.phet.sound.model.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class SingleSourceApparatusPanel extends SoundApparatusPanel {

    //    private PhetImageGraphic speakerFrame;
    //    private PhetImageGraphic speakerCone;
//    private WaveMediumGraphic waveMediumGraphic = null;
    private BufferedImage speakerFrameImg;
    private BufferedImage speakerConeImg;
    private int audioSource = SPEAKER_SOURCE;
    private boolean audioEnabledOnActivation;
    private SpeakerGraphic speakerGraphic;


    /**
     * @param model
     */
    public SingleSourceApparatusPanel( SoundModel model ) {
        super( model );
        this.model = model;
//        final WaveMedium waveMedium = model.getWaveMedium();
//        waveMediumGraphic = new WaveMediumGraphic( waveMedium, this );
//        this.addGraphic( waveMediumGraphic, 7 );
//        Point2D.Double audioSource = new Point2D.Double( SoundConfig.s_wavefrontBaseX,
//                                                         SoundConfig.s_wavefrontBaseY );
//        waveMediumGraphic.initLayout( audioSource,
//                                      SoundConfig.s_wavefrontHeight,
//                                      SoundConfig.s_wavefrontRadius );

        // Set up the octave wavefront and graphic
        //        WaveMediumGraphic wgB = new WaveMediumGraphic( waveMedium, this );
        //        waveMediumGraphic.init( waveMedium );
        //        this.addGraphic( waveMediumGraphic, 7 );

        //        Point2D.Double audioSourceB = new Point2D.Double( SoundConfig.s_wavefrontBaseX,
        //                                                          SoundConfig.s_wavefrontBaseY );
        //        waveMediumGraphic.initLayout( audioSourceB,
        //                                     SoundConfig.s_wavefrontHeight,
        //                                     SoundConfig.s_wavefrontRadius );

//        setWavefrontType( new SphericalWavefront() );
        this.setBackground( SoundConfig.MIDDLE_GRAY );

        // Set up the speaker
        final WaveMedium waveMedium = model.getWaveMedium();
        speakerGraphic = new SpeakerGraphic( this, waveMedium );
        this.addGraphic( speakerGraphic, 8 );
        waveMedium.addObserver( new SimpleObserver() {
            private int s_maxSpeakerConeExcursion = 6;

            public void update() {
                int coneOffset = (int)( waveMedium.getAmplitudeAt( 0 ) / SoundConfig.s_maxAmplitude * s_maxSpeakerConeExcursion );
                speakerGraphic.setConePosition( coneOffset );
            }
        } );
    }

    /**
     *
     */
    //    public void init() {
    //    }

    /**
     * @return
     */
    protected Image getSpeakerFrameImg() {
        return speakerFrameImg;
    }

    /**
     * @return
     */
    protected Image getSpeakerConeImg() {
        return speakerConeImg;
    }

    /**
     *
     */
//    public void setWavefrontType( WavefrontType wavefrontType ) {
//        waveMediumGraphic.setPlanar( wavefrontType instanceof PlaneWavefront );
//    }

    /**
     * @return
     */
//    protected WaveMediumGraphic getWavefrontGraphic() {
//        return waveMediumGraphic;
//    }

    /**
     * TODO: refactor this class so that we don't need this to make interference work
     *
     * @return
     */
    //    protected PhetGraphic getSpeakerFrame() {
    //        return speakerFrame;
    //    }

    /**
     * TODO: refactor this class so that we don't need this to make interference work
     *
     * @return
     */
    //    protected PhetGraphic getSpeakerCone() {
    //        return speakerCone;
    //    }

    /**
     * Gets the amplitude at the speaker or the listener, depending on what is
     * specified by the control panel
     *
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
     * @return
     */
    protected int getAudioSource() {
        return audioSource;
    }
}
