/**
 * Class: SingleSourceListenModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 6, 2004
 */
package edu.colorado.phet.sound;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.sound.model.Listener;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.view.ListenerGraphic;
import edu.colorado.phet.sound.view.SoundApparatusPanel;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SingleSourceListenModule extends SingleSourceModule {
    private SoundModel soundModel;
    private Listener speakerListener = new Listener();
    private Listener headListener = new Listener();
//    private Listener currentListener;

    protected SingleSourceListenModule( ApplicationModel appModel ) {
        super( appModel, "<html>Listen to<br>Single Source</html>" );

        // Add the listener
        BufferedImage headImg = null;
        try {
            headImg = ImageLoader.loadBufferedImage( SoundConfig.HEAD_IMAGE_FILE );
            PhetImageGraphic head = new PhetImageGraphic( getApparatusPanel(), headImg );
            head.setPosition( SoundConfig.s_headBaseX, SoundConfig.s_headBaseY );
            soundModel = (SoundModel)getModel();
            ListenerGraphic listenerGraphic = new ListenerGraphic( this, headListener, head,
                                                                   SoundConfig.s_headBaseX, SoundConfig.s_headBaseY,
                                                                   SoundConfig.s_headBaseX - 150, SoundConfig.s_headBaseY,
                                                                   SoundConfig.s_headBaseX + 150, SoundConfig.s_headBaseY );
            {
                this.addGraphic( listenerGraphic, 9 );
            }
            headListener.setLocation( new Point2D.Double(  SoundConfig.s_headBaseX - SoundConfig.s_speakerBaseX,
                                                           SoundConfig.s_headBaseY - SoundConfig.s_speakerBaseY) );
            speakerListener.setLocation( new Point2D.Double() );
            setAudioSource( SoundApparatusPanel.SPEAKER_SOURCE );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public void setAudioSource( int source ) {
        switch (source ) {
            case SoundApparatusPanel.LISTENER_SOURCE:
                setListener( headListener );
                break;
            case SoundApparatusPanel.SPEAKER_SOURCE:
                setListener( speakerListener );
                break;
        }
    }
}
