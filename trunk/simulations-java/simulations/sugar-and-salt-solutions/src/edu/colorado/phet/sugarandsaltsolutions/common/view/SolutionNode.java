// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Solution;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo graphic that shows the solution (water + dissolved solutes) in the beaker.  It may be displaced upward by solid precipitate.
 *
 * @author Sam Reid
 */
public class SolutionNode extends PNode {
    public SolutionNode( final ModelViewTransform transform, final Solution solution, Color color ) {
        addChild( new PhetPPath( color ) {{
            solution.shape.addObserver( new VoidFunction1<Shape>() {
                public void apply( Shape shape ) {
                    setPathTo( transform.modelToView( shape ) );
                }
            } );
        }} );
    }
}
