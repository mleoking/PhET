// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.piccolo;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkSpline;
import edu.colorado.phet.energyskatepark.model.PreFabSplines;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSimulationPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 12:03:00 AM
 */

public class SplineToolboxNode extends PNode {
    private EnergySkateParkSimulationPanel energySkateParkSimulationPanel;
    private PText textGraphic;
    private PNode draggableIcon;
    private PPath boundGraphic;
    private boolean created = false;
    private EnergySkateParkSpline createdSurface;

    public SplineToolboxNode( final EnergySkateParkSimulationPanel energySkateParkSimulationPanel ) {
        this.energySkateParkSimulationPanel = energySkateParkSimulationPanel;
        this.draggableIcon = new PNodeFacade( createSplineGraphic() );
        draggableIcon.addInputEventListener( new CursorHandler() );
        draggableIcon.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                if( !created ) {
                    create( event );
                    created = true;
                }
                else {
                    drag( event );
                }
            }

            private void drag( PInputEvent event ) {
                energySkateParkSimulationPanel.dragSplineSurface( event, createdSurface );
            }

            private void create( PInputEvent event ) {
                Point2D pt = new Point2D.Double( event.getCanvasPosition().getX(), event.getCanvasPosition().getY() );
                energySkateParkSimulationPanel.getRootNode().screenToWorld( pt );
                PreFabSplines.CubicSpline spline = new PreFabSplines.CubicSpline();
                spline.addControlPoint( pt.getX() - 1, pt.getY() );
                spline.addControlPoint( pt.getX(), pt.getY() );
                spline.addControlPoint( pt.getX() + 1, pt.getY() );

                createdSurface = new EnergySkateParkSpline( spline.getControlPoints() );
                energySkateParkSimulationPanel.getEnergySkateParkModel().addSplineSurface( createdSurface );
                energySkateParkSimulationPanel.getSplineNode( createdSurface ).processExternalStartDragEvent();
            }

            public void mouseReleased( PInputEvent event ) {
                energySkateParkSimulationPanel.getSplineNode( createdSurface ).processExternalDropEvent( event );
                created = false;
            }
        } );

        boundGraphic = new PPath( new Rectangle( 200, 60 ) );
        boundGraphic.setStroke( new BasicStroke( 2 ) );
        boundGraphic.setStrokePaint( Color.blue );
        boundGraphic.setPaint( Color.yellow );
        addChild( boundGraphic );
        textGraphic = new PText( EnergySkateParkStrings.getString( "controls.add-track" ) );
        textGraphic.setFont( new PhetFont( Font.BOLD, 14 ) );
        textGraphic.setOffset( boundGraphic.getFullBounds().getX() + 5, boundGraphic.getFullBounds().getY() + 2 );
        addChild( textGraphic );
        addChild( this.draggableIcon );

        energySkateParkSimulationPanel.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                centerTheNode();
            }
        } );
        centerTheNode();
    }

    private void centerTheNode() {
        draggableIcon.setScale( 60 );
        draggableIcon.setOffset( boundGraphic.getFullBounds().getWidth() / 2 - draggableIcon.getFullBounds().getWidth() / 2, boundGraphic.getFullBounds().getHeight() / 2 );
    }

    private SplineNode createSplineGraphic() {
        EnergySkateParkSpline surface = createSplineSurface();

        final SplineNode splineNode = new SplineNode( energySkateParkSimulationPanel, surface, energySkateParkSimulationPanel );
        splineNode.setControlPointsPickable( false );

        return splineNode;
    }

    public static class PNodeFacade extends PNode {
        public PNodeFacade( PNode target ) {
            addChild( target );
            target.setPickable( false );
            target.setChildrenPickable( false );
            PBounds bounds = target.getFullBounds();
            addChild( new PhetPPath( bounds, new Color( 0, 0, 0, 0 ) ) );
        }
    }

    private EnergySkateParkSpline createSplineSurface() {
        PreFabSplines.CubicSpline spline = new PreFabSplines.CubicSpline();
        spline.addControlPoint( 0, 0 );
        spline.addControlPoint( 1, 0 );
        spline.addControlPoint( 2.0, 0 );

//        return new SplineSurface( spline );
        return new EnergySkateParkSpline( spline.getControlPoints() );
    }
}
