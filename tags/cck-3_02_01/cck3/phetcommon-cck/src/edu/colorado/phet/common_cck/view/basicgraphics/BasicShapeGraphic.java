/** Sam Reid*/
package edu.colorado.phet.common_cck.view.basicgraphics;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 10, 2004
 * Time: 7:34:52 AM
 * Copyright (c) Sep 10, 2004 by Sam Reid
 */
public class BasicShapeGraphic extends BasicGraphic {
    private Shape shape;
    private Paint paint;
    private Stroke stroke;
    private Paint strokePaint;

    public BasicShapeGraphic( Shape shape, Paint paint ) {
        this.shape = shape;
        this.paint = paint;
    }

    public BasicShapeGraphic( Shape shape, Paint paint, Stroke stroke, Paint strokeColor ) {
        this.shape = shape;
        this.paint = paint;
        this.stroke = stroke;
        this.strokePaint = strokeColor;
    }


    public Shape getShape() {
        return shape;
    }

    public Rectangle getBounds() {
        if( stroke != null ) {
            return stroke.createStrokedShape( shape ).getBounds();
        }
        else {
            return shape.getBounds();
        }
    }

    public void setShape( Shape shape ) {
        this.shape = shape;
        super.boundsChanged();
    }

    public void setPaint( Paint paint ) {
        this.paint = paint;
        super.appearanceChanged();
    }

    public void setStroke( Stroke stroke ) {
        this.stroke = stroke;
        super.boundsChanged();
    }

    public void paint( Graphics2D g ) {
        if( stroke != null ) {
            Stroke origStroke = g.getStroke();
            Paint origPaint = g.getPaint();

            g.setPaint( paint );
            g.fill( shape );

            g.setStroke( stroke );
            g.setPaint( strokePaint );
            g.draw( shape );

            g.setStroke( origStroke );
            g.setPaint( origPaint );
        }
        else {
            Paint origPaint = g.getPaint();
            g.setPaint( paint );
            g.fill( shape );
            g.setPaint( origPaint );
        }
    }
}
