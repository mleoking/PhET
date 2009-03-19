package edu.colorado.phet.waveinterference;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;


public class WaveInterferenceConstants {

    public static final String PROJECT_NAME = "wave-interference";
    public static final FrameSetup FRAME_SETUP = new FrameSetup() {
        int width = 1280;
        int height = 960;

        public void initialize( JFrame frame ) {
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension d = tk.getScreenSize();
            int x = ( d.width - width ) / 2;
            int y = ( d.height - height ) / 2;
            frame.setLocation( x, y );

            //test to see that the requested dimensions aren't bigger than the screen
            if ( d.width < width || d.height < height ) {
                new MaxExtent( new CenteredWithInsets( 10, 10 ) ).initialize( frame );
            }
            else {
                frame.setSize( width, height );
            }
        }
    };

}
