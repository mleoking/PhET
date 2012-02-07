// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.beerslaw.model.Cuvette;
import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.common.model.Solution;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.HighlightHandler.PaintHighlightHandler;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.colorado.phet.common.piccolophet.simsharing.NonInteractiveEventHandler;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Visual representation of the cuvette.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class CuvetteNode extends PNode {

    private static final double PERCENT_FULL = 0.85;
    private static final double ARROW_WIDTH = 80;
    private static final Color ARROW_FILL = Color.ORANGE;

    public CuvetteNode( final Cuvette cuvette, final Solution solution, final ModelViewTransform mvt ) {

        // nodes
        final PPath cuvetteNode = new PPath() {{
            setStroke( new BasicStroke( 2f ) );
        }};
        final PPath solutionNode = new PPath() {{
            setStroke( BLLConstants.FLUID_STROKE );
        }};
        final DoubleArrowNode widthHandleNode = new DoubleArrowNode( new Point2D.Double( -ARROW_WIDTH / 2, 0 ), new Double( ARROW_WIDTH / 2, 0 ), 25, 30, 15 ) {{
            setPaint( ARROW_FILL );
        }};

        // rendering order
        addChild( solutionNode );
        addChild( cuvetteNode );
        addChild( widthHandleNode );

        // when the cuvette's width changes...
        cuvette.width.addObserver( new SimpleObserver() {
            public void update() {
                final double width = mvt.modelToViewDeltaX( cuvette.width.get() );
                final double height = mvt.modelToViewDeltaY( cuvette.height );
                cuvetteNode.setPathTo( new Rectangle2D.Double( 0, 0, width, height ) );
                solutionNode.setPathTo( new Rectangle2D.Double( 0, 0, width, PERCENT_FULL * height ) );
                solutionNode.setOffset( cuvetteNode.getXOffset(),
                                        cuvetteNode.getFullBoundsReference().getMaxY() - solutionNode.getFullBoundsReference().getHeight() );
                widthHandleNode.setOffset( cuvetteNode.getFullBoundsReference().getMaxX(),
                                           0.85 * cuvetteNode.getFullBoundsReference().getHeight() );
            }
        } );

        // when the solution color changes...
        solution.addFluidColorObserver( new SimpleObserver() {
            public void update() {
                Color solutonColor = solution.getFluidColor();
                solutionNode.setPaint( solutonColor );
                solutionNode.setStrokePaint( BLLConstants.createFluidStrokeColor( solutonColor ) );
            }
        } );

        // location of the cuvette
        setOffset( mvt.modelToView( cuvette.location.toPoint2D() ) );

        // Event handlers
        cuvetteNode.addInputEventListener( new NonInteractiveEventHandler( UserComponents.cuvette ) );
        solutionNode.addInputEventListener( new NonInteractiveEventHandler( UserComponents.solution ) );
        widthHandleNode.addInputEventListener( new CursorHandler() );
        addInputEventListener( new PaintHighlightHandler( widthHandleNode, ARROW_FILL, ARROW_FILL.brighter() ) );
        addInputEventListener( new WidthDragHandler( this, cuvette, mvt ) );
    }

    // Drag handler for manipulating the cuvette's width
    private static class WidthDragHandler extends SimSharingDragHandler {

        private final Cuvette cuvette;
        private final CuvetteNode cuvetteNode;
        private final ModelViewTransform mvt;

        private double startXOffset; // x offset of mouse click from cuvette's origin
        private double startWidth; // width of the cuvette when the drag started

        public WidthDragHandler( CuvetteNode cuvetteNode, Cuvette cuvette, ModelViewTransform mvt ) {
            super( UserComponents.cuvetteWidthHandle, UserComponentTypes.sprite );
            this.cuvette = cuvette;
            this.cuvetteNode = cuvetteNode;
            this.mvt = mvt;
        }

        @Override protected void startDrag( PInputEvent event ) {
            super.startDrag( event );
            startXOffset = event.getPositionRelativeTo( cuvetteNode ).getX();
            startWidth = cuvette.width.get();
        }

        @Override protected void drag( PInputEvent event ) {
            super.drag( event );
            double dragXOffset = event.getPositionRelativeTo( cuvetteNode ).getX();
            double deltaWidth = mvt.viewToModelDeltaX( dragXOffset - startXOffset );
            double cuvetteWidth = MathUtil.clamp( startWidth + deltaWidth, cuvette.widthRange );
            cuvette.width.set( cuvetteWidth );
        }
    }
}
