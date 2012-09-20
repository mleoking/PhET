// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Dimension;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.view.SlopeSpinnerNode.RiseSpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SlopeSpinnerNode.RunSpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SpinnerStateIndicator.SlopeColors;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class for all interactive equations.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class InteractiveEquationNode extends PhetPNode {

    //TODO this should be a percentage of the font size
    /*
    * This controls the vertical offset of the slope's sign.
    * Zero is vertically centered on the equals sign, positive values move it down, negative move it up.
    * This was created because there was a great deal of discussion and disagreement about where the sign should be placed.
    */
    protected static final int SLOPE_SIGN_Y_OFFSET = 0;

    //TODO these should be percentages of the font size
    // fudge factors for horizontal lines, to vertically center them with equals sign (set by visual inspection)
    protected static final int SLOPE_SIGN_Y_FUDGE_FACTOR = 2;
    protected static final int OPERATOR_Y_FUDGE_FACTOR = 2;
    protected static final int FRACTION_LINE_Y_FUDGE_FACTOR = 2;
    protected static final int UNDEFINED_SLOPE_Y_FUDGE_FACTOR = 2;

    // size of the lines used to create + and - operators
    protected static final PDimension OPERATOR_LINE_SIZE = new PDimension( 15, 2 );

    // size of the lines used to create + and - signs
    protected static final PDimension SIGN_LINE_SIZE = new PDimension( 15, 3 );

    // thickness of the fraction divisor line
    protected static final int FRACTION_LINE_THICKNESS = 2;

    protected static final NumberFormat FORMAT = new DefaultDecimalFormat( "0" );

    // Gets the max width for the rise and run spinners used in an interactive equation.
    protected static double computeMaxSlopeSpinnerWidth( Property<DoubleRange> riseRange, Property<DoubleRange> runRange, PhetFont font, NumberFormat format ) {

        // Create prototypical spinners.
        PNode maxRiseNode = new RiseSpinnerNode( UserComponents.riseSpinner,
                                                 new Property<Double>( riseRange.get().getMax() ), new Property<Double>( runRange.get().getMax() ), riseRange,
                                                 new SlopeColors(), font, format );
        PNode minRiseNode = new RiseSpinnerNode( UserComponents.riseSpinner,
                                                 new Property<Double>( riseRange.get().getMin() ), new Property<Double>( runRange.get().getMax() ), riseRange,
                                                 new SlopeColors(), font, format );

        PNode maxRunNode = new RunSpinnerNode( UserComponents.riseSpinner,
                                               new Property<Double>( riseRange.get().getMin() ), new Property<Double>( runRange.get().getMax() ), runRange,
                                               new SlopeColors(), font, format );
        PNode minRunNode = new RunSpinnerNode( UserComponents.riseSpinner,
                                               new Property<Double>( riseRange.get().getMin() ), new Property<Double>( runRange.get().getMin() ), runRange,
                                               new SlopeColors(), font, format );

        // Compute the max
        double maxRiseWidth = Math.max( maxRiseNode.getFullBoundsReference().getWidth(), minRiseNode.getFullBoundsReference().getWidth() );
        double maxRunWidth = Math.max( maxRunNode.getFullBoundsReference().getWidth(), minRunNode.getFullBoundsReference().getWidth() );
        return Math.max( maxRiseWidth, maxRunWidth );
    }
}
