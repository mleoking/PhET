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

import java.awt.*;
import java.awt.geom.NoninvertibleTransformException;

/**
 * PhetShapeGraphic
 *
 * @author ?
 * @version $Revision$
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
        if( this.shape == null && shape == null ) {
            differentShape = false;
        }
        else if( this.shape == null && shape != null ) {
            differentShape = true;
        }
        else if( this.shape != null && shape == null ) {
            differentShape = true;
        }
        else if( !shape.equals( this.shape ) ) {
            differentShape = true;
        }
        if( differentShape ) {
            this.shape = shape;
            setBoundsDirty();
            autorepaint();
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
        autorepaint();
    }

    public void setStroke( Stroke stroke ) {
        this.stroke = stroke;
        autorepaint();
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
            autorepaint();
        }
    }

    public void setColor( Color color ) {
        setPaint( color );
    }
}
