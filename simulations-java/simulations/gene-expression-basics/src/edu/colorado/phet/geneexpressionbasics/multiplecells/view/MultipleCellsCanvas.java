// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsSimSharing.UserComponents;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.Cell;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.MultipleCellsModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
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

    // For debug - shows bounding box for cells.
    private static final boolean SHOW_CELL_BOUNDING_BOX = false;
    private final PPath cellBoundingBox = new PhetPPath( new BasicStroke( 5f ), Color.red );

    private final ModelViewTransform mvt;
    final MultipleCellsModel model;

    // Property that controls whether the clock is running, used in the
    // floating clock control.
    BooleanProperty clockRunning = new BooleanProperty( false );

    // Local root node for all things in the "world", which in this case is
    // the set of cells.  This exists in order to support zooming.
    private final PNode localWorldRootNode = new PNode();

    // Chart that depicts the average protein level.
    protected ProteinLevelChartNode proteinLevelChartNode;

    /**
     * Constructor.
     *
     * @param model
     * @param parentFrame
     */
    public MultipleCellsCanvas( final MultipleCellsModel model, final Frame parentFrame ) {
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
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.475 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.4 ) ),
                1E8 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        // Set the background color.
        setBackground( Color.BLACK );

        // Add the local world root node.
        addWorldChild( localWorldRootNode );

        // Set up an observer of the list of cells in the model so that the
        // view representations can come and go as needed.
        model.visibleCellList.addElementAddedObserver( new VoidFunction1<Cell>() {
            public void apply( final Cell addedCell ) {
                final PNode cellNode = new ColorChangingCellNode( addedCell, mvt );
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
        proteinLevelChartNode = new ProteinLevelChartNode( model.averageProteinLevel, model.getClock() );
        addWorldChild( proteinLevelChartNode );

        // Create and add the slider that controls one vs. many cells.
        final CellNumberController cellNumberController = new CellNumberController( model );
        addWorldChild( cellNumberController );

        // Create and add the control panels that controls the cell parameters.
        final CollapsibleControlPanel concentrationControlPanel = new ConcentrationsControlPanel( model );
        addWorldChild( concentrationControlPanel );
        final CollapsibleControlPanel affinityControlPanel = new AffinityControlPanel( model );
        addWorldChild( affinityControlPanel );
        final CollapsibleControlPanel degradationParameterController = new DegradationControlPanel( model );
        addWorldChild( degradationParameterController );

        // Create the floating clock control.
        final ConstantDtClock modelClock = (ConstantDtClock) model.getClock();
        clockRunning.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean isRunning ) {
                modelClock.setRunning( isRunning );
            }
        } );
        final FloatingClockControlNode floatingClockControl = new FloatingClockControlNode( clockRunning, null,
                                                                                            model.getClock(), null,
                                                                                            new Property<Color>( Color.white ) );

        // Make sure that the floating clock control sees the change when the
        // clock gets started.
        model.getClock().addClockListener( new ClockAdapter() {
            @Override public void clockStarted( ClockEvent clockEvent ) {
                clockRunning.set( true );
            }
        } );

        // Create the Reset All button.
        final ResetAllButtonNode resetAllButton = new ResetAllButtonNode( new Resettable[] { model, this }, this, 18, Color.BLACK, new Color( 255, 153, 0 ) ) {{
            setConfirmationEnabled( false );
            setOffset( floatingClockControl.getFullBoundsReference().getCenterX() - getFullBoundsReference().getWidth() / 2,
                       concentrationControlPanel.getFullBoundsReference().getMaxY() + 30 );
        }};

        // Create button for showing a picture of real fluorescent cells.
        PNode showRealCells = new HTMLImageButtonNode( "Show Real Cells", new PhetFont( 18 ), Color.YELLOW ) {{
            centerFullBoundsOnPoint( concentrationControlPanel.getFullBoundsReference().getCenterX(), resetAllButton.getFullBoundsReference().getCenterY() );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    FluorescentCellsPictureDialog dialog = new FluorescentCellsPictureDialog( parentFrame );
                    if ( dialog != null ) {
                        SwingUtils.centerInParent( dialog );
                    }
                    dialog.setVisible( true );
                }
            } );
        }};

        // Create a panel containing the clock control, reset button, and the
        // button for showing real cells.
        PNode globalControlsPanel = new VBox( 20, floatingClockControl, resetAllButton, showRealCells );
        addWorldChild( globalControlsPanel );

        if ( SHOW_CELL_BOUNDING_BOX ) {
            localWorldRootNode.addChild( cellBoundingBox );
        }

        // Add a handler that automatically controls the zoom level of the
        // collection of cells.  This zooms in and out as cells are added or
        // removed to allow all of the cells to be visible.
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

        // Make the parameter controllers all the same size.
        double parameterControllerWidth = Math.max( Math.max( concentrationControlPanel.getFullBoundsReference().width,
                                                              affinityControlPanel.getFullBoundsReference().width ),
                                                    degradationParameterController.getFullBoundsReference().width );
        concentrationControlPanel.setMinWidth( parameterControllerWidth );
        affinityControlPanel.setMinWidth( parameterControllerWidth );
        degradationParameterController.setMinWidth( parameterControllerWidth );

        // Do the lay out.
        proteinLevelChartNode.setOffset( mvt.modelToViewX( 0 ) - proteinLevelChartNode.getFullBoundsReference().width / 2,
                                         STAGE_SIZE.getHeight() - proteinLevelChartNode.getFullBoundsReference().height - 10 );
        globalControlsPanel.setOffset( proteinLevelChartNode.getFullBoundsReference().getMinX() / 2 - globalControlsPanel.getFullBoundsReference().width / 2,
                                       proteinLevelChartNode.getFullBoundsReference().getCenterY() - globalControlsPanel.getFullBoundsReference().height / 2 );
        concentrationControlPanel.setOffset( STAGE_SIZE.getWidth() - concentrationControlPanel.getFullBoundsReference().width - 10, 20 );
        affinityControlPanel.setOffset( concentrationControlPanel.getFullBoundsReference().getMinX(),
                                        concentrationControlPanel.getFullBoundsWhenOpen().getMaxY() + 10 );
        degradationParameterController.setOffset( affinityControlPanel.getFullBoundsReference().getMinX(),
                                                  affinityControlPanel.getFullBoundsWhenOpen().getMaxY() + 10 );
        cellNumberController.setOffset( mvt.modelToViewX( 0 ) - cellNumberController.getFullBoundsReference().width / 2, 10 );
    }

    private void setZoomToSeeAllCells() {
        // Set the existing transform back to default, unzoomed state.
        localWorldRootNode.setTransform( new AffineTransform() );

        // Set the scale so that the visible cells fit on the "stage".
        Rectangle2D visibleCellCollectionBounds = model.getVisibleCellCollectionBounds();
        if ( visibleCellCollectionBounds.getWidth() > 0 && visibleCellCollectionBounds.getHeight() > 0 ) {
            double xScale = Math.min( ( STAGE_SIZE.getWidth() * 0.75 ) / mvt.modelToViewDeltaX( visibleCellCollectionBounds.getWidth() ), 1 );
            double yScale = Math.min( ( STAGE_SIZE.getHeight() * 0.4 ) / Math.abs( mvt.modelToViewDeltaY( visibleCellCollectionBounds.getHeight() ) ), 1 );
            localWorldRootNode.scaleAboutPoint( Math.min( xScale, yScale ), mvt.modelToView( new Point2D.Double( 0, 0 ) ) );
        }

        // Set the bounding box size for the cells.
        cellBoundingBox.setPathTo( mvt.modelToView( model.getVisibleCellCollectionBounds() ) );
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

            // Create the slider.
            IntegerHSliderNode sliderNode = new IntegerHSliderNode( UserComponents.numberOfCellsSlider, 1, MultipleCellsModel.MAX_CELLS, 4, 100, model.numberOfVisibleCells );
            // TODO: i18n
            sliderNode.addLabel( 1, new PLabel( "One", 14 ) );
            // TODO: i18n
            sliderNode.addLabel( (double) MultipleCellsModel.MAX_CELLS, new PLabel( "Many", 14 ) );

            // Put the title and slider together in a box and add to the node
            // and enclose in a control panel.
            // TODO: i18n
            ControlPanelNode controlPanel = new ControlPanelNode( new VBox( new PhetPText( "Cells", new PhetFont( 16, true ) ), sliderNode ),
                                                                  new Color( 245, 205, 245 ) );

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
