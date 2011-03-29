// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.isotopemixture.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.IsotopeMixturesModel;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode.PieValue;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Class that represents a pie chart portraying the proportion of the various
 * isotopes in the test chamber.
 *
 * @author John Blanco
 */
class IsotopeProprotionsPieChart extends PNode {

    private static final double OVERALL_HEIGHT = 100;
    private static final int PIE_CHART_DIAMETER = 80; // Must be less than overall height.
    private static final Stroke CONNECTING_LINE_STROKE = new BasicStroke(2);

    /**
     * Constructor.
     */
    public IsotopeProprotionsPieChart( final IsotopeMixturesModel model ) {
        // Create and add a layer where the labels will be placed.
        final PNode labelLayer = new PNode();
        addChild( labelLayer );
        // Create and add the pie chart itself.
        final PieChartNode pieChart = new PieChartNode( new PieValue[] { new PieValue( 100, Color.red ) },
                new Rectangle( -PIE_CHART_DIAMETER / 2, -PIE_CHART_DIAMETER / 2, PIE_CHART_DIAMETER, PIE_CHART_DIAMETER ) ) {{
                setOffset( 0, 0 );
            }};
            addChild( pieChart );
        // Create and add the node that will be shown when there is nothing
        // in the chamber, since showing a pie chart would make no sense.
        final PNode emptyPieChart = new PhetPPath(
                new Ellipse2D.Double( -PIE_CHART_DIAMETER / 2, -PIE_CHART_DIAMETER / 2, PIE_CHART_DIAMETER, PIE_CHART_DIAMETER ),
                new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5, 4}, 0), Color.black ){{
                    setOffset( 0, 0 );
                }};
        addChild( emptyPieChart );
        // Add the observer that will update the pie chart when the contents
        // of the test chamber change.
        model.getIsotopeTestChamber().addTotalCountChangeObserver( new SimpleObserver() {
            public void update() {
                int isotopeCount = model.getIsotopeTestChamber().getTotalIsotopeCount();
                // Hide the chart if there is nothing in the chamber.
                pieChart.setVisible( isotopeCount > 0 );
                emptyPieChart.setVisible( isotopeCount == 0 );
                labelLayer.setVisible( isotopeCount > 0 );
                if ( isotopeCount > 0 ) {
                    // Clear the labels.
                    labelLayer.removeAllChildren();
                    // Update the proportions of the pie slices.
                    ArrayList<PieValue> pieSlices = new ArrayList<PieValue>();
                    for ( ImmutableAtom isotope : model.getPossibleIsotopesProperty().getValue() ) {
                        double proportion = model.getIsotopeTestChamber().getIsotopeProportion( isotope );
                        // Only add non-zero values.
                        if ( proportion > 0 ){
                            pieSlices.add( new PieValue( proportion, model.getColorForIsotope( isotope ) ) );
                        }
                    }
                    // Convert the pie value array into the type needed by the
                    // pie chart.
                    pieChart.setPieValues( pieSlices.toArray( new PieValue[pieSlices.size()] ) );
                    // TODO: The following was put in to catch a race condition where
                    // there could be isotopes in the chamber, but none that matched
                    // the current prototype isotope.  Changes were made that
                    // appeared to fix this, but this check should be left for a
                    // while to make sure that it doesn't come back.  If the
                    // errors from this haven't been seen for a while, it can
                    // probably be safely removed.
                    if ( pieSlices.size() == 0){
                        System.out.println("No pie slices, aborting update of chart.");
                        System.out.println("Prototype isotope = " + model.getAtom().toImmutableAtom());
                        System.out.println("Possible Isotopes: ");
                        for (ImmutableAtom isotope : model.getPossibleIsotopesProperty().getValue()){
                            System.out.println("   " + isotope);
                        }
                        return;
                    }

                    // Orient the pie chart such that the slice for the
                    // lightest element is centered on the left side.  This
                    // is done to make the chart behave in a why that causes
                    // the labels to be in consistent and reasonable
                    // positions.
                    double lightestIsotopeProportion = pieSlices.get( 0 ).getValue() / pieChart.getTotal();
                    pieChart.setInitialAngle( Math.PI - ( lightestIsotopeProportion * Math.PI ) );
                    // Add the floating labels to the chart.
                    ArrayList<SliceLabel> sliceLabels = new ArrayList<SliceLabel>();
                    for ( int i = 0; i < pieSlices.size(); i++ ) {
                        // Create the label for this pie slice.
                        SliceLabel labelNode;
                        Point2D centerEdgeOfPieSlice = pieChart.getCenterEdgePtForSlice( i );
                        boolean labelOnLeft = centerEdgeOfPieSlice.getX() < 0;
                        labelNode = new SliceLabel( model.getPossibleIsotopesProperty().getValue().get( i ),
                                pieSlices.get( i ).getValue() / pieChart.getTotal(), labelOnLeft );
                        labelLayer.addChild( labelNode );
                        sliceLabels.add( labelNode );

                        // Determine the "unconstrained" target position
                        // for the label, meaning a position that is
                        // directly out from the edge of the slice, but
                        // may be above or below the edges of the pie
                        // chart.
                        Vector2D positionVector = new Vector2D( centerEdgeOfPieSlice );
                        positionVector.scale( 1.4 );
                        labelNode.setUnconstrainedPos( positionVector.getX(), positionVector.getY() );

                        // Constrain the position so that no part of the
                        // label goes above or below the upper and lower
                        // edges of the pie chart.
                        double minY = -OVERALL_HEIGHT / 2 + labelNode.getFullBoundsReference().height / 2;
                        double maxY = OVERALL_HEIGHT / 2 - labelNode.getFullBoundsReference().height / 2;
                        double xSign = labelOnLeft ? -1 : 1;
                        if ( positionVector.getY() < minY ){
                            positionVector.setX( xSign * Math.sqrt( positionVector.getMagnitudeSq() - minY * minY ) );
                            positionVector.setY( minY );
                        }
                        else if ( positionVector.getY() > maxY ){
                            positionVector.setX( xSign * Math.sqrt( positionVector.getMagnitudeSq() - maxY * maxY ) );
                            positionVector.setY( maxY );
                        }

                        // Position the label.
                        if ( labelOnLeft ){
                            labelNode.setOffset(
                                    positionVector.getX()-labelNode.getFullBoundsReference().width,
                                    positionVector.getY()-labelNode.getFullBoundsReference().height / 2 );
                        }
                        else{
                            // Label on right.
                            labelNode.setOffset(
                                    positionVector.getX(),
                                    positionVector.getY()-labelNode.getFullBoundsReference().height / 2 );
                        }
                    }
                    // Now that the labels are added in their initial
                    // positions, they need to be checked to make sure that
                    // they aren't overlapping and, if they are, their
                    // positions are adjusted.
                    adjustLabelYPositions( sliceLabels, -OVERALL_HEIGHT / 2, OVERALL_HEIGHT / 2 );

                    // The labels should now be all in reasonable positions,
                    // so draw a line from the edge of the label to the pie
                    // slice to which it corresponds.
                    for (int i = 0; i < sliceLabels.size(); i++){
                        PNode label = sliceLabels.get( i );
                        Point2D labelConnectPt = new Point2D.Double();
                        if (label.getFullBoundsReference().getCenterX() > pieChart.getFullBoundsReference().getCenterX()){
                            // Label is on right, so connect point should be on left.
                            labelConnectPt.setLocation(
                                    label.getFullBoundsReference().getMinX(),
                                    label.getFullBoundsReference().getCenterY() );
                        }
                        else{
                            // Label is on left, so connect point should be on right.
                            labelConnectPt.setLocation(
                                    label.getFullBoundsReference().getMaxX(),
                                    label.getFullBoundsReference().getCenterY() );
                        }
                        Point2D sliceConnectPt = pieChart.getCenterEdgePtForSlice( i );
                        assert sliceConnectPt != null; // Should be a valid slice edge point for each label.
                        // Find a point that is straight out from the center
                        // of the pie chart above the point that connects to
                        // the slice.  Note that these calculations assume
                        // that the center of the pie chart is at (0,0).
                        DoubleGeneralPath connectingLineShape = new DoubleGeneralPath( sliceConnectPt );
                        if ( sliceConnectPt.getY() > OVERALL_HEIGHT * 0.25 || sliceConnectPt.getY() < -OVERALL_HEIGHT * 0.25 ){
                            // Add a "bend point" so that the line doesn't go under the pie chart.
                            Point2D bendPt = new Point2D.Double(
                                    sliceConnectPt.getX() * ( OVERALL_HEIGHT / PIE_CHART_DIAMETER ),
                                    sliceConnectPt.getY() * ( OVERALL_HEIGHT / PIE_CHART_DIAMETER ) );
                            connectingLineShape.lineTo( bendPt );
                        }
                        connectingLineShape.lineTo( labelConnectPt );
                        labelLayer.addChild( new PhetPPath( connectingLineShape.getGeneralPath(),
                                CONNECTING_LINE_STROKE, Color.BLACK) );
                    }
                }
            }
        } );
    }

    /**
     * Adjust the pie chart labels such that they do not overlap with one
     * another and yet are still within the overall bounds of the chart.
     *
     * @param sliceLabels
     */
    private void adjustLabelYPositions( ArrayList<SliceLabel> sliceLabels, double minY, double maxY ) {
        double rotationIncrement = Math.PI / 200; // Empirically chosen.
        for (int i = 1; i < 50; i++ ){ // Number of iterations empirically chosen.
            boolean overlapDetected = false;
            for ( SliceLabel label : sliceLabels ) {
                boolean moveUp = false;
                boolean moveDown = false;
                for ( SliceLabel comparisonLabel : sliceLabels ) {
                    if ( label == comparisonLabel ){
                        // Same label, so ignore.
                        continue;
                    }
                    if ( label.fullIntersects( comparisonLabel.getFullBoundsReference() )){
                        // These labels overlap.
                        overlapDetected = true;
                        if ( label.getUnconstrainedPosRef().getY() > comparisonLabel.getUnconstrainedPosRef().getY() && label.getFullBoundsReference().getMaxY() < maxY ){
                            moveUp = true;
                        }
                        else if ( label.getUnconstrainedPosRef().getY() < comparisonLabel.getUnconstrainedPosRef().getY() && label.getFullBoundsReference().getMinY() > minY ) {
                            moveDown = true;
                        }
                    }
                }
                // Adjust this label's position based upon any overlap that
                // was detected.  The general idea is this: if there is
                // overlap in both directions, don't move.  If there is only
                // overlap with a label that has a higher unconstrained
                // location, move down.  If there is only overlap with a label
                // with a lower unconstrained location, move down.
                if ( moveUp && !moveDown ){
                    Vector2D posVector = new Vector2D( label.getOffset() );
                    double sign = isLabelOnRight( label ) ? 1 : -1;
                    posVector.rotate( sign * rotationIncrement );
                    label.setOffset( posVector.getX(), posVector.getY() );
                }
                else if ( moveDown && !moveUp ){
                    Vector2D posVector = new Vector2D( label.getOffset() );
                    double sign = isLabelOnRight( label ) ? -1 : 1;
                    posVector.rotate( sign * rotationIncrement );
                    label.setOffset( posVector.getX(), posVector.getY() );
                }
            }
            if (!overlapDetected){
                // No overlap for any of the labels, so we are done.
                System.out.println("i = " + i);
                break;
            }
        }
    }

    private boolean isLabelOnRight( SliceLabel label ){
        return ( label.getFullBoundsReference().getCenterX() > 0 );
    }

    /**
     * Class that represents the label for a slice, which consists of a
     * readout that shows the percentage for this slice and a label of the
     * isotope.
     */
    private static class SliceLabel extends PNode {
        private static final DecimalFormat FORMATTER = new DecimalFormat( "#.00" );
        private static final Font READOUT_FONT = new PhetFont(14);

        // The "unconstrained position" is the position where this label
        // would be placed if it didn't need to sit within the upper and
        // lower bounds of the pie chart and didn't have to worry about
        // avoiding overlap with other labels.  It is used for arbitrating
        // how labels move when handling overlap.
        private final Point2D unconstrainedPos = new Point2D.Double(0, 0);

        public SliceLabel( ImmutableAtom isotopeConfig, double proportionOfIsotope, boolean labelOnLeft ){
            final ChemSymbolWithNumbers symbol = new ChemSymbolWithNumbers( isotopeConfig );
            addChild( symbol );
            final PText readoutText = new PText( FORMATTER.format( proportionOfIsotope * 100 ) + " %"){{
               setFont(READOUT_FONT);
            }};
            PNode readoutBox = new PhetPPath( Color.WHITE, new BasicStroke( 2 ), Color.BLACK ){{
                Shape readoutBoxShape = new RoundRectangle2D.Double( 0, 0, readoutText.getFullBoundsReference().width * 1.2,
                        readoutText.getFullBoundsReference().height * 1.2, 4, 4);
                setPathTo( readoutBoxShape );
                readoutText.centerFullBoundsOnPoint( getFullBoundsReference().getCenterX(), getFullBoundsReference().getCenterY() );
                addChild( readoutText );
            }};
            // Make the two portions of the label line up on the horizontal
            // axis.
            if ( symbol.getFullBoundsReference().height > readoutBox.getFullBoundsReference().height ){
                readoutBox.setOffset(
                        readoutBox.getOffset().getX(),
                        symbol.getFullBoundsReference().getCenterY() - readoutBox.getFullBoundsReference().height / 2 );
            }
            else {
                symbol.setOffset(
                        symbol.getOffset().getX(),
                        readoutBox.getFullBoundsReference().getCenterY() - symbol.getFullBoundsReference().height / 2 );
            }
            // Position the elements of the overall label.
            addChild( readoutBox );
            if ( labelOnLeft ){
                readoutBox.setOffset(symbol.getFullBoundsReference().getMaxX() + 5, readoutBox.getOffset().getY() );
            }
            else{
                symbol.setOffset(readoutBox.getFullBoundsReference().getMaxX() + 5, symbol.getOffset().getY() );
            }
        }

        protected void setUnconstrainedPos( double x, double y) {
            unconstrainedPos.setLocation( x, y );
        }

        protected Point2D getUnconstrainedPosRef(){
            return unconstrainedPos;
        }
    }

    /**
     * Class that represents a chemical symbol, including the mass number
     * (in front of the chemical symbol and partially above it) and the
     * atomic number (in front of the chemical symbol and partially below
     * it).
     */
    private static class ChemSymbolWithNumbers extends PNode {
        private static final Font CHEMICAL_SYMBOL_FONT = new PhetFont(20, true);
        private static final Font SUPERSCRIPT_SUBSCRIPT_FONT = new PhetFont(11);
        private static final double DISTANCE_FROM_NUMBERS_TO_SYMBOL = 2; // In screen coords, close to pixels.

        public ChemSymbolWithNumbers( ImmutableAtom chemical ){
            final PText massNumber = new PText( Integer.toString( chemical.getMassNumber() ) ){{
                setFont( SUPERSCRIPT_SUBSCRIPT_FONT );
                setOffset( 0, 0 );
            }};
            addChild( massNumber );
            final PText symbol = new PText( chemical.getSymbol() ){{
                setFont( CHEMICAL_SYMBOL_FONT );
                setOffset(
                        massNumber.getFullBoundsReference().getMaxX() + DISTANCE_FROM_NUMBERS_TO_SYMBOL,
                        massNumber.getFullBoundsReference().height * 0.1 );
            }};
            addChild( symbol );
            PText atomicNumber = new PText( Integer.toString( chemical.getNumProtons() ) ){{
                setFont( SUPERSCRIPT_SUBSCRIPT_FONT );
                setOffset(
                        symbol.getFullBoundsReference().getMinX() - DISTANCE_FROM_NUMBERS_TO_SYMBOL - getFullBoundsReference().width,
                        symbol.getFullBoundsReference().getMaxY() - getFullBoundsReference().height * 0.85 );
            }};
            addChild( atomicNumber );
        }
    }
}