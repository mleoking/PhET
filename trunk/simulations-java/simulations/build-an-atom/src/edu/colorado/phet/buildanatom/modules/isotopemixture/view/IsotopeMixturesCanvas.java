// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.isotopemixture.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.model.MonoIsotopeParticleBucket;
import edu.colorado.phet.buildanatom.model.SphericalParticle;
import edu.colorado.phet.buildanatom.modules.interactiveisotope.view.IsotopeSliderNode;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.IsotopeMixturesModel;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.MovableAtom;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.IsotopeMixturesModel.InteractivityMode;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.IsotopeMixturesModel.NumericalIsotopeQuantityControl;
import edu.colorado.phet.buildanatom.view.BucketFrontNode;
import edu.colorado.phet.buildanatom.view.BucketHoleNode;
import edu.colorado.phet.buildanatom.view.MaximizeControlNode;
import edu.colorado.phet.buildanatom.view.PeriodicTableControlNode;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode.PieValue;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas for the tab where the user experiments with mixtures of different
 * isotopes.
 */
public class IsotopeMixturesCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    public static final Color BACKGROUND_COLOR = BuildAnAtomConstants.CANVAS_BACKGROUND;
    public static final double DISTANCE_BUTTON_CENTER_FROM_BOTTOM = 30;
    public static final int BUTTON_FONT_SIZE = 18;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    // View
    private final PNode rootNode;

    // Transform.
    private final ModelViewTransform mvt;

    // Nodes that hide and show the pie chart and mass indicator.
    private final MaximizeControlNode pieChartWindow;
    private final MaximizeControlNode averageAtomicMassWindow;

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

    public IsotopeMixturesCanvas( final IsotopeMixturesModel model ) {

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, BuildAnAtomDefaults.STAGE_SIZE ) );

        // Set up the model-canvas transform.  The test chamber is centered
        // at (0, 0) in model space, and this transform is set up to place
        // the chamber where we want it on the canvas.
        //
        // IMPORTANT NOTES: The multiplier factors for the 2nd point can be
        // adjusted to shift the center right or left, and the scale factor
        // can be adjusted to zoom in or out (smaller numbers zoom out, larger
        // ones zoom in).
        mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.width * 0.30 ), (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.height * 0.37 ) ),
                0.16 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        setBackground( BACKGROUND_COLOR );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        // Add the nodes that will allow the canvas to be layered.
        final PNode bucketHoleLayer = new PNode();
        rootNode.addChild( bucketHoleLayer );
        PNode chamberLayer = new PNode();
        rootNode.addChild( chamberLayer );
        final PNode particleLayer = new PNode();
        rootNode.addChild( particleLayer );
        final PNode bucketFrontLayer = new PNode();
        rootNode.addChild( bucketFrontLayer );
        final PNode controlsLayer = new PNode();
        rootNode.addChild( controlsLayer );

        // Listen to the model for events that concern the canvas.
        model.addListener( new IsotopeMixturesModel.Adapter() {
            @Override
            public void isotopeInstanceAdded( final MovableAtom atom ) {
                // Add a representation of the new atom to the canvas.
                final LabeledIsotopeNode isotopeNode = new LabeledIsotopeNode( mvt, atom, model.getColorForIsotope( atom.getAtomConfiguration() ) );
                particleLayer.addChild( isotopeNode );
                atom.addListener( new SphericalParticle.Adapter() {
                    @Override
                    public void removedFromModel( SphericalParticle particle ){
                        particleLayer.removeChild( isotopeNode );
                    }
                } );
                // If the model is portraying "nature's mix" of isotopes,
                // disallow user interaction with these particles.
                if ( model.getShowingNaturesMixProperty().getValue() ){
                    isotopeNode.setPickable( false );
                    isotopeNode.setChildrenPickable( false );
                }
            }
            @Override
            public void isotopeBucketAdded( final MonoIsotopeParticleBucket bucket ) {
                final BucketFrontNode bucketFrontNode = new BucketFrontNode( bucket, mvt );
                bucketFrontNode.setOffset( mvt.modelToView( bucket.getPosition() ) );
                final BucketHoleNode bucketHoleNode = new BucketHoleNode( bucket, mvt );
                bucketHoleNode.setOffset( mvt.modelToView( bucket.getPosition() ) );
                bucketFrontLayer.addChild( bucketFrontNode );
                bucketHoleLayer.addChild( bucketHoleNode );
                bucket.getPartOfModelProperty().addObserver( new SimpleObserver() {
                    public void update() {
                        // Remove the representation of the bucket when the bucket
                        // itself is removed from the model.
                        if ( !bucket.getPartOfModelProperty().getValue() ){
                            bucketFrontLayer.removeChild( bucketFrontNode );
                            bucketHoleLayer.removeChild( bucketHoleNode );
                        }
                    }
                }, false );
            }
            @Override
            public void isotopeNumericalControllerAdded( final NumericalIsotopeQuantityControl controller ) {
                final IsotopeSliderNode controllerNode = new IsotopeSliderNode( controller, mvt );
                controlsLayer.addChild( controllerNode );
                controller.getPartOfModelProperty().addObserver( new SimpleObserver() {
                    public void update() {
                        if ( !controller.getPartOfModelProperty().getValue() ){
                            // Remove the representation of the bucket when the bucket
                            // itself is removed from the model.
                            controlsLayer.removeChild( controllerNode );
                        }
                    }
                }, false );
            }
        });

        // Add the test chamber into and out of which the individual isotopes
        // will be moved. As with all elements in this model, the shape and
        // position are considered to be two separate things.
        final PhetPPath testChamberNode = new PhetPPath( Color.BLACK ){{
            setPathTo( mvt.modelToView( model.getIsotopeTestChamber().getTestChamberRect() ) );
        }};
        chamberLayer.addChild( testChamberNode );

        // Add the periodic table node that will allow the user to set the
        // current isotope.
        PNode periodicTableNode = new PeriodicTableControlNode( model, 18, BACKGROUND_COLOR ){{
            setOffset( testChamberNode.getFullBoundsReference().getMaxX() + 15, testChamberNode.getFullBoundsReference().getMinY() );
            setScale( 1.1 ); // Empirically determined.
        }};
        controlsLayer.addChild( periodicTableNode );

        // Add the pie chart to the canvas.
        double indicatorWindowX = 600;
        final PNode pieChart = new IsotopeProprotionPieChart( model );
        // TODO: i18n
        pieChartWindow = new MaximizeControlNode( "Percent Composition", new PDimension( 400, 150 ), pieChart, true );
        pieChartWindow.setOffset( indicatorWindowX, periodicTableNode.getFullBoundsReference().getMaxY() + 30 );
        controlsLayer.addChild( pieChartWindow );
        pieChart.setOffset( 150, 40 ); // Empirically determined, tweak as needed.
        pieChartWindow.addChild( pieChart );

        // Add the average atomic mass indicator to the canvas.
        PNode averageAtomicMassIndicator = new AverageAtomicMassIndicator( model );
        // TODO: i18n
        averageAtomicMassWindow = new MaximizeControlNode( "Average Atomic Mass", new PDimension( 400, 120 ), averageAtomicMassIndicator, true );
        averageAtomicMassWindow.setOffset( indicatorWindowX, pieChartWindow.getFullBoundsReference().getMaxY() + 30 );
        controlsLayer.addChild( averageAtomicMassWindow );
        averageAtomicMassIndicator.setOffset(
                averageAtomicMassWindow.getFullBoundsReference().width / 2 - averageAtomicMassIndicator.getFullBoundsReference().width / 2,
                30 /* Empirically determined, tweak as needed. */ );
        averageAtomicMassWindow.addChild( averageAtomicMassIndicator );

        // Add the radio buttons that allow the user to choose between their
        // mix and nature's mix.
        JPanel radioButtonPanel = new VerticalLayoutPanel();
        // TODO: i18n
        PropertyRadioButton<Boolean> usersMixRadioButton = new PropertyRadioButton<Boolean>( "My mix of isotopes", model.getShowingNaturesMixProperty(), false ){{
            setBackground( BACKGROUND_COLOR );
            setFont( new PhetFont( 20 ) );
        }};
        radioButtonPanel.add( usersMixRadioButton );
        // TODO: i18n
        PropertyRadioButton<Boolean> naturesMixRadioButton = new PropertyRadioButton<Boolean>( "Nature's mix of isotopes", model.getShowingNaturesMixProperty(), true ){{
            setBackground( BACKGROUND_COLOR );
            setFont( new PhetFont( 20 ) );
        }};
        radioButtonPanel.add( naturesMixRadioButton );
        controlsLayer.addChild( new PSwing( radioButtonPanel ){{
            setOffset(
                testChamberNode.getFullBoundsReference().getMaxX() + 140,
                averageAtomicMassWindow.getFullBoundsReference().getMaxY() + 20 );
        }} );

        // Add the button that allows the user to select between the smaller
        // and larger atoms.
        final Point2D moreLessButtonLocation = new Point2D.Double( testChamberNode.getFullBoundsReference().getCenterX(),
                BuildAnAtomDefaults.STAGE_SIZE.height - DISTANCE_BUTTON_CENTER_FROM_BOTTOM );
        // TODO: i18n
        final ButtonNode moreAtomsButton = new ButtonNode( "More", BUTTON_FONT_SIZE, new Color(255, 153, 0) ){{
            centerFullBoundsOnPoint( moreLessButtonLocation.getX(), moreLessButtonLocation.getY() );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.getInteractivityModeProperty().setValue( InteractivityMode.SLIDERS_AND_SMALL_ATOMS );
                }
            });
        }};
        controlsLayer.addChild( moreAtomsButton );
        // TODO: i18n
        final ButtonNode lessAtomsButton = new ButtonNode( "Less", BUTTON_FONT_SIZE, new Color( 0, 178, 138 ) ){{
            centerFullBoundsOnPoint( moreLessButtonLocation.getX(), moreLessButtonLocation.getY() );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.getInteractivityModeProperty().setValue( InteractivityMode.BUCKETS_AND_LARGE_ATOMS );
                }
            });
        }};
        controlsLayer.addChild( lessAtomsButton );

        // Create a simple observer that will update the more/less button
        // visibility and hook it up to be triggered on changes to relevant
        // model properties.
        SimpleObserver moreLessButtonVizUpdater = new SimpleObserver() {
            public void update() {
                moreAtomsButton.setVisible( model.getInteractivityModeProperty().getValue() == InteractivityMode.BUCKETS_AND_LARGE_ATOMS && model.getShowingNaturesMixProperty().getValue() == false );
                lessAtomsButton.setVisible( model.getInteractivityModeProperty().getValue() == InteractivityMode.SLIDERS_AND_SMALL_ATOMS && model.getShowingNaturesMixProperty().getValue() == false );
            }
        };
        model.getInteractivityModeProperty().addObserver( moreLessButtonVizUpdater );
        model.getShowingNaturesMixProperty().addObserver( moreLessButtonVizUpdater );

        // Add the button that clears the test chamber.
        ButtonNode clearTestChamberButton = new ButtonNode( "Clear Box", BUTTON_FONT_SIZE, new Color( 255, 153, 0 ) ){{
            centerFullBoundsOnPoint( averageAtomicMassWindow.getFullBoundsReference().getMinX() + 80,
                    BuildAnAtomDefaults.STAGE_SIZE.height - DISTANCE_BUTTON_CENTER_FROM_BOTTOM );
        }};
        controlsLayer.addChild( clearTestChamberButton );

        // Add the "Reset All" button.
        ResetAllButtonNode resetButtonNode = new ResetAllButtonNode( model, this, BUTTON_FONT_SIZE, Color.BLACK,
                new Color( 255, 153, 0 ) ){{
            setConfirmationEnabled( false );
            centerFullBoundsOnPoint( averageAtomicMassWindow.getFullBoundsReference().getMaxX() - 80,
                    BuildAnAtomDefaults.STAGE_SIZE.height - DISTANCE_BUTTON_CENTER_FROM_BOTTOM );
        }};
        controlsLayer.addChild( resetButtonNode );
    }

    /**
     * Class that represents a pie chart portraying the proportion of the
     * various isotopes in the test chamber.
     *
     * @author John Blanco
     */
    private static class IsotopeProprotionPieChart extends PNode {

        private static final Dimension SIZE = new Dimension( 100, 100 );

        /**
         * Constructor.
         */
        public IsotopeProprotionPieChart( final IsotopeMixturesModel model ) {
            final PieChartNode pieChart = new PieChartNode( new PieValue[] { new PieValue( 100, Color.red ) },
                    new Rectangle(0, 0, SIZE.width, SIZE.height ) );
            pieChart.setOffset( 0, 0 );
            addChild( pieChart );
            model.getIsotopeTestChamber().addTotalCountChangeObserver( new SimpleObserver(){
                public void update() {
                    int isotopeCount = model.getIsotopeTestChamber().getTotalIsotopeCount();
                    // Hide the chart if there is nothing in the chamber.
                    pieChart.setVisible( isotopeCount > 0 );
                    if ( isotopeCount > 0 ){
                        // Update the proportions.
                        PieValue[] pieSlices = new PieValue[model.getPossibleIsotopesProperty().getValue().size()];
                        int sliceCount = 0;
                        for ( ImmutableAtom isotope : model.getPossibleIsotopesProperty().getValue() ){
                            pieSlices[sliceCount++] =
                                new PieValue( model.getIsotopeTestChamber().getIsotopeProportion( isotope ),
                                        model.getColorForIsotope( isotope ) );
                        }
                        pieChart.setPieValues( pieSlices );
                    }
                }

            });
        }
    }
}
