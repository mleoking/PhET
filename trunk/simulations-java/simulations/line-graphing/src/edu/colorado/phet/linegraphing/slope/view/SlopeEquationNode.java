// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slope.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.common.view.MinusNode;
import edu.colorado.phet.linegraphing.common.view.NumberBackgroundNode;
import edu.colorado.phet.linegraphing.common.view.UndefinedSlopeIndicator;
import edu.colorado.phet.linegraphing.common.view.spinner.CoordinateSpinnerNode;
import edu.colorado.phet.linegraphing.common.view.spinner.SpinnerStateIndicator.X1Y1Colors;
import edu.colorado.phet.linegraphing.common.view.spinner.SpinnerStateIndicator.X2Y2Colors;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Renderer for slope equations.
 * General form is m = (y2 - y1) / (x2 - x1) = rise/run
 * <p>
 * x1, y1, x2, and y2 are all interactive.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeEquationNode extends EquationNode {

    private final NumberFormat FORMAT = new DefaultDecimalFormat( "0" );

    private boolean updatingControls; // flag that allows us to update all controls atomically when the model changes
    private PNode unsimplifiedRiseNode, unsimplifiedRunNode;

    /*
     * Use this constructor for interactive equations.
     * x1, y1, x2 and y2 are spinners.
     */
    public SlopeEquationNode( final Property<Line> interactiveLine,
                              Property<DoubleRange> xRange,
                              Property<DoubleRange> yRange,
                              PhetFont interactiveFont,
                              final PhetFont staticFont,
                              final Color staticColor ) {
        super( staticFont.getSize() );

        // properties that are connected to spinners
        final Property<Double> x1 = new Property<Double>( interactiveLine.get().x1 );
        final Property<Double> y1 = new Property<Double>( interactiveLine.get().y1 );
        final Property<Double> x2 = new Property<Double>( interactiveLine.get().x2 );
        final Property<Double> y2 = new Property<Double>( interactiveLine.get().y2 );

        // Nodes that could appear is all possible ways to write the equation
        // m =
        PNode mNode = new PhetPText( Strings.SYMBOL_SLOPE, staticFont, staticColor );
        PNode interactiveEqualsNode = new PhetPText( "=", staticFont, staticColor );
        // y2 - y2
        PNode y2Node = new ZeroOffsetNode( new CoordinateSpinnerNode( UserComponents.y2Spinner, y2, x2, y1, x1, yRange, new X2Y2Colors(), interactiveFont, FORMAT ) );
        PNode numeratorOperatorNode = new PhetPText( "-", staticFont, staticColor );
        PNode y1Node = new ZeroOffsetNode( new CoordinateSpinnerNode( UserComponents.y1Spinner, y1, x1, y2, x2, yRange, new X1Y1Colors(), interactiveFont, FORMAT ) );
        // fraction line
        PPath interactiveFractionLineNode = new PhetPPath( createFractionLineShape( 10 ), staticColor, null, null ); // correct length will be set later
        // x2 - x1
        PNode x2Node = new ZeroOffsetNode( new CoordinateSpinnerNode( UserComponents.x2Spinner, x2, y2, x1, y1, xRange, new X2Y2Colors(), interactiveFont, FORMAT ) );
        PNode denominatorOperatorNode = new PhetPText( "-", staticFont, staticColor );
        PNode x1Node = new ZeroOffsetNode( new CoordinateSpinnerNode( UserComponents.x1Spinner, x1, y1, x2, y2, xRange, new X1Y1Colors(), interactiveFont, FORMAT ) );
        // = unsimplified value
        final PNode unsimplifiedEqualsNode = new PhetPText( "=", staticFont, staticColor );
        unsimplifiedRiseNode = new PNode(); // non-null for now, proper node created later
        unsimplifiedRunNode = new PNode();  // non-null for now, proper node created later
        final PPath unsimplifiedFractionLineNode = new PhetPPath( createFractionLineShape( 10 ), staticColor, null, null ); // correct length will be set later

        // Compute the max width needed to display the unsimplified rise and run values.
        final double maxRangeLength = Math.max( xRange.get().getLength(), yRange.get().getLength() );
        final double maxUnsimplifiedWidth = new PhetPText( FORMAT.format( -maxRangeLength ), interactiveFont, staticColor ).getFullBoundsReference().getWidth();

        // rendering order
        final PNode parentNode = new PNode();
        addChild( parentNode );
        {
            // m =
            parentNode.addChild( mNode );
            parentNode.addChild( interactiveEqualsNode );
            // y2 - y1
            parentNode.addChild( y2Node );
            parentNode.addChild( numeratorOperatorNode );
            parentNode.addChild( y1Node );
            // fraction line
            parentNode.addChild( interactiveFractionLineNode );
            // x2 - x1
            parentNode.addChild( x2Node );
            parentNode.addChild( denominatorOperatorNode );
            parentNode.addChild( x1Node );
            // = rise/run
            parentNode.addChild( unsimplifiedEqualsNode );
            parentNode.addChild( unsimplifiedRiseNode );
            parentNode.addChild( unsimplifiedFractionLineNode );
            parentNode.addChild( unsimplifiedRunNode );
        }

        // static layout
        {
            // m =
            mNode.setOffset( 0, 0 );
            interactiveEqualsNode.setOffset( mNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing, mNode.getYOffset() );
            // fraction line
            interactiveFractionLineNode.setOffset( interactiveEqualsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                                   interactiveEqualsNode.getFullBoundsReference().getCenterY() + fractionLineYFudgeFactor );
            // y2 - y1
            y2Node.setOffset( interactiveFractionLineNode.getXOffset(),
                              interactiveFractionLineNode.getFullBoundsReference().getMinY() - y2Node.getFullBoundsReference().getHeight() - spinnersYSpacing );
            numeratorOperatorNode.setOffset( y2Node.getFullBoundsReference().getMaxX() + operatorXSpacing,
                                             y2Node.getFullBoundsReference().getCenterY() - ( numeratorOperatorNode.getFullBoundsReference().getHeight() / 2 ) );
            y1Node.setOffset( numeratorOperatorNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                              y2Node.getYOffset() );
            // fix fraction line length
            final double leftLineLength = y1Node.getFullBoundsReference().getMaxX() - y2Node.getFullBoundsReference().getMinX();
            interactiveFractionLineNode.setPathTo( createFractionLineShape( leftLineLength ) );
            // x2 - x1
            x2Node.setOffset( y2Node.getXOffset(),
                              interactiveFractionLineNode.getFullBoundsReference().getMaxY() + spinnersYSpacing );
            denominatorOperatorNode.setOffset( x2Node.getFullBoundsReference().getMaxX() + operatorXSpacing,
                                               x2Node.getFullBoundsReference().getCenterY() - ( denominatorOperatorNode.getFullBoundsReference().getHeight() / 2 ) );
            x1Node.setOffset( denominatorOperatorNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                              x2Node.getYOffset() );
            // = rise/run
            unsimplifiedEqualsNode.setOffset( interactiveFractionLineNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                              interactiveEqualsNode.getYOffset() );
            unsimplifiedFractionLineNode.setOffset( unsimplifiedEqualsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                                    interactiveFractionLineNode.getYOffset() );
            // all other layout is done dynamically, in updateLayout
        }

        // dynamic layout
        final VoidFunction1<Line> updateLayout = new VoidFunction1<Line>() {
            public void apply( Line line ) {
                // horizontally center rise and run above fraction line
                unsimplifiedRiseNode.setOffset( unsimplifiedFractionLineNode.getFullBoundsReference().getCenterX() - ( unsimplifiedRiseNode.getFullBoundsReference().getWidth() / 2 ),
                                                unsimplifiedFractionLineNode.getFullBoundsReference().getMinY() - unsimplifiedRiseNode.getFullBoundsReference().getHeight() - slopeYSpacing );
                unsimplifiedRunNode.setOffset( unsimplifiedFractionLineNode.getFullBoundsReference().getCenterX() - ( unsimplifiedRunNode.getFullBoundsReference().getWidth() / 2 ),
                                               unsimplifiedFractionLineNode.getFullBoundsReference().getMaxY() + slopeYSpacing );
            }
        };

        // sync the model with the controls
        RichSimpleObserver lineUpdater = new RichSimpleObserver() {
            @Override public void update() {
                if ( !updatingControls ) {
                    interactiveLine.set( new Line( x1.get(), y1.get(), x2.get(), y2.get(), interactiveLine.get().color ) );
                }
            }
        };
        lineUpdater.observe( x1, y1, x2, y2 );

        // sync the controls and layout with the model
        interactiveLine.addObserver( new VoidFunction1<Line>() {

            PNode undefinedSlopeIndicator;

            public void apply( Line line ) {

                // Synchronize the controls atomically.
                updatingControls = true;
                {
                    x1.set( line.x1 );
                    y1.set( line.y1 );
                    x2.set( line.x2 );
                    y2.set( line.y2 );
                }
                updatingControls = false;

                // Update the unsimplified slope
                {
                    final double margin = 3;
                    final double cornerRadius = 10;

                    // rise
                    removeChild( unsimplifiedRiseNode );
                    unsimplifiedRiseNode = new NumberBackgroundNode( line.rise, FORMAT, staticFont, staticColor, LGColors.SLOPE, margin, margin, cornerRadius, maxUnsimplifiedWidth );
                    addChild( unsimplifiedRiseNode );

                    // run
                    removeChild( unsimplifiedRunNode );
                    unsimplifiedRunNode = new NumberBackgroundNode( line.run, FORMAT, staticFont, staticColor, LGColors.SLOPE, margin, margin, cornerRadius, maxUnsimplifiedWidth );
                    addChild( unsimplifiedRunNode );

                    // fraction line length
                    unsimplifiedFractionLineNode.setPathTo( createFractionLineShape( Math.max( unsimplifiedRiseNode.getFullBoundsReference().getWidth(), unsimplifiedRunNode.getFullBoundsReference().getWidth() ) ) );
                }

                // do layout before adding undefined-slope indicator
                updateLayout.apply( line );

                // undefined-slope indicator
                if ( undefinedSlopeIndicator != null ) {
                    removeChild( undefinedSlopeIndicator );
                }
                if ( line.undefinedSlope() ) {
                    undefinedSlopeIndicator = new UndefinedSlopeIndicator( getFullBoundsReference().getWidth(), getFullBoundsReference().getHeight() );
                    undefinedSlopeIndicator.setOffset( parentNode.getXOffset(),
                                                       parentNode.getFullBoundsReference().getCenterY() - ( undefinedSlopeIndicator.getFullBoundsReference().getHeight() / 2 ) + undefinedSlopeYFudgeFactor );
                    addChild( undefinedSlopeIndicator );
                }
            }
        } );
    }

    /*
     * Use this constructor for non-interactive equations. Form: m = <value>
     * Slope value is displayed in simplified form.
     */
    public SlopeEquationNode( Property<Line> line, PhetFont font, Color color ) {
        super( font.getSize() );

        // Nodes
        // m =
        final PNode slopeIsNode = new PhetPText( MessageFormat.format( Strings.SLOPE_IS, Strings.SYMBOL_SLOPE ), font, color );
        // rise/run
        final PNode minusSignNode = new MinusNode( signLineSize, color );
        final PText riseNode = new PhetPText( "?", font, color );
        final PText runNode = new PhetPText( "?", font, color );
        final PPath fractionLineNode = new PhetPPath( createFractionLineShape( 1 ), color, null, null ); // correct length will be set later

        // rendering order
        addChild( slopeIsNode );

        // layout
        slopeIsNode.setOffset( 0, 0 );

        final VoidFunction1<Line> updateLayout = new VoidFunction1<Line>() {
            public void apply( Line line ) {

                // remove all related nodes, then we'll add the ones that are relevant
                removeChild( minusSignNode );
                removeChild( riseNode );
                removeChild( runNode );
                removeChild( fractionLineNode );

                if ( line.undefinedSlope() ) {
                    // "undefined"
                    addChild( riseNode );
                    riseNode.setText( Strings.UNDEFINED );
                    riseNode.setOffset( slopeIsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                        slopeIsNode.getY() );
                }
                else if ( line.getSlope() == 0 ) {
                    // 0
                    addChild( riseNode );
                    riseNode.setText( "0" );
                    riseNode.setOffset( slopeIsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                        slopeIsNode.getY() );
                }
                else {
                    final double nextXOffset;
                    if ( line.getSlope() < 0 ) {
                        // minus sign
                        addChild( minusSignNode );
                        minusSignNode.setOffset( slopeIsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                                 slopeIsNode.getFullBoundsReference().getCenterY() - ( minusSignNode.getFullBoundsReference().getHeight() / 2 ) + slopeSignYFudgeFactor + slopeSignYOffset );
                        nextXOffset = minusSignNode.getFullBoundsReference().getMaxX() + operatorXSpacing;
                    }
                    else {
                        nextXOffset = slopeIsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing;
                    }

                    if ( MathUtil.isInteger( line.getSlope() ) ) {
                        // integer
                        addChild( riseNode );
                        riseNode.setText( FORMAT.format( Math.abs( line.getSlope() ) ) );
                        riseNode.setOffset( nextXOffset, slopeIsNode.getYOffset() );
                    }
                    else {
                        // fraction
                        addChild( fractionLineNode );
                        addChild( riseNode );
                        addChild( runNode );

                        // set absolute values
                        riseNode.setText( FORMAT.format( Math.abs( line.getSimplifiedRise() ) ) );
                        runNode.setText( FORMAT.format( Math.abs( line.getSimplifiedRun() ) ) );

                        // adjust fraction line length
                        fractionLineNode.setPathTo( createFractionLineShape( Math.max( riseNode.getFullBoundsReference().getWidth(), runNode.getFullBoundsReference().getWidth() ) ) );

                        // layout, values horizontally centered
                        fractionLineNode.setOffset( nextXOffset,
                                                    slopeIsNode.getFullBoundsReference().getCenterY() + fractionLineYFudgeFactor );
                        riseNode.setOffset( fractionLineNode.getFullBoundsReference().getCenterX() - ( riseNode.getFullBoundsReference().getWidth() / 2 ),
                                            fractionLineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - ySpacing );
                        runNode.setOffset( fractionLineNode.getFullBoundsReference().getCenterX() - ( runNode.getFullBoundsReference().getWidth() / 2 ),
                                           fractionLineNode.getFullBoundsReference().getMaxY() + ySpacing );
                    }
                }
            }
        };

        line.addObserver( updateLayout );
    }

    // Use this constructor for static equations
    public SlopeEquationNode( Line line, PhetFont font, Color color ) {
        this( new Property<Line>( line ), font, color );
    }

    // Creates a node that displays the general form of this equation.
    public static PNode createGeneralFormNode() {

        final Color color = LGColors.INTERACTIVE_LINE;
        final Font font = new PhetFont( Font.BOLD, 18 );

        // m =
        String htmlLeftSide = MessageFormat.format( "{0}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{1} =", Strings.SLOPE, Strings.SYMBOL_SLOPE );
        PNode leftSideNode = new HTMLNode( htmlLeftSide, color, font );

        // y2 - y1
        //NOTE: <font> tag is deprecated in HTML4 and unsupported in HTML5. But as of Java 1.7, Swing (supposedly) implements a subset of HTML3.
        String pattern = "<html>{0}<font size='-1'><sub>2</sub></font> - {1}<font size='-1'><sub>1</sub></font></html>"; // same for numerator and denominator
        String htmlNumerator = MessageFormat.format( pattern, Strings.SYMBOL_Y, Strings.SYMBOL_Y );
        PNode numeratorNode = new HTMLNode( htmlNumerator, color, font );

        // x2 - x1
        String htmlDenominator = MessageFormat.format( pattern, Strings.SYMBOL_X, Strings.SYMBOL_X );
        PNode denominatorNode = new HTMLNode( htmlDenominator, color, font );

        // fraction line
        final double length = Math.max( numeratorNode.getFullBoundsReference().getWidth(), denominatorNode.getFullBoundsReference().getWidth() );
        PPath fractionLineNode = new PPath( new Rectangle2D.Double( 0, 0, length, 1 ) );
        fractionLineNode.setPaint( color );
        fractionLineNode.setStroke( null );

        // rendering order
        PNode parentNode = new PNode();
        parentNode.addChild( leftSideNode );
        parentNode.addChild( numeratorNode );
        parentNode.addChild( denominatorNode );
        parentNode.addChild( fractionLineNode );

        // layout
        leftSideNode.setOffset( 0, 0 );
        fractionLineNode.setOffset( leftSideNode.getFullBoundsReference().getMaxX() + 5,
                                    leftSideNode.getFullBoundsReference().getCenterY() - ( fractionLineNode.getFullBoundsReference().getHeight() / 2 ) + 3 );
        numeratorNode.setOffset( fractionLineNode.getFullBoundsReference().getCenterX() - ( numeratorNode.getFullBoundsReference().getWidth() / 2 ),
                                 fractionLineNode.getFullBoundsReference().getMinY() - numeratorNode.getFullBoundsReference().getHeight() - 1 );
        denominatorNode.setOffset( fractionLineNode.getFullBoundsReference().getCenterX() - ( denominatorNode.getFullBoundsReference().getWidth() / 2 ),
                                   fractionLineNode.getFullBoundsReference().getMaxY() + 1 );

        return new ZeroOffsetNode( parentNode );
    }
}
