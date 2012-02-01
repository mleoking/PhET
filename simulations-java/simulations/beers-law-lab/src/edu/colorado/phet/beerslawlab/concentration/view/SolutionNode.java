// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

import java.awt.BasicStroke;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.model.Beaker;
import edu.colorado.phet.beerslawlab.model.Solution;
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Solution that appears in the beaker.
 * Origin is at bottom center of beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SolutionNode extends PPath {

    private static final double MIN_NONZERO_HEIGHT = 5; // minimum height for a solution with non-zero volume, set by visual inspection

    private final Solution solution;
    private final Beaker beaker;
    private final LinearFunction volumeToHeightFunction;

    public SolutionNode( Solution solution, Beaker beaker ) {
        setPickable( false );
        setChildrenPickable( false );
        setStroke( new BasicStroke( 0.25f ) );

        this.solution = solution;
        this.beaker = beaker;
        this.volumeToHeightFunction = new LinearFunction( 0, beaker.getVolume(), 0, beaker.getHeight() );

        SimpleObserver observer = new SimpleObserver() {
            public void update() {
                updateNode();
            }
        };
        solution.addFluidColorObserver( observer );
        solution.volume.addObserver( observer );
    }

    private void updateNode() {

        // update the color of the solution, accounting for saturation
        setPaint( solution.getFluidColor() );
        setStrokePaint( solution.getFluidColor().darker().darker() );

        // update amount of stuff in the beaker, based on solution volume
        final double volume = solution.volume.get();
        double height = volumeToHeightFunction.evaluate( volume );
        if ( volume > 0 && height < MIN_NONZERO_HEIGHT ) {
            // constrain non-zero volume to minimum height, so that the solution is visible to the user and detectable by the concentration probe
            height = MIN_NONZERO_HEIGHT;
        }
        setPathTo( new Rectangle2D.Double( -beaker.getWidth() / 2, -height, beaker.getWidth(), height ) );
    }
}
