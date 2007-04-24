/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 27, 2006
 * Time: 12:01:24 PM
 *
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
