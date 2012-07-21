// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.genenetwork.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * Primary model for the Lac Operon flavor of this sim.  The primary
 * responsibilities for this class are:
 * - To maintain the references of the individual model elements.
 * - To step each individual element in time.
 * - To provide an API for adding new model elements.
 * - To provide an API for obtaining references to and information about
 * model elements.
 */
public class LacOperonModel implements IGeneNetworkModelControl {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    protected static final double MODEL_AREA_WIDTH = 140;
    private static final double MODEL_AREA_HEIGHT = 120;
    private static final Rectangle2D MODEL_BOUNDS = new Rectangle2D.Double( -MODEL_AREA_WIDTH / 2,
                                                                            -MODEL_AREA_HEIGHT / 2, MODEL_AREA_WIDTH, MODEL_AREA_HEIGHT );
    protected static final Random RAND = new Random();

    // Constants that define where in the model space the DNA strand will be.
    private static final double DNA_STRAND_WIDTH = MODEL_AREA_WIDTH * 1.3;
    private static final double DNA_STRAND_HEIGHT = 1.5;  // In nanometers.
    protected static final Dimension2D DNA_STRAND_SIZE = new PDimension( DNA_STRAND_WIDTH, DNA_STRAND_HEIGHT );
    protected static final Point2D DNA_STRAND_POSITION = new Point2D.Double( 0, -20 );

    // Constants that define where the mobile model elements can go.
    private static final Rectangle2D MOTION_BOUNDS = new Rectangle2D.Double( MODEL_BOUNDS.getMinX(),
                                                                             DNA_STRAND_POSITION.getY(), MODEL_BOUNDS.getWidth(),
                                                                             MODEL_BOUNDS.getHeight() - DNA_STRAND_POSITION.getY() + MODEL_BOUNDS.getMinY() );

    private static final Rectangle2D MOTION_BOUNDS_ABOVE_DNA = new Rectangle2D.Double( MODEL_BOUNDS.getMinX(),
                                                                                       DNA_STRAND_POSITION.getY() + DNA_STRAND_HEIGHT + 10, MODEL_BOUNDS.getWidth(),
                                                                                       MODEL_BOUNDS.getMaxY() - ( DNA_STRAND_POSITION.getY() + DNA_STRAND_HEIGHT + 10 ) );

    // Constant that controls delayed enabling of lactose injection.
    private static final int LACTOSE_INJECTION_ENABLE_DELAY = 3000;  // In milliseconds.

    // Constant that controls the amount of time between automatic injections
    // of a molecule of lactose.
    private static final double INTER_LACTOSE_INJECTION_TIME = 1.65; // In seconds.

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private final GeneNetworkClock clock;
    protected ArrayList<IGeneNetworkModelListener> listeners = new ArrayList<IGeneNetworkModelListener>();

    // The DNA strand.
    private DnaStrand dnaStrand = new DnaStrand( this, DNA_STRAND_SIZE, DNA_STRAND_POSITION );

    // Lists of simple model elements for which multiple instances can exist.
    private final ArrayList<LacI> lacIList = new ArrayList<LacI>();
    private final ArrayList<LacZ> lacZList = new ArrayList<LacZ>();
    private final ArrayList<LacY> lacYList = new ArrayList<LacY>();
    private final ArrayList<Glucose> glucoseList = new ArrayList<Glucose>();
    private final ArrayList<Galactose> galactoseList = new ArrayList<Galactose>();
    private final ArrayList<RnaPolymerase> rnaPolymeraseList = new ArrayList<RnaPolymerase>();
    private final ArrayList<MessengerRna> messengerRnaList = new ArrayList<MessengerRna>();
    private final ArrayList<TransformationArrow> transformationArrowList = new ArrayList<TransformationArrow>();

    // Lists of simple model elements for which only one instance can exist.
    private Cap cap = null;
    private CapBindingRegion capBindingRegion = null;
    private LacOperator lacOperator = null;
    private LacIGene lacIGene = null;
    private LacZGene lacZGene = null;
    private LacYGene lacYGene = null;
    private LacIPromoter lacIPromoter = null;
    private LacPromoter lacPromoter = null;

    // Location, in model space, of the tool box that is shown in the view.
    // This is needed so that we can figure out when the user has moved a
    // model element over the toolbox (and is probably trying to remove it
    // from the model).
    private Rectangle2D toolBoxRect = new Rectangle2D.Double( 0, 0, 0, 0 );

