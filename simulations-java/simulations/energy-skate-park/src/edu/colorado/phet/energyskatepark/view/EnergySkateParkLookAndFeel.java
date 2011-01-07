// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 27, 2006
 * Time: 12:01:24 PM
 */

public class EnergySkateParkLookAndFeel extends PhetLookAndFeel {
    public static final Color backgroundColor = new Color( 200, 240, 200 );

    public EnergySkateParkLookAndFeel() {
        setBackgroundColor( backgroundColor );
        setTextFieldBackgroundColor( Color.white );
        setTabFont( new PhetFont( Font.BOLD, 18 ) );
    }

    protected String getLookAndFeelClassName() {
        return UIManager.getSystemLookAndFeelClassName();//windows on windows, please
    }
}
