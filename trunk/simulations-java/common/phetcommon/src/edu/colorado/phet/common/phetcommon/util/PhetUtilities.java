/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.phetcommon.util;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.ModelClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;

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
    // Operating Systems
    public static final int OS_WINDOWS = 0;
    public static final int OS_MACINTOSH = 1;
    public static final int OS_OTHER = 2;

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
        if( activeModule != null ) {
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

    /**
     * Returns a reference to the clock associated with the currently active module
     *
     * @return The active clock
     */
    public static IClock getActiveClock() {
        return getActiveModule().getClock();
    }

    /**
     * Gets the operating system type.
     *
     * @return OS_WINDOWS, OS_MACINTOSH, or OS_OTHER
     */
    public static int getOperatingSystem() {

        // Get the operating system name.
        String osName = "";
        try {
            osName = System.getProperty( "os.name" ).toLowerCase();
        }
        catch( Throwable t ) {
            t.printStackTrace();
        }

        // Convert to one of the operating system constants.
        int os = OS_OTHER;
        if( osName.indexOf( "windows" ) >= 0 ) {
            os = OS_WINDOWS;
        }
        else if( osName.indexOf( "mac" ) >= 0 ) {
            os = OS_MACINTOSH;
        }

        return os;
    }
    
    /**
     * Determines whether we're running on a Macintosh.
     * 
     * @return true or false
     */
    public static boolean isMacintosh() {
        return getOperatingSystem() == OS_MACINTOSH;
    }
}
