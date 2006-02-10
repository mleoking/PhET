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

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.ModelClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.PhetFrame;

import javax.swing.*;
import java.util.ArrayList;

/**
 * PhetModelUtilities
 * <p/>
 * Static methods of general utility
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhetUtilities {
    private static ArrayList pendingRunnables = new ArrayList();

    /**
     * Requests that a Runnable be executed by the model.
     * <p/>
     * This provides thread-safe execution of a method by the model, whether its clock is running
     * in the Swing dispatch queue thread or a model-specific thread.
     *
     * @param runnable
     */
    public static void invokeLater( Runnable runnable ) {
        pendingRunnables.add( runnable );
        Module activeModule = getActiveModule();
        if( activeModule != null ){
            IClock clock = activeModule.getClock();
            for( int i = 0; i < pendingRunnables.size(); i++ ) {
                Runnable target = (Runnable)pendingRunnables.get( i );
                if( clock instanceof ModelClock ) {
                    ModelClock modelClock = (ModelClock)clock;
                    modelClock.invokeLater( target );
                }
                else if( clock instanceof SwingClock ) {
                    SwingUtilities.invokeLater( target );
                }
                else {
                    target.run();
                }
            }
            pendingRunnables.clear();
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
