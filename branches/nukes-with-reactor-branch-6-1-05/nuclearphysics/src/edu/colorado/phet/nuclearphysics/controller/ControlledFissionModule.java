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

    private Vessel vessel;
    private static final double VESSEL_LAYER = 100;
    private static final double CONTROL_ROD_LAYER = VESSEL_LAYER - 1;

    // TODO: clean up when refactoring is done
    public void setContainmentEnabled( boolean b ) {
        // noop
    }


    /**
     * @param clock
     */
    public ControlledFissionModule( AbstractClock clock ) {
        super( "Controlled Reaction", clock );

        // Add the chamber
        vessel = new Vessel( -500, -600, 1000, 800 );
//        vessel = new Vessel( 100, 100, 600, 400 );
        VesselGraphic vesselGraphic = new VesselGraphic( getPhysicalPanel(), vessel );
        getPhysicalPanel().addOriginCenteredGraphic( vesselGraphic, VESSEL_LAYER );

        // Add control rods
        ControlRod[] controlRods = createControlRods( VERTICAL, vessel );
        ControlRodGroupGraphic controlRodGroupGraphic = new ControlRodGroupGraphic( getPhysicalPanel(),
                                                                                    controlRods,
                                                                                    vessel,
                                                                                    getPhysicalPanel().getNucleonTx());
        getPhysicalPanel().addGraphic( controlRodGroupGraphic, CONTROL_ROD_LAYER );

        // Add a thermometer

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
        if( orientation == VERTICAL ) {
            Rectangle2D[] channels = vessel.getChannels();
            for( int i = 0; i < channels.length; i++ ) {
                Rectangle2D channel = channels[i];
                rods[i] = new ControlRod( new Point2D.Double( channel.getMinX() + channel.getWidth() / 2,
                                                              channel.getMinY() ),
                                          new Point2D.Double( channel.getMinX() + channel.getWidth() / 2,
                                                              channel.getMaxY() ), 15 );
            }
        }
        return rods;
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
        neutronLaunchGamma = random.nextDouble() * Math.PI * 2;
        double x = bounds * Math.cos( neutronLaunchGamma );
        double y = bounds * Math.sin( neutronLaunchGamma );
        neutronLaunchPoint = new Point2D.Double( x, y );
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
            // If there is already a nucleus at (0,0), then generate a random location
            boolean centralNucleusExists = false;
            if( !centralNucleusExists ) {
//            for( int i = 0; i < getNuclei().size() && !centralNucleusExists; i++ ) {
                Nucleus testNucleus = new Uranium235( new Point2D.Double( ), getModel() );
//                Nucleus testNucleus = (Nucleus)getNuclei().get( i );
                if( testNucleus.getPosition().getX() == 0 && testNucleus.getPosition().getY() == 0 ) {
                    centralNucleusExists = true;
                }
            }
            overlapping = !centralNucleusExists;

            // Generate a random location and test to see if it's acceptable
            double x = centralNucleusExists ? random.nextDouble() * ( modelBounds.getWidth() - 50 ) + modelBounds.getMinX() + 25 : 0;
            double y = centralNucleusExists ? random.nextDouble() * ( modelBounds.getHeight() - 50 ) + modelBounds.getMinY() + 25 : 0;
            location.setLocation( x, y );
            for( int j = 0; j < getNuclei().size() && !overlapping; j++ ) {
                Rectangle2D[] channels = vessel.getChannels();
                for( int i = 0; !overlapping && i < channels.length; i++ ) {
                    Rectangle2D channel = channels[i];
                    System.out.println( "vessel.contains( location ) = " + vessel.contains( location ) );
                    if( channel.contains( location ) || !vessel.contains( location )) {
                        overlapping = true;
                    }
                }
            }

            // Test that the new location won't put the nucleus on the path of the neutron that will
            // be fired to start the reaction
            // todo: the hard-coded 50 here should be replaced with the radius of a Uranium nucleus
            if( !overlapping ) {
//            if( location.getX() != 0 && location.getY() != 0 ) {
                overlapping = overlapping
                              || getNeutronPath().ptSegDist( location ) < 50;
//                              || location.distance( 0, 0 ) + 50 > bounds.getBounds2D().getWidth() / 2;
            }
            attempts++;
        } while( overlapping && attempts < s_maxPlacementAttempts );

        if( overlapping ) {
            location = null;
        }
        return location;
    }
}

