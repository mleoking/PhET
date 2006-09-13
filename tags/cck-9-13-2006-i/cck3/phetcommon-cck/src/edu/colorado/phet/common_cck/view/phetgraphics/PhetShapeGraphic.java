/** Sam Reid*/
package edu.colorado.phet.common_cck.view.phetgraphics;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * User: Sam Reid
 * Date: Jun 25, 2004
 * Time: 5:59:48 PM
 * Copyright (c) Jun 25, 2004 by Sam Reid
 */
public class PhetShapeGraphic extends PhetGraphic {
    private Shape shape;
    private Stroke stroke;
    private Paint fill;
    private Color border;

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

    public Shape getShape() {
        return shape;
    }

    public Paint getFill() {
        return fill;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public Color getBorder() {
        return border;
    }

    public void paint( Graphics2D g ) {
        if( isVisible() ) {
            super.saveGraphicsState( g );
            RenderingHints hints = super.getRenderingHints();
            if( hints != null ) {
                g.setRenderingHints( hints );
            }
            if( shape != null ) {
                if( fill != null ) {
                    g.setPaint( fill );
                    g.fill( shape );
                }
                if( stroke != null ) {
                    g.setColor( border );
                    Stroke origStroke = g.getStroke();
                    g.setStroke( stroke );
                    g.draw( shape );
                    g.setStroke( origStroke );
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
            return shape.getBounds();
        }
        else {
            Shape outlineShape = stroke.createStrokedShape( shape );
            Rectangle bounds = outlineShape.getBounds();
            Rectangle expanded = new Rectangle( bounds.x, bounds.y, bounds.width + 1, bounds.height + 1 ); //necessary to capture the entire bounds.
            return expanded;
        }
    }

    public Point getPosition() {
        return shape.getBounds().getLocation();
    }

    public void setShape( Shape shape ) {
        boolean differentShape = false;
        if( this.shape == null && shape != null ) {
            differentShape = true;
        }
        else if( this.shape != null && shape == null ) {
            differentShape = true;
        }
        else if( shape != null && !shape.equals( this.shape ) ) {
            differentShape = true;
        }
        if( differentShape ) {
            this.shape = shape;
            setBoundsDirty();
            repaint();
        }
    }

    public void translate( double dx, double dy ) {
        Shape newShape = AffineTransform.getTranslateInstance( dx, dy ).createTransformedShape( shape );
        setShape( newShape );
    }

    public boolean contains( int x, int y ) {
        return isVisible() && getShape().contains( x, y );
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
        this.fill = paint;
        repaint();
    }
}
