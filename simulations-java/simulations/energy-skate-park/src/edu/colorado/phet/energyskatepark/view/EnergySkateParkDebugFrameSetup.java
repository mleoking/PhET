package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Sam Reid
* Jun 3, 2007, 3:33:45 PM
*/
public class EnergySkateParkDebugFrameSetup implements FrameSetup {
    public void initialize( JFrame frame ) {
        frame.setSize( Toolkit.getDefaultToolkit().getScreenSize().width - EnergySkateParkModule.energyFrameWidth,
                       Toolkit.getDefaultToolkit().getScreenSize().height - 100 - EnergySkateParkModule.chartFrameHeight //for debug
        );
        frame.setLocation( 0, 0 );
    }
}
