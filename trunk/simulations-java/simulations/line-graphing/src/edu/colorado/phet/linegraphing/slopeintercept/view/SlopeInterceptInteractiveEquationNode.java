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
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.DynamicValueNode;
import edu.colorado.phet.linegraphing.common.view.SlopeSpinnerNode.RiseSpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SlopeSpinnerNode.RunSpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SpinnerStateIndicator.InterceptColors;
import edu.colorado.phet.linegraphing.common.view.SpinnerStateIndicator.SlopeColors;
import edu.colorado.phet.linegraphing.common.view.UndefinedSlopeIndicator;
import edu.umd.cs.piccolo.PNode;

/**
 * Interface for manipulating a slope-intercept equation.
 * Uses spinners to increment/decrement rise, run and intercept.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptInteractiveEquationNode extends PhetPNode {

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
                                                  boolean variableSlope,
                                                  boolean variableIntercept ) {
        this( interactiveLine, riseRange, runRange, yInterceptRange, variableSlope, variableIntercept,
              LGConstants.INTERACTIVE_EQUATION_FONT, LGConstants.STATIC_EQUATION_FONT, LGColors.STATIC_EQUATION_ELEMENT );
    }

    private SlopeInterceptInteractiveEquationNode( final Property<Line> interactiveLine,
                                                   Property<DoubleRange> riseRange,
                                                   Property<DoubleRange> runRange,
                                                   Property<DoubleRange> yInterceptRange,
                                                   final boolean variableSlope,
                                                   boolean variableIntercept,
                                                   PhetFont interactiveFont,
                                                   PhetFont staticFont,
                                                   Color staticColor ) {

        this.rise = new Property<Double>( interactiveLine.get().rise );
        this.run = new Property<Double>( interactiveLine.get().run );
        this.yIntercept = new Property<Double>( interactiveLine.get().y1 );

        // determine the max width of the rise and run values, based on the extents of their range
        double maxSlopeWidth;
        if ( variableSlope ) {
            PNode maxRiseNode = new RiseSpinnerNode( UserComponents.riseSpinner,
                                                     new Property<Double>( riseRange.get().getMax() ), new Property<Double>( runRange.get().getMax() ), riseRange,
                                                     new SlopeColors(), interactiveFont, FORMAT );
            PNode minRiseNode = new RiseSpinnerNode( UserComponents.riseSpinner,
                                                     new Property<Double>( riseRange.get().getMin() ), new Property<Double>( runRange.get().getMax() ), riseRange,
                                                     new SlopeColors(), interactiveFont, FORMAT );
            double maxRiseWidth = Math.max( maxRiseNode.getFullBoundsReference().getWidth(), minRiseNode.getFullBoundsReference().getWidth() );
            PNode maxRunNode = new RunSpinnerNode( UserComponents.riseSpinner,
                                                   new Property<Double>( riseRange.get().getMax() ), new Property<Double>( runRange.get().getMax() ), runRange,
                                                   new SlopeColors(), interactiveFont, FORMAT );
            PNode minRunNode = new RunSpinnerNode( UserComponents.riseSpinner,
                                                   new Property<Double>( riseRange.get().getMax() ), new Property<Double>( runRange.get().getMin() ), runRange,
                                                   new SlopeColors(), interactiveFont, FORMAT );
            double maxRunWidth = Math.max( maxRunNode.getFullBoundsReference().getWidth(), minRunNode.getFullBoundsReference().getWidth() );
            maxSlopeWidth = Math.max( maxRiseWidth, maxRunWidth );
        }
        else {
            PNode minRiseNode = new DynamicValueNode( new Property<Double>( riseRange.get().getMin() ), interactiveFont, staticColor );
            PNode maxRiseNode = new DynamicValueNode( new Property<Double>( riseRange.get().getMax() ), interactiveFont, staticColor );
            double maxRiseWidth = Math.max( maxRiseNode.getFullBoundsReference().getWidth(), minRiseNode.getFullBoundsReference().getWidth() );
            PNode minRunNode = new DynamicValueNode( new Property<Double>( runRange.get().getMin() ), interactiveFont, staticColor );
            PNode maxRunNode = new DynamicValueNode( new Property<Double>( runRange.get().getMax() ), interactiveFont, staticColor );
            double maxRunWidth = Math.max( maxRunNode.getFullBoundsReference().getWidth(), minRunNode.getFullBoundsReference().getWidth() );
            maxSlopeWidth = Math.max( maxRiseWidth, maxRunWidth );
        }

        // nodes: y = mx + b
        PNode yNode = new PhetPText( "y", staticFont, staticColor );
        PNode equalsNode = new PhetPText( "=", staticFont, staticColor );
        final PNode riseNode, runNode;
        if ( variableSlope ) {
            riseNode = new ZeroOffsetNode( new RiseSpinnerNode( UserComponents.riseSpinner, this.rise, this.run, riseRange, new SlopeColors(),
                                                                interactiveFont, FORMAT ) );
            runNode = new ZeroOffsetNode( new RunSpinnerNode( UserComponents.runSpinner, this.rise, this.run, runRange, new SlopeColors(),
                                                              interactiveFont, FORMAT ) );
        }
        else {
            riseNode = new DynamicValueNode( rise, interactiveFont, staticColor );
            runNode = new DynamicValueNode( run, interactiveFont, staticColor );
        }
        final PNode lineNode = new PhetPPath( new Line2D.Double( 0, 0, maxSlopeWidth, 0 ), new BasicStroke( 3f ), staticColor );
        PNode xNode = new PhetPText( "x", staticFont, staticColor );
        PNode interceptSignNode = new PhetPText( "+", staticFont, staticColor );
        PNode interceptNode;
        if ( variableIntercept ) {
            interceptNode = new ZeroOffsetNode( new SpinnerNode( UserComponents.interceptSpinner, this.yIntercept, yInterceptRange, new InterceptColors(), interactiveFont, FORMAT ) );
        }
        else {
            interceptNode = new DynamicValueNode( yIntercept, interactiveFont, staticColor );
        }

        // rendering order
        {
            addChild( yNode );
            addChild( equalsNode );
            addChild( riseNode );
            addChild( lineNode );
            addChild( runNode );
            addChild( xNode );
            addChild( interceptSignNode );
            addChild( interceptNode );
        }

        // layout
        {
            final double xSpacing = 10;
            final double ySpacing = 6;
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + xSpacing,
                                  yNode.getYOffset() );
            lineNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing,
                                equalsNode.getFullBoundsReference().getCenterY() + 2 );
            riseNode.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( riseNode.getFullBoundsReference().getWidth() / 2 ),
                                lineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - ySpacing );
            runNode.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( runNode.getFullBoundsReference().getWidth() / 2 ),
                               lineNode.getFullBoundsReference().getMinY() + ySpacing );
            xNode.setOffset( lineNode.getFullBoundsReference().getMaxX() + xSpacing,
                             yNode.getYOffset() );
            interceptSignNode.setOffset( xNode.getFullBoundsReference().getMaxX() + xSpacing,
                                         xNode.getYOffset() );
            interceptNode.setOffset( interceptSignNode.getFullBoundsReference().getMaxX() + xSpacing,
                                     xNode.getFullBoundsReference().getCenterY() - ( interceptNode.getFullBoundsReference().getHeight() / 2 ) );
        }

        // undefined-slope indicator, added after everything else
        final PNode undefinedSlopeIndicator = new UndefinedSlopeIndicator( getFullBoundsReference().getWidth(), getFullBoundsReference().getHeight() );
        undefinedSlopeIndicator.setOffset( getFullBoundsReference().getCenterX() - ( undefinedSlopeIndicator.getFullBoundsReference().getWidth() / 2 ),
                                           lineNode.getFullBoundsReference().getCenterY() - ( undefinedSlopeIndicator.getFullBoundsReference().getHeight() / 2 ) + 2 );
        addChild( undefinedSlopeIndicator );

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
            public void apply( Line line ) {
                assert ( line.x1 == 0 ); // line is in slope-intercept form

                // Atomically synchronize the controls.
                updatingControls = true;
                {
                    rise.set( line.rise );
                    run.set( line.run );
                    yIntercept.set( line.y1 );
                }
                updatingControls = false;

                // center align rise and run, if they are not interactive
                if ( !variableSlope ) {
                    riseNode.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( riseNode.getFullBoundsReference().getWidth() / 2 ),
                                        riseNode.getYOffset() );
                    runNode.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( runNode.getFullBoundsReference().getWidth() / 2 ),
                                       runNode.getYOffset() );
                }

                // Make the undefined-slope indicator visible for line with undefined slope.
                undefinedSlopeIndicator.setVisible( line.run == 0 );
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

        // canvas
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 600, 400 ) );
        canvas.getLayer().addChild( equationNode1 );
        canvas.getLayer().addChild( equationNode2 );
        canvas.getLayer().addChild( equationNode3 );

        // layout
        equationNode1.setOffset( 100, 50 );
        equationNode2.setOffset( equationNode1.getXOffset(), equationNode1.getFullBoundsReference().getMaxY() + 40 );
        equationNode3.setOffset( equationNode1.getXOffset(), equationNode2.getFullBoundsReference().getMaxY() + 60 );

        // frame
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
