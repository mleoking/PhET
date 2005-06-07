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

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.nuclearphysics.model.*;
import edu.colorado.phet.nuclearphysics.view.*;
import edu.colorado.phet.nuclearphysics.Config;
import edu.colorado.phet.coreadditions.TxGraphic;

import java.awt.geom.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;

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
    private static BufferedImage bi;
    private static NucleusGraphic u235Graphic = new Uranium235Graphic( new Uranium235( new Point2D.Double(),
                                                                                       null ) );
    private static BufferedImage u235Img;

    static {
        ng2 = NucleusGraphicFactory.create( new Uranium235( new Point2D.Double(),
                                                            null ) );
        bi = ng2.getImage();
        AffineTransformOp atxOp = new AffineTransformOp( AffineTransform.getScaleInstance( 1, 1 ),
//        AffineTransformOp atxOp = new AffineTransformOp( AffineTransform.getScaleInstance( 1, 1 ),
                                                         new RenderingHints( RenderingHints.KEY_INTERPOLATION,
                                                                             RenderingHints.VALUE_INTERPOLATION_BICUBIC ) );
        u235Img = atxOp.filter( bi, null );
    }

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    private double vesselWidth;
    private double vesselHeight;
    private int numChannels = 5;
    private Vessel vessel;
    private int nCols = 20;
    private ControlRodGroupGraphic controlRodGroupGraphic;
    private ControlRod[] controlRods;

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

        // Add the vessel
        vessel = new Vessel( -vesselWidth / 2,
                             -vesselHeight,
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

        // Add control rods
        controlRods = createControlRods( VERTICAL, vessel );
        controlRodGroupGraphic = new ControlRodGroupGraphic( getPhysicalPanel(),
                                                             controlRods,
                                                             vessel,
                                                             getPhysicalPanel().getNucleonTx() );
        getPhysicalPanel().addGraphic( controlRodGroupGraphic, CONTROL_ROD_LAYER );

        // Add a thermometer

        // Create the nuclei
        createNuclei();
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
                                          model );
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
//        final PhetImageGraphic nig = new PhetImageGraphic( getPhysicalPanel(),
//                                                           u235Img,
//                                                           (int)nucleus.getPosition().getX(),
//                                                           (int)nucleus.getPosition().getY() );
        nuclei.add( nucleus );
        nucleus.addFissionListener( this );
        getModel().addModelElement( nucleus );
        double rad = nucleus.getRadius();
        final Graphic ng = new PhetShapeGraphic( getPhysicalPanel(),
                                                        new Ellipse2D.Double(nucleus.getPosition().getX() - rad,
                                                                             nucleus.getPosition().getY() - rad,
                                                                             rad * 2, rad * 2),
                                                        Color.red);
        NuclearModelElement.Listener listener = new NuclearModelElement.Listener() {
            public void leavingSystem( NuclearModelElement nme ) {
//                getPhysicalPanel().removeGraphic( nig );
                getPhysicalPanel().removeGraphic( ng );
            }
        };
        nucleus.addListener( listener );
//        getPhysicalPanel().addOriginCenteredGraphic( nig, Config.nucleusLevel );
        getPhysicalPanel().addOriginCenteredGraphic( ng, Config.nucleusLevel );
    }


    /**
     * Puts nuclei in the vessel on a grid laid out by the vessel.
     */
    protected void createNuclei() {
        Point2D[] locations = vessel.getInitialNucleusLocations( nCols );
        for( int i = 0; i < locations.length; i++ ) {
            Point2D location = locations[i];
            Nucleus nucleus = new Uranium235( location, getModel() );
            u235Nuclei.add( nucleus );
            addNucleus( nucleus );

            // Add listeners to the nucleus for when it fissions
            nucleus.addFissionListener( this );
            nucleus.addFissionListener( vessel );
        }
        getPhysicalPanel().repaint( getPhysicalPanel().getBounds() );
    }

    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    public int getNumControlRods() {
        return numChannels;
    }

    public void setNumControlRods( int n ) {
        numChannels = n;
        stop();
        start();
    }

    public void setAbsorptionProbability( double probability ) {
        Uranium235.setAbsoptionProbability( probability );
    }

    //----------------------------------------------------------------
    // Extensions of superclass behavior
    //----------------------------------------------------------------

    /**
     * Extends superclass behavior to recompute the launch parameters each time a neutron is fired
     */
    public void fireNeutron() {
        computeNeutronLaunchParams();
        super.fireNeutron();
    }

    /**
     * Prevents the fission products from flying apart by setting their positions and setting their
     * velocities to 0 before calling the parent class behavior
     *
     * @param products
     */
    public void fission( FissionProducts products ) {
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

        super.fission( products );
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
        neutronLaunchGamma = random.nextDouble() * Math.PI + Math.PI;
        do {
            double x = vessel.getBounds().getMinX() + random.nextDouble() * vessel.getBounds().getWidth();
            double y = vessel.getBounds().getMinY() + random.nextDouble() * vessel.getBounds().getHeight();
            neutronLaunchPoint = new Point2D.Double( x, y );
        } while( vessel.isInChannel( neutronLaunchPoint ) );

        // Fire the neutron toward the center of the vessel
        Point2D vesselCenter = new Point2D.Double( vessel.getX() + vessel.getWidth() / 2,
                                                   vessel.getY() + vessel.getHeight() / 2 );
        neutronLaunchGamma = Math.atan2( vesselCenter.getY() - neutronLaunchPoint.getY(),
                                         vesselCenter.getX() - neutronLaunchPoint.getX() );
        neutronPath = new Line2D.Double( neutronLaunchPoint, new Point2D.Double( 0, 0 ) );
    }

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

    public void setInterNucleusSpacing( double modelValue ) {
        double diam = new Uranium235( new Point2D.Double( ), null ).getRadius() * 2;
        double spacing = diam * modelValue;
        nCols = (int)(vessel.getWidth() / spacing );
        stop();
        start();
    }

    public double getInterNucleusSpacing() {
        double spacing = vesselWidth / nCols;
        double diam = new Uranium235( new Point2D.Double( ), null ).getRadius() * 2;
        return diam / spacing;
    }
}

