package edu.colorado.phet.ec3;

import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.CubicSpline;
import edu.colorado.phet.ec3.model.spline.SplineSurface;
import edu.colorado.phet.ec3.view.SplineGraphic;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.nodes.PhetPPath;
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
 * Copyright (c) Sep 30, 2005 by Sam Reid
 */

public class SplineToolbox2 extends PNode {
    private EnergySkateParkSimulationPanel energySkateParkSimulationPanel;
    private PText textGraphic;
    private EnergySkateParkRootNode energySkateParkRootNode;
    private PNode draggableIcon;
    private PPath boundGraphic;
    private boolean created = false;
    private SplineSurface createdSurface;

    public SplineToolbox2( final EnergySkateParkSimulationPanel energySkateParkSimulationPanel, EnergySkateParkRootNode energySkateParkRootNode ) {
        this.energySkateParkRootNode = energySkateParkRootNode;
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
                AbstractSpline spline = new CubicSpline( EnergySkateParkSimulationPanel.NUM_CUBIC_SPLINE_SEGMENTS );
                spline.addControlPoint( pt.getX() - 1, pt.getY() );
                spline.addControlPoint( pt.getX(), pt.getY() );
                spline.addControlPoint( pt.getX() + 1, pt.getY() );

                createdSurface = new SplineSurface( spline );
                energySkateParkSimulationPanel.getEnergyConservationModel().addSplineSurface( createdSurface );
                energySkateParkSimulationPanel.redrawAllGraphics();
                energySkateParkSimulationPanel.getSplineGraphic( createdSurface ).processExternalStartDragEvent();
            }

            public void mouseReleased( PInputEvent event ) {
                energySkateParkSimulationPanel.getSplineGraphic( createdSurface ).processExternalDropEvent();
                created = false;
            }
        } );

        boundGraphic = new PPath( new Rectangle( 200, 60 ) );
        boundGraphic.setStroke( new BasicStroke( 2 ) );
        boundGraphic.setStrokePaint( Color.blue );
        boundGraphic.setPaint( Color.yellow );
        addChild( boundGraphic );
        textGraphic = new PText( EnergySkateParkStrings.getString( "drag.to.add.track" ) );
        textGraphic.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
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

    public void centerTheNode() {
        draggableIcon.setScale( 60 );
        draggableIcon.setOffset( boundGraphic.getFullBounds().getWidth() / 2 - draggableIcon.getFullBounds().getWidth() / 2, boundGraphic.getFullBounds().getHeight() / 2 );
    }

    private SplineGraphic createSplineGraphic() {
        SplineSurface surface = createSplineSurface();

        final SplineGraphic splineGraphic = new SplineGraphic( energySkateParkSimulationPanel, surface );
        splineGraphic.setControlPointsPickable( false );
        splineGraphic.updateAll();
        return splineGraphic;
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

    private SplineSurface createSplineSurface() {
        AbstractSpline spline = new CubicSpline( EnergySkateParkSimulationPanel.NUM_CUBIC_SPLINE_SEGMENTS );
        spline.addControlPoint( 0, 0 );
        spline.addControlPoint( 1, 0 );
        spline.addControlPoint( 2.0, 0 );

        return new SplineSurface( spline );
    }
}
