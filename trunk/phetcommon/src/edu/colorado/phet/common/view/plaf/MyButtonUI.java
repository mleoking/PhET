/** Sam Reid*/
package edu.colorado.phet.common.view.plaf;

import edu.colorado.phet.common.examples.TestComponents;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;

/**
 * User: Sam Reid
 * Date: Apr 2, 2004
 * Time: 2:45:20 PM
 * Copyright (c) Apr 2, 2004 by Sam Reid
 */
public class MyButtonUI extends MetalButtonUI {
    /*
    redbump_off_md_wht.gif
    */
//    private static ImageIcon pressed = new ImageIcon( TestComponents.class.getClassLoader().
//                                                      getResource( "images/components/webt/round-button-pressed.gif" ) );
//    private static ImageIcon unpressed = new ImageIcon( TestComponents.class.getClassLoader().
//                                                        getResource( "images/components/webt/round-button.gif" ) );
    private static ImageIcon pressed = new ImageIcon( TestComponents.class.getClassLoader().
                                                      getResource( "images/components/webt/redbump_on_md_wht.gif" ) );
    private static ImageIcon unpressed = new ImageIcon( TestComponents.class.getClassLoader().
                                                        getResource( "images/components/webt/redbump_off_md_wht.gif" ) );
    private JButton button;
    private Icon origUnpressed;
    private Icon origPressed;
    private MouseAdapter handAdapter = new MouseHandAdapter();

    public MyButtonUI( JButton button ) {
        super();
        this.button = button;
        origUnpressed = button.getIcon();
        origPressed = button.getPressedIcon();
    }

    public static ComponentUI createUI( JComponent c ) {
        return new MyButtonUI( (JButton)c );
    }

    public void paint( Graphics g, JComponent c ) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        super.paint( g, c );
    }

    // ********************************
    //         Uninstall PLAF
    // ********************************
    public void uninstallUI( JComponent c ) {
        super.uninstallUI( c );
        button.setIcon( origUnpressed );
        button.setPressedIcon( origPressed );
        button.removeMouseListener( handAdapter );
    }

    public void installUI( JComponent c ) {
        super.installUI( c );
        JButton button = (JButton)c;
        button.setIcon( unpressed );
        button.setPressedIcon( pressed );
        button.addMouseListener( handAdapter );
    }
}
