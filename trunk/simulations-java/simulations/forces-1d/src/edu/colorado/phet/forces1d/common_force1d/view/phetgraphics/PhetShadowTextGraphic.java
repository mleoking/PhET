/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.common_force1d.view.phetgraphics;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.forces1d.common_force1d.view.util.RectangleUtils;

/**
 * PhetShadowTextGraphic
 *
 * @author ?
 * @version $Revision$
 */
public class PhetShadowTextGraphic extends PhetGraphic {
    private PhetTextGraphic foreground;
    private PhetTextGraphic background;

    public PhetShadowTextGraphic( Component component, Font font, String text, Color foregroundColor, int dx, int dy, Color backgroundColor ) {
        super( component );
        foreground = new PhetTextGraphic( component, font, text, foregroundColor );
        background = new PhetTextGraphic( component, font, text, backgroundColor, dx, dy );
    }

    /**
     * @deprecated
     */
    public PhetShadowTextGraphic( Component component, String text, Font font, int x, int y, Color foregroundColor, int dx, int dy, Color backgroundColor ) {
        super( component );
        foreground = new PhetTextGraphic( component, font, text, foregroundColor, 0, 0 );
        background = new PhetTextGraphic( component, font, text, backgroundColor, 0 + dx, 0 + dy );
        setLocation( x, y );
    }

    public void paint( Graphics2D g2 ) {
        if ( isVisible() ) {
            super.saveGraphicsState( g2 );
            super.updateGraphicsState( g2 );
            g2.transform( getNetTransform() );
            background.paint( g2 );
            foreground.paint( g2 );
            super.restoreGraphicsState();
        }
    }

    protected Rectangle determineBounds() {
        Rectangle fore = foreground.getBounds();
        Rectangle back = background.getBounds();
        if ( fore == null && back == null ) {
            return null;
        }
        else if ( fore == null ) {
            return back;
        }
        else if ( back == null ) {
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
        autorepaint();
    }

    public void setColor( Color color ) {
        this.foreground.setColor( color );
    }

    public void setShadowColor( Color color ) {
        this.background.setColor( color );
    }

    public void setFont( Font font ) {
        foreground.setFont( font );
        background.setFont( font );
        setBoundsDirty();
        autorepaint();
    }

    //-----------------------------------------------------------
    // Provided for Java Beans conformance
    //-----------------------------------------------------------

    public PhetShadowTextGraphic() {
    }

    public PhetTextGraphic getForeground() {
        return foreground;
    }

    public void setForeground( PhetTextGraphic foreground ) {
        this.foreground = foreground;
    }

    public PhetTextGraphic getBackground() {
        return background;
    }

    public void setBackground( PhetTextGraphic background ) {
        this.background = background;
    }

    public String getText() {
        return foreground.getText();
    }
}
