// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelObject;

/**
 * Sim-sharing enums that are specific to this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author Sam Reid
 */
public class MoleculeShapesSimSharing {

    public static enum UserComponents implements IUserComponent {
        backgroundColor, bond, draggingState, mouseMiddleButton, moleculeShapesTab, realMoleculesTab
    }

    public static enum Actions implements IUserAction {
        created, removed
    }

    public static enum ParamKeys implements IParameterKey {
        bondOrder, color, dragging, dragMode, electronGeometry, moleculeGeometry, removedPair
    }

    public static enum ParamValues {
        black, white
    }

    public static enum ModelObjects implements ModelObject {
        molecule
    }

    public static enum ModelActions implements IModelAction {
        bondsChanged
    }
}