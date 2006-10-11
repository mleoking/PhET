/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.view;

import edu.colorado.phet.ec3.EC3Canvas;
import edu.colorado.phet.ec3.EnergySkateParkStrings;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.SplineSurface;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.event.PopupMenuHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 1:17:41 PM
 * Copyright (c) Sep 26, 2005 by Sam Reid
 */

public class SplineGraphic extends PNode {
    private EC3Canvas ec3Canvas;
    private AbstractSpline spline;
    private AbstractSpline reverse;
    private PPath pathLayer;
    private PNode controlPointLayer;

    private Point2D.Double[] initDragSpline;
    private Point2D.Double controlPointLoc;

    private BasicStroke dottedStroke = new BasicStroke( 0.03f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, new float[]{0.09f, 0.09f}, 0 );
    private BasicStroke lineStroke = new BasicStroke( 0.03f );
    private SplineSurface splineSurface;
    private SplineSurface lastRenderState;
    private PBasicInputEventHandler dragHandler;

    public SplineGraphic( EC3Canvas ec3Canvas, SplineSurface splineSurface ) {
        this( ec3Canvas, splineSurface.getTop(), splineSurface.getBottom(), splineSurface );
    }

    private SplineGraphic( EC3Canvas ec3Canvas, AbstractSpline spline, AbstractSpline reverse, SplineSurface splineSurface ) {
        this.ec3Canvas = ec3Canvas;
        this.spline = spline;
        this.reverse = reverse;
        this.splineSurface = splineSurface;
        pathLayer = new PPath();
        pathLayer.setStroke( new BasicStroke( AbstractSpline.SPLINE_THICKNESS ) );
        pathLayer.setStrokePaint( Color.black );
        controlPointLayer = new PNode();

        addChild( pathLayer );
        addChild( controlPointLayer );

        updateAll();
        dragHandler = new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                initDragSpline();
            }

            public void mouseDragged( PInputEvent event ) {
                dragSpline( event );
            }