    // State variable that tracks whether lactose may be injected.
    private boolean lactoseInjectionAllowed = false;

    // Variables that control the automatic injection of lactose.
    private boolean automaticLactoseInjectionEnabled = false;
    private double automaticLactoseInjectionCountdown = 0;
    private Point2D automaticLactoseInjectionPoint = new Point2D.Double();
    private MutableVector2D automaticLactoseInjectionVelocity = new MutableVector2D();

    // State variable that tracks whether the legend should be shown.
    private boolean isLegendVisible = false;

    // State variable that tracks whether lactose meter should be shown.
    private boolean isLactoseMeterVisible = false;

    // Timer used for delayed enabling of lactose injection.
    private final Timer delayedLactoseInjectionEnableTimer = new Timer( LACTOSE_INJECTION_ENABLE_DELAY,
                                                                        new ActionListener() {

                                                                            public void actionPerformed( ActionEvent e ) {
                                                                                setLactoseInjectionAllowed( true );
                                                                                delayedLactoseInjectionEnableTimer.stop();
                                                                            }
                                                                        } );

    // Count of the amount of lactose in the cell.
    int lactoseLevel;

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

    /**
     * Constructor for the main model for simulating the Lac Operon gene
     * network.
     *
     * @param clock        - Simulation clock that will drive the model.
     * @param simulateLacY - Boolean that controls whether LacY (the protein
     *                     that transports lactose across the cell membrane) will be simulated.
     *                     This has a number of implications for what is and isn't included in
     *                     the operation of the model.
     */
    public LacOperonModel( GeneNetworkClock clock, boolean simulateLacY ) {
        super();

        this.clock = clock;

        clock.addClockListener( new ClockAdapter() {

            @Override
            public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );

        addInitialModelElements();
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    /**
     * Reset the model, which will remove all model elements.
     */
    public void reset() {

        removeElementsFromModel( lacIList );
        removeElementsFromModel( lacZList );
        removeElementsFromModel( lacYList );
        removeElementsFromModel( glucoseList );
        removeElementsFromModel( galactoseList );
        removeElementsFromModel( rnaPolymeraseList );
        removeElementsFromModel( messengerRnaList );
        removeElementsFromModel( transformationArrowList );

        if ( cap != null ) {
            cap.removeFromModel();
            cap = null;
        }
        if ( capBindingRegion != null ) {
            capBindingRegion.removeFromModel();
            capBindingRegion = null;
        }
        if ( lacOperator != null ) {
            lacOperator.removeFromModel();
            lacOperator = null;
        }
        if ( lacIGene != null ) {
            lacIGene.removeFromModel();
            lacIGene = null;
        }
        if ( lacZGene != null ) {
            lacZGene.removeFromModel();
            lacZGene = null;
        }
        if ( lacYGene != null ) {
            lacYGene.removeFromModel();
            lacYGene = null;
        }
        if ( lacIPromoter != null ) {
            lacIPromoter.removeFromModel();
            lacIPromoter = null;
        }
        if ( lacPromoter != null ) {
            lacPromoter.removeFromModel();
            lacPromoter = null;
        }

        setLactoseInjectionAllowed( false );
        setAutomaticLactoseInjectionEnabled( false );
        setLegendVisible( false );
        setLactoseMeterVisible( false );

        addInitialModelElements();
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#getLacIList()
      */
    public ArrayList<LacI> getLacIList() {
        return lacIList;
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#getLacZList()
      */
    public ArrayList<LacZ> getLacZList() {
        return lacZList;
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#getLacYList()
      */
    public ArrayList<LacY> getLacYList() {
        return lacYList;
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#getGlucoseList()
      */
    public ArrayList<Glucose> getGlucoseList() {
        return glucoseList;
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#getGalactoseList()
      */
    public ArrayList<Galactose> getGalactoseList() {
        return galactoseList;
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#getRnaPolymeraseList()
      */
    public ArrayList<RnaPolymerase> getRnaPolymeraseList() {
        return rnaPolymeraseList;
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#getCap()
      */
    public Cap getCap() {
        return cap;
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#getCapBindingRegion()
      */
    public CapBindingRegion getCapBindingRegion() {
        return capBindingRegion;
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#getLacOperator()
      */
    public LacOperator getLacOperator() {
        return lacOperator;
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#getLacIGene()
      */
    public LacIGene getLacIGene() {
        return lacIGene;
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#getLacZGene()
      */
    public LacZGene getLacZGene() {
        return lacZGene;
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#getLacYGene()
      */
    public LacYGene getLacYGene() {
        return lacYGene;
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#getLacIPromoter()
      */
    public LacIPromoter getLacIPromoter() {
        return lacIPromoter;
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#getLacPromoter()
      */
    public LacPromoter getLacPromoter() {
        return lacPromoter;
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#setToolBoxRect()
      */
    public void setToolBoxRect( Rectangle2D rect ) {
        toolBoxRect.setFrame( rect );
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#getNearestLactose(Point2D pt, boolean freeOnly)
      */
    public Glucose getNearestLactose( Point2D pt, PositionWrtCell positionWrtCell, boolean freeOnly ) {
        assert positionWrtCell != null; // Has to request something specific.
        assert positionWrtCell != PositionWrtCell.WITHIN_CELL_MEMBRANE; // This would be a weird request.
        double distance = Double.POSITIVE_INFINITY;
        Glucose nearestFreeGlucose = null;
        for ( Glucose glucose : glucoseList ) {
            if ( glucose.isBoundToGalactose() &&
                 ( !freeOnly || glucose.isAvailableForAttaching() ) &&
                 pt.distance( glucose.getPositionRef() ) < distance &&
                 classifyPosWrtCell( glucose.getPositionRef() ) == positionWrtCell ) {

                // This is the best candidate so far.
                nearestFreeGlucose = glucose;
                distance = pt.distance( glucose.getPositionRef() );
            }
        }

        return nearestFreeGlucose;
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#getNearestFreeLacI
      */
    public LacI getNearestFreeLacI( Point2D pt ) {
        double distance = Double.POSITIVE_INFINITY;
        LacI nearestFreeLacI = null;
        for ( LacI lacI : lacIList ) {
            if ( lacI.isAvailableForAttaching() &&
                 pt.distance( lacI.getPositionRef() ) < distance ) {

                // This is the best candidate so far.
                nearestFreeLacI = lacI;
                distance = pt.distance( lacI.getPositionRef() );
            }
        }

        return nearestFreeLacI;
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#findNearestFreeRnaPolymerase(Point2D pt)
      */
    public RnaPolymerase getNearestFreeRnaPolymerase( Point2D pt ) {
        double distance = Double.POSITIVE_INFINITY;
        RnaPolymerase nearestFreeRnaPolymerase = null;
        for ( RnaPolymerase rnaPolymerase : rnaPolymeraseList ) {
            if ( rnaPolymerase.isAvailableForAttaching() &&
                 pt.distance( rnaPolymerase.getPositionRef() ) < distance ) {

                // This is the best candidate so far.
                nearestFreeRnaPolymerase = rnaPolymerase;
                distance = pt.distance( rnaPolymerase.getPositionRef() );
            }
        }

        return nearestFreeRnaPolymerase;
    }

    public boolean isLactoseInjectionAllowed() {
        return lactoseInjectionAllowed;
    }

    public void setLactoseInjectionAllowed( boolean isLactoseInjectionAllowed ) {
        // Always make sure that the delay time is stopped when this is called.
        delayedLactoseInjectionEnableTimer.stop();

        // Do the actual set and notification.
        if ( isLactoseInjectionAllowed != this.lactoseInjectionAllowed ) {
            this.lactoseInjectionAllowed = isLactoseInjectionAllowed;
            notifyLactoseInjectionAllowedStateChange();
        }
    }

    public void setAutomaticLactoseInjectionEnabled( boolean automaticLactoseInjectionEnabled ) {
        if ( this.automaticLactoseInjectionEnabled != automaticLactoseInjectionEnabled ) {
            this.automaticLactoseInjectionEnabled = automaticLactoseInjectionEnabled;
            notifyAutomaticLactoseInjectionEnabledStateChange();
            if ( automaticLactoseInjectionEnabled == true ) {
                // Set the countdown to zero initially so that it will start right away.
                automaticLactoseInjectionCountdown = 0;
            }
        }
    }

    public boolean isAutomaticLactoseInjectionEnabled() {
        return this.automaticLactoseInjectionEnabled;
    }

    public void setAutomaticLactoseInjectionParams( Point2D location, MutableVector2D velocity ) {
        automaticLactoseInjectionPoint.setLocation( location );
        automaticLactoseInjectionVelocity.setComponents( velocity.getX(), velocity.getY() );
    }

    /**
     * This method starts a timer that will, when it expires, enable lactose
     * injection.  If the timer is already running, the request will be
     * ignored.  This was created because the injector was appearing on the
     * screen at the same time as some other key elements, so people were
     * worried that it would distract, so there needed to be a means of
     * turning on lactose injection in a delayed fashion.
     */
    public void startLactoseInjectionAllowedTimer() {
        if ( !lactoseInjectionAllowed && !delayedLactoseInjectionEnableTimer.isRunning() ) {
            delayedLactoseInjectionEnableTimer.restart();
        }
    }

    public void setLegendVisible( boolean isLegendVisible ) {
        if ( isLegendVisible != this.isLegendVisible ) {
            this.isLegendVisible = isLegendVisible;
            notifyLegendVisibilityStateChange();
        }
    }

    public boolean isLegendVisible() {
        return this.isLegendVisible;
    }

    public void setLactoseMeterVisible( boolean isLactoseMeterVisible ) {
        if ( isLactoseMeterVisible != this.isLactoseMeterVisible ) {
            this.isLactoseMeterVisible = isLactoseMeterVisible;
            notifyLactoseMeterVisibilityStateChange();
        }
    }

    public boolean isLactoseMeterVisible() {
        return isLactoseMeterVisible;
    }

    public boolean isPointInToolBox( Point2D pt ) {
        return toolBoxRect.contains( pt );
    }

    private void addInitialModelElements() {

        // Initialize the elements that are floating around the cell.
        for ( int i = 0; i < 2; i++ ) {
            RnaPolymerase rnaPolymerase = new RnaPolymerase( this );
            randomlyInitModelElement( rnaPolymerase );
            rnaPolymeraseList.add( rnaPolymerase );
            notifyModelElementAdded( rnaPolymerase );
        }
    }

    public GeneNetworkClock getClock() {
        return clock;
    }

    /**
     * Get the rectangle that represents the cell membrane.  Returns null if
     * no cell membrane is included in this model.  This is the default in the
     * base class, and subclasses should override this to add the membrane.
     *
     * @return
     */
    public Rectangle2D getCellMembraneRect() {
        return null;
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#getInteriorMotionBoundsAboveDna()
      */
    public Rectangle2D getInteriorMotionBoundsAboveDna() {
        return MOTION_BOUNDS_ABOVE_DNA;
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#getInteriorMotionBounds()
      */
    public Rectangle2D getInteriorMotionBounds() {
        return MOTION_BOUNDS;
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#getExteriorMotionBounds()
      */
    public Rectangle2D getExteriorMotionBounds() {
        // This version of the model has no exterior, so return null.
        return null;
    }

    public boolean isLacIAttachedToDna() {
        if ( lacOperator == null ) {
            return false;
        }
        else {
            return lacOperator.isLacIAttached();
        }
    }

    public boolean isLacOperatorPresent() {
        return lacOperator != null;
    }

    public boolean isLacZGenePresent() {
        return lacZGene != null;
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#getDnaStrand()
      */
    public DnaStrand getDnaStrand() {
        return dnaStrand;
    }

    /**
     * Used by subclasses to set the DNA strand to something other than the
     * default configuration.
     *
     * @param dnaStrand
     */
    protected void setDnaStrand( DnaStrand dnaStrand ) {
        this.dnaStrand = dnaStrand;
    }

    //----------------------------------------------------------------------------
    // Other Methods
    //----------------------------------------------------------------------------

    private void randomlyInitModelElement( SimpleModelElement modelElement ) {
        modelElement.setPosition( ( RAND.nextDouble() - 0.5 ) * ( MODEL_AREA_WIDTH / 2 ),
                                  ( RAND.nextDouble() / 2 ) * ( MODEL_AREA_HEIGHT / 2 ) );
        double maxVel = 2;
        modelElement.setVelocity( ( RAND.nextDouble() - 0.5 ) * maxVel, ( RAND.nextDouble() - 0.5 ) * maxVel );
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#getAllSimpleModelElements()
      */
    public ArrayList<SimpleModelElement> getAllSimpleModelElements() {
        ArrayList<SimpleModelElement> allSimples = new ArrayList<SimpleModelElement>();
        allSimples.addAll( rnaPolymeraseList );
        allSimples.addAll( lacIList );
        allSimples.addAll( lacZList );
        allSimples.addAll( lacYList );
        allSimples.addAll( glucoseList );
        allSimples.addAll( galactoseList );
        allSimples.addAll( messengerRnaList );
        allSimples.addAll( transformationArrowList );
        if ( cap != null ) {
            allSimples.add( cap );
        }
        if ( capBindingRegion != null ) {
            allSimples.add( capBindingRegion );
        }
        if ( lacOperator != null ) {
            allSimples.add( lacOperator );
        }
        if ( lacIGene != null ) {
            allSimples.add( lacIGene );
        }
        if ( lacYGene != null ) {
            allSimples.add( lacYGene );
        }
        if ( lacZGene != null ) {
            allSimples.add( lacZGene );
        }
        if ( lacIPromoter != null ) {
            allSimples.add( lacIPromoter );
        }
        if ( lacPromoter != null ) {
            allSimples.add( lacPromoter );
        }

        return allSimples;
    }

    public void addMessengerRna( MessengerRna messengerRna ) {
        messengerRnaList.add( messengerRna );
        notifyModelElementAdded( messengerRna );
    }

    public void addTransformationArrow( TransformationArrow transformationArrow ) {
        transformationArrowList.add( transformationArrow );
        notifyModelElementAdded( transformationArrow );
    }

    public void addLacZ( LacZ lacZ ) {
        lacZList.add( lacZ );
        notifyModelElementAdded( lacZ );

        // If LacZ is present, lactose injection should be allowed.
        startLactoseInjectionAllowedTimer();
    }

    public void addLacY( LacY lacY ) {
        lacYList.add( lacY );
        notifyModelElementAdded( lacY );

        // If LacY is present, lactose injection should be allowed.
        startLactoseInjectionAllowedTimer();
    }

    public void addLacI( LacI lacI ) {
        lacIList.add( lacI );
        notifyModelElementAdded( lacI );

        // If LacI is present, lactose injection should be allowed.
        startLactoseInjectionAllowedTimer();
    }

    public LacZGene createAndAddLacZGene( Point2D initialPosition ) {
        assert lacZGene == null; // Only one exists in this model, so multiple calls to this shouldn't occur.
        lacZGene = new LacZGene( this, initialPosition );
        notifyModelElementAdded( lacZGene );
        return lacZGene;
    }

    public LacYGene createAndAddLacYGene( Point2D initialPosition ) {
        assert lacYGene == null; // Only one exists in this model, so multiple calls to this shouldn't occur.
        lacYGene = new LacYGene( this, initialPosition );
        notifyModelElementAdded( lacYGene );
        return lacYGene;
    }

    public LacIGene createAndAddLacIGene( Point2D initialPosition ) {
        assert lacIGene == null; // Only one exists in this model, so multiple calls to this shouldn't occur.
        lacIGene = new LacIGene( this, initialPosition );
        notifyModelElementAdded( lacIGene );
        return lacIGene;
    }

    public LacOperator createAndAddLacOperator( Point2D initialPosition ) {
        assert lacOperator == null; // Only one exists in this model, so multiple calls to this shouldn't occur.
        lacOperator = new LacOperator( this, initialPosition );
        notifyModelElementAdded( lacOperator );
        return lacOperator;
    }

    public LacPromoter createAndAddLacPromoter( Point2D initialPosition ) {
        assert lacPromoter == null; // Only one exists in this model, so multiple calls to this shouldn't occur.
        lacPromoter = new LacPromoter( this, initialPosition );
        notifyModelElementAdded( lacPromoter );
        return lacPromoter;
    }

    public LacIPromoter createAndAddLacIPromoter( Point2D initialPosition ) {
        assert lacIPromoter == null; // Only one exists in this model, so multiple calls to this shouldn't occur.
        lacIPromoter = new LacIPromoter( this, initialPosition );
        notifyModelElementAdded( lacIPromoter );
        return lacIPromoter;
    }

    public RnaPolymerase createAndAddRnaPolymerase( Point2D initialPosition ) {
        RnaPolymerase rnaPolymerase = new RnaPolymerase( this, initialPosition );
        rnaPolymeraseList.add( rnaPolymerase );
        notifyModelElementAdded( rnaPolymerase );
        return rnaPolymerase;
    }

    public void createAndAddLactose( Point2D initialPosition, MutableVector2D initialVelocity ) {

        // Create the two simple model element that comprise lactose.  Note
        // that glucose is considered to be the "dominant partner", so its
        // motion strategy is set while galactose is left alone, assuming that
        // it will attach to glucose and follow it around.
        Glucose glucose = new Glucose( this );
        double xOffset = glucose.getShape().getBounds2D().getWidth() / 2;
        glucose.setPosition( initialPosition.getX() - xOffset, initialPosition.getY() );
        Galactose galactose = new Galactose( this );

        // Set the motion for the glucose (which will define the motion of the
        // lactose molecule) based on whether it is inside or outside of the
        // cell.
        if ( classifyPosWrtCell( initialPosition ) == PositionWrtCell.INSIDE_CELL ) {
            glucose.setMotionStrategy( new InjectionMotionStrategy(
                    MotionBoundsTrimmer.trim( getInteriorMotionBoundsAboveDna(), glucose ), initialVelocity ) );
            glucose.setUpDraggableBounds( PositionWrtCell.INSIDE_CELL );
        }
        else {
            glucose.setMotionStrategy( new InjectionMotionStrategy(
                    MotionBoundsTrimmer.trim( getExteriorMotionBounds(), glucose ), initialVelocity ) );
            glucose.setUpDraggableBounds( PositionWrtCell.OUTSIDE_CELL );
        }

        // Attach these two to one another.
        glucose.formLactose( galactose );

        // Add these elements to the list.
        galactoseList.add( galactose );
        notifyModelElementAdded( galactose );
        glucoseList.add( glucose );
        notifyModelElementAdded( glucose );
    }

    private void stepInTime( double dt ) {

        // Step the elements for which there can be multiple instances.
        stepElementsInTime( lacZList, dt );
        stepElementsInTime( lacIList, dt );
        stepElementsInTime( lacYList, dt );
        stepElementsInTime( glucoseList, dt );
        stepElementsInTime( galactoseList, dt );
        stepElementsInTime( rnaPolymeraseList, dt );
        stepElementsInTime( messengerRnaList, dt );
        stepElementsInTime( transformationArrowList, dt );

        // See if the level of lactose has changed and update it if so.
        int currentLactoseLevel = 0;
        for ( Glucose glucose : glucoseList ) {
            if ( glucose.isBoundToGalactose() && classifyPosWrtCell( glucose.getPositionRef() ) == PositionWrtCell.INSIDE_CELL ) {
                currentLactoseLevel++;
            }
        }
        if ( lactoseLevel != currentLactoseLevel ) {
            // Save the new value.
            lactoseLevel = currentLactoseLevel;
            // Notify listenters of the change.
            notifyLactoseLevelChanged();
        }

        // Step the elements for which there can be only one.
        if ( cap != null ) {
            cap.stepInTime( dt );
        }
        if ( capBindingRegion != null ) {
            if ( capBindingRegion.getExistenceStrength() == 0 ) {
                capBindingRegion.removeFromModel();
                capBindingRegion = null;
            }
            else {
                capBindingRegion.stepInTime( dt );
            }
        }
        if ( lacOperator != null ) {
            if ( lacOperator.getExistenceStrength() == 0 ) {
                lacOperator.removeFromModel();
                lacOperator = null;
            }
            else {
                lacOperator.stepInTime( dt );
            }
        }
        if ( lacIGene != null ) {
            if ( lacIGene.getExistenceStrength() == 0 ) {
                lacIGene.removeFromModel();
                lacIGene = null;
            }
            else {
                lacIGene.stepInTime( dt );
            }
        }
        if ( lacYGene != null ) {
            if ( lacYGene.getExistenceStrength() == 0 ) {
                lacYGene.removeFromModel();
                lacYGene = null;
            }
            else {
                lacYGene.stepInTime( dt );
            }
        }
        if ( lacZGene != null ) {
            if ( lacZGene.getExistenceStrength() == 0 ) {
                lacZGene.removeFromModel();
                lacZGene = null;
            }
            else {
                lacZGene.stepInTime( dt );
            }
        }
        if ( lacIPromoter != null ) {
            if ( lacIPromoter.getExistenceStrength() == 0 ) {
                lacIPromoter.removeFromModel();
                lacIPromoter = null;
            }
            else {
                lacIPromoter.stepInTime( dt );
            }
        }
        if ( lacPromoter != null ) {
            if ( lacPromoter.getExistenceStrength() == 0 ) {
                lacPromoter.removeFromModel();
                lacPromoter = null;
            }
            else {
                lacPromoter.stepInTime( dt );
            }
        }

        // Is automatic injection of lactose enabled?
        if ( automaticLactoseInjectionEnabled && lactoseInjectionAllowed ) {
            // Yes it is, so see if it is time to inject.
            automaticLactoseInjectionCountdown -= dt;
            if ( automaticLactoseInjectionCountdown <= 0 ) {
                // Inject a molecule of lactose.
                createAndAddLactose( automaticLactoseInjectionPoint, automaticLactoseInjectionVelocity );
                // Reset the countdown.
                automaticLactoseInjectionCountdown = INTER_LACTOSE_INJECTION_TIME;
            }
        }
    }

    private void stepElementsInTime( ArrayList<? extends IModelElement> elements, double dt ) {
        ArrayList<IModelElement> toBeRemoved = new ArrayList<IModelElement>();
        for ( IModelElement element : elements ) {
            if ( element.getExistenceStrength() <= 0 ) {
                // If a model element gets to the point where its existence
                // strength is zero, it has essentially died, or dissolved, or
                // just "ceased to be", so should be removed from the model.
                toBeRemoved.add( element );
            }
            else {
                element.stepInTime( dt );
            }
        }

        // Remove the identified elements, if any.
        elements.removeAll( toBeRemoved );
        for ( IModelElement element : toBeRemoved ) {
            // Send out notifications of removal.
            element.removeFromModel();
        }
    }

    private void removeElementsFromModel( ArrayList<? extends IModelElement> elements ) {
        ArrayList<? extends IModelElement> elementsToRemove = new ArrayList<IModelElement>( elements );
        for ( IModelElement element : elementsToRemove ) {
            elements.remove( element );
            element.removeFromModel();
        }
    }

    //------------------------------------------------------------------------
    // Listener support
    //------------------------------------------------------------------------

    protected void notifyModelElementAdded( SimpleModelElement modelElement ) {
        // Notify all listeners of the addition of this model element.
        for ( IGeneNetworkModelListener listener : listeners ) {
            listener.modelElementAdded( modelElement );
        }
    }

    protected void notifyLactoseInjectionAllowedStateChange() {
        // Notify all listeners of the change to the injection allowed state.
        for ( IGeneNetworkModelListener listener : listeners ) {
            listener.lactoseInjectionAllowedStateChange();
        }
    }

    protected void notifyAutomaticLactoseInjectionEnabledStateChange() {
        // Notify all listeners of the change to the automatic injection state.
        for ( IGeneNetworkModelListener listener : listeners ) {
            listener.automaticLactoseInjectionEnabledStateChange();
        }
    }

    protected void notifyLegendVisibilityStateChange() {
        // Notify all listeners of the change to the legend visibility state.
        for ( IGeneNetworkModelListener listener : listeners ) {
            listener.legendVisibilityStateChange();
        }
    }

    protected void notifyLactoseMeterVisibilityStateChange() {
        // Notify all listeners of the change to the lactose meter visibility state.
        for ( IGeneNetworkModelListener listener : listeners ) {
            listener.lactoseMeterVisibilityStateChange();
        }
    }

    protected void notifyLactoseLevelChanged() {
        // Notify all listeners of the change to the lactose level.
        for ( IGeneNetworkModelListener listener : listeners ) {
            listener.lactoseLevelChanged();
        }
    }

    public void addListener( IGeneNetworkModelListener listener ) {
        if ( listeners.contains( listener ) ) {
            // Don't bother re-adding.
            System.err.println( getClass().getName() + "- Warning: Attempting to re-add a listener that is already listening." );
            assert false;
            return;
        }

        listeners.add( listener );
    }

    public void removeListener( IGeneNetworkModelListener listener ) {
        listeners.remove( listener );
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#classifyPosWrtCell(Point2D pt)
      */
    public PositionWrtCell classifyPosWrtCell( Point2D pt ) {
        // In this base class, there is no exterior of the cell, so everything
        // is inside.
        return PositionWrtCell.INSIDE_CELL;
    }

    /* (non-Javadoc)
      * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#getOpenSpotForLacY()
      */
    public Point2D getOpenSpotForLacY() {
        // Does nothing in this class, and shouldn't really be called.
        assert false;
        return null;
    }

    public int getLactoseLevel() {
        return lactoseLevel;
    }
}
