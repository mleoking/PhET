/** Sam Reid*/
package edu.colorado.phet.cck3.common.primarygraphics;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * User: Sam Reid
 * Date: Jun 25, 2004
 * Time: 5:59:48 PM
 * Copyright (c) Jun 25, 2004 by Sam Reid
 */
public class PrimaryShapeGraphic extends PrimaryGraphic {
    private Shape shape;
    private Stroke stroke;
    private Color fill;
    private Color border;

    public PrimaryShapeGraphic( Component component, Shape shape, Color fill ) {
        super( component );
        this.shape = shape;
        this.fill = fill;
    }

    public PrimaryShapeGraphic( Component component, Shape shape, Stroke stroke, Color border ) {
        super( component );
        this.shape = shape;
        this.stroke = stroke;
        this.border = border;
    }

    public PrimaryShapeGraphic( Component component, Shape shape, Color fill, Stroke stroke, Color border ) {
        super( component );
        this.shape = shape;
        this.fill = fill;
        this.stroke = stroke;
        this.border = border;
    }

    public Shape getShape() {
        return shape;
    }

    public Color getFill() {
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
            if( shape != null ) {
                if( fill != null ) {
                    g.setColor( fill );
                    g.fill( shape );
                }
                if( stroke != null ) {
                    g.setColor( border );
                    g.setStroke( stroke );
                    g.draw( shape );
                }
            }
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
            return outlineShape.getBounds();
        }
    }

    public Point getPosition() {
        return shape.getBounds().getLocation();
    }

    public void setShape( Shape shape ) {
        this.shape = shape;
        setBoundsDirty();
        repaint();
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
}
