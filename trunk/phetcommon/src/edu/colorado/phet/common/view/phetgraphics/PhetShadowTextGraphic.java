/** University of Colorado, PhET*/
package edu.colorado.phet.common.view.phetgraphics;

import edu.colorado.phet.common.view.util.RectangleUtils;

import java.awt.*;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;

/**
 * User: University of Colorado, PhET
 * Date: Jun 24, 2004
 * Time: 11:44:02 PM
 * Copyright (c) Jun 24, 2004 by University of Colorado, PhET
 */
public class PhetShadowTextGraphic extends PhetGraphic {
    private PhetTextGraphic foreground;
    private PhetTextGraphic background;

    public PhetShadowTextGraphic( Component component, String text, Font font, int x, int y, Color foregroundColor, int dx, int dy, Color backgroundColor ) {
        super( component );
        foreground = new PhetTextGraphic( component, font, text, foregroundColor, 0, 0 );
        background = new PhetTextGraphic( component, font, text, backgroundColor, 0 + dx, 0 + dy );
        setLocation( x, y );
    }

    public void paint( Graphics2D g ) {
        g.transform( getTransform() );
        background.paint( g );
        foreground.paint( g );
        try {
            g.transform( getTransform().createInverse() );
        }
        catch( NoninvertibleTransformException e ) {
            e.printStackTrace();
        }
    }

    protected Rectangle determineBounds() {
        Rectangle2D b = foreground.getBounds().createUnion( background.getBounds() );
        b = getTransform().createTransformedShape( b ).getBounds2D();
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
