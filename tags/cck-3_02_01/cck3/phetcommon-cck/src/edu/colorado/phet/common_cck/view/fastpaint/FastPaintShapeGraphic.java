/**
 * Class: FastPaintShapeGraphic
 * Package: edu.colorado.phet.common.view.fastpaint
 * Author: Another Guy
 * Date: May 19, 2004
 */
package edu.colorado.phet.common_cck.view.fastpaint;

import edu.colorado.phet.common_cck.view.graphics.ShapeGraphic;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class FastPaintShapeGraphic extends ShapeGraphic implements FastPaint.Graphic {

    private AffineTransform transform;
    private FastPaint fastPaint;

    public FastPaintShapeGraphic( Shape shape, Paint fill, Component parent ) {
        super( shape, fill );
        init( parent );
    }

    public FastPaintShapeGraphic( Shape shape, Paint outline, Stroke stroke, Component parent ) {
        super( shape, outline, stroke );
        init( parent );
    }

    public FastPaintShapeGraphic( Shape shape, Paint fill, Paint outline, Stroke stroke, Component parent ) {
        super( shape, fill, outline, stroke );
        init( parent );
    }

    private void init( Component parent ) {
        this.fastPaint = new FastPaint( parent, this );
        //        this.parent = parent;
        fastPaint.repaint();
    }

    public Rectangle getBounds() {
        if( getShape() != null ) {
            Stroke stroke = super.getOutlineStroke();
            if( stroke != null ) {
                Shape strokeShape = stroke.createStrokedShape( super.getShape() );
                return strokeShape.getBounds();
            }
            else {
                return super.getShape().getBounds();
            }
        }
        else {
            return null;
        }
    }

    public void setFillPaint( Paint fillPaint ) {
        super.setFillPaint( fillPaint );
        repaint();
    }

    public void setShape( Shape shape ) {
        super.setShape( shape );
        repaint();
    }

    public void setOutlineStroke( Stroke outlineStroke ) {
        super.setOutlineStroke( outlineStroke );
        repaint();
    }

    public void repaint() {
        fastPaint.repaint();
    }

    //    public AffineTransform getTransform() {
    //        return transform;
    //    }
    //
    //    public void setTransform( AffineTransform transform ) {
    //        this.transform = transform;
    //    }

    //    public void paint( Graphics2D g ) {
    //        //        AffineTransform orgTx = g.getTransform();
    //        //        g.transform( this.transform );
    //        //        super.paint( g );
    //        //        g.setTransform( orgTx );
    //    }

    //    public void translate( double dx, double dy ) {
    //        setShape( AffineTransform.getTranslateInstance( dx, dy ).createTransformedShape( getShape() ) );
    //    }
}
