package edu.colorado.phet.bernoulli.pump;

//import edu.colorado.phet.bernoulli.common.DifferentialDragHandler;

import edu.colorado.phet.bernoulli.common.RepaintManager;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.coreadditions.graphics.DifferentialDragHandler;
import edu.colorado.phet.coreadditions.graphics.arrows.LineSegment;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;
import edu.colorado.phet.coreadditions.math.PhetVector;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

/**
 * User: Sam Reid
 * Date: Aug 19, 2003
 * Time: 10:08:54 AM
 * Copyright (c) Aug 19, 2003 by Sam Reid
 */
public class PistonGraphic implements InteractiveGraphic, Observer, TransformListener {
    Rectangle shape;
    private Piston piston;
    private ModelViewTransform2d transform;
    private ApparatusPanel target;
    private RepaintManager rm;
    LineSegment segment = new LineSegment( new BasicStroke( 4 ), Color.black );

//    private Point targetPoint;
    private Point anchor;
    private Point center;
    PistonRectangle centeredRectangle = new PistonRectangle( 30, 80 );

    public PistonGraphic( Piston piston, ModelViewTransform2d transform, ApparatusPanel target, RepaintManager rm ) {
        this.piston = piston;
        this.transform = transform;
        this.target = target;
        this.rm = rm;
        transform.addTransformListener( this );
        piston.addObserver( this );
        update( null, this );
        centeredRectangle.setWidth( transform.modelToViewDifferentialX( piston.getPistonWidth() ) );
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        if( shape == null ) {
            return false;
        }
        return shape.contains( event.getPoint() );
    }

    public void mousePressed( MouseEvent event ) {
        drag = new DifferentialDragHandler( event.getPoint() );
    }

    DifferentialDragHandler drag;

    public void mouseDragged( MouseEvent event ) {
        Point dx = drag.getDifferentialLocationAndReset( event.getPoint() );
        Point2D.Double pt = transform.viewToModelDifferential( dx.x, dx.y );
        PhetVector dv = new PhetVector( pt.x, pt.y );
        PhetVector unit = piston.getUnitVector();
        double scale = unit.dot( dv );
        piston.translate( scale );
        target.repaint();
    }

    public void mouseReleased( MouseEvent event ) {
    }

    public void mouseEntered( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public void mouseExited( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }

    public void paint( Graphics2D g ) {
        if( shape == null ) {
            return;
        }
        g.setColor( Color.black );
        segment.paint( g, this.center.x + 2, center.y, anchor.x, anchor.y );
        g.setColor( new Color( 200, 180, 220 ) );
        g.fillRect( shape.x, shape.y, shape.width, shape.height );
    }

    public void update( Observable o, Object arg ) {
        Rectangle2D.Double pistonRect = piston.getHeadRectangle();//toPistonRectangle();
        shape = transform.modelToView( pistonRect );
        PhetVector ctr = piston.getExtensionCenter();
        center = transform.modelToView( ctr.getX(), ctr.getY() );
        anchor = transform.modelToView( piston.getX(), piston.getY() );
//        PhetVector center=new PhetVector(pist);
        rm.update();
    }

    public void transformChanged( ModelViewTransform2d mvt ) {
        centeredRectangle.setWidth( transform.modelToViewDifferentialX( piston.getPistonWidth() ) );
        update( null, mvt );
    }
}
