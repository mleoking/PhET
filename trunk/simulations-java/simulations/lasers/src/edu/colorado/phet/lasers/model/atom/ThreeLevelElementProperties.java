// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.common.phetcommon.util.PhysicsUtil;
import edu.colorado.phet.common.quantum.model.Atom;
import edu.colorado.phet.common.quantum.model.AtomicState;
import edu.colorado.phet.common.quantum.model.EnergyEmissionStrategy;
import edu.colorado.phet.common.quantum.model.Photon;
import edu.colorado.phet.lasers.LasersConfig;

/**
 * ThreeLevelElementProperties
 * <p/>
 * ElementProperties for a 3 level laser atom
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ThreeLevelElementProperties extends LaserElementProperties {

    // The time that an atom spends in any one state before dropping to a lower one (except for
    // the ground state)
    public static final double DEFAULT_STATE_LIFETIME = ( LasersConfig.DT / LasersConfig.FPS ) * 100;

    private static double groundStateEnergy = -13.6;
    private static double[] energyLevels = {
            groundStateEnergy,
            groundStateEnergy + PhysicsUtil.wavelengthToEnergy( Photon.RED ),
            groundStateEnergy + PhysicsUtil.wavelengthToEnergy( Photon.BLUE )
    };

    public ThreeLevelElementProperties() {
        super( "Laser Atom", energyLevels,
               new EmissionStrategy(),
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

