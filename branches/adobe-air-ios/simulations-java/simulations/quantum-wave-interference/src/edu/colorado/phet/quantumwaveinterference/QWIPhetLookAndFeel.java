// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference;

import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 11, 2006
 * Time: 10:21:56 AM
 */

public class QWIPhetLookAndFeel extends PhetLookAndFeel {
    public static final Color backgroundColor = new Color( 200, 240, 200 );

    public QWIPhetLookAndFeel() {
        setBackgroundColor( backgroundColor );
    }

    protected String getLookAndFeelClassName() {
        return UIManager.getSystemLookAndFeelClassName();
    }
}
