/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.PhysicsUtil;
import edu.colorado.phet.lasers.model.photon.Photon;

/**
 * LaserAtomElementProperties
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ThreeLevelElementProperties extends LaserElementProperties {

    // The time that an atom spends in any one state before dropping to a lower one (except for
    // the ground state)
    public static final double DEFAULT_STATE_LIFETIME = ( LaserConfig.DT / LaserConfig.FPS ) * 100;

    private static double groundStateEnergy = -13.6;
    private static double[] energyLevels = {
        groundStateEnergy,
        groundStateEnergy + PhysicsUtil.wavelengthToEnergy( Photon.RED ),
        groundStateEnergy + PhysicsUtil.wavelengthToEnergy( Photon.BLUE )
    };

    public ThreeLevelElementProperties() {
        super( "Laser Atom", energyLevels,
               new EmissionStrategy(),
               null,
               DEFAULT_STATE_LIFETIME );
    }

    private static class EmissionStrategy implements EnergyEmissionStrategy {
        public AtomicState emitEnergy( Atom atom ) {
            return atom.getCurrState().getNextLowerEnergyState();
        }
    }

    public AtomicState getHighEnergyState() {
        return getStates()[2];
    }
}

