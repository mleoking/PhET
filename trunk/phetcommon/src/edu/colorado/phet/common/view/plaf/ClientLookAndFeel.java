/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.plaf;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;

/**
 * ClientLookAndFeel
 *
 * @author ?
 * @version $Revision$
 */
public class ClientLookAndFeel extends MetalLookAndFeel {
    Color backgroundColor = new Color( 200, 240, 200 );
    Color buttonBackgroundColor = new Color( 220, 220, 240 );

    protected void initComponentDefaults( UIDefaults table ) {
        super.initComponentDefaults( table );
        ColorUIResource background = new ColorUIResource( backgroundColor );
        ColorUIResource buttonBackground = new ColorUIResource( buttonBackgroundColor );
        Object[] defaults = {
            "Panel.background", background
            , "Menu.background", background
            , "MenuItem.background", background
            , "MenuBar.background", background
            , "Slider.background", background
            , "RadioButton.background", background
            , "CheckBox.background", background
            , "Button.background", buttonBackground
            , "ComboBox.background", background
        };
        table.putDefaults( defaults );
    }

}