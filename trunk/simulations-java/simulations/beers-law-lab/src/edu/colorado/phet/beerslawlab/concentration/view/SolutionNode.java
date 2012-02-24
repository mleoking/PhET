// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing;
import edu.colorado.phet.beerslawlab.concentration.model.Beaker;
import edu.colorado.phet.beerslawlab.concentration.model.ConcentrationSolution;
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.simsharing.NonInteractiveEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Solution that appears in the beaker.
 * Origin is at bottom center of beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SolutionNode extends PPath {

    private static final double MIN_NONZERO_HEIGHT = 5; // minimum height for a solution with non-zero volume, set by visual inspection

    private final ConcentrationSolution solution;
    private final Beaker beaker;
    private final LinearFunction volumeToHeightFunction;

    public SolutionNode( ConcentrationSolution solution, Beaker beaker ) {
        setStroke( BLLConstants.FLUID_STROKE );

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

        addInputEventListener( new NonInteractiveEventHandler( BLLSimSharing.UserComponents.solution ) );
    }

    private void updateNode() {

        // update the color of the solution, accounting for saturation
        Color solutionColor = solution.fluidColor.get();
        setPaint( solutionColor );
        setStrokePaint( BLLConstants.createFluidStrokeColor( solutionColor ) );

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
