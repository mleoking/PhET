// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.view;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.SystemActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.SystemComponentTypes;
import edu.colorado.phet.common.phetcommon.simsharing.messages.SystemComponents;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager.sendSystemMessage;

/**
 * PhetExit encapsulates the various ways of exiting a sim.  It also sends out notifications before System.exit(0) is
 * called in case any final work needs to be done, as in the case of sim sharing.
 *
 * @author Sam Reid
 */
public class PhetExit {
    private static ArrayList<VoidFunction0> exitListeners = new ArrayList<VoidFunction0>();//Listeners that are notified just before the PhetApplication exits
    private static ArrayList<VoidFunction0> finalExitMessageSentListeners = new ArrayList<VoidFunction0>();//Listeners that are notified just before the PhetApplication exits

    /**
     * Add a listener that will be notified just before the PhetApplication exits.
     *
     * @param listener the listener to notify just before the PhetApplication exits
     */
    public static void addExitListener( VoidFunction0 listener ) {
        exitListeners.add( listener );
    }

    /**
     * Notifies any listeners that the PhetApplication is about to exit,
     * then exits the application by closing the VM with System.exit(0)
     */
    public static void exit() {
        for ( VoidFunction0 exitListener : exitListeners ) {
            exitListener.apply();
        }
        // Send a message for sim exit, work for both frame closing and File - > Exit( but not application kill )
        sendSystemMessage( SystemComponents.application, SystemComponentTypes.application, SystemActions.exited, Parameter.param( ParameterKeys.messageCount, SimSharingManager.getInstance().getMessageCount() ) );

        //Notify after exit message sent so logs can be closed, etc.
        for ( VoidFunction0 listener : finalExitMessageSentListeners ) {
            listener.apply();
        }
        System.exit( 0 );
    }

    public static void addExitMessageSentListener( VoidFunction0 listener ) {
        finalExitMessageSentListeners.add( listener );
    }
}