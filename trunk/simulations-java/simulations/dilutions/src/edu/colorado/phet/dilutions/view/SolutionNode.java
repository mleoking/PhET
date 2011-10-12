// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.dilutions.DilutionsColors;
import edu.colorado.phet.dilutions.model.Solution;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Soution shown in the beaker in the "Molarity" tab.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SolutionNode extends PPath {

    private final PDimension beakerSize;
    private final Solution solution;
    private final LinearFunction volumeFunction;

    public SolutionNode( PDimension beakerSize, Solution solution, DoubleRange volumeRange ) {

        // this node is not interactive
        setPickable( false );
        setChildrenPickable( false );

        setStroke( null );

        this.beakerSize = beakerSize;
        this.solution = solution;
        this.volumeFunction = new LinearFunction( volumeRange.getMin(), volumeRange.getMax(),
                                                  ( volumeRange.getMin() / volumeRange.getMax() ) * beakerSize.getHeight(), beakerSize.getHeight() );

        SimpleObserver observer = new SimpleObserver() {
            public void update() {
                updateNode();
            }
        };
        solution.addConcentrationObserver( observer );
        solution.addPrecipitateAmountObserver( observer );
        solution.solute.addObserver( observer );
        solution.volume.addObserver( observer );
    }

    private void updateNode() {

        // update the color of the solution, accounting for saturation
        LinearFunction f = new LinearFunction( 0, solution.getSaturatedConcentration(), 0, 1 );
        double colorScale = f.evaluate( solution.getConcentration() );
        setPaint( ColorUtils.interpolateRBGA( DilutionsColors.WATER_COLOR, solution.solute.get().solutionColor, colorScale ) );

        // update amount of stuff in the beaker, based on solution volume
        double height = volumeFunction.evaluate( solution.volume.get() );
        setPathTo( new Rectangle2D.Double( 0, beakerSize.getHeight() - height, beakerSize.getWidth(), height ) );
    }
}
