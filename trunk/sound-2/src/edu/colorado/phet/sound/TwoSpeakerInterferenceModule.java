/**
 * Class: TwoSpeakerInterferenceModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 11, 2004
 */
package edu.colorado.phet.sound;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.sound.model.Listener;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.model.WaveMedium;
import edu.colorado.phet.sound.view.*;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TwoSpeakerInterferenceModule extends SoundModule {

    private Listener speakerListener = new Listener();
    private Listener headListener = new Listener();

    private WaveMediumGraphic primaryWg;
    private InterferenceListenerGraphic head;
    private Point2D.Double audioSourceA;
    private Point2D.Double audioSourceB;
    private SoundModel soundModel;
    private boolean saveInterferenceOverideEnabled = false;

    protected TwoSpeakerInterferenceModule( ApplicationModel appModel ) {
        super( appModel, "<html>Two Source<br>Interference</html>" );
        soundModel = (SoundModel)getModel();
        setApparatusPanel( new SoundApparatusPanel( soundModel ) );
        initApparatusPanel();
        initControlPanel();
    }

    private void initControlPanel() {
        this.setControlPanel( new TwoSourceInterferenceControlPanel( this ) );
    }

    /**
     * This initialization function must currently be called from activate(), because
     * it requires the main panel to be in place.
     */
    public void initApparatusPanel() {
        // Determine the origins of the interferning wavefronts
        audioSourceA = new Point2D.Double( SoundConfig.s_wavefrontBaseX,
                                           SoundConfig.s_wavefrontBaseY - 120 );
        audioSourceB = new Point2D.Double( SoundConfig.s_wavefrontBaseX,
                                           SoundConfig.s_wavefrontBaseY + 120 );

        // Create the upper wave and speaker
        WaveMedium wm = getSoundModel().getWaveMedium();
        WaveMediumGraphic wgA = new WaveMediumGraphic( wm, getApparatusPanel() );
        wm.addObserver( wgA );
        this.addGraphic( wgA, 5 );
        wgA.initLayout( audioSourceA,
                        SoundConfig.s_wavefrontHeight,
                        SoundConfig.s_wavefrontRadius );
        wgA.setOpacity( 0.5f );
        SpeakerGraphic speakerGraphicA = new SpeakerGraphic( getApparatusPanel(), wm );
        getApparatusPanel().addGraphic( speakerGraphicA, 8 );
        speakerGraphicA.setLocation( SoundConfig.s_speakerBaseX, (int)audioSourceA.getY() );

        // Add the lower wave and speaker
        WaveMediumGraphic wgB = new WaveMediumGraphic( wm, getApparatusPanel() );
        wm.addObserver( wgB );
        this.addGraphic( wgB, 5 );
        wgB.initLayout( audioSourceB,
                        SoundConfig.s_wavefrontHeight,
                        SoundConfig.s_wavefrontRadius );
        wgB.setOpacity( 0.5f );
        SpeakerGraphic speakerGraphicB = new SpeakerGraphic( getApparatusPanel(), wm );
        getApparatusPanel().addGraphic( speakerGraphicB, 8 );
        speakerGraphicB.setLocation( SoundConfig.s_speakerBaseX, (int)audioSourceB.getY() );

        // Set up the listener
        BufferedImage headImg = null;
        try {
            headImg = ImageLoader.loadBufferedImage( SoundConfig.LISTENER_W_EARS_IMAGE_FILE );
            PhetImageGraphic head = new PhetImageGraphic( getApparatusPanel(), headImg );
            head.setPosition( SoundConfig.s_headBaseX, SoundConfig.s_headBaseY );
            ListenerGraphic listenerGraphic = new InterferenceListenerGraphic( this, headListener, head,
                                                                               (double)SoundConfig.s_headBaseX, (double)SoundConfig.s_headBaseY,
                                                                               (double)SoundConfig.s_headBaseX - 150, (double)SoundConfig.s_headBaseY - 200,
                                                                               (double)SoundConfig.s_headBaseX + 150, (double)SoundConfig.s_headBaseY + 200,
                                                                               audioSourceA,
                                                                               audioSourceB,
                                                                               soundModel.getPrimaryWavefront() );
            getApparatusPanel().addGraphic( listenerGraphic, 9 );

            headListener.setLocation( new Point2D.Double( SoundConfig.s_headBaseX - SoundConfig.s_speakerBaseX,
                                                          SoundConfig.s_headBaseY - SoundConfig.s_speakerBaseY ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public void setAudioSource( int source ) {
        switch( source ) {
            case SoundApparatusPanel.LISTENER_SOURCE:
                setListener( headListener );
                getPrimaryOscillator().setInterferenceOverideEnabled( true );
                break;
            case SoundApparatusPanel.SPEAKER_SOURCE:
                setListener( speakerListener );
                getPrimaryOscillator().setInterferenceOverideEnabled( false );
                break;
        }
    }

    public void activate( PhetApplication app ) {
        super.activate( app );
        getPrimaryOscillator().setInterferenceOverideEnabled( saveInterferenceOverideEnabled );
    }

    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        saveInterferenceOverideEnabled = getPrimaryOscillator().getInterferenceOverideEnabled();
        getPrimaryOscillator().setInterferenceOverideEnabled( false );
    }
}
