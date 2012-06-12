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
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.colorado.phet.linegraphing.common.view.SpinnerNode.InterceptSpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SpinnerNode.RiseSpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SpinnerNode.RunSpinnerNode;
import edu.colorado.phet.linegraphing.slopeintercept.model.SlopeInterceptModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Interface for manipulating a slope-intercept equation.
 * This version uses spinner buttons to increment/decrement rise, run and intercept.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SlopeInterceptEquationNode extends PhetPNode {

    private static final NumberFormat FORMAT = new DefaultDecimalFormat( "0" );

    private final Property<Double> rise, run, yIntercept;

    public SlopeInterceptEquationNode( final Property<StraightLine> interactiveLine,
                                       Property<DoubleRange> riseRange,
                                       Property<DoubleRange> runRange,
                                       Property<DoubleRange> yInterceptRange ) {
        this( interactiveLine, riseRange, runRange, yInterceptRange,
              LGConstants.INTERACTIVE_EQUATION_FONT, LGConstants.STATIC_EQUATION_FONT, LGColors.STATIC_EQUATION_ELEMENT );
    }

    private SlopeInterceptEquationNode( final Property<StraightLine> interactiveLine,
                                        Property<DoubleRange> riseRange,
                                        Property<DoubleRange> runRange,
                                        Property<DoubleRange> yInterceptRange,
                                        PhetFont interactiveFont,
                                        PhetFont staticFont,
                                        Color staticColor ) {

        this.rise = new Property<Double>( interactiveLine.get().rise );
        this.run = new Property<Double>( interactiveLine.get().run );
        this.yIntercept = new Property<Double>( interactiveLine.get().yIntercept );

        // determine the max width of the rise and run spinners, based on the extents of their range
        double maxSlopeWidth;
        {
            PNode maxRiseNode = new RiseSpinnerNode( UserComponents.riseSpinner, new Property<Double>( riseRange.get().getMax() ), riseRange, interactiveFont, FORMAT );
            PNode minRiseNode = new RiseSpinnerNode( UserComponents.riseSpinner, new Property<Double>( riseRange.get().getMin() ), riseRange, interactiveFont, FORMAT );
            double maxRiseWidth = Math.max( maxRiseNode.getFullBoundsReference().getWidth(), minRiseNode.getFullBoundsReference().getWidth() );
            PNode maxRunNode = new RunSpinnerNode( UserComponents.riseSpinner, new Property<Double>( runRange.get().getMax() ), runRange, interactiveFont, FORMAT );
            PNode minRunNode = new RunSpinnerNode( UserComponents.riseSpinner, new Property<Double>( runRange.get().getMin() ), runRange, interactiveFont, FORMAT );
            double maxRunWidth = Math.max( maxRunNode.getFullBoundsReference().getWidth(), minRunNode.getFullBoundsReference().getWidth() );
            maxSlopeWidth = Math.max( maxRiseWidth, maxRunWidth );
        }

        // nodes: y = mx + b
        PNode yNode = new PhetPText( "y", staticFont, staticColor );
        PNode equalsNode = new PhetPText( "=", staticFont, staticColor );
        PNode riseNode = new ZeroOffsetNode( new RiseSpinnerNode( UserComponents.riseSpinner, this.rise, riseRange, interactiveFont, FORMAT ) );
        PNode runNode = new ZeroOffsetNode( new RunSpinnerNode( UserComponents.runSpinner, this.run, runRange, interactiveFont, FORMAT ) );
        PNode lineNode = new PhetPPath( new Line2D.Double( 0, 0, maxSlopeWidth, 0 ), new BasicStroke( 3f ), staticColor );
        PNode xNode = new PhetPText( "x", staticFont, staticColor );
        PNode interceptSignNode = new PhetPText( "+", staticFont, staticColor );
        PNode interceptNode = new ZeroOffsetNode( new InterceptSpinnerNode( UserComponents.interceptSpinner, this.yIntercept, yInterceptRange, interactiveFont, FORMAT ) );

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
            riseNode.setOffset( lineNode.getFullBoundsReference().getMaxX() - riseNode.getFullBoundsReference().getWidth(),
                                lineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - ySpacing );
            runNode.setOffset( lineNode.getFullBoundsReference().getMaxX() - runNode.getFullBoundsReference().getWidth(),
                               lineNode.getFullBoundsReference().getMinY() + ySpacing );
            xNode.setOffset( lineNode.getFullBoundsReference().getMaxX() + xSpacing,
                             yNode.getYOffset() );
            interceptSignNode.setOffset( xNode.getFullBoundsReference().getMaxX() + xSpacing,
                                         xNode.getYOffset() );
            interceptNode.setOffset( interceptSignNode.getFullBoundsReference().getMaxX() + xSpacing,
                                     xNode.getFullBoundsReference().getCenterY() - ( interceptNode.getFullBoundsReference().getHeight() / 2 ) );
        }

        // sync the model with the controls
        RichSimpleObserver lineUpdater = new RichSimpleObserver() {
            @Override public void update() {
                interactiveLine.set( new StraightLine( rise.get(), run.get(), yIntercept.get(), interactiveLine.get().color, interactiveLine.get().highlightColor ) );
            }
        };
        lineUpdater.observe( rise, run, yIntercept );

        // sync the controls with the model
        interactiveLine.addObserver( new VoidFunction1<StraightLine>() {
            public void apply( StraightLine line ) {
                rise.set( line.rise );
                run.set( line.run );
                yIntercept.set( line.yIntercept );
            }
        } );
    }

    // test
    public static void main( String[] args ) {

        // model
        SlopeInterceptModel model = new SlopeInterceptModel();

        // equation
        SlopeInterceptEquationNode equationNode =
                new SlopeInterceptEquationNode( model.interactiveLine, model.riseRange, model.runRange, model.interceptRange );
        equationNode.setOffset( 100, 100 );

        // canvas
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 600, 400 ) );
        canvas.getLayer().addChild( equationNode );

        // frame
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
