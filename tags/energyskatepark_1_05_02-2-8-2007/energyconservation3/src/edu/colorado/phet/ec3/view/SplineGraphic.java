/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.view;

import edu.colorado.phet.ec3.EnergySkateParkSimulationPanel;
import edu.colorado.phet.ec3.EnergySkateParkStrings;
import edu.colorado.phet.ec3.FloorSpline;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.SplineSurface;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.event.PopupMenuHandler;
import edu.colorado.phet.piccolo.nodes.PhetPPath;
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

/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 1:17:41 PM
 * Copyright (c) Sep 26, 2005 by Sam Reid
 */

public class SplineGraphic extends PNode {
    private EnergySkateParkSimulationPanel ec3Canvas;
    private AbstractSpline spline;
    private PPath splinePath;
    private PhetPPath splineFrontPath;

    private PNode controlPointLayer;

    private Point2D.Double[] initDragSpline;
    private Point2D.Double controlPointLoc;

    private BasicStroke dottedStroke = new BasicStroke( 0.03f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, new float[]{0.09f, 0.09f}, 0 );
    private BasicStroke lineStroke = new BasicStroke( 0.03f );
    private SplineSurface splineSurface;
    private SplineSurface lastRenderState;
    private PBasicInputEventHandler dragHandler;

    public SplineGraphic( EnergySkateParkSimulationPanel ec3Canvas, SplineSurface splineSurface ) {
        this( ec3Canvas, splineSurface.getSpline(), splineSurface );
    }

    private SplineGraphic( EnergySkateParkSimulationPanel ec3Canvas, AbstractSpline spline, SplineSurface splineSurface ) {
        this.ec3Canvas = ec3Canvas;
        this.spline = spline;
        this.splineSurface = splineSurface;
        splinePath = new PhetPPath( getTrackStroke( 1.0f ), Color.black );
        splineFrontPath = new PhetPPath( getRailroadStroke( 0.4f ), Color.gray );
        splineFrontPath.setPickable( false );
        splineFrontPath.setChildrenPickable( false );
        controlPointLayer = new PNode();

        addChild( splinePath );
        addChild( splineFrontPath );
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
        splinePath.addInputEventListener( this.dragHandler );
        splinePath.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        splinePath.addInputEventListener( new PopupMenuHandler( ec3Canvas, new PathPopupMenu( ec3Canvas ) ) );
    }

    private BasicStroke getTrackStroke( float thickness ) {
        return new BasicStroke( AbstractSpline.SPLINE_THICKNESS * thickness );
    }

    private BasicStroke getRailroadStroke( float thickness ) {
        return new BasicStroke( AbstractSpline.SPLINE_THICKNESS * thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{AbstractSpline.SPLINE_THICKNESS * 0.4f, AbstractSpline.SPLINE_THICKNESS * 0.6f}, 0 );
    }

    public void disableDragHandler() {
        removeInputEventListener( dragHandler );
    }

    public PBasicInputEventHandler getDragHandler() {
        return dragHandler;
    }

    private void dragSpline( PInputEvent event ) {
        Point2D.Double tx = new Point2D.Double( event.getDeltaRelativeTo( this ).width, event.getDeltaRelativeTo( this ).height );
        dragSpline( tx );
    }

    private void dragSpline( Point2D.Double tx ) {
        translateAll( tx );
        proposeMatchesTrunk();
        updateAll();
    }

    public SplineSurface getSplineSurface() {
        return splineSurface;
    }

    public void setSplineSurface( SplineSurface splineSurface ) {
        this.splineSurface = splineSurface;
        this.spline = splineSurface.getSpline();
        updateAll();
    }

    public void forceUpdate() {
        lastRenderState = null;
    }

    public void processExternalStartDragEvent() {
        initDragSpline();
    }

    public void processExternalDragEvent( double dx, double dy ) {
        dragSpline( new Point2D.Double( dx, dy ) );
    }

    public void processExternalDropEvent() {
        finishDragSpline();
    }

    class PathPopupMenu extends JPopupMenu {
        public PathPopupMenu( final EnergySkateParkSimulationPanel ec3Canvas ) {
            final JCheckBoxMenuItem rollerCoasterMode = new JCheckBoxMenuItem( "Roller-Coaster Mode", spline.isRollerCoasterMode() );
            rollerCoasterMode.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    spline.setRollerCoasterMode( rollerCoasterMode.isSelected() );
                    lastRenderState = null;
                    updateAll();//todo should be notification mechanism
                }
            } );

            JMenuItem delete = new JMenuItem( EnergySkateParkStrings.getString( "delete.track" ) );
            delete.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    ec3Canvas.removeSpline( SplineGraphic.this );
                }
            } );
            add( rollerCoasterMode );
            addSeparator();
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
        if( changed() ) {
            splinePath.setPathTo( spline.getInterpolationPath() );
            splineFrontPath.setPathTo( spline.getInterpolationPath() );
            splineFrontPath.setVisible( spline.isRollerCoasterMode() );
            splineFrontPath.setStrokePaint( spline.isRollerCoasterMode() ? Color.gray : Color.black );

            controlPointLayer.removeAllChildren();

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
            lastRenderState = splineSurface.copy();
        }
        setVisible( !( spline instanceof FloorSpline ) );
    }

    private boolean changed() {
        return lastRenderState == null || !same();
    }

    private boolean same() {
        return lastRenderState.equals( splineSurface.copy() );
    }

    private class ControlPointNode extends PPath {

        public ControlPointNode( double x, double y, double diameter, final int index ) {
            super( new Ellipse2D.Double( x - diameter / 2, y - diameter / 2, diameter, diameter ) );

            setStroke( dottedStroke );
            setStrokePaint( Color.black );
            setPaint( new Color( 0, 0, 1f, 0.5f ) );

            addInputEventListener( new PBasicInputEventHandler() {
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
                    if( spline.getControlPoints()[index].getY() + rel.getHeight() < 0 ) {
                        rel.height = 0 - spline.getControlPoints()[index].getY();
                    }
                    spline.translateControlPoint( index, rel.getWidth(), rel.getHeight() );
                    if( index == 0 || index == numControlPointGraphics() - 1 ) {
                        controlPointLoc.x += rel.getWidth();
                        controlPointLoc.y += rel.getHeight();
                    }

                    proposeMatchesEndpoint( index );
                    updateAll();
                    event.setHandled( true );
                }
            } );
            addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
            addInputEventListener( new PopupMenuHandler( ec3Canvas, new ControlCirclePopupMenu( index ) ) );
        }
    }

    private void addControlPoint( Point2D point, int index ) {
        controlPointLayer.addChild( new ControlPointNode( point.getX(), point.getY(), 0.5, index ) );
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
