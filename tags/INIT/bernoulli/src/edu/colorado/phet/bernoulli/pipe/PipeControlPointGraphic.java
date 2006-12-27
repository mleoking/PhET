package edu.colorado.phet.bernoulli.pipe;

import edu.colorado.phet.bernoulli.BernoulliModule;
import edu.colorado.phet.bernoulli.common.CircleGraphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.coreadditions.graphics.DifferentialDragHandler;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Aug 22, 2003
 * Time: 4:49:39 PM
 * Copyright (c) Aug 22, 2003 by Sam Reid
 */
public class PipeControlPointGraphic implements InteractiveGraphic, TransformListener, SimpleObserver {
    CircleGraphic cg;
    ModelViewTransform2d transform;
    private PipeGraphic pipeGraphic;
    private boolean top;
    private int index;
    private BernoulliModule module;
    private JPopupMenu menu;

    public PipeControlPointGraphic( final PipeGraphic pipeGraphic, double x, double y, double radius, ModelViewTransform2d transform, Color color, final boolean top, final int index, final BernoulliModule module ) {
        this.pipeGraphic = pipeGraphic;
        this.top = top;
        this.index = index;
        this.module = module;
        cg = new CircleGraphic( new Point2D.Double( x, y ), radius, color, transform );
        this.transform = transform;
        pipeGraphic.getPipe().addObserver( this );

        menu = new JPopupMenu();
        JMenuItem item = new JMenuItem( "Punch a hole in the pipe." );
        item.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ControlSection cs = pipeGraphic.getPipe().controlSectionAt( index );
                HoleInThePipe hitp = new HoleInThePipe( pipeGraphic.getPipe(), cs, top, module );
                module.getModel().addModelElement( hitp );
            }
        } );
        menu.add( item );
        update();
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return cg.containsPoint( event.getPoint() );
    }

    public void mousePressed( MouseEvent event ) {
        dragger = new DifferentialDragHandler( event.getPoint() );
    }

    DifferentialDragHandler dragger;

    public void mouseDragged() {
//        Point dx = dragger.getDifferentialLocationAndReset(new Point(100, 100));
//        Point2D.Double modelDX = transform.viewToModelDifferential(dx);
        Point2D.Double modelDX = new Point2D.Double( 0, 0 );
        modelDX.x = 0;
        pipeGraphic.translatePoint( modelDX, top, index );
    }

    public void mouseDragged( MouseEvent event ) {
        Point dx = dragger.getDifferentialLocationAndReset( event.getPoint() );
        Point2D.Double modelDX = transform.viewToModelDifferential( dx );
        modelDX.x = 0;
        pipeGraphic.translatePoint( modelDX, top, index );
    }

    public void mouseReleased( MouseEvent event ) {
        if( SwingUtilities.isRightMouseButton( event ) ) {
            menu.show( event.getComponent(), event.getX(), event.getY() );
        }
    }

    public void mouseEntered( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public void mouseExited( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }

    public void paint( Graphics2D g ) {
        cg.paint( g );
    }

    public void transformChanged( ModelViewTransform2d mvt ) {
        cg.transformChanged( mvt );
    }

    public void update() {
        if( top ) {
            Point2D.Double pt = pipeGraphic.topGraphic.getSpline().controlPointAt( index );
            cg.setLocation( pt );
        }
        else {
            Point2D.Double pt = pipeGraphic.bottomGraphic.getSpline().controlPointAt( index );
            cg.setLocation( pt );
        }
    }
}
