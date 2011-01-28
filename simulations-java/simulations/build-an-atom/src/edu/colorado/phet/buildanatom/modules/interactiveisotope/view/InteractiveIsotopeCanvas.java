// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.interactiveisotope.view;

import static edu.colorado.phet.buildanatom.BuildAnAtomDefaults.STAGE_SIZE;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.buildanatom.model.IDynamicAtom;
import edu.colorado.phet.buildanatom.modules.interactiveisotope.model.InteractiveIsotopeModel;
import edu.colorado.phet.buildanatom.view.MaximizeControlNode;
import edu.colorado.phet.buildanatom.view.ParticleCountLegend;
import edu.colorado.phet.buildanatom.view.PeriodicTableControlNode;
import edu.colorado.phet.buildanatom.view.StabilityIndicator;
import edu.colorado.phet.buildanatom.view.SymbolIndicatorNode;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode.PieValue;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PLine;
import edu.umd.cs.piccolox.util.LineShape;
import edu.umd.cs.piccolox.util.XYArray;

/**
 * Canvas for the tab where the user builds an atom.
 */
public class InteractiveIsotopeCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    private final static Font LABEL_FONT = new PhetFont( 20 );

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    // View
    private final PNode rootNode;

    // Transform.
    private final ModelViewTransform2D mvt;

    private final MaximizeControlNode symbolWindow;
    private final MaximizeControlNode abundanceWindow;

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

    public InteractiveIsotopeCanvas( final InteractiveIsotopeModel model ) {

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );

        // Set up the model-canvas transform.  IMPORTANT NOTES: The multiplier
        // factors for the point in the view can be adjusted to shift the
        // center right or left, and the scale factor can be adjusted to zoom
        // in or out (smaller numbers zoom out, larger ones zoom in).
        mvt = new ModelViewTransform2D(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( STAGE_SIZE.width * 0.32 ), (int) Math.round( STAGE_SIZE.height * 0.49 ) ),
                2.0, // "Zoom factor" - smaller zooms out, larger zooms in.
        true );

        setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        // Create the node that contains both the atom and the neutron bucket.
        final PNode atomAndBucketNode = new InteractiveIsotopeNode( model, mvt, new BooleanProperty( true ) );

        // Create the weigh scale that sits beneath the atom.
        final PNode scaleNode = new AtomScaleNode( model.getAtom() ) {
            {
                // The scale needs to sit just below the atom, and there are some
                // "tweak factors" needed to get it looking right.
                setOffset( mvt.modelToViewXDouble( 0 ) - getFullBoundsReference().width / 2, 530 );
            }
        };

        // Add the scale followed by the atom so that the layering effect is
        // correct.
        rootNode.addChild( scaleNode );
        rootNode.addChild( atomAndBucketNode );

        // Add indicator that shows whether the nucleus is stable.
        final StabilityIndicator stabilityIndicator = new StabilityIndicator( model.getAtom(), new BooleanProperty( true ) );
        model.getAtom().addObserver( new SimpleObserver() {
                public void update() {
                stabilityIndicator.setOffset( mvt.modelToViewX( 0 ) - stabilityIndicator.getFullBounds().getWidth() / 2,
                        mvt.modelToViewY( -Atom.ELECTRON_SHELL_1_RADIUS ) + 5 );
                }
                } );
        rootNode.addChild( stabilityIndicator );

        // Add the interactive periodic table that allows the user to select
        // the current element.
        final PeriodicTableControlNode periodicTableNode = new PeriodicTableControlNode( model,
                BuildAnAtomConstants.CANVAS_BACKGROUND ) {
            {
                setScale( 1.2 );
                setOffset( STAGE_SIZE.width - getFullBoundsReference().width - 20, 20 );
            }
        };
        rootNode.addChild( periodicTableNode );

        // Set the x position of the indicators.
        int indicatorWindowPosX = 600;

        // Add the symbol indicator node that provides more detailed
        // information about the currently selected element.
        final SymbolIndicatorNode symbolIndicatorNode = new SymbolIndicatorNode( model.getAtom(), false, false );
        symbolWindow = new MaximizeControlNode( BuildAnAtomStrings.INDICATOR_SYMBOL, new PDimension( 400, 100 ), symbolIndicatorNode, true );
        symbolIndicatorNode.setOffset( 20, symbolWindow.getFullBoundsReference().height / 2 - symbolIndicatorNode.getFullBounds().getHeight() / 2 );
        symbolWindow.setOffset( indicatorWindowPosX, 250 );
        rootNode.addChild( symbolWindow );

        // Add the node that indicates the percentage abundance.
        final PDimension abundanceWindowSize = new PDimension( 400, 200 );
        final PNode abundanceIndicatorNode = new AbundanceIndicatorNode( model.getAtom() );
        abundanceIndicatorNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                if ( evt.getPropertyName() == "fullBounds" ){
                    // Center the indicator in the min/max window.
                    abundanceIndicatorNode.centerFullBoundsOnPoint( abundanceWindowSize.getWidth() / 2, abundanceWindowSize.getHeight() / 2 );
                }
            }
        });
        abundanceWindow = new MaximizeControlNode( BuildAnAtomStrings.ABUNDANCE, abundanceWindowSize, abundanceIndicatorNode, true );
        abundanceIndicatorNode.setOffset( 20, abundanceWindow.getFullBoundsReference().height / 2 - abundanceIndicatorNode.getFullBounds().getHeight() / 2 );
        abundanceWindow.setOffset( indicatorWindowPosX, symbolWindow.getFullBoundsReference().getMaxY() + 30 );
        rootNode.addChild( abundanceWindow );

        // Add the legend/particle count indicator.
        ParticleCountLegend particleCountLegend = new ParticleCountLegend( model.getAtom(), Color.WHITE );
        particleCountLegend.setScale( 1.25 );
        particleCountLegend.setOffset( 20, 10 );
        rootNode.addChild( particleCountLegend );
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    /**
     * Shows the abundance readout for a user-selected isotope.
     */
    private static class AbundanceIndicatorNode extends PNode {

        private static DecimalFormat ABUNDANCE_FORMATTER = new DecimalFormat( "#.#####" );
        private static final double MIN_ABUNDANCE_TO_SHOW = 0.00001; // Should match the resolution of the ABUNDANCE_FORMATTER
        private static final Font READOUT_FONT = new PhetFont( 20 );
        private static final int PIE_CHART_DIAMETER = 100; // In screen coords, which is close to pixels.
        private static final Stroke CONNECTING_LINE_STROKE = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2, 2}, 0);
        private final int RECTANGLE_INSET_X = 6;

        public AbundanceIndicatorNode( final IDynamicAtom atom ) {

            // Add the line that connects the indicator to the pie chart.
            // This must be added first because it goes behind the other
            // nodes.
            LineShape lineShape = new LineShape( new XYArray( new double[]{0, 0, 0, 0} ) );
            final PLine connectingLine = new PLine( lineShape, CONNECTING_LINE_STROKE );
            addChild( connectingLine );

            // Add the numerical value readout.
            final HTMLNode value = new HTMLNode() {
                {
                    setFont( READOUT_FONT );
                }
            };
            final PhetPPath valueBackground = new PhetPPath( Color.white, new BasicStroke( 1 ), Color.darkGray );
            addChild( valueBackground );
            addChild( value );

            // Add the pie chart.
            final TwoItemPieChartNode pieChart = new TwoItemPieChartNode( PIE_CHART_DIAMETER,
                    atom.getNaturalAbundance(), 1 - atom.getNaturalAbundance() );
            addChild( pieChart );

            // Watch the atom for changes and update the abundance information accordingly.
            atom.addObserver( new SimpleObserver() {
                public void update() {
                    // Show the abundance value
                    final double abundancePercent = atom.getNaturalAbundance() * 100;
                    value.setHTML( abundancePercent < MIN_ABUNDANCE_TO_SHOW && abundancePercent > 0 ? BuildAnAtomStrings.VERY_SMALL : ABUNDANCE_FORMATTER.format( abundancePercent ) + "%" );
                    value.setOffset( 0, PIE_CHART_DIAMETER / 2 - value.getFullBoundsReference().height / 2 );

                    // Expand the white background to contain the text value
                    final Rectangle2D r = RectangleUtils.expand( value.getFullBounds(), RECTANGLE_INSET_X, 3 );
                    valueBackground.setPathTo( new RoundRectangle2D.Double( r.getX(), r.getY(), r.getWidth(), r.getHeight(), 10, 10 ) );

                    // Update the pie chart.
                    pieChart.updateValues( atom.getNaturalAbundance(), 1 - atom.getNaturalAbundance() );
                    pieChart.setOffset( valueBackground.getFullBoundsReference().getMaxX() + 20, 0 );

                    // Update the connecting line.
                    connectingLine.setPoint( 0, valueBackground.getFullBoundsReference().getMaxX(), valueBackground.getFullBoundsReference().getCenterY() );
                    connectingLine.setPoint( 1, pieChart.getFullBoundsReference().getCenterX(), valueBackground.getFullBoundsReference().getCenterY()  );
                }
            } );
        }
    }

    /**
     * This class represents a pie chart that has exactly two slices and is
     * rotated in such a way that the first specified portion is always on
     * the left side.  The intent is that the user can place a label on the
     * left side of the chart that will represent the value.
     *
     * IMPORTANT: The behavior of this node has some important dependencies
     * on the particular behaviors of the PieChartNode.  Specifically, the
     * way this rotates depends on the way the PieChartNode draws itself as
     * of Jan 28, 2011.  Changes to the way the PieChartNode is drawn may
     * impact this class.
     */
    private static class TwoItemPieChartNode extends PNode {

        private static final Color LEFT_SLICE_COLOR = new Color( 134, 102, 172 );
        private static final Color RIGHT_SLICE_COLOR = ColorUtils.darkerColor( BuildAnAtomConstants.CANVAS_BACKGROUND, 0.3 );

        private final PieValue[] pieSlices = new PieValue[] {
                new PieValue( 100, LEFT_SLICE_COLOR ),
                new PieValue( 0, RIGHT_SLICE_COLOR ) };
        private final PieChartNode pieChart;

        /**
         * Constructor.
         */
        public TwoItemPieChartNode( int pieChartDiameter, double initialLeftSliceValue, double initialRightSliceValue ) {

            pieChart = new PieChartNode(
                    pieSlices,
                    new Rectangle( -pieChartDiameter / 2, -pieChartDiameter / 2, pieChartDiameter, pieChartDiameter ) );
            pieChart.setOffset( pieChartDiameter / 2, pieChartDiameter / 2 );
            addChild( pieChart );
            updateValues( initialLeftSliceValue, initialRightSliceValue );
        }

        /**
         * Update the pie chart and rotate it so that the left slice value is
         * centered on the left side.
         */
        public void updateValues( double leftSliceValue, double rightSliceValue ) {
            pieSlices[0].setValue( leftSliceValue );
            pieSlices[1].setValue( rightSliceValue );
            pieChart.setPieValues( pieSlices );
            pieChart.setRotation( -Math.PI * 2 * rightSliceValue / ( rightSliceValue + leftSliceValue ) / 2 );
        }
    }
}
