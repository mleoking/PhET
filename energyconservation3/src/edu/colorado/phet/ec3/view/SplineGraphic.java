/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.view;

import edu.colorado.phet.ec3.EC3Canvas;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.piccolo.CursorHandler;
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
import java.awt.geom.GeneralPath;
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

    private BasicStroke dottedStroke = new BasicStroke( 2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, new float[]{3, 5}, 0 );
    private BasicStroke lineStroke = new BasicStroke( 2 );

    public SplineGraphic( EC3Canvas ec3Canvas, AbstractSpline spline, AbstractSpline reverse ) {
        this.ec3Canvas = ec3Canvas;
        this.spline = spline;
        this.reverse = reverse;
        pathLayer = new PPath();
        pathLayer.setStroke( new BasicStroke( AbstractSpline.SPLINE_THICKNESS ) );
        pathLayer.setStrokePaint( Color.black );
        controlPointLayer = new PNode();

        addChild( pathLayer );
        addChild( controlPointLayer );

        updateAll();
        pathLayer.addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                super.mousePressed( event );
                initDragSpline();
            }

            public void mouseDragged( PInputEvent event ) {
                super.mouseDragged( event );
                translateAll( event.getDeltaRelativeTo( SplineGraphic.this ).width, event.getDeltaRelativeTo( SplineGraphic.this ).height );
                proposeMatchesTrunk();
                updateAll();
            }

            public void mouseReleased( PInputEvent event ) {
                super.mouseReleased( event );
                finishDragSpline();
            }
        } );
        pathLayer.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        pathLayer.addInputEventListener( new PopupMenuHandler( ec3Canvas, new PathPopupMenu( ec3Canvas ) ) );
    }

    class PathPopupMenu extends JPopupMenu {
        public PathPopupMenu( final EC3Canvas ec3Canvas ) {
            JMenuItem delete = new JMenuItem( "Delete Track" );
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
        SplineMatch match = ec3Canvas.proposeMatch( this, toMatch );
        return match;
    }

    private void translateAll( double dx, double dy ) {
        spline.translate( dx, dy );
        for( int i = 0; i < initDragSpline.length; i++ ) {
            initDragSpline[i].x += dx;
            initDragSpline[i].y += dy;
        }
    }

    private void updateAll() {
        pathLayer.removeAllChildren();
        controlPointLayer.removeAllChildren();
        GeneralPath path = spline.getInterpolationPath();
        pathLayer.setPathTo( path );
        for( int i = 0; i < spline.numControlPoints(); i++ ) {
            Point2D point = spline.controlPointAt( i );
            addControlPoint( point, i );
        }
        for( int i = 0; i < pathLayer.getChildrenCount(); i++ ) {
            PPath child = (PPath)pathLayer.getChild( i );
            if( i == 0 || i == pathLayer.getChildrenCount() - 1 ) {
                child.setStroke( dottedStroke );
                child.setStrokePaint( Color.red );
            }
            else {
                child.setStroke( lineStroke );
                child.setStrokePaint( Color.black );
            }
        }
        updateReverseSpline();
    }

    private void addControlPoint( Point2D point, final int index ) {
        int width = 20;
        PPath controlCircle = new PPath( new Ellipse2D.Double( point.getX() - width / 2, point.getY() - width / 2, width, width ) );

        controlCircle.setStroke( dottedStroke );
        controlCircle.setStrokePaint( Color.black );
        controlCircle.setPaint( new Color( 0, 0, 1f, 0.5f ) );
        pathLayer.addChild( controlCircle );
        controlCircle.addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                initDragControlPoint( index );
                event.setHandled( true );
            }

            public void mouseReleased( PInputEvent event ) {
                finishDragControlPoint( index );
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

    class ControlCirclePopupMenu extends JPopupMenu {
        public ControlCirclePopupMenu( final int index ) {
            super( "Circle Popup Menu" );
            JMenuItem delete = new JMenuItem( "Delete Control Point" );
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
        SplineMatch match = ec3Canvas.proposeMatch( this, toMatch );//todo allow joining end of this to start of this?
        return match;
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

    private void updateReverseSpline() {
        reverse.setControlPoints( reverse( spline.getControlPoints() ) );
    }

    private Point2D[] reverse( Point2D[] controlPoints ) {
        ArrayList list = new ArrayList( Arrays.asList( controlPoints ) );
        Collections.reverse( list );
        return (Point2D[])list.toArray( new Point2D[0] );
    }

    public PNode getControlPointGraphic( int index ) {
        return pathLayer.getChild( index );
    }

    public int numControlPointGraphics() {
        return pathLayer.getChildrenCount();
    }

    public AbstractSpline getSpline() {
        return spline;
    }

    public AbstractSpline getReverseSpline() {
        return reverse;
    }

    public void disableDragControlPoints() {
        for( int i = 0; i < numControlPointGraphics(); i++ ) {
            getControlPointGraphic( i ).setPickable( false );
            getControlPointGraphic( i ).setChildrenPickable( false );
        }
    }
}
