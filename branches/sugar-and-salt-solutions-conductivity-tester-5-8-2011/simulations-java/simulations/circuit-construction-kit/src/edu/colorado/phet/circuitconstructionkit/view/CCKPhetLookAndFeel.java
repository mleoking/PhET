// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view;

import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 11, 2006
 * Time: 12:32:54 AM
 */

public class CCKPhetLookAndFeel extends PhetLookAndFeel {
    private static Color cckBackground = new Color(200, 240, 200);  // light green

    public CCKPhetLookAndFeel() {
        setBackgroundColor(cckBackground);
    }

    protected String getLookAndFeelClassName() {
        return UIManager.getSystemLookAndFeelClassName();
    }
}
