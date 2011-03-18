package edu.colorado.phet.buildanatom.modules.isotopemixture.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;

import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.IsotopeMixturesModel;
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

    //        private final Map<ImmutableAtom, >

    /**
     * Constructor.
     */
    public IsotopeProprotionsPieChart( final IsotopeMixturesModel model ) {
        final PNode labelLayer = new PNode();
        addChild( labelLayer );
        final PieChartNode pieChart = new PieChartNode( new PieValue[] { new PieValue( 100, Color.red ) },
                new Rectangle( -PIE_CHART_DIAMETER / 2, -PIE_CHART_DIAMETER / 2, PIE_CHART_DIAMETER, PIE_CHART_DIAMETER ) ) {
            {
                setOffset( 0, 0 );
            }
        };
        addChild( pieChart );
        // Add the observer that will update the pie chart.
        model.getIsotopeTestChamber().addTotalCountChangeObserver( new SimpleObserver() {
                public void update() {
                int isotopeCount = model.getIsotopeTestChamber().getTotalIsotopeCount();
                                // Hide the chart if there is nothing in the chamber.
                pieChart.setVisible( isotopeCount > 0 );
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
                            PNode labelNode = new SliceLabel( model.getPossibleIsotopesProperty().getValue().get( i ),
                                    pieSlices[i].getValue() / pieChart.getTotal(), true );
                            Point2D labelOffset = pieChart.getCenterEdgePtForSlice( i );
                            if (labelOffset.getX() > 0 ){
                                // On right side of chart.
                                labelOffset.setLocation(
                                        labelOffset.getX() + labelNode.getFullBoundsReference().width + 10,
                                        labelOffset.getY() );
                            }
                            else{
                                // On left side of chart.
                                labelOffset.setLocation(
                                        labelOffset.getX() - labelNode.getFullBoundsReference().width - 10,
                                        labelOffset.getY() );
                            }
                            labelNode.setOffset( labelOffset );
                            labelLayer.addChild( labelNode );
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
        private static final Font CHEMICAL_SYMBOL_FONT = new PhetFont(20);
        private static final Font SUPERSCRIPT_SUBSCRIPT_FONT = new PhetFont(10);
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
                        massNumber.getFullBoundsReference().getCenterY() );
            }};
            addChild( symbol );
            PText atomicNumber = new PText( Integer.toString( chemical.getNumProtons() ) ){{
                setFont( SUPERSCRIPT_SUBSCRIPT_FONT );
                setOffset(
                        symbol.getFullBoundsReference().getMinX() - DISTANCE_FROM_NUMBERS_TO_SYMBOL - getFullBoundsReference().width,
                        symbol.getFullBoundsReference().getMaxY() - getFullBoundsReference().height / 2 );
            }};
            addChild( atomicNumber );
        }
    }
}