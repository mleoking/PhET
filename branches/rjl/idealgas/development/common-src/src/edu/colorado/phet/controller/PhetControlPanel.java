/**
 * Class: PhetControlPanel
 * Package: edu.colorado.phet.controller
 *
 * User: Ron LeMaster
 * Date: Jan 16, 2003
 * Time: 1:56:22 PM
 */
package edu.colorado.phet.controller;

import javax.swing.*;
import java.util.Observable;
import java.util.Observer;

/**
 * This class contains application-specific user interface elements for
 * controlling the physical system.
 * <p>
 * Instances of this class are intended to be Observers of the application's
 * PhysicalSystem. This linkage is created by the framework when the
 * PhetApplication starts.
 */
public abstract class PhetControlPanel extends JPanel implements Observer {


    public PhetControlPanel() {
        this( null );
    }

    /**
     * Deprecated
     * @param application The PhetApplication with which this PhetControlPanel
     * is associated
     */
    protected PhetControlPanel( PhetApplication application ) {
    }

    /**
     * Empty implementation of the Observer update() method, making this
     * class an Observer adapter. It is intended to be overridden by concrete
     * subclasses for application-specific behavior.
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        // NOP
    }

    //
    // Abstract methods
    //

    /**
     * Performs actions on the PhetControlPanel associated with *clear*
     * being called on the PhetApplication.
     */
    public abstract void clear();
}
