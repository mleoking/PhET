/**
 * Class: TubeGraphic
 * Package: edu.colorado.phet.bernoulli.tube
 * Author: Another Guy
 * Date: Sep 26, 2003
 */
package edu.colorado.phet.bernoulli.tube;

import edu.colorado.phet.bernoulli.common.graphics.GeneralPathGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObserver;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.List;


public class TubeGraphic implements Graphic, SimpleObserver {
    private Tube tube;
    private ModelViewTransform2d tx;
    private GeneralPathGraphic graphicDelegate;
    private GeneralPath path = new GeneralPath();
    private Stroke stroke;

    public TubeGraphic( Tube tube, ModelViewTransform2d tx ) {
        this.tube = tube;
        this.tx = tx;
        tx.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d modelViewTransform2d ) {
                update();
            }
        } );
        Paint paint = Color.gray;
        graphicDelegate = new GeneralPathGraphic( path, stroke, paint, tx );
        tube.addObserver( this );
        update();
    }

    public void paint( Graphics2D graphics2D ) {
        graphicDelegate.paint( graphics2D );
    }

    public void update() {
        stroke = new BasicStroke( tx.modelToViewDifferentialX( tube.getDiameter() ),
                                  BasicStroke.CAP_SQUARE,
                                  BasicStroke.JOIN_ROUND );
        graphicDelegate.setStroke( stroke );
        path.reset();
        List points = tube.getPoints();
        for( int i = 0; i < points.size(); i++ ) {
            Point2D point = (Point2D)points.get( i );
            if( i == 0 ) {
                path.moveTo( (float)point.getX(), (float)point.getY() );
            }
            else {
                path.lineTo( (float)point.getX(), (float)point.getY() );
            }
        }
        graphicDelegate.setModel( path );
    }
}
