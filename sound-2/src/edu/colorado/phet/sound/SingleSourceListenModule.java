/**
 * Class: SingleSourceListenModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 6, 2004
 */
package edu.colorado.phet.sound;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.view.help.HelpItem;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.sound.model.Listener;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.view.AudioControlPanel;
import edu.colorado.phet.sound.view.ListenerGraphic;
import edu.colorado.phet.sound.view.SoundApparatusPanel;
import edu.colorado.phet.sound.view.SoundControlPanel;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SingleSourceListenModule extends SingleSourceModule {

    private Listener speakerListener;
    private Listener headListener;
    private int headOffsetY = 20;
    private AudioControlPanel audioControlPanel;

    public SingleSourceListenModule( ApplicationModel appModel ) {
        this( appModel, "<html>Listen to<br>Single Source</html>" );
    }

    protected SingleSourceListenModule( ApplicationModel appModel, String title ) {
        super( appModel, title );
        init();
    }

    private void init() {
        // Add the listener
        speakerListener = new Listener( (SoundModel)getModel(),
                                        new Point2D.Double() );
        speakerListener.setLocation( new Point2D.Double() );
        setListener( speakerListener );
        headListener = new Listener( (SoundModel)getModel(),
                                     new Point2D.Double() );
        headListener.addObserver( getPrimaryOscillator() );
        headListener.addObserver( getOctaveOscillator() );
        BufferedImage headImg = null;
        try {
            headImg = ImageLoader.loadBufferedImage( SoundConfig.HEAD_IMAGE_FILE );
            PhetImageGraphic head = new PhetImageGraphic( getApparatusPanel(), headImg );
            head.setPosition( SoundConfig.s_headBaseX, SoundConfig.s_headBaseY );
            ListenerGraphic listenerGraphic = new ListenerGraphic( this, headListener, head,
                                                                   SoundConfig.s_headBaseX, SoundConfig.s_headBaseY + headOffsetY,
                                                                   SoundConfig.s_headBaseX - 150, SoundConfig.s_headBaseY + headOffsetY,
                                                                   SoundConfig.s_headBaseX + 150, SoundConfig.s_headBaseY + headOffsetY );
            this.addGraphic( listenerGraphic, 9 );

            // Add help items
            HelpItem help1 = new HelpItem( "Listener can be moved\nleft and right",
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




        // TEST
        //        final ScalarObservable so = new ScalarObservable() {
        //            private int cnt;
        //            public double getValue() {
        //                cnt = ( ++cnt ) % 100;
        //                return cnt;
        //            }
        //        };
        //        getModel().addModelElement( new ModelElement() {
        //            public void stepInTime( double dt ) {
        //                so.notifyObservers();
        //            }
        //        } );
        //        getApparatusPanel().addGraphic( new DialGauge( so, getApparatusPanel(), 400, 200, 100, 0, 100, "ATM" ), 100 );


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
