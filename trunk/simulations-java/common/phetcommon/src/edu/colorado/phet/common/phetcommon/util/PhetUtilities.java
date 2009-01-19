/* Copyright 2003-2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.util;

import java.util.ArrayList;
import java.io.File;
import java.security.AccessControlException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;

/**
 * PhetUtilities
 * <p/>
 * Static methods of general utility
 *
 * @author Ron LeMaster
 */
public class PhetUtilities {
    
    // Operating Systems
    public static final int OS_WINDOWS = 0;
    public static final int OS_MACINTOSH = 1;
    public static final int OS_OTHER = 2;

    private static ArrayList pendingRunnables = new ArrayList();
    
    private PhetUtilities() {}

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
        if ( activeModule != null ) {
            IClock clock = activeModule.getClock();
            for ( int i = 0; i < pendingRunnables.size(); i++ ) {
                Runnable target = (Runnable) pendingRunnables.get( i );
                if ( clock instanceof SwingClock ) {
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
        if ( osName.indexOf( "windows" ) >= 0 ) {
            os = OS_WINDOWS;
        }
        else if ( osName.indexOf( "mac" ) >= 0 ) {
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


    /**
     * Determines whether we're running on a Windows
     *
     * @return true or false
     */
    public static boolean isWindows() {
        return getOperatingSystem() == OS_WINDOWS;
    }

    /**
     * Are we running on Mac OS 10.4.x ?
     * 
     * @return true or false
     */
    public static boolean isMacOS_10_4() {
        boolean rval = false;
        if ( isMacintosh() ) {
            String osVersion = System.getProperty( "os.version" );
            if ( osVersion != null ) {
                rval = osVersion.startsWith( "10.4" );
            }
        }
        return rval;
    }

    /**
     * Was this sim run as part of a PhET installation, created using the PhET installer?
     * If it was, then a file named .phet-installer will live in the JAR's parent directory.
     * 
     * @return true or false
     */
    public static boolean isRunningFromPhetInstallation() {
        boolean isPhetInstallation = false;
        if ( hasPermissionsToGetCodeSource() ) {
            File codeSource = FileUtils.getCodeSource();
            File parent = codeSource.getParentFile();
            if ( parent != null ) {
                File grandparent = parent.getParentFile();
                if ( grandparent != null ) {
                    File specialFile = new File( grandparent.getAbsolutePath() + System.getProperty( "file.separator" ) + "phet-installation.properties" );
                    isPhetInstallation = specialFile.exists();
                }
            }
        }
        return isPhetInstallation;
    }

    private static boolean hasPermissionsToGetCodeSource() {
        //TODO: bad style to write code that depends on exceptions, see AccessControlContext for a better solution 
        try {
            FileUtils.getCodeSource();
            return true;
        }
        catch( AccessControlException ace ) {
            return false;
        }
    }

    /**
     * Is this sim running from a stand-alone JAR file on the user's local machine?
     * @return
     */
    public static boolean isRunningFromStandaloneJar() {
        return !PhetServiceManager.isJavaWebStart() && !PhetUtilities.isRunningFromPhetInstallation();
    }
    
    /**
     * Is this sim running from a web site using Java Web Start?
     * @return
     */
    public static boolean isRunningFromWebsite() {
        return PhetServiceManager.isJavaWebStart() && !PhetUtilities.isRunningFromPhetInstallation(); // PhET installer uses Web Start!
    }
    
    public static String getJavaPath() {
        return System.getProperty( "java.home" ) + System.getProperty( "file.separator" ) + "bin" + System.getProperty( "file.separator" ) + "java";
    }
}