            public void mouseReleased( PInputEvent event ) {
                finishDragSpline();
            }
        };
        pathLayer.addInputEventListener( this.dragHandler );
        pathLayer.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        pathLayer.addInputEventListener( new PopupMenuHandler( ec3Canvas, new PathPopupMenu( ec3Canvas ) ) );
    }

    public void disableDragHandler() {
        removeInputEventListener( dragHandler );
    }

    public PBasicInputEventHandler getDragHandler() {
        return dragHandler;
    }

    private void dragSpline( PInputEvent event ) {
        Point2D.Double tx = new Point2D.Double( event.getDeltaRelativeTo( this ).width, event.getDeltaRelativeTo( this ).height );
        translateAll( tx );
        proposeMatchesTrunk();
        updateAll();
    }

    public SplineSurface getSplineSurface() {
        return splineSurface;
    }

    public void setSplineSurface( SplineSurface splineSurface ) {
        this.splineSurface = splineSurface;
        this.spline = splineSurface.getTop();
        this.reverse = splineSurface.getBottom();
        updateAll();
    }

    public void forceUpdate() {
        lastRenderState = null;
    }

    class PathPopupMenu extends JPopupMenu {
        public PathPopupMenu( final EC3Canvas ec3Canvas ) {
            JMenuItem delete = new JMenuItem( EnergySkateParkStrings.getString( "delete.track" ) );
            delete.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    ec3Canvas.removeSpline( SplineGraphic.this );
                }
            } );
            add( delete );
        }
    }

    private void finishDragSpline() {
        boolean didAttach = testAttach( 0 );
        if( !didAttach ) {
            testAttach( numControlPointGraphics() - 1 );//can't do two at once.
        }
        initDragSpline = null;
        spline.setUserControlled( false );
    }

    private boolean testAttach( int index ) {
        SplineMatch startMatch = getTrunkMatch( index );
        if( startMatch != null ) {
            attach( index, startMatch );
            return true;
        }
        return false;
    }

    private void initDragSpline() {
        spline.setUserControlled( true );
        initDragSpline = new Point2D.Double[spline.getControlPoints().length];
        for( int i = 0; i < initDragSpline.length; i++ ) {
            initDragSpline[i] = new Point2D.Double( spline.controlPointAt( i ).getX(), spline.controlPointAt( i ).getY() );
        }
    }

    private void proposeMatchesTrunk() {
        boolean ok = proposeMatchTrunk( 0 );
        if( !ok ) {
            proposeMatchTrunk( numControlPointGraphics() - 1 );
        }
    }

    private boolean proposeMatchTrunk( int index ) {
        SplineMatch match = getTrunkMatch( index );
        if( match != null ) {
            System.out.println( "match=" + match );
            spline.controlPointAt( index ).setLocation( match.getTarget().getFullBounds().getCenter2D() );
            updateAll();
            return true;
        }
        else {
            spline.controlPointAt( index ).setLocation( initDragSpline[index] );
            return false;
        }
    }

    private SplineMatch getTrunkMatch( int index ) {
        Point2D toMatch = new Point2D.Double( initDragSpline[index].getX(), initDragSpline[index].getY() );
        return ec3Canvas.proposeMatch( this, toMatch );
    }

    private void translateAll( Point2D pt ) {
        translateAll( pt.getX(), pt.getY() );
    }

    private void translateAll( double dx, double dy ) {
        ec3Canvas.getEnergyConservationModel().splineTranslated( getSplineSurface(), dx, dy );
        spline.translate( dx, dy );
        for( int i = 0; i < initDragSpline.length; i++ ) {
            initDragSpline[i].x += dx;
            initDragSpline[i].y += dy;
        }
    }

    public void updateAll() {
        setPickable( splineSurface.isInteractive() );
        setChildrenPickable( splineSurface.isInteractive() );
//        setVisible( splineSurface.isInteractive() );

//        System.out.println( "changed() = " + changed() );
        if( changed() ) {
            pathLayer.removeAllChildren();
            pathLayer.setPathTo( spline.getInterpolationPath() );

            controlPointLayer.removeAllChildren();
//        pathLayer.setPathTo( new Rectangle( 50,50,50,50) );

            for( int i = 0; i < spline.numControlPoints(); i++ ) {
                Point2D point = spline.controlPointAt( i );
                addControlPoint( point, i );
            }
            for( int i = 0; i < controlPointLayer.getChildrenCount(); i++ ) {
                PPath child = (PPath)controlPointLayer.getChild( i );
                if( i == 0 || i == controlPointLayer.getChildrenCount() - 1 ) {
                    child.setStroke( dottedStroke );
                    child.setStrokePaint( Color.red );
                }
                else {
                    child.setStroke( lineStroke );
                    child.setStrokePaint( Color.black );
                }
            }
            updateReverseSpline();
            lastRenderState = splineSurface.copy();
        }
    }

    private boolean changed() {
        return lastRenderState == null || !same();
    }

    private boolean same() {
        return lastRenderState.equals( splineSurface.copy() );
    }

    private void addControlPoint( Point2D point, final int index ) {
        double width = 0.5;
        PPath controlCircle = new PPath( new Ellipse2D.Double( point.getX() - width / 2, point.getY() - width / 2, width, width ) );

        controlCircle.setStroke( dottedStroke );
        controlCircle.setStrokePaint( Color.black );
        controlCircle.setPaint( new Color( 0, 0, 1f, 0.5f ) );
        controlPointLayer.addChild( controlCircle );
        controlCircle.addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                initDragControlPoint( index );
                spline.setUserControlled( true );
                event.setHandled( true );
            }

            public void mouseReleased( PInputEvent event ) {
                finishDragControlPoint( index );
                spline.setUserControlled( false );
                event.setHandled( true );
            }

            public void mouseDragged( PInputEvent event ) {
                PDimension rel = event.getDeltaRelativeTo( SplineGraphic.this );
                spline.translateControlPoint( index, rel.getWidth(), rel.getHeight() );
                if( index == 0 || index == numControlPointGraphics() - 1 ) {
                    controlPointLoc.x += rel.getWidth();
                    controlPointLoc.y += rel.getHeight();
                }

                proposeMatchesEndpoint( index );
                updateReverseSpline();
                updateAll();
                event.setHandled( true );
            }
        } );
        controlCircle.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        controlCircle.addInputEventListener( new PopupMenuHandler( ec3Canvas, new ControlCirclePopupMenu( index ) ) );
    }

    private void updateReverseSpline() {
        reverse.setControlPoints( reverse( spline.getControlPoints() ) );
    }

    class ControlCirclePopupMenu extends JPopupMenu {
        public ControlCirclePopupMenu( final int index ) {
            super( EnergySkateParkStrings.getString( "circle.popup.menu" ) );
            JMenuItem delete = new JMenuItem( EnergySkateParkStrings.getString( "delete.control.point" ) );
            delete.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if( spline.numControlPoints() == 1 ) {
                        ec3Canvas.removeSpline( SplineGraphic.this );
                    }
                    else {
                        spline.removeControlPoint( index );
                        updateAll();
                    }
                }
            } );
            add( delete );
        }
    }

    private void proposeMatchesEndpoint( int index ) {
        if( index == 0 || index == numControlPointGraphics() - 1 ) {
            SplineMatch match = getEndpointMatch();
            if( match != null ) {
                System.out.println( "match=" + match );
                spline.controlPointAt( index ).setLocation( match.getTarget().getFullBounds().getCenter2D() );
                updateAll();
            }
            else {
                spline.controlPointAt( index ).setLocation( controlPointLoc );
            }
        }
    }

    private SplineMatch getEndpointMatch() {
        Point2D toMatch = new Point2D.Double( controlPointLoc.getX(), controlPointLoc.getY() );
        return ec3Canvas.proposeMatch( this, toMatch );
    }

    private void finishDragControlPoint( int index ) {
        if( index == 0 || index == numControlPointGraphics() - 1 ) {
            SplineMatch match = getEndpointMatch();
            if( match != null ) {
                attach( index, match );
            }
            controlPointLoc = null;
        }
    }

    private void attach( int index, SplineMatch match ) {
        ec3Canvas.attach( this, index, match );
    }

    private void initDragControlPoint( int index ) {
        if( index == 0 || index == numControlPointGraphics() - 1 ) {
            controlPointLoc = new Point2D.Double( spline.controlPointAt( index ).getX(), spline.controlPointAt( index ).getY() );
        }
    }

    private Point2D[] reverse( Point2D[] controlPoints ) {
        ArrayList list = new ArrayList( Arrays.asList( controlPoints ) );
        Collections.reverse( list );
        return (Point2D[])list.toArray( new Point2D[0] );
    }

    public PNode getControlPointGraphic( int index ) {
        return controlPointLayer.getChild( index );
    }

    public int numControlPointGraphics() {
        return controlPointLayer.getChildrenCount();
    }

    public void setControlPointsPickable( boolean pick ) {
        controlPointLayer.setPickable( pick );
        controlPointLayer.setChildrenPickable( pick );
        for( int i = 0; i < numControlPointGraphics(); i++ ) {
            getControlPointGraphic( i ).setPickable( pick );
            getControlPointGraphic( i ).setChildrenPickable( pick );
        }

    }
}
