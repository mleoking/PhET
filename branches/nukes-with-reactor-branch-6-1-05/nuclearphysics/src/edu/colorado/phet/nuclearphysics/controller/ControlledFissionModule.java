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

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.nuclearphysics.model.FissionProducts;
import edu.colorado.phet.nuclearphysics.model.Vessel;
import edu.colorado.phet.nuclearphysics.model.ControlRod;
import edu.colorado.phet.nuclearphysics.view.VesselGraphic;
import edu.colorado.phet.nuclearphysics.view.ControlRodGraphic;
import edu.colorado.phet.nuclearphysics.view.ControlRodGroupGraphic;

import java.awt.geom.Point2D;

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

    public ControlledFissionModule( AbstractClock clock ) {
        super( "Controlled Reaction", clock );

        // Add the chamber
        vessel = new Vessel( 100, 100, 600, 400 );
        VesselGraphic vesselGraphic = new VesselGraphic( getPhysicalPanel(), vessel );
        getPhysicalPanel().addGraphic( vesselGraphic, VESSEL_LAYER );

        // Add control rods
        ControlRod[] controlRods = createControlRods( 5, VERTICAL, vessel );
        ControlRodGroupGraphic controlRodGroupGraphic = new ControlRodGroupGraphic( getPhysicalPanel(), controlRods );
        getPhysicalPanel().addGraphic( controlRodGroupGraphic, CONTROL_ROD_LAYER );

        // Add a thermometer
    }

    private ControlRod[] createControlRods( int numRods, int orientation, Vessel vessel ) {
        double initialInsertDist = 100;
        double length = 200;
        double spacing = 0;
        ControlRod[] rods = new ControlRod[numRods];
        if( orientation == VERTICAL ) {
            spacing = vessel.getWidth() / ( numRods + 1 );
            for( int i = 0; i < numRods; i++ ) {
                double x = vessel.getX() + spacing *  ( i + 1 );
                double y = vessel.getY() + vessel.getHeight() - initialInsertDist;
                rods[i] = new ControlRod( new Point2D.Double( x, y ),
                                          new Point2D.Double( x, y + length ), 15 );
            }
        }
        return rods;
    }

    public void start() {

    }

    protected void computeNeutronLaunchParams() {

    }

    public void fission( FissionProducts products ) {

    }

    protected Point2D.Double findLocationForNewNucleus() {
        return null;
    }
}

