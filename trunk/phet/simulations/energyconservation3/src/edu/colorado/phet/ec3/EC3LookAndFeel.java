/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.view.PhetLookAndFeel;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 27, 2006
 * Time: 12:01:24 PM
 * Copyright (c) Mar 27, 2006 by Sam Reid
 */

public class EC3LookAndFeel extends PhetLookAndFeel {
    public static final Color backgroundColor = new Color( 200, 240, 200 );

    public EC3LookAndFeel() {
        setBackgroundColor( backgroundColor );
        setTextFieldBackgroundColor( Color.white );
        setTabFont( new Font( "Lucida Sans", Font.BOLD, 18 ) );
    }

    protected String getLookAndFeelClassName() {
        return UIManager.getSystemLookAndFeelClassName();//windows on windows, please
    }
}
