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
import edu.colorado.phet.nuclearphysics.model.*;
import edu.colorado.phet.nuclearphysics.view.ControlRodGroupGraphic;
import edu.colorado.phet.nuclearphysics.view.VesselGraphic;

import java.awt.*;
import java.awt.geom.*;

/**
 * ControlledFissionModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ControlledFissionModule extends ChainReactionModule {

    public static final int VERTICAL = 1, HORIZONTAL = 2;

    private static final double VESSEL_LAYER = 100;
    private static final double CONTROL_ROD_LAYER = VESSEL_LAYER - 1;
    private static final double refWidth = 700;
    private static final double refHeight = 300;

    private double vesselWidth;
    private double vesselHeight;
    private int numChannels = 5;
    private Vessel vessel;
    private int nCols = 20;
    private double scale = 0.2;

    // TODO: clean up when refactoring is done
    public void setContainmentEnabled( boolean b ) {
        // noop
    }


    /**
     * @param clock
     */
    public ControlledFissionModule( AbstractClock clock ) {
        super( "Controlled Reaction", clock );

        // set the scale of the physical panel so we can fit more nuclei in it
        getPhysicalPanel().setScale( scale );
        vesselWidth = refWidth / scale;
        vesselHeight = refHeight / scale;

        // Add the chamber
        vessel = new Vessel( -vesselWidth / 2, -vesselHeight, vesselWidth, vesselHeight, numChannels );
        VesselGraphic vesselGraphic = new VesselGraphic( getPhysicalPanel(), vessel );
        getPhysicalPanel().addOriginCenteredGraphic( vesselGraphic, VESSEL_LAYER );

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
     *
     */
    protected void createNuclei() {
        Point2D[] locations = vessel.getInitialNucleusLocations( nCols );
        for( int i = 0; i < locations.length; i++ ) {
            Point2D location = locations[i];
            Nucleus nucleus = new Uranium235( location, getModel() );
            u235Nuclei.add( nucleus );
            addNucleus( nucleus );
        }

//        double xSpacing = vessel.getWidth() / ( nCols + 1 );
//
//        double ySpacing = xSpacing;
//        int nRows = (int)( ( vessel.getHeight() / ySpacing ) - 1 );
//
//        for( int i = 0; i < nCols; i++ ) {
//            double x = vessel.getX() + ( xSpacing * ( i + 1 ) );
//            for( int j = 0; j < nRows; j++ ) {
//                double y = vessel.getY() + ( ySpacing * ( j + 1 ) );
//                Point2D.Double position = new Point2D.Double( x, y );
//                if( !vessel.isInChannel( position ) ) {
//                    Nucleus nucleus = new Uranium235( position, getModel() );
//                    u235Nuclei.add( nucleus );
//                    addNucleus( nucleus );
//                }
//            }
//        }
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

    //----------------------------------------------------------------
    // Implementation of abstract methods
    //----------------------------------------------------------------

    public void start() {
        computeNeutronLaunchParams();
    }

    protected void computeNeutronLaunchParams() {
        // Compute how we'll fire the neutron
        double bounds = 600 / getPhysicalPanel().getScale();
        neutronLaunchGamma = random.nextDouble() * Math.PI + Math.PI;
        do {
            double x = vessel.getBounds().getMinX() + random.nextDouble() * vessel.getBounds().getWidth();
            double y = vessel.getBounds().getMinY() + random.nextDouble() * vessel.getBounds().getHeight();
            neutronLaunchPoint = new Point2D.Double( x, y );
        } while( vessel.isInChannel( neutronLaunchPoint ) );
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

// Check that the nucleus will not overlap an existing nucleus
            for( int j = 0; j < getNuclei().size() && !overlapping; j++ ) {
                //
            }

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

