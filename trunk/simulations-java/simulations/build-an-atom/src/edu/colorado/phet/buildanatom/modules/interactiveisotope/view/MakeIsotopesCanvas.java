// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.interactiveisotope.view;

import static edu.colorado.phet.buildanatom.BuildAnAtomDefaults.STAGE_SIZE;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.AtomListener;
import edu.colorado.phet.buildanatom.model.IDynamicAtom;
import edu.colorado.phet.buildanatom.modules.interactiveisotope.model.MakeIsotopesModel;
import edu.colorado.phet.buildanatom.view.ElementNameIndicator;
import edu.colorado.phet.buildanatom.view.MaximizeControlNode;
import edu.colorado.phet.buildanatom.view.ParticleCountLegend;
import edu.colorado.phet.buildanatom.view.PeriodicTableControlNode;
import edu.colorado.phet.buildanatom.view.StabilityIndicator;
import edu.colorado.phet.buildanatom.view.SymbolIndicatorNode;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode.PieValue;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PLine;
import edu.umd.cs.piccolox.util.LineShape;
import edu.umd.cs.piccolox.util.XYArray;

/**
 * Canvas for the tab where the user makes isotopes of a given element by
 * adding and removing neutrons.
 */
public class MakeIsotopesCanvas extends PhetPCanvas implements Resettable {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    private static final Color BACKGROUND_COLOR = BuildAnAtomConstants.CANVAS_BACKGROUND;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    // Model
    private final MakeIsotopesModel model;

    // View
    private final PNode rootNode;

    // Transform.
    private final ModelViewTransform mvt;

    private final MaximizeControlNode symbolWindow;
    private final MaximizeControlNode abundanceWindow;

    private final AtomScaleNode scaleNode;

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

    public MakeIsotopesCanvas( final MakeIsotopesModel model ) {
        this.model = model;

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );

