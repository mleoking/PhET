// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Sam Reid
 * Jun 3, 2007, 3:34:07 PM
 */
public class EnergySkateParkFrameSetup implements FrameSetup {
    public void initialize( JFrame frame ) {
        if( Toolkit.getDefaultToolkit().getScreenSize().height <= 768 ) {
            new MaxExtent( new TopCenter( Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height - 100 ) ).initialize( frame );
        }
        else {
            Toolkit tk = Toolkit.getDefaultToolkit();
            frame.setLocation( 0, 0 );
            frame.setSize( tk.getScreenSize().width - 200, tk.getScreenSize().height - 200 );
        }
    }
}
