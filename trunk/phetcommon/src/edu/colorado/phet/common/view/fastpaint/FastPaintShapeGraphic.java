/**
 * Class: FastPaintShapeGraphic
 * Package: edu.colorado.phet.common.view.fastpaint
 * Author: Another Guy
 * Date: May 19, 2004
 */
package edu.colorado.phet.common.view.fastpaint;

import edu.colorado.phet.common.view.graphics.ShapeGraphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;

import java.awt.*;

public class FastPaintShapeGraphic extends ShapeGraphic {

    private Component parent;

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
        this.parent = parent;
    }

    public Rectangle getViewBounds() {
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
        Rectangle viewBounds = getViewBounds();
        super.setFillPaint( fillPaint );
        GraphicsUtil.fastRepaint( parent, viewBounds );
    }

    public void setShape( Shape shape ) {
        Rectangle orig = getViewBounds();
        super.setShape( shape );
        Rectangle newRect = getViewBounds();
        GraphicsUtil.fastRepaint( parent, orig, newRect );
    }

    public void setOutlineStroke( Stroke outlineStroke ) {
        Rectangle orig = getViewBounds();
        super.setOutlineStroke( outlineStroke );
        Rectangle newRect = getViewBounds();
        GraphicsUtil.fastRepaint( parent, orig, newRect );
    }
}
