// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Class where all the user components are defined for items in the sim, which
 * is necessary for the "sim sharing" (a.k.a. data collection) feature.
 *
 * @author John Blanco
 */
public class EnergyFormsAndChangesSimSharing {

    // Sim sharing components that exist only in the view.
    public static enum UserComponents implements IUserComponent {

        // Tabs
        introTab, energySystemsTab,

        // Movable model elements
        brick, leadBlock, beaker
    }

    public static enum ModelComponents implements IUserComponent, IModelComponent {
    }

    public static enum UserActions implements IUserAction {
    }

    public static enum ModelComponentTypes implements IModelComponentType {
    }

    public static enum ModelActions implements IModelAction {
    }

    public static enum ParameterKeys implements IParameterKey {
    }
}
