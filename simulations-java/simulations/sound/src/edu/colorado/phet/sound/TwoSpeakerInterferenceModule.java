/**
 * Class: TwoSpeakerInterferenceModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 11, 2004
 */
package edu.colorado.phet.sound;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetgraphics.view.help.HelpItem;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.sound.model.SoundListener;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.model.WaveMedium;
import edu.colorado.phet.sound.view.*;

public class TwoSpeakerInterferenceModule extends SoundModule {

    private SoundListener speakerListener;
    private SoundListener headListener;
    private Point2D.Double audioSourceA;
    private Point2D.Double audioSourceB;
    private SoundModel soundModel;
    private boolean saveInterferenceOverideEnabled = false;
    private final SoundApparatusPanel apparatusPanel;

    protected TwoSpeakerInterferenceModule() {
        super( SoundResources.getString( "ModuleTitle.TwoSpeakerIntererence" ) );
        soundModel = getSoundModel();
        speakerListener = new SoundListener( soundModel, new Point2D.Double() );
        speakerListener.setLocation( new Point2D.Double() );
        setListener( speakerListener );
        headListener = new SoundListener( soundModel, new Point2D.Double() );

        apparatusPanel = new SoundApparatusPanel( soundModel, getClock() );
        setApparatusPanel( apparatusPanel );
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
    private void initApparatusPanel() {
        // Determine the origins of the interferning wavefronts
        audioSourceA = new Point2D.Double( SoundConfig.s_wavefrontBaseX,
                                           SoundConfig.s_wavefrontBaseY - 120 );
        audioSourceB = new Point2D.Double( SoundConfig.s_wavefrontBaseX,
                                           SoundConfig.s_wavefrontBaseY + 120 );

        // Create the upper wave and speaker
        WaveMedium wm = getSoundModel().getWaveMedium();
        BufferedWaveMediumGraphic wgA = new BufferedWaveMediumGraphic( wm, apparatusPanel );
        wm.addObserver( wgA );
        apparatusPanel.addGraphic( wgA, 5 );
        wgA.initLayout( audioSourceA,
                        SoundConfig.s_wavefrontHeight,
                        SoundConfig.s_wavefrontRadius );
//        wgA.setOpacity( 0.5f );
        wgA.setOpacity( 1.0f );
        SpeakerGraphic speakerGraphicA = new SpeakerGraphic( apparatusPanel, wm );
        speakerGraphicA.setLocation( SoundConfig.s_speakerBaseX, (int)audioSourceA.getY() );
        InteractiveSpeakerGraphic iSpeakerGraphicA = new InteractiveSpeakerGraphic( speakerGraphicA, wgA );

        iSpeakerGraphicA.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                InteractiveSpeakerGraphic isg = (InteractiveSpeakerGraphic)e.getSource();
                audioSourceA.setLocation( isg.getAudioSourceLocation() );
            }
        } );

        apparatusPanel.addGraphic( iSpeakerGraphicA, 8 );

        // Add the lower wave and speaker
        BufferedWaveMediumGraphic wgB = new BufferedWaveMediumGraphic( wm, apparatusPanel );
        wm.addObserver( wgB );
        apparatusPanel.addGraphic( wgB, 5 );
        wgB.initLayout( audioSourceB,
                        SoundConfig.s_wavefrontHeight,
                        SoundConfig.s_wavefrontRadius );
//        wgB.setOpacity( 1f );
        wgB.setOpacity( 0.5f );
        SpeakerGraphic speakerGraphicB = new SpeakerGraphic( apparatusPanel, wm );
        apparatusPanel.addGraphic( speakerGraphicB, 8 );
        speakerGraphicB.setLocation( SoundConfig.s_speakerBaseX, (int)audioSourceB.getY() );

        // Set up the listener
        BufferedImage headImg = null;
        try {
            int headImageIdx = randomGenerator.nextInt( SoundConfig.HEAD_IMAGE_FILES.length );
            headImg = ImageLoader.loadBufferedImage( SoundConfig.HEAD_IMAGE_FILES[headImageIdx] );
            headImg = ImageLoader.loadBufferedImage( SoundConfig.HEAD_IMAGE_FILE );
            PhetImageGraphic head = new PhetImageGraphic( apparatusPanel, headImg );
            head.setLocation( SoundConfig.s_headBaseX, SoundConfig.s_headBaseY );
            ListenerGraphic listenerGraphic = new InterferenceListenerGraphic( this, headListener, head,
                                                                               (double)SoundConfig.s_headBaseX, (double)SoundConfig.s_headBaseY,
                                                                               (double)SoundConfig.s_headBaseX - 150, (double)SoundConfig.s_headBaseY - 200,
                                                                               (double)SoundConfig.s_headBaseX + 150, (double)SoundConfig.s_headBaseY + 200,
                                                                               audioSourceA,
                                                                               audioSourceB,
                                                                               soundModel.getPrimaryWavefront(),
                                                                               iSpeakerGraphicA );
            apparatusPanel.addGraphic( listenerGraphic, 9 );

            // Add help items
            HelpItem help1 = new HelpItem( apparatusPanel,
                                           SoundResources.getString( "TwoSpeakerInterferenceModule.Help1" ),
                                           SoundConfig.s_headBaseX,
                                           SoundConfig.s_headBaseY - 20,
                                           HelpItem.RIGHT, HelpItem.ABOVE );
            help1.setForegroundColor( Color.white );
            addHelpItem( help1 );

            HelpItem help2 = new HelpItem( apparatusPanel,
                                           SoundResources.getString( "TwoSpeakerInterferenceModule.Help2" ),
                                           SoundConfig.s_speakerBaseX, (int)audioSourceA.getY() - 120,
                                           HelpItem.RIGHT, HelpItem.ABOVE );
            help2.setForegroundColor( Color.white );
            addHelpItem( help2 );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
    
    public boolean hasHelp() {
        return true;
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

    public void activate() {
        super.activate();
        getPrimaryOscillator().setInterferenceOverideEnabled( saveInterferenceOverideEnabled );
    }

    public void deactivate() {
        super.deactivate();
        saveInterferenceOverideEnabled = getPrimaryOscillator().getInterferenceOverideEnabled();
    }

    public int rgbAt( int x, int y ) {
        return super.rgbAt( x, y ) * 3 / 2;
    }
}
