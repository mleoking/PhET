// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.util.PhetUtilities;

import javax.swing.*;

/**
 * Author: Sam Reid
 * Jun 4, 2007, 2:57:05 AM
 */
public class EnergySkateParkUtils {
    public static void fixButtonOpacity( JButton button ) {
        if( PhetUtilities.isMacintosh() ) {
            button.setOpaque( false );
        }
    }
}
