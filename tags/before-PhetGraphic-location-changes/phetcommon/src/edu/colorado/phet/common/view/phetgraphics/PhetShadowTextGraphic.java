/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.phetgraphics;

import edu.colorado.phet.common.view.util.RectangleUtils;

import java.awt.*;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;

/**
 * PhetShadowTextGraphic
 *
 * @author ?
 * @version $Revision$
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
        g.transform( getNetTransform() );
        background.paint( g );
        foreground.paint( g );
        try {
            g.transform( getNetTransform().createInverse() );
        }
        catch( NoninvertibleTransformException e ) {
            e.printStackTrace();
        }
    }

    protected Rectangle determineBounds() {
        Rectangle fore = foreground.getBounds();
        Rectangle back = background.getBounds();
        if( fore == null && back == null ) {
            return null;
        }
        else if( fore == null ) {
            return back;
        }
        else if( back == null ) {
            return fore;
        }
        else {
            Rectangle2D b = foreground.getBounds().createUnion( background.getBounds() );
            b = getNetTransform().createTransformedShape( b ).getBounds2D();
            return RectangleUtils.toRectangle( b );
        }
    }

    public void setText( String text ) {
        foreground.setText( text );
        background.setText( text );
        setBoundsDirty();
    }

    public void setColor( Color color ) {
        this.foreground.setColor( color );
    }

    public void setShadowColor( Color color ) {
        this.background.setColor( color );
    }
}
