package edu.colorado.phet.motion2d;

import edu.colorado.phet.motion2d.phetcommon.PhetLookAndFeel;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Sep 2, 2006
 * Time: 6:30:33 PM
 * Copyright (c) Sep 2, 2006 by Sam Reid
 */

public class Motion2DLookAndFeel extends PhetLookAndFeel {
    protected String getLookAndFeelClassName() {
        return UIManager.getSystemLookAndFeelClassName();
    }
}
