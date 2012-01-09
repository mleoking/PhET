// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
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

    // Chart that depicts the average protein level.
    protected ProteinLevelChartNode proteinLevelChartNode;

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
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.4 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.32 ) ),
                1E8 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        // Set the background color.
        setBackground( new Color( 250, 232, 189 ) );

        // Add the local world root node.
        addWorldChild( localWorldRootNode );

        // Set up an observer of the list of cells in the model so that the
        // view representations can come and go as needed.
        model.visibleCellList.addElementAddedObserver( new VoidFunction1<Cell>() {
            public void apply( final Cell addedCell ) {
                final PNode cellNode = new CellNode( addedCell, mvt );
                localWorldRootNode.addChild( cellNode );
                model.visibleCellList.addElementRemovedObserver( new VoidFunction1<Cell>() {
                    public void apply( Cell removedCell ) {
                        if ( removedCell == addedCell ) {
                            localWorldRootNode.removeChild( cellNode );
                            model.visibleCellList.removeElementRemovedObserver( this );
                        }
                    }
                } );
            }
        } );

        // Add the chart that displays the average protein level.
        proteinLevelChartNode = new ProteinLevelChartNode( model.averageProteinLevel, model.getClock() ) {{
            setOffset( mvt.modelToViewX( 0 ) - getFullBoundsReference().width / 2,
                       STAGE_SIZE.getHeight() - getFullBoundsReference().height - 10 );
        }};
        addWorldChild( proteinLevelChartNode );

        // Create and add the slider that controls one vs. many cells.
        final CellNumberController cellNumberController = new CellNumberController( model );
        addWorldChild( cellNumberController );

        // Create and add the control panel that controls the cell parameters.
        final MultiCellParameterController cellParameterController = new MultiCellParameterController( model );
        addWorldChild( cellParameterController );

        // Lay out the controllers.
        double maxControllerWidth = Math.max( cellNumberController.getFullBoundsReference().width, cellParameterController.getFullBoundsReference().width );
        cellNumberController.setOffset( STAGE_SIZE.getWidth() - maxControllerWidth / 2 - cellNumberController.getFullBoundsReference().getWidth() / 2 - 20, 20 );
        cellParameterController.setOffset( cellNumberController.getFullBoundsReference().getCenterX() - cellParameterController.getFullBoundsReference().getWidth() / 2,
                                           cellNumberController.getFullBoundsReference().getMaxY() + 20 );

        // Add the Reset All button.
        addWorldChild( new ResetAllButtonNode( new Resettable[] { model, this }, this, 18, Color.BLACK, new Color( 255, 153, 0 ) ) {{
            setConfirmationEnabled( false );
            setOffset( cellParameterController.getFullBoundsReference().getCenterX() - getFullBoundsReference().getWidth() / 2,
                       cellParameterController.getFullBoundsReference().getMaxY() + 20 );
        }} );
        /*
        // Add the sliders that control the various model parameters.
        // TODO: i18n
        addWorldChild( new CellParameterController( "<center>Transcription Factor<br>Level</center>",
                                                    model.transcriptionFactorLevel,
                                                    CellProteinSynthesisSimulator.TRANSCRIPTION_FACTOR_COUNT_RANGE,
                                                    Color.orange ) {{
            setOffset( 10, 10 );
        }} );
        // TODO: i18n
        addWorldChild( new CellParameterController( "<center>Transcription Factor<br>Affinity</center>",
                                                    model.transcriptionFactorAssociationProbability,
                                                    CellProteinSynthesisSimulator.TF_ASSOCIATION_PROBABILITY_RANGE,
                                                    new Color( 224, 255, 255 ) ) {{
            setOffset( 10, 300 );
        }} );
        // TODO: i18n
        addWorldChild( new CellParameterController( "<center>Polymerase<br>Affinity</center>",
                                                    model.polymeraseAssociationProbability,
                                                    CellProteinSynthesisSimulator.POLYMERASE_ASSOCIATION_PROBABILITY_RANGE,
                                                    new Color( 216, 191, 216 ) ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBoundsReference().width - 10, 100 );
        }} );
        */

        // Add a handler that controls the zoom level.  This automatically
        // zooms in and out to allow all of the cells to be seen.
        model.visibleCellList.addElementAddedObserver( new VoidFunction1<Cell>() {
            public void apply( Cell cell ) {
                setZoomToSeeAllCells();
            }
        } );
        model.visibleCellList.addElementRemovedObserver( new VoidFunction1<Cell>() {
            public void apply( Cell cell ) {
                setZoomToSeeAllCells();
            }
        } );
    }

    private void setZoomToSeeAllCells() {
        // Set the existing transform back to default, unzoomed state.
        localWorldRootNode.setTransform( new AffineTransform() );

        // Set the scale so that the visible cells fit on the "stage".
        Rectangle2D visibleCellCollectionBounds = model.getVisibleCellCollectionBounds();
        if ( visibleCellCollectionBounds.getWidth() > 0 && visibleCellCollectionBounds.getHeight() > 0 ) {
            double scale = Math.min( ( STAGE_SIZE.getWidth() * 0.75 ) / mvt.modelToViewDeltaX( visibleCellCollectionBounds.getWidth() ), 1 );
            localWorldRootNode.scaleAboutPoint( scale, mvt.modelToView( new Point2D.Double( 0, 0 ) ) );
        }
    }

    public void reset() {
        // Reset the chart.
        proteinLevelChartNode.clear();
    }

    /**
     * Class the defines the slider that controls the number of cells in the
     * model.
     */
    private static class CellNumberController extends PNode {
        private CellNumberController( final MultipleCellsModel model ) {

            // Create the title.
            PNode title = new HTMLNode( "<center>Number of<br>Cells</center>", Color.black, new PhetFont( 16, true ) );

            // Create the slider.
            IntegerHSliderNode sliderNode = new IntegerHSliderNode( 1, MultipleCellsModel.MAX_CELLS, 100, 4, model.numberOfVisibleCells );
            sliderNode.addLabel( 1, new PLabel( "One", 14 ) );
            sliderNode.addLabel( (double) MultipleCellsModel.MAX_CELLS, new PLabel( "Many", 14 ) );
            addChild( sliderNode );

            // Put the title and slider together in a box and add to the node
            // and enclose in a control panel.
            ControlPanelNode controlPanel = new ControlPanelNode( new VBox( title, sliderNode ), new Color( 245, 205, 245 ) );

            // Add the control panel as a child.
            addChild( controlPanel );
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
