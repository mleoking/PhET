/** Sam Reid*/
package edu.colorado.phet.common.view.plaf;

import edu.colorado.phet.common.examples.TestComponents;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalCheckBoxUI;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 2, 2004
 * Time: 2:45:20 PM
 * Copyright (c) Apr 2, 2004 by Sam Reid
 */
public class MyCheckBoxUI extends MetalCheckBoxUI {

    private ImageIcon pressed;
    private ImageIcon unpressed;

    public MyCheckBoxUI() {
        super();
        pressed = new ImageIcon( TestComponents.class.getClassLoader().getResource( "images/components/webt/on.gif" ) );
        unpressed = new ImageIcon( TestComponents.class.getClassLoader().getResource( "images/components/webt/off.gif" ) );
    }

    public static ComponentUI createUI( JComponent c ) {
        return new MyCheckBoxUI();
    }

    public void paint( Graphics g, JComponent c ) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        super.paint( g, c );
    }

    public void installUI( JComponent c ) {
        super.installUI( c );
        JCheckBox button = (JCheckBox)c;

        button.setIcon( unpressed );
        button.setPressedIcon( pressed );
        button.setSelectedIcon( pressed );
    }
}
