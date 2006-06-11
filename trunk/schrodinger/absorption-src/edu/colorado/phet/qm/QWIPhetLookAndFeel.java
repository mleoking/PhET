/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.view.PhetLookAndFeel;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 11, 2006
 * Time: 10:21:56 AM
 * Copyright (c) Jun 11, 2006 by Sam Reid
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
