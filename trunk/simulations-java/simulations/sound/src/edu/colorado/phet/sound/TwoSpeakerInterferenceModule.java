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
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.phetgraphics.view.help.HelpItem;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common_sound.application.PhetApplication;
import edu.colorado.phet.sound.model.Listener;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.model.WaveMedium;
import edu.colorado.phet.sound.view.*;

public class TwoSpeakerInterferenceModule extends SoundModule {

    private Listener speakerListener;
    private Listener headListener;
    private Point2D.Double audioSourceA;
    private Point2D.Double audioSourceB;
    private SoundModel soundModel;
    private boolean saveInterferenceOverideEnabled = false;

    protected TwoSpeakerInterferenceModule( SoundApplication application ) {
        super( application, SimStrings.get( "ModuleTitle.TwoSpeakerIntererence" ) );
        soundModel = (SoundModel)getModel();
        speakerListener = new Listener( (SoundModel)getModel(),
                                        new Point2D.Double() );
        speakerListener.setLocation( new Point2D.Double() );
        setListener( speakerListener );
        headListener = new Listener( (SoundModel)getModel(),
                                     new Point2D.Double() );

        setApparatusPanel( new SoundApparatusPanel( soundModel, application.getClock() ) );
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
        BufferedWaveMediumGraphic wgA = new BufferedWaveMediumGraphic( wm, getApparatusPanel() );
        wm.addObserver( wgA );
        this.addGraphic( wgA, 5 );
        wgA.initLayout( audioSourceA,
                        SoundConfig.s_wavefrontHeight,
                        SoundConfig.s_wavefrontRadius );
//        wgA.setOpacity( 0.5f );
        wgA.setOpacity( 1.0f );
        SpeakerGraphic speakerGraphicA = new SpeakerGraphic( getApparatusPanel(), wm );
        speakerGraphicA.setLocation( SoundConfig.s_speakerBaseX, (int)audioSourceA.getY() );
        InteractiveSpeakerGraphic iSpeakerGraphicA = new InteractiveSpeakerGraphic( speakerGraphicA, wgA );

        iSpeakerGraphicA.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                InteractiveSpeakerGraphic isg = (InteractiveSpeakerGraphic)e.getSource();
                audioSourceA.setLocation( isg.getAudioSourceLocation() );
            }
        } );

        getApparatusPanel().addGraphic( iSpeakerGraphicA, 8 );

        // Add the lower wave and speaker
        BufferedWaveMediumGraphic wgB = new BufferedWaveMediumGraphic( wm, getApparatusPanel() );
        wm.addObserver( wgB );
        this.addGraphic( wgB, 5 );
        wgB.initLayout( audioSourceB,
                        SoundConfig.s_wavefrontHeight,
                        SoundConfig.s_wavefrontRadius );
//        wgB.setOpacity( 1f );
        wgB.setOpacity( 0.5f );
        SpeakerGraphic speakerGraphicB = new SpeakerGraphic( getApparatusPanel(), wm );
        getApparatusPanel().addGraphic( speakerGraphicB, 8 );
        speakerGraphicB.setLocation( SoundConfig.s_speakerBaseX, (int)audioSourceB.getY() );

        // Set up the listener
        BufferedImage headImg = null;
        try {
            int headImageIdx = randomGenerator.nextInt( SoundConfig.HEAD_IMAGE_FILES.length );
            headImg = ImageLoader.loadBufferedImage( SoundConfig.HEAD_IMAGE_FILES[headImageIdx] );
            headImg = ImageLoader.loadBufferedImage( SoundConfig.HEAD_IMAGE_FILE );
            PhetImageGraphic head = new PhetImageGraphic( getApparatusPanel(), headImg );
            head.setLocation( SoundConfig.s_headBaseX, SoundConfig.s_headBaseY );
            ListenerGraphic listenerGraphic = new InterferenceListenerGraphic( this, headListener, head,
                                                                               (double)SoundConfig.s_headBaseX, (double)SoundConfig.s_headBaseY,
                                                                               (double)SoundConfig.s_headBaseX - 150, (double)SoundConfig.s_headBaseY - 200,
                                                                               (double)SoundConfig.s_headBaseX + 150, (double)SoundConfig.s_headBaseY + 200,
                                                                               audioSourceA,
                                                                               audioSourceB,
                                                                               soundModel.getPrimaryWavefront(),
                                                                               iSpeakerGraphicA );
            this.addGraphic( listenerGraphic, 9 );

            // Add help items
            HelpItem help1 = new HelpItem( getApparatusPanel(),
                                           SimStrings.get( "TwoSpeakerInterferenceModule.Help1" ),
                                           SoundConfig.s_headBaseX,
                                           SoundConfig.s_headBaseY - 20,
                                           HelpItem.RIGHT, HelpItem.ABOVE );
            help1.setForegroundColor( Color.white );
            addHelpItem( help1 );

            HelpItem help2 = new HelpItem( getApparatusPanel(),
                                           SimStrings.get( "TwoSpeakerInterferenceModule.Help2" ),
                                           SoundConfig.s_speakerBaseX, (int)audioSourceA.getY() - 120,
                                           HelpItem.RIGHT, HelpItem.ABOVE );
            help2.setForegroundColor( Color.white );
            addHelpItem( help2 );
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
    }

    public int rgbAt( int x, int y ) {
        return super.rgbAt( x, y ) * 3 / 2;
    }
}
