// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.nuclearphysics.module.decayrates;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.common.model.NuclearDecayControl;
import edu.colorado.phet.nuclearphysics.common.view.AbstractAtomicNucleusNode;
import edu.colorado.phet.nuclearphysics.common.view.UnlabeledSphericalAtomicNucleusNode;
import edu.colorado.phet.nuclearphysics.model.HalfLifeInfo;
import edu.colorado.phet.nuclearphysics.model.NuclearDecayListenerAdapter;
import edu.colorado.phet.nuclearphysics.view.AutoPressButtonNode;
import edu.colorado.phet.nuclearphysics.view.BucketOfNucleiNode;
import edu.colorado.phet.nuclearphysics.view.NuclearDecayProportionChart;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the canvas that presents a view of many nuclei
 * decaying.
 *
 * @author John Blanco
 */
public class DecayRatesCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Canvas size in femto meters.  Assumes a 4:3 aspect ratio.
    private final double CANVAS_WIDTH = 900;
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * ( 3.0d / 4.0d );

    // Translation factors, used to set origin of canvas area.
    private final double WIDTH_TRANSLATION_FACTOR = 0.5;   // 0 = all the way left, 1 = all the way right.
    private final double HEIGHT_TRANSLATION_FACTOR = 0.29; // 0 = all the way up, 1 = all the way down.

    // Constants that control where the charts are placed.
    private final double PROPORTION_CHART_FRACTION = 0.45;   // Fraction of canvas for proportion chart.

    // Constants that control the appearance of the canvas.
    private static final Color BUCKET_AND_BUTTON_COLOR = new Color( 90, 180, 225 );

    // This constant is used to reduce the frequency with which the
    // proportions chart is updated, which reduces the amount of processor
    // cycles that are consumed.  A value of 1 means that the chart is updated
    // on every clock tick, higher values make it less frequent.
    private static final int PROPORTIONS_CHART_UPDATE_COUNT = 1;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private DecayRatesModel _model;
    private HashMap<AtomicNucleus, AbstractAtomicNucleusNode> _mapNucleiToNodes = new HashMap<AtomicNucleus, AbstractAtomicNucleusNode>();
    private NuclearDecayProportionChart _proportionsChart;
    private int _proportionsChartUpdateCounter = 0;
    private PNode _particleLayer;
    private PNode _chartLayer;
    private BucketOfNucleiNode _bucketNode;
    private AutoPressButtonNode _resetButtonNode;
    private PPath _holdingAreaRect;

    //----------------------------------------------------------------------------
    // Builder + Constructor
    //----------------------------------------------------------------------------

    public DecayRatesCanvas( DecayRatesModel decayRatesModel ) {

        _model = decayRatesModel;

        _model.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                if ( ( _proportionsChartUpdateCounter++ % PROPORTIONS_CHART_UPDATE_COUNT == 0 ) &&
                     ( _model.getNumActiveNuclei() > 0 ) ) {
                    addNewDataPointToChart();
                }
            }
        } );

        // Set the transform strategy in such a way that the center of the
        // visible canvas will be at 0,0.
        setWorldTransformStrategy( new RenderingSizeStrategy( this,
                                                              new PDimension( CANVAS_WIDTH, CANVAS_HEIGHT ) ) {
            protected AffineTransform getPreprocessedTransform() {
                return AffineTransform.getTranslateInstance( getWidth() * WIDTH_TRANSLATION_FACTOR,
                                                             getHeight() * HEIGHT_TRANSLATION_FACTOR );
            }
        } );

        // Set the background color.
        setBackground( NuclearPhysicsConstants.CANVAS_BACKGROUND );

        // Add a rect that represents the holding area.
        // TODO: This is here for debugging placement issues and should be removed eventually.
        _holdingAreaRect = new PhetPPath( new Rectangle2D.Double( 0, 0, _model.getHoldingAreaRect().getWidth(),
                                                                  _model.getHoldingAreaRect().getHeight() ), Color.CYAN );
        _holdingAreaRect.setOffset( _model.getHoldingAreaRect().getX(), _model.getHoldingAreaRect().getY() );
