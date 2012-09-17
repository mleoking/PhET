// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Line2D;
import java.text.NumberFormat;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
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
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.DynamicValueNode;
import edu.colorado.phet.linegraphing.common.view.InteractiveEquationNode;
import edu.colorado.phet.linegraphing.common.view.SlopeSpinnerNode.RiseSpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SlopeSpinnerNode.RunSpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SpinnerStateIndicator.InterceptColors;
import edu.colorado.phet.linegraphing.common.view.SpinnerStateIndicator.SlopeColors;
import edu.colorado.phet.linegraphing.common.view.UndefinedSlopeIndicator;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Interface for manipulating a slope-intercept equation.
 * Uses spinners to increment/decrement rise, run and intercept.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptInteractiveEquationNode extends InteractiveEquationNode {

    private static final int SLOPE_SIGN_Y_OFFSET_FUDGE_FACTOR = 2;
    private static final int INTERCEPT_SIGN_Y_OFFSET_FUDGE_FACTOR = 2;
    private static final int FRACTION_LINE_Y_OFFSET_FUDGE_FACTOR = 2;
    private static final int UNDEFINED_SLOPE_Y_OFFSET_FUDGE_FACTOR = 2;

    private static final float SLOPE_SIGN_STROKE_WIDTH = 3f;
    private static final float FRACTION_LINE_STROKE_WIDTH = 2f;
    private static final int MINUS_SIGN_LENGTH = 15;

    private static final NumberFormat FORMAT = new DefaultDecimalFormat( "0" );

    private final Property<Double> rise, run, yIntercept; // internal properties that are connected to spinners
    private boolean updatingControls; // flag that allows us to update all controls atomically when the model changes

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
                                                   Color staticColor ) {

        this.rise = new Property<Double>( interactiveLine.get().rise );
        this.run = new Property<Double>( interactiveLine.get().run );
        this.yIntercept = new Property<Double>( interactiveLine.get().y1 );

        // Determine the max width of the rise and run components.
        double maxSlopeWidth = computeSlopeComponentMaxWidth( riseRange, runRange, interactiveFont, FORMAT, interactiveSlope );

        // nodes: y = mx + b
        final PNode yNode = new PhetPText( "y", staticFont, staticColor );
        final PNode equalsNode = new PhetPText( "=", staticFont, staticColor );
        final PNode slopeMinusSignNode = new PhetPPath( new Line2D.Double( 0, 0, MINUS_SIGN_LENGTH, 0 ), new BasicStroke( SLOPE_SIGN_STROKE_WIDTH ), staticColor );
        final PNode riseNode, runNode;
        if ( interactiveSlope ) {
            riseNode = new ZeroOffsetNode( new RiseSpinnerNode( UserComponents.riseSpinner, this.rise, this.run, riseRange, new SlopeColors(),
                                                                interactiveFont, FORMAT ) );
            runNode = new ZeroOffsetNode( new RunSpinnerNode( UserComponents.runSpinner, this.rise, this.run, runRange, new SlopeColors(),
                                                              interactiveFont, FORMAT ) );
        }
        else {
            riseNode = new DynamicValueNode( rise, interactiveFont, staticColor, true );
            runNode = new DynamicValueNode( run, interactiveFont, staticColor, true );
        }
        final PPath lineNode = new PhetPPath( new Line2D.Double( 0, 0, maxSlopeWidth, 0 ), new BasicStroke( FRACTION_LINE_STROKE_WIDTH ), staticColor );
        final PNode xNode = new PhetPText( "x", staticFont, staticColor );
        final PText operatorNode = new PhetPText( "+", staticFont, staticColor );
        final PNode interceptMinusSignNode = new PhetPPath( new Line2D.Double( 0, 0, MINUS_SIGN_LENGTH, 0 ), new BasicStroke( SLOPE_SIGN_STROKE_WIDTH ), staticColor );
        final PNode interceptNode;
        if ( interactiveIntercept ) {
            interceptNode = new ZeroOffsetNode( new SpinnerNode( UserComponents.interceptSpinner, this.yIntercept, yInterceptRange, new InterceptColors(), interactiveFont, FORMAT ) );
        }
        else {
            interceptNode = new DynamicValueNode( yIntercept, interactiveFont, staticColor, true ); // absolute value
        }

        // rendering order
        {
            addChild( yNode );
            addChild( equalsNode );
            addChild( slopeMinusSignNode );
            addChild( riseNode );
            addChild( lineNode );
            addChild( runNode );
            addChild( xNode );
            addChild( operatorNode );
            addChild( interceptMinusSignNode );
            addChild( interceptNode );
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

        // sync the controls with the model
        interactiveLine.addObserver( new VoidFunction1<Line>() {

            private PNode undefinedSlopeIndicator;

            public void apply( Line line ) {
                assert ( line.x1 == 0 ); // line is in slope-intercept form

                // Atomically synchronize the controls.
                updatingControls = true;
                {
                    rise.set( interactiveSlope ? line.rise : line.simplified().rise );
                    run.set( interactiveSlope ? line.run : line.simplified().run );
                    yIntercept.set( line.y1 );
                }
                updatingControls = false;

                // Adjust equation format based on slope.
                final boolean zeroSlope = ( line.rise == 0 );
                final boolean unitySlope = ( Math.abs( line.rise / line.run ) == 1 );
                final boolean integerSlope = ( Math.abs( line.simplified().run ) == 1 );
                final boolean positiveSlope = ( line.rise / line.run > 0 );
                {
                    addChild( slopeMinusSignNode );
                    addChild( riseNode );
                    addChild( runNode );
                    addChild( lineNode );
                    addChild( xNode );
                    addChild( operatorNode );

                    if ( interactiveSlope ) {
                        removeChild( slopeMinusSignNode );
                    }
                    else {
                        if ( zeroSlope || positiveSlope ) {
                            removeChild( slopeMinusSignNode );
                        }

                        if ( zeroSlope ) {
                            // If slope is zero, hide slope, x, and operator.
                            removeChild( riseNode );
                            removeChild( runNode );
                            removeChild( lineNode );
                            removeChild( xNode );
                            removeChild( operatorNode );
                        }
                        else if ( unitySlope && !interactiveSlope ) {
                            // If slope is 1, hide slope
                            removeChild( riseNode );
                            removeChild( runNode );
                            removeChild( lineNode );
                        }
                        else if ( integerSlope && !interactiveSlope ) {
                            // If slope is an integer, show only rise.
                            removeChild( runNode );
                            removeChild( lineNode );
                        }
                    }
                }

                // Adjust format of equation based on intercept.
                {
                    addChild( operatorNode );
                    addChild( interceptMinusSignNode );
                    addChild( interceptNode );

                    if ( interactiveIntercept ) {
                        removeChild( interceptMinusSignNode );
                    }
                    else {
                        /*
                        * Change the operator to account for the sign of the intercept.
                        * We're doing this because intercept is displayed as absolute value when not interactive.
                        */
                        operatorNode.setText( line.y1 >= 0 ? "+" : "-" );

                        if ( line.y1 >= 0 || !zeroSlope ) {
                            removeChild( interceptMinusSignNode );
                        }

                        if ( zeroSlope && !interactiveSlope ) {
                            removeChild( operatorNode );
                        }

                        if ( line.y1 == 0 && !zeroSlope ) {
                            removeChild( operatorNode );
                            removeChild( interceptNode );
                        }
                    }
                }

                // layout
                {
                    final double xSpacing = 10;
                    final double ySpacing = 6;

                    // y =
                    yNode.setOffset( 0, 0 );
                    equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + xSpacing,
                                          yNode.getYOffset() );

                    // mx
                    PNode previousNode = null;
                    if ( zeroSlope && !interactiveSlope ) {
                        // do nothing, there is no slope to lay out
                        previousNode = equalsNode;
                    }
                    else if ( unitySlope && !interactiveSlope ) {
                        if ( positiveSlope ) {
                            xNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing, yNode.getYOffset() );
                        }
                        else {
                            slopeMinusSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing,
                                                          equalsNode.getFullBoundsReference().getCenterY() - ( slopeMinusSignNode.getFullBoundsReference().getHeight() / 2 ) + SLOPE_SIGN_Y_OFFSET_FUDGE_FACTOR );
                            xNode.setOffset( slopeMinusSignNode.getFullBoundsReference().getMaxX() + ( xSpacing / 2 ), yNode.getYOffset() );
                        }
                        previousNode = xNode;
                    }
                    else if ( integerSlope && !interactiveSlope ) {
                        if ( positiveSlope ) {
                            riseNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing, yNode.getYOffset() );
                        }
                        else {
                            slopeMinusSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing,
                                                          equalsNode.getFullBoundsReference().getCenterY() - ( slopeMinusSignNode.getFullBoundsReference().getHeight() / 2 ) + SLOPE_SIGN_Y_OFFSET_FUDGE_FACTOR );
                            riseNode.setOffset( slopeMinusSignNode.getFullBoundsReference().getMaxX(), yNode.getYOffset() );
                        }
                        xNode.setOffset( riseNode.getFullBoundsReference().getMaxX() + ( xSpacing / 2 ), yNode.getYOffset() );
                        previousNode = xNode;
                    }
                    else {
                        if ( positiveSlope || interactiveSlope ) {
                            lineNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing,
                                                equalsNode.getFullBoundsReference().getCenterY() + FRACTION_LINE_Y_OFFSET_FUDGE_FACTOR );
                        }
                        else {
                            slopeMinusSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing,
                                                          equalsNode.getFullBoundsReference().getCenterY() - ( slopeMinusSignNode.getFullBoundsReference().getHeight() / 2 ) + SLOPE_SIGN_Y_OFFSET_FUDGE_FACTOR );
                            lineNode.setOffset( slopeMinusSignNode.getFullBoundsReference().getMaxX() + xSpacing,
                                                equalsNode.getFullBoundsReference().getCenterY() + FRACTION_LINE_Y_OFFSET_FUDGE_FACTOR );
                        }
                        riseNode.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( riseNode.getFullBoundsReference().getWidth() / 2 ),
                                            lineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - ySpacing );
                        runNode.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( runNode.getFullBoundsReference().getWidth() / 2 ),
                                           lineNode.getFullBoundsReference().getMinY() + ySpacing );
                        xNode.setOffset( lineNode.getFullBoundsReference().getMaxX() + ( xSpacing / 2 ),
                                         yNode.getYOffset() );
                        operatorNode.setOffset( xNode.getFullBoundsReference().getMaxX() + xSpacing,
                                                xNode.getYOffset() );
                        xNode.setOffset( lineNode.getFullBoundsReference().getMaxX() + xSpacing,
                                         yNode.getYOffset() );
                        previousNode = xNode;
                    }

                    // + b
                    if ( zeroSlope && !interactiveSlope ) {
                        if ( line.y1 >= 0 ) {
                            interceptNode.setOffset( previousNode.getFullBoundsReference().getMaxX() + xSpacing,
                                                     yNode.getFullBoundsReference().getCenterY() - ( interceptNode.getFullBoundsReference().getHeight() / 2 ) );
                        }
                        else {
                            interceptMinusSignNode.setOffset( previousNode.getFullBoundsReference().getMaxX() + xSpacing,
                                                              equalsNode.getFullBoundsReference().getCenterY() - ( interceptMinusSignNode.getFullBoundsReference().getHeight() / 2 ) + INTERCEPT_SIGN_Y_OFFSET_FUDGE_FACTOR );
                            interceptNode.setOffset( interceptMinusSignNode.getFullBoundsReference().getMaxX(),
                                                     yNode.getFullBoundsReference().getCenterY() - ( interceptNode.getFullBoundsReference().getHeight() / 2 ) );
                        }
                    }
                    else {
                        operatorNode.setOffset( previousNode.getFullBoundsReference().getMaxX() + xSpacing,
                                                yNode.getYOffset() );
                        interceptNode.setOffset( operatorNode.getFullBoundsReference().getMaxX() + xSpacing,
                                                 yNode.getFullBoundsReference().getCenterY() - ( interceptNode.getFullBoundsReference().getHeight() / 2 ) );
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
                    undefinedSlopeIndicator.setOffset( 0,
                                                       lineNode.getFullBoundsReference().getCenterY() - ( undefinedSlopeIndicator.getFullBoundsReference().getHeight() / 2 ) + UNDEFINED_SLOPE_Y_OFFSET_FUDGE_FACTOR );
                    addChild( undefinedSlopeIndicator );
                }
            }
        } );
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
