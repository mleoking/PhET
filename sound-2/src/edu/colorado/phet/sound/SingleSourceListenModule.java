/**
 * Class: SingleSourceListenModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 6, 2004
 */
package edu.colorado.phet.sound;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.sound.view.ListenerGraphic;
import edu.colorado.phet.sound.model.Listener;
import edu.colorado.phet.sound.model.SoundModel;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class SingleSourceListenModule extends SingleSourceModule {
    private SoundModel soundModel;

    protected SingleSourceListenModule( ApplicationModel appModel) {
        super( appModel, "<html>Listen to<br>Single Source</html>" );

        // Add the listener
        BufferedImage headImg = null;
        try {
            headImg = ImageLoader.loadBufferedImage( SoundConfig.HEAD_IMAGE_FILE );
            PhetImageGraphic head = new PhetImageGraphic( getApparatusPanel(), headImg );
            head.setPosition( SoundConfig.s_headBaseX, SoundConfig.s_headBaseY );
            soundModel = (SoundModel)getModel();
            ListenerGraphic listener = new ListenerGraphic( soundModel, new Listener(), head,
                                                            SoundConfig.s_headBaseX, SoundConfig.s_headBaseY,
                                                            SoundConfig.s_headBaseX - 150, SoundConfig.s_headBaseY,
                                                            SoundConfig.s_headBaseX + 150, SoundConfig.s_headBaseY );
            this.addGraphic( listener, 9 );
            this.setListenerLocation( SoundConfig.s_headBaseX, SoundConfig.s_headBaseY );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }        
    }

    private void setListenerLocation( int x, int y ) {
        soundModel.setListenerLocation( x, y );
    }

}
