/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.coreadditions.TxGraphic;
import edu.colorado.phet.nuclearphysics.Config;
import edu.colorado.phet.nuclearphysics.model.*;
import edu.colorado.phet.nuclearphysics.view.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.AffineTransformOp;
import java.util.*;
import java.util.List;

/**
 * ControlledFissionModule
 * <p/>
 * Presents a large number of U235 nuclei and control rods in a chamber. Neutrons are fired into the chamber, and
 * the chain reaction is controlled by rods that absorb neutron. The rods can be moved in and out of the chamber
 * by the user.
 * <p/>
 * The chamger is divided up by channels in which control rods can move in and out. The Uranium nuclei are placed
 * evenly in the areas between the channels.
 * <p/>
 * There are U238 nuclei in the chamber, equal in number to and event spaced between the U235 nuclei, that act as dampers
 * for the reaction. These nuclei do not have visible graphics. The user is not supposed to know they are there.
 * <p/>
 * The model is given a FissionDetector instance that takes care of detecting when neutrons hit nuclei, and what
 * should happen when they do. FissionDetector is somewhat intelligent about searching for collisions to reduce the
 * number of checks between neutrons and nuclei.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ControlledFissionModule extends ChainReactionModule {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    public static final int VERTICAL = 1, HORIZONTAL = 2;
    public static double SCALE = 0.2;

    private static final double VESSEL_LAYER = 100;
    private static final double CONTROL_ROD_LAYER = VESSEL_LAYER - 1;
    private static final double refWidth = 700;
    private static final double refHeight = 300;

    private static final double DEAFULT_U235_ABSORPTION_PROB = 0.75;
    private static final double DEAFULT_U238_ABSORPTION_PROB = 0.25;
    private static final double DEAFULT_ROD_ABSORPTION_PROB = 1.0;
    private static final double DEFAULT_INTER_NUCLEAR_SPACING = 2.5;

    private static BufferedImage u235Img;
    private static BufferedImage rubidiumImg;
    private static BufferedImage cesiumImg;

    // Make flyweights for nucleus graphics
    static {
        NucleusGraphicFactory nucleusGraphicFactory = new NucleusGraphicFactory();
        u235Img = nucleusGraphicFactory.create( new Uranium235( new Point2D.Double(), null ) ).getImage();
        rubidiumImg = nucleusGraphicFactory.create( new Rubidium( new Point2D.Double() ) ).getImage();
        cesiumImg = nucleusGraphicFactory.create( new Cesium( new Point2D.Double() ) ).getImage();
    }

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    private double vesselWidth;
    private double vesselHeight;
    private int numChannels = 5;
    private Vessel vessel;
    private int nCols;
    private ControlRodGroupGraphic controlRodGroupGraphic;
    private ControlRod[] controlRods;
    private int numNeutronsFired = 1;
    private EnergyGraphDialog energyGraphDialog;
    private boolean energyGraphDialogVisible;
    private long fissionDelay;
    private double rodAbsorptionProbability = 1;
    private DevelopmentControlDialog developmentControlDialog;
    private PeriodicNeutronGun periodicNeutronGun;
    private int DEFAULT_NUM_NEUTRONS_FIRED = 2;
    private int DEFAULT_NUM_CONTROLS_RODS = 5;
    private ArrayList neutronLaunchers = new ArrayList();
    private FissionDetector fissionDetector;
    private ControlledFissionModule.NucleusGraphicRemover nucleusGraphicRemover;

    // TODO: clean up when refactoring is done
    public void setContainmentEnabled( boolean b ) {
        // noop
    }


    /**
     * @param clock
     */
    public ControlledFissionModule( IClock clock ) {
        super( SimStrings.get( "ModuleTitle.ControlledReaction" ), clock );
    }


    protected List getLegendClasses() {
        Object[] legendClasses = new Object[]{
                LegendPanel.NEUTRON,
                LegendPanel.U235,
        };
        return Arrays.asList( legendClasses );
    }

    protected void init() {
        super.init();
        energyGraphDialog = new EnergyGraphDialog( PhetUtilities.getPhetFrame(), getU235Nuclei().size() );
        Container container = PhetUtilities.getPhetFrame();
        int x = container.getX() + 30;
        int y = container.getY() + container.getHeight() - energyGraphDialog.getHeight() - 30;
        energyGraphDialog.setLocation( x, y );
        super.addControlPanelElement( new ControlledChainReactionControlPanel( this ) );
        init( getClock() );
    }

    /**
     * Sets the values of various parameters so that a sustained reaction will proceed when the
     * control rods are 1/2 in, and things will run away when the rods are all the way out.
     */
    private void setParameterDefaults() {
        setNumNeutronsFired( DEFAULT_NUM_NEUTRONS_FIRED );
        setNumControlRods( DEFAULT_NUM_CONTROLS_RODS );
        setU235AbsorptionProbability( DEAFULT_U235_ABSORPTION_PROB );
        setU238AbsorptionProbability( DEAFULT_U238_ABSORPTION_PROB );
        setRodAbsorptionProbability( DEAFULT_ROD_ABSORPTION_PROB );
        setInterNucleusSpacing( DEFAULT_INTER_NUCLEAR_SPACING );
        createNuclei();
    }

    protected void init( final IClock clock ) {

        // Set congtrol parameters
        setNumNeutronsFired( DEFAULT_NUM_NEUTRONS_FIRED );
        setNumControlRods( DEFAULT_NUM_CONTROLS_RODS );
        setU235AbsorptionProbability( DEAFULT_U235_ABSORPTION_PROB );
        setU238AbsorptionProbability( DEAFULT_U238_ABSORPTION_PROB );

        // set the SCALE of the physical panel so we can fit more nuclei in it
        getPhysicalPanel().setPhysicalScale( SCALE );
        vesselWidth = refWidth / SCALE;
        vesselHeight = refHeight / SCALE;

        // Add the vessel. In case we are called in a reset, ditch the old vessel before
        // we build a new one
        if( vessel != null ) {
            getModel().removeModelElement( vessel );
            vessel.removeAllChangeListeners();
        }
        vessel = new Vessel( -vesselWidth / 2 - 120,
                             -vesselHeight * .7,
                             vesselWidth,
                             vesselHeight,
                             numChannels,
                             (NuclearPhysicsModel)getModel(),
                             clock );
        getModel().addModelElement( vessel );
        VesselGraphic vesselGraphic = new VesselGraphic( getPhysicalPanel(), vessel );
        getPhysicalPanel().addOriginCenteredGraphic( vesselGraphic, VESSEL_LAYER );

        // Add a background to the vessel that will change color with the energy being produced by the reaction
        VesselBackgroundPanel vesselBackgroundPanel = new VesselBackgroundPanel( getPhysicalPanel(), vessel );
        getPhysicalPanel().addOriginCenteredGraphic( vesselBackgroundPanel, ApparatusPanel.LAYER_DEFAULT - 1 );

        // Add control rods. In case we are in a reset, get rid of the old ones first.
        // If it's not a reset, set the location of the rods to be at the top of the
        // vessel. If it is a reset, set the new rods to be at the same height as the
        // old ones
        double controlRodPositionY = vessel.getChannels()[0].getMinY();
        for( int i = 0; controlRods != null && i < controlRods.length; i++ ) {
            ControlRod controlRod = controlRods[i];
            getModel().removeModelElement( controlRod );
            controlRodPositionY = controlRod.getLocation().getY();
        }
        controlRods = createControlRods( controlRodPositionY, vessel );
        setRodAbsorptionProbability( DEAFULT_ROD_ABSORPTION_PROB );
        controlRodGroupGraphic = new ControlRodGroupGraphic( getPhysicalPanel(),
                                                             controlRods,
                                                             vessel,
                                                             getPhysicalPanel().getNucleonTx() );
        getPhysicalPanel().addGraphic( controlRodGroupGraphic, CONTROL_ROD_LAYER );

        // Add a thermometer and a listener that will control the thermometer
        double thermometerColumnHeight = 400;
        final VesselThermometer thermometer = new VesselThermometer( getPhysicalPanel(),
                                                                     new Point2D.Double( vessel.getX() + 200,
                                                                                         vessel.getY() - thermometerColumnHeight ),
                                                                     thermometerColumnHeight, 80, true, 0, Config.MAX_TEMPERATURE,
                                                                     vessel );
        thermometer.setNumericReadoutEnabled( false );
        getPhysicalPanel().addOriginCenteredGraphic( thermometer, 1000 );

        // Add a listener that will hide the U238 and U239 nuclei that are just here to dampen the reaction
        if( nucleusGraphicRemover == null ) {
            nucleusGraphicRemover = new NucleusGraphicRemover();
            getPhysicalPanel().addGraphicListener( nucleusGraphicRemover );
        }

        // Create the nuclei
        setInterNucleusSpacing( DEFAULT_INTER_NUCLEAR_SPACING );
        createNuclei();

        // Create the object that will detect neutron/Uranium collisions and invoke fission behavior. Note
        // that this can't be done until after the call to createNuclei(), because the FissionDetector
        // depends on knowing their placement so that it can optimize performance.
        if( fissionDetector == null ) {
            fissionDetector = new FissionDetector( (NuclearPhysicsModel)getModel() );
            getModel().addModelElement( fissionDetector );
        }

        // Reset the energy graph dialog
        resetEnergyGraphDialog();
    }

    public void start() {
        init( getClock() );
        return;
    }

    public void stop() {
        super.stop();

        // Remove  all the neutron launchers from the model
        for( int i = 0; i < neutronLaunchers.size(); i++ ) {
            NeutronLauncher neutronLauncher = (NeutronLauncher)neutronLaunchers.get( i );
            getModel().removeModelElement( neutronLauncher );
        }
        neutronLaunchers.clear();

        // Remove all the control rods
        getModel().removeModelElement( vessel );
        for( int i = 0; i < controlRods.length; i++ ) {
            ControlRod controlRod = controlRods[i];
            getModel().removeModelElement( controlRod );
        }

        // Remove the fission detector
        getModel().removeModelElement( fissionDetector );
        fissionDetector = null;

        // Make sure all the nuclei are gone
        ( (NuclearPhysicsModel)getModel() ).removeNuclearParticles();
        getU235Nuclei().clear();
        getU238Nuclei().clear();
        getU239Nuclei().clear();

        getPhysicalPanel().removeAllGraphics();
    }

    /**
     * Creates a control rod for each channel in the specified vessel
     *
     * @param yMin   The y position of the top of the rod
     * @param vessel
     * @return an array of control rods
     */
    private ControlRod[] createControlRods( double yMin, Vessel vessel ) {

        // Some excess length for the control rods so they will stick out the bottom of the vessel
        double controlRodExcess = 200;
        ControlRod[] rods = new ControlRod[vessel.getNumControlRodChannels()];
        NuclearPhysicsModel model = (NuclearPhysicsModel)getModel();
        Rectangle2D[] channels = vessel.getChannels();
        for( int i = 0; i < channels.length; i++ ) {
            Rectangle2D channel = channels[i];
            // The computation of the x coordinate may look funny. Here's why it's the way it is: The rod is
            // specified by its centerline, rather than its corner. The second offset of half the channel width
            // is there to make all the rods evenly spaced within the vessel.
            rods[i] = new ControlRod( new Point2D.Double( channel.getMinX() + ( channel.getWidth() ) / 2,
                                                          yMin ),
                                      new Point2D.Double( channel.getMinX() + channel.getWidth() / 2,
                                                          yMin + ( channel.getMaxY() - channel.getMinY() ) + controlRodExcess ),
                                      channel.getWidth(),
                                      model,
                                      rodAbsorptionProbability );
            model.addModelElement( rods[i] );
        }
        return rods;
    }

    /**
     * Overrides superclass behavior to use a different graphic
     *
     * @param nucleus
     */
    protected void addNucleus( Nucleus nucleus ) {
        getNuclei().add( nucleus );
        nucleus.addFissionListener( this );
        getModel().addModelElement( nucleus );

        if( true ) {
            return;
        }

        BufferedImage sourceImg = null;
        if( nucleus instanceof Uranium235 ) {
            sourceImg = u235Img;
        }
        else if( nucleus instanceof Rubidium ) {
            sourceImg = rubidiumImg;
        }
        else if( nucleus instanceof Cesium ) {
            sourceImg = cesiumImg;
        }
        else if( sourceImg == null ) {
//            throw new RuntimeException( "nucleus is of unexpected type" );
        }
        if( sourceImg != null ) {
            final PhetImageGraphic nig = new PhetImageGraphic( getPhysicalPanel(),
                                                               sourceImg,
                                                               (int)( nucleus.getPosition().getX() - nucleus.getRadius() ),
                                                               (int)( nucleus.getPosition().getY() - nucleus.getRadius() ) );
            NuclearModelElement.Listener listener = new NuclearModelElement.Listener() {
                public void leavingSystem( NuclearModelElement nme ) {
                    getPhysicalPanel().removeGraphic( nig );
                }
            };
            nucleus.addListener( listener );
            getPhysicalPanel().addOriginCenteredGraphic( nig, Config.nucleusLevel );
        }
    }


    /**
     * Puts nuclei in the vessel on a grid laid out by the vessel.
     */
    protected void createNuclei() {

        // Create U235 nuclei
        Point2D[] locations = vessel.getInitialNucleusLocations( nCols );
        for( int i = 0; i < locations.length; i++ ) {
            Point2D location = locations[i];
            Nucleus nucleus = new Uranium235( location, (NuclearPhysicsModel)getModel() );
            getU235Nuclei().add( nucleus );
            addNucleus( nucleus );

            // Add the vessel as a listener for when the nucleus fissions, so
            // it can track the energy being released
            nucleus.addFissionListener( vessel );
        }

        // Create U238 nuclei
        double spacing = locations[0].distance( locations[1] );
        for( int i = 0; i < locations.length; i++ ) {
            Point2D location = new Point2D.Double( locations[i].getX() + spacing / 2,
                                                   locations[i].getY() + spacing / 2 );
            Nucleus nucleus = new Uranium238( location, getModel() );
            getU238Nuclei().add( nucleus );
            addNucleus( nucleus );

            // Add the vessel as a listener for when the nucleus fissions, so
            // it can track the energy being released
            nucleus.addFissionListener( vessel );
        }

        getPhysicalPanel().repaint( getPhysicalPanel().getBounds() );
    }

    private void resetEnergyGraphDialog() {
        if( energyGraphDialog != null ) {
            // Tell the energy dialog how many nuclei there are, so it can set up the
            // total energy gauge
            energyGraphDialog.reset( getU235Nuclei().size() );
            vessel.addChangeListener( energyGraphDialog.getVesselChangeListener() );

            // Add the energyGraphDialog as a listener to all the U235 nuclei that are already in existence
            List modelElements = ( (NuclearPhysicsModel)getModel() ).getNuclearModelElements();
            for( int i = 0; i < modelElements.size(); i++ ) {
                Object o = modelElements.get( i );
                if( o instanceof Nucleus ) {
                    Nucleus nucleus = (Nucleus)o;
                    nucleus.addFissionListener( energyGraphDialog.getFissionListener() );
                }
            }
        }
    }

    /**
     * Handle all the gummy stuff caused by the fact that our constructor gets called before the PhetFrame is made.
     */
    public void activate() {
        super.activate();
        resetEnergyGraphDialog();
        energyGraphDialog.setVisible( energyGraphDialogVisible );

        // create the dialog with the developers' controls
        PhetFrame phetFrame = PhetUtilities.getPhetFrame();
        developmentControlDialog = new DevelopmentControlDialog( phetFrame, this );
        developmentControlDialog.setLocation( (int)phetFrame.getWidth() - developmentControlDialog.getWidth(),
                                              (int)( phetFrame.getLocation().getX() + phetFrame.getHeight() / 2 ) );
    }

    public void deactivate() {
        energyGraphDialogVisible = energyGraphDialog.isVisible();
        energyGraphDialog.setVisible( false );
        super.deactivate();
    }

    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    public void setNumNeutronsFired( int numNeutronsFired ) {
        this.numNeutronsFired = numNeutronsFired;
    }

    public int getNumNeutronsFired() {
        return numNeutronsFired;
    }

    public int getNumControlRods() {
        return numChannels;
    }

    public void setNumControlRods( int n ) {
        numChannels = n;
//        stop();
//        start();
    }

    public void setU235AbsorptionProbability( double probability ) {
        Uranium235.setAbsoptionProbability( probability );
    }

    public void setU238AbsorptionProbability( double probability ) {
        Uranium238.setAbsoptionProbability( probability );
    }

    public void setInterNucleusSpacing( double modelValue ) {
        double diam = new Uranium235( new Point2D.Double(), null ).getRadius() * 2;
        double spacing = diam * modelValue;
        nCols = (int)( vessel.getWidth() / spacing );
//        stop();
//        start();
    }

    public double getInterNucleusSpacing() {
        double spacing = vesselWidth / nCols;
        double diam = new Uranium235( new Point2D.Double(), null ).getRadius() * 2;
        return diam / spacing;
    }

    //----------------------------------------------------------------
    // Extensions of superclass behavior
    //----------------------------------------------------------------

    /**
     * Extends superclass behavior to recompute the launch parameters each time a neutron is fired. Launch
     * points must be in different inter-rod chambers
     */
    public void fireNeutron() {
        ArrayList chambersUsed = new ArrayList();
        for( int i = 0; i < numNeutronsFired; i++ ) {
            Integer chamberIdx = null;
            do {
                computeNeutronLaunchParams();
                chamberIdx = new Integer( vessel.getChamberIdx( getNeutronLaunchPoint().getX() ) );
            } while( vessel.getNumControlRodChannels() > numNeutronsFired &&
                     chambersUsed.contains( chamberIdx ) );
            chambersUsed.add( chamberIdx );
            super.fireNeutron();
        }
    }

    /**
     * Overrides the superclass behavior to create a graphic that is bigger than it otherwise
     * would be
     *
     * @param particle
     */
    protected void addNeutron( final NuclearParticle particle ) {
        this.getModel().addModelElement( particle );
        final NeutronGraphic ng = new BigNeutronGraphic( particle );
        getPhysicalPanel().addGraphic( ng );

        particle.addListener( new NuclearModelElement.Listener() {
            public void leavingSystem( NuclearModelElement nme ) {
                getPhysicalPanel().removeGraphic( ng );
                particle.removeListener( this );
            }
        } );
    }


    /**
     * Overrides the superclass behavior to create a graphic that is bigger than it otherwise
     * would be
     *
     * @param particle
     */
    protected void addNeutron( final NuclearParticle particle, Nucleus nucleus ) {
        this.getModel().addModelElement( particle );
        final NeutronGraphic ng = new BigNeutronGraphic( particle );
        getPhysicalPanel().addGraphic( ng );

        particle.addListener( new NuclearModelElement.Listener() {
            public void leavingSystem( NuclearModelElement nme ) {
                getPhysicalPanel().removeGraphic( ng );
                particle.removeListener( this );
            }
        } );
    }

    /**
     * Need to figure out how to make the image bigger without scaling the translation, too
     * @param neutron
     * @return
     */
    protected NeutronGraphic createNeutronGraphic( NuclearParticle neutron ) {
        return new BigNeutronGraphic( neutron );
    }

    /**
     * Prevents the fission products from flying apart by setting their positions and setting their
     * velocities to 0 before calling the parent class behavior
     *
     * @param products
     */
    public void fission( final FissionProducts products ) {
        double nominalDisplacement = 150;
        double displacement = nominalDisplacement * SCALE;
        double theta = random.nextDouble() * Math.PI * 2;
        double dx = displacement * Math.sin( theta );
        double dy = displacement * Math.cos( theta );
        products.getDaughter1().setPosition( products.getParent().getPosition().getX() + dx,
                                             products.getParent().getPosition().getY() + dy );
        products.getDaughter2().setPosition( products.getParent().getPosition().getX() - dx,
                                             products.getParent().getPosition().getY() - dy );
        products.getDaughter1().setVelocity( 0, 0 );
        products.getDaughter2().setVelocity( 0, 0 );

        // Delay for a fixed time before we actually release the neutron
        NeutronLauncher neutronLauncher = new NeutronLauncher( getModel(), products );
        neutronLaunchers.add( neutronLauncher );
        getModel().addModelElement( neutronLauncher );
    }

    //----------------------------------------------------------------
    // Implementation of abstract methods
    //----------------------------------------------------------------

    /**
     * Computes where the neutron will be fired from, and in what direction. It picks a random point within
     * one of the areas between control rods and sets a path toward the center of that area.
     */
    protected void computeNeutronLaunchParams() {

        // Find a location that's not within a channel in the vessel
        do {
            double x = vessel.getBounds().getMinX() + random.nextDouble() * vessel.getBounds().getWidth();
            double y = vessel.getBounds().getMinY() + random.nextDouble() * vessel.getBounds().getHeight();
            setNeutronLaunchPoint( new Point2D.Double( x, y ) );
        } while( vessel.isInChannel( getNeutronLaunchPoint() ) );

        // Fire the neutron toward the center of the vessel
        Point2D vesselCenter = new Point2D.Double( vessel.getX() + vessel.getWidth() / 2,
                                                   vessel.getY() + vessel.getHeight() / 2 );
        neutronLaunchAngle = Math.atan2( vesselCenter.getY() - getNeutronLaunchPoint().getY(),
                                         vesselCenter.getX() - getNeutronLaunchPoint().getX() );
        neutronPath = new Line2D.Double( getNeutronLaunchPoint(), new Point2D.Double( 0, 0 ) );

        // Figure out which area the launch point is in, so we can fire toward the center of that area
        int numChannels = vessel.getNumControlRodChannels();
        int numAreas = numChannels + 1;
        double channelWidth = vessel.getChannels()[0].getWidth();
        double areaWidth = ( vessel.getWidth() - ( numChannels * channelWidth ) ) / numAreas;
        double dx = getNeutronLaunchPoint().getX() - vessel.getX();
        int areaIdx = (int)( dx / ( areaWidth + channelWidth ) );
        double x0 = vessel.getX() + areaIdx * ( areaWidth + channelWidth );
        double xMid = x0 + areaWidth / 2;
        double yMid = vessel.getY() + vessel.getHeight() / 2;
        neutronLaunchAngle = Math.atan2( yMid - getNeutronLaunchPoint().getY(), xMid - getNeutronLaunchPoint().getX() );
        neutronPath = new Line2D.Double( getNeutronLaunchPoint().getX(), getNeutronLaunchPoint().getY(), xMid, yMid );
    }

    /**
     * @return Point2D.Double location for new nucleus
     */
    protected Point2D.Double findLocationForNewNucleus() {
        // Determine the model bounds represented by the current size of the apparatus panel
        Rectangle2D r = getPhysicalPanel().getBounds();
        AffineTransform atx = new AffineTransform( getPhysicalPanel().getNucleonTx() );
        AffineTransform gtx = getPhysicalPanel().getGraphicTx();
        atx.preConcatenate( gtx );
        Rectangle2D modelBounds = new Rectangle2D.Double();
        try {
            modelBounds.setFrameFromDiagonal( atx.inverseTransform( new Point2D.Double( r.getMinX(), r.getMinY() ), null ),
                                              atx.inverseTransform( new Point2D.Double( r.getMinX() + r.getWidth(), r.getMinY() + r.getHeight() ), null ) );
        }
        catch( NoninvertibleTransformException e ) {
            e.printStackTrace();
        }

        boolean overlapping = false;
        Point2D.Double location = new Point2D.Double();
        int attempts = 0;
        do {
            overlapping = false;

            // Generate a random location and test to see if it's acceptable
            double x = random.nextDouble() * ( modelBounds.getWidth() - 50 ) + modelBounds.getMinX() + 25;
            double y = random.nextDouble() * ( modelBounds.getHeight() - 50 ) + modelBounds.getMinY() + 25;
            location.setLocation( x, y );

            // Check that the nucleus won't overlap a channel, and will be inside the vessel
            Rectangle2D[] channels = vessel.getChannels();
            for( int i = 0; !overlapping && i < channels.length; i++ ) {
                if( channels[i].contains( location ) || !vessel.contains( location ) ) {
                    overlapping = true;
                }
            }
            attempts++;
        } while( overlapping && attempts < s_maxPlacementAttempts );

        if( overlapping ) {
            location = null;
        }
        return location;
    }

    public void testFireNeutron() {
        stop();

    }

    private double orgDt;

    public void setSlowMotion( boolean b ) {
        throw new RuntimeException( "Needs to be re-implemented with new clock" );
//        if( b ) {
//            orgDt = getClock().getDt();
//            getClock().setDt( orgDt / 5 );
//        }
//        else {
//            getClock().setDt( orgDt );
//        }
    }

    public void setFissionDelay( long msec ) {
        fissionDelay = msec;
    }

    public void setRodAbsorptionProbability( double probability ) {
        rodAbsorptionProbability = probability;
        for( int i = 0; i < controlRods.length; i++ ) {
            controlRods[i].setAbsorptionProbability( probability );
        }
    }

    public void setDevelopmentControlDialog( boolean selected ) {
        developmentControlDialog.setVisible( true );
    }

    public void setPeriodicNeutronsPeriod( double period ) {
        if( periodicNeutronGun == null ) {
            periodicNeutronGun = new PeriodicNeutronGun( getClock(), period );
        }
        else {
            periodicNeutronGun.setPeriod( period );
        }
    }

    public void enablePeriodicNeutrons( boolean enabled ) {
        if( periodicNeutronGun != null ) {
            periodicNeutronGun.setEnabled( enabled );
        }
    }

    public void setEnergyGraphsVisible( boolean visible ) {
        energyGraphDialog.setVisible( visible );
    }

    public EnergyGraphDialog getEnergyGraphDialog() {
        return energyGraphDialog;
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * ?????
     */
    private class NeutronLauncher implements ModelElement {
        private long startTime = System.currentTimeMillis();
        private BaseModel model;
        private FissionProducts fissionProducts;

        public NeutronLauncher( BaseModel model, FissionProducts fissionProducts ) {
            this.model = model;
            this.fissionProducts = fissionProducts;
        }

        public void stepInTime( double v ) {
            if( System.currentTimeMillis() - startTime > fissionDelay ) {
                model.removeModelElement( this );
                ControlledFissionModule.super.fission( fissionProducts );
            }
        }
    }

    /**
     * Automatically fires neutrons into the vessel at regular intervals
     */
    private class PeriodicNeutronGun extends ClockAdapter {
        private double lastTimeFired;
        private double period;
        private boolean enabled;
        private Random random = new Random();

        public PeriodicNeutronGun( IClock clock, double period ) {
            this.period = period;
            clock.addClockListener( this );
            lastTimeFired = clock.getSimulationTime();
        }

        public void setPeriod( double period ) {
            this.period = period;
        }

        public void setEnabled( boolean enabled ) {
            this.enabled = enabled;
        }

        public void clockTicked( ClockEvent clockEvent ) {
            if( clockEvent.getSimulationTime() - lastTimeFired > period ) {
                lastTimeFired = clockEvent.getSimulationTime();
                if( enabled ) {
                    fireNeutron();
                }
            }
        }

        private void fireNeutrons() {
            int numChannels = vessel.getNumControlRodChannels();
            int numAreas = numChannels + 1;
            double channelWidth = vessel.getChannels()[0].getWidth();
            double areaWidth = ( vessel.getWidth() - ( numChannels * channelWidth ) ) / numAreas;
            for( int i = 0; i < numAreas; i++ ) {
                double x0 = vessel.getX() + i * ( areaWidth + channelWidth );
                double x = x0 + random.nextDouble() * areaWidth;
                double y = vessel.getY() + random.nextDouble() * vessel.getHeight();
                double xMid = vessel.getX() + areaWidth / 2;
                double yMid = vessel.getY() + vessel.getHeight() / 2;
                neutronLaunchAngle = Math.atan2( yMid - y, xMid - x );
                setNeutronLaunchPoint( new Point2D.Double( x, y ) );
                neutronPath = new Line2D.Double( x, y, xMid, yMid );
                ControlledFissionModule.super.fireNeutron();
            }
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------

    /**
     * Detects fission caused by collisions between neutrons and U235 or U238 nuclei
     */
    private class FissionDetector implements ModelElement {
        private Line2D utilLine = new Line2D.Double();
        private NuclearPhysicsModel model;
        private Map interChannelAreaToNucleiMap = new HashMap();

        public FissionDetector( NuclearPhysicsModel model ) {
            this.model = model;

            // Determine the bounds of the areas between the channels. These are the only places
            // nuclei are.
            // Make a map of lists, keyed by the channels in the vessel
            Rectangle2D[] channels = vessel.getChannels();
            double x = vessel.getBounds().getX();
            double y = vessel.getBounds().getY();
            double height = vessel.getBounds().getHeight();
            double width = vessel.getBounds().getWidth() / ( channels.length + 1 );
            for( int i = 0; i < channels.length + 1; i++ ) {
                x = vessel.getBounds().getX() + ( i * width );
                Rectangle2D interChannelArea = new Rectangle2D.Double( x, y, width, height );
                interChannelAreaToNucleiMap.put( interChannelArea, new ArrayList() );
            }

            // Get the U235 neutrons and put them in bins according to their x coordinates

            List modelElements = model.getNuclearModelElements();
            for( int i = 0; i < modelElements.size(); i++ ) {
                Object o = modelElements.get( i );
                if( o instanceof Uranium235 || o instanceof Uranium238 ) {
                    Nucleus nucleus = (Nucleus)o;
                    Iterator interChannelAreaIterator = interChannelAreaToNucleiMap.keySet().iterator();
                    while( interChannelAreaIterator.hasNext() ) {
                        Rectangle2D interChannelArea = (Rectangle2D)interChannelAreaIterator.next();
                        if( interChannelArea.contains( nucleus.getPosition().getX(),
                                                       nucleus.getPosition().getY() ) ) {
                            List nuclei = (List)interChannelAreaToNucleiMap.get( interChannelArea );
                            nuclei.add( nucleus );
                        }
                    }
                }
            }
        }

        /**
         * Detects collisions between neutrons and U235 and U238 nuclei, and invokes the
         * fision behavior on the Uranium nucleus if a collision occurs.
         *
         * @param dt
         */
        public void stepInTime( double dt ) {

            // Check all neutrons
            List neutrons = new ArrayList( getNeutrons() );
            for( int i = 0; i < neutrons.size(); i++ ) {
                Neutron neutron = (Neutron)neutrons.get( i );
                utilLine.setLine( neutron.getPosition(), neutron.getPositionPrev() );

                // Find the interChannelArea that the neutron is currently in
                Iterator interChannelAreaIt = interChannelAreaToNucleiMap.keySet().iterator();
                while( interChannelAreaIt.hasNext() ) {
                    Rectangle2D area = (Rectangle2D)interChannelAreaIt.next();
                    if( area.contains( neutron.getPosition() ) ) {

                        // See if the neutron has hit any of the nuclei in the area
                        List nuclei = (List)interChannelAreaToNucleiMap.get( area );
                        for( int j = 0; j < nuclei.size(); j++ ) {
                            Nucleus nucleus = (Nucleus)nuclei.get( j );
                            double perpDist = utilLine.ptSegDistSq( nucleus.getPosition() );
                            if( perpDist <= nucleus.getRadius() * nucleus.getRadius() ) {
                                nucleus.fission( neutron );
                                // Take the old nucleus off the list. It's fissioned, so it's
                                // really not around anymore
                                nuclei.remove( nucleus );
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Removes the U238 and U239 graphics that the PhysicalPanel adds when U238 and U239 model instances are
     * created. These nuclei are just there to dampen the reaction, and we don't want the user to know they are there.
     */
    private class NucleusGraphicRemover implements PhysicalPanel.GraphicListener {
        public void graphicAdded( PhysicalPanel.GraphicEvent event ) {
            PhetGraphic graphic = event.getPhetGraphic();
            if( graphic instanceof Uranium238Graphic ) {
                getPhysicalPanel().removeGraphic( graphic );
            }
            if( graphic instanceof TxGraphic
                && ( ( (TxGraphic)graphic ).getWrappedGraphic() instanceof Uranium238Graphic
                     || ( (TxGraphic)graphic ).getWrappedGraphic() instanceof Uranium239Graphic ) ) {
                getPhysicalPanel().removeGraphic( graphic );
            }
        }

        public void graphicRemoved( PhysicalPanel.GraphicEvent event ) {
            // noop
        }
    }
}

