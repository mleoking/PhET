// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.geom.Line2D;
import java.text.MessageFormat;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.EquationFactory;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Factory that creates line equations in point-slope form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeEquationFactory extends EquationFactory {

    public EquationNode createNode( Line line, PhetFont font ) {
        if ( line.run == 0 ) {
            return new UndefinedSlopeNode( line, font );
        }
        else if ( line.rise == 0 ) {
            return new ZeroSlopeNode( line, font );
        }
        else if ( Math.abs( line.rise ) == Math.abs( line.run ) ) {
            return new UnitSlopeNode( line, font );
        }
        else if ( Math.abs( line.run ) == 1 ) {
            return new IntegerSlopeNode( line, font );
        }
        else {
            return new FractionSlopeNode( line, font );
        }
    }

    // Verbose form of point-slope, not reduced, for debugging.
    private static class VerboseNode extends EquationNode {
        public VerboseNode( Line line, PhetFont font ) {
            addChild( new PhetPText( MessageFormat.format( "(y - {0}) = ({1}/{2})(x - {3})", line.y1, line.rise, line.run, line.x1 ), font, line.color ) );
        }
    }

    /*
     * Forms when slope is zero.
     * y = y1
     */
    private static class ZeroSlopeNode extends EquationNode {

        public ZeroSlopeNode( Line line, PhetFont font ) {

            assert ( line.rise == 0 );

            // y = y1
            PNode yNode = new PhetPText( Strings.SYMBOL_Y, font, line.color );
            PNode equalsNode = new PhetPText( "=", font, line.color );
            PNode y1Node = new PhetPText( toIntString( line.y1 ), font, line.color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            addChild( y1Node );

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            y1Node.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
        }
    }

    /*
     * Forms where absolute slope is 1.
     * (y - y1) = (x - x1)
     * (y - y1) = -(x - x1)
     */
    private static class UnitSlopeNode extends EquationNode {

        public UnitSlopeNode( Line line, PhetFont font ) {

            assert ( Math.abs( line.rise ) == Math.abs( line.run ) );

            PNode yNode = new PhetPText( getYText( line.y1 ), font, line.color );
            PNode equalsNode = new PhetPText( "=", font, line.color );
            String slopeText = ( line.rise * line.run >= 0 ) ? "" : "-";
            PNode xNode = new PhetPText( slopeText + getXText( line.x1 ), font, line.color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            addChild( xNode );

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            xNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
        }
    }

    /*
     * Forms where the slope is an integer.
     * (y - y1) = m(x - x1)
     * (y - y1) = -m(x - x1)
     */
    private static class IntegerSlopeNode extends EquationNode {

        public IntegerSlopeNode( Line line, PhetFont font ) {

            assert ( Math.abs( line.run ) == 1 );

            PNode yNode = new PhetPText( getYText( line.y1 ), font, line.color );
            PNode equalsNode = new PhetPText( "=", font, line.color );
            PNode slopeNode = new PhetPText( toIntString( line.rise / line.run ), font, line.color );
            PNode xNode = new PhetPText( getXText( line.x1 ), font, line.color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            addChild( slopeNode );
            addChild( xNode );

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            slopeNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            xNode.setOffset( slopeNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
        }
    }

    /*
    * Forms where the slope is a fraction.
    * (y - y1) = (rise/run)(x - x1)
    * (y - y1) = -(rise/run)(x - x1)
    */
    private static class FractionSlopeNode extends EquationNode {

        public FractionSlopeNode( Line line, PhetFont font ) {

            final boolean slopeIsPositive = ( line.rise * line.run ) >= 0;

            // y = -(reducedRise/reducedRun)x + b
            PNode yNode = new PhetPText( getYText( line.y1 ), font, line.color );
            PNode equalsNode = new PhetPText( "=", font, line.color );
            PNode slopeSignNode = new PhetPText( slopeIsPositive ? "" : "-", font, line.color );
            PNode riseNode = new PhetPText( toIntString( Math.abs( line.rise ) ), font, line.color );
            PNode runNode = new PhetPText( toIntString( Math.abs( line.run ) ), font, line.color );
            PNode lineNode = new PhetPPath( new Line2D.Double( 0, 0, Math.max( riseNode.getFullBoundsReference().getWidth(), runNode.getFullBoundsReference().getHeight() ), 0 ), new BasicStroke( FRACTION_LINE_WIDTH ), line.color );
            PNode xNode = new PhetPText( getXText( line.x1 ), font, line.color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            if ( !slopeIsPositive ) {
                addChild( slopeSignNode );
            }
            addChild( riseNode );
            addChild( lineNode );
            addChild( runNode );
            addChild( xNode );

            // layout
            final double yFudgeFactor = 2; // fudge factor to align fraction dividing line with the center of the equals sign, visually tweaked
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            if ( !slopeIsPositive ) {
                slopeSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                         equalsNode.getYOffset() - ( slopeSignNode.getFullBoundsReference().getHeight() / 2 ) + yFudgeFactor );
                lineNode.setOffset( slopeSignNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                    equalsNode.getFullBoundsReference().getCenterY() - ( lineNode.getFullBoundsReference().getHeight() / 2 ) + yFudgeFactor );
            }
            else {
                lineNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                    equalsNode.getFullBoundsReference().getCenterY() - ( lineNode.getFullBoundsReference().getHeight() / 2 ) + yFudgeFactor );
            }
            riseNode.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( riseNode.getFullBoundsReference().getWidth() / 2 ),
                                lineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - Y_SPACING );
            runNode.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( runNode.getFullBoundsReference().getWidth() / 2 ),
                               lineNode.getFullBoundsReference().getMaxY() + Y_SPACING );
            xNode.setOffset( lineNode.getFullBoundsReference().getMaxX() + X_SPACING,
                             yNode.getYOffset() );
        }
    }

    // (x-x1)
    private static String getXText( double x1 ) {
        if ( x1 == 0 ) {
            return MessageFormat.format( "{0}", Strings.SYMBOL_X );
        }
        else {
            return MessageFormat.format( "({0}{1}{2})", Strings.SYMBOL_X, x1 < 0 ? "+" : "-", Math.abs( x1 ) );
        }
    }

    // (y-y1)
    private static String getYText( double y1 ) {
        if ( y1 == 0 ) {
            return MessageFormat.format( "{0}", Strings.SYMBOL_Y );
        }
        else {
            return MessageFormat.format( "({0}{1}{2})", Strings.SYMBOL_Y, y1 < 0 ? "+" : "-", Math.abs( y1 ) );
        }
    }

    // test
    public static void main( String[] args ) {

        // factory
        PointSlopeEquationFactory factory = new PointSlopeEquationFactory();
        PhetFont font = new PhetFont( 24 );

        // nodes
        PNode undefinedSlopeNode = factory.createNode( Line.createPointSlope( 1, 2, 1, 0 ), font );
        PNode positiveZeroSlopeNode = factory.createNode( Line.createPointSlope( 1, 2, 0, 1 ), font );
        PNode negativeZeroSlopeNode = factory.createNode( Line.createPointSlope( -1, -2, 0, 1 ), font );
        PNode positiveUnitSlopeNode = factory.createNode( Line.createPointSlope( 1, 2, 3, 3 ), font );
        PNode negativeUnitSlopeNode = factory.createNode( Line.createPointSlope( -1, -2, -3, 3 ), font );
        PNode positiveIntegerSlopeNode = factory.createNode( Line.createPointSlope( 1, 2, 3, 1 ), font );
        PNode negativeIntegerSlopeNode = factory.createNode( Line.createPointSlope( -1, -2, -3, 1 ), font );
        PNode positiveFractionSlopeNode = factory.createNode( Line.createPointSlope( 1, 2, 3, 5 ), font );
        PNode negativeFractionSlopeNode = factory.createNode( Line.createPointSlope( -1, -2, -3, 5 ), font );

        // layout
        final double xOffset = 50;
        final double ySpacing = 15;
        undefinedSlopeNode.setOffset( xOffset, 50 );
        positiveZeroSlopeNode.setOffset( xOffset, undefinedSlopeNode.getFullBoundsReference().getMaxY() + ySpacing );
        negativeZeroSlopeNode.setOffset( xOffset, positiveZeroSlopeNode.getFullBoundsReference().getMaxY() + ySpacing );
        positiveUnitSlopeNode.setOffset( xOffset, negativeZeroSlopeNode.getFullBoundsReference().getMaxY() + ySpacing );
        negativeUnitSlopeNode.setOffset( xOffset, positiveUnitSlopeNode.getFullBoundsReference().getMaxY() + ySpacing );
        positiveIntegerSlopeNode.setOffset( xOffset, negativeUnitSlopeNode.getFullBoundsReference().getMaxY() + ySpacing );
        negativeIntegerSlopeNode.setOffset( xOffset, positiveIntegerSlopeNode.getFullBoundsReference().getMaxY() + ySpacing );
        positiveFractionSlopeNode.setOffset( xOffset, negativeIntegerSlopeNode.getFullBoundsReference().getMaxY() + ySpacing );
        negativeFractionSlopeNode.setOffset( xOffset, positiveFractionSlopeNode.getFullBoundsReference().getMaxY() + ySpacing );

        // canvas
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 800, 600 ) );
        canvas.getLayer().addChild( undefinedSlopeNode );
        canvas.getLayer().addChild( positiveZeroSlopeNode );
        canvas.getLayer().addChild( negativeZeroSlopeNode );
        canvas.getLayer().addChild( positiveUnitSlopeNode );
        canvas.getLayer().addChild( negativeUnitSlopeNode );
        canvas.getLayer().addChild( positiveIntegerSlopeNode );
        canvas.getLayer().addChild( negativeIntegerSlopeNode );
        canvas.getLayer().addChild( positiveFractionSlopeNode );
        canvas.getLayer().addChild( negativeFractionSlopeNode );

        // frame
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
