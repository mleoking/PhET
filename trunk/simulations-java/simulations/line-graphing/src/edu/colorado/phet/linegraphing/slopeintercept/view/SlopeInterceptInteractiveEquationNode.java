// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Line2D;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.DynamicValueNode;
import edu.colorado.phet.linegraphing.common.view.InteractiveEquationNode;
import edu.colorado.phet.linegraphing.common.view.MinusNode;
import edu.colorado.phet.linegraphing.common.view.PlusNode;
import edu.colorado.phet.linegraphing.common.view.SlopeSpinnerNode.RiseSpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SlopeSpinnerNode.RunSpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SpinnerStateIndicator.InterceptColors;
import edu.colorado.phet.linegraphing.common.view.SpinnerStateIndicator.SlopeColors;
import edu.colorado.phet.linegraphing.common.view.UndefinedSlopeIndicator;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * User interface for manipulating a slope-intercept equation: y = mx + b
 * <p/>
 * Spinners are used to increment/decrement parts of the equation that are specified as being interactive.
 * Non-interactive parts of the equation are expressed in a form that is typical of how the equation
 * would normally be written.  For example, if the slope is -1, then only the sign is written, not "-1".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptInteractiveEquationNode extends InteractiveEquationNode {

    private final Property<Double> rise, run, yIntercept; // internal properties that are connected to spinners
    private boolean updatingControls; // flag that allows us to update all controls atomically when the model changes

    // Nodes that appear in all possible forms of the equation "y = mx + b"
    private final PNode yNode, equalsNode, slopeMinusSignNode, riseNode, runNode, xNode, operatorNode, interceptMinusSignNode, interceptNode;
    private final PPath slopeLineNode;
    private PNode undefinedSlopeIndicator;

    // Constructor that makes both slope and intercept interactive.
    public SlopeInterceptInteractiveEquationNode( Property<Line> interactiveLine,
                                                  Property<DoubleRange> riseRange,
                                                  Property<DoubleRange> runRange,
                                                  Property<DoubleRange> yInterceptRange ) {
        this( interactiveLine, riseRange, runRange, yInterceptRange, true, true );
    }

    // Constructor that allows you to specify which parts of the equation are interactive.
    public SlopeInterceptInteractiveEquationNode( Property<Line> interactiveLine,
                                                  Property<DoubleRange> riseRange,
                                                  Property<DoubleRange> runRange,
                                                  Property<DoubleRange> yInterceptRange,
                                                  boolean interactiveSlope,
                                                  boolean interactiveIntercept ) {
        this( interactiveLine, riseRange, runRange, yInterceptRange, interactiveSlope, interactiveIntercept,
              LGConstants.INTERACTIVE_EQUATION_FONT, LGConstants.STATIC_EQUATION_FONT, LGColors.STATIC_EQUATION_ELEMENT );
    }

    private SlopeInterceptInteractiveEquationNode( final Property<Line> interactiveLine,
                                                   Property<DoubleRange> riseRange,
                                                   Property<DoubleRange> runRange,
                                                   Property<DoubleRange> yInterceptRange,
                                                   final boolean interactiveSlope,
                                                   final boolean interactiveIntercept,
                                                   PhetFont interactiveFont,
                                                   PhetFont staticFont,
                                                   final Color staticColor ) {

        this.rise = new Property<Double>( interactiveLine.get().rise );
        this.run = new Property<Double>( interactiveLine.get().run );
        this.yIntercept = new Property<Double>( interactiveLine.get().y1 );

        // Determine the max width of the rise and run spinners.
        double maxSlopeSpinnerWidth = computeMaxSlopeSpinnerWidth( riseRange, runRange, interactiveFont, FORMAT );

        // nodes: y = -(rise/run)x + -b
        yNode = new PhetPText( Strings.SYMBOL_Y, staticFont, staticColor );
        equalsNode = new PhetPText( "=", staticFont, staticColor );
        slopeMinusSignNode = new MinusNode( SIGN_LINE_SIZE, staticColor );
        if ( interactiveSlope ) {
            riseNode = new ZeroOffsetNode( new RiseSpinnerNode( UserComponents.riseSpinner, this.rise, this.run, riseRange, new SlopeColors(),
                                                                interactiveFont, FORMAT ) );
            runNode = new ZeroOffsetNode( new RunSpinnerNode( UserComponents.runSpinner, this.rise, this.run, runRange, new SlopeColors(),
                                                              interactiveFont, FORMAT ) );
        }
        else {
            riseNode = new DynamicValueNode( rise, staticFont, staticColor, true );
            runNode = new DynamicValueNode( run, staticFont, staticColor, true );
        }
        slopeLineNode = new PhetPPath( new Line2D.Double( 0, 0, maxSlopeSpinnerWidth, 0 ), new BasicStroke( FRACTION_LINE_THICKNESS ), staticColor );
        xNode = new PhetPText( Strings.SYMBOL_X, staticFont, staticColor );
        operatorNode = new PNode(); // parent for + or - node
        interceptMinusSignNode = new MinusNode( SIGN_LINE_SIZE, staticColor );
        if ( interactiveIntercept ) {
            interceptNode = new ZeroOffsetNode( new SpinnerNode( UserComponents.interceptSpinner, this.yIntercept, yInterceptRange, new InterceptColors(), interactiveFont, FORMAT ) );
        }
        else {
            interceptNode = new DynamicValueNode( yIntercept, staticFont, staticColor, true ); // absolute value
        }

        // sync the model with the controls
        RichSimpleObserver lineUpdater = new RichSimpleObserver() {
            @Override public void update() {
                if ( !updatingControls ) {
                    interactiveLine.set( Line.createSlopeIntercept( rise.get(), run.get(), yIntercept.get(), interactiveLine.get().color ) );
                }
            }
        };
        lineUpdater.observe( rise, run, yIntercept );

        // sync the controls and layout with the model
        interactiveLine.addObserver( new VoidFunction1<Line>() {

            public void apply( Line line ) {
                assert ( line.x1 == 0 ); // line is in slope-intercept form

                // Synchronize the controls atomically.
                updatingControls = true;
                {
                    rise.set( interactiveSlope ? line.rise : line.simplified().rise );
                    run.set( interactiveSlope ? line.run : line.simplified().run );
                    yIntercept.set( line.y1 );
                }
                updatingControls = false;

                // Update the layout.
                updateLayout( line, interactiveSlope, interactiveIntercept, staticColor );
            }
        } );
    }

    /*
     * Updates the layout to match the desired form of the equation.
     * This is based on which parts of the equation are interactive, and what the
     * non-interactive parts of the equation should look like when written in simplified form.
     */
    private void updateLayout( Line line, boolean interactiveSlope, boolean interactiveIntercept, Color staticColor ) {

        // Start by adding all nodes, then we'll selectively remove some nodes based on the desired form of the equation.
        {
            // nodes that may be interactive first, so we can more easily identify layout problems
            addChild( riseNode );
            addChild( runNode );
            addChild( interceptNode );

            addChild( yNode );
            addChild( equalsNode );
            addChild( slopeMinusSignNode );
            addChild( slopeLineNode );
            addChild( xNode );
            addChild( operatorNode );
            addChild( interceptMinusSignNode );

        }

        // slope properties
        final boolean zeroSlope = ( line.getSlope() == 0 );
        final boolean unitySlope = ( Math.abs( line.getSlope() ) == 1 );
        final boolean integerSlope = ( Math.abs( line.simplified().run ) == 1 );
        final boolean positiveSlope = ( line.getSlope() > 0 );

        // spacing between components of the equation, visually tweaked
        final double xSpacing = 10;
        final double ySpacing = 6;

        // y =
        yNode.setOffset( 0, 0 );
        equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + xSpacing,
                              yNode.getYOffset() );

        // Layout the "y = mx" part of the equation
        if ( interactiveSlope ) {
            // y = (rise/run)x
            removeChild( slopeMinusSignNode );
            slopeLineNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing,
                                     equalsNode.getFullBoundsReference().getCenterY() + FRACTION_LINE_Y_FUDGE_FACTOR );
            riseNode.setOffset( slopeLineNode.getFullBoundsReference().getCenterX() - ( riseNode.getFullBoundsReference().getWidth() / 2 ),
                                slopeLineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - ySpacing );
            runNode.setOffset( slopeLineNode.getFullBoundsReference().getCenterX() - ( runNode.getFullBoundsReference().getWidth() / 2 ),
                               slopeLineNode.getFullBoundsReference().getMinY() + ySpacing );
            xNode.setOffset( slopeLineNode.getFullBoundsReference().getMaxX() + xSpacing, yNode.getYOffset() );
        }
        else {
            if ( zeroSlope ) {
                // y =
                removeChild( slopeMinusSignNode );
                removeChild( slopeLineNode );
                removeChild( riseNode );
                removeChild( runNode );
                removeChild( xNode );
            }
            else if ( unitySlope ) {
                removeChild( slopeLineNode );
                removeChild( riseNode );
                removeChild( runNode );
                if ( line.getSlope() > 0 ) {
                    // y = x
                    removeChild( slopeMinusSignNode );
                    xNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing, yNode.getYOffset() );
                }
                else {
                    // y = -x
                    slopeMinusSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing,
                                                  equalsNode.getFullBoundsReference().getCenterY() - ( slopeMinusSignNode.getFullBoundsReference().getHeight() / 2 ) + SLOPE_SIGN_Y_FUDGE_FACTOR );
                    xNode.setOffset( slopeMinusSignNode.getFullBoundsReference().getMaxX() + ( xSpacing / 2 ), yNode.getYOffset() );
                }
            }
            else if ( integerSlope ) {
                removeChild( slopeLineNode );
                removeChild( runNode );
                if ( line.getSlope() > 0 ) {
                    // y = Nx
                    removeChild( slopeMinusSignNode );
                    riseNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing, yNode.getYOffset() );
                    xNode.setOffset( riseNode.getFullBoundsReference().getMaxX() + ( xSpacing / 2 ), yNode.getYOffset() );
                }
                else {
                    // y = -Nx
                    slopeMinusSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing,
                                                  equalsNode.getFullBoundsReference().getCenterY() - ( slopeMinusSignNode.getFullBoundsReference().getHeight() / 2 ) + SLOPE_SIGN_Y_FUDGE_FACTOR );
                    riseNode.setOffset( slopeMinusSignNode.getFullBoundsReference().getMaxX() + ( xSpacing / 2 ), yNode.getYOffset() );
                    xNode.setOffset( riseNode.getFullBoundsReference().getMaxX() + ( xSpacing / 2 ), yNode.getYOffset() );
                }
            }
            else {
                // y = -(rise/run)x

                // adjust fraction line width
                double lineWidth = Math.max( riseNode.getFullBoundsReference().getWidth(), runNode.getFullBoundsReference().getWidth() );
                slopeLineNode.setPathTo( new Line2D.Double( 0, 0, lineWidth, 0 ) );

                if ( positiveSlope ) {
                    removeChild( slopeMinusSignNode );
                    // y = (rise/run)x
                    slopeLineNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing,
                                             equalsNode.getFullBoundsReference().getCenterY() + FRACTION_LINE_Y_FUDGE_FACTOR );
                    riseNode.setOffset( slopeLineNode.getFullBoundsReference().getCenterX() - ( riseNode.getFullBoundsReference().getWidth() / 2 ),
                                        slopeLineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - ySpacing );
                    runNode.setOffset( slopeLineNode.getFullBoundsReference().getCenterX() - ( runNode.getFullBoundsReference().getWidth() / 2 ),
                                       slopeLineNode.getFullBoundsReference().getMinY() + ySpacing );
                    xNode.setOffset( slopeLineNode.getFullBoundsReference().getMaxX() + xSpacing, yNode.getYOffset() );
                }
                else {
                    // y = -(rise/run)x
                    slopeMinusSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing,
                                                  equalsNode.getFullBoundsReference().getCenterY() - ( slopeMinusSignNode.getFullBoundsReference().getHeight() / 2 ) + SLOPE_SIGN_Y_FUDGE_FACTOR + SLOPE_SIGN_Y_OFFSET );
                    slopeLineNode.setOffset( slopeMinusSignNode.getFullBoundsReference().getMaxX() + xSpacing,
                                             equalsNode.getFullBoundsReference().getCenterY() + FRACTION_LINE_Y_FUDGE_FACTOR );
                    riseNode.setOffset( slopeLineNode.getFullBoundsReference().getCenterX() - ( riseNode.getFullBoundsReference().getWidth() / 2 ),
                                        slopeLineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - ySpacing );
                    runNode.setOffset( slopeLineNode.getFullBoundsReference().getCenterX() - ( runNode.getFullBoundsReference().getWidth() / 2 ),
                                       slopeLineNode.getFullBoundsReference().getMinY() + ySpacing );
                    xNode.setOffset( slopeLineNode.getFullBoundsReference().getMaxX() + xSpacing, yNode.getYOffset() );
                }
            }
        }

        // Layout the "+ b" part of the equation.
        operatorNode.removeAllChildren();
        if ( interactiveIntercept ) {

            removeChild( interceptMinusSignNode );

            // If intercept is interactive, the operator is +
            operatorNode.addChild( new PlusNode( OPERATOR_LINE_SIZE, staticColor ) );

            if ( zeroSlope && !interactiveSlope ) {
                // y = b
                removeChild( operatorNode );
                interceptNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing,
                                         yNode.getFullBoundsReference().getCenterY() - ( interceptNode.getFullBoundsReference().getHeight() / 2 ) );
            }
            else {
                // y = (rise/run)x + b
                operatorNode.setOffset( xNode.getFullBoundsReference().getMaxX() + xSpacing,
                                        equalsNode.getFullBoundsReference().getCenterY() - ( operatorNode.getFullBoundsReference().getHeight() / 2 ) + OPERATOR_Y_FUDGE_FACTOR );
                interceptNode.setOffset( operatorNode.getFullBoundsReference().getMaxX() + xSpacing,
                                         yNode.getFullBoundsReference().getCenterY() - ( interceptNode.getFullBoundsReference().getHeight() / 2 ) );
            }
        }
        else {
            // Set the operator based on the sign of the y intercept.
            if ( line.y1 >= 0 ) {
                operatorNode.addChild( new PlusNode( OPERATOR_LINE_SIZE, staticColor ) );
            }
            else {
                operatorNode.addChild( new MinusNode( OPERATOR_LINE_SIZE, staticColor ) );
            }

            if ( line.y1 == 0 ) {
                removeChild( interceptMinusSignNode );
                removeChild( operatorNode );
                if ( zeroSlope && !interactiveSlope ) {
                    // y = 0
                    interceptNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing,
                                             yNode.getFullBoundsReference().getCenterY() - ( interceptNode.getFullBoundsReference().getHeight() / 2 ) );
                }
                else {
                    // y = mx
                    removeChild( interceptNode );
                }
            }
            else if ( line.y1 > 0 ) {
                removeChild( interceptMinusSignNode );
                if ( zeroSlope && !interactiveSlope ) {
                    // y = b
                    removeChild( operatorNode );
                    interceptNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing,
                                             yNode.getFullBoundsReference().getCenterY() - ( interceptNode.getFullBoundsReference().getHeight() / 2 ) );
                }
                else {
                    // y = mx + b
                    operatorNode.setOffset( xNode.getFullBoundsReference().getMaxX() + xSpacing,
                                            equalsNode.getFullBoundsReference().getCenterY() - ( operatorNode.getFullBoundsReference().getHeight() / 2 ) + OPERATOR_Y_FUDGE_FACTOR );
                    interceptNode.setOffset( operatorNode.getFullBoundsReference().getMaxX() + xSpacing,
                                             yNode.getFullBoundsReference().getCenterY() - ( interceptNode.getFullBoundsReference().getHeight() / 2 ) );
                }
            }
            else { /* line.y1 < 0 */
                if ( zeroSlope && !interactiveSlope ) {
                    // y = -b
                    removeChild( operatorNode );
                    interceptMinusSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing,
                                                      equalsNode.getFullBoundsReference().getCenterY() - ( interceptMinusSignNode.getFullBoundsReference().getHeight() / 2 ) + OPERATOR_Y_FUDGE_FACTOR );
                    interceptNode.setOffset( interceptMinusSignNode.getFullBoundsReference().getMaxX() + ( xSpacing / 2 ),
                                             yNode.getFullBoundsReference().getCenterY() - ( interceptNode.getFullBoundsReference().getHeight() / 2 ) );
                }
                else {
                    // y = mx - b
                    removeChild( interceptMinusSignNode );
                    operatorNode.setOffset( xNode.getFullBoundsReference().getMaxX() + xSpacing,
                                            equalsNode.getFullBoundsReference().getCenterY() - ( operatorNode.getFullBoundsReference().getHeight() / 2 ) + OPERATOR_Y_FUDGE_FACTOR );
                    interceptNode.setOffset( operatorNode.getFullBoundsReference().getMaxX() + xSpacing,
                                             yNode.getFullBoundsReference().getCenterY() - ( interceptNode.getFullBoundsReference().getHeight() / 2 ) );
                }
            }
        }

        // Remove any previous undefined-slope indicator (the big "X" that appears over the equation.)
        if ( undefinedSlopeIndicator != null ) {
            removeChild( undefinedSlopeIndicator );
            undefinedSlopeIndicator = null;
        }

        // Add the undefined-slope indicator after layout has been done, so that it covers the entire equation.
        if ( line.run == 0 ) {
            undefinedSlopeIndicator = new UndefinedSlopeIndicator( getFullBoundsReference().getWidth(), getFullBoundsReference().getHeight() );
            undefinedSlopeIndicator.setOffset( 0,
                                               slopeLineNode.getFullBoundsReference().getCenterY() - ( undefinedSlopeIndicator.getFullBoundsReference().getHeight() / 2 ) + UNDEFINED_SLOPE_Y_FUDGE_FACTOR );
            addChild( undefinedSlopeIndicator );
        }
    }

    // test
    public static void main( String[] args ) {

        // model
        DoubleRange range = new DoubleRange( -10, 10 );
        Property<DoubleRange> riseRange = new Property<DoubleRange>( range );
        Property<DoubleRange> runRange = new Property<DoubleRange>( range );
        Property<DoubleRange> yInterceptRange = new Property<DoubleRange>( range );
        Property<Line> line = new Property<Line>( Line.createSlopeIntercept( 1, 1, 1, LGColors.INTERACTIVE_LINE ) );

        // equations
        SlopeInterceptInteractiveEquationNode equationNode1 = new SlopeInterceptInteractiveEquationNode( line, riseRange, runRange, yInterceptRange, true, true );
        SlopeInterceptInteractiveEquationNode equationNode2 = new SlopeInterceptInteractiveEquationNode( line, riseRange, runRange, yInterceptRange, false, true );
        SlopeInterceptInteractiveEquationNode equationNode3 = new SlopeInterceptInteractiveEquationNode( line, riseRange, runRange, yInterceptRange, true, false );
        SlopeInterceptInteractiveEquationNode equationNode4 = new SlopeInterceptInteractiveEquationNode( line, riseRange, runRange, yInterceptRange, false, false );

        // canvas
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 600, 650 ) );
        canvas.getLayer().addChild( equationNode1 );
        canvas.getLayer().addChild( equationNode2 );
        canvas.getLayer().addChild( equationNode3 );
        canvas.getLayer().addChild( equationNode4 );

        // layout
        final int ySpacing = 60;
        equationNode1.setOffset( 100, 50 );
        equationNode2.setOffset( equationNode1.getXOffset(), equationNode1.getFullBoundsReference().getMaxY() + ySpacing );
        equationNode3.setOffset( equationNode1.getXOffset(), equationNode2.getFullBoundsReference().getMaxY() + ySpacing );
        equationNode4.setOffset( equationNode1.getXOffset(), equationNode3.getFullBoundsReference().getMaxY() + ySpacing );

        // frame
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
