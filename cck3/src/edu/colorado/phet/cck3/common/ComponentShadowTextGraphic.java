/** Sam Reid*/
package edu.colorado.phet.cck3.common;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jun 24, 2004
 * Time: 11:44:02 PM
 * Copyright (c) Jun 24, 2004 by Sam Reid
 */
public class ComponentShadowTextGraphic implements ITextGraphic {
    private ComponentTextGraphic foreground;
    private ComponentTextGraphic background;
    private Rectangle bounds;
    private boolean boundsDirty = true;

    private int dx;
    private int dy;

    public ComponentShadowTextGraphic( String text, Font font, int x, int y, Color foregroundColor, int dx, int dy, Color backgroundColor, Component component ) {
        this.dx = dx;
        this.dy = dy;
        foreground = new ComponentTextGraphic( text, font, x, y, component, foregroundColor );
        background = new ComponentTextGraphic( text, font, x + dx, y + dy, component, backgroundColor );
    }

    public Rectangle getBounds() {
        if( boundsDirty ) {
            recomputeBounds();
            boundsDirty = false;
        }
        return bounds;
    }

    public void setPosition( int x, int y ) {
        foreground.setPosition( x, y );
        background.setPosition( x + dx, y + dy );
        boundsDirty = true;
    }

    private void recomputeBounds() {
        Rectangle2D b = foreground.getBounds().createUnion( background.getBounds() );
        this.bounds = RectangleUtils.toRectangle( b );
    }

    public void paint( Graphics2D g ) {
        background.paint( g );
        foreground.paint( g );
    }

    public boolean contains( int x, int y ) {
        return getBounds().contains( x, y );
    }
}
