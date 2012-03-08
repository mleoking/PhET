// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.Cuvette;
import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.HighlightHandler.PaintHighlightHandler;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
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

    private static final double PERCENT_FULL = 0.92;
    private static final double ARROW_WIDTH = 80;
    private static final Color ARROW_FILL = Color.ORANGE;
    private static final int SOLUTION_ALPHA = 150;

    public CuvetteNode( final Cuvette cuvette, final Property<BeersLawSolution> solution, final ModelViewTransform mvt, double snapInterval ) {

        // nodes
        final PPath cuvetteNode = new PPath() {{
            setPickable( false ); // so that things behind the cuvette can be manipulated
            setStroke( new BasicStroke( 3f ) );
        }};
        final PPath solutionNode = new PPath() {{
            setPickable( false ); // so that things behind the solution can be manipulated
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
                cuvetteNode.setPathTo( new DoubleGeneralPath() {{
                    moveTo( 0, 0 );
                    lineTo( 0, height );
                    lineTo( width, height );
                    lineTo( width, 0 );
                }}.getGeneralPath() );
                solutionNode.setPathTo( new Rectangle2D.Double( 0, 0, width, PERCENT_FULL * height ) );
                solutionNode.setOffset( cuvetteNode.getXOffset(),
                                        cuvetteNode.getFullBoundsReference().getMaxY() - solutionNode.getFullBoundsReference().getHeight() );
                widthHandleNode.setOffset( cuvetteNode.getFullBoundsReference().getMaxX(),
                                           0.85 * cuvetteNode.getFullBoundsReference().getHeight() );
            }
        } );

        // fluid color observer
        final VoidFunction1<Color> colorObserver = new VoidFunction1<Color>() {
            public void apply( Color color ) {
                updateColor( solutionNode, color );
            }
        };
        solution.get().fluidColor.addObserver( colorObserver );

        // when the solution changes, rewire the color observer
        solution.addObserver( new ChangeObserver<BeersLawSolution>() {
            public void update( BeersLawSolution newSolution, BeersLawSolution oldSolution ) {
                oldSolution.fluidColor.removeObserver( colorObserver );
                newSolution.fluidColor.addObserver( colorObserver );
            }
        } );

        // location of the cuvette
        setOffset( mvt.modelToView( cuvette.location.toPoint2D() ) );

        // Event handlers
        widthHandleNode.addInputEventListener( new CursorHandler() );
        widthHandleNode.addInputEventListener( new PaintHighlightHandler( widthHandleNode, ARROW_FILL, ARROW_FILL.brighter() ) );
        widthHandleNode.addInputEventListener( new WidthDragHandler( this, cuvette, mvt, snapInterval ) );
    }

    private void updateColor( PPath node, Color color ) {
        node.setPaint( ColorUtils.createColor( color, SOLUTION_ALPHA ) );
        node.setStrokePaint( BLLConstants.createFluidStrokeColor( color ) );
    }

    // Drag handler for manipulating the cuvette's width
    private static class WidthDragHandler extends SimSharingDragHandler {

        private final Cuvette cuvette;
        private final CuvetteNode cuvetteNode;
        private final ModelViewTransform mvt;
        private final double snapInterval;

        private double startXOffset; // x offset of mouse click from cuvette's origin
        private double startWidth; // width of the cuvette when the drag started

        public WidthDragHandler( CuvetteNode cuvetteNode, Cuvette cuvette, ModelViewTransform mvt, double snapInterval ) {
            super( UserComponents.cuvetteWidthHandle, UserComponentTypes.sprite );
            this.cuvette = cuvette;
            this.cuvetteNode = cuvetteNode;
            this.mvt = mvt;
            this.snapInterval = snapInterval;
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

        // snap to the closest value
        @Override protected void endDrag( PInputEvent event ) {
            super.endDrag( event );
            int numberOfIntervals = (int) ( ( cuvette.width.get() + ( snapInterval / 2 ) ) / snapInterval );
            cuvette.width.set( numberOfIntervals * snapInterval );
        }
    }
}
