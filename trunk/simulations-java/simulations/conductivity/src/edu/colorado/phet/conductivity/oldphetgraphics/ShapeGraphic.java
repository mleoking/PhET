/**
 * Class: ShapeGraphic
 * Package: edu.colorado.phet.common.examples.examples
 * Author: Another Guy
 * Date: Dec 18, 2003
 */
package edu.colorado.phet.conductivity.oldphetgraphics;

import java.awt.*;

/**
 * Wrap an Area around your Shape to make it mutable.
 */
public class ShapeGraphic implements Graphic {
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
        Stroke origStroke = g.getStroke();
        if( fillPaint != null ) {
            g.setPaint( fillPaint );
            g.fill( shape );
        }
        if( outlineStroke != null ) {
            g.setPaint( outlinePaint );
            g.setStroke( outlineStroke );
            g.draw( shape );
        }
        g.setStroke( origStroke );
    }

    public void setShape( Shape shape ) {
        this.shape = shape;
    }

    public void setOutlinePaint( Paint outlinePaint ) {
        this.outlinePaint = outlinePaint;
    }

    public void setFillPaint( Paint fillPaint ) {
        this.fillPaint = fillPaint;
    }


}
