/**
 * Class: SingleSourceListenModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 6, 2004
 */
package edu.colorado.phet.sound;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetgraphics.view.help.HelpItem;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.sound.model.SoundListener;
import edu.colorado.phet.sound.view.AudioControlPanel;
import edu.colorado.phet.sound.view.ListenerGraphic;
import edu.colorado.phet.sound.view.SoundApparatusPanel;
import edu.colorado.phet.sound.view.SoundControlPanel;

public class SingleSourceListenModule extends SingleSourceModule {

    private SoundListener speakerListener;
    private SoundListener headListener;
    private int headOffsetY = 20;
    private AudioControlPanel audioControlPanel;
    private ListenerGraphic listenerGraphic;

    public SingleSourceListenModule() {
        this( SoundResources.getString( "ModuleTitle.SingleSourceListen" ) );
    }

    protected SingleSourceListenModule( String title ) {
        super( title );
        init();
    }

    private void init() {
        // Add the listener
        speakerListener = new SoundListener( getSoundModel(), new Point2D.Double() );
        speakerListener.setLocation( new Point2D.Double() );
        setListener( speakerListener );
        headListener = new SoundListener( getSoundModel(), new Point2D.Double() );
        headListener.addObserver( getPrimaryOscillator() );
        headListener.addObserver( getOctaveOscillator() );
        BufferedImage headImg = null;
        try {
            int headImageIdx = randomGenerator.nextInt( SoundConfig.HEAD_IMAGE_FILES.length );
            headImg = ImageLoader.loadBufferedImage( SoundConfig.HEAD_IMAGE_FILES[headImageIdx] );
            PhetImageGraphic head = new PhetImageGraphic( getSimulationPanel(), headImg );
            head.setLocation( SoundConfig.s_headBaseX, SoundConfig.s_headBaseY );
            listenerGraphic = new ListenerGraphic( this, headListener, head,
                                                   SoundConfig.s_headBaseX, SoundConfig.s_headBaseY + headOffsetY,
                                                   SoundConfig.s_headBaseX - 150, SoundConfig.s_headBaseY + headOffsetY,
                                                   SoundConfig.s_headBaseX + 150, SoundConfig.s_headBaseY + headOffsetY );
            getApparatusPanel().addGraphic( listenerGraphic, 9 );

            // Add help items
            HelpItem help1 = new HelpItem( getSimulationPanel(),
                                           SoundResources.getString( "SingleSourceListenModule.help1" ),
                                           SoundConfig.s_headBaseX,
                                           SoundConfig.s_headBaseY + headOffsetY - 20,
                                           HelpItem.RIGHT, HelpItem.ABOVE );
            help1.setForegroundColor( Color.white );
            addHelpItem( help1 );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        // Set up the control panel
        SoundControlPanel controlPanel = (SoundControlPanel)getControlPanel();
        audioControlPanel = new AudioControlPanel( this );
        controlPanel.addPanel( audioControlPanel );
    }

    public boolean hasHelp() {
        return true;
    }
    
    protected ListenerGraphic getListenerGraphic() {
        return listenerGraphic;
    }

    protected SoundListener getSpeakerListener() {
        return speakerListener;
    }

    protected AudioControlPanel getAudioControlPanel() {
        return audioControlPanel;
    }

    public void setAudioSource( int source ) {
        switch( source ) {
            case SoundApparatusPanel.LISTENER_SOURCE:
                setListener( headListener );
                break;
            case SoundApparatusPanel.SPEAKER_SOURCE:
                setListener( speakerListener );
                break;
        }
    }
}
