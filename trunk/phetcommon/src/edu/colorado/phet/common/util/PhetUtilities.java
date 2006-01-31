/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.util;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.ModelClock;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.PhetFrame;

import javax.swing.*;

/**
 * PhetModelUtilities
 * <p>
 * Static methods of general utility
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhetUtilities {

    /**
     * Requests that a Runnable be executed by the model.
     * <p>
     * This provides thread-safe execution of a method by the model, whether its clock is running
     * in the Swing dispatch queue thread or a model-specific thread.
     *
     * @param runnable
     */
    public static void invokeLater( Runnable runnable ) {
        IClock clock = PhetApplication.instance().getActiveModule().getClock();
        if( clock instanceof ModelClock ) {
            ModelClock modelClock = (ModelClock)clock;
            modelClock.invokeLater( runnable );
        }
        else if( clock instanceof SwingClock ) {
            SwingUtilities.invokeLater( runnable );
        }
        else {
            runnable.run();
        }
    }

    /**
     * Returns the active module
     *
     * @return The active module
     */
    public static Module getActiveModule() {
        return PhetApplication.instance().getActiveModule();
    }

    /**
     * Returns a reference to the application's PhetFrame
     *
     * @return The PhetFrame
     */
    public static PhetFrame getPhetFrame() {
        return PhetApplication.instance().getPhetFrame();
    }
}
