// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.BasicStroke;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.beerslaw.model.Cuvette;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.common.model.Solution;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.simsharing.NonInteractiveEventHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

//TODO add handle for manipulating width
/**
 * Visual representation of the cuvette.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class CuvetteNode extends PNode {

    private static final double PERCENT_FULL = 0.85;

    public CuvetteNode( final Cuvette cuvette, final Solution solution, final ModelViewTransform mvt ) {

        final PPath cuvetteNode = new PPath() {{
            setStroke( new BasicStroke( 2f ) );
            setPaint( null );
        }};
        final PPath solutionNode = new PPath() {{
            setStroke( new BasicStroke( 0.25f ) );
        }};

        addChild( solutionNode );
        addChild( cuvetteNode );

        cuvette.width.addObserver( new SimpleObserver() {
            public void update() {
                double width = mvt.modelToViewDeltaX( cuvette.width.get() );
                double height = mvt.modelToViewDeltaY( cuvette.height );
                cuvetteNode.setPathTo( new Rectangle2D.Double( 0, 0, width, height ) );
                solutionNode.setPathTo( new Rectangle2D.Double( 0, 0, width, PERCENT_FULL * height ) );
                solutionNode.setOffset( cuvetteNode.getXOffset(),
                                        cuvetteNode.getFullBoundsReference().getMaxY() - solutionNode.getFullBoundsReference().getHeight() );
            }
        } );

        solution.addFluidColorObserver( new SimpleObserver() {
            public void update() {
                solutionNode.setPaint( solution.getFluidColor() );
                solutionNode.setStrokePaint( solution.getFluidColor().darker().darker() );
            }
        });

        setOffset( mvt.modelToView( cuvette.location.toPoint2D() ) );

        addInputEventListener( new NonInteractiveEventHandler( UserComponents.cuvette ) );
    }
}
