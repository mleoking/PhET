// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

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
import edu.colorado.phet.linegraphing.common.view.SpinnerNode.RiseSpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SpinnerNode.RunSpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SpinnerNode.X1SpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SpinnerNode.Y1SpinnerNode;
import edu.colorado.phet.linegraphing.pointslope.model.PointSlopeModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Interface for manipulating a point-slope equation.
 * This version uses spinner buttons to increment/decrement rise, run, x1 and y1.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class PointSlopeEquationNode extends PhetPNode {

    private static final NumberFormat FORMAT = new DefaultDecimalFormat( "0" );

    private final Property<Double> rise, run, x1, y1;

    public PointSlopeEquationNode( final Property<StraightLine> interactiveLine,
                                   Property<DoubleRange> riseRange,
                                   Property<DoubleRange> runRange,
                                   Property<DoubleRange> x1Range,
                                   Property<DoubleRange> y1Range ) {
        this( interactiveLine, riseRange, runRange, x1Range, y1Range,
              LGConstants.INTERACTIVE_EQUATION_FONT, LGConstants.STATIC_EQUATION_FONT, LGColors.STATIC_EQUATION_ELEMENT );
    }

    private PointSlopeEquationNode( final Property<StraightLine> interactiveLine,
                                    Property<DoubleRange> riseRange,
                                    Property<DoubleRange> runRange,
                                    Property<DoubleRange> x1Range,
                                    Property<DoubleRange> y1Range,
                                    PhetFont interactiveFont,
                                    PhetFont staticFont,
                                    Color staticColor ) {

        this.rise = new Property<Double>( interactiveLine.get().rise );
        this.run = new Property<Double>( interactiveLine.get().run );
        this.x1 = new Property<Double>( interactiveLine.get().x1 );
        this.y1 = new Property<Double>( interactiveLine.get().y1 );

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

        // nodes: (y-y1) = m(x-x1)
        PNode yLeftParenNode = new PhetPText( "(", staticFont, staticColor );
        PNode yNode = new PhetPText( "y", staticFont, staticColor );
        PNode y1SignNode = new PhetPText( "-", staticFont, staticColor );
        PNode y1Node = new ZeroOffsetNode( new Y1SpinnerNode( UserComponents.y1Spinner, this.y1, y1Range, interactiveFont, FORMAT ) );
        PNode yRightParenNode = new PhetPText( ")", staticFont, staticColor );
        PNode equalsNode = new PhetPText( "=", staticFont, staticColor );
        PNode riseNode = new ZeroOffsetNode( new RiseSpinnerNode( UserComponents.riseSpinner, this.rise, riseRange, interactiveFont, FORMAT ) );
        PNode runNode = new ZeroOffsetNode( new RunSpinnerNode( UserComponents.runSpinner, this.run, runRange, interactiveFont, FORMAT ) );
        PNode lineNode = new PhetPPath( new Line2D.Double( 0, 0, maxSlopeWidth, 0 ), new BasicStroke( 3f ), staticColor );
        PNode xLeftParenNode = new PhetPText( "(", staticFont, staticColor );
        PNode xNode = new PhetPText( "x", staticFont, staticColor );
        PNode x1SignNode = new PhetPText( "-", staticFont, staticColor );
        PNode x1Node = new ZeroOffsetNode( new X1SpinnerNode( UserComponents.x1Spinner, this.x1, x1Range, interactiveFont, FORMAT ) );
        PNode xRightParenNode = new PhetPText( ")", staticFont, staticColor );

        // rendering order
        {
            addChild( yLeftParenNode );
            addChild( yNode );
            addChild( y1SignNode );
            addChild( y1Node );
            addChild( yRightParenNode );
            addChild( equalsNode );
            addChild( riseNode );
            addChild( lineNode );
            addChild( runNode );
            addChild( xLeftParenNode );
            addChild( xNode );
            addChild( x1SignNode );
            addChild( x1Node );
            addChild( xRightParenNode );
        }

        // layout
        {
            final double xSpacing = 5;
            final double xParenSpacing = 2;
            final double ySpacing = 6;
            yLeftParenNode.setOffset( 0, 0 );
            yNode.setOffset( yLeftParenNode.getFullBoundsReference().getMaxX() + xParenSpacing,
                             yLeftParenNode.getYOffset() );
            y1SignNode.setOffset( yNode.getFullBoundsReference().getMaxX() + xSpacing,
                                  yNode.getYOffset() );
            y1Node.setOffset( y1SignNode.getFullBoundsReference().getMaxX() + xSpacing,
                              yNode.getFullBoundsReference().getCenterY() - ( y1Node.getFullBoundsReference().getHeight() / 2 ) );
            yRightParenNode.setOffset( y1Node.getFullBoundsReference().getMaxX() + xParenSpacing,
                                       yNode.getYOffset() );
            equalsNode.setOffset( yRightParenNode.getFullBoundsReference().getMaxX() + xSpacing,
                                  yNode.getYOffset() );
            lineNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing,
                                equalsNode.getFullBoundsReference().getCenterY() + 2 );
            riseNode.setOffset( lineNode.getFullBoundsReference().getMaxX() - riseNode.getFullBoundsReference().getWidth(),
                                lineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - ySpacing );
            runNode.setOffset( lineNode.getFullBoundsReference().getMaxX() - runNode.getFullBoundsReference().getWidth(),
                               lineNode.getFullBoundsReference().getMinY() + ySpacing );
            xLeftParenNode.setOffset( lineNode.getFullBoundsReference().getMaxX() + xSpacing,
                                      yNode.getYOffset() );
            xNode.setOffset( xLeftParenNode.getFullBoundsReference().getMaxX() + xParenSpacing,
                             yNode.getYOffset() );
            x1SignNode.setOffset( xNode.getFullBoundsReference().getMaxX() + xSpacing,
                                  xNode.getYOffset() );
            x1Node.setOffset( x1SignNode.getFullBoundsReference().getMaxX() + xSpacing,
                              xNode.getFullBoundsReference().getCenterY() - ( x1Node.getFullBoundsReference().getHeight() / 2 ) );
            xRightParenNode.setOffset( x1Node.getFullBoundsReference().getMaxX() + xParenSpacing,
                                       yNode.getYOffset() );
        }

        // sync the model with the controls
        RichSimpleObserver lineUpdater = new RichSimpleObserver() {
            @Override public void update() {
                interactiveLine.set( new StraightLine( rise.get(), run.get(), x1.get(), y1.get(), interactiveLine.get().color, interactiveLine.get().highlightColor ) );
            }
        };
        lineUpdater.observe( rise, run, x1, y1 );

        // sync the controls with the model
        interactiveLine.addObserver( new VoidFunction1<StraightLine>() {
            public void apply( StraightLine line ) {
                rise.set( line.rise );
                run.set( line.run );
                x1.set( line.x1 );
                y1.set( line.y1 );
            }
        } );
    }

    // test
    public static void main( String[] args ) {

        // model
        PointSlopeModel model = new PointSlopeModel();

        // equation
        PointSlopeEquationNode equationNode =
                new PointSlopeEquationNode( model.interactiveLine, model.riseRange, model.runRange, model.x1Range, model.y1Range );
        equationNode.setOffset( 50, 100 );

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
