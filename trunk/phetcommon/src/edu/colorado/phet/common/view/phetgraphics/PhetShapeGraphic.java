/** University of Colorado, PhET*/
package edu.colorado.phet.common.view.phetgraphics;

import java.awt.*;
import java.awt.geom.NoninvertibleTransformException;

/**
 * User: University of Colorado, PhET
 * Date: Jun 25, 2004
 * Time: 5:59:48 PM
 * Copyright (c) Jun 25, 2004 by University of Colorado, PhET
 */
public class PhetShapeGraphic extends PhetGraphic {
    private Shape shape;
    private Stroke stroke;
    private Paint fill;
    private Paint border;

    public PhetShapeGraphic( Component component, Shape shape, Color fill ) {
        super( component );
        this.shape = shape;
        this.fill = fill;
    }

    public PhetShapeGraphic( Component component, Shape shape, Stroke stroke, Color border ) {
        super( component );
        this.shape = shape;
        this.stroke = stroke;
        this.border = border;
    }

    public PhetShapeGraphic( Component component, Shape shape, Color fill, Stroke stroke, Color border ) {
        super( component );
        this.shape = shape;
        this.fill = fill;
        this.stroke = stroke;
        this.border = border;
    }

    public PhetShapeGraphic( Component component ) {
        this( component, null, null );
    }

    public Shape getShape() {
        return shape;
    }

    public Paint getFill() {
        return fill;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public Paint getBorder() {
        return border;
    }

    public void setBorderPaint( Paint border ) {
        this.border = border;
    }

    public void paint( Graphics2D g ) {
        if( isVisible() ) {
            super.saveGraphicsState( g );
            RenderingHints hints = super.getRenderingHints();
            if( hints != null ) {
                g.setRenderingHints( hints );
            }
            if( shape != null ) {
                g.transform( getTransform() );
                if( fill != null ) {
                    g.setPaint( fill );
                    g.fill( shape );
                }
                if( stroke != null ) {
                    g.setPaint( border );
                    Stroke origStroke = g.getStroke();
                    g.setStroke( stroke );
                    g.draw( shape );
                    g.setStroke( origStroke );
                }
                try {
                    g.transform( getTransform().createInverse() );
                }
                catch( NoninvertibleTransformException e ) {
                    e.printStackTrace();
                }
            }
            super.restoreGraphicsState();
        }
    }

    protected Rectangle determineBounds() {
        if( shape == null ) {
            return null;
        }
        if( stroke == null ) {
            return getTransform().createTransformedShape( shape.getBounds() ).getBounds();
        }
        else {
            Shape outlineShape = stroke.createStrokedShape( shape );
            Rectangle bounds = outlineShape.getBounds();
            Rectangle expanded = new Rectangle( bounds.x, bounds.y, bounds.width + 1, bounds.height + 1 ); //necessary to capture the entire bounds.
            return getTransform().createTransformedShape( expanded ).getBounds();
        }
    }

    public void setShape( Shape shape ) {
        boolean differentShape = false;
        if( this.shape == null && shape != null ) {
            differentShape = true;
        }
        else if( this.shape != null && shape == null ) {
            differentShape = true;
        }
        else if( this.shape != null && shape != null && !shape.equals( this.shape ) ) {
            differentShape = true;
        }
        if( differentShape ) {
            this.shape = shape;
            setBoundsDirty();
            repaint();
        }
    }

    public boolean contains( int x, int y ) {
        if( getShape() != null ) {
            Shape txShape = getTransform().createTransformedShape( getShape() );
            return isVisible() && txShape.contains( x, y );
        }
        return false;
    }

    public void setBorderColor( Color color ) {
        this.border = color;
        repaint();
    }

    public void setStroke( Stroke stroke ) {
        this.stroke = stroke;
        repaint();
    }

    public void setPaint( Paint paint ) {
        boolean changed = false;
        if( this.fill == null && paint != null ) {
            changed = true;
        }
        else if( this.fill != null && paint == null ) {
            changed = true;
        }
        else if( !this.fill.equals( paint ) ) {
            changed = true;
        }
        if( changed ) {
            this.fill = paint;
            repaint();
        }
    }

    public void setColor( Color color ) {
        setPaint( color );
    }
}
