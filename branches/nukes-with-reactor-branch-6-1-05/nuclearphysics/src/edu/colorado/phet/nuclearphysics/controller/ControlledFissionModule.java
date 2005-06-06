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
import edu.colorado.phet.nuclearphysics.model.*;
import edu.colorado.phet.nuclearphysics.view.*;
import edu.colorado.phet.nuclearphysics.Config;
import edu.colorado.phet.coreadditions.TxGraphic;

import java.awt.geom.*;
import java.awt.*;

/**
 * ControlledFissionModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ControlledFissionModule extends ChainReactionModule {

    public static final int VERTICAL = 1, HORIZONTAL = 2;
//    public static double SCALE = 1;
    public static double SCALE = 0.2;

    private static final double VESSEL_LAYER = 100;
    private static final double CONTROL_ROD_LAYER = VESSEL_LAYER - 1;
    private static final double refWidth = 700;
    private static final double refHeight = 300;

    private double vesselWidth;
    private double vesselHeight;
    private int numChannels = 5;
    private Vessel vessel;
    private int nCols = 20;

    // TODO: clean up when refactoring is done
    public void setContainmentEnabled( boolean b ) {
        // noop
    }


    /**
     * @param clock
     */
    public ControlledFissionModule( AbstractClock clock ) {
        super( "Controlled Reaction", clock );


        // set the SCALE of the physical panel so we can fit more nuclei in it
        getPhysicalPanel().setScale( SCALE );
        vesselWidth = refWidth / SCALE;
        vesselHeight = refHeight / SCALE;

        // Set up the control panel
        super.addControlPanelElement( new ControlledChainReactionControlPanel( this ) );

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
        ControlRod[] controlRods = createControlRods( VERTICAL, vessel );
        ControlRodGroupGraphic controlRodGroupGraphic = new ControlRodGroupGraphic( getPhysicalPanel(),
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

    protected void addNucleus( Nucleus nucleus ) {
        nuclei.add( nucleus );
        nucleus.addFissionListener( this );
        getModel().addModelElement( nucleus );
        double rad = nucleus.getRadius();
        final Graphic ng = new PhetShapeGraphic( getPhysicalPanel(),
                                                        new Ellipse2D.Double(nucleus.getPosition().getX() - rad,
                                                                             nucleus.getPosition().getY() - rad,
                                                                             rad * 2, rad * 2),
                                                        Color.red);
//        final Graphic ng2 = new Uranium235Graphic( nucleus );
        NuclearModelElement.Listener listener = new NuclearModelElement.Listener() {
            public void leavingSystem( NuclearModelElement nme ) {
//                getPhysicalPanel().removeGraphic( ng2 );
                getPhysicalPanel().removeGraphic( ng );
            }
        };
        nucleus.addListener( listener );
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
        computeNeutronLaunchParams();
        createNuclei();
        return;
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

//        getPhysicalPanel().addOriginCenteredGraphic( new PhetShapeGraphic( getApparatusPanel(),
//                                                                           new Rectangle2D.Double( neutronLaunchPoint.getX(),
//                                                                                                   neutronLaunchPoint.getY(),
//                                                                                                   50, 50 ),
//                                                                           Color.red ),
//                                                     1000 );
//        getPhysicalPanel().addOriginCenteredGraphic( new PhetShapeGraphic( getApparatusPanel(),
//                                                                           new Rectangle2D.Double( neutronLaunchPoint.getX() + Math.cos( neutronLaunchGamma ) * 300,
//                                                                                              neutronLaunchPoint.getY() + Math.sin( neutronLaunchGamma ) * 300,
//                                                                           20,20 ),
//                                                                           Color.green ),
//                                                     1000 );
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
}

