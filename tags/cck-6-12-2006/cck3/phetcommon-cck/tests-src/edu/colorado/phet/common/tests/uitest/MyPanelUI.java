/** Sam Reid*/
package edu.colorado.phet.common.tests.uitest;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 3, 2004
 * Time: 12:23:35 AM
 * Copyright (c) Apr 3, 2004 by Sam Reid
 */
public class MyPanelUI extends BasicPanelUI {
    public static ComponentUI createUI( JComponent c ) {
        return new MyPanelUI();
    }

    public void paint( Graphics g, JComponent c ) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        super.paint( g, c );
    }
}
