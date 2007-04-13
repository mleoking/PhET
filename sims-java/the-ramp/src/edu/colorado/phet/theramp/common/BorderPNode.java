/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

import javax.swing.border.Border;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Oct 14, 2005
 * Time: 8:33:16 AM
 * Copyright (c) Oct 14, 2005 by Sam Reid
 */

public class BorderPNode extends PNode {
    private Border border;
    private Rectangle borderRectangle;
    private Component component;

    public BorderPNode( Component component, Border border, Rectangle borderRectangle ) {
        this.component = component;
        this.border = border;
        setBorderRectangle( borderRectangle );
    }

    protected void paint( PPaintContext paintContext ) {
        super.paint( paintContext );
        Rectangle rect = getBorderRectangle();
        border.paintBorder( component, paintContext.getGraphics(), rect.x, rect.y, rect.width, rect.height );
    }

    public Border getBorder() {
        return border;
    }

    public void setBorder( Border border ) {
        this.border = border;
        changed();
    }

    public Rectangle getBorderRectangle() {
        return borderRectangle;
    }

    public void setBorderRectangle( Rectangle borderRectangle ) {
        this.borderRectangle = borderRectangle;
        changed();
    }

    private void changed() {
        setBounds( getBorderRectangle() );
        repaint();
    }
}
