// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.isotopemixture.view;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.MonoIsotopeParticleBucket;
import edu.colorado.phet.buildanatom.model.SphericalParticle;
import edu.colorado.phet.buildanatom.modules.interactiveisotope.view.IsotopeSliderNode;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.MixIsotopesModel;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.MovableAtom;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.MixIsotopesModel.InteractivityMode;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.MixIsotopesModel.NumericalIsotopeQuantityControl;
import edu.colorado.phet.buildanatom.view.BucketView;
import edu.colorado.phet.buildanatom.view.MaximizeControlNode;
import edu.colorado.phet.buildanatom.view.PeriodicTableControlNode;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas for the tab where the user experiments with mixtures of different
 * isotopes.
 */
public class MixIsotopesCanvas extends PhetPCanvas implements Resettable {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    public static final Color BACKGROUND_COLOR = BuildAnAtomConstants.CANVAS_BACKGROUND;
    public static final double DISTANCE_BUTTON_CENTER_FROM_BOTTOM = 30;
    public static final int BUTTON_FONT_SIZE = 18;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    // Model
    private final MixIsotopesModel model;

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

    public MixIsotopesCanvas( final MixIsotopesModel model ) {
        this.model = model;

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
                new Point( (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.width * 0.295 ), (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.height * 0.38 ) ),
                0.16 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        setBackground( BACKGROUND_COLOR );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        // Add the nodes that will allow the canvas to be layered.
        final PNode controlsLayer = new PNode();
        rootNode.addChild( controlsLayer );
        final PNode bucketHoleLayer = new PNode();
        rootNode.addChild( bucketHoleLayer );
        PNode chamberLayer = new PNode();
        rootNode.addChild( chamberLayer );
        final PNode particleLayer = new PNode();
        rootNode.addChild( particleLayer );
        final PNode bucketFrontLayer = new PNode();
        rootNode.addChild( bucketFrontLayer );

        // Listen to the model for events that concern the canvas.
        model.addListener( new MixIsotopesModel.Adapter() {
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
                // Only allow interaction with the atoms when showing the
                // buckets and when not showing nature's mix.
                boolean interactiveParticles = model.getInteractivityModeProperty().getValue() == InteractivityMode.BUCKETS_AND_LARGE_ATOMS && !model.getShowingNaturesMixProperty().getValue();
                isotopeNode.setPickable( interactiveParticles );
                isotopeNode.setChildrenPickable( interactiveParticles );
            }
            @Override
            public void isotopeBucketAdded( final MonoIsotopeParticleBucket bucket ) {
                final BucketView bucketView = new BucketView( bucket, mvt );
                bucketHoleLayer.addChild( bucketView.getHoleNode() );
                bucketFrontLayer.addChild( bucketView.getFrontNode() );
                bucket.getPartOfModelProperty().addObserver( new SimpleObserver() {
                    public void update() {
                        // Remove the representation of the bucket when the bucket
                        // itself is removed from the model.
                        if ( !bucket.getPartOfModelProperty().getValue() ){
                            bucketFrontLayer.removeChild( bucketView.getFrontNode() );
                            bucketHoleLayer.removeChild( bucketView.getHoleNode() );
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
        final PNode periodicTableNode = new PeriodicTableControlNode( model, 18, BACKGROUND_COLOR ){{
            setOffset( testChamberNode.getFullBoundsReference().getMaxX() + 15, testChamberNode.getFullBoundsReference().getMinY() );
            setScale( 1.1 ); // Empirically determined.
        }};
        controlsLayer.addChild( periodicTableNode );

        // Add the pie chart to the canvas.
        final double indicatorWindowX = periodicTableNode.getFullBoundsReference().getX();
        final PNode pieChart = new IsotopeProprotionsPieChart( model );
        pieChart.setOffset( 200, 90 ); // Empirically determined, tweak as needed.
        pieChartWindow = new MaximizeControlNode( BuildAnAtomStrings.PERCENT_COMPOSITION, new PDimension( 400, 155 ), pieChart, true ){{
            setOffset( indicatorWindowX, periodicTableNode.getFullBoundsReference().getMaxY() + 25 );
            addChild( pieChart );
        }};
        controlsLayer.addChild( pieChartWindow );

        // Add the average atomic mass indicator to the canvas.
        final PNode averageAtomicMassIndicator = new AverageAtomicMassIndicator( model );
        averageAtomicMassWindow = new MaximizeControlNode( BuildAnAtomStrings.AVERAGE_ATOMIC_MASS, new PDimension( 400, 120 ), averageAtomicMassIndicator, true ){{
            setOffset( indicatorWindowX, testChamberNode.getFullBoundsReference().getMaxY() - getFullBoundsReference().height );
            addChild( averageAtomicMassIndicator );
        }};
        controlsLayer.addChild( averageAtomicMassWindow );
        averageAtomicMassIndicator.setOffset(
                averageAtomicMassWindow.getFullBoundsReference().width / 2 - averageAtomicMassIndicator.getFullBoundsReference().width / 2,
                30 /* Empirically determined, tweak as needed. */ );

        // Add the radio buttons that allow the user to choose between their
        // mix and nature's mix.
        final PropertyRadioButton<Boolean> usersMixRadioButton = new PropertyRadioButton<Boolean>( BuildAnAtomStrings.MY_MIX_OF_ISOTOPES, model.getShowingNaturesMixProperty(), false ){{
            setFont( new PhetFont( 20 ) );
        }};
        final PropertyRadioButton<Boolean> naturesMixRadioButton = new PropertyRadioButton<Boolean>( BuildAnAtomStrings.NATURES_MIX_OF_ISOTOPES, model.getShowingNaturesMixProperty(), true ){{
            setFont( new PhetFont( 20 ) );
        }};
        JPanel radioButtonPanel = new VerticalLayoutPanel(){{
            add( usersMixRadioButton );
            add( naturesMixRadioButton );
            SwingUtils.setBackgroundDeep( this, BACKGROUND_COLOR );
        }};
        controlsLayer.addChild( new PSwing( radioButtonPanel ){{
            setOffset(
                testChamberNode.getFullBoundsReference().getMaxX() + 140,
                averageAtomicMassWindow.getFullBoundsReference().getMaxY() + 20 );
        }} );

        // Add the button that allows the user to select between the smaller
        // and larger atoms.
        final Point2D moreLessButtonLocation = new Point2D.Double( testChamberNode.getFullBoundsReference().getCenterX(),
                BuildAnAtomDefaults.STAGE_SIZE.height - DISTANCE_BUTTON_CENTER_FROM_BOTTOM );
        final ButtonNode moreAtomsButton = new ButtonNode( BuildAnAtomStrings.MORE, BUTTON_FONT_SIZE, new Color( 0, 198, 158 ) ){{
            centerFullBoundsOnPoint( moreLessButtonLocation.getX(), moreLessButtonLocation.getY() );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.getInteractivityModeProperty().setValue( InteractivityMode.SLIDERS_AND_SMALL_ATOMS );
                }
            });
        }};
        controlsLayer.addChild( moreAtomsButton );
        final ButtonNode lessAtomsButton = new ButtonNode( BuildAnAtomStrings.LESS, BUTTON_FONT_SIZE, new Color( 159, 182, 205 ) ){{
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
        final ButtonNode clearTestChamberButton = new ButtonNode( BuildAnAtomStrings.CLEAR_BOX, BUTTON_FONT_SIZE, new Color( 255, 153, 0 ) ){{
            centerFullBoundsOnPoint( averageAtomicMassWindow.getFullBoundsReference().getMinX() + 80,
                    BuildAnAtomDefaults.STAGE_SIZE.height - DISTANCE_BUTTON_CENTER_FROM_BOTTOM );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.clearTestChamber();
                }
            });
        }};
        controlsLayer.addChild( clearTestChamberButton );

        // Create and hook up a visibility controller for the button that
        // clears the test chamber.
        SimpleObserver clearBoxButtonVizUpdater = new SimpleObserver() {
            public void update() {
                clearTestChamberButton.setVisible( model.getIsotopeTestChamber().getTotalIsotopeCount() > 0 && !model.getShowingNaturesMixProperty().getValue() );
            }
        };
        model.getShowingNaturesMixProperty().addObserver( clearBoxButtonVizUpdater );
        model.getIsotopeTestChamber().addTotalCountChangeObserver( clearBoxButtonVizUpdater );

        // Add the "Reset All" button.
        ResetAllButtonNode resetButtonNode = new ResetAllButtonNode( this, this, BUTTON_FONT_SIZE, Color.BLACK,
                new Color( 255, 153, 0 ) ){{
            setConfirmationEnabled( false );
            centerFullBoundsOnPoint( averageAtomicMassWindow.getFullBoundsReference().getMaxX() - 80,
                    BuildAnAtomDefaults.STAGE_SIZE.height - DISTANCE_BUTTON_CENTER_FROM_BOTTOM );
        }};
        controlsLayer.addChild( resetButtonNode );
    }

    public void reset() {
        // Note that this resets the model, so be careful about hooking this
        // up to any reset coming from the model or you will end up in
        // Nastyrecursionville.
        model.reset();

        // Reset the view componenets.
        pieChartWindow.setMaximized( true );
        averageAtomicMassWindow.setMaximized( true );
    }
}
