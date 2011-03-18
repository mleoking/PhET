package edu.colorado.phet.buildanatom.modules.isotopemixture.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;

import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.IsotopeMixturesModel;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode.PieValue;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
     * Class that represents a pie chart portraying the proportion of the
     * various isotopes in the test chamber.
     *
     * @author John Blanco
     */
class IsotopeProprotionsPieChart extends PNode {

    private static final int PIE_CHART_DIAMETER = 100;

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
        // in the chamber, so showing a pie chart would make no sense.
        final PNode emptyPieChart = new PhetPPath(
                new Ellipse2D.Double( -PIE_CHART_DIAMETER / 2, -PIE_CHART_DIAMETER / 2, PIE_CHART_DIAMETER, PIE_CHART_DIAMETER ),
                new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5, 4}, 0), Color.black );
        addChild(emptyPieChart);
        // Add the observer that will update the pie chart.
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
                                    // Update the proportions.
                    PieValue[] pieSlices = new PieValue[model.getPossibleIsotopesProperty().getValue().size()];
                    int sliceCount = 0;
                    for ( ImmutableAtom isotope : model.getPossibleIsotopesProperty().getValue() ) {
                        pieSlices[sliceCount++] =
                                new PieValue( model.getIsotopeTestChamber().getIsotopeProportion( isotope ),
                                model.getColorForIsotope( isotope ) );
                    }
                    pieChart.setPieValues( pieSlices );
                    double lightestIsotopeProportion = pieSlices[0].getValue() / pieChart.getTotal();
                    pieChart.setInitialAngle( Math.PI - ( lightestIsotopeProportion * Math.PI ) );
                    for ( int i = 0; i < pieSlices.length; i++ ) {
                        if ( pieChart.getCenterEdgePtForSlice( i ) != null ) {
                            // Create the label for this pie slice.
                            PNode labelNode;
                            Point2D centerEdgeOfPieSlice = pieChart.getCenterEdgePtForSlice( i );
                            boolean labelOnLeft = centerEdgeOfPieSlice.getX() < 0;
                            labelNode = new SliceLabel( model.getPossibleIsotopesProperty().getValue().get( i ),
                                    pieSlices[i].getValue() / pieChart.getTotal(), labelOnLeft );
                            labelLayer.addChild( labelNode );

                            // Determine the "unconstrained" target position
                            // for the label, meaning a position that is
                            // directly out from the edge of the slice, but
                            // may be above or below the edges of the pie
                            // chart.
                            Vector2D positionVector = new Vector2D( centerEdgeOfPieSlice );
                            positionVector.scale( 1.3 );

                            // Constrain the position so that no part of the
                            // label goes above or below the upper and lower
                            // edges of the pie chart.
                            double minY = -PIE_CHART_DIAMETER / 2 + labelNode.getFullBoundsReference().height / 2;
                            double maxY = PIE_CHART_DIAMETER / 2 - labelNode.getFullBoundsReference().height / 2;
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
                    }
                }
            }
        } );
    }

    /**
     * Class that represents the label for a slice, which consists of a
     * readout that shows the percentage for this slice and a label of the
     * isotope.
     *
     * @author John Blanco
     */
    private static class SliceLabel extends PNode {
        private static final DecimalFormat FORMATTER = new DecimalFormat( "#.00" );
        private static final Font READOUT_FONT = new PhetFont(14);

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
            double nodeHeight = getFullBoundsReference().height;
            addChild( readoutBox );
            if ( labelOnLeft ){
                readoutBox.setOffset(symbol.getFullBoundsReference().getMaxX() + 5, readoutBox.getOffset().getY() );
            }
            else{
                symbol.setOffset(readoutBox.getFullBoundsReference().getMaxX() + 5, symbol.getOffset().getY() );
            }
        }
    }

    /**
     * Class that represents a chemical symbol, including the mass number
     * (in front of the chemical symbol and partially above it) and the
     * atomic number (in front of the chemical symbol and partially below
     * it).
     *
     * @author John Blanco
     */
    private static class ChemSymbolWithNumbers extends PNode {
        private static final Font CHEMICAL_SYMBOL_FONT = new PhetFont(24, true);
        private static final Font SUPERSCRIPT_SUBSCRIPT_FONT = new PhetFont(12);
        private static final double DISTANCE_FROM_NUMBERS_TO_SYMBOL = 4; // In screen coords, close to pixels.

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
                        massNumber.getFullBoundsReference().height * 0.25 );
            }};
            addChild( symbol );
            PText atomicNumber = new PText( Integer.toString( chemical.getNumProtons() ) ){{
                setFont( SUPERSCRIPT_SUBSCRIPT_FONT );
                setOffset(
                        symbol.getFullBoundsReference().getMinX() - DISTANCE_FROM_NUMBERS_TO_SYMBOL - getFullBoundsReference().width,
                        symbol.getFullBoundsReference().getMaxY() - getFullBoundsReference().height * 0.75 );
            }};
            addChild( atomicNumber );
        }
    }
}