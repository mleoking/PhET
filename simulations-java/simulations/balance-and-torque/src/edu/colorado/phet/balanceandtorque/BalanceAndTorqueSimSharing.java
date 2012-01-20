// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Class where all the user components are defined for items in the sim, which
 * is necessary for the "sim sharing" (a.k.a. data collection) feature.
 *
 * @author John Blanco
 */
public class BalanceAndTorqueSimSharing {
    public static enum UserComponents implements IUserComponent {
        massLabelsCheckBox, rulersCheckBox, forceFromObjectsCheckBox,
        levelCheckBox, introTab, balanceLabTab, gameTab
    }
}
