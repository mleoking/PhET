// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

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
import edu.colorado.phet.linegraphing.common.view.spinner.SpinnerStateIndicator.SlopeColors;
import edu.colorado.phet.linegraphing.common.view.spinner.SpinnerStateIndicator.X1Y1Colors;
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptEquationNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Renderer for point-slope equations, with optional interactivity of point and slope.
 * General point-slope form is: (y - y1) = m(x - x1)
 * <p/>
 * Spinners are used to increment/decrement parts of the equation that are specified as being interactive.
 * Non-interactive parts of the equation are expressed in a form that is typical of how the equation
 * would normally be written. For example, if the slope is -1, then only the sign is written, not "-1".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeEquationNode extends EquationNode {

    private final Property<Double> x1, y1, rise, run; // internal properties that are connected to spinners
    private boolean updatingControls; // flag that allows us to update all controls atomically when the model changes

    // Nodes that appear in all possible forms of the equation "(y - y1) = m(x - x1)"
    private final PNode yLeftParenNode, yNode, yOperatorNode, y1Node, yRightParenNode, equalsNode;
    private final PNode slopeMinusSignNode, riseNode, runNode, xLeftParenNode, xNode, xOperatorNode, x1Node, xRightParenNode;
    private final PNode y1MinusSignNode; // for "y = -y1" case
    private final PPath fractionLineNode;

    // Constructor for a static line.
    public PointSlopeEquationNode( Line line, PhetFont font, Color color ) {
        this( new Property<Line>( line ),
              new Property<DoubleRange>( new DoubleRange( 0, 1 ) ),
              new Property<DoubleRange>( new DoubleRange( 0, 1 ) ),
              new Property<DoubleRange>( new DoubleRange( 0, 1 ) ),
              new Property<DoubleRange>( new DoubleRange( 0, 1 ) ),
              false, false, false,
              font, font, color );
    }

    public PointSlopeEquationNode( final Property<Line> interactiveLine,
                                   Property<DoubleRange> x1Range,
                                   Property<DoubleRange> y1Range,
                                   Property<DoubleRange> riseRange,
                                   Property<DoubleRange> runRange,
                                   final boolean interactiveX1,
                                   final boolean interactiveY1,
                                   final boolean interactiveSlope,
                                   PhetFont interactiveFont,
                                   final PhetFont staticFont,
                                   final Color staticColor ) {
        super( staticFont.getSize() );

        this.x1 = new Property<Double>( interactiveLine.get().x1 );
        this.y1 = new Property<Double>( interactiveLine.get().y1 );
        this.rise = new Property<Double>( interactiveLine.get().rise );
        this.run = new Property<Double>( interactiveLine.get().run );

        // Determine the max width of the rise and run spinners.
        double maxSlopeSpinnerWidth = computeMaxSlopeSpinnerWidth( riseRange, runRange, interactiveFont, FORMAT );

        // nodes: (y-y1) = m(x-x1)
        yLeftParenNode = new PhetPText( "(", staticFont, staticColor );
        yNode = new PhetPText( Strings.SYMBOL_Y, staticFont, staticColor );
        yOperatorNode = new PNode(); // parent for + or - node
        if ( interactiveY1 ) {
            y1Node = new ZeroOffsetNode( new SpinnerNode( UserComponents.y1Spinner, y1, y1Range, new X1Y1Colors(), interactiveFont, FORMAT ) );
        }
        else {
            y1Node = new DynamicValueNode( y1, FORMAT, staticFont, staticColor, true ); // displayed as absolute value
        }
        yRightParenNode = new PhetPText( ")", staticFont, staticColor );
        y1MinusSignNode = new MinusNode( signLineSize, staticColor ); // for y=-y1 case
        equalsNode = new PhetPText( "=", staticFont, staticColor );
        slopeMinusSignNode = new MinusNode( signLineSize, staticColor );
        if ( interactiveSlope ) {
            riseNode = new ZeroOffsetNode( new RiseSpinnerNode( UserComponents.riseSpinner, rise, run, riseRange, new SlopeColors(), interactiveFont, FORMAT ) );
            runNode = new ZeroOffsetNode( new RunSpinnerNode( UserComponents.runSpinner, rise, run, runRange, new SlopeColors(), interactiveFont, FORMAT ) );
        }
        else {
            riseNode = new DynamicValueNode( rise, FORMAT, staticFont, staticColor, true ); // displayed as absolute value
            runNode = new DynamicValueNode( run, FORMAT, staticFont, staticColor, true ); // displayed as absolute value
        }
        fractionLineNode = new PPath( createFractionLineShape( maxSlopeSpinnerWidth ) ) {{
            setStroke( null );
            setPaint( staticColor );
        }};
        xLeftParenNode = new PhetPText( "(", staticFont, staticColor );
        xNode = new PhetPText( Strings.SYMBOL_X, staticFont, staticColor );
        xOperatorNode = new PNode(); // parent for + or - node
        if ( interactiveX1 ) {
            x1Node = new ZeroOffsetNode( new SpinnerNode( UserComponents.x1Spinner, x1, x1Range, new X1Y1Colors(), interactiveFont, FORMAT ) );
        }
        else {
            x1Node = new DynamicValueNode( x1, FORMAT, staticFont, staticColor, true ); // displayed as absolute value
        }
        xRightParenNode = new PhetPText( ")", staticFont, staticColor );

        // sync the model with the controls
        RichSimpleObserver lineUpdater = new RichSimpleObserver() {
            @Override public void update() {
                if ( !updatingControls ) {
                    interactiveLine.set( Line.createPointSlope( x1.get(), y1.get(), rise.get(), run.get(), interactiveLine.get().color ) );
                }
            }
        };
        lineUpdater.observe( rise, run, x1, y1 );

        // sync the controls and layout with the model
        interactiveLine.addObserver( new VoidFunction1<Line>() {

            public void apply( Line line ) {

                // Synchronize the controls atomically.
                updatingControls = true;
                {
                    x1.set( line.x1 );
                    y1.set( line.y1 );
                    rise.set( interactiveSlope ? line.rise : line.getSimplifiedRise() );
                    run.set( interactiveSlope ? line.run : line.getSimplifiedRun() );
                }
                updatingControls = false;

                // Update the layout
                updateLayout( line, interactiveX1, interactiveY1, interactiveSlope, staticFont, staticColor );
            }
        } );
    }

    /*
     * Updates the layout to match the desired form of the equation.
     * This is based on which parts of the equation are interactive, and what the
     * non-interactive parts of the equation should look like when written in simplified form.
     */
    private void updateLayout( Line line, boolean interactiveX1, boolean interactiveY1, boolean interactiveSlope, PhetFont staticFont, Color staticColor ) {

        final boolean interactive = interactiveX1 || interactiveY1 || interactiveSlope;

        // Start by removing all nodes, then we'll selectively add nodes based on the desired form of the equation.
        removeAllChildren();
        xOperatorNode.removeAllChildren();
        yOperatorNode.removeAllChildren();
        if ( line.undefinedSlope() && !interactive ) {
            // slope is undefined and nothing is interactive
            addChild( new SlopeUndefinedNode( line, staticFont, staticColor ) );
            return;
        }
        else if ( ( line.same( Line.Y_EQUALS_X_LINE ) || line.same( Line.Y_EQUALS_NEGATIVE_X_LINE ) ) && !interactive ) {
            // use slope-intercept form for y=x and y=-x, using a line with the proper slope and (x1,y1)=(0,0)
            addChild( new SlopeInterceptEquationNode( Line.createSlopeIntercept( line.rise, line.run, 0, line.color ), staticFont, staticColor ) );
            return;
        }

        // Change the operators to account for the signs of x1 and y1.
        {
            if ( interactiveX1 || line.x1 >= 0 ) {
                xOperatorNode.addChild( new MinusNode( operatorLineSize, staticColor ) );
            }
            else {
                xOperatorNode.addChild( new PlusNode( operatorLineSize, staticColor ) );
            }

            if ( interactiveY1 || line.y1 >= 0 ) {
                yOperatorNode.addChild( new MinusNode( operatorLineSize, staticColor ) );
            }
            else {
                yOperatorNode.addChild( new PlusNode( operatorLineSize, staticColor ) );
            }
        }

        if ( line.rise == 0 && !interactiveSlope && !interactiveX1 ) {
            // y1 is on the right side of the equation
            addChild( yNode );
            addChild( equalsNode );
            addChild( y1Node );
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing, yNode.getYOffset() );
            if ( interactiveY1 || line.y1 >= 0 ) {
                // y = y1
                y1Node.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                  yNode.getFullBoundsReference().getCenterY() - ( y1Node.getFullBoundsReference().getHeight() / 2 ) );
            }
            else {
                // y = -y1
                addChild( y1MinusSignNode );
                y1MinusSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                           equalsNode.getFullBoundsReference().getCenterY() - ( y1MinusSignNode.getFullBoundsReference().getHeight() / 2 ) + operatorYFudgeFactor );
                y1Node.setOffset( y1MinusSignNode.getFullBoundsReference().getMaxX() + integerSignXSpacing,
                                  yNode.getFullBoundsReference().getCenterY() - ( y1Node.getFullBoundsReference().getHeight() / 2 ) );
            }
        }
        else {  // y1 is on the left side of the equation

            PNode previousNode;

            // (y - y1)
            addChild( yLeftParenNode );
            addChild( yNode );
            addChild( yOperatorNode );
            addChild( y1Node );
            addChild( yRightParenNode );
            yLeftParenNode.setOffset( 0, 0 );
            yNode.setOffset( yLeftParenNode.getFullBoundsReference().getMaxX() + parenXSpacing,
                             yLeftParenNode.getYOffset() );
            yOperatorNode.setOffset( yNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                                     equalsNode.getFullBoundsReference().getCenterY() - ( yOperatorNode.getFullBoundsReference().getHeight() / 2 ) + operatorYFudgeFactor );
            y1Node.setOffset( yOperatorNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                              yNode.getFullBoundsReference().getCenterY() - ( y1Node.getFullBoundsReference().getHeight() / 2 ) );
            yRightParenNode.setOffset( y1Node.getFullBoundsReference().getMaxX() + parenXSpacing,
                                       yNode.getYOffset() );

            // =
            addChild( equalsNode );
            equalsNode.setOffset( yRightParenNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                  yNode.getYOffset() );

            // slope
            double previousXOffset;
            if ( interactiveSlope ) {
                // (rise/run), where rise and run are spinners, and the sign is integrated into the spinners
                addChild( riseNode );
                addChild( fractionLineNode );
                addChild( runNode );
                fractionLineNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                            equalsNode.getFullBoundsReference().getCenterY() + 2 );
                riseNode.setOffset( fractionLineNode.getFullBoundsReference().getCenterX() - ( riseNode.getFullBoundsReference().getWidth() / 2 ),
                                    fractionLineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - spinnersYSpacing );
                runNode.setOffset( fractionLineNode.getFullBoundsReference().getCenterX() - ( runNode.getFullBoundsReference().getWidth() / 2 ),
                                   fractionLineNode.getFullBoundsReference().getMinY() + spinnersYSpacing );
                previousNode = fractionLineNode;
                previousXOffset = fractionalSlopeXSpacing;
            }
            else {
                // slope is not interactive, so here we put it in the desired form

                // slope properties, used to determine correct form
                final double slope = line.getSlope();
                final boolean zeroSlope = ( slope == 0 );
                final boolean unitySlope = ( Math.abs( slope ) == 1 );
                final boolean integerSlope = MathUtil.isInteger( slope );
                final boolean positiveSlope = ( slope > 0 );
                final boolean fractionalSlope = ( !zeroSlope && !unitySlope && !integerSlope );

                // adjust fraction line width, use max width of rise or run
                double lineWidth = Math.max( riseNode.getFullBoundsReference().getWidth(), runNode.getFullBoundsReference().getWidth() );
                fractionLineNode.setPathTo( createFractionLineShape( lineWidth ) );

                // decide whether to include the slope minus sign
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
                    // rise/run
                    addChild( riseNode );
                    addChild( fractionLineNode );
                    addChild( runNode );
                    fractionLineNode.setOffset( previousNode.getFullBoundsReference().getMaxX() + previousXOffset,
                                                equalsNode.getFullBoundsReference().getCenterY() + 2 );
                    riseNode.setOffset( fractionLineNode.getFullBoundsReference().getCenterX() - ( riseNode.getFullBoundsReference().getWidth() / 2 ),
                                        fractionLineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - ySpacing );
                    runNode.setOffset( fractionLineNode.getFullBoundsReference().getCenterX() - ( runNode.getFullBoundsReference().getWidth() / 2 ),
                                       fractionLineNode.getFullBoundsReference().getMinY() + ySpacing );
                    previousNode = fractionLineNode;
                    previousXOffset = fractionalSlopeXSpacing;
                }
                else if ( zeroSlope ) {
                    // 0
                    addChild( riseNode );
                    riseNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing, yNode.getYOffset() );
                    previousNode = riseNode;
                    previousXOffset = integerSlopeXSpacing;
                }
                else if ( unitySlope ) {
                    // no slope term
                    previousXOffset = relationalOperatorXSpacing;
                }
                else if ( integerSlope ) {
                    // N
                    addChild( riseNode );
                    riseNode.setOffset( previousNode.getFullBoundsReference().getMaxX() + previousXOffset, yNode.getYOffset() );
                    previousNode = riseNode;
                    previousXOffset = integerSlopeXSpacing;
                }
                else {
                    throw new IllegalStateException( "programming error, didn't handle some slope case" );
                }
            }

            // x term
            if ( interactiveX1 || interactiveSlope || line.rise != 0 ) {
                // (x - x1)
                addChild( xLeftParenNode );
                addChild( xNode );
                addChild( xOperatorNode );
                addChild( x1Node );
                addChild( xRightParenNode );
                xLeftParenNode.setOffset( previousNode.getFullBoundsReference().getMaxX() + previousXOffset,
                                          yNode.getYOffset() );
                xNode.setOffset( xLeftParenNode.getFullBoundsReference().getMaxX() + parenXSpacing,
                                 yNode.getYOffset() );
                xOperatorNode.setOffset( xNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                                         equalsNode.getFullBoundsReference().getCenterY() - ( xOperatorNode.getFullBoundsReference().getHeight() / 2 ) + operatorYFudgeFactor );
                x1Node.setOffset( xOperatorNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                                  xNode.getFullBoundsReference().getCenterY() - ( x1Node.getFullBoundsReference().getHeight() / 2 ) );
                xRightParenNode.setOffset( x1Node.getFullBoundsReference().getMaxX() + parenXSpacing,
                                           yNode.getYOffset() );
            }
            else if ( line.rise == 0 ) {
                // no x term
            }
            else {
                throw new IllegalStateException( "programming error, didn't handle some x-term case" );
            }
        }

        // undefined-slope indicator, added after layout has been done
        if ( line.undefinedSlope() ) {
            PNode undefinedSlopeIndicator = new UndefinedSlopeIndicator( getFullBoundsReference().getWidth(), getFullBoundsReference().getHeight() );
            undefinedSlopeIndicator.setOffset( 0, fractionLineNode.getFullBoundsReference().getCenterY() - ( undefinedSlopeIndicator.getFullBoundsReference().getHeight() / 2 ) + undefinedSlopeYFudgeFactor );
            addChild( undefinedSlopeIndicator );
        }
    }

    // Creates a node that displays the general form of this equation.
    public static PNode createGeneralFormNode() {
        //NOTE: <font> tag is deprecated in HTML4 and unsupported in HTML5. But as of Java 1.7, Swing (supposedly) implements a subset of HTML3.
        String html = MessageFormat.format( "<html>({0} - {1}<font size='-1'><sub>1</sub></font>) = {2}({3} - {4}<font size='-1'><sub>1</sub></font>)</html>", /* (y - y1) = m(x - x1) */
                                            Strings.SYMBOL_Y, Strings.SYMBOL_Y, Strings.SYMBOL_SLOPE, Strings.SYMBOL_X, Strings.SYMBOL_X );
        return new HTMLNode( html, LGColors.INTERACTIVE_LINE, new PhetFont( Font.BOLD, 18 ) );
    }
}