        // Set up the model-canvas transform.  IMPORTANT NOTES: The multiplier
        // factors for the point in the view can be adjusted to shift the
        // center right or left, and the scale factor can be adjusted to zoom
        // in or out (smaller numbers zoom out, larger ones zoom in).
        mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( STAGE_SIZE.width * 0.32 ), (int) Math.round( STAGE_SIZE.height * 0.49 ) ),
                2.0 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        setBackground( BACKGROUND_COLOR );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        // Add the legend/particle count indicator.
        final ParticleCountLegend particleCountLegend = new ParticleCountLegend( model.getAtom(), BACKGROUND_COLOR );
        particleCountLegend.setScale( 1.1 );
        particleCountLegend.setOffset( 20, 10 );
        rootNode.addChild( particleCountLegend );

        // Create the node that represents the scale upon which the atom sits.
        scaleNode = new AtomScaleNode( model.getAtom() ) {{
                // The scale needs to sit just below the atom, and there are some
                // "tweak factors" needed to get it looking right.
                setOffset( mvt.modelToViewX( 0 ) - getFullBoundsReference().width / 2, 530 );
        }};

        // Create the node that contains both the atom and the neutron bucket.
        Point2D topCenterOfScale = new Point2D.Double( scaleNode.getFullBoundsReference().getCenterX(),
                scaleNode.getFullBoundsReference().getMinY() + scaleNode.getWeighPlateTopProjectedHeight() / 2 );
        final InteractiveIsotopeNode atomAndBucketNode = new InteractiveIsotopeNode( model, mvt, topCenterOfScale );

        // Add the scale followed by the atom so that the layering effect is
        // correct.
        rootNode.addChild( scaleNode );
        rootNode.addChild( atomAndBucketNode );

        // Add the "My Isotope" label.
        final PText myIsotopeLabel = new PText(BuildAnAtomStrings.MY_ISOTOPE){{
            setFont( new PhetFont( 24, true ) );
            setTextPaint( Color.DARK_GRAY );
            setOffset(
                    mvt.modelToViewX( 0 ) - getFullBoundsReference().width / 2,
                    particleCountLegend.getFullBoundsReference().getMaxY() + 30 );
        }};
        rootNode.addChild( myIsotopeLabel );
        atomAndBucketNode.addElectronCloudBoundsChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                // Position the "My Isotope" indicator to be just above the
                // electron cloud.
                myIsotopeLabel.setOffset(
                        mvt.modelToViewX( model.getAtom().getPosition().getX()) - myIsotopeLabel.getFullBoundsReference().width / 2,
                        mvt.modelToViewY( model.getAtom().getPosition().getY() ) - atomAndBucketNode.getCloudRadius() - myIsotopeLabel.getFullBoundsReference().height - 4);
            }
        });

        // Add indicator that shows the name of the element.
        final ElementNameIndicator elementNameIndicator = new ElementNameIndicator( model.getAtom(), new BooleanProperty( true ), true ){{
            setFont( new PhetFont( 20, true ) );
            setColor( Color.BLACK );
            setOffset( mvt.modelToViewX( 0 ), myIsotopeLabel.getFullBoundsReference().getMaxY() + getFullBoundsReference().height );
        }};
        rootNode.addChild( elementNameIndicator );

        // Add indicator that shows whether the nucleus is stable.
        final StabilityIndicator stabilityIndicator = new StabilityIndicator( model.getAtom(), new BooleanProperty( true ) );
        rootNode.addChild( stabilityIndicator );

        // Add functionality to position the labels based on the location of
        // the nucleus.
        model.getAtom().addAtomListener( new AtomListener.Adapter(){
            @Override
            public void postitionChanged(){
                updateLabelPositions();
            }
            @Override
            public void configurationChanged() {
                updateLabelPositions();
            }
            private void updateLabelPositions(){
                double centerX = model.getAtom().getPosition().getX();
                double centerY = model.getAtom().getPosition().getY();
                elementNameIndicator.setOffset(
                        mvt.modelToViewX( centerX ),
                        mvt.modelToViewY( centerY ) - elementNameIndicator.getFullBounds().height - 25 );
                stabilityIndicator.setOffset(
                        mvt.modelToViewX( centerX ),
                        mvt.modelToViewY( centerY ) + elementNameIndicator.getFullBounds().height + 20 );
            }
        });

        // Add the interactive periodic table that allows the user to select
        // the current element.
        final PeriodicTableControlNode periodicTableNode = new PeriodicTableControlNode( model,
                10, BACKGROUND_COLOR ) {
            {
                setScale( 1.3 );
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
        symbolWindow.setOffset( indicatorWindowPosX, 270 );
        rootNode.addChild( symbolWindow );

        // Add the node that indicates the percentage abundance.
        final PDimension abundanceWindowSize = new PDimension( 400, 150 );
        final PNode abundanceIndicatorNode = new AbundanceIndicatorNode( model.getAtom() );
        abundanceIndicatorNode.setOffset(
                abundanceWindowSize.getWidth() / 2 + 40,    // Tweak factor empirically determined.
                abundanceWindowSize.getHeight() / 2 + 10);  // Tweak factor empirically determined.
        abundanceWindow = new MaximizeControlNode( BuildAnAtomStrings.ABUNDANCE_IN_NATURE, abundanceWindowSize, abundanceIndicatorNode, true );
        abundanceWindow.setOffset( indicatorWindowPosX, symbolWindow.getFullBoundsReference().getMaxY() + 30 );
        rootNode.addChild( abundanceWindow );

        // Add the "Reset All" button.
        ResetAllButtonNode resetButtonNode = new ResetAllButtonNode( this, this, 16, Color.BLACK, new Color( 255, 153, 0 ) ){{
            setConfirmationEnabled( false );
        }};
        double desiredResetButtonWidth = 100;
        resetButtonNode.setScale( desiredResetButtonWidth / resetButtonNode.getFullBoundsReference().width );
        rootNode.addChild( resetButtonNode );

        resetButtonNode.centerFullBoundsOnPoint(
                abundanceWindow.getFullBoundsReference().getCenterX(),
                BuildAnAtomDefaults.STAGE_SIZE.height - resetButtonNode.getFullBoundsReference().height );

        // Close up the maximizable nodes that contain some of the indicators.
        // This is done here rather than when they are constructed because
        // they need to be at their full size for initial layout.
        symbolWindow.setMaximized( false );
        abundanceWindow.setMaximized( false );
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    public void reset() {
        // Note that this resets the model, so be careful about hooking this
        // up to any reset coming from the model or you will end up in
        // Nastyrecursionville.
        model.reset();

        // Reset the view componenets.
        symbolWindow.setMaximized( false );
        abundanceWindow.setMaximized( false );
        scaleNode.reset();
    }

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    /**
     * Shows the abundance readout for a user-selected isotope.  Note that the
     * center of this node matches the center of the pie chart.  This helps to
     * minimize the impact of translations, number lengths, etc. on the layout
     * of the node.
     */
    private static class AbundanceIndicatorNode extends PNode {

        private static DecimalFormat ABUNDANCE_FORMATTER = new DecimalFormat( "#.####" );
        private static final double MIN_ABUNDANCE_TO_SHOW = 0.0001; // Should match the resolution of the ABUNDANCE_FORMATTER
        private static final Font READOUT_FONT = new PhetFont( 20 );
        private static final int PIE_CHART_DIAMETER = 100; // In screen coords, which is close to pixels.
        private static final int CONNECTING_LINE_LEGNTH = 40; // In screen coords, which is close to pixels.
        private static final Stroke CONNECTING_LINE_STROKE = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5, 3}, 0);
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

            // Add the caption for the numerical readout.
            final PText readoutCaption = new PText( BuildAnAtomStrings.THIS_ISOTOPE ){{
                setFont( new PhetFont( 18 ) );
                valueBackground.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
                    public void propertyChange( PropertyChangeEvent evt ) {
                          centerFullBoundsOnPoint(
                                  valueBackground.getFullBoundsReference().getCenterX(),
                                  valueBackground.getFullBoundsReference().getMinY() - getFullBoundsReference().height / 2 - 2 );
                    }
                });
            }};
            addChild( readoutCaption );

            // Add the pie chart.
            final TwoItemPieChartNode pieChart = new TwoItemPieChartNode( PIE_CHART_DIAMETER,
                    atom.getNaturalAbundance(), 1 - atom.getNaturalAbundance() );
            addChild( pieChart );

            // Add caption for right side of pie chart.  This caption labels
            // the section of the pie chart the refers to other isotopes.
            final HTMLNode otherIsotopesCaption = new HTMLNode(){{
                setFont( new PhetFont( 18 ) );
                atom.addAtomListener( new AtomListener.Adapter() {
                    @Override
                    public void configurationChanged() {
                        String caption = "<center>" + BuildAnAtomStrings.OTHER + "<br>" + atom.getName() + "<br>" +
                            BuildAnAtomStrings.ISOTOPES + "</center>";
                        setHTML( caption );
                        setOffset(
                                pieChart.getFullBoundsReference().getMaxX() + 4,
                                pieChart.getFullBoundsReference().getCenterY() - getFullBoundsReference().height / 2 );
                        setVisible( atom.getNaturalAbundance() < 1.0 ); // Don't show if no other isotopes exist.
                    }
                });
            }};

            addChild( otherIsotopesCaption );

            // Position the pie chart in the center of this node.  Everything
            // else is laid out relative to this node.
            pieChart.setOffset( 0, 0 );

            // Create the listener for watching the atom configuration and making updates.
            AtomListener atomConfigListener = new AtomListener.Adapter() {
                @Override
                public void configurationChanged() {
                    // Show the abundance value
                    final double abundancePercent = atom.getNaturalAbundance() * 100;
                    value.setHTML( abundancePercent < MIN_ABUNDANCE_TO_SHOW && abundancePercent > 0 ? BuildAnAtomStrings.VERY_SMALL : ABUNDANCE_FORMATTER.format( abundancePercent ) + "%" );
                    value.setOffset( 
                            pieChart.getFullBoundsReference().getMinX() - value.getFullBoundsReference().getWidth() - CONNECTING_LINE_LEGNTH,
                            pieChart.getFullBoundsReference().getCenterY() - value.getFullBoundsReference().height / 2 );

                    // Expand the white background to contain the text value
                    final Rectangle2D r = RectangleUtils.expand( value.getFullBounds(), RECTANGLE_INSET_X, 3 );
                    valueBackground.setPathTo( new RoundRectangle2D.Double( r.getX(), r.getY(), r.getWidth(), r.getHeight(), 10, 10 ) );

                    // Update the pie chart.
                    pieChart.updateValues( atom.getNaturalAbundance(), 1 - atom.getNaturalAbundance() );

                    // Update the connecting line.  Don't display if pie chart isn't there.
                    connectingLine.setPoint( 0, valueBackground.getFullBoundsReference().getMaxX(), valueBackground.getFullBoundsReference().getCenterY() );
                    connectingLine.setPoint( 1, pieChart.getFullBoundsReference().getCenterX(), valueBackground.getFullBoundsReference().getCenterY()  );
                    connectingLine.setVisible( atom.getNaturalAbundance() > 0 );
                }
            };

            // Watch the atom for changes and update the abundance information accordingly.
            atom.addAtomListener( atomConfigListener );

            // Do the initial update to establish the layout.
            atomConfigListener.configurationChanged();
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
        private static final Color RIGHT_SLICE_COLOR = BACKGROUND_COLOR;

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
