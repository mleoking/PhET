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

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.instrumentation.Thermometer;
import edu.colorado.phet.nuclearphysics.Config;
import edu.colorado.phet.nuclearphysics.model.*;
import edu.colorado.phet.nuclearphysics.view.*;

import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * ControlledFissionModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ControlledFissionModule extends ChainReactionModule {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    public static final int VERTICAL = 1, HORIZONTAL = 2;
//    public static double SCALE = 1;
    public static double SCALE = 0.2;

    private static final double VESSEL_LAYER = 100;
    private static final double CONTROL_ROD_LAYER = VESSEL_LAYER - 1;
    private static final double refWidth = 700;
    private static final double refHeight = 300;

    private static NucleusGraphic ng2;
    private static BufferedImage u235Img;
    private static BufferedImage rubidiumImg;
    private static BufferedImage cesiumImg;

    static {
        ng2 = NucleusGraphicFactory.create( new Uranium235( new Point2D.Double(),
                                                            null ) );
        u235Img = ng2.getImage();
        rubidiumImg = NucleusGraphicFactory.create( new Rubidium( new Point2D.Double() ) ).getImage();
        cesiumImg = NucleusGraphicFactory.create( new Cesium( new Point2D.Double() ) ).getImage();
    }

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    private double vesselWidth;
    private double vesselHeight;
//    private int numChannels = 2;
    private int numChannels = 5;
    private Vessel vessel;
//    private int nCols = 3;
    private int nCols = 20;
    private ControlRodGroupGraphic controlRodGroupGraphic;
    private ControlRod[] controlRods;
    private int numNeutronsFired = 1;
    private EnergyGraphDialog energyGraphDialog;
    private long fissionDelay;
    private double rodAbsorptionProbability;
    private DevelopmentControlDialog developmentControlDialog;
    private PeriodicNeutronGun periodicNeutronGun;

    // TODO: clean up when refactoring is done
    public void setContainmentEnabled( boolean b ) {
        // noop
    }


    /**
     * @param clock
     */
    public ControlledFissionModule( AbstractClock clock ) {
        super( "Controlled Reaction", clock );

        // Set up the control panel
        super.addControlPanelElement( new ControlledChainReactionControlPanel( this ) );
        init( getClock() );
    }

    private void init( AbstractClock clock ) {
        // set the SCALE of the physical panel so we can fit more nuclei in it
        getPhysicalPanel().setScale( SCALE );
        vesselWidth = refWidth / SCALE;
        vesselHeight = refHeight / SCALE;

        // Add the vessel. In case we are called in a reset, ditch the old vessel before
        // we build a new one
        if( vessel != null ) {
            getModel().removeModelElement( vessel );
            vessel.removeAllChangeListeners();
        }
        vessel = new Vessel( -vesselWidth / 2,
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

        // Add control rods. In case we are in a reset, get rid of the old ones first
        for( int i = 0; controlRods != null && i < controlRods.length; i++ ) {
            ControlRod controlRod = controlRods[i];
            getModel().removeModelElement( controlRod );
        }
        controlRods = createControlRods( VERTICAL, vessel );
        controlRodGroupGraphic = new ControlRodGroupGraphic( getPhysicalPanel(),
                                                             controlRods,
                                                             vessel,
                                                             getPhysicalPanel().getNucleonTx() );
        getPhysicalPanel().addGraphic( controlRodGroupGraphic, CONTROL_ROD_LAYER );

        // Add a thermometer and a listener that will control the thermometer
        double thermometerColumnHeight = 400;
        final Thermometer thermometer = new Thermometer( getPhysicalPanel(),
                                                         new Point2D.Double( vessel.getX() + 200,
                                                                             vessel.getY() - thermometerColumnHeight ),
                                                         thermometerColumnHeight, 80, true, 0, Config.MAX_TEMPERATURE );
        thermometer.setNumericReadoutEnabled( false );
        getPhysicalPanel().addOriginCenteredGraphic( thermometer, 1000 );
        vessel.addChangeListener( new Vessel.ChangeListener() {
            public void temperatureChanged( Vessel.ChangeEvent event ) {
                thermometer.setValue( event.getVessel().getTemperature() );
            }
        } );

        // Create the nuclei
        createNuclei();

        // Reset the energy graph dialog
        resetEnergyGraphDialog();
    }

    /**
     * Creates a control rod for each channel in the specified vessel
     *
     * @param orientation
     * @param vessel
     * @return
     */
    private ControlRod[] createControlRods( int orientation, Vessel vessel ) {
        ControlRod[] rods = new ControlRod[vessel.getNumControlRodChannels()];
        NuclearPhysicsModel model = (NuclearPhysicsModel)getModel();
        if( orientation == VERTICAL ) {
            Rectangle2D[] channels = vessel.getChannels();
            for( int i = 0; i < channels.length; i++ ) {
                Rectangle2D channel = channels[i];
                // The computation of the x coordinate may look funny. Here's why it's the way it is: The rod is
                // specified by its centerline, rather than its corner. The second offset of half the channel width
                // is there to make all the rods evenly spaced within the vessel.
                rods[i] = new ControlRod( new Point2D.Double( channel.getMinX() + ( channel.getWidth() ) / 2,
                                                              channel.getMinY() ),
                                          new Point2D.Double( channel.getMinX() + channel.getWidth() / 2,
                                                              channel.getMaxY() ), channel.getWidth(),
                                          model,
                                          rodAbsorptionProbability );
                model.addModelElement( rods[i] );
            }
        }
        return rods;
    }

    /**
     * Overrides superclass behavior to use a different graphic
     *
     * @param nucleus
     */
    protected void addNucleus( Nucleus nucleus ) {
        nuclei.add( nucleus );
        nucleus.addFissionListener( this );
        getModel().addModelElement( nucleus );

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
//        double rad = nucleus.getRadius();
//        final Graphic ng = new PhetShapeGraphic( getPhysicalPanel(),
//                                                 new Ellipse2D.Double( nucleus.getPosition().getX() - rad,
//                                                                       nucleus.getPosition().getY() - rad,
//                                                                       rad * 2, rad * 2 ),
//                                                 Color.red );
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
            Nucleus nucleus = new Uranium235( location, getModel() );
            u235Nuclei.add( nucleus );
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
            u238Nuclei.add( nucleus );
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
            energyGraphDialog.reset( u235Nuclei.size() );
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
     *
     * @param application
     */
    public void activate( PhetApplication application ) {
        super.activate( application );
        PhetFrame phetFrame = PhetApplication.instance().getApplicationView().getPhetFrame();

        // Add the dialog that will show the energy tracking gauges
        if( energyGraphDialog == null ) {
            energyGraphDialog = new EnergyGraphDialog( phetFrame,
                                                       u235Nuclei.size() );
            energyGraphDialog.setVisible( true );
        }
        resetEnergyGraphDialog();

        // create the dialog with the developers' controls
        developmentControlDialog = new DevelopmentControlDialog( phetFrame, this );
        developmentControlDialog.setLocation( (int)phetFrame.getWidth() - developmentControlDialog.getWidth(),
                                              (int)( phetFrame.getLocation().getX() + phetFrame.getHeight() / 2 ) );
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
        stop();
        start();
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
        stop();
        start();
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
     * Extends superclass behavior to recompute the launch parameters each time a neutron is fired
     */
    public void fireNeutron() {
        for( int i = 0; i < numNeutronsFired; i++ ) {
            computeNeutronLaunchParams();
            super.fireNeutron();
        }
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
        getModel().addModelElement( new NeutronLauncher( getModel(), products ) );

//        super.fission( products );
    }

    //----------------------------------------------------------------
    // Implementation of abstract methods
    //----------------------------------------------------------------

    public void start() {
        init( getClock() );
        return;
    }

    public void stop() {
        super.stop();
        getModel().removeModelElement( vessel );
        for( int i = 0; i < controlRods.length; i++ ) {
            ControlRod controlRod = controlRods[i];
            getModel().removeModelElement( controlRod );
        }
        getPhysicalPanel().removeAllGraphics();
    }

    /**
     * Computes where the neutron will be fired from, and in what direction
     */
    protected void computeNeutronLaunchParams() {

        // Find a location that's not within a channel in the vessel
        do {
            double x = vessel.getBounds().getMinX() + random.nextDouble() * vessel.getBounds().getWidth();
            double y = vessel.getBounds().getMinY() + random.nextDouble() * vessel.getBounds().getHeight();
            neutronLaunchPoint = new Point2D.Double( x, y );
        } while( vessel.isInChannel( neutronLaunchPoint ) );

        // Fire the neutron toward the center of the vessel
        Point2D vesselCenter = new Point2D.Double( vessel.getX() + vessel.getWidth() / 2,
                                                   vessel.getY() + vessel.getHeight() / 2 );
        neutronLaunchAngle = Math.atan2( vesselCenter.getY() - neutronLaunchPoint.getY(),
                                         vesselCenter.getX() - neutronLaunchPoint.getX() );
        neutronPath = new Line2D.Double( neutronLaunchPoint, new Point2D.Double( 0, 0 ) );
    }

    /**
     * @return
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
        if( b ) {
            orgDt = getClock().getDt();
            getClock().setDt( orgDt / 5 );
        }
        else {
            getClock().setDt( orgDt );
        }
    }

    public void setFissionDelay( long msec ) {
        fissionDelay = msec;
    }

    public void setRodAbsorptionProbability( double probability ) {
        rodAbsorptionProbability = probability;
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


    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

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

    private class PeriodicNeutronGun implements ClockTickListener {
        private double lastTimeFired;
        private double period;
        private boolean enabled;

        public PeriodicNeutronGun( AbstractClock clock, double period ) {
            this.period = period;
            clock.addClockTickListener( this );
            lastTimeFired = clock.getRunningTime();
        }

        public void setPeriod( double period ) {
            this.period = period;
        }

        public void setEnabled( boolean enabled ) {
            this.enabled = enabled;
        }

        public void clockTicked( AbstractClock clock, double v ) {
            if( clock.getRunningTime() - lastTimeFired > period ) {
                lastTimeFired = clock.getRunningTime();
                if( enabled ) {
                    fireNeutron();
                }
            }
        }
    }
}

