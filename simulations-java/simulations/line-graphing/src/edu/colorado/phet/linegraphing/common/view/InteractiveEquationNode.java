// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.view.SlopeSpinnerNode.RiseSpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SlopeSpinnerNode.RunSpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SpinnerStateIndicator.SlopeColors;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for all interactive equations.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class InteractiveEquationNode extends PhetPNode {

    /*
     * Gets the max width for the rise and run UI-components of the equation.
     * This is dependent on the extend of the rise and run ranges, and whether the slope is interactive.
     * The type of UI-components varies depending on whether the slope is interactive.
     */
    protected static double computeSlopeComponentMaxWidth( Property<DoubleRange> riseRange, Property<DoubleRange> runRange, PhetFont font, NumberFormat format, boolean interactiveSlope ) {

        PNode minRiseNode, maxRiseNode, minRunNode, maxRunNode;

        // Create prototypical nodes.
        if ( interactiveSlope ) {
            // slope is interactive, so we'll be using spinners
            maxRiseNode = new RiseSpinnerNode( UserComponents.riseSpinner,
                                               new Property<Double>( riseRange.get().getMax() ), new Property<Double>( runRange.get().getMax() ), riseRange,
                                               new SlopeColors(), font, format );
            minRiseNode = new RiseSpinnerNode( UserComponents.riseSpinner,
                                               new Property<Double>( riseRange.get().getMin() ), new Property<Double>( runRange.get().getMax() ), riseRange,
                                               new SlopeColors(), font, format );

            maxRunNode = new RunSpinnerNode( UserComponents.riseSpinner,
                                             new Property<Double>( riseRange.get().getMin() ), new Property<Double>( runRange.get().getMax() ), runRange,
                                             new SlopeColors(), font, format );
            minRunNode = new RunSpinnerNode( UserComponents.riseSpinner,
                                             new Property<Double>( riseRange.get().getMin() ), new Property<Double>( runRange.get().getMin() ), runRange,
                                             new SlopeColors(), font, format );

        }
        else {
            // slope is not interactive, so we'll be using a read-only display
            minRiseNode = new DynamicValueNode( new Property<Double>( riseRange.get().getMin() ), font, Color.BLACK );
            maxRiseNode = new DynamicValueNode( new Property<Double>( riseRange.get().getMax() ), font, Color.BLACK );
            minRunNode = new DynamicValueNode( new Property<Double>( runRange.get().getMin() ), font, Color.BLACK );
            maxRunNode = new DynamicValueNode( new Property<Double>( runRange.get().getMax() ), font, Color.BLACK );
        }

        // Compute the max
        double maxRiseWidth = Math.max( maxRiseNode.getFullBoundsReference().getWidth(), minRiseNode.getFullBoundsReference().getWidth() );
        double maxRunWidth = Math.max( maxRunNode.getFullBoundsReference().getWidth(), minRunNode.getFullBoundsReference().getWidth() );
        return Math.max( maxRiseWidth, maxRunWidth );
    }
}
