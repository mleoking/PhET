/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.colorvision.phetcommon.view.phetgraphics;

import edu.colorado.phet.colorvision.phetcommon.view.util.RectangleUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * PhetShadowTextGraphic
 *
 * @author S Reid
 * @version $Revision$
 */
public class PhetShadowTextGraphic extends PhetGraphic implements IPhetTextGraphic {
    private PhetTextGraphic foreground;
    private PhetTextGraphic background;
    private int dx;
    private int dy;

    public PhetShadowTextGraphic( Component component, String text, Font font, int x, int y, Color foregroundColor, int dx, int dy, Color backgroundColor ) {
        super( component );
        this.dx = dx;
        this.dy = dy;
        foreground = new PhetTextGraphic( component, font, text, foregroundColor, x, y );
        background = new PhetTextGraphic( component, font, text, backgroundColor, x + dx, y + dy );
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

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        foreground.setVisible( visible );
        background.setVisible( visible );
    }

    protected Rectangle determineBounds() {
        Rectangle2D b = foreground.getBounds().createUnion( background.getBounds() );
        return RectangleUtils.toRectangle( b );
    }

    public void setText( String text ) {
        foreground.setText( text );
        background.setText( text );
    }

    public void setColor( Color color ) {
        this.foreground.setColor( color );
    }

    public void setShadowColor( Color color ) {
        this.background.setColor( color );
    }
}
