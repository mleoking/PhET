// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.isotopemixture.view;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.model.Bucket;
import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.IsotopeMixturesModel;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.MobileAtom;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.IsotopeMixturesModel.Listener;
import edu.colorado.phet.buildanatom.view.BucketFrontNode;
import edu.colorado.phet.buildanatom.view.BucketHoleNode;
import edu.colorado.phet.buildanatom.view.PeriodicTableControlNode;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode.PieValue;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the tab where the user builds an atom.
 */
public class IsotopeMixturesCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

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

        setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );

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

        // Listen to the model for events that concern the canvas.
        model.addListener( new Listener() {
            public void isotopeInstanceAdded( final MobileAtom atom ) {
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
            setPathTo( new Rectangle2D.Double( 0, 0,
                mvt.modelToViewDifferentialXDouble( model.getIsotopeTestChamberSize().getWidth() ),
                mvt.modelToViewDifferentialYDouble( -model.getIsotopeTestChamberSize().getHeight() ) ) );
            setOffset( mvt.modelToViewXDouble( model.getIsotopeTestChamberPosition().getX() ),
                    mvt.modelToViewYDouble( -model.getIsotopeTestChamberPosition().getY() ) );
        }};
        chamberLayer.addChild( testChamberNode );

        // Add the periodic table node that will allow the user to set the
        // current isotope.
        PNode periodicTableNode = new PeriodicTableControlNode( model, 18, BuildAnAtomConstants.CANVAS_BACKGROUND ){{
            setOffset( testChamberNode.getFullBoundsReference().getMaxX() + 15, testChamberNode.getFullBoundsReference().getMinY() );
            setScale( 1.1 ); // Empirically determined.
        }};
        chamberLayer.addChild( periodicTableNode );

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

        // TODO: For debug, put a marker at 0,0 in model space.
        addWorldChild( new PhetPPath( mvt.createTransformedShape( new Ellipse2D.Double(-5, -5, 10, 10) ), Color.PINK ) );

        // Add the pie chart to the canvas.
        final PieChartNode pieChart = new PieChartNode( new PieValue[] { new PieValue( 100, Color.red ) }, new Rectangle(0, 0, 100, 100)){{
            setOffset( 720, 230 );
        }};
        chamberLayer.addChild( pieChart );
        model.getPossibleIsotopesProperty().addObserver( new SimpleObserver() {
            public void update() {
                PieValue[] pieSlices = new PieValue[model.getPossibleIsotopesProperty().getValue().size()];
                int isotopeCount = 0;
                for ( ImmutableAtom atom : model.getPossibleIsotopesProperty().getValue() ){
                    pieSlices[isotopeCount++] = new PieValue( 100, model.getColorForIsotope( atom ) );
                }
                pieChart.setPieValues( pieSlices );
            }
        });
    }
}
