/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.elements.spline;

import edu.colorado.phet.common.view.graphics.DragHandler;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.coreadditions.graphics.positioned.CenteredCircleGraphic2;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.ec2.common.util.CursorHandler;
import edu.colorado.phet.ec2.elements.car.SplineMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jul 25, 2003
 * Time: 8:09:04 PM
 * Copyright (c) Jul 25, 2003 by Sam Reid
 */
public class VertexGraphic implements InteractiveGraphic {
    private Spline spline;
    protected int index;
    int x;
    int y;
    CenteredCircleGraphic2 circleGraphic = new CenteredCircleGraphic2( 8, Color.red );
    Color brighter = Color.yellow;
    private ModelViewTransform2d transform;
    CursorHandler cursorHandler = new CursorHandler();
    private JPopupMenu popup;

    public VertexGraphic( final SplineGraphic parent, final Spline source, final int index, int x, int y ) {
        this.spline = source;
        this.index = index;
        this.x = x;
        this.y = y;
        popup = CurveGraphic.newJPopupMenu( "Control Point" );
        popup.add( CurveGraphic.newMenuItem( "Remove", new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( spline.numPoints() == 2 ) {
                    parent.remove();
                }
                else {
                    source.removeControlPoint( index );
                }
            }
        } ) );
    }

    public Point2D.Double getModelLocation() {
        return spline.pointAt( index );
    }

    public void paint( Graphics2D g ) {
        circleGraphic.paint( g, x, y );
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return circleGraphic.containsPoint( event.getX(), event.getY(), x, y );
    }

    protected DragHandler dragger;

    public void mousePressed( MouseEvent event ) {
        if( SwingUtilities.isRightMouseButton( event ) ) {
            popup.show( event.getComponent(), event.getX(), event.getY() );
        }
        else {
            dragger = new DragHandler( event.getPoint(), new Point() );
            SplineMode.healEnergy = false;
        }
    }

    public void mouseDragged( MouseEvent event ) {
        if( dragger == null ) {
            return;
        }
        Point rel = dragger.getNewLocation( event.getPoint() );
        Point2D.Double modelPt = CurveGraphic.transformLocalViewToModel( transform, rel.x, rel.y );//transform.viewToModel(rel.x,rel.y);

        spline.translatePoint( index, modelPt.x, modelPt.y );
        dragger = new DragHandler( event.getPoint(), new Point() );
//        O.d("Source="+spline);
    }

    public void mouseReleased( MouseEvent event ) {
        SplineMode.healEnergy = true;
    }

    public void mouseEntered( MouseEvent event ) {
        circleGraphic.setColor( brighter );
        cursorHandler.mouseEntered( event );
    }

    public void mouseExited( MouseEvent event ) {
        circleGraphic.setColor( Color.red );
        cursorHandler.mouseExited( event );
    }

    public void setPosition( int x, int y ) {
        this.x = x;
        this.y = y;
    }

    public void viewChanged( ModelViewTransform2d transform ) {
        this.transform = transform;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


}
