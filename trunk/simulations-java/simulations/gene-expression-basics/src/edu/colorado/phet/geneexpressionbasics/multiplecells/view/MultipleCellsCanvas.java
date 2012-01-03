// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.Cell;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.MultipleCellsModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Main canvas for the Multiple Cells tab.
 *
 * @author John Blanco
 */
public class MultipleCellsCanvas extends PhetPCanvas implements Resettable {

    // Stage size, based on default screen size.
    private static Dimension2D STAGE_SIZE = new PDimension( 1008, 679 );

    private final ModelViewTransform mvt;
    final MultipleCellsModel model;

    // Local root node for all things in the "world", which in this case is
    // the set of cells.  This exists in order to support zooming.
    private final PNode localWorldRootNode = new PNode();

    public MultipleCellsCanvas( final MultipleCellsModel model ) {
        this.model = model;

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new CenteredStage( this, STAGE_SIZE ) );

        // Set up the model-canvas transform.
        // IMPORTANT NOTES: The multiplier factors for the 2nd point can be
        // adjusted to shift the center right or left, and the scale factor
        // can be adjusted to zoom in or out (smaller numbers zoom out, larger
        // ones zoom in).
        mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.5 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.5 ) ),
                1E8 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        // Set the background color.
        setBackground( new Color( 250, 232, 189 ) );

        // Add the local world root node.
        addWorldChild( localWorldRootNode );

        // Set up an observer of the list of cells in the model so that the
        // view representations can come and go as needed.
        model.cellList.addElementAddedObserver( new VoidFunction1<Cell>() {
            public void apply( final Cell addedCell ) {
                final PNode cellNode = new CellNode( addedCell, mvt );
                localWorldRootNode.addChild( cellNode );
                model.cellList.addElementRemovedObserver( new VoidFunction1<Cell>() {
                    public void apply( Cell removedCell ) {
                        if ( removedCell == addedCell ) {
                            localWorldRootNode.removeChild( cellNode );
                            model.cellList.removeElementRemovedObserver( this );
                        }
                    }
                } );
            }
        } );

        // Add the slider that controls one vs. many cells.
        addWorldChild( new CellNumberSlider( model ) {{
            double inset = 40;
            setOffset( inset, STAGE_SIZE.getHeight() - getFullBoundsReference().height - inset );
        }} );

        // Add the sliders that control the various model parameters.
        addWorldChild( new CellParameterController( "<center>Transcription Factor<br>Level</center>", Color.orange ) );

        // Add a handler that controls the zoom level.  This automatically
        // zooms in and out to allow all of the cells to be seen.
        model.cellList.addElementAddedObserver( new VoidFunction1<Cell>() {
            public void apply( Cell cell ) {
                setZoomToSeeAllCells();
            }
        } );
        model.cellList.addElementRemovedObserver( new VoidFunction1<Cell>() {
            public void apply( Cell cell ) {
                setZoomToSeeAllCells();
            }
        } );

        // Add the chart that displays the average protein level.
        addWorldChild( new ProteinLevelChartNode( model.averageProteinLevel, model.getClock() ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBoundsReference().width - 20,
                       STAGE_SIZE.getHeight() - getFullBoundsReference().height - 20 );
        }} );
    }

    private void setZoomToSeeAllCells() {
        if ( model.getNumCells() > 0 ) {
            // Set the existing transform back to default, unzoomed state.
            localWorldRootNode.setTransform( new AffineTransform() );

            // Set the scale so that the cells fit on the "stage".
            double scale = Math.min( ( STAGE_SIZE.getWidth() * 0.75 ) / mvt.modelToViewDeltaX( model.getCellCollectionBounds().getWidth() ), 1 );
            localWorldRootNode.scaleAboutPoint( scale, mvt.modelToView( new Point2D.Double( 0, 0 ) ) );
        }
    }

    public void reset() {
        // TODO: Implement or remove at some point.
    }

    /**
     * Class the defines the slider that controls the number of cells in the
     * model.
     */
    private static class CellNumberSlider extends PNode {
        private CellNumberSlider( final MultipleCellsModel model ) {

            // Create the title.
            PText title = new PLabel( "Number of Cells", 18, true );

            // Create the slider.
            Property<Double> numCellsProperty = new Property<Double>( 1.0 );
            HSliderNode sliderNode = new HSliderNode( 1, (double) MultipleCellsModel.MAX_CELLS, 200, 5, numCellsProperty, new BooleanProperty( true ) ) {
                @Override protected Paint getTrackFillPaint( Rectangle2D trackRect ) {
                    // Gradient doesn't look good, keep it white.
                    return Color.WHITE;
                }
            };
            sliderNode.addLabel( 1, new PLabel( "One", 14 ) );
            sliderNode.addLabel( (double) MultipleCellsModel.MAX_CELLS, new PLabel( "Many", 14 ) );
            addChild( sliderNode );

            // Put them together in a box and add to the node.
            addChild( new VBox( title, sliderNode ) );

            // Listen to the property and adjust the number of cells in the
            // model accordingly.
            numCellsProperty.addObserver( new VoidFunction1<Double>() {
                public void apply( Double numCells ) {
                    model.setNumCells( (int) Math.round( numCells ) );
                }
            } );
        }
    }

    private static class PLabel extends PText {
        private PLabel( String text, int fontSize ) {
            this( text, fontSize, false );
        }

        private PLabel( String text, int fontSize, boolean bold ) {
            super( text );
            setFont( new PhetFont( fontSize, bold ) );
        }
    }
}
