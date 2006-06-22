/** Sam Reid*/
package edu.colorado.phet.common_cck.tests.uitest;

import javax.accessibility.Accessible;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 3, 2004
 * Time: 12:26:12 AM
 * Copyright (c) Apr 3, 2004 by Sam Reid
 */
public class MyTextFieldUI extends ComponentUI {
    BasicTextFieldUI delegate = new BasicTextFieldUI();

    public static ComponentUI createUI( JComponent c ) {
        return new MyTextFieldUI();
    }

    public int getAccessibleChildrenCount( JComponent c ) {
        return delegate.getAccessibleChildrenCount( c );
    }

    public void installUI( JComponent c ) {
        delegate.installUI( c );
    }

    public void uninstallUI( JComponent c ) {
        delegate.uninstallUI( c );
    }

    public boolean contains( JComponent c, int x, int y ) {
        return delegate.contains( c, x, y );
    }

    public Dimension getMaximumSize( JComponent c ) {
        return delegate.getMaximumSize( c );
    }

    public Dimension getMinimumSize( JComponent c ) {
        return delegate.getMinimumSize( c );
    }

    public Dimension getPreferredSize( JComponent c ) {
        return delegate.getPreferredSize( c );
    }

    public Accessible getAccessibleChild( JComponent c, int i ) {
        return delegate.getAccessibleChild( c, i );
    }

    public void update( Graphics g, JComponent c ) {
        delegate.update( g, c );
    }

    public void paint( Graphics g, JComponent c ) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        delegate.paint( g, c );
    }
}
