/**
 * Class: ShapeGraphic
 * Package: edu.colorado.phet.common.examples.examples
 * Author: Another Guy
 * Date: Dec 18, 2003
 */
package edu.colorado.phet.distanceladder.common.view.graphics;

import edu.colorado.phet.distanceladder.common.view.graphics.bounds.Boundary;

import java.awt.*;

/**
 * Wrap an Area around your Shape to make it mutable.
 */
public class ShapeGraphic implements Graphic, Boundary {
    private Shape shape;
    private Paint outlinePaint;
    private Paint fillPaint;
    private Stroke outlineStroke;

    public ShapeGraphic( Shape shape, Paint fill ) {
        this( shape, fill, null, null );
    }

    public ShapeGraphic( Shape shape, Paint outline, Stroke stroke ) {
        this( shape, null, outline, stroke );
    }

    public ShapeGraphic( Shape shape, Paint fill, Paint outline, Stroke stroke ) {
        this.shape = shape;
        this.fillPaint = fill;
        this.outlinePaint = outline;
        this.outlineStroke = stroke;
    }

    public void paint( Graphics2D g ) {
        if( fillPaint != null ) {
            g.setPaint( fillPaint );
            g.fill( shape );
        }
        if( outlineStroke != null ) {
            g.setPaint( outlinePaint );
            g.setStroke( outlineStroke );
            g.draw( shape );
        }
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape( Shape shape ) {
        this.shape = shape;
    }

    public Paint getOutlinePaint() {
        return outlinePaint;
    }

    public void setOutlinePaint( Paint outlinePaint ) {
        this.outlinePaint = outlinePaint;
    }

    public Paint getFillPaint() {
        return fillPaint;
    }

    public void setFillPaint( Paint fillPaint ) {
        this.fillPaint = fillPaint;
    }

    public Stroke getOutlineStroke() {
        return outlineStroke;
    }

    public void setOutlineStroke( Stroke outlineStroke ) {
        this.outlineStroke = outlineStroke;
    }

    public boolean contains( int x, int y ) {
        return shape.contains( x, y );
    }


}
