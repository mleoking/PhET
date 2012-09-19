// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

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
import edu.colorado.phet.linegraphing.common.view.SpinnerStateIndicator.PointColors;
import edu.colorado.phet.linegraphing.common.view.SpinnerStateIndicator.SlopeColors;
import edu.colorado.phet.linegraphing.common.view.UndefinedSlopeIndicator;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Interface for manipulating a point-slope equation.
 * Uses spinners to increment/decrement rise, run, x1 and y1.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeInteractiveEquationNode extends InteractiveEquationNode {

    private final Property<Double> rise, run, x1, y1; // internal properties that are connected to spinners
    private boolean updatingControls; // flag that allows us to update all controls atomically when the model changes

    // Nodes that appear in all possible forms of the equation "(y - y1) = m(x - x1)"
    private final PNode yLeftParenNode, yNode, yOperatorNode, y1Node, yRightParenNode, y1MinusSignNode, equalsNode;
    private final PNode slopeMinusSignNode, riseNode, runNode, xLeftParenNode, xNode, xOperatorNode, x1Node, xRightParenNode;
    private final PPath slopeLineNode;
    private PNode undefinedSlopeIndicator;

    // Constructor that makes x1, y1, and slope interactive.
    public PointSlopeInteractiveEquationNode( Property<Line> interactiveLine,
                                              Property<DoubleRange> riseRange,
                                              Property<DoubleRange> runRange,
                                              Property<DoubleRange> x1Range,
                                              Property<DoubleRange> y1Range ) {
        this( interactiveLine, riseRange, runRange, x1Range, y1Range, true, true, true );
    }

    // Constructor that allows you to specify which parts of the equation are interactive.
    public PointSlopeInteractiveEquationNode( Property<Line> interactiveLine,
                                              Property<DoubleRange> riseRange,
                                              Property<DoubleRange> runRange,
                                              Property<DoubleRange> x1Range,
                                              Property<DoubleRange> y1Range,
                                              boolean interactiveX1,
                                              boolean interactiveY1,
                                              boolean interactiveSlope ) {
        this( interactiveLine, riseRange, runRange, x1Range, y1Range, interactiveX1, interactiveY1, interactiveSlope,
              LGConstants.INTERACTIVE_EQUATION_FONT, LGConstants.STATIC_EQUATION_FONT, LGColors.STATIC_EQUATION_ELEMENT );
    }

    private PointSlopeInteractiveEquationNode( final Property<Line> interactiveLine,
                                               Property<DoubleRange> riseRange,
                                               Property<DoubleRange> runRange,
                                               Property<DoubleRange> x1Range,
                                               Property<DoubleRange> y1Range,
                                               final boolean interactiveX1,
                                               final boolean interactiveY1,
                                               final boolean interactiveSlope,
                                               PhetFont interactiveFont,
                                               PhetFont staticFont,
                                               final Color staticColor ) {

        this.rise = new Property<Double>( interactiveLine.get().rise );
        this.run = new Property<Double>( interactiveLine.get().run );
        this.x1 = new Property<Double>( interactiveLine.get().x1 );
        this.y1 = new Property<Double>( interactiveLine.get().y1 );

        // Determine the max width of the rise and run spinners.
        double maxSlopeSpinnerWidth = computeMaxSlopeSpinnerWidth( riseRange, runRange, interactiveFont, FORMAT );

        // nodes: (y-y1) = m(x-x1)
        yLeftParenNode = new PhetPText( "(", staticFont, staticColor );
        yNode = new PhetPText( Strings.SYMBOL_Y, staticFont, staticColor );
        yOperatorNode = new PNode(); // parent for + or - node
        if ( interactiveY1 ) {
            y1Node = new ZeroOffsetNode( new SpinnerNode( UserComponents.y1Spinner, this.y1, y1Range, new PointColors(), interactiveFont, FORMAT ) );
        }
        else {
            y1Node = new DynamicValueNode( y1, interactiveFont, staticColor, true ); // displayed as absolute value
        }
        yRightParenNode = new PhetPText( ")", staticFont, staticColor );
        y1MinusSignNode = new MinusNode( SIGN_LINE_SIZE, staticColor ); // for y=-y1 case
        equalsNode = new PhetPText( "=", staticFont, staticColor );
        slopeMinusSignNode = new MinusNode( SIGN_LINE_SIZE, staticColor );
        if ( interactiveSlope ) {
            riseNode = new ZeroOffsetNode( new RiseSpinnerNode( UserComponents.riseSpinner, this.rise, this.run, riseRange, new SlopeColors(), interactiveFont, FORMAT ) );
            runNode = new ZeroOffsetNode( new RunSpinnerNode( UserComponents.runSpinner, this.rise, this.run, runRange, new SlopeColors(), interactiveFont, FORMAT ) );
        }
        else {
            riseNode = new DynamicValueNode( rise, interactiveFont, staticColor, true ); // displayed as absolute value
            runNode = new DynamicValueNode( run, interactiveFont, staticColor, true ); // displayed as absolute value
        }
        slopeLineNode = new PhetPPath( new Line2D.Double( 0, 0, maxSlopeSpinnerWidth, 0 ), new BasicStroke( 3f ), staticColor );
        xLeftParenNode = new PhetPText( "(", staticFont, staticColor );
        xNode = new PhetPText( Strings.SYMBOL_X, staticFont, staticColor );
        xOperatorNode = new PNode(); // parent for + or - node
        if ( interactiveX1 ) {
            x1Node = new ZeroOffsetNode( new SpinnerNode( UserComponents.x1Spinner, this.x1, x1Range, new PointColors(), interactiveFont, FORMAT ) );
        }
        else {
            x1Node = new DynamicValueNode( x1, interactiveFont, staticColor, true ); // displayed as absolute value
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
                    rise.set( interactiveSlope ? line.rise : line.simplified().rise );
                    run.set( interactiveSlope ? line.run : line.simplified().run );
                    x1.set( line.x1 );
                    y1.set( line.y1 );
                }
                updatingControls = false;

                // Update the layout
                updateLayout( line, interactiveX1, interactiveY1, interactiveSlope, staticColor );
            }
        } );
    }

    /*
     * Updates the layout to match the desired form of the equation.
     * This is based on which parts of the equation are interactive, and what the
     * non-interactive parts of the equation should look like when written in simplified form.
     */
    private void updateLayout( Line line, boolean interactiveX1, boolean interactiveY1, boolean interactiveSlope, Color staticColor ) {

        // Start by adding all nodes, then we'll selectively remove some nodes based on the desired form of the equation.
        {
            // nodes that may be interactive first, so we can more easily identify layout problems
            addChild( y1Node );
            addChild( x1Node );
            addChild( riseNode );
            addChild( runNode );

            addChild( yLeftParenNode );
            addChild( yNode );
            addChild( yOperatorNode );
            addChild( yRightParenNode );
            addChild( y1MinusSignNode );
            addChild( equalsNode );
            addChild( slopeMinusSignNode );
            addChild( slopeLineNode );
            addChild( xLeftParenNode );
            addChild( xNode );
            addChild( xOperatorNode );
            addChild( xRightParenNode );
        }

        // Change the operators to account for the signs of x1 and y1.
        {
            xOperatorNode.removeAllChildren();
            if ( interactiveX1 || line.x1 >= 0 ) {
                xOperatorNode.addChild( new MinusNode( SIGN_LINE_SIZE, staticColor ) );
            }
            else {
                xOperatorNode.addChild( new PlusNode( OPERATOR_LINE_SIZE, staticColor ) );
            }

            yOperatorNode.removeAllChildren();
            if ( interactiveY1 || line.y1 >= 0 ) {
                yOperatorNode.addChild( new MinusNode( SIGN_LINE_SIZE, staticColor ) );
            }
            else {
                yOperatorNode.addChild( new PlusNode( OPERATOR_LINE_SIZE, staticColor ) );
            }
        }

        // spacing between components of the equation, visually tweaked
        final double xSpacing = 5;
        final double xParenSpacing = 2;
        final double ySpacing = 6;

        if ( line.rise == 0 && !interactiveSlope ) {
            // special case, slope of zero changes form to "y = y1"
            removeChild( yLeftParenNode );
            removeChild( yOperatorNode );
            removeChild( yRightParenNode );
            removeChild( slopeMinusSignNode );
            removeChild( riseNode );
            removeChild( runNode );
            removeChild( slopeLineNode );
            removeChild( xLeftParenNode );
            removeChild( xNode );
            removeChild( xOperatorNode );
            removeChild( x1Node );
            removeChild( xRightParenNode );
            if ( line.y1 >= 0 ) {
                removeChild( y1MinusSignNode );
            }

            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + xSpacing, yNode.getYOffset() );
            if ( interactiveY1 || line.y1 >= 0 ) {
                // y = y1
                y1Node.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing,
                                  yNode.getFullBoundsReference().getCenterY() - ( y1Node.getFullBoundsReference().getHeight() / 2 ) );
            }
            else {
                // y = -y1
                y1MinusSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing,
                                           equalsNode.getFullBoundsReference().getCenterY() - ( y1MinusSignNode.getFullBoundsReference().getHeight() / 2 ) + OPERATOR_Y_FUDGE_FACTOR );
                y1Node.setOffset( y1MinusSignNode.getFullBoundsReference().getMaxX() + xSpacing,
                                  yNode.getFullBoundsReference().getCenterY() - ( y1Node.getFullBoundsReference().getHeight() / 2 ) );
            }
        }
        else {
            removeChild( y1MinusSignNode );

            // left side of equation (y term)
            PNode previousNode;
            if ( interactiveY1 || line.y1 != 0 ) {
                removeChild( y1MinusSignNode );
                // (y - y1)
                yLeftParenNode.setOffset( 0, 0 );
                yNode.setOffset( yLeftParenNode.getFullBoundsReference().getMaxX() + xParenSpacing,
                                 yLeftParenNode.getYOffset() );
                yOperatorNode.setOffset( yNode.getFullBoundsReference().getMaxX() + xSpacing,
                                         equalsNode.getFullBoundsReference().getCenterY() - ( yOperatorNode.getFullBoundsReference().getHeight() / 2 ) + OPERATOR_Y_FUDGE_FACTOR );
                y1Node.setOffset( yOperatorNode.getFullBoundsReference().getMaxX() + xSpacing,
                                  yNode.getFullBoundsReference().getCenterY() - ( y1Node.getFullBoundsReference().getHeight() / 2 ) );
                yRightParenNode.setOffset( y1Node.getFullBoundsReference().getMaxX() + xParenSpacing,
                                           yNode.getYOffset() );
                previousNode = yRightParenNode;
            }
            else {
                // y
                removeChild( yLeftParenNode );
                removeChild( yOperatorNode );
                removeChild( y1Node );
                removeChild( yRightParenNode );
                removeChild( y1MinusSignNode );
                yNode.setOffset( 0, 0 );
                previousNode = yNode;
            }

            // =
            equalsNode.setOffset( previousNode.getFullBoundsReference().getMaxX() + xSpacing,
                                  yNode.getYOffset() );

            // slope
            if ( !interactiveSlope ) {
                // adjust fraction line width
                double lineWidth = Math.max( riseNode.getFullBoundsReference().getWidth(), runNode.getFullBoundsReference().getWidth() );
                slopeLineNode.setPathTo( new Line2D.Double( 0, 0, lineWidth, 0 ) );
            }
            slopeMinusSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing,
                                          equalsNode.getFullBoundsReference().getCenterY() - ( slopeMinusSignNode.getFullBoundsReference().getHeight() / 2 ) + SLOPE_SIGN_Y_FUDGE_FACTOR + SLOPE_SIGN_Y_OFFSET );
            slopeLineNode.setOffset( slopeMinusSignNode.getFullBoundsReference().getMaxX() + xSpacing,
                                     equalsNode.getFullBoundsReference().getCenterY() + 2 );
            riseNode.setOffset( slopeLineNode.getFullBoundsReference().getCenterX() - ( riseNode.getFullBoundsReference().getWidth() / 2 ),
                                slopeLineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - ySpacing );
            runNode.setOffset( slopeLineNode.getFullBoundsReference().getCenterX() - ( runNode.getFullBoundsReference().getWidth() / 2 ),
                               slopeLineNode.getFullBoundsReference().getMinY() + ySpacing );

            // x term
            if ( interactiveX1 || ( line.x1 != 0 && line.rise != 0 ) ) {
                // (x - x1)
                xLeftParenNode.setOffset( slopeLineNode.getFullBoundsReference().getMaxX() + xSpacing,
                                          yNode.getYOffset() );
                xNode.setOffset( xLeftParenNode.getFullBoundsReference().getMaxX() + xParenSpacing,
                                 yNode.getYOffset() );
                xOperatorNode.setOffset( xNode.getFullBoundsReference().getMaxX() + xSpacing,
                                         equalsNode.getFullBoundsReference().getCenterY() - ( xOperatorNode.getFullBoundsReference().getHeight() / 2 ) + OPERATOR_Y_FUDGE_FACTOR );
                x1Node.setOffset( xOperatorNode.getFullBoundsReference().getMaxX() + xSpacing,
                                  xNode.getFullBoundsReference().getCenterY() - ( x1Node.getFullBoundsReference().getHeight() / 2 ) );
                xRightParenNode.setOffset( x1Node.getFullBoundsReference().getMaxX() + xParenSpacing,
                                           yNode.getYOffset() );
            }
            else {
                // x
                removeChild( xLeftParenNode );
                removeChild( xOperatorNode );
                removeChild( x1Node );
                removeChild( xRightParenNode );
                xNode.setOffset( slopeLineNode.getFullBoundsReference().getMaxX() + xSpacing, yNode.getYOffset() );
            }
        }

        // remove any previous undefined-slope indicator
        if ( undefinedSlopeIndicator != null ) {
            removeChild( undefinedSlopeIndicator );
            undefinedSlopeIndicator = null;
        }

        // undefined-slope indicator, added after layout has been done
        if ( line.run == 0 ) {
            undefinedSlopeIndicator = new UndefinedSlopeIndicator( getFullBoundsReference().getWidth(), getFullBoundsReference().getHeight() );
            undefinedSlopeIndicator.setOffset( 0, slopeLineNode.getFullBoundsReference().getCenterY() - ( undefinedSlopeIndicator.getFullBoundsReference().getHeight() / 2 ) + 2 );
            addChild( undefinedSlopeIndicator );
        }
    }

    // test
    public static void main( String[] args ) {

        // model
        Property<Line> line = new Property<Line>( Line.createPointSlope( 1, 2, 3, 4, LGColors.INTERACTIVE_LINE ) );
        DoubleRange range = new DoubleRange( -10, 10 );
        Property<DoubleRange> riseRange = new Property<DoubleRange>( range );
        Property<DoubleRange> runRange = new Property<DoubleRange>( range );
        Property<DoubleRange> x1Range = new Property<DoubleRange>( range );
        Property<DoubleRange> y1Range = new Property<DoubleRange>( range );

        // equation
        PointSlopeInteractiveEquationNode equationNode1 = new PointSlopeInteractiveEquationNode( line, riseRange, runRange, x1Range, y1Range, true, true, true );
        PointSlopeInteractiveEquationNode equationNode2 = new PointSlopeInteractiveEquationNode( line, riseRange, runRange, x1Range, y1Range, false, false, true );
        PointSlopeInteractiveEquationNode equationNode3 = new PointSlopeInteractiveEquationNode( line, riseRange, runRange, x1Range, y1Range, true, true, false );
        PointSlopeInteractiveEquationNode equationNode4 = new PointSlopeInteractiveEquationNode( line, riseRange, runRange, x1Range, y1Range, true, false, false );
        PointSlopeInteractiveEquationNode equationNode5 = new PointSlopeInteractiveEquationNode( line, riseRange, runRange, x1Range, y1Range, false, true, false );
        PointSlopeInteractiveEquationNode equationNode6 = new PointSlopeInteractiveEquationNode( line, riseRange, runRange, x1Range, y1Range, false, false, false );

        // canvas
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 600, 750 ) );
        canvas.getLayer().addChild( equationNode1 );
        canvas.getLayer().addChild( equationNode2 );
        canvas.getLayer().addChild( equationNode3 );
        canvas.getLayer().addChild( equationNode4 );
        canvas.getLayer().addChild( equationNode5 );
        canvas.getLayer().addChild( equationNode6 );

        // layout
        final int ySpacing = 60;
        equationNode1.setOffset( 100, 50 );
        equationNode2.setOffset( equationNode1.getXOffset(), equationNode1.getFullBoundsReference().getMaxY() + ySpacing );
        equationNode3.setOffset( equationNode1.getXOffset(), equationNode2.getFullBoundsReference().getMaxY() + ySpacing );
        equationNode4.setOffset( equationNode1.getXOffset(), equationNode3.getFullBoundsReference().getMaxY() + ySpacing );
        equationNode5.setOffset( equationNode1.getXOffset(), equationNode4.getFullBoundsReference().getMaxY() + ySpacing );
        equationNode6.setOffset( equationNode1.getXOffset(), equationNode5.getFullBoundsReference().getMaxY() + ySpacing );

        // frame
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
