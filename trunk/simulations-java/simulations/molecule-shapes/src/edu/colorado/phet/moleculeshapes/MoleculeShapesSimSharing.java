// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelObject;
import edu.colorado.phet.common.phetcommon.simsharing.messages.SimSharingConstants;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;

/**
 * Sim-sharing strings for this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author Sam Reid
 */
public class MoleculeShapesSimSharing {

    public static enum Components implements UserComponent {
        backgroundColor, bond, draggingState, mouseMiddleButton
    }

    public static enum Actions implements UserAction {
        created, removed
    }

    public static enum ParamKeys implements SimSharingConstants.ParameterKey {
        bondOrder, color, dragging, dragMode, electronGeometry, moleculeGeometry, removedPair
    }

    public static enum ParamValues {
        black, white
    }

    public static enum ModelObjects implements ModelObject {
        molecule
    }

    public static enum ModelActions implements ModelAction {
        bondsChanged
    }

    public static void sendModelEvent( ModelObjects modelObject, ModelActions modelAction, Parameter... params ) {
        SimSharingManager.sendModelEvent( modelObject, modelAction, params );
    }
}