// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Sim-sharing enums that are specific to this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MPSimSharing {

    public static enum UserComponents implements IUserComponent {
        twoAtomsTab, threeAtomsTab, realMoleculesTab,
        bondAngle, bondCharacterNode, electronegativityControl, moleculeAngle, rainbowMenuItem, jmolViewerNode
    }

    public static enum Parameters implements IParameterKey {
        angle, atom, currentMolecule, electronegativity, interactive
    }
}