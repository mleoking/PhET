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

    protected SingleSourceListenModule( ApplicationModel appModel) {
        super( appModel, "<html>Listen to<br>Single Source</html>" );

        // Add the listener
        BufferedImage headImg = null;
        try {
            headImg = ImageLoader.loadBufferedImage( SoundConfig.HEAD_IMAGE_FILE );
            PhetImageGraphic head = new PhetImageGraphic( getApparatusPanel(), headImg );
            head.setPosition( SoundConfig.s_headBaseX, SoundConfig.s_headBaseY );
            SoundModel model = (SoundModel)getModel();
            ListenerGraphic listener = new ListenerGraphic( model, new Listener(), head,
                                                            SoundConfig.s_headBaseX, SoundConfig.s_headBaseY,
                                                            SoundConfig.s_headBaseX - 150, SoundConfig.s_headBaseY,
                                                            SoundConfig.s_headBaseX + 150, SoundConfig.s_headBaseY );
            this.addGraphic( listener, 9 );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }        
    }
}
