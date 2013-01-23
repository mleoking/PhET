// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.view.spinner.SlopeSpinnerNode.RiseSpinnerNode;
import edu.colorado.phet.linegraphing.common.view.spinner.SlopeSpinnerNode.RunSpinnerNode;
import edu.colorado.phet.linegraphing.common.view.spinner.SpinnerStateIndicator.SlopeColors;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class for all equations.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class EquationNode extends PhetPNode {

    protected static final NumberFormat FORMAT = new DefaultDecimalFormat( "0" );

    /*
    * This controls the vertical offset of the slope's sign.
    * Zero is vertically centered on the equals sign, positive values move it down, negative move it up.
    * This was created because there was a great deal of discussion and disagreement about where the sign should be placed.
    */
    protected final double slopeSignYOffset;

    // fudge factors for horizontal lines, to vertically center them with equals sign (set by visual inspection)
    protected final double slopeSignYFudgeFactor;
    protected final double operatorYFudgeFactor;
    protected final double fractionLineYFudgeFactor;
    protected final double undefinedSlopeYFudgeFactor;

    private final float fractionLineThickness; // thickness of the fraction divisor line
    protected final PDimension operatorLineSize; // size of the lines used to create + and - operators
    protected final PDimension signLineSize; // size of the lines used to create + and - signs

    // spacing between components of an equation (set by visual inspection)
    protected final double integerSignXSpacing; // spacing between a sign and the integer to the right of it
    protected final double fractionSignXSpacing; // spacing between a sign and the fraction to the right of it
    protected final double integerSlopeXSpacing, fractionalSlopeXSpacing; // spacing between the slope and what's to the right of it
    protected final double operatorXSpacing; // space around an operator (eg, +)
    protected final double relationalOperatorXSpacing; // space around the relational operator (eg, =)
    protected final double parenXSpacing; // space between a parenthesis and the thing it encloses
    protected final double spinnersYSpacing; // y spacing between spinners and fraction line
    protected final double slopeYSpacing; // y spacing between rise and run values (with blue backgrounds) and fraction line
    protected final double ySpacing; // all other y spacing

    /**
     * Constructor.
     *
     * @param pointSize point size of the font used to render the equation.
     */
    protected EquationNode( int pointSize ) {

        /*
         * Compute dimensions and layout offsets as percentages of the font's point size.
         * These multipliers were determined by laborious visual inspection.
         */
        slopeSignYOffset = 0;
        slopeSignYFudgeFactor = 0.07 * pointSize;
        operatorYFudgeFactor = 0.07 * pointSize;
        fractionLineYFudgeFactor = 0.07 * pointSize;
        undefinedSlopeYFudgeFactor = 0.07 * pointSize;
        fractionLineThickness = 0.06f * pointSize;
        operatorLineSize = new PDimension( 0.54 * pointSize, 0.07 * pointSize );
        signLineSize = new PDimension( 0.54 * pointSize, 0.11 * pointSize );
        integerSignXSpacing = 0.18 * pointSize;
        fractionSignXSpacing = 0.36 * pointSize;
        integerSlopeXSpacing = 0.04 * pointSize;
        fractionalSlopeXSpacing = 0.15 * pointSize;
        operatorXSpacing = 0.25 * pointSize;
        relationalOperatorXSpacing = 0.35 * pointSize;
        parenXSpacing = 0.07 * pointSize;
        ySpacing = 0.1 * pointSize;
        spinnersYSpacing = 0.2 * pointSize;
        slopeYSpacing = 0.4 * pointSize;
    }

    // Creates the shape for the fraction division line.
    protected Shape createFractionLineShape( double length ) {
        return new Rectangle2D.Double( 0, 0, length, fractionLineThickness );
    }

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
