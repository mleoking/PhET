package edu.colorado.phet.bernoulli.common;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.graphics.arrows.Arrow;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 2, 2003
 * Time: 12:42:30 AM
 * Copyright (c) Sep 2, 2003 by Sam Reid
 */
public class ArrowGraphicTransform implements Graphic {
    Arrow arrowPaint;
    double x;
    double y;
    double x2;
    double y2;
    ModelViewTransform2d transform2d;
    Point start;
    Point end;
//    int x;
//    int y;
//    int
    public ArrowGraphicTransform( Arrow arrowPaint, double x, double y, double x2, double y2, ModelViewTransform2d transform2d ) {
        this.arrowPaint = arrowPaint;
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        this.transform2d = transform2d;
        transform2d.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d modelViewTransform2d ) {
                update();
            }
        } );
        update();
    }

    private void update() {
        start = transform2d.modelToView( x, y );
        end = transform2d.modelToView( x2, y2 );
    }

    public void paint( Graphics2D g ) {
        if( start != null ) {
            arrowPaint.drawLine( g, start.x, start.y, end.x, end.y );
        }
    }


}
