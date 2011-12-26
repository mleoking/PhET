// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

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
public class SolutionNode extends PPath {

    private final Solution solution;
    private final Beaker beaker;
    private final LinearFunction volumeToHeightFunction;

    public SolutionNode( Solution solution, Beaker beaker ) {

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

        // update amount of stuff in the beaker, based on solution volume
        double height = volumeToHeightFunction.evaluate( solution.volume.get() );
        setPathTo( new Rectangle2D.Double( -beaker.getWidth() / 2, -height, beaker.getWidth(), height ) );
    }
}
