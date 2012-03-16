// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.IdentityHashMap;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.NucleusType;
import edu.colorado.phet.nuclearphysics.common.TimeDisplayNode;
import edu.colorado.phet.nuclearphysics.module.radioactivedatinggame.RadiometricDatingMeter.MeasurementMode;
import edu.colorado.phet.nuclearphysics.module.radioactivedatinggame.RadiometricMeasurementModel.SIMULATION_MODE;
import edu.colorado.phet.nuclearphysics.view.NuclearDecayProportionChart;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * This class represents the canvas upon which the view of the model is
 * displayed for the Single Nucleus Alpha Decay tab of this simulation.
 *
 * @author John Blanco
 */
public class RadiometricMeasurementCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    // Initial size of the reference coordinates that are used when setting up
    // the canvas transform strategy.  These were empirically determined to
    // match the expected initial size of the canvas.
    private static final int INITIAL_INTERMEDIATE_COORD_WIDTH = 786;
    private static final int INITIAL_INTERMEDIATE_COORD_HEIGHT = 786;
    private static final Dimension INITIAL_INTERMEDIATE_DIMENSION = new Dimension( INITIAL_INTERMEDIATE_COORD_WIDTH,
                                                                                   INITIAL_INTERMEDIATE_COORD_HEIGHT );

    // Fraction of canvas width used for the proportions chart.
    private static final double PROPORTIONS_CHART_WIDTH_FRACTION = 0.80;

    // Proportions and location of the meter on the canvas.
    private static final double METER_WIDTH_FRACTION = 0.23;
    private static final double CHART_AND_METER_HEIGHT_FRACTION = 0.30;
    private static final double METER_X_OFFSET = 0;

    // Fixed distance from very top of canvas where the meter and chart will
    // be positioned.
    private static final double METER_AND_CHART_OFFSET_FROM_TOP = 8;

    // Constants that control the appearance of the buttons in the play area.
    private static final Color START_OPERATION_BUTTON_COLOR = new Color( 255, 204, 102 );
    private static final Color FORCE_CLOSURE_BUTTON_COLOR = new Color( 250, 70, 0 );
    private static final PhetFont PLAY_AREA_BUTTON_FONT = new PhetFont( Font.BOLD, 24 );
    private static final double BUTTON_DISTANCE_FROM_BOTTOM = 20;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private ModelViewTransform2D _mvt;
    private RadiometricMeasurementModel _model;
    private PNode _datableItemsLayer;
    private PNode _chartAndMeterLayer;
    private NuclearDecayProportionChart _proportionsChart;
    private RadiometricDatingMeterNode _meterNode;
    private IdentityHashMap<DatableItem, PNode> _mapModelElementsToNodes = new IdentityHashMap<DatableItem, PNode>();
    private HTMLImageButtonNode _startOperationButtonNode;
    private HTMLImageButtonNode _forceClosureButtonNode;
    private HTMLImageButtonNode _resetButtonNode;
    private PPath _probeDragBounds = new PPath();
    private TimeDisplayNode _timeDisplay;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    RadiometricMeasurementCanvas( RadiometricMeasurementModel radiometricMeasurementModel ) {

        _model = radiometricMeasurementModel;

        setWorldTransformStrategy( new PhetPCanvas.CenterWidthScaleHeight( this, INITIAL_INTERMEDIATE_DIMENSION ) );
        _mvt = new ModelViewTransform2D(
                new Point2D.Double( 0, 0 ),
                new Point( INITIAL_INTERMEDIATE_COORD_WIDTH / 2,
                           (int) Math.round( INITIAL_INTERMEDIATE_COORD_HEIGHT * 0.75 ) ),
                20,
                true );

        // Register with the meter in the model in order to know when the user
        // changes any of the meter settings, since they need to be propagated
        // to the chart.
        _model.getMeter().addListener( new RadiometricDatingMeter.Adapter() {
            @Override
            public void datingElementChanged() {
                configureProportionsChart();
                update();
            }

            ;

            @Override
            public void touchedStateChanged() {
                handleMeterTouchStateChanged();
            }

            @Override
            public void measurementModeChanged() {
                // Most of the time the chart just shows unconnected data
                // points so that gaps appear when no measurements were being
                // made.  However, this looked weird in some cases, so we need
                // the following special case.
                if ( _model.getMeter().getNucleusTypeUsedForDating() == NucleusType.CARBON_14 &&
                     _model.getMeter().getMeasurementMode() == MeasurementMode.AIR &&
                     _model.getSimulationMode() == SIMULATION_MODE.ROCK ) {

                    _proportionsChart.setLineModeEnabled( true );
                }
                else {
                    _proportionsChart.setLineModeEnabled( false );
                }
            }

            ;
        } );

        // Register with the model for notifications of new elements coming
        // and going.
        _model.addListener( new RadiometricMeasurementModel.Adapter() {
            public void modelElementAdded( Object modelElement ) {
                handleModelElementAdded( modelElement );
            }

            public void modelElementRemoved( Object modelElement ) {
                handleModelElementRemoved( modelElement );
            }

            public void simulationModeChanged() {
                handleSimulationModeChanged();
            }

            public void closureStateChanged() {
                updateButtonState();
            }
        } );

        // Register with the model's clock for changes in status that impact
        // what is presented to the user.
        _model.getClock().addClockListener( new ClockAdapter() {
            public void clockStarted( ClockEvent clockEvent ) {
                updateButtonState();
            }

            public void simulationTimeReset( ClockEvent clockEvent ) {
                // Clear the chart when time starts over.
                _proportionsChart.clear();
                // Reset the time display to 0.
                _timeDisplay.setTime( 0 );
            }

            public void clockTicked( ClockEvent clockEvent ) {
                addDataToChart( clockEvent );
                updateTimeDisplay( clockEvent );
            }
        } );

        // Set the background color.
        setBackground( NuclearPhysicsConstants.CANVAS_BACKGROUND );

        // Create the layer where the background will be placed.
        PNode backgroundLayer = new PNode();
        addWorldChild( backgroundLayer );

        // Create the layer where the datable items will be placed.
        _datableItemsLayer = new PNode();
        addWorldChild( _datableItemsLayer );

        _chartAndMeterLayer = new PNode();
        addWorldChild( _chartAndMeterLayer );

        // Add the ground to the background.
        GroundNode ground = new GroundNode();
        ground.setOffset( _mvt.modelToViewXDouble( 0 ), _mvt.modelToViewYDouble( _model.getGroundLevelY() ) );
        backgroundLayer.addChild( ground );

        // Add the sky to the background.
        SkyNode sky = new SkyNode( INITIAL_INTERMEDIATE_COORD_WIDTH * 4, INITIAL_INTERMEDIATE_COORD_HEIGHT * 1.5 );
        sky.setOffset( _mvt.modelToViewXDouble( 0 ), _mvt.modelToViewYDouble( _model.getGroundLevelY() ) );
        backgroundLayer.addChild( sky );

        // Add a couple of clouds to the background.
        PImage cloud1 = NuclearPhysicsResources.getImageNode( "cloud_1.png" );
        cloud1.setScale( 0.75 );
        cloud1.setOffset( -110, 320 );
        backgroundLayer.addChild( cloud1 );
        PImage cloud2 = NuclearPhysicsResources.getImageNode( "cloud_1.png" );
        cloud2.setScale( 0.5 );
        cloud2.setOffset( 760, 380 );
        backgroundLayer.addChild( cloud2 );

        // Create the chart that will display relative decay proportions.
        _proportionsChart = new NuclearDecayProportionChart( false, false, false, false );
        configureProportionsChart();
        _chartAndMeterLayer.addChild( _proportionsChart );

        // Set the size and position of the chart.  There are some "tweak
        // factors" in here to make things look good.
        // TODO: If I end up leaving this as a world child, I need to change this
        // resizing call to be a part of the constructor for the chart (I think).
        _proportionsChart.componentResized( new Rectangle2D.Double( 0, 0,
                                                                    INITIAL_INTERMEDIATE_COORD_WIDTH * PROPORTIONS_CHART_WIDTH_FRACTION,
                                                                    INITIAL_INTERMEDIATE_COORD_HEIGHT * CHART_AND_METER_HEIGHT_FRACTION ) );
        _proportionsChart.setOffset(
                METER_X_OFFSET + INITIAL_INTERMEDIATE_COORD_WIDTH * METER_WIDTH_FRACTION + 8,
                METER_AND_CHART_OFFSET_FROM_TOP );

        // Create the radiometric measuring device.
        _meterNode = new RadiometricDatingMeterNode(
                _model.getMeter(),
                INITIAL_INTERMEDIATE_COORD_WIDTH * METER_WIDTH_FRACTION,
                INITIAL_INTERMEDIATE_COORD_HEIGHT * CHART_AND_METER_HEIGHT_FRACTION,
                _mvt,
                this,
                false,
                _probeDragBounds );
        _meterNode.setMeterBodyOffset( METER_X_OFFSET, METER_AND_CHART_OFFSET_FROM_TOP );
        _chartAndMeterLayer.addChild( _meterNode );

        // Add the time display.
        _timeDisplay = new TimeDisplayNode( 220, 50 );
        _timeDisplay.setOffset(
                _proportionsChart.getFullBoundsReference().getMaxX() - _timeDisplay.getFullBoundsReference().width,
                _proportionsChart.getFullBoundsReference().getMaxY() + 5 );
        _chartAndMeterLayer.addChild( _timeDisplay );
        _timeDisplay.setTime( 0 );


        // Set up the initial buttons in the play area.
        updateButtonState();

        // Add the node(s) to the canvas corresponding to the datable items in
        // the model.
        ArrayList modelElements = _model.getModelElements();
        for ( Object modelElement : modelElements ) {
            handleModelElementAdded( modelElement );
        }

        // Set initial location for the probe.
        initializeProbeLocation();

        // Add the node that define the bounds where the probe may be dragged.
        addWorldChild( _probeDragBounds );
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    @Override
    protected void updateLayout() {
        super.updateLayout();

        // Set the bounding node to match exactly the size of the viewport.
        AffineTransform transform = getWorldTransformStrategy().getTransform();
        AffineTransform inverseTransform;
        try {
            inverseTransform = transform.createInverse();
        }
        catch ( NoninvertibleTransformException e ) {
            System.err.println( getClass().getName() + " - Error: Unable to invert transform." );
            e.printStackTrace();
            inverseTransform = new AffineTransform(); // Unity transform by default.
        }
        Shape tranformedBounds = inverseTransform.createTransformedShape( getBounds() );

        _probeDragBounds.setPathTo( tranformedBounds );
    }

    /**
     * Add the current meter reading to the chart (if there is one) for the
     * current time value.
     *
     * @param clockEvent
     */
    private void addDataToChart( ClockEvent clockEvent ) {
        if ( _model.getMeter().getItemBeingTouched() != null ) {
            // The meter is currently touching something, so we should graph
            // this reading.
            double readingTime = _model.getAdjustedTime();
            _proportionsChart.addDataPoint( readingTime, _model.getMeter().getPercentageOfDatingElementRemaining() );
        }
    }

    private void updateTimeDisplay( ClockEvent clockEvent ) {
        _timeDisplay.setTime( _model.getAdjustedTime() );
    }

    /**
     * Handle a notification that a model element was added by comparing the
     * list of elements in the model to the list of corresponding nodes and
     * creating new nodes for any elements that are not yet represented.
     */
    private void handleModelElementAdded( Object modelElement ) {

        if ( !_mapModelElementsToNodes.containsKey( modelElement ) ) {
            if ( modelElement instanceof AnimatedDatableItem ) {
                // Add a new node for this model element.
                DatableItem item = (AnimatedDatableItem) modelElement;
                DatableItemNode itemNode = new DatableItemNode( item, _mvt );
                _mapModelElementsToNodes.put( item, itemNode );
                _datableItemsLayer.addChild( itemNode );
            }
            else {
                // TODO: Not sure if there is a need for handling of items
                // other than non-datable items.  The volcano may, for
                // instance, spew out non-datable stuff.  If so, this will
                // need to be handled at some point.
                assert false;
            }
        }
        else {
            System.err.println( getClass().getName() + " - Error: Redundant attempt to add model element " + modelElement.toString() );
            assert ( false );
        }
    }

    /**
     * Handle and event that signifies that a model element has been removed
     * from the model by removing the corresponding node from the canvas.
     */
    private void handleModelElementRemoved( Object modelElement ) {

        if ( _mapModelElementsToNodes.containsKey( modelElement ) ) {
            PNode node = _mapModelElementsToNodes.get( modelElement );
            _datableItemsLayer.removeChild( node );
            _mapModelElementsToNodes.remove( modelElement );
        }
    }

    /**
     * Handle a notification that the simulation mode - which means whether
     * a tree, a rock, or whatever is being simulated - has changed.
     */
    private void handleSimulationModeChanged() {
        updateButtonState();
        initializeProbeLocation();
    }

    /**
     * Move the probe to the appropriate initial location.  These are
     * empirically determined to be on the tree or rock when they are
     * first created, and will need to be adjusted if the model were
     * changed to locate the trees and rocks differently.
     */
    private void initializeProbeLocation() {
        if ( _model.getSimulationMode() == SIMULATION_MODE.TREE ) {
            _model.getMeter().getProbeModel().setTipLocation( new Point2D.Double( 0, -4 ) );
        }
        else {
            _model.getMeter().getProbeModel().setTipLocation( new Point2D.Double( -6, -2 ) );
        }
    }

    /**
     * Update the state of the play area buttons based on the current state of
     * the model.
     */
    private void updateButtonState() {

        // Remove any pre-existing buttons.
        if ( _startOperationButtonNode != null ) {
            _chartAndMeterLayer.removeChild( _startOperationButtonNode );
            _startOperationButtonNode = null;
        }
        if ( _forceClosureButtonNode != null ) {
            _chartAndMeterLayer.removeChild( _forceClosureButtonNode );
            _forceClosureButtonNode = null;
        }
        if ( _resetButtonNode != null ) {
            _chartAndMeterLayer.removeChild( _resetButtonNode );
            _resetButtonNode = null;
        }

        // If the clock is not running, set up the buttons to initiate the
        // simulation.
        if ( !_model.getClock().isRunning() ) {
            switch( _model.getSimulationMode() ) {

                case TREE:
                    _startOperationButtonNode = new HTMLImageButtonNode( NuclearPhysicsStrings.PLANT_TREE,
                                                                PLAY_AREA_BUTTON_FONT, START_OPERATION_BUTTON_COLOR );
                    break;
                case ROCK:
                    _startOperationButtonNode = new HTMLImageButtonNode( NuclearPhysicsStrings.ERUPT_VOLCANO,
                                                                PLAY_AREA_BUTTON_FONT, START_OPERATION_BUTTON_COLOR );
            }

            _startOperationButtonNode.setOffset(
                    INITIAL_INTERMEDIATE_COORD_WIDTH - _startOperationButtonNode.getFullBoundsReference().width,
                    INITIAL_INTERMEDIATE_COORD_HEIGHT - BUTTON_DISTANCE_FROM_BOTTOM - _startOperationButtonNode.getFullBoundsReference().height );
            _chartAndMeterLayer.addChild( _startOperationButtonNode );

            // Hook up the button to the method in the model that will start
            // the simulation.
            _startOperationButtonNode.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    _model.startOperation();
                }
            } );
        }
        else {
            // Clock is running, so if closure has not occurred we need to add
            // the button to force it.
            if ( _model.getRadiometricClosureState() == RadiometricClosureState.CLOSURE_POSSIBLE ) {
                switch( _model.getSimulationMode() ) {

                    case TREE:
                        _forceClosureButtonNode = new HTMLImageButtonNode( NuclearPhysicsStrings.KILL_TREE,
                                                                  PLAY_AREA_BUTTON_FONT, FORCE_CLOSURE_BUTTON_COLOR );
                        break;
                    case ROCK:
                        _forceClosureButtonNode = new HTMLImageButtonNode( NuclearPhysicsStrings.COOL_ROCK,
                                                                  PLAY_AREA_BUTTON_FONT, FORCE_CLOSURE_BUTTON_COLOR );
                        break;
                }

                _forceClosureButtonNode.setOffset(
                        INITIAL_INTERMEDIATE_COORD_WIDTH - _forceClosureButtonNode.getFullBoundsReference().width,
                        INITIAL_INTERMEDIATE_COORD_HEIGHT - BUTTON_DISTANCE_FROM_BOTTOM - _forceClosureButtonNode.getFullBoundsReference().height );
                _chartAndMeterLayer.addChild( _forceClosureButtonNode );

                // Hook up the button to the method in the model that will start
                // the simulation.
                _forceClosureButtonNode.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        _model.forceClosure();
                    }
                } );
            }
            else if ( _model.getRadiometricClosureState() == RadiometricClosureState.CLOSED ) {
                // The clock is running and closure has occurred.  Put up the
                // reset button.
                _resetButtonNode = new HTMLImageButtonNode( NuclearPhysicsStrings.RESET_BUTTON_LABEL,
                                                   PLAY_AREA_BUTTON_FONT, NuclearPhysicsConstants.CANVAS_RESET_BUTTON_COLOR );
                _resetButtonNode.setOffset(
                        INITIAL_INTERMEDIATE_COORD_WIDTH - _resetButtonNode.getFullBoundsReference().width,
                        INITIAL_INTERMEDIATE_COORD_HEIGHT - BUTTON_DISTANCE_FROM_BOTTOM - _resetButtonNode.getFullBoundsReference().height );
                _chartAndMeterLayer.addChild( _resetButtonNode );

                // Hook up the button to the method in the model that will reset
                // the simulation.
                _resetButtonNode.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        _model.resetOperation();
                    }
                } );
            }
        }
    }

    /**
     * Handle a notification from the meter that indicates that it has started
     * or stopped touching a datable item.
     */
    private void handleMeterTouchStateChanged() {

        DatableItem itemBeingTouched = _model.getMeter().getItemBeingTouched();

        if ( itemBeingTouched != null ) {
            // TODO
        }
        else {
        }
    }

    private void configureProportionsChart() {

        double halfLife = _model.getMeter().getHalfLifeForDating();
        _proportionsChart.setTimeParameters( halfLife * 3.2, halfLife );
        _proportionsChart.setDisplayInfoForNucleusType( _model.getMeter().getNucleusTypeUsedForDating() );
        if ( _model.getMeter().getNucleusTypeUsedForDating() == NucleusType.CARBON_14 ) {
            _proportionsChart.setShowCarbonOptions( true );
        }
        else {
            _proportionsChart.setShowCarbonOptions( false );
        }
    }

    /**
     * Class used to represent the ground on this canvas.
     */
    private static class GroundNode extends PNode {

        public static final double GROUND_WIDTH = INITIAL_INTERMEDIATE_COORD_WIDTH * 4;
        public static final double GROUND_HEIGHT = INITIAL_INTERMEDIATE_COORD_HEIGHT;

        public GroundNode() {
            PPath ground = new PPath( new Rectangle2D.Double( 0, 0, GROUND_WIDTH, GROUND_HEIGHT ) );
            GradientPaint groundGradient = new GradientPaint( 0, 0, new Color( 0, 100, 0 ), 0, (float) GROUND_HEIGHT / 4,
                                                              new Color( 120, 255, 60 ), false );
            ground.setPaint( groundGradient );
            ground.setOffset( -GROUND_WIDTH / 2, 0 );
            addChild( ground );
        }
    }
}