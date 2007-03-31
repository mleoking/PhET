/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.util.SwingUtils;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkSpline;
import edu.colorado.phet.energyskatepark.model.physics.ParametricFunction2D;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.event.PopupMenuHandler;
import edu.colorado.phet.piccolo.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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

public class SplineNode extends PNode {
    private TrackNode splinePath;
    private PhetPPath rollerCoasterPath;

    private PNode controlPointLayer;

    private Point2D.Double[] initDragSpline;
    private Point2D.Double controlPointLoc;

    private BasicStroke dottedStroke = new BasicStroke( 0.03f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, new float[]{0.09f, 0.09f}, 0 );
    private BasicStroke lineStroke = new BasicStroke( 0.03f );
    private EnergySkateParkSpline spline;
    private EnergySkateParkSpline lastState;
    private PBasicInputEventHandler dragHandler;

    private JComponent parent;
    private EnergySkateParkSplineEnvironment ec3Canvas;
    private TrackNode centerPath;

    class TrackNode extends PhetPPath {
        private float thickness;
        private Color color;

        public TrackNode( float thickness, Color strokePaint ) {
            super( getTrackStroke( thickness ), strokePaint );
            this.thickness = thickness;
            this.color = strokePaint;
        }

        public float getThickness() {
            return thickness;
        }

        public void setThickness( float thickness ) {
            this.thickness = thickness;
            setStroke( getTrackStroke( thickness ) );
        }

        public Color getColor() {
            return color;
        }

        public void setColor( Color color ) {
            this.color = color;
            setStrokePaint( color );
        }
    }

    public SplineNode( JComponent parent, EnergySkateParkSpline splineSurface, EnergySkateParkSplineEnvironment ec3Canvas ) {
        this.parent = parent;
        this.ec3Canvas = ec3Canvas;
//        this.spline = spline;
        this.spline = splineSurface;
        //Original recommendation
//        splinePath = new TrackNode( 1.0f, Color.gray );
//        centerPath = new TrackNode( 0.2f, Color.black );

        //Kathy's recommendation:
        splinePath = new TrackNode( 0.75f, Color.gray );
        centerPath = new TrackNode( 0.15f, new Color( 235, 193, 56 ) );

        rollerCoasterPath = new PhetPPath( getRailroadStroke( 0.4f ), Color.gray );
        rollerCoasterPath.setPickable( false );
        rollerCoasterPath.setChildrenPickable( false );

        centerPath.setPickable( false );
        centerPath.setChildrenPickable( false );

        controlPointLayer = new PNode();

        addChild( splinePath );
        addChild( centerPath );
        addChild( rollerCoasterPath );
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
        splinePath.addInputEventListener( new PopupMenuHandler( parent, new PathPopupMenu( ec3Canvas ) ) );
    }

    class TrackEditPanel extends JPanel {

