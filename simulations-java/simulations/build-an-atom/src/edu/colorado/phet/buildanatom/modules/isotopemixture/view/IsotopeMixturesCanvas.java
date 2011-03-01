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
import edu.colorado.phet.buildanatom.model.Bucket;
import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.IsotopeMixturesModel;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.MovableAtom;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.IsotopeMixturesModel.IsotopeSize;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.IsotopeMixturesModel.Listener;
import edu.colorado.phet.buildanatom.view.BucketFrontNode;
import edu.colorado.phet.buildanatom.view.BucketHoleNode;
import edu.colorado.phet.buildanatom.view.PeriodicTableControlNode;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode.PieValue;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas for the tab where the user experiments with mixtures of different
 * isotopes.
 */
public class IsotopeMixturesCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    private static final Color BACKGROUND_COLOR = BuildAnAtomConstants.CANVAS_BACKGROUND;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    // View
    private final PNode rootNode;

    // Transform.
    private final ModelViewTransform2D mvt;

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
        // IMPORTANT NOTES: The multiplier factors for the point in the view
        // can be adjusted to shift the center right or left, and the scale
        // factor can be adjusted to zoom in or out (smaller numbers zoom out,
        // larger ones zoom in).
        mvt = new ModelViewTransform2D(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.width * 0.30 ), (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.height * 0.37 ) ),
                0.16, // "Zoom factor" - smaller zooms out, larger zooms in.
                true );

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
        model.addListener( new Listener() {
            public void isotopeInstanceAdded( final MovableAtom atom ) {
                // Add a representation of the new atom to the canvas.
                final LabeledIsotopeNode isotopeNode = new LabeledIsotopeNode( mvt, atom, model.getColorForIsotope( atom.getAtomConfiguration() ) );
                particleLayer.addChild( isotopeNode );
                atom.getPartOfModelProperty().addObserver( new SimpleObserver(){
                    public void update() {
                        if ( !atom.getPartOfModelProperty().getValue() )
                        particleLayer.removeChild( isotopeNode );
                    }
                }, false);
            }
        });

        // Add the test chamber into and out of which the individual isotopes
        // will be moved. As with all elements in this model, the shape and
        // position are considered to be two separate things.
        final PhetPPath testChamberNode = new PhetPPath( Color.BLACK ){{
            setPathTo( mvt.createTransformedShape( model.getIsotopeTestChamber().getTestChamberRect() ) );
        }};
        chamberLayer.addChild( testChamberNode );

        // Add the periodic table node that will allow the user to set the
        // current isotope.
        PNode periodicTableNode = new PeriodicTableControlNode( model, 18, BACKGROUND_COLOR ){{
            setOffset( testChamberNode.getFullBoundsReference().getMaxX() + 15, testChamberNode.getFullBoundsReference().getMinY() );
            setScale( 1.1 ); // Empirically determined.
        }};
        controlsLayer.addChild( periodicTableNode );

        // Listen to the bucket list property in the model and update our
        // buckets if and when the list changes.
        model.getBucketListProperty().addObserver( new SimpleObserver() {
            public void update() {
                bucketHoleLayer.removeAllChildren();
                bucketFrontLayer.removeAllChildren();
                for ( Bucket bucket : model.getBucketListProperty().getValue() ) {
                    BucketFrontNode bucketFrontNode = new BucketFrontNode( bucket, mvt );
                    bucketFrontNode.setOffset( mvt.modelToViewDouble( bucket.getPosition() ) );
                    BucketHoleNode bucketHoleNode = new BucketHoleNode( bucket, mvt );
                    bucketHoleNode.setOffset( mvt.modelToViewDouble( bucket.getPosition() ) );
                    bucketFrontLayer.addChild( bucketFrontNode );
                    bucketHoleLayer.addChild( bucketHoleNode );
                }
            }
        });

        // Add the pie chart to the canvas.
        final PNode pieChart = new IsotopeProprotionPieChart( model );
        pieChart.setOffset( 650, 190 );
        controlsLayer.addChild( pieChart );

        // Add the average atomic mass indicator to the canvas.
        PNode averageAtomicMassIndicator = new AverageAtomicMassIndicator( model );
        averageAtomicMassIndicator.setOffset( pieChart.getOffset().getX(),
                pieChart.getFullBoundsReference().getMaxY() + 10 );
        controlsLayer.addChild( averageAtomicMassIndicator );

        // Add the button that allows the user to select between the smaller
        // and larger atoms.
        final Point2D buttonLocation = new Point2D.Double( 355, 650 );
        // TODO: i18n
        final ButtonNode moreAtomsButton = new ButtonNode( "More", 20, new Color(255, 153, 0) ){{
            centerFullBoundsOnPoint( buttonLocation.getX(), buttonLocation.getY() );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.getAtomSizeProperty().setValue( IsotopeSize.SMALL );
                }
            });
        }};
        controlsLayer.addChild( moreAtomsButton );
        // TODO: i18n
        final ButtonNode lessAtomsButton = new ButtonNode( "Less", 20, new Color( 0, 178, 138 ) ){{
            centerFullBoundsOnPoint( buttonLocation.getX(), buttonLocation.getY() );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.getAtomSizeProperty().setValue( IsotopeSize.LARGE );
                }
            });
        }};
        controlsLayer.addChild( lessAtomsButton );

        // Create a simple observer that will update the button visibility and
        // hook it up to be triggered on changes to relevant model properties.
        SimpleObserver buttonVizUpdater = new SimpleObserver() {
            public void update() {
                moreAtomsButton.setVisible( model.getAtomSizeProperty().getValue() == IsotopeSize.LARGE && model.getShowNaturesMix().getValue() == false );
                lessAtomsButton.setVisible( model.getAtomSizeProperty().getValue() == IsotopeSize.SMALL && model.getShowNaturesMix().getValue() == false );
            }
        };
        model.getAtomSizeProperty().addObserver( buttonVizUpdater );
        model.getShowNaturesMix().addObserver( buttonVizUpdater );

        // Add the radio buttons that allow the user to choose between their
        // mix and nature's mix.
        JPanel radioButtonPanel = new VerticalLayoutPanel();
        PropertyRadioButton<Boolean> usersMixRadioButton = new PropertyRadioButton<Boolean>( "My mix of isotopes", model.getShowNaturesMix(), false ){{
            setBackground( BACKGROUND_COLOR );
            setFont( new PhetFont( 20 ) );
        }};
        radioButtonPanel.add( usersMixRadioButton );
        PropertyRadioButton<Boolean> naturesMixRadioButton = new PropertyRadioButton<Boolean>( "Nature's mix of isotopes", model.getShowNaturesMix(), true ){{
            setBackground( BACKGROUND_COLOR );
            setFont( new PhetFont( 20 ) );
        }};
        radioButtonPanel.add( naturesMixRadioButton );
        controlsLayer.addChild( new PSwing( radioButtonPanel ){{
            setOffset(
                testChamberNode.getFullBoundsReference().getMaxX() + 110,
                BuildAnAtomDefaults.STAGE_SIZE.height - getFullBoundsReference().height - 20 );
        }} );
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
            // TODO: i18n
            PText title = new PText("Percent Composition");
            title.setFont( new PhetFont( 20, true ) );
            addChild( title );
            final PieChartNode pieChart = new PieChartNode( new PieValue[] { new PieValue( 100, Color.red ) },
                    new Rectangle(0, 0, SIZE.width, SIZE.height ) );
            pieChart.setOffset( title.getFullBoundsReference().getCenterX(), title.getFullBoundsReference().getMaxY() + 10 );
            addChild( pieChart );
            model.getIsotopeTestChamber().getIsotopeCountProperty().addObserver( new SimpleObserver(){
                public void update() {
                    int isotopeCount = model.getIsotopeTestChamber().getIsotopeCountProperty().getValue();
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
