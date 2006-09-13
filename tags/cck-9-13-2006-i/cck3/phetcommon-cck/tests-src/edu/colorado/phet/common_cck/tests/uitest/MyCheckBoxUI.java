/** Sam Reid*/
package edu.colorado.phet.common_cck.tests.uitest;

import edu.colorado.phet.common_cck.examples.TestComponents;
import edu.colorado.phet.common_cck.view.plaf.MouseHandAdapter;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalCheckBoxUI;
import java.awt.*;
import java.awt.event.MouseAdapter;

/**
 * User: Sam Reid
 * Date: Apr 2, 2004
 * Time: 2:45:20 PM
 * Copyright (c) Apr 2, 2004 by Sam Reid
 */
public class MyCheckBoxUI extends MetalCheckBoxUI {

    //    private static ImageIcon pressed = new ImageIcon( TestComponents.class.getClassLoader().getResource( "images/components/webt/in.gif" ) );
    //    private static ImageIcon unpressed = new ImageIcon( TestComponents.class.getClassLoader().getResource( "images/components/webt/out.gif" ) );

    static String foldername = "graymarble";
    static String name = "gray_marble";
    static String offstr = "images/animfactory/suites/" + foldername + "/" + name + "_off_md_wht.gif";
    static String onstr = "images/animfactory/suites/" + foldername + "/" + name + "_on_md_wht.gif";

    private static ImageIcon pressed = new ImageIcon( TestComponents.class.getClassLoader().getResource( onstr ) );
    private static ImageIcon unpressed = new ImageIcon( TestComponents.class.getClassLoader().getResource( offstr ) );
    private Icon origicon;
    private Icon origPressed;
    private Icon origSel;
    private MouseAdapter handAdapter = new MouseHandAdapter();

    public MyCheckBoxUI( JCheckBox box ) {
        super();
        this.origicon = box.getIcon();
        this.origPressed = box.getPressedIcon();
        this.origSel = box.getSelectedIcon();
    }

    public static ComponentUI createUI( JComponent c ) {
        return new MyCheckBoxUI( (JCheckBox)c );
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
        button.addMouseListener( handAdapter );
    }

    // ********************************
    //         Uninstall PLAF
    // ********************************
    public void uninstallUI( JComponent c ) {
        super.uninstallUI( c );
        JCheckBox box = (JCheckBox)c;
        box.setIcon( origicon );
        box.setPressedIcon( origPressed );
        box.setSelectedIcon( origSel );
        box.removeMouseListener( handAdapter );
    }
}
