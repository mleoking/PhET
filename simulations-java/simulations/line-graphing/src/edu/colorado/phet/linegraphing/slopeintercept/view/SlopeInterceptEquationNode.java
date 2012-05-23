// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import edu.colorado.phet.common.piccolophet.nodes.OutlineTextNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.colorado.phet.linegraphing.common.view.NumberPickerNode.InterceptPickerNode;
import edu.colorado.phet.linegraphing.common.view.NumberPickerNode.SlopePickerNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Interface for manipulating a source-intercept equation.
 * This version uses Ariel Paul's "number picker" user-interface component instead of spinner buttons.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SlopeInterceptEquationNode extends PhetPNode {

    private static final NumberFormat FORMAT = new DefaultDecimalFormat( "0" );
    private static final double SIGN_STROKE_WIDTH = 1;
    private static final Color SIGN_STROKE_COLOR = Color.BLACK;

    private final Property<Double> rise, run, intercept;

    public SlopeInterceptEquationNode( final Property<StraightLine> interactiveLine,
                                       Property<DoubleRange> riseRange,
                                       Property<DoubleRange> runRange,
                                       Property<DoubleRange> interceptRange,
                                       PhetFont font ) {

        this.rise = new Property<Double>( interactiveLine.get().rise );
        this.run = new Property<Double>( interactiveLine.get().run );
        this.intercept = new Property<Double>( interactiveLine.get().yIntercept );

        // determine the max width of the rise and run spinners, based on the extents of their range
        double maxSlopeWidth;
        {
            PNode maxRiseNode = new SlopePickerNode( UserComponents.riseSpinner, new Property<Double>( Math.abs( riseRange.get().getMax() ) ), riseRange, font, FORMAT, true );
            PNode minRiseNode = new SlopePickerNode( UserComponents.riseSpinner, new Property<Double>( Math.abs( riseRange.get().getMin() ) ), riseRange, font, FORMAT, true );
            double maxRiseWidth = Math.max( maxRiseNode.getFullBoundsReference().getWidth(), minRiseNode.getFullBoundsReference().getWidth() );
            PNode maxRunNode = new SlopePickerNode( UserComponents.riseSpinner, new Property<Double>( Math.abs( runRange.get().getMax() ) ), runRange, font, FORMAT, true );
            PNode minRunNode = new SlopePickerNode( UserComponents.riseSpinner, new Property<Double>( Math.abs( runRange.get().getMin() ) ), runRange, font, FORMAT, true );
            double maxRunWidth = Math.max( maxRunNode.getFullBoundsReference().getWidth(), minRunNode.getFullBoundsReference().getWidth() );
            maxSlopeWidth = Math.max( maxRiseWidth, maxRunWidth );
        }

        // y = (rise/run)x + b
        PText yNode = new PhetPText( "y", font );
        PText equalsNode = new PhetPText( "=", font );
        final OutlineTextNode riseSignNode = new OutlineTextNode( "-", font, LGColors.SLOPE, SIGN_STROKE_COLOR, SIGN_STROKE_WIDTH );
        final OutlineTextNode runSignNode = new OutlineTextNode( "-", font, LGColors.SLOPE, SIGN_STROKE_COLOR, SIGN_STROKE_WIDTH );
        PNode riseNode = new ZeroOffsetNode( new SlopePickerNode( UserComponents.riseSpinner, rise, riseRange, font, FORMAT, true ) );
        PNode runNode = new ZeroOffsetNode( new SlopePickerNode( UserComponents.runSpinner, run, runRange, font, FORMAT, true ) );
        final PPath lineNode = new PPath( new Line2D.Double( 0, 0, maxSlopeWidth, 0 ) ) {{
            setStroke( new BasicStroke( 2f ) );
        }};
        PText xNode = new PhetPText( "x", font );
        final OutlineTextNode interceptSignNode = new OutlineTextNode( "-", font, LGColors.INTERCEPT, SIGN_STROKE_COLOR, SIGN_STROKE_WIDTH );
        PNode interceptNode = new ZeroOffsetNode( new InterceptPickerNode( UserComponents.interceptSpinner, intercept, interceptRange, font, FORMAT, true ) );

        // rendering order
        {
            addChild( yNode );
            addChild( equalsNode );
            addChild( riseSignNode );
            addChild( runSignNode );
            addChild( riseNode );
            addChild( runNode );
            addChild( lineNode );
            addChild( xNode );
            addChild( interceptSignNode );
            addChild( interceptNode );
        }

        // layout
        {
            // NOTE: x spacing varies and was tweaked to look good
            final double ySpacing = 6;
            final double yFudgeFactor = 3; // fudge factor to align fraction dividing line with the center of the equals sign, visually tweaked
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + 10,
                                  yNode.getYOffset() );
            riseSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX(),
                                    equalsNode.getFullBoundsReference().getCenterY() - ySpacing - ( riseNode.getFullBoundsReference().getHeight() / 2 ) - ( riseSignNode.getFullBoundsReference().getHeight() / 2 ) + yFudgeFactor );
            runSignNode.setOffset( riseSignNode.getXOffset(),
                                   equalsNode.getFullBoundsReference().getCenterY() + ySpacing + ( riseNode.getFullBoundsReference().getHeight() / 2 ) - ( runSignNode.getFullBoundsReference().getHeight() / 2 ) + yFudgeFactor );
            lineNode.setOffset( riseSignNode.getFullBoundsReference().getMaxX() + 2,
                                equalsNode.getFullBoundsReference().getCenterY() + yFudgeFactor );
            riseNode.setOffset( lineNode.getFullBoundsReference().getMaxX() - riseNode.getFullBoundsReference().getWidth(),
                                lineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - ySpacing );
            runNode.setOffset( lineNode.getFullBoundsReference().getMaxX() - runNode.getFullBoundsReference().getWidth(),
                               lineNode.getFullBoundsReference().getMinY() + ySpacing );
            xNode.setOffset( lineNode.getFullBoundsReference().getMaxX() + 15,
                             yNode.getYOffset() );
            interceptSignNode.setOffset( xNode.getFullBoundsReference().getMaxX() + 10,
                                         lineNode.getFullBoundsReference().getCenterY() - ( interceptSignNode.getFullBoundsReference().getHeight() / 2 ) );
            interceptNode.setOffset( interceptSignNode.getFullBoundsReference().getMaxX() + 10,
                                     interceptSignNode.getFullBoundsReference().getCenterY() - ( interceptNode.getFullBoundsReference().getHeight() / 2 ) );
        }

        // sync the model with the controls
        RichSimpleObserver lineUpdater = new RichSimpleObserver() {
            @Override public void update() {
                interactiveLine.set( new StraightLine( rise.get(), run.get(), intercept.get(), interactiveLine.get().color, interactiveLine.get().highlightColor ) );
            }
        };
        lineUpdater.observe( rise, run, intercept );

        // sync the controls with the model
        interactiveLine.addObserver( new VoidFunction1<StraightLine>() {
            public void apply( StraightLine line ) {

                // values
                rise.set( line.rise );
                run.set( line.run );
                intercept.set( line.yIntercept );

                // signs
                riseSignNode.setText( line.rise < 0 ? "-" : " " );
                runSignNode.setText( line.run < 0 ? "-" : " " );
                interceptSignNode.setText( line.yIntercept < 0 ? "-" : "+" ); // intercept shows '+' sign for positive values
            }
        } );
    }

    // test
    public static void main( String[] args ) {

        // properties
        Property<StraightLine> interactiveLine = new Property<StraightLine>( new StraightLine( 1, 1, 1, Color.RED, Color.RED ) );
        Property<DoubleRange> riseRange = new Property<DoubleRange>( new DoubleRange( -10, 10 ) );
        Property<DoubleRange> runRange = new Property<DoubleRange>( new DoubleRange( -10, 10 ) );
        Property<DoubleRange> interceptRange = new Property<DoubleRange>( new DoubleRange( -10, 10 ) );

        // equation
        SlopeInterceptEquationNode equationNode = new SlopeInterceptEquationNode( interactiveLine,riseRange,runRange,interceptRange, new PhetFont( Font.BOLD, 38 ));
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