        public TrackEditPanel( final TrackNode splinePath, String name ) {
            final ModelSlider modelSlider = new ModelSlider( name, "", 0, 10, splinePath.getThickness() );
            modelSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    splinePath.setThickness( (float)modelSlider.getValue() );
                }
            } );
            setLayout( new GridBagLayout() );
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = GridBagConstraints.RELATIVE;
            gridBagConstraints.gridy = 0;

            final JColorChooser colorChooser = new JColorChooser( splinePath.getColor() );
            colorChooser.getSelectionModel().addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    splinePath.setColor( colorChooser.getColor() );
                }
            } );

            add( modelSlider, gridBagConstraints );
            add( colorChooser, gridBagConstraints );
        }
    }

    private void showColorControls() {
        JPanel panel = new JPanel();
        panel.setLayout( new GridBagLayout() );
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = GridBagConstraints.RELATIVE;

        panel.add( new TrackEditPanel( splinePath, "Outside Line" ), gridBagConstraints );
        panel.add( new TrackEditPanel( centerPath, "Center Line" ), gridBagConstraints );

        JDialog colorControls = new JDialog( (Frame)SwingUtilities.getWindowAncestor( parent ) );
        colorControls.setContentPane( panel );
        colorControls.pack();
        SwingUtils.centerDialogInParent( colorControls );
        colorControls.setVisible( true );
    }

    public ParametricFunction2D getParametricFunction2D() {
        return spline.getParametricFunction2D();
    }

    private BasicStroke getTrackStroke( float thickness ) {
        return new BasicStroke( (float)( EnergySkateParkModel.SPLINE_THICKNESS * thickness ) );
    }

    private BasicStroke getRailroadStroke( float thickness ) {
        return new BasicStroke( (float)( EnergySkateParkModel.SPLINE_THICKNESS * thickness ), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{(float)( EnergySkateParkModel.SPLINE_THICKNESS * 0.4 ), (float)( EnergySkateParkModel.SPLINE_THICKNESS * 0.6f )}, 0 );
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

    public EnergySkateParkSpline getSpline() {
        return spline;
    }

    public void setSpline( EnergySkateParkSpline spline ) {
        this.spline = spline;
//        this.spline = spline.getSpline();
        updateAll();
    }

    public void forceUpdate() {
        lastState = null;
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
        public PathPopupMenu( final EnergySkateParkSplineEnvironment ec3Canvas ) {
            final JCheckBoxMenuItem rollerCoasterMode = new JCheckBoxMenuItem( "Roller-Coaster Mode", spline.isRollerCoasterMode() );
            rollerCoasterMode.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    spline.setRollerCoasterMode( rollerCoasterMode.isSelected() );
                    lastState = null;
                    updateAll();//todo should be notification mechanism
                }
            } );

            JMenuItem delete = new JMenuItem( EnergySkateParkStrings.getString( "delete.track" ) );
            delete.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    ec3Canvas.removeSpline( SplineNode.this );
                }
            } );

            JMenuItem colors = new JMenuItem( "Edit look" );
            colors.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    showColorControls();
                }
            } );

            add( rollerCoasterMode );
            addSeparator();
            add( delete );
            addSeparator();
            add( colors );
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
            spline.setControlPointLocation( index, match.getTarget().getFullBounds().getCenter2D() );
            updateAll();
            return true;
        }
        else {
            spline.setControlPointLocation( index, initDragSpline[index] );
            return false;
        }
    }

    private SplineMatch getTrunkMatch( int index ) {
        if( initDragSpline == null ) {
            System.out.println( "initdragspline was null" );
            return null;
        }
        if( index < 0 || index > initDragSpline.length ) {
            System.out.println( "index = " + index + ", initDragSpline.length=" + initDragSpline.length );
            return null;
        }
        else {
            Point2D toMatch = new Point2D.Double( initDragSpline[index].getX(), initDragSpline[index].getY() );
            return ec3Canvas.proposeMatch( this, toMatch );
        }
    }

    private void translateAll( Point2D pt ) {
        translateAll( pt.getX(), pt.getY() );
    }

    private void translateAll( double dx, double dy ) {
        spline.translate( dx, dy );
        for( int i = 0; i < initDragSpline.length; i++ ) {
            initDragSpline[i].x += dx;
            initDragSpline[i].y += dy;
        }
    }

    public void updateAll() {
        setPickable( spline.isInteractive() );
        setChildrenPickable( spline.isInteractive() );
        if( changed() ) {
            splinePath.setPathTo( spline.getInterpolationPath() );
            centerPath.setPathTo( spline.getInterpolationPath() );
            rollerCoasterPath.setPathTo( spline.getInterpolationPath() );
            rollerCoasterPath.setVisible( spline.isRollerCoasterMode() );
            rollerCoasterPath.setStrokePaint( spline.isRollerCoasterMode() ? Color.gray : Color.black );

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
            lastState = spline.copy();
        }
//        setVisible( !( spline instanceof FloorSpline ) );
//        setVisible( !( spline instanceof FloorSpline ) );//todo: handle floor invisibility
    }

    private boolean changed() {
        return lastState == null || !same();
    }

    private boolean same() {
        return lastState.equals( spline.copy() );
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
                    PDimension rel = event.getDeltaRelativeTo( SplineNode.this );
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
            addInputEventListener( new PopupMenuHandler( parent, new ControlCirclePopupMenu( index ) ) );
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
                        ec3Canvas.removeSpline( SplineNode.this );
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
