// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import java.awt.Color;
import java.awt.Font;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Fraction;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.DynamicValueNode;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.common.view.MinusNode;
import edu.colorado.phet.linegraphing.common.view.PlusNode;
import edu.colorado.phet.linegraphing.common.view.SlopeUndefinedNode;
import edu.colorado.phet.linegraphing.common.view.UndefinedSlopeIndicator;
import edu.colorado.phet.linegraphing.common.view.spinner.SlopeSpinnerNode.RiseSpinnerNode;
import edu.colorado.phet.linegraphing.common.view.spinner.SlopeSpinnerNode.RunSpinnerNode;
import edu.colorado.phet.linegraphing.common.view.spinner.SpinnerNode;
import edu.colorado.phet.linegraphing.common.view.spinner.SpinnerStateIndicator.InterceptColors;
import edu.colorado.phet.linegraphing.common.view.spinner.SpinnerStateIndicator.SlopeColors;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Renderer for slope-intercept equations, with optional interactivity of slope and intercept.
 * General slope-intercept form is: y = mx + b
 * <p>
 * Spinners are used to increment/decrement parts of the equation that are specified as being interactive.
 * Non-interactive parts of the equation are expressed in a form that is typical of how the equation
 * would normally be written.  For example, if the slope is -1, then only the sign is written, not "-1".
 * <p>
 * Note that both m and b may be improper fractions. b may be an improper fraction only if no parts
 * of the equation are interactive.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptEquationNode extends EquationNode {

    private final Property<Double> rise, run, yInterceptNumerator, yInterceptDenominator; // internal properties that are connected to spinners
    private boolean updatingControls; // flag that allows us to update all controls atomically when the model changes

    // Nodes that appear in all possible forms of the equation "y = mx + b"
    private final PNode yNode, equalsNode, slopeMinusSignNode, riseNode, runNode, xNode, operatorNode, yInterceptNumeratorNode, yInterceptDenominatorNode;
    private final PNode yInterceptMinusSignNode; // for "y = -b" case
    private final PPath slopeFractionLineNode, yInterceptFractionLineNode;

    // Constructor for a static line.
    public SlopeInterceptEquationNode( Line line, PhetFont font, Color color ) {
        this( new Property<Line>( line ),
              new Property<DoubleRange>( new DoubleRange( 0, 1 ) ),
              new Property<DoubleRange>( new DoubleRange( 0, 1 ) ),
              new Property<DoubleRange>( new DoubleRange( 0, 1 ) ),
              false, false,
              font, font, color );
    }

    public SlopeInterceptEquationNode( final Property<Line> interactiveLine,
                                       Property<DoubleRange> riseRange,
                                       Property<DoubleRange> runRange,
                                       Property<DoubleRange> yInterceptRange,
                                       final boolean interactiveSlope,
                                       final boolean interactiveIntercept,
                                       PhetFont interactiveFont,
                                       final PhetFont staticFont,
                                       final Color staticColor ) {
        super( staticFont.getSize() );

        this.rise = new Property<Double>( interactiveLine.get().rise );
        this.run = new Property<Double>( interactiveLine.get().run );
        Fraction yIntercept = interactiveLine.get().getYIntercept();
        this.yInterceptNumerator = new Property<Double>( (double) yIntercept.numerator );
        this.yInterceptDenominator = new Property<Double>( (double) yIntercept.denominator );

        // Determine the max width of the rise and run spinners.
        double maxSlopeSpinnerWidth = computeMaxSlopeSpinnerWidth( riseRange, runRange, interactiveFont, FORMAT );

        // nodes: y = -(rise/run)x + -b
        yNode = new PhetPText( Strings.SYMBOL_Y, staticFont, staticColor );
        equalsNode = new PhetPText( "=", staticFont, staticColor );
        slopeMinusSignNode = new MinusNode( signLineSize, staticColor );
        if ( interactiveSlope ) {
            riseNode = new ZeroOffsetNode( new RiseSpinnerNode( UserComponents.riseSpinner, rise, run, riseRange, new SlopeColors(),
                                                                interactiveFont, FORMAT ) );
            runNode = new ZeroOffsetNode( new RunSpinnerNode( UserComponents.runSpinner, rise, run, runRange, new SlopeColors(),
                                                              interactiveFont, FORMAT ) );
        }
        else {
            riseNode = new DynamicValueNode( rise, FORMAT, staticFont, staticColor, true );
            runNode = new DynamicValueNode( run, FORMAT, staticFont, staticColor, true );
        }
        slopeFractionLineNode = new PPath( createFractionLineShape( maxSlopeSpinnerWidth ) ) {{
            setStroke( null );
            setPaint( staticColor );
        }};
        xNode = new PhetPText( Strings.SYMBOL_X, staticFont, staticColor );
        operatorNode = new PNode(); // parent for + or - node
        yInterceptMinusSignNode = new MinusNode( signLineSize, staticColor );
        if ( interactiveIntercept ) {
            yInterceptNumeratorNode = new ZeroOffsetNode( new SpinnerNode( UserComponents.interceptSpinner, yInterceptNumerator, yInterceptRange, new InterceptColors(), interactiveFont, FORMAT ) );
            yInterceptDenominatorNode = new PPath();
        }
        else {
            yInterceptNumeratorNode = new DynamicValueNode( yInterceptNumerator, FORMAT, staticFont, staticColor, true ); // absolute value
            yInterceptDenominatorNode = new DynamicValueNode( yInterceptDenominator, FORMAT, staticFont, staticColor, true ); // absolute value
        }
        yInterceptFractionLineNode = new PPath( createFractionLineShape( maxSlopeSpinnerWidth ) ) {{
            setStroke( null );
            setPaint( staticColor );
        }};

        // sync the model with the controls
        RichSimpleObserver lineUpdater = new RichSimpleObserver() {
            @Override public void update() {
                if ( !updatingControls ) {
                    interactiveLine.set( Line.createSlopeIntercept( rise.get(), run.get(), yInterceptNumerator.get(), interactiveLine.get().color ) );
                }
            }
        };
        lineUpdater.observe( rise, run, yInterceptNumerator );

        // sync the controls and layout with the model
        interactiveLine.addObserver( new VoidFunction1<Line>() {

            public void apply( Line line ) {
                // If intercept is interactive, then (x1,y1) must be on a grid line on the y intercept.
                assert ( !interactiveIntercept || ( line.x1 == 0 && MathUtil.isInteger( line.y1 ) ) );

                // Synchronize the controls atomically.
                updatingControls = true;
                {
                    rise.set( interactiveSlope ? line.rise : line.getSimplifiedRise() );
                    run.set( interactiveSlope ? line.run : line.getSimplifiedRun() );
                    Fraction yIntercept = interactiveLine.get().getYIntercept();
                    yInterceptNumerator.set( (double) yIntercept.numerator );
                    yInterceptDenominator.set( (double) yIntercept.denominator );
                }
                updatingControls = false;

                // Update the layout.
                updateLayout( line, interactiveSlope, interactiveIntercept, staticFont, staticColor );
            }
        } );
    }

    /*
     * Updates the layout to match the desired form of the equation.
     * This is based on which parts of the equation are interactive, and what the
     * non-interactive parts of the equation should look like when written in simplified form.
     */
    private void updateLayout( Line line, boolean interactiveSlope, boolean interactiveIntercept, PhetFont staticFont, Color staticColor ) {

        // Start by removing all nodes, then we'll selectively add nodes based on the desired form of the equation.
        removeAllChildren();
        operatorNode.removeAllChildren();
        if ( line.undefinedSlope() && !interactiveSlope && !interactiveIntercept ) {
            // slope is undefined and nothing is interactive
            addChild( new SlopeUndefinedNode( line, staticFont, staticColor ) );
            return;
        }

        // y-intercept
        final int yInterceptNumerator = line.getYIntercept().numerator;
        final int yInterceptDenominator = line.getYIntercept().denominator;

        // slope properties
        final double slope = line.getSlope();
        final boolean zeroSlope = ( slope == 0 );
        final boolean unitySlope = ( Math.abs( slope ) == 1 );
        final boolean integerSlope = MathUtil.isInteger( slope );
        final boolean positiveSlope = ( slope > 0 );
        final boolean fractionalSlope = ( !zeroSlope && !unitySlope && !integerSlope );
        final boolean zeroYIntercept = ( (double) yInterceptNumerator / (double) yInterceptDenominator ) == 0;
        final boolean positiveYIntercept = ( (double) yInterceptNumerator / (double) yInterceptDenominator ) > 0;

        // y =
        addChild( yNode );
        addChild( equalsNode );
        yNode.setOffset( 0, 0 );
        equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                              yNode.getYOffset() );

        // Layout the "mx" part of the equation.
        if ( interactiveSlope ) {
            // (rise/run)x
            addChild( riseNode );
            addChild( slopeFractionLineNode );
            addChild( runNode );
            addChild( xNode );
            slopeFractionLineNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                        equalsNode.getFullBoundsReference().getCenterY() + fractionLineYFudgeFactor );
            riseNode.setOffset( slopeFractionLineNode.getFullBoundsReference().getCenterX() - ( riseNode.getFullBoundsReference().getWidth() / 2 ),
                                slopeFractionLineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - spinnersYSpacing );
            runNode.setOffset( slopeFractionLineNode.getFullBoundsReference().getCenterX() - ( runNode.getFullBoundsReference().getWidth() / 2 ),
                               slopeFractionLineNode.getFullBoundsReference().getMinY() + spinnersYSpacing );
            xNode.setOffset( slopeFractionLineNode.getFullBoundsReference().getMaxX() + fractionalSlopeXSpacing, yNode.getYOffset() );
        }
        else {
            // decide whether to include the slope minus sign
            PNode previousNode;
            double previousXOffset;
            if ( positiveSlope || zeroSlope ) {
                // no sign
                previousNode = equalsNode;
                previousXOffset = relationalOperatorXSpacing;
            }
            else {
                // -
                addChild( slopeMinusSignNode );
                slopeMinusSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                              equalsNode.getFullBoundsReference().getCenterY() - ( slopeMinusSignNode.getFullBoundsReference().getHeight() / 2 ) + slopeSignYFudgeFactor + slopeSignYOffset );
                previousNode = slopeMinusSignNode;
                previousXOffset = ( fractionalSlope ? fractionSignXSpacing : integerSignXSpacing );
            }

            if ( line.undefinedSlope() || fractionalSlope ) {
                // rise/run x
                addChild( riseNode );
                addChild( slopeFractionLineNode );
                addChild( runNode );
                addChild( xNode );
                // adjust fraction line width
                double lineWidth = Math.max( riseNode.getFullBoundsReference().getWidth(), runNode.getFullBoundsReference().getWidth() );
                slopeFractionLineNode.setPathTo( createFractionLineShape( lineWidth ) );
                // layout
                slopeFractionLineNode.setOffset( previousNode.getFullBoundsReference().getMaxX() + previousXOffset,
                                            equalsNode.getFullBoundsReference().getCenterY() + fractionLineYFudgeFactor );
                riseNode.setOffset( slopeFractionLineNode.getFullBoundsReference().getCenterX() - ( riseNode.getFullBoundsReference().getWidth() / 2 ),
                                    slopeFractionLineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - ySpacing );
                runNode.setOffset( slopeFractionLineNode.getFullBoundsReference().getCenterX() - ( runNode.getFullBoundsReference().getWidth() / 2 ),
                                   slopeFractionLineNode.getFullBoundsReference().getMinY() + ySpacing );
                xNode.setOffset( slopeFractionLineNode.getFullBoundsReference().getMaxX() + fractionalSlopeXSpacing, yNode.getYOffset() );
            }
            else if ( zeroSlope ) {
                // no x term
            }
            else if ( unitySlope ) {
                // x
                addChild( xNode );
                xNode.setOffset( previousNode.getFullBoundsReference().getMaxX() + previousXOffset, yNode.getYOffset() );
            }
            else if ( integerSlope ) {
                // Nx
                addChild( riseNode );
                addChild( xNode );
                riseNode.setOffset( previousNode.getFullBoundsReference().getMaxX() + previousXOffset, yNode.getYOffset() );
                xNode.setOffset( riseNode.getFullBoundsReference().getMaxX() + integerSlopeXSpacing, yNode.getYOffset() );
            }
            else {
                throw new IllegalStateException( "programming error, didn't handle some slope case" );
            }
        }

        // Layout the "+ b" part of the equation.
        if ( interactiveIntercept ) {
            if ( zeroSlope && !interactiveSlope ) {
                // y = b
                addChild( yInterceptNumeratorNode );
                yInterceptNumeratorNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                          yNode.getFullBoundsReference().getCenterY() - ( yInterceptNumeratorNode.getFullBoundsReference().getHeight() / 2 ) );
            }
            else {
                // y = (rise/run)x + b
                addChild( operatorNode );
                addChild( yInterceptNumeratorNode );
                operatorNode.addChild( new PlusNode( operatorLineSize, staticColor ) );
                operatorNode.setOffset( xNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                                        equalsNode.getFullBoundsReference().getCenterY() - ( operatorNode.getFullBoundsReference().getHeight() / 2 ) + operatorYFudgeFactor );
                yInterceptNumeratorNode.setOffset( operatorNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                                          yNode.getFullBoundsReference().getCenterY() - ( yInterceptNumeratorNode.getFullBoundsReference().getHeight() / 2 ) );
            }
        }
        else {
            if ( zeroYIntercept ) {
                if ( zeroSlope && !interactiveSlope ) {
                    // y = 0
                    addChild( yInterceptNumeratorNode );
                    yInterceptNumeratorNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                                       yNode.getFullBoundsReference().getCenterY() - ( yInterceptNumeratorNode.getFullBoundsReference().getHeight() / 2 ) );
                }
                else {
                    // no intercept
                }
            }
            else if ( positiveYIntercept && zeroSlope && !interactiveSlope ) {
                // y = b
                addChild( yInterceptNumeratorNode );
                yInterceptNumeratorNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                                   yNode.getFullBoundsReference().getCenterY() - ( yInterceptNumeratorNode.getFullBoundsReference().getHeight() / 2 ) );
            }
            else if ( !positiveYIntercept && zeroSlope && !interactiveSlope ) {
                // y = -b
                addChild( yInterceptMinusSignNode );
                addChild( yInterceptNumeratorNode );
                yInterceptMinusSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                                   equalsNode.getFullBoundsReference().getCenterY() - ( yInterceptMinusSignNode.getFullBoundsReference().getHeight() / 2 ) + operatorYFudgeFactor );
                yInterceptNumeratorNode.setOffset( yInterceptMinusSignNode.getFullBoundsReference().getMaxX() + integerSignXSpacing,
                                                   yNode.getFullBoundsReference().getCenterY() - ( yInterceptNumeratorNode.getFullBoundsReference().getHeight() / 2 ) );
            }
            else {
                // y = mx +/- b
                addChild( operatorNode );
                operatorNode.addChild( positiveYIntercept ? new PlusNode( operatorLineSize, staticColor ) : new MinusNode( operatorLineSize, staticColor ) );
                operatorNode.setOffset( xNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                                        equalsNode.getFullBoundsReference().getCenterY() - ( operatorNode.getFullBoundsReference().getHeight() / 2 ) + operatorYFudgeFactor );

                if ( yInterceptDenominator == 1 ) {
                    // b is an integer
                    addChild( yInterceptNumeratorNode );
                    yInterceptNumeratorNode.setOffset( operatorNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                                                       yNode.getFullBoundsReference().getCenterY() - ( yInterceptNumeratorNode.getFullBoundsReference().getHeight() / 2 ) );
                }
                else {
                    // b is an improper fraction
                    addChild( yInterceptNumeratorNode );
                    addChild( yInterceptFractionLineNode );
                    addChild( yInterceptDenominatorNode );
                    // adjust fraction line width
                    double lineWidth = Math.max( yInterceptNumeratorNode.getFullBoundsReference().getWidth(), yInterceptDenominatorNode.getFullBoundsReference().getWidth() );
                    yInterceptFractionLineNode.setPathTo( createFractionLineShape( lineWidth ) );
                    // layout
                    yInterceptFractionLineNode.setOffset( operatorNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                                                          equalsNode.getFullBoundsReference().getCenterY() + fractionLineYFudgeFactor );
                    yInterceptNumeratorNode.setOffset( yInterceptFractionLineNode.getFullBoundsReference().getCenterX() - ( yInterceptNumeratorNode.getFullBoundsReference().getWidth() / 2 ),
                                                       yInterceptFractionLineNode.getFullBoundsReference().getMinY() - yInterceptNumeratorNode.getFullBoundsReference().getHeight() - ySpacing );
                    yInterceptDenominatorNode.setOffset( yInterceptFractionLineNode.getFullBoundsReference().getCenterX() - ( yInterceptDenominatorNode.getFullBoundsReference().getWidth() / 2 ),
                                                         yInterceptFractionLineNode.getFullBoundsReference().getMaxY() + ySpacing );
                }
            }
        }

        // Add the undefined-slope indicator after layout has been done, so that it covers the entire equation.
        if ( line.undefinedSlope() ) {
            PNode undefinedSlopeIndicator = new UndefinedSlopeIndicator( getFullBoundsReference().getWidth(), getFullBoundsReference().getHeight() );
            undefinedSlopeIndicator.setOffset( 0, slopeFractionLineNode.getFullBoundsReference().getCenterY() - ( undefinedSlopeIndicator.getFullBoundsReference().getHeight() / 2 ) + undefinedSlopeYFudgeFactor );
            addChild( undefinedSlopeIndicator );
        }
    }

    // Creates a node that displays the general form of this equation.
    public static PNode createGeneralFormNode() {
        String html = MessageFormat.format( "{0} = {1}{2} + {3}", /* y = mx + b */
                                            Strings.SYMBOL_Y, Strings.SYMBOL_SLOPE, Strings.SYMBOL_X, Strings.SYMBOL_INTERCEPT );
        return new HTMLNode( html, LGColors.INTERACTIVE_LINE, new PhetFont( Font.BOLD, 18 ) );
    }
}
