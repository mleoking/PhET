/** Sam Reid*/
package edu.colorado.phet.cck3.common.primarygraphics;

import edu.colorado.phet.cck3.common.RectangleUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jun 24, 2004
 * Time: 11:44:02 PM
 * Copyright (c) Jun 24, 2004 by Sam Reid
 */
public class PrimaryShadowTextGraphic extends PrimaryGraphic implements IPrimaryTextGraphic {
    private PrimaryTextGraphic foreground;
    private PrimaryTextGraphic background;
    private int dx;
    private int dy;

    public PrimaryShadowTextGraphic( String text, Font font, int x, int y, Color foregroundColor, int dx, int dy, Color backgroundColor, Component component ) {
        super( component );
        this.dx = dx;
        this.dy = dy;
        foreground = new PrimaryTextGraphic( component, font, text, foregroundColor, x, y );
        background = new PrimaryTextGraphic( component, font, text, backgroundColor, x + dx, y + dy );
    }

    public void setPosition( int x, int y ) {
        foreground.setPosition( x, y );
        background.setPosition( x + dx, y + dy );
        super.setBoundsDirty();
        super.repaint();
    }

    public void paint( Graphics2D g ) {
        background.paint( g );
        foreground.paint( g );
    }

    protected Rectangle determineBounds() {
        Rectangle2D b = foreground.getBounds().createUnion( background.getBounds() );
        return RectangleUtils.toRectangle( b );
    }
}
