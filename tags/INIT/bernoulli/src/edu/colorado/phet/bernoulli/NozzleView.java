/**
 * Class: NozzleView
 * Package: edu.colorado.phet.bernoulli
 * Author: Another Guy
 * Date: Sep 29, 2003
 */
package edu.colorado.phet.bernoulli;

import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObserver;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class NozzleView implements InteractiveGraphic, SimpleObserver {

    private Nozzle nozzle;
    private ModelViewTransform2d transform;
    private Shape nozzleShape;

    public NozzleView( Nozzle nozzle, ModelViewTransform2d transform ) {
        this.nozzle = nozzle;
        nozzle.addObserver( this );
        this.transform = transform;
        update();

        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d modelViewTransform2d ) {
                update();
            }
        } );
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return nozzleShape.contains( event.getPoint() );
    }

    public void mousePressed( MouseEvent event ) {
    }

    public void mouseDragged( MouseEvent event ) {
        Point2D.Double modelClickPt = transform.viewToModel( event.getPoint() );
        double currDragAngle = Math.atan2( modelClickPt.getY() - nozzle.getPivot().getY(),
                                           modelClickPt.getX() - nozzle.getPivot().getX() );
        nozzle.setAngle( currDragAngle );
    }

    public void mouseReleased( MouseEvent event ) {
    }

    public void mouseEntered( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public void mouseExited( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }

    public void paint( Graphics2D graphics2D ) {
//        graphics2D.setStroke( nozzleStroke );
        graphics2D.setColor( Color.DARK_GRAY );
        graphics2D.fill( nozzleShape );
//        graphics2D.drawLine( pivotPt.x, pivotPt.y, outletPt.x, outletPt.y );
    }

    public void update() {
        Point pivotPt = transform.modelToView( nozzle.getPivot() );
        Point outletPt = transform.modelToView( nozzle.getOutlet() );
        Stroke nozzleStroke = new BasicStroke( 20f );
        Line2D.Double nozzleLine = new Line2D.Double( pivotPt, outletPt );
        nozzleShape = nozzleStroke.createStrokedShape( nozzleLine );
    }
}