//        addWorldChild(_holdingAreaRect);

        // Add the PNodes that will act as layers for the particles and graphs.
        _particleLayer = new PNode();
        addWorldChild( _particleLayer );
        _chartLayer = new PNode();
        addWorldChild( _chartLayer );

        // Create and add the node the represents the bucket from which nuclei
        // can be extracted and added to the play area.
        Rectangle2D bucketRect = _model.getHoldingAreaRect();

        // Create the bucket where the holding area is in the model.  Make the
        // bucket a little smaller than the holding are so that we don't end
        // up with particles really close to it.
        bucketRect.setRect( bucketRect.getX() + 0.1 * bucketRect.getWidth(),
                            bucketRect.getY() + 0.1 * bucketRect.getHeight(),
                            bucketRect.getWidth() * 0.8, bucketRect.getHeight() * 0.9 );
        _bucketNode = new BucketOfNucleiNode( bucketRect.getWidth(), bucketRect.getHeight(), Math.PI / 12,
                                              BUCKET_AND_BUTTON_COLOR );
        _particleLayer.addChild( _bucketNode );
        _bucketNode.setShowLabel( false );
        _bucketNode.setShowRadiationSymbol( false );
        _bucketNode.setSliderEnabled( true );
        _bucketNode.setOffset( bucketRect.getX(), bucketRect.getY() );

        // Register to listen to the slider on the bucket so that nuclei can
        // be added or removed based on the user's input.
        _bucketNode.getSlider().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setProportionOfNucleiOutsideHoldingArea( _bucketNode.getSlider().getNormalizedReading() );
            }
        } );
        _bucketNode.getSlider().addMouseListener( new MouseAdapter() {

            public void mousePressed( MouseEvent me ) {
                _model.getClock().setPaused( true );
                _model.getClock().resetSimulationTime();
                _model.resetActiveAndDecayedNuclei();
                _proportionsChart.clear();
            }

            public void mouseReleased( MouseEvent me ) {
                _model.getClock().setPaused( false );
            }
        } );

        // Add a button to the canvas for resetting the nuclei.
        _resetButtonNode = new AutoPressButtonNode( NuclearPhysicsStrings.RESET_ALL_NUCLEI, 22,
                                                    BUCKET_AND_BUTTON_COLOR );
        _resetButtonNode.setOffset( _bucketNode.getFullBoundsReference().getCenterX()
                                    - _resetButtonNode.getFullBoundsReference().width / 2, -70 );
        _chartLayer.addChild( _resetButtonNode );

        // Register to receive button pushes.
        _resetButtonNode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                _model.getClock().resetSimulationTime();
                _model.resetActiveAndDecayedNuclei();
                _proportionsChart.clear();
            }
        } );

        // Add the diagram that will depict the relative concentration of
        // pre- and post-decay nuclei.
        _proportionsChart = new NuclearDecayProportionChart( true, false, true, true );
        _proportionsChart.setDisplayInfoForNucleusType( _model.getNucleusType() );
        _proportionsChart.setSquareModeEnabled( true );
        _chartLayer.addChild( _proportionsChart );

        // Register with the model for notifications of nuclei coming and
        // going.
        _model.addListener( new NuclearDecayListenerAdapter() {
            public void modelElementAdded( Object modelElement ) {
                handleModelElementAdded( modelElement );
            }

            ;

            public void modelElementRemoved( Object modelElement ) {
                handleModelElementRemoved( modelElement );
            }

            ;

            public void nucleusTypeChanged() {
                _proportionsChart.clear();
                double halfLife = HalfLifeInfo.getHalfLifeForNucleusType( _model.getNucleusType() );
                _proportionsChart.setTimeParameters( halfLife * 3.2, halfLife );
                _proportionsChart.setDisplayInfoForNucleusType( _model.getNucleusType() );
//                updateChartData();
                _bucketNode.resetSliderPosition();
            }
        } );

        // Add a listener for when the canvas is resized.
        addComponentListener( new ComponentAdapter() {

            /**
             * This method is called when the canvas is resized.  In response,
             * we generally pass this event on to child nodes that need to be
             * aware of it.
             */
            public void componentResized( ComponentEvent e ) {
                update();
            }
        } );

        // Add a listener for when the clock gets reset.
        _model.getClock().addClockListener( new ClockAdapter() {

            public void simulationTimeReset( ClockEvent clockEvent ) {
                // When the simulation time is reset, clear the chart.
                _proportionsChart.clear();
            }
        } );
    }

    /**
     * Highly specialized function that can be used to move nuclei into or out
     * of the holding area (a.k.a. the bucket) based on the ratio of nuclei
     * outside to total nuclei.
     */
    private void setProportionOfNucleiOutsideHoldingArea( double targetProportion ) {

        // Set up some variables that will make calculations more straightforward.
        int totalNumNuclei = _model.getTotalNumNuclei();
        int numNucleiInHoldingArea = _model.getNumNucleiInHoldingArea();
        int numNucleiOutOfHoldingArea = totalNumNuclei - numNucleiInHoldingArea;

        // Calculate the current proportion of nuclei outside the bucket.
        double currentProportion = (double) numNucleiOutOfHoldingArea / (double) totalNumNuclei;

        if ( currentProportion > targetProportion ) {
            // We need to move some nuclei into the bucket.
            int numNucleiToMove = numNucleiOutOfHoldingArea - (int) Math.round( targetProportion * totalNumNuclei );
            moveNucleiToBucket( numNucleiToMove );
        }
        else if ( currentProportion < targetProportion ) {
            // We need to move some nuclei from the bucket into the main canvas area.
            int numNucleiToMove = (int) Math.round( targetProportion * totalNumNuclei ) - numNucleiOutOfHoldingArea;
            moveNucleiFromBucket( numNucleiToMove );
        }
    }

    /**
     * Update the layout on the canvas.
     */
    public void update() {

        super.update();

        _proportionsChart.componentResized( new Rectangle2D.Double( 0, 0, getWorldSize().getWidth() * 1.00 - 15,
                                                                    getWorldSize().getHeight() * PROPORTION_CHART_FRACTION ) );
        PBounds propChartBounds = _proportionsChart.getFullBoundsReference();
        _proportionsChart.setOffset( -propChartBounds.width / 2 + 3,
                                     getWorldSize().getHeight() * ( 1 - HEIGHT_TRANSLATION_FACTOR )
                                     - _proportionsChart.getFullBoundsReference().height );
    }

    /**
     * Extract the specified number of nuclei from the bucket and place them
     * on the canvas.  If there aren't enough in the bucket, add as many as
     * possible.
     *
     * @param numNucleiToMove - Number of nuclei to move.
     * @return - Number of nuclei actually moved.
     */
    private int moveNucleiFromBucket( int numNucleiToMove ) {

        int numberOfNucleiObtained;
        for ( numberOfNucleiObtained = 0; numberOfNucleiObtained < numNucleiToMove; numberOfNucleiObtained++ ) {
            AbstractAtomicNucleusNode nucleusNode = _bucketNode.extractAnyNucleusFromBucket();
            if ( nucleusNode == null ) {
                // The bucket must be empty, so there is nothing more to do.
                break;
            }
            else {
                // Make the node a child of the appropriate layer on the canvas.
                _particleLayer.addChild( nucleusNode );

                // Move the nucleus to an open location outside of the holding
                // area.
                nucleusNode.getNucleusRef().setPosition( _model.findOpenNucleusLocation() );

                // Activate the nucleus so that it will decay.
                AtomicNucleus nucleus = nucleusNode.getNucleusRef();
                if ( nucleus instanceof NuclearDecayControl ) {
                    ( (NuclearDecayControl) nucleus ).activateDecay();
                }
            }
        }

        return numberOfNucleiObtained;
    }

    /**
     * Move the specified number of nuclei from the canvas to the bucket.  If
     * there aren't enough to meet the request, move as many as possible.
     *
     * @param numNucleiToMove - Number of nuclei to move.
     * @return - Number of nuclei actually moved.
     */
    private int moveNucleiToBucket( int numNucleiToMove ) {

        int numberOfNucleiMoved;
        for ( numberOfNucleiMoved = 0; numberOfNucleiMoved < numNucleiToMove; numberOfNucleiMoved++ ) {

            // Get an arbitrary nucleus that is not in the holding area.
            AtomicNucleus nucleus = _model.getAnyNonHeldNucleus();

            if ( nucleus == null ) {
                // The play area must be empty, so there is nothing more to do.
                break;
            }
            else {
                // Move the nucleus to the holding area.
                nucleus.reset();
                _model.moveNucleusToHoldingArea( nucleus );

                // Find the node associated with this nucleus.
                AbstractAtomicNucleusNode nucleusNode = (AbstractAtomicNucleusNode) _mapNucleiToNodes.get( nucleus );

                // Add this node to the bucket.
                _bucketNode.addNucleus( nucleusNode );
            }
        }

        return numberOfNucleiMoved;
    }

    /**
     * Handle a notification from the model that an element (generally a
     * nucleus) was added.
     *
     * @param modelElement
     */
    private void handleModelElementAdded( Object modelElement ) {

        if ( modelElement instanceof AtomicNucleus ) {
            // A new nucleus has been added to the model.  Create a
            // node for it and add it to the nucleus-to-node map.
            AtomicNucleus nucleus = (AtomicNucleus) modelElement;
            UnlabeledSphericalAtomicNucleusNode atomicNucleusNode =
                    new UnlabeledSphericalAtomicNucleusNode( nucleus );

            // Map this node and nucleus together.
            _mapNucleiToNodes.put( nucleus, atomicNucleusNode );

            if ( _model.isNucleusInHoldingArea( nucleus ) ) {
                // The nucleus is in the holding area in the model, so place
                // it in the bucket in order to convey this to the user.
                _bucketNode.addNucleus( atomicNucleusNode );
            }
            else {
                // The nucleus is outside of the holding area, so just add it
                // directly to the appropriate layer.
                _particleLayer.addChild( atomicNucleusNode );
            }
        }
        else {
            System.err.println( "WARNING: Unrecognized model element added, unable to create node for canvas." );
        }
    }

    /**
     * Handle a notification from the model that indicates that an element
     * (e.g. a nucleus) was removed.  This generally means that the
     * corresponding view elements should also go away.
     *
     * @param modelElement
     */
    private void handleModelElementRemoved( Object modelElement ) {

        if ( modelElement instanceof AtomicNucleus ) {
            AbstractAtomicNucleusNode nucleusNode = (AbstractAtomicNucleusNode) _mapNucleiToNodes.get( modelElement );
            if ( nucleusNode == null ) {
                System.err.println( this.getClass().getName() + ": Error - Could not find node for removed model element." );
            }
            else {
                // Remove the node from the canvas or the bucket.
                if ( _bucketNode.isNodeInBucket( nucleusNode ) ) {
                    _bucketNode.removeNucleus( nucleusNode );
                }
                else {
                    PNode child = _particleLayer.removeChild( nucleusNode );
                    if ( child == null ) {
                        System.err.println( this.getClass().getName() + ": Error - Could not remove nucleus from canvas." );
                    }
                }

                // Tell this node to remove itself as a listener from anything
                // that it may have registered with.
                nucleusNode.cleanup();
            }
            _mapNucleiToNodes.remove( modelElement );
        }
    }

    private void addNewDataPointToChart() {
        _proportionsChart.addDataPoint( _model.convertSimTimeToAdjustedTime( _model.getClock().getSimulationTime() ),
                                        _model.getNumActiveNuclei(), _model.getNumDecayedNuclei() );
    }
}
