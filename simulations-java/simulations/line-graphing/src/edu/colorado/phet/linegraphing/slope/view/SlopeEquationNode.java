// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slope.view;

import java.awt.Color;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.common.view.SpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SpinnerStateIndicator.PointColors;
import edu.colorado.phet.linegraphing.common.view.UndefinedSlopeIndicator;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Renderer for slope equations, with interactive points (x1, y1, x2, y2).
 * Form is: m = y2 - y1 / x2 - x1  = rise/run
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeEquationNode extends EquationNode {

    private final NumberFormat FORMAT = new DefaultDecimalFormat( "0" );

    private final Property<Double> x1, y1, x2, y2; // internal properties that are connected to spinners
    private boolean updatingControls; // flag that allows us to update all controls atomically when the model changes
    private PNode undefinedSlopeIndicator;

    public SlopeEquationNode( final Property<Line> interactiveLine,
                              Property<DoubleRange> xRange,
                              Property<DoubleRange> yRange,
                              PhetFont interactiveFont,
                              final PhetFont staticFont,
                              final Color staticColor ) {
        super( staticFont.getSize() );

        this.x1 = new Property<Double>( interactiveLine.get().x1 );
        this.y1 = new Property<Double>( interactiveLine.get().y1 );
        this.x2 = new Property<Double>( interactiveLine.get().x2 );
        this.y2 = new Property<Double>( interactiveLine.get().y2 );

        // Determine the max width of the rise and run spinners.
        double maxSlopeSpinnerWidth = computeMaxSlopeSpinnerWidth( xRange, yRange, interactiveFont, FORMAT );

        // Nodes
        // m =
        PNode mNode = new PhetPText( Strings.SYMBOL_SLOPE, staticFont, staticColor );
        PNode leftEqualsNode = new PhetPText( "=", staticFont, staticColor );
        // y2 - y2
        PNode y2Node = new ZeroOffsetNode( new SpinnerNode( UserComponents.y2Spinner, y2, xRange, new PointColors(), interactiveFont, FORMAT ) );
        PNode numeratorOperatorNode = new PhetPText( "-", staticFont, staticColor );
        PNode y1Node = new ZeroOffsetNode( new SpinnerNode( UserComponents.y1Spinner, y1, xRange, new PointColors(), interactiveFont, FORMAT ) );
        // left fraction line
        PPath leftLineNode = new PhetPPath( createFractionLineShape( 10 ), staticColor, null, null ); // correct length will be set later
        // x2 - x1
        PNode x2Node = new ZeroOffsetNode( new SpinnerNode( UserComponents.x2Spinner, x2, xRange, new PointColors(), interactiveFont, FORMAT ) );
        PNode denominatorOperatorNode = new PhetPText( "-", staticFont, staticColor );
        PNode x1Node = new ZeroOffsetNode( new SpinnerNode( UserComponents.x1Spinner, x1, xRange, new PointColors(), interactiveFont, FORMAT ) );
        // = rise / run
        PNode rightEqualsNode = new PhetPText( "=", staticFont, staticColor );
        final PText riseNode = new PhetPText( FORMAT.format( interactiveLine.get().rise ), staticFont, staticColor );
        final PText runNode = new PhetPText( FORMAT.format( interactiveLine.get().run ), staticFont, staticColor );
        final PPath rightLineNode = new PhetPPath( createFractionLineShape( 10 ), staticColor, null, null ); // correct length will be set later

        // rendering order
        {
            addChild( mNode );
            addChild( leftEqualsNode );
            addChild( y2Node );
            addChild( numeratorOperatorNode );
            addChild( y1Node );
            addChild( leftLineNode );
            addChild( x2Node );
            addChild( denominatorOperatorNode );
            addChild( x1Node );
            addChild( rightEqualsNode );
            addChild( riseNode );
            addChild( rightLineNode );
            addChild( runNode );
        }

        // layout
        {
            // m =
            mNode.setOffset( 0, 0 );
            leftEqualsNode.setOffset( mNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing, mNode.getYOffset() );
            // fraction line
            leftLineNode.setOffset( leftEqualsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                    leftEqualsNode.getFullBoundsReference().getCenterY() + fractionLineYFudgeFactor );
            // y2 - y1
            y2Node.setOffset( leftLineNode.getXOffset(),
                              leftLineNode.getFullBoundsReference().getMinY() - y2Node.getFullBoundsReference().getHeight() - spinnersYSpacing );
            numeratorOperatorNode.setOffset( y2Node.getFullBoundsReference().getMaxX() + operatorXSpacing,
                                             y2Node.getFullBoundsReference().getCenterY() - ( numeratorOperatorNode.getFullBoundsReference().getHeight() / 2 ) );
            y1Node.setOffset( numeratorOperatorNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                              y2Node.getYOffset() );
            // fix fraction line length
            final double leftLineLength = y1Node.getFullBoundsReference().getMaxX() - y2Node.getFullBoundsReference().getMinX();
            leftLineNode.setPathTo( createFractionLineShape( leftLineLength ) );
            // x2 - x1
            x2Node.setOffset( y2Node.getXOffset(),
                              leftLineNode.getFullBoundsReference().getMaxY() + spinnersYSpacing );
            denominatorOperatorNode.setOffset( x2Node.getFullBoundsReference().getMaxX() + operatorXSpacing,
                                               x2Node.getFullBoundsReference().getCenterY() - ( denominatorOperatorNode.getFullBoundsReference().getHeight() / 2 ) );
            x1Node.setOffset( denominatorOperatorNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                              x2Node.getYOffset() );
            // = rise/run
            rightEqualsNode.setOffset( leftLineNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                       leftEqualsNode.getYOffset() );
            rightLineNode.setOffset( rightEqualsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                     leftLineNode.getYOffset() );
            riseNode.setOffset( rightLineNode.getXOffset(), // x offset will be set correctly later
                                rightLineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - ySpacing );
            runNode.setOffset( rightLineNode.getXOffset(), // x offset will be set correctly later
                               rightLineNode.getFullBoundsReference().getMaxY() + ySpacing );
        }

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

                // Update the rise & run values
                riseNode.setText( FORMAT.format( line.rise ) );
                runNode.setText( FORMAT.format( line.run ) );

                // Adjust the fraction line length and center the rise and run values
                rightLineNode.setPathTo( createFractionLineShape( Math.max( riseNode.getFullBoundsReference().getWidth(), runNode.getFullBoundsReference().getWidth() ) ) );
                riseNode.setOffset( rightLineNode.getFullBoundsReference().getCenterX() - ( riseNode.getFullBoundsReference().getWidth() / 2 ),
                                    riseNode.getYOffset() );
                runNode.setOffset( rightLineNode.getFullBoundsReference().getCenterX() - ( runNode.getFullBoundsReference().getWidth() / 2 ),
                                   runNode.getYOffset() );

                // undefined-slope indicator
                removeChild( undefinedSlopeIndicator );
                if ( line.undefinedSlope() ) {
                    final double centerX = getFullBoundsReference().getCenterX();
                    final double centerY = getFullBoundsReference().getCenterY();
                    undefinedSlopeIndicator = new UndefinedSlopeIndicator( getFullBoundsReference().getWidth(), getFullBoundsReference().getHeight() );
                    undefinedSlopeIndicator.setOffset( centerX - ( undefinedSlopeIndicator.getFullBoundsReference().getWidth() / 2 ),
                                                       centerY - ( undefinedSlopeIndicator.getFullBoundsReference().getHeight() / 2 ) );
                    addChild( undefinedSlopeIndicator );
                }
            }
        } );
    }
}
