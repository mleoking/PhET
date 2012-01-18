// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.piccolo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.common.spline.ParametricFunction2D;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkSpline;
import edu.colorado.phet.energyskatepark.model.PreFabSplines;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSimulationPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter.param;
import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.ParameterKeys.numTracks;
import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.ParameterKeys.trackIndex;
import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.UserComponents.toolboxTrack;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 12:03:00 AM
 */

public class SplineToolboxNode extends PNode {
    private final EnergySkateParkSimulationPanel energySkateParkSimulationPanel;
    private final boolean splinesMovable;
    private final PText textGraphic;
    private final PNode draggableIcon;
    private final PPath boundGraphic;
    private boolean created = false;
    private EnergySkateParkSpline createdSurface;

    public SplineToolboxNode( final EnergySkateParkSimulationPanel energySkateParkSimulationPanel, boolean splinesMovable ) {
        this.energySkateParkSimulationPanel = energySkateParkSimulationPanel;
        this.splinesMovable = splinesMovable;
        this.draggableIcon = new PNodeFacade( createSplineGraphic() ) {{

            //Limit the number of user-created splines to be 4 (keep in mind the floor is another spline in the count)
            energySkateParkSimulationPanel.getEnergySkateParkModule().numberOfSplines.addObserver( new VoidFunction1<Integer>() {
                public void apply( Integer numSplines ) {

                    //Really not sure why this number should be 3.  I determined it experimentally and it seemed to have the right behavior as described above
                    final boolean visible = numSplines <= 3;
                    setVisible( visible );
                    setPickable( visible );
                    setChildrenPickable( visible );
                }
            } );
        }};
        draggableIcon.addInputEventListener( new CursorHandler() );
        draggableIcon.addInputEventListener( new SimSharingDragHandler( toolboxTrack, UserComponentTypes.sprite ) {

            //Indicate how many tracks the user has created, including this one
            @Override public ParameterSet getStartDragParameters( PInputEvent event ) {

                //Use the class count to get the new track ID, assumes nothing else will happen between now and then to change that index
                return super.getStartDragParameters( event ).param( numTracks, energySkateParkSimulationPanel.getEnergySkateParkModel().getNumSplines() + 1 ).param( trackIndex, ParametricFunction2D.count );
            }

            @Override public ParameterSet getEndDragParameters( PInputEvent event ) {
                return super.getEndDragParameters( event ).addAll( param( trackIndex, createdSurface.getParametricFunction2D().index ) );
            }

            @Override protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                if ( !created ) {
                    create( event );
                    created = true;
                }
                else {
                    drag( event );
                }
            }

            @Override protected void drag( PInputEvent event ) {
                super.drag( event );
                energySkateParkSimulationPanel.dragSplineSurface( event, createdSurface );
            }

            @Override protected void endDrag( PInputEvent event ) {
                super.endDrag( event );
                if ( created ) { // guards against the case where the mouse was pressed but not dragged
                    energySkateParkSimulationPanel.getSplineNode( createdSurface ).processExternalDropEvent( event );
                    created = false;
                }
            }

            private void create( PInputEvent event ) {

                //Add 1 to account for the track this will create.
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
        } );

        boundGraphic = new PPath( new RoundRectangle2D.Double( 0, 0, 180, 60, 20, 20 ) );
        boundGraphic.setStroke( new BasicStroke( 2 ) );
        boundGraphic.setStrokePaint( Color.blue );
        boundGraphic.setPaint( Color.yellow );
        addChild( boundGraphic );
        textGraphic = new PText( EnergySkateParkResources.getString( "controls.add-track" ) );
        textGraphic.setFont( new PhetFont( Font.BOLD, 14 ) );
        textGraphic.setOffset( boundGraphic.getFullBounds().getCenterX() - textGraphic.getFullBounds().getWidth() / 2, boundGraphic.getFullBounds().getY() + 2 );
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
        final double scale = 60;
        draggableIcon.setScale( scale );
        draggableIcon.centerFullBoundsOnPoint( boundGraphic.getFullBounds().getCenterX(), boundGraphic.getFullBounds().getCenterY() );
        draggableIcon.translate( 0, 5 / scale );
    }

    private SplineNode createSplineGraphic() {
        EnergySkateParkSpline surface = createSplineSurface();

        final SplineNode splineNode = new SplineNode( energySkateParkSimulationPanel, surface, energySkateParkSimulationPanel, splinesMovable );
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

        return new EnergySkateParkSpline( spline.getControlPoints() );
    }
}
