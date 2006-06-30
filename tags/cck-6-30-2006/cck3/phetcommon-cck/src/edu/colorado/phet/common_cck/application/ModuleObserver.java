/**
 * Class: ModuleObserver
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: Jun 9, 2003
 */
package edu.colorado.phet.common_cck.application;

/**
 * Observes additions and removals of Modules, change in the active Module.
 */
public interface ModuleObserver {
    public void moduleAdded( Module m );

    public void activeModuleChanged( Module m );

    public void moduleRemoved( Module m );
}
