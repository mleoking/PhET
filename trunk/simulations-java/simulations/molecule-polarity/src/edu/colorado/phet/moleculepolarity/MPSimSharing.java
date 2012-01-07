// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity;

import edu.colorado.phet.common.phetcommon.simsharing.messages.SimSharingConstants;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;

/**
 * Sim-sharing strings that are specific to this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MPSimSharing {

    public static enum Components implements UserComponent {
        bondAngle, bondCharacterNode, electronegativityControl, moleculeAngle, rainboxMenuItem, jmolViewerNode
    }

    public static enum Parameters implements SimSharingConstants.ParameterKey {
        angle, atom, currentMolecule, electronegativity, interactive
    }
}